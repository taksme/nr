package me.taks.nr.schedule;

public class Schedule {
	public static final byte MON = 0b00000001;
	public static final byte TUE = 0b00000010;
	public static final byte WED = 0b00000100;
	public static final byte THU = 0b00001000;
	public static final byte FRI = 0b00010000;
	public static final byte SAT = 0b00100000;
	public static final byte SUN = 0b01000000;
	
	private int start;
	private int end;
	private byte days;
	public enum BankHoliday { NORMAL, NO, NOT_GLASGOW }
	private BankHoliday bankHoliday;
	public enum Type { PERMANENT, OVERLAY, STP, STP_CANCEL }
	private Type type;
	
	/** @return (Year << 16) + DayOfYear */
	public int getStart() {
		return start;
	}
	/** @return (Year << 16) + DayOfYear */
	public int getEnd() {
		return end;
	}
	public byte getDays() {
		return days;
	}
	public BankHoliday getBankHoliday() {
		return bankHoliday;
	}
	public Type getType() {
		return type;
	}

	public static class Builder {
		private Schedule schedule = new Schedule();

		public Builder setStart(int start) {
			schedule.start = start;
			return this;
		}
		public Builder setEnd(int end) {
			schedule.end = end;
			return this;
		}
		public Builder setDays(byte days) {
			schedule.days = days;
			return this;
		}
		public Builder setBankHoliday(BankHoliday bankHoliday) {
			schedule.bankHoliday = bankHoliday;
			return this;
		}
		public Builder setType(Type type) {
			schedule.type = type;
			return this;
		}
		
		public Schedule build() {
			return schedule;
		}
}
	
}

