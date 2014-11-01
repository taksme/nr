package me.taks.nr.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import me.taks.json.JSONArray;
import me.taks.json.JSONObject;
import me.taks.nr.HalfMins;
import me.taks.nr.data.PlanFlags;
import me.taks.nr.data.Schedule.Plan;
import me.taks.nr.data.Schedule.Plan.Category;
import me.taks.nr.data.Schedule.Plan.Catering;
import me.taks.nr.data.Schedule.Plan.PlanLoc;
import me.taks.nr.data.Schedule.Plan.PlanLocOrBuilder;
import me.taks.nr.data.Schedule.Plan.Power;
import me.taks.nr.data.Schedule.Plan.Reservation;
import me.taks.nr.data.Schedule.Plan.SleeperType;
import me.taks.nr.data.Schedule.Plan.TrainType;
import me.taks.nr.location.CorpusImporter;
import me.taks.nr.location.Locations;
import me.taks.nr.location.NaptanImporter;
import me.taks.nr.schedule.Relation.Period;
import me.taks.nr.schedule.Relation.RelationType;

public class Importer {
	private Locations locations;
	private Trains trains;
	
	/** handle the null case and provide a more terse syntax */
	private boolean e(String s1, String s2) {
		if (s1==null && s2==null) return true;
		if (s1==null) return false;
		return s1.equals(s2);
	}
	
	public Importer(Locations locations, Trains trains) {
		this.locations = locations;
		this.trains = trains;
	}
	
	public void process(String file) {
		process(new File(file));
	}
	
	public void process(File file) {
		try {
			BufferedReader reader = new BufferedReader(
										new InputStreamReader(
											new GZIPInputStream(new FileInputStream(file))));
			String line;
			while (null!=(line = reader.readLine())) {
				JSONObject o = new JSONObject(line);
				if (o.has("JsonAssociationV1")) {
//					assocFromJson(o.getJSONObject("JsonAssociationV1"));
				} else if (o.has("JsonScheduleV1")) {
					Plan plan = planFromJson(o.getJSONObject("JsonScheduleV1"));
					trains.add(plan);
					System.out.println(PlanViewer.get(plan).getSummaryPlusLocations());
				}
			}
			reader.close();
		} catch (IOException ie) {}
	}
	
	private static byte getDays(String days) {
		byte out = 0;
		for (int i=0; i<7; i++) out|= (days.charAt(i)=='1' ? 1 : 0) << i;
		return out;
	}
	
	private static int getYearsDays(long date) {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
		c.setTimeInMillis(date);
		return (c.get(Calendar.YEAR)<<16) + c.get(Calendar.DAY_OF_YEAR);
	}
	
	private Relation assocFromJson(JSONObject o) {
		String mainId = o.getString("main_train_uid");
		String assocId = o.getString("assoc_train_uid");
		String c = o.getString("category");
		RelationType type = e("JJ", c) ? RelationType.JOIN : 
							e("VV", c) ? RelationType.DIVIDE : RelationType.NEXT;
		char i = o.getString("date_indicator").charAt(0);
		Period period = i=='S' ? Period.STANDARD : i=='P' ? Period.PREV_OVERNIGHT : Period.NEXT_OVERNIGHT;
		String tiploc = o.getString("location");
		String baseLocSuffix = o.getString("base_location_suffix");
		String assocLocSuffic = o.getString("assoc_location_suffix");		
		Relation a = new Relation(mainId, assocId, type, period, tiploc, 
										baseLocSuffix, assocLocSuffic
						);
		a.setStart(getYearsDays(o.getQuotedLongOr0("assoc_start_date")))
		.setEnd(getYearsDays(o.getQuotedLongOr0("assoc_end_date")))
		.setDays(getDays(o.getString("assoc_days")));
		return a;
	}

	
	private TrainType getType(char t) {
		return 'B'==t || '5'==t ? TrainType.BUS : 
				'F'==t || '2'==t ? TrainType.FREIGHT : 
				'P'==t || '1'==t ? TrainType.PASSENGER :
				'S'==t || '4'==t ? TrainType.SHIP :
				TrainType.TRIP;
	}
	
	private SleeperType getSleeperType(char st) {
		return 'B'==st ? SleeperType.BOTH : 'F'==st ? SleeperType.FIRST_ONLY :
				'S'==st ? SleeperType.STANDARD_ONLY : SleeperType.NO_SLEEPER;
	}
	
	private Reservation getReservationType(char res) {
		return 'A'==res ? Reservation.COMPULSORY : 'E'==res ? Reservation.BIKES_COMPULSORY :
				'R'==res ? Reservation.RECOMMENDED : 'S'==res ? Reservation.AVAILABLE :
				Reservation.NO_RESERVATIONS;
	}
	
	private PlanFlags planflags = new PlanFlags();
	
	private Plan planFromJson(JSONObject o) {
		//System.out.println(o.toString()+"\n\n");
		JSONObject os=o.getJSONObject("schedule_segment");
		JSONObject on= !o.isNull("new_schedule_segment") ? o.getJSONObject("new_schedule_segment") 
														: new JSONObject();
		JSONArray locs = o.has("schedule_location") ? o.getJSONArray("schedule_location") :
							os.has("schedule_location") ? os.getJSONArray("schedule_location") :
							null;
		
		Plan.Builder plan = Plan.newBuilder()
		.setId(o.getString("CIF_train_uid"))
		.setIsDelete(!"Create".equals(o.getString("transaction_type")))
		.setTrainType(getType(o.getString("train_status").charAt(0)))
		.setCategory(fromCode(os.getString("CIF_train_category", "")))
		.setHeadcode(os.getString("signalling_id", ""))
		.setPortionId(os.getString("CIF_business_sector", ""))
		.setSleeperType(getSleeperType(os.getString("CIF_sleepers", " ").charAt(0)))
		.setReservation(getReservationType(os.getString("CIF_reservations", " ").charAt(0)))
		//enum Catering { TROLLEY, BUFFET, HOT_BUFFET, MEAL_FIRST, RESTAURANT_FIRST, RESTAURANT, WHEELCHAIR }
		.setCatering(Catering.NO_CATERING) //TODO
		//.setInternationalUicCode(on.getString("uic_code", ""))
		.setAtocCode(o.getString("atoc_code", ""));
		
		int len=locs.length();
		OldPlanLoc[] out = new OldPlanLoc[len];
		for (int i=0; i<len; i++) {
			plan.addPlanLocs(planLocFromJson(locs.getJSONObject(i)));
		}

		String p = os.getString("CIF_power_type", "");
		String tl = os.getString("CIF_timing_load", "");
		short tll = 0;
		try { tll = Short.parseShort(tl); } catch (NumberFormatException nfe) {}
		Power power = getPower(p, tl);
		plan.setPower(power)
		.setEmuClass(power==Power.EMU ? tll : 0)
		.setHauledTonnage(power==Power.DIESEL_HAULED || power==Power.ELECTRIC_HAULED
							|| power==Power.MIXED_HAULED 
							? tll
							: 0
						)
		.setSpeed((short)os.getQuotedLongOr0("CIF_speed"));
		
		String oc = os.getString("CIF_operating_characteristics", "");
		plan.setFlags(
			planflags.setAll((byte)0)
			.setGuard(oc.indexOf('G')>=0)
			.setPushPull(oc.indexOf('P')>=0)
			.setAsReqd(oc.indexOf('Q')>=0)
			.setAirCon(oc.indexOf('R')>=0)
			.setAsReqdToYard(oc.indexOf('Y')>=0)
			.setLargeGuage(oc.indexOf('Z')>=0)
			.setHasFirstClass(!"S".equals(os.getString("CIF_train_class", "")))
			.setPerformanceMonitored("Y".equals(o.getString("applicable_timetable", "")))
			.getAll()
		)
		.setStart(getYearsDays(o.getYMD("schedule_start_date")))
		.setEnd(getYearsDays(o.getYMD("schedule_end_date")))
		.setDays(getDays(o.getString("schedule_days_runs")));

		return plan.build();
	}

	private PlanLoc planLocFromJson(JSONObject o) {
		String ri = o.getString("record_identity");
		short departure = 0;
		OldPlanLoc.Type type;
		if (e("LO", ri)) {
			type = OldPlanLoc.Type.ORIGIN;
			departure = o.getHalfMins("departure");
		} else if (e("LT", ri)) {
			type = OldPlanLoc.Type.DESTINATION;
		} else {
			String passStr = o.getString("pass", "");
			if (passStr==null) {
				type = OldPlanLoc.Type.CALLING;
				departure = o.getHalfMins("departure");
			} else {
				type = OldPlanLoc.Type.PASSING;
				departure = HalfMins.parse(passStr);
			}
		}
		return new OldPlanLoc(type, locations.getByTiploc(o.getString("tiploc_code", "")), 
							o.getHalfMins("arrival"), departure, 
							o.getHalfMins("public_arrival"), o.getHalfMins("public_departure"), 
							o.getString("platform", ""), o.getString("line", ""), o.getString("path", ""),
							o.getHalfMins("engineering_allowance"), o.getHalfMins("pathing_allowance"), 
							o.getHalfMins("performance_allowance")
		);
	}
	
	private Power getPower(String p, String tl) {
		char tl0 = null!=tl ? tl.charAt(0) : ' ';
		return "D".equals(p) ? Power.DIESEL_HAULED : "DEM".equals(p) ? Power.DEMU :
				"E".equals(p) ? Power.ELECTRIC_HAULED : "ED".equals(p) ? Power.MIXED_HAULED :
				"EML".equals(p) ? Power.EMU_HAULED : "EPU".equals(p) ? Power.PARCELS_EMU :
				"HST".equals(p) ? Power.HST : "LDS".equals(p) ? Power.SHUNTER :
				"DMU".equals(p) ? (
						'A'==tl0 ? Power.PACER : 'N'==tl0 ? Power.TURBO : 
						'S'==tl0 ? Power.SPRINTER : 'T'==tl0 ? Power.FAST_TURBO :
						'V'==tl0 ? Power.VOYAGER : Power.DMU_OTHER
				) : "EMU".equals(p) ? (
						"AT".equals(tl) ? Power.ACCELERATED_EMU : Power.EMU
				) : Power.NO_POWER;
	}
	
	private Category fromCode(String code) {
		if (code==null || code.length()==0) return Category.NO_CATEGORY;
		if ("JJ".equals(code)) return Category.POST;
		char s = code.length()>1 ? code.charAt(1) : ' ';
		switch (code.charAt(0)) {
		case 'O':
			return 'L'==s ? Category.METRO : 'U'==s ? Category.UNADVERTISED :
					'O'==s ? Category.ORDINARY_PASSENGER : 'S'==s ? Category.STAFF : Category.MIXED;
		case 'X':
			return 'C'==s ? Category.EUROSTAR : 'D'==s ? Category.EUROSLEEPER :
					'I'==s ? Category.INTERNATIONAL : 'R'==s ? Category.MOTORAIL :
					'U'==s ? Category.UNADVERTISED_EXPRESS : 'X'==s ? Category.EXPRESS_PASSENGER :
						Category.SLEEPER;
		case 'B':
			return 'R'==s ? Category.BUS_REPLACEMENT : 'S'==s ? Category.BUS_SCHEDULED :
					Category.TRAINFREIGHT;
		case 'E':
			return 'E'==s ? Category.EMPTY_STOCK : 'L'==s ? Category.EMPTY_METRO : 
					'S'==s ? Category.EMPTY_STAFF : Category.TRAINFREIGHT;
		case 'P':
			return 'M'==s ? Category.POST_OFFICE_PARCELS : 'P'==s ? Category.PARCELS : 
					Category.EMPTY_PARCELS; 
		case 'D':
			return 'D'==s ? Category.DEPARMENTAL : 'Q'==s ? Category.STORES :
					'T'==s ? Category.TEST : Category.ENGINEER;
		case 'Z':
			return Category.LIGHT;
		case 'J':
			return Category.RAILFREIGHT;
		case 'H':
			switch (s) {
			case '2': case '8': case '9': return Category.RAILFREIGHT;
			default: return Category.EUROFREIGHT;
			}
		case 'A':
			return Category.TRAINFREIGHT;
		default:
			return Category.NO_CATEGORY;
		}
	}
	
	public static int count=0;
	
	public static void main(String[] args) throws IOException {
		Locations locations = new Locations();
		new CorpusImporter(locations).process("../nrdata/CORPUSExtract.json");
		new NaptanImporter(locations).process("../nrdata/RailReferences.csv");
//		Properties props = new Properties();
//		props.load(new FileReader("../nrdata/nr.props"));
		Importer i = new Importer(locations, null);
		long start = System.currentTimeMillis();
		i.process("../nrdata/CIF_ALL_FULL_DAILY-toc-full.json.gz");
		System.out.println("\nTOOK "+((System.currentTimeMillis()-start)/1000)+" secs to process "+count+" records");
	}
}
