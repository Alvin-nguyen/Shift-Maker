package shiftmaker.client;

import java.util.ArrayList;

import shiftmaker.client.Shift;

public class Schedule {

	public String name;
	public int maxHours;
	public int priority;
	
	public ArrayList<ArrayList<Shift>> shifts = new ArrayList<ArrayList<Shift>>();
	
	public Schedule(String n, int h, int p) {
	   name = n;
	   maxHours = h;
	   priority = p;
	}
	
	public Schedule() {
	   
	}
}
