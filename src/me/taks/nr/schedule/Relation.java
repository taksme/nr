package me.taks.nr.schedule;

public class Relation extends Schedule {
	private String mainId;
	private String assocId;
	public enum RelationType { DIVIDE, JOIN, NEXT }
	private RelationType type;
	public enum Period { STANDARD, NEXT_OVERNIGHT, PREV_OVERNIGHT }
	private Period period;
	private String tiploc;
	private String baseLocSuffix;
	private String assocLocSuffix;
	
	public Relation(String mainId, String assocId, RelationType type, Period period,
						String tiploc, String baseLocSuffix, String assocLocSuffix
					) {
		this.mainId = mainId;
		this.assocId = assocId;
		this.type = type;
		this.period = period;
		this.tiploc = tiploc;
		this.baseLocSuffix = baseLocSuffix;
		this.assocLocSuffix = assocLocSuffix;
	}

	public String getMainId() {
		return mainId;
	}

	public String getAssocId() {
		return assocId;
	}

	public RelationType getRelationType() {
		return type;
	}

	public Period getPeriod() {
		return period;
	}

	public String getTiploc() {
		return tiploc;
	}

	public String getBaseLocSuffix() {
		return baseLocSuffix;
	}

	public String getAssocLocSuffix() {
		return assocLocSuffix;
	}
}
