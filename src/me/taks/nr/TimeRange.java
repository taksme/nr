package me.taks.nr;

public class TimeRange {
	private long expected;
	private long actual;
	
	public long getStart() {
		return expected;
	}
	public long getEnd() {
		return actual;
	}
	public TimeRange(long expected, long actual) {
		this.expected = expected;
		this.actual = actual;
	}
	public long getMinutesDiff() {
		return (actual-expected)/60000;
	}
	
	public String getMinutesAndQuarters() {
		return getMinutesAndQuarters("early", "on time", "late");
	}
	
	public String getMinutesAndQuarters(String early, String onTime, String late) { //TODO: this is display code
		if ((actual-expected)<15000 && (expected-actual)<15000) {
			return onTime;
		} else {
			long mins = Math.abs(getMinutesDiff());
			long q = (Math.abs(actual-expected)/15000)%4;
			return (mins>0 ? mins : "")
					+ (q==1 ? "¼" : q==2 ? "½" : q==3 ? "¾" : "")
					//+ "min" + ((mins!=1 || q>0) ? "s" : "")
					+ " " + (actual>expected ? late : early)
			;
		}
	}
}
