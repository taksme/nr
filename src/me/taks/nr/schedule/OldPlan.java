package me.taks.nr.schedule;

import java.util.Iterator;

import org.apache.commons.collections4.iterators.ArrayIterator;

public class OldPlan extends Schedule implements Iterable<OldPlanLoc> {
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
//	private String internationalUicCode;
	private String atocCode;
	private OldPlanLoc[] planLocs;
	
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

/*	public String getInternationalUicCode() {
		return internationalUicCode;
	}
*/
	public String getAtocCode() {
		return atocCode;
	}

	
	public OldPlanLoc getStartPlan() {
		return planLocs.length>0 ? planLocs[0] : null;
	}

	public OldPlanLoc getFinishPlan() {
		return planLocs.length>0 ? planLocs[planLocs.length-1] : null;
	}

	@Override
	public Iterator<OldPlanLoc> iterator() {
		return new ArrayIterator<OldPlanLoc>(planLocs);
	}
	
	public int getPlanLocCount() {
		return planLocs.length;
	}

	public OldPlanLoc getPlanLoc(int index) {
		return planLocs[index];
	}

	public OldPlan setId(String id) {
		this.id = id;
		return this;
	}

	public OldPlan setDelete(boolean isDelete) {
		this.isDelete = isDelete;
		return this;
	}

	public OldPlan setTrainType(TrainType type) {
		this.type = type;
		return this;
	}

	public OldPlan setCategory(Category category) {
		this.category = category;
		return this;
	}

	public OldPlan setHeadcode(String headcode) {
		this.headcode = headcode;
		return this;
	}

	public OldPlan setPortionId(String portionId) {
		this.portionId = portionId;
		return this;
	}

	public OldPlan setPower(Power power) {
		this.power = power;
		return this;
	}

	public OldPlan setEmuClass(short emuClass) {
		this.emuClass = emuClass;
		return this;
	}

	public OldPlan setHauledTonnage(short hauledTonnage) {
		this.hauledTonnage = hauledTonnage;
		return this;
	}

	public OldPlan setSpeed(short speed) {
		this.speed = speed;
		return this;
	}

	public OldPlan setGuard(boolean guard) {
		flags = (byte)(guard ? flags|OldPlan.guard : flags&~OldPlan.guard);
		return this;
	}

	public OldPlan setPushPull(boolean pushPull) {
		flags = (byte)(pushPull ? flags|OldPlan.pushPull : flags&~OldPlan.pushPull);
		return this;
	}

	public OldPlan setAsRequired(boolean asRequired) {
		flags = (byte)(asRequired ? flags|OldPlan.asRequired : flags&~OldPlan.asRequired);
		return this;
	}

	public OldPlan setAirCon(boolean airCon) {
		flags = (byte)(airCon ? flags|OldPlan.airCon : flags&~OldPlan.airCon);
		return this;
	}

	public OldPlan setAsRequiredToYard(boolean asRequiredToYard) {
		flags = (byte)(asRequiredToYard ? flags|OldPlan.asRequiredToYard : flags&~OldPlan.asRequiredToYard);
		return this;
	}

	public OldPlan setLargeGuage(boolean largeGuage) {
		flags = (byte)(largeGuage ? flags|OldPlan.largeGuage : flags&~OldPlan.largeGuage);
		return this;
	}

	public OldPlan setHasFirstClass(boolean hasFirstClass) {
		flags = (byte)(hasFirstClass ? flags|OldPlan.hasFirstClass : flags&~OldPlan.hasFirstClass);
		return this;
	}

	public OldPlan setPerformanceMonitored(boolean performanceMonitored) {
		this.performanceMonitored = performanceMonitored;
		return this;
	}

	public OldPlan setSleeperType(SleeperType sleeperType) {
		this.sleeperType = sleeperType;
		return this;
	}

	public OldPlan setReservation(Reservation reservation) {
		this.reservation = reservation;
		return this;
	}

	public OldPlan setCatering(Catering catering) {
		this.catering = catering;
		return this;
	}

/*	public Plan setInternationalUicCode(String internationalUicCode) {
		this.internationalUicCode = internationalUicCode;
		return this;
	}
*/
	public OldPlan setAtocCode(String atocCode) {
		this.atocCode = atocCode;
		return this;
	}


	public OldPlan setPlanLocs(OldPlanLoc[] planLocs) {
		this.planLocs = planLocs;
		return this;
	}
	
	public OldPlan ready() {
		return this;
	}
}
