package org.repositoryminer.effort.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

/**
 * <h1>Each instance of effort</h1>
 * <p>
 * Effort calculation is based on info related to:
 * <ul>
 * <li>Commits -> number of commits that have affected the file
 * <li>Modifications -> the code churn related to the file
 * </ul>
 * <p>
 */
public class Effort {

	private String repository;
	private String reference;
	private String referencePath;
	private String commit;

	private List<EffortItem> effortItems = new ArrayList<>();
	private Map<String, Integer> files = new HashMap<>();

	public static List<Effort> parseDocuments(List<Document> effortDocs) {
		List<Effort> efforts = new ArrayList<Effort>();
		for (Document effortDoc : effortDocs) {
			efforts.add(parseDocument(effortDoc));
		}

		return efforts;
	}

	@SuppressWarnings("unchecked")
	public static Effort parseDocument(Document effortDoc) {
		Effort effort = new Effort();
		effort.setRepository(effortDoc.getString("repository"));
		effort.setReference(effortDoc.getString("reference"));
		effort.setReferencePath(effortDoc.getString("reference_path"));
		effort.setCommit(effortDoc.getString("commit"));

		effort.setEffortItems(EffortItem.parseDocuments((List<Document>) effortDoc.get("efforts")));

		List<Document> docs = (List<Document>) effortDoc.get("files");
		if (docs != null) {
			for (Document doc : docs) {
				effort.getFiles().put(doc.getString("files"), doc.getInteger("modifications", 0));
			}
		}

		return effort;
	}

	public static List<Document> toDocumentList(List<Effort> efforts) {
		List<Document> list = new ArrayList<Document>();
		for (Effort effort : efforts) {
			list.add(effort.toDocument());
		}

		return list;
	}

	public Document toDocument() {
		Document doc = new Document();
		doc.append("repository", repository);
		doc.append("reference", reference);
		doc.append("reference_path", referencePath);
		doc.append("commit", commit);

		doc.append("efforts", EffortItem.toDocumentList(effortItems));
		
		List<Document> docs = new ArrayList<Document>();
		for (Entry<String, Integer> entry : files.entrySet()) {
			docs.add(new Document().append("file", entry.getKey()).append("modifications", entry.getValue()));
		}
		doc.append("files", docs);

		return doc;
	}

	public Effort() {
	}

	public Effort(String repository, String reference, String referencePath, String commit) {
		this.repository = repository;
		this.reference = reference;
		this.referencePath = referencePath;
		this.commit = commit;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getReferencePath() {
		return referencePath;
	}

	public void setReferencePath(String referencePath) {
		this.referencePath = referencePath;
	}

	public String getCommit() {
		return commit;
	}

	public void setCommit(String commit) {
		this.commit = commit;
	}

	public List<EffortItem> getEffortItems() {
		return effortItems;
	}

	public void setEffortItems(List<EffortItem> effortItems) {
		this.effortItems = effortItems;
	}

	public Map<String, Integer> getFiles() {
		return files;
	}

	public void setFiles(Map<String, Integer> files) {
		this.files = files;
	}

	@Override
	public String toString() {
		Document doc = this.toDocument();

		return doc.toJson();
	}

}
