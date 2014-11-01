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
import me.taks.nr.schedule.Plan;
import me.taks.nr.schedule.Plan.Category;
import me.taks.nr.schedule.Plan.Catering;
import me.taks.nr.schedule.PlanLoc;
import me.taks.nr.schedule.Plan.Power;
import me.taks.nr.schedule.Plan.Reservation;
import me.taks.nr.schedule.Plan.SleeperType;
import me.taks.nr.schedule.Plan.TrainType;
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
		String c = o.getString("category");
		char i = o.getString("date_indicator").charAt(0);
		return 
			new Relation.Builder()
			.setMainId(o.getString("main_train_uid"))
			.setAssocId(o.getString("assoc_train_uid"))
			.setType(
				e("JJ", c) ? RelationType.JOIN : 
				e("VV", c) ? RelationType.DIVIDE : RelationType.NEXT
			)
			.setPeriod(i=='S' ? Period.STANDARD : i=='P' ? Period.PREV_OVERNIGHT : Period.NEXT_OVERNIGHT)
			.setTiploc(o.getString("location"))
			.setBaseLocSuffix(o.getString("base_location_suffix"))
			.setAssocLocSuffix(o.getString("assoc_location_suffix"))
			.setSchedule(
				new Schedule.Builder()
				.setStart(getYearsDays(o.getQuotedLongOr0("assoc_start_date")))
				.setEnd(getYearsDays(o.getQuotedLongOr0("assoc_end_date")))
				.setDays(getDays(o.getString("assoc_days")))
				.build()
			)
			.build()
		;
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
				'S'==st ? SleeperType.STANDARD_ONLY : SleeperType.NO;
	}
	
	private Reservation getReservationType(char res) {
		return 'A'==res ? Reservation.COMPULSORY : 'E'==res ? Reservation.BIKES_COMPULSORY :
				'R'==res ? Reservation.RECOMMENDED : 'S'==res ? Reservation.AVAILABLE :
				Reservation.NO;
	}
	
	private Plan planFromJson(JSONObject o) {
		//System.out.println(o.toString()+"\n\n");
		JSONObject os = o.getJSONObject("schedule_segment");
		JSONObject on = o.getJSONObjectOrEmpty("new_schedule_segment");
		JSONArray locs = 
			o.has("schedule_location") ? o.getJSONArray("schedule_location") :
			os.has("schedule_location") ? os.getJSONArray("schedule_location") :
			null;

		int len=locs.length();
		PlanLoc[] planLocs = new PlanLoc[len];
		for (int i=0; i<len; i++) {
			planLocs[i] = planLocFromJson(locs.getJSONObject(i));
		}

							
		String oc = os.getString("CIF_operating_characteristics", "");
		
		Plan.Builder plan = new Plan.Builder()
			.setId(o.getString("CIF_train_uid"))
			.setDelete(!"Create".equals(o.getString("transaction_type")))
			.setTrainType(getType(o.getString("train_status").charAt(0)))
			.setCategory(fromCode(os.getString("CIF_train_category", "")))
			.setHeadcode(os.getString("signalling_id", ""))
			.setPortionId(os.getString("CIF_business_sector", ""))
			.setSleeperType(getSleeperType(os.getString("CIF_sleepers", " ").charAt(0)))
			.setReservation(getReservationType(os.getString("CIF_reservations", " ").charAt(0)))
			//enum Catering { TROLLEY, BUFFET, HOT_BUFFET, MEAL_FIRST, RESTAURANT_FIRST, RESTAURANT, WHEELCHAIR }
			.setCatering(Catering.NO) //TODO
			//.setInternationalUicCode(on.getString("uic_code", ""))
			.setAtocCode(o.getString("atoc_code", ""))
			.setPlanLocs(planLocs)
		;
		
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
		.setSpeed((short)os.getQuotedLongOr0("CIF_speed"))
		
		.setGuard(oc.indexOf('G')>=0)
		.setPushPull(oc.indexOf('P')>=0)
		.setAsRequired(oc.indexOf('Q')>=0)
		.setAirCon(oc.indexOf('R')>=0)
		.setAsRequiredToYard(oc.indexOf('Y')>=0)
		.setLargeGuage(oc.indexOf('Z')>=0)
		.setHasFirstClass(!"S".equals(os.getString("CIF_train_class", "")))
		.setInternationalUicCode(on.getString("uic_code", ""))
//		.setPerformanceMonitored("Y".equals(o.getString("applicable_timetable", "")))
		.setSchedule(
			new Schedule.Builder()
			.setStart(getYearsDays(o.getYMD("schedule_start_date")))
			.setEnd(getYearsDays(o.getYMD("schedule_end_date")))
			.setDays(getDays(o.getString("schedule_days_runs")))
			.build()
		);

		return plan.build();
	}

	private PlanLoc planLocFromJson(JSONObject o) {
		String ri = o.getString("record_identity");
		int departure = 0;
		PlanLoc.Type type;
		if (e("LO", ri)) {
			type = PlanLoc.Type.ORIGIN;
			departure = o.getHalfMins("departure");
		} else if (e("LT", ri)) {
			type = PlanLoc.Type.DESTINATION;
		} else {
			String passStr = o.getString("pass", "");
			if (passStr==null) {
				type = PlanLoc.Type.CALLING;
				departure = o.getHalfMins("departure");
			} else {
				type = PlanLoc.Type.PASSING;
				departure = HalfMins.build(passStr);
			}
		}
		return new PlanLoc.Builder()
			.setType(type)
			.setLoc(locations.getByTiploc(o.getString("tiploc_code", ""))) 
			.setArrival(o.getHalfMins("arrival"))
			.setDeparture(departure)
			.setPublicArrival(o.getHalfMins("public_arrival"))
			.setPublicDeparture(o.getHalfMins("public_departure"))
			.setPlatform(o.getString("platform", ""))
			.setLine(o.getString("line", ""))
			.setPath(o.getString("path", ""))
			.setEngineering(o.getHalfMins("engineering_allowance"))
			.setPathing(o.getHalfMins("pathing_allowance"))
			.setPerformance(o.getHalfMins("performance_allowance"))
			.build()
		;
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
				) : Power.NONE;
	}
	
	private Category fromCode(String code) {
		if (code==null || code.length()==0) return Category.NONE;
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
			return Category.NONE;
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
