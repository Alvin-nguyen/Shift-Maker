package shiftmaker.client;

import java.util.ArrayList;
import com.google.gwt.user.client.ui.*;

public class StudentSchedule extends DialogBox {

	private String studentName;
	private Schedule stuSchedule;
	
	private Button save = new Button("Save");
	private ArrayList<TextArea> workDays = new ArrayList<TextArea>();
	private TextBox name;
	private TextBox maxHours;
	private TextBox priority;	
	private TextArea mondayHours = new TextArea();
	private TextArea tuesdayHours = new TextArea();
	private TextArea wednesdayHours = new TextArea();
	private TextArea thursdayHours = new TextArea();
	private TextArea fridayHours = new TextArea();
	
	public void setSchedule(Schedule s) {
	   this.studentName = s.name;
	   this.stuSchedule = s;
	}
	
	public Schedule getSchedule() {
	   return stuSchedule;
	}
	
	public String getName() {
		return studentName;
	}
	
	public Button getSaveButton() {
		return save;
	}
	
   public static int fixHour(int hour) {
      if(hour > 5) 
         return hour - 8;
      return hour + 4;
   }
 
   public String getStudentInfo() {
      return stuSchedule.priority + ","+ stuSchedule.name+","+stuSchedule.maxHours+"\n";
   }
   
   public String getHoursInfo() {
      String text;
      String[] hours;
      String ret = "";
      for(TextArea area: workDays) {
         text = area.getText();
         hours = text.split("\\s+");
         for(String s: hours) 
            ret = ret + s+",";
         ret = ret +"\n";
      }
      return ret;
   }
   
	public Schedule makeSchedule() throws Exception {
		Schedule ret = new Schedule();
		String day;
		String[] hoursArray;
		int[] hoursArrayInts;
		ArrayList<Shift> currentShifts;
		
		ret.name = name.getText();
		ret.maxHours = Integer.parseInt(maxHours.getText());
		ret.priority = Integer.parseInt(priority.getText());
		
		for(int i=0 ; i < 5; i++) {
			day = workDays.get(i).getText();
			hoursArray = day.split("-|\\r?\\n|\\s+");
			currentShifts = new ArrayList<Shift>();
			
			if(hoursArray.length != 0 && !hoursArray[0].equals("")) {
				hoursArrayInts = StringToInt(hoursArray);
				for(int j=0; j<hoursArray.length -1; j=j+2) {
				   int from = fixHour(hoursArrayInts[j]);
				   int to = fixHour(hoursArrayInts[j+1]);
					currentShifts.add(new Shift(from ,to ));
				}
			}
			ret.shifts.add(currentShifts);
		}
		
		return ret;
	}
	
	
	/*
	 * Converts the string array into integer array.
	 */
	public int[] StringToInt(String[] sarray) throws Exception {
		if (sarray != null) {
			int intarray[] = new int[sarray.length];
			for (int i = 0; i < sarray.length; i++) {
				intarray[i] = Integer.parseInt(sarray[i]);
			}
			return intarray;
		}
		return null;
	}
	
	public void fillSchedule(){
	   name.setText(stuSchedule.name);
	   maxHours.setText(Integer.toString(stuSchedule.maxHours));
	   priority.setText(Integer.toString(stuSchedule.priority));
	   
	   ArrayList<ArrayList<Shift>> week = stuSchedule.shifts;
	   ArrayList<Shift> day;
	   TextArea dayText;
	   
	   for(int i=0; i<week.size();i++) {
	      day = week.get(i);
	      dayText = workDays.get(i);
	      String hours = "";
	      for(Shift s: day) {
	         hours = dayText.getText() + s.start +"-"+ s.stop +"\n";
	         dayText.setText(hours);
	      }
	   }
	   
	}
	
	
	public StudentSchedule() {
		
		/* set the dialog box options.
		 * false -- we down want clicking out of the box to hide it
		 * true -- we want the box to be modal -- ignore clicking outside of the box
		 */
		super(false, true);
		setText("Add a new student!");
		
		FlowPanel panel = new FlowPanel(); 
		
		Label StudentName = new Label("Name:");
		panel.add(StudentName);
		
		name = new TextBox();
		panel.add(name);
		
		Label monday = new Label("Monday");
		panel.add(monday);
		
		Label tuesday = new Label("Tuesday");
		panel.add(tuesday);
		
		Label wednesday = new Label("Wednesday");
		panel.add(wednesday);
		
		Label thursday = new Label("Thursday");
		panel.add(thursday);
		
		Label friday = new Label("Friday");
		panel.add(friday);
		
		mondayHours.setTabIndex(0);
		tuesdayHours.setTabIndex(0);
		wednesdayHours.setTabIndex(0);
		thursdayHours.setTabIndex(0);
		fridayHours.setTabIndex(0);
				
		workDays.add(mondayHours);
		workDays.add(tuesdayHours);
		workDays.add(wednesdayHours);
		workDays.add(thursdayHours);
		workDays.add(fridayHours);
		
		panel.add(mondayHours);
		panel.add(tuesdayHours);
		panel.add(wednesdayHours);		
		panel.add(thursdayHours);		
		panel.add(fridayHours);
				
		panel.add(save);
		
		maxHours = new TextBox();
		maxHours.setWidth("50px");
		panel.add(maxHours);
		
		Label maxHoursLabel = new Label("Max Hours:");
		panel.add(maxHoursLabel);
		
		Label priorityLabel = new Label("Priority:");
		panel.add(priorityLabel);
		
		priority = new TextBox();
		priority.setWidth("50px");
		panel.add(priority);
		
		add(panel);
	}
}
