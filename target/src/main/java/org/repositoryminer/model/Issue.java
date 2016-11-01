package org.repositoryminer.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.repositoryminer.scm.hostingservice.StatusType;

public class Issue {

	private String creator;
	private String assignee;
	private Date closedAt;
	private Date createdAt;
	private int number;
	private StatusType status;
	private String title;
	private Date updatedAt;
	private String body;
	private int milestone;
	private String repository;
	private List<Label> labels;
	private List<Comment> comments;
	private List<Event> events;

	public static List<Issue> parseDocuments(List<Document> issuesDocs) {
		List<Issue> issues = new ArrayList<Issue>();
		for (Document doc : issuesDocs) {
			issues.add(parseDocument(doc));
		}

		return issues;
	}

	@SuppressWarnings("unchecked")
	public static Issue parseDocument(Document issueDoc) {
		Issue issue = new Issue(issueDoc.getString("creator"), issueDoc.getDate("closed_at"),
				issueDoc.getDate("created_at"), issueDoc.getInteger("number").intValue(),
				StatusType.valueOf(issueDoc.getString("status")), issueDoc.getString("title"),
				issueDoc.getDate("updated_at"), issueDoc.getString("body"));
		
		List<Document> labels = (List<Document>)issueDoc.get("labels");
		if ((labels != null) && (!labels.isEmpty())) {
			issue.setLabels(Label.parseDocuments(labels));
		}

		List<Document> comments = (List<Document>)issueDoc.get("comments");
		if ((comments != null) && (!comments.isEmpty())) {
			issue.setComments(Comment.parseDocuments(comments));
		}
		
		List<Document> events = (List<Document>)issueDoc.get("events");
		if ((events != null) && (!events.isEmpty())) {
			issue.setEvents(Event.parseDocuments(events));
		}
		
		return issue;
	}

	public Document toDocument() {
		Document doc = new Document();
		doc.append("creator", creator).append("assignee", assignee).append("closed_at", closedAt)
				.append("created_at", createdAt).append("number", number).append("status", status.toString())
				.append("title", title).append("updated_at", updatedAt).append("body", body)
				.append("milestone", milestone).append("repository", repository)
				.append("labels", Label.toDocumentList(labels)).append("comments", Comment.toDocumentList(comments))
				.append("events", Event.toDocumentList(events));

		return doc;
	}

	public Issue() {
	}

	public Issue(String creator, Date closedAt, Date createdAt, int number, StatusType status, String title,
			Date updatedAt, String body) {
		super();
		this.creator = creator;
		this.closedAt = closedAt;
		this.createdAt = createdAt;
		this.number = number;
		this.status = status;
		this.title = title;
		this.updatedAt = updatedAt;
		this.body = body;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public Date getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(Date closedAt) {
		this.closedAt = closedAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getMilestone() {
		return milestone;
	}

	public void setMilestone(int milestone) {
		this.milestone = milestone;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

}