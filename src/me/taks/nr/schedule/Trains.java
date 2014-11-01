package me.taks.nr.schedule;

import java.util.Collection;
import java.util.Hashtable;

import me.taks.nr.location.Location;

import org.apache.commons.collections4.map.MultiValueMap;

public class Trains {
	private Hashtable<String, Train> byId = new Hashtable<>();
	private MultiValueMap<String, Train> byHeadcode = new MultiValueMap<>();
	private MultiValueMap<Location, Train> byLoc = new MultiValueMap<>();
	
	public void add(Train train) {
		byHeadcode.put(train.getHeadcode(), train);
		byId.put(train.getId(), train);
	}
	
	public void add(Plan plan) {
		Train train = byId.get(plan.getId());
		if (null==train) {
			train = new Train(plan);
			add(train);
		}
		else train.add(plan);
/*		for (PlanLoc pl : plan.getPlanLocsList()) {
			Location l = pl.getLocation();
			if (!byLoc.containsValue(l, train))
				byLoc.put(l, train);
		}
*/	}
	
	public Train getById(String id) {
		return byId.get(id);
	}
	
	public Collection<Train> getByHeadcode(String headcode) {
		return byHeadcode.getCollection(headcode);
	}
}
