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
	public Schedule setStart(int start) {
		this.start = start;
		return this;
	}
	public Schedule setEnd(int end) {
		this.end = end;
		return this;
	}
	public Schedule setDays(byte days) {
		this.days = days;
		return this;
	}
	public Schedule setBankHoliday(BankHoliday bankHoliday) {
		this.bankHoliday = bankHoliday;
		return this;
	}
	public Schedule setType(Type type) {
		this.type = type;
		return this;
	}
}

