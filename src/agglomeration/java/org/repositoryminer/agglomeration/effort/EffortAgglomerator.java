package org.repositoryminer.agglomeration.effort;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.repositoryminer.agglomeration.KMeansAgglomerator;
import org.repositoryminer.effort.model.Effort;
import org.repositoryminer.effort.model.EffortItem;
import org.repositoryminer.effort.model.EffortType;
import org.repositoryminer.effort.persistence.handler.EffortsByReferenceDocumentHandler;
import org.repositoryminer.model.Reference;
import org.repositoryminer.model.Repository;

import weka.core.Attribute;

public class EffortAgglomerator extends KMeansAgglomerator<Effort> {

	private Repository repository;
	private Reference reference;

	public EffortAgglomerator(Repository repository, Reference reference) {
		this.repository = repository;
		this.reference = reference;
	}

	@Override
	public String getName() {
		return "Effort Agglomeration";
	}

	@Override
	public ArrayList<Attribute> getAttributes() {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();

		attributes.add(new Attribute("enhancement"));
		attributes.add(new Attribute("bug"));
		attributes.add(new Attribute("new"));

		return attributes;
	}

	@Override
	public List<List<Double>> getDataset() {
		List<List<Double>> dataset = new ArrayList<List<Double>>();

		EffortsByReferenceDocumentHandler handler = new EffortsByReferenceDocumentHandler();

		List<Document> docs = handler.findByRepositoryAndReferencePath(repository.getId(), reference.getPath());
		if (docs != null && !docs.isEmpty()) {
			List<Effort> efforts = Effort.parseDocuments(docs);

			for (Effort effort : efforts) {
				List<Double> values = new ArrayList<Double>();
				
				values.add(calculateEffort(effort, EffortType.ENHANCEMENT));
				values.add(calculateEffort(effort, EffortType.BUG));
				values.add(calculateEffort(effort, EffortType.NEW_FUNCTIONALITY));

				dataset.add(values);
			}
		}

		return dataset;
	}
	
	private Double calculateEffort(Effort effort, EffortType type) {
		double value = 0.0;
		
		List<EffortItem> items = effort.getEffortItems();
		for (EffortItem item : items) {
			if (type.equals(item.getType())) {
				value = item.getDuration();
			}
		}
		
		return value;
	}
}
