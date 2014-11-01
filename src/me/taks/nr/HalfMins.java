package me.taks.nr;

/** Times are stored in half minutes. 
 * Times will be relative to the start of the day the journey starts on.
 * All times are in local timezone. If the train is running over a DST change the times
 * 
 *   */
public class HalfMins {
	public static final short INVALID = Short.MAX_VALUE;
	public static final short HOUR = 120;
	public static final short DAY = 2880;
	public static final short WEEK = 20160;
	public static final short TIMEONLY_MASK = 0x3FFF;
	public static final short TZ_BACK_FLAG = 0x8000;
	public static final short TZ_FWD_FLAG = 0x4000;
	
	public static short parse(String hhmmH) {
		try {
			return 	hhmmH==null || hhmmH.length()<4 ? -1 :
					(short) (Short.parseShort(hhmmH.substring(0, 2))*120 +
							Short.parseShort(hhmmH.substring(2, 4))*2 +
							(hhmmH.length()>4 && hhmmH.charAt(4)=='H' ? 1 : 0)
					);
		} catch (NumberFormatException nfe) { 
			return INVALID; 
		}
	}
	
	public static boolean valid(short halfMins) {
		return halfMins!=INVALID;
	}
	
	public static short parse(Long millis, Long startOfDay) {
		return (short)(millis/30000);
	}

	public static String toMinsHalves(short halfMins) {
		if (halfMins & )
		return (short)(halfMins/2) + (halfMins%2>0 ? "½" : "");
	}
	
	public static short diff(short a, short b) {
		if (a==INVALID || b==INVALID) return INVALID;
		else return (short)(Math.abs(a-b));
	}

	/** Get the represented time in hhmmH
	 * @param hm
	 * @return
	 */
	public static String toString(short hm) {
		return hm<0 ? "?"
				: String.format("%02d%02d%s", (int)(hm/120)%24, (int)(hm/2)%60, hm%2>0 ? "½" : "");
	}
}
