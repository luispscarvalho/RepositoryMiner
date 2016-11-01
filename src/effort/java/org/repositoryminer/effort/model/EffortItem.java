package org.repositoryminer.effort.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.repositoryminer.scm.hostingservice.StatusType;

public class EffortItem {

	private StatusType status;
	private EffortType type;
	private long duration;

	public static List<EffortItem> parseDocuments(List<Document> effortItemDocs) {
		List<EffortItem> effortItems = new ArrayList<>();
		for (Document effortItemDoc : effortItemDocs) {
			effortItems.add(parseDocument(effortItemDoc));
		}
		
		return effortItems;
	}

	public static EffortItem parseDocument(Document effortItemDoc) {
		EffortItem effortItem = new EffortItem();
		effortItem.setDuration(effortItemDoc.getLong("duration"));
		effortItem.setStatus(StatusType.valueOf(effortItemDoc.getString("status")));
		effortItem.setType(EffortType.valueOf(effortItemDoc.getString("type")));

		return effortItem;
	}

	public static List<Document> toDocumentList(List<EffortItem> effortItems) {
		List<Document> list = new ArrayList<Document>();
		for (EffortItem effortItem : effortItems) {
			list.add(effortItem.toDocument());
		}

		return list;
	}

	public EffortItem() {
	}

	public EffortItem(StatusType status, EffortType type, long duration) {
		this.status = status;
		this.type = type;
		this.duration = duration;
	}

	public Document toDocument() {
		Document doc = new Document();
		doc.append("status", status.toString());
		doc.append("type", type.toString());
		doc.append("duration", duration);

		return doc;
	}

	public StatusType getStatus() {
		return status;
	}

	public void setStatus(StatusType status) {
		this.status = status;
	}

	public EffortType getType() {
		return type;
	}

	public void setType(EffortType type) {
		this.type = type;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

}
