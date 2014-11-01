package me.taks.nr.schedule;

/** Times are stored in half minutes from a base time (usually a day start. 
 * If there is a DST change during the represented period the appropriate flag should be set
 *  */

public class HalfMins {
	public static final int HOUR = 120;
	public static final int DAY = 2880;
	public static final int WEEK = 20160;
	public static final int TIME_MASK = 0xFFFF;
	public static final int DST_BACK = 1<<15;
	public static final int DST_FORWARD = 2<<15;
	public static final int DST_MASK = 3 << 15;
	public static final int INVALID_MASK = 1 << 17;
	
	public static int build(String hhmmH) {
		return 	hhmmH==null || hhmmH.length()<4 ? INVALID_MASK :
			(short) (Short.parseShort(hhmmH.substring(0, 2))*120 +
					Short.parseShort(hhmmH.substring(2, 4))*2 +
					(hhmmH.length()>4 && hhmmH.charAt(4)=='H' ? 1 : 0)
			);
	}
	
	public static int build(String hhmmH, int days) {
		int out = build(hhmmH) + days * DAY;
		return (out>Short.MAX_VALUE) ? INVALID_MASK : out;
	}
	
	public static int build(String hhmmH, int days, int dstOffset) {
		int out = build(hhmmH, days);
		return (out | (dstOffset & DST_MASK));
	}
	
	public static boolean valid(short halfMins) {
		return (halfMins & INVALID_MASK) == 0;
	}
	
	public static String toMinsHalves(int halfMins) {
		return (short)(halfMins/2) + (halfMins%2>0 ? "½" : "");
	}
	
	/** Get the represented time in hhmmH
	 * @param hm
	 * @return
	 */
	public static String toString(int hm) {
		return hm<0 ? ""
				: String.format("%02d%02d%s", (int)(hm/120)%24, (int)(hm/2)%60, hm%2>0 ? "½" : "");
	}
}
