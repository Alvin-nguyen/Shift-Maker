package shiftmaker.client;

import java.util.*;

public class TimeSlot {

	//public ArrayList<LockedIn> lockedStudents;
	public ArrayList<StudentEmployee> students;
	public ArrayList<Double> scores;
	
	public TimeSlot() {
		//lockedStudents = new ArrayList<LockedIn>();
		students = new ArrayList<StudentEmployee>();
	}

	public void addStudent(StudentEmployee stud) {
		students.add(stud);	
	}
	/*
	public void addLocked(LockedIn lock) {
		lockedStudents.add(lock);
	}
	
	public void lockInStudent(StudentEmployee stud) {
		lockedStudents.get(lockedStudents.indexOf(stud)).lockIn();
	}
	
	public int numLocked() {
		int lock = 0;
		
		for(LockedIn locked: lockedStudents) {
			if(locked.isLocked)
				lock++;
		}
		return lock;
	}
	*/
	
	public void setScores(ArrayList<Double> setS) {
		scores = setS;
	}

    public void removeStudent(StudentEmployee stud) {
        /*for(int i = 0; i < lockedStudents.size(); i++) {
            if(stud.equals(lockedStudents.get(i).student)) {
                lockedStudents.remove(i);
                return;
            }
        }
		*/
		for(int i = 0; i < students.size(); i++) {
			if(stud.equals(students.get(i))) {
				students.remove(i);
				return;
			}
		}
    }
}