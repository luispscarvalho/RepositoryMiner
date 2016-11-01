package org.repositoryminer.effort.model;

public enum EffortType {

	UNCATEGORIZED		(new String[] { "unknown", "uncategorized" }, 0),
	ENHANCEMENT			(new String[] { "enhancement", "need improvement" }, 1), 
	NEW_FUNCTIONALITY	(new String[] { "new functionality" }, 2), 
	BUG					(new String[] { "bug", "defect" }, 3);

	private int weight = 0;
	private String[] variabilities = null;

	private EffortType(String[] variabilities, int weight) {
		this.variabilities = variabilities;
		this.weight = weight;
	}
	
	public static EffortType parse(String value) {
		EffortType type = UNCATEGORIZED;

		if (value != null) {
			if (BUG.fitsInVariabilities(value)) {
				type = BUG;
			} else if (ENHANCEMENT.fitsInVariabilities(value)) {
				type = ENHANCEMENT;
			} else if (NEW_FUNCTIONALITY.fitsInVariabilities(value)) {
				type = NEW_FUNCTIONALITY;
			}
		}

		return type;
	}

	private boolean fitsInVariabilities(String value) {
		boolean fits = false;

		for (String var : variabilities) {
			fits = (var.equals(value.toLowerCase()));

			if (fits)
				break;
		}

		return fits;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public boolean lessThan(EffortType type) {
		return weight < type.getWeight();
	}
	
	public boolean greaterThan(EffortType type) {
		return !lessThan(type);
	}

}
