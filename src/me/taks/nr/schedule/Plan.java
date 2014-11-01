package me.taks.nr.schedule;

import java.util.Iterator;

import org.apache.commons.collections4.iterators.ArrayIterator;

public class Plan implements Iterable<PlanLoc> {
	public enum Category {
		METRO, UNADVERTISED, ORDINARY_PASSENGER, STAFF, MIXED, 
		EUROSTAR, EUROSLEEPER, INTERNATIONAL, MOTORAIL, UNADVERTISED_EXPRESS, EXPRESS_PASSENGER, SLEEPER,
		BUS_REPLACEMENT, BUS_SCHEDULED,
		EMPTY_STOCK, EMPTY_METRO, EMPTY_STAFF,
		POST, POST_OFFICE_PARCELS, PARCELS, EMPTY_PARCELS,
		DEPARMENTAL, ENGINEER, STORES, TEST,
		LIGHT, 
		RAILFREIGHT, TRAINFREIGHT, EUROFREIGHT,
		NONE
	}
	public enum Power { DIESEL_HAULED, DEMU, PACER, TURBO, SPRINTER, FAST_TURBO, VOYAGER, DMU_OTHER,
		ELECTRIC_HAULED, MARK_4_ELECTRIC, MIXED_HAULED, EMU_HAULED, EMU, ACCELERATED_EMU, PARCELS_EMU,
		HST, SHUNTER, NONE
	}
	public enum TrainType { PASSENGER, FREIGHT, BUS, SHIP, TRIP }
	public enum SleeperType { NO, FIRST_ONLY, STANDARD_ONLY, BOTH }
	public enum Reservation { NO, AVAILABLE, RECOMMENDED, BIKES_COMPULSORY, COMPULSORY }
	public enum Catering { NO, TROLLEY, BUFFET, HOT_BUFFET, MEAL_FIRST, RESTAURANT_FIRST, RESTAURANT, WHEELCHAIR }

	private String id;
	private boolean isDelete;
	private TrainType type;
	private Category category;
	private String headcode;
	private String portionId;
	private Power power;
	private short emuClass;
	private short hauledTonnage;
	private short speed;
	private byte flags;
	private static final byte guard 			= 1;
	private static final byte pushPull 			= 1<<1;
	private static final byte asRequired 		= 1<<2;
	private static final byte airCon 			= 1<<3;
	private static final byte performanceMonitored	= 1<<4;
	private static final byte largeGuage 		= 1<<5;
	private static final byte hasFirstClass 	= 1<<6;
	private static final byte asRequiredToYard	= Byte.MIN_VALUE;
	private SleeperType sleeperType;
	private Reservation reservation;
	private Catering catering;
	private String internationalUicCode;
	private String atocCode;
	private PlanLoc[] planLocs;
	private Schedule schedule;
	
	public String getId() {
		return id;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public TrainType getTrainType() {
		return type;
	}

	public Category getCategory() {
		return category;
	}

	public String getHeadcode() {
		return headcode;
	}

	public String getPortionId() {
		return portionId;
	}

	public Power getPower() {
		return power;
	}

	public int getEmuClass() {
		return emuClass;
	}

	public int getHauledTonnage() {
		return hauledTonnage;
	}

	public int getSpeed() {
		return speed;
	}

	public boolean isGuard() {
		return (flags & guard) > 0;
	}

	public boolean isPushPull() {
		return (flags & pushPull) > 0;
	}

	public boolean isAsRequired() {
		return (flags & asRequired) > 0;
	}

	public boolean isAirCon() {
		return (flags & airCon) > 0;
	}

	public boolean isAsRequiredToYard() {
		return (flags & asRequiredToYard) > 0;
	}

	public boolean isLargeGuage() {
		return (flags & largeGuage) > 0;
	}

	public boolean isHasFirstClass() {
		return (flags & hasFirstClass) > 0;
	}

	public boolean isPerformanceMonitored() {
		return (flags & performanceMonitored) > 0;
	}

	public SleeperType getSleeperType() {
		return sleeperType;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public Catering getCatering() {
		return catering;
	}

	public String getInternationalUicCode() {
		return internationalUicCode;
	}

	public String getAtocCode() {
		return atocCode;
	}

	
	public PlanLoc getStartPlan() {
		return planLocs.length>0 ? planLocs[0] : null;
	}

	public PlanLoc getFinishPlan() {
		return planLocs.length>0 ? planLocs[planLocs.length-1] : null;
	}

	@Override
	public Iterator<PlanLoc> iterator() {
		return new ArrayIterator<PlanLoc>(planLocs);
	}
	
	public int getPlanLocCount() {
		return planLocs.length;
	}

	public PlanLoc getPlanLoc(int index) {
		return planLocs[index];
	}
	
	public Schedule getSchedule() {
		return schedule;
	}
	
	public static class Builder {
		private Plan plan = new Plan();
	
		public Builder setId(String id) {
			plan.id = id;
			return this;
		}
	
		public Builder setDelete(boolean isDelete) {
			plan.isDelete = isDelete;
			return this;
		}
	
		public Builder setTrainType(TrainType type) {
			plan.type = type;
			return this;
		}
	
		public Builder setCategory(Category category) {
			plan.category = category;
			return this;
		}
	
		public Builder setHeadcode(String headcode) {
			plan.headcode = headcode;
			return this;
		}
	
		public Builder setPortionId(String portionId) {
			plan.portionId = portionId;
			return this;
		}
	
		public Builder setPower(Power power) {
			plan.power = power;
			return this;
		}
	
		public Builder setEmuClass(short emuClass) {
			plan.emuClass = emuClass;
			return this;
		}
	
		public Builder setHauledTonnage(short hauledTonnage) {
			plan.hauledTonnage = hauledTonnage;
			return this;
		}
	
		public Builder setSpeed(short speed) {
			plan.speed = speed;
			return this;
		}
	
		public Builder setGuard(boolean guard) {
			plan.flags = (byte)(guard ? plan.flags|Plan.guard : plan.flags&~Plan.guard);
			return this;
		}
	
		public Builder setPushPull(boolean pushPull) {
			plan.flags = (byte)(pushPull ? plan.flags|Plan.pushPull : plan.flags&~Plan.pushPull);
			return this;
		}
	
		public Builder setAsRequired(boolean asRequired) {
			plan.flags = (byte)(asRequired ? plan.flags|Plan.asRequired : plan.flags&~Plan.asRequired);
			return this;
		}
	
		public Builder setAirCon(boolean airCon) {
			plan.flags = (byte)(airCon ? plan.flags|Plan.airCon : plan.flags&~Plan.airCon);
			return this;
		}
	
		public Builder setAsRequiredToYard(boolean asRequiredToYard) {
			plan.flags = (byte)(asRequiredToYard ? plan.flags|Plan.asRequiredToYard : plan.flags&~Plan.asRequiredToYard);
			return this;
		}
	
		public Builder setLargeGuage(boolean largeGuage) {
			plan.flags = (byte)(largeGuage ? plan.flags|Plan.largeGuage : plan.flags&~Plan.largeGuage);
			return this;
		}
	
		public Builder setHasFirstClass(boolean hasFirstClass) {
			plan.flags = (byte)(hasFirstClass ? plan.flags|Plan.hasFirstClass : plan.flags&~Plan.hasFirstClass);
			return this;
		}
		
/*		public Builder setPerformanceMonitored(boolean performanceMonitored) {
			plan.performanceMonitored = performanceMonitored;
			return this;
		}
*/	
		public Builder setSleeperType(SleeperType sleeperType) {
			plan.sleeperType = sleeperType;
			return this;
		}
	
		public Builder setReservation(Reservation reservation) {
			plan.reservation = reservation;
			return this;
		}
	
		public Builder setCatering(Catering catering) {
			plan.catering = catering;
			return this;
		}
	
		public Builder setInternationalUicCode(String internationalUicCode) {
			plan.internationalUicCode = internationalUicCode;
			return this;
		}
	
		public Builder setAtocCode(String atocCode) {
			plan.atocCode = atocCode;
			return this;
		}
	
	
		public Builder setPlanLocs(PlanLoc[] planLocs) {
			plan.planLocs = planLocs;
			return this;
		}
		
		public Builder setSchedule(Schedule schedule) {
			plan.schedule = schedule;
			return this;
		}
		
		public Builder ready() {
			return this;
		}

		public Plan build() {
			return plan;
		}
}
}
