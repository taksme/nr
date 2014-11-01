package me.taks.nr.schedule;

public class Relation {
	private String mainId;
	private String assocId;
	public enum RelationType { DIVIDE, JOIN, NEXT }
	private RelationType type;
	public enum Period { STANDARD, NEXT_OVERNIGHT, PREV_OVERNIGHT }
	private Period period;
	private String tiploc;
	private String baseLocSuffix;
	private String assocLocSuffix;
	private Schedule schedule;
	
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
	
	public Schedule getSchedule() {
		return schedule;
	}

	public static class Builder {
		private Relation relation = new Relation();
		
		public Builder setMainId(String mainId) {
			relation.mainId = mainId;
			return this;
		}

		public Builder setAssocId(String assocId) {
			relation.assocId = assocId;
			return this;
		}

		public Builder setType(RelationType type) {
			relation.type = type;
			return this;
		}

		public Builder setPeriod(Period period) {
			relation.period = period;
			return this;
		}

		public Builder setTiploc(String tiploc) {
			relation.tiploc = tiploc;
			return this;
		}

		public Builder setBaseLocSuffix(String baseLocSuffix) {
			relation.baseLocSuffix = baseLocSuffix;
			return this;
		}

		public Builder setAssocLocSuffix(String assocLocSuffix) {
			relation.assocLocSuffix = assocLocSuffix;
			return this;
		}

		public Builder setSchedule(Schedule schedule) {
			relation.schedule = schedule;
			return this;
		}
		
		public Relation build() {
			return relation;
		}
	}	
}
