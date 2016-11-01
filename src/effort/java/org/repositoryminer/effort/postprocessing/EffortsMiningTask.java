package org.repositoryminer.effort.postprocessing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.repositoryminer.effort.model.Effort;
import org.repositoryminer.effort.model.EffortItem;
import org.repositoryminer.effort.model.EffortType;
import org.repositoryminer.effort.persistence.handler.EffortsByReferenceDocumentHandler;
import org.repositoryminer.listener.IPostMiningListener;
import org.repositoryminer.mining.RepositoryMiner;
import org.repositoryminer.mining.TimeFrameType;
import org.repositoryminer.model.Commit;
import org.repositoryminer.model.Diff;
import org.repositoryminer.model.Event;
import org.repositoryminer.model.Issue;
import org.repositoryminer.model.Label;
import org.repositoryminer.model.Reference;
import org.repositoryminer.model.Repository;
import org.repositoryminer.persistence.handler.CommitDocumentHandler;
import org.repositoryminer.persistence.handler.IssueDocumentHandler;
import org.repositoryminer.persistence.handler.ReferenceDocumentHandler;
import org.repositoryminer.postprocessing.IPostMiningTask;

/**
 * <h1>A task to mine effort</h1>
 * <p>
 * It provides ways to calculate effort based on metrics obtained by previous
 * file-abstract-type mining. That's to say, being a post mining task, effort
 * mining relies on the metrics calculated from the source-code.
 * <p>
 * It is important that this task is executed previous to any other
 * effort-related task, since it provides all the basic elements related to
 * effort mining/(re)mining. For instance, it should be called before the
 * execution of {@link EffortCategoriesMiningTask}.
 * <p>
 * After the execution of this task, it is expected that a set of basic elements
 * related to effort ({@link EffortsByReference}) are pushed to the database
 */
public class EffortsMiningTask implements IPostMiningTask {

	private static final String TASK_NAME = "Efforts Mining";

	public final static String EVENT_ASSIGNED = "assigned";
	public final static String EVENT_REFERENCED = "referenced";
	public final static String EVENT_MILESTONED = "milestoned";
	public final static String EVENT_LABELED = "labeled";
	public final static String EVENT_CLOSED = "closed";

	private List<Effort> efforts = new ArrayList<Effort>();

	public EffortsMiningTask() {
	}

	@Override
	public String getTaskName() {
		return TASK_NAME;
	}

	@Override
	public void execute(RepositoryMiner repositoryMiner, Repository repository, IPostMiningListener listener) {
		ReferenceDocumentHandler handler = new ReferenceDocumentHandler();
		// prior to calculating effort, we must retrieve selected references
		// from the miner
		Map<Reference, TimeFrameType[]> refs = repositoryMiner.getReferences();
		if (refs != null) {
			int idx = 0;
			// for each reference of the repository...
			for (Reference ref : refs.keySet()) {
				if (listener != null) {
					listener.postMiningTaskProgressChange("efforts", ++idx, refs.size());
				}
				// we must retrieve the reference from the database prior to
				// processing it
				Document refDoc = handler.findByPathAndName(ref.getPath(), ref.getName());
				Reference reference = Reference.parseDocument(refDoc);
				// let's process the reference...
				processReference(reference);
				// ...and save the calculated efforts
				save(repository.getId(), reference);

				efforts.clear();
			}
		}
	}

	/**
	 * Process a reference found in the repository
	 * 
	 * @param reference
	 */
	private void processReference(Reference reference) {
		CommitDocumentHandler handler = new CommitDocumentHandler();
		// as we process a reference, we must recover all commits contained in
		// it...
		List<String> commits = reference.getCommits();
		if (commits != null) {
			for (String id : commits) {
				// ...lets retrieve each commit document based on the id...
				Document doc = handler.findById(id);
				if (doc != null && !doc.isEmpty()) {
					Commit commit = Commit.parseDocument(doc);
					// ...and process it
					processCommit(commit);
				}
			}
		}
	}

	/**
	 * Process a commit found in the reference
	 * 
	 * @param commit
	 */
	private void processCommit(Commit commit) {
		Effort effort = null;
		// processing of commits requires:
		// calculating the number of affecting commits and codechurn
		List<Diff> diffs = commit.getDiffs();
		if ((diffs != null) && (!diffs.isEmpty())) {
			effort = processDiffs(commit, diffs);
		}
		// calculating duration of effort based on associated issue's events
		if (effort != null) {
			IssueDocumentHandler handler = new IssueDocumentHandler();
			List<Document> docs = handler.findAllClosedByCommit(commit.getId());
			if ((docs != null) && (!docs.isEmpty())) {
				List<Issue> issues = Issue.parseDocuments(docs);

				processIssues(effort, issues);
			}

			efforts.add(effort);
		}
	}

	/**
	 * Process diffs/files affected by commit
	 * <p>
	 * A map is used to accumulate efforts for a commit reference. To each file
	 * we calculate the number of modifications that has affected the file. The
	 * calculation is based on file's codechurn.
	 * 
	 * @param commit
	 *            the commit being examined
	 * @param diffs
	 *            commit's diffs
	 * @return a new instance of effort or null if diffs are not accepted
	 */
	private Effort processDiffs(Commit commit, List<Diff> diffs) {
		Effort effort = new Effort();
		effort.setCommit(commit.getId());

		for (Diff diff : diffs) {
			effort.getFiles().put(diff.getPath(), diff.getLinesAdded() + diff.getLinesRemoved());
		}

		return effort;
	}

	/**
	 * 
	 * 
	 * @param commit
	 * @param issues
	 */
	private void processIssues(Effort effort, List<Issue> issues) {
		List<EffortItem> effortItems = new ArrayList<>();
		for (Issue issue : issues) {
			EffortItem effortItem = new EffortItem(issue.getStatus(), extractEffortType(issue),
					calculateDuration(issue));

			effortItems.add(effortItem);
		}

		effort.setEffortItems(effortItems);
	}

	private EffortType extractEffortType(Issue issue) {
		EffortType type = EffortType.UNCATEGORIZED;
		
		List<Label> labels = issue.getLabels();
		if (labels != null) {
			for (Label label : issue.getLabels()) {
				EffortType t = EffortType.parse(label.getName());
				if (t.greaterThan(type)) {
					type = t;
				}
			}
		}
		
		return type;
	}

	/**
	 * 
	 * 
	 * @param issue
	 * @return
	 */
	private long calculateDuration(Issue issue) {
		Date iniDate = issue.getCreatedAt();
		Date endDate = issue.getClosedAt();

		List<Event> events = issue.getEvents();
		if ((events != null) && (!events.isEmpty())) {
			for (Event event : events) {
				if (EVENT_ASSIGNED.equals(event.getDescription())) {
					iniDate = event.getCreatedAt();
				} else if (EVENT_CLOSED.equals(event.getClass())) {
					endDate = event.getCreatedAt();
				}
			}
		}

		return endDate.getTime() - iniDate.getTime();
	}

	/**
	 * Saves a mined effort to the database
	 * 
	 * @param repositoryId
	 *            the repository id being examined
	 * @param reference
	 *            a reference of the repository
	 */
	private void save(String repositoryId, Reference reference) {
		EffortsByReferenceDocumentHandler ebfHandler = new EffortsByReferenceDocumentHandler();

		for (Effort effort : efforts) {
			effort.setRepository(repositoryId);
			effort.setReference(reference.getId());
			effort.setReferencePath(reference.getPath());

			ebfHandler.insert(effort.toDocument());
		}
	}

}
