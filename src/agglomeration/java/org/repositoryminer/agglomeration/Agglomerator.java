package org.repositoryminer.agglomeration;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public abstract class Agglomerator<IAgglomerativeType> {

	public abstract String getName();
	
	public abstract String getOptions();
	
	public abstract ArrayList<Attribute> getAttributes();
	
	public abstract List<List<Double>> getDataset();
	
	public Instances getInstances() {
		Instances instances = new Instances(getName(), getAttributes(), 0);
		List<List<Double>> dataset = getDataset();
		
		for (List<Double> values : dataset) {
			double vals[] = new double[instances.numAttributes()];
			int i = 0;
			for (Double value : values) {
				vals[i] = value;
				i++;	
			}

			instances.add(new DenseInstance(1.0, vals));
		}

		return instances;		
	}

	public abstract void agglomerate() throws Exception;

}
