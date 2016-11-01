package org.repositoryminer.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class Label {

	private String name;
	private String color;

	public static List<Label> parseDocuments(List<Document> labelsDocs) {
		List<Label> labels = new ArrayList<Label>();
		for (Document doc : labelsDocs) {
			labels.add(parseDocument(doc));
		}

		return labels;
	}

	public static Label parseDocument(Document labelDoc) {
		Label label = new Label(labelDoc.getString("name"), labelDoc.getString("color"));

		return label;
	}

	public static List<Document> toDocumentList(List<Label> labels) {
		if (labels == null) {
			return null;
		}

		List<Document> docs = new ArrayList<Document>();
		for (Label l : labels) {
			docs.add(new Document("name", l.name).append("color", l.color));
		}

		return docs;
	}

	public Label() {
	}

	public Label(String name, String color) {
		super();
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}