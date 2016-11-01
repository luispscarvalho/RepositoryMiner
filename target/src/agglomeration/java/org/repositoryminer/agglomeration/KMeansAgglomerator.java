package org.repositoryminer.agglomeration;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public abstract class KMeansAgglomerator<IAgglomerativeType> extends Agglomerator<IAgglomerativeType> {

	private static final String OPTIONS = "-init 0 -max-candidates 100 -periodic-pruning 10000 "
			+ "-min-density 2.0 -t1 -1.25 -t2 -1.0 -N 10 -A \"weka.core.EuclideanDistance -R "
			+ "first-last\" -I 500 -num-slots 1 -S 10";

	@Override
	public String getOptions() {
		return OPTIONS;
	}

	@Override
	public void agglomerate() throws Exception {
		SimpleKMeans kmean = new SimpleKMeans();
		kmean.setOptions(weka.core.Utils.splitOptions(getOptions()));

		Instances instances = getInstances();
		System.out.println(instances.toString());
		
		kmean.buildClusterer(instances);
		System.out.println(kmean.toString());
	}

}
