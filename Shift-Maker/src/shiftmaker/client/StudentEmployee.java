package shiftmaker.client;

import java.util.*;

public class StudentEmployee implements Comparable<StudentEmployee> {
	public String name;
	public int maxHours;
	public int priority;
	public int totalHours;
	public int lockedHours;
	public ArrayList<ArrayList<Shift>> availability;
	public ArrayList<ArrayList<Integer>> availHours;
		
	public StudentEmployee(String n, int max, int pri) {
		name = n;
		maxHours = max;
		priority = pri;
		totalHours = 0;
		lockedHours = 0;
		
		availability = new ArrayList<ArrayList<Shift>>();
		availability.add(new ArrayList<Shift>());
		availability.add(new ArrayList<Shift>());
		availability.add(new ArrayList<Shift>());
		availability.add(new ArrayList<Shift>());
		availability.add(new ArrayList<Shift>());
		
		availHours = new ArrayList<ArrayList<Integer>>();
		availHours.add(new ArrayList<Integer>());
		availHours.add(new ArrayList<Integer>());
		availHours.add(new ArrayList<Integer>());
		availHours.add(new ArrayList<Integer>());
		availHours.add(new ArrayList<Integer>());
	}
	
	public void addShift(Shift added, int day) {
		availability.get(day).add(added);
		totalHours += added.stop - added.start;
		
		for(int i = added.start; i < added.stop; i++) {
			availHours.get(day).add(i);
		}
	}
	
	public void addLocked() {
		lockedHours++;
	}
	
	public Shift getShift(int day, int hour) {
		for(Shift spot: availability.get(day)) {
			if(spot.start <= hour && spot.stop >= hour)
				return spot;
		}
		
		return null;
	}
	
	public void removeHour(int day, int hour) {		
		Shift spot = getShift(day, hour);
		
		// only one hour shift
		if((spot.start == hour && spot.stop == hour+1) || (spot.start == hour-1 && spot.stop == hour)) {
		    availability.get(day).remove(availability.get(day).indexOf(spot));
		}
		// trim the beginning
		else if(spot.start == hour) {
		    spot.start++;
		}
		// trim the end
		else if(spot.stop == hour+1) {
		    spot.stop--;
		}
		// split into two shifts
		else {
            if(this.name.equals("Clement") && day == 1 && hour == 4) {
		        System.out.println("\n\n\n\n------------\n----------\n"+spot.start+" " + spot.stop+"\n----------------\n------------\n\n\n\n\n\n");
		    }
		    int oldStop = spot.stop;
			spot.stop = hour;
			Shift addShift = new Shift(hour+1, oldStop);
			availability.get(day).add(addShift);
		}

		//availHours.get(day).remove(availHours.indexOf(hour));
	}
	
	public String toString() {
		return "("+priority+") "+name+" "+totalHours + "/"+maxHours+" hours";
	}
	
	public int compareTo(StudentEmployee that) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;
		
		if(this.priority < that.priority)
			return BEFORE;
		else if(this.priority == that.priority)
			return EQUAL;
		
		return AFTER;
	}
	
	public int compareTotalHours(StudentEmployee that) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;
		
		if(this.totalHours < that.totalHours)
			return BEFORE;
		else if(this.totalHours == that.totalHours)
			return EQUAL;
		
		return AFTER;
	}
	
	public boolean equals(Object thatE) {
		StudentEmployee that = (StudentEmployee)thatE;
		
		return this.name.equals(that.name);
	}
}