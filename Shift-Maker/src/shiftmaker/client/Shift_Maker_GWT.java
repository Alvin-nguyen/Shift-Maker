package shiftmaker.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Shift_Maker_GWT implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */

	public  ArrayList<String> uniqueNames = new ArrayList<String>();
	public  HashMap <String, StudentSchedule> mapping = new HashMap<String, StudentSchedule>();

	static TextBox numWeeks = new TextBox();
	static TextBox startDate = new TextBox();

	public StudentSchedule selectedStudent;
	public String selectedTag;
	public int selectedIndex;
	public String selectedName;
	public String loadFileContents;

	private final Button addStudent = new Button("Add Student");
	private final Button editStudent = new Button("Edit Student");
	private final Button removeStudent = new Button("Remove Student");
	private final Button generateSchedule = new Button("Generate Schedule");
	private final Button load = new Button("Load");
	private final ListBox list = new ListBox();
	private final Button save = new Button("Save");
	private final FormPanel formPanel = new FormPanel();
	private final FileUpload loadFileChooser = new FileUpload();

	public static int[][] spacesFilled= new int[9][5];

	public static ArrayList<ArrayList<TimeSlot>> schedule = new ArrayList<ArrayList<TimeSlot>>();
	public static ArrayList<StudentEmployee> students = new ArrayList<StudentEmployee>();
	public static ArrayList<Score> scoreTable = new ArrayList<Score>();
	static int year,month,day,repeat;


	/**
	 * This button is set every time a student is created or updated. 
	 */
	private Button currentSave;


	public boolean editFlag = false;
	//	private boolean debugFlag = true;


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		RootPanel rootPanel = RootPanel.get();
		rootPanel.setStyleName("none");


		rootPanel.add(addStudent, 10, 281);
		addStudent.setSize("136px", "30px");
		rootPanel.add(editStudent, 176, 281);
		editStudent.setSize("136px", "30px");
		rootPanel.add(removeStudent, 347, 281);
		removeStudent.setSize("136px", "30px");
		rootPanel.add(generateSchedule, 10, 472);
		list.setVisibleItemCount(5);

		rootPanel.add(list, 10, 10);
		list.setSize("473px", "265px");

		Label weeksLabel = new Label("Number of Weeks:");
		rootPanel.add(weeksLabel,10,370);
		weeksLabel.setSize("108px", "30px");
		rootPanel.add(numWeeks,124,370);
		numWeeks.setSize("168px", "18px");

		Label dateHelpLabel = new Label("(MM/DD/YYYY)");
		rootPanel.add(dateHelpLabel,306,334);
		dateHelpLabel.setSize("136px", "30px");

		Label startDateLabel = new Label("Start Date:");
		rootPanel.add(startDateLabel, 10,334);
		startDateLabel.setSize("108px", "30px");
		rootPanel.add(startDate,124,334);
		startDate.setSize("168px", "18px");

		rootPanel.add(save, 176, 472);
		save.setSize("113px", "30px");

		loadFileChooser.setStyleName("none");

		// Create a FileUpload widget.
		loadFileChooser.setName("loadFileChooser");
		AbsolutePanel p = new AbsolutePanel();
		formPanel.setWidget(p);
		p.setHeight("45px");
		p.add(loadFileChooser, 10, 10);
		loadFileChooser.setSize("323px", "26px");
		p.add(load, 340, 6);
		load.setSize("123px", "29px");
		formPanel.setStyleName("none");
		rootPanel.add(formPanel, 10, 406);
		formPanel.setSize("473px", "48px");
		//formPanel.setAction(url)
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);

		String action = GWT.getModuleBaseURL() + "LoadService";
		formPanel.setAction(action);

		load.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent c) {
				formPanel.submit();
			}
		});

		formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				loadFileContents = event.getResults();
				list.clear();
				mapping.clear();
				uniqueNames.clear();
				readInStudents();
				//System.out.println(loadFileContents);
			}
		});


		setClickHandlers();
	}

	private void setClickHandlers() {
		addStudent.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent c) {
				final StudentSchedule student = new StudentSchedule();
				currentSave = student.getSaveButton();
				setSaveHandler(currentSave, student);
				student.show();
			}
		});

		editStudent.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent c) {
				if(list.getSelectedIndex() != -1){
					selectedTag = list.getItemText(list.getSelectedIndex());
					selectedIndex = list.getSelectedIndex();
					selectedStudent = mapping.get(selectedTag);
					selectedName = selectedStudent.getName();
					editFlag = true;
					selectedStudent.show();
				}
			}
		});

		removeStudent.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent c) {
				String removeMe = list.getItemText(list.getSelectedIndex());
				StudentSchedule temp = mapping.get(removeMe);
				mapping.remove(removeMe);
				uniqueNames.remove(temp.getName());
				list.removeItem(list.getSelectedIndex());
			}
		});

		generateSchedule.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent c) {
				String icsString = "";

				Set<Entry<String, StudentSchedule>> temp = mapping.entrySet();

				for(Entry<String, StudentSchedule> entry :temp) {
					StudentSchedule s = entry.getValue();
					Schedule s2 = s.getSchedule();
					StudentEmployee student = new StudentEmployee(s2.name, s2.maxHours, s2.priority);
					for(int i=0; i<s2.shifts.size(); i++) {
						for(int j=0; j< s2.shifts.get(i).size(); j++) {
							student.addShift(s2.shifts.get(i).get(j), i);
						}
					}
					students.add(student);
				}

				Collections.sort(students);
				prepareSlots();
				fillSpaces();

				for(int loop = 0; loop < 20; loop++) {
					scoreTable();
					removeStudents();
				}
				ArrayList<Integer> dates = errorCheckDate();
				if(dates != null) {
					icsString = icsFormat(dates.get(2), dates.get(0), dates.get(1), dates.get(3));
					PopupPanel error = new PopupPanel(true, true);
					error.setTitle("Success!");
					error.setWidget(new Label("Calander created!"));
					error.show();
				}
				else {
					PopupPanel error = new PopupPanel(true, true);
					error.setTitle("Error!");
					error.setWidget(new Label("Error in Start Date!"));
					error.show();
				}

				final String link = GWT.getModuleBaseURL() + "IcsService";
				RequestBuilder request = new RequestBuilder(RequestBuilder.POST, link);

				RequestCallback callback = new RequestCallback() {

					@Override
					public void onResponseReceived(Request request, Response response) {
						Window.Location.assign(link);
					}

					@Override
					public void onError(Request request, Throwable exception) {
						System.out.println("bad");
					}

				};

				request.setCallback(callback);
				try {
					request.setRequestData(icsString);
					request.send();
				} catch (RequestException e) {
					PopupPanel error = new PopupPanel(true, true);
					error.setTitle("Error!");
					error.setWidget(new Label("Error saving Schedule"));
					error.show();
					e.printStackTrace();
				}

			}
		});

		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent c) {
				String saveFileContents = "";
				int nWeeks = 1;
				try {
					nWeeks = Integer.parseInt(numWeeks.getText());
				} catch (NumberFormatException e) {
					PopupPanel error = new PopupPanel(true, true);
					error.setTitle("Error!");
					error.setWidget(new Label("Please correctly format the number of weeks. Default is 1"));
					error.show();
				}

				String[] temp = startDate.getText().split("\\|\\s+|/");
				ArrayList<Integer> intDate = stringToInt(temp);
				intDate.add(nWeeks);

				// print the first line
				saveFileContents += intDate.get(0) +","+intDate.get(1)+","+intDate.get(2)+","+intDate.get(3)+"\n";

				Set<Entry<String, StudentSchedule>> s = mapping.entrySet();
				for(Entry<String, StudentSchedule> entry : s){
					StudentSchedule stuFrame = entry.getValue();

					// print out the student info
					saveFileContents += stuFrame.getStudentInfo();

					// print out the hours info
					saveFileContents += stuFrame.getHoursInfo();

				}

				final String link = GWT.getModuleBaseURL() + "FileService";
				RequestBuilder request = new RequestBuilder(RequestBuilder.POST, link);

				RequestCallback callback = new RequestCallback() {

					@Override
					public void onResponseReceived(Request request, Response response) {
						Window.Location.assign(link);
					}

					@Override
					public void onError(Request request, Throwable exception) {
						System.out.println("bad");
					}

				};

				request.setCallback(callback);
				try {
					request.setRequestData(saveFileContents);
					request.send();
				} catch (RequestException e) {
					PopupPanel error = new PopupPanel(true, true);
					error.setTitle("Error!");
					error.setWidget(new Label("Error saving Schedule"));
					error.show();
					e.printStackTrace();
				}
			}
		});

	}


	public static String createTag(Schedule s) {
		return s.name + "[Max Hours: " + s.maxHours + "] [Priority: " + s.priority +"]";
	}

	public void setSaveHandler(Button currentSave, final StudentSchedule student) {

		//Set up the Save button handler.
		currentSave.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent c) {
				Schedule studentSchedule = null;
				String tag = null;

				try{
					studentSchedule = student.makeSchedule();
					tag = createTag(studentSchedule);
				}
				catch (Exception e){
					//e.printStackTrace();
					PopupPanel error = new PopupPanel(true, true);
					error.setTitle("Error!");
					error.setWidget(new Label("Error saving student Schedule; check hours."));
					error.show();
					return;
				}

				if(editFlag){

					if(!uniqueNames.contains(studentSchedule.name) || selectedName.equals(studentSchedule.name)) {
						list.removeItem(selectedIndex);
						list.insertItem(tag, selectedIndex);

						uniqueNames.remove(selectedName);
						mapping.remove(selectedTag);

						uniqueNames.add(studentSchedule.name);
						student.setSchedule(studentSchedule);
						mapping.put(tag,student);
						editFlag = false;
						// if(debugFlag) debug(); //TODO
						student.hide();
					}
					else {
						PopupPanel error = new PopupPanel(true, true);
						error.setTitle("Error!");
						error.setWidget(new Label("Name already exists!"));
						error.show();
					}
				}
				else {
					if(!uniqueNames.contains(studentSchedule.name)) {
						list.addItem(tag);
						uniqueNames.add(studentSchedule.name);
						student.setSchedule(studentSchedule);
						mapping.put(tag, student);
						// if(debugFlag) debug(); //TODO
						student.hide();
					}
					else {
						PopupPanel error = new PopupPanel(true, true);
						error.setTitle("Error!");
						error.setWidget(new Label("Name already exists!"));
						error.show();
					}
				}  
			}
		});
	}
	public ArrayList<Integer> stringToInt(String[] sarray) throws NumberFormatException {
		if (sarray != null) {
			ArrayList<Integer> intarray = new ArrayList<Integer>();
			for (int i = 0; i < sarray.length; i++) {
				intarray.add(Integer.parseInt(sarray[i]));
			}
			return intarray;
		}
		return null;
	}

	private void readInStudents() {
		String[] fileContentsLines = loadFileContents.split("\n");
		String studentLine;
		String[] studentLineSplit, shiftLineSplit;
		String name;
		int hours, priority;


		System.out.println(fileContentsLines[0] + "\n\n----------\n\n"+ fileContentsLines[1]);
		String[] temp = fileContentsLines[1].split(",");
		String dateLine = temp[0] +"/"+ temp[1] +"/"+temp[2];
		numWeeks.setText(temp[3]);
		startDate.setText(dateLine);

		for(int i = 2; i < fileContentsLines.length - 1; i++) {
			studentLine = fileContentsLines[i];
			studentLineSplit = studentLine.split(",");
			priority = Integer.parseInt(studentLineSplit[0].trim());
			name = studentLineSplit[1].trim();
			hours = Integer.parseInt(studentLineSplit[2].trim());

			Schedule temp2 = new Schedule(name, hours, priority);
			ArrayList<ArrayList<Shift>> week= temp2.shifts;
			StudentSchedule newSchedule = new StudentSchedule();
			setSaveHandler(newSchedule.getSaveButton(), newSchedule);
			newSchedule.setSchedule(temp2);
			for(int j = 1; j <= 5; j++) {
				studentLine = fileContentsLines[i+j];
				ArrayList<Shift> day = new ArrayList<Shift>();
				week.add(day);

				studentLineSplit = studentLine.split(",");
				for(String shiftString: studentLineSplit) {

					shiftLineSplit = shiftString.split("-");
					int from = Integer.parseInt(shiftLineSplit[0].trim());
					int to = Integer.parseInt(shiftLineSplit[1].trim());
					Shift tempShift = new Shift(from, to);
					day.add(tempShift);
				}
			}
			i+=5;

			try {
				String tag = createTag(temp2);
				System.out.println(tag);
				uniqueNames.add(name);
				newSchedule.fillSchedule();
				newSchedule.setSchedule(newSchedule.makeSchedule());
				mapping.put(tag, newSchedule);
				list.addItem(tag);  
			} catch (Exception e) {
				PopupPanel error = new PopupPanel(true, true);
				error.setTitle("Error!");
				error.setWidget(new Label("Some sort of exception"));
				error.show();
				e.printStackTrace();
			}
		}
	}

	public static void removeStudents() {
		ArrayList<ArrayList<StudentEmployee>> round = new ArrayList<ArrayList<StudentEmployee>>();
		round.add(new ArrayList<StudentEmployee>());
		round.add(new ArrayList<StudentEmployee>());
		round.add(new ArrayList<StudentEmployee>());	
		round.add(new ArrayList<StudentEmployee>());
		round.add(new ArrayList<StudentEmployee>());

		for(Score sc: scoreTable) {
			if(sc.score > 50) {
				if(!round.get(sc.day).contains(sc.student)) {
					if(spacesFilled[sc.hour][sc.day] > 2) {

						sc.student.totalHours--;
						ArrayList<Integer> hours = sc.student.availHours.get(sc.day);
						if(hours.indexOf(sc.hour) > -1)
							hours.remove(hours.indexOf(sc.hour));
						spacesFilled[sc.hour][sc.day]--;

						round.get(sc.day).add(sc.student);

						// need to actually remove the shift, or adjust it
						sc.student.removeHour(sc.day, sc.hour);

						//remove the student from the timeslot
						schedule.get(sc.day).get(sc.hour).removeStudent(sc.student);
					}
				}
			}
		}
	}

	public static int getMaxFilled() {
		int max = 0;
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 9; j++) {
				if (spacesFilled[j][i] > max) {
					max = spacesFilled[j][i];
				}
			}
		}

		return max;
	}

	/***	
==========================================================================
==========================================================================
==========================================================================

Scoring of removing a slot

Easy to remove						|						Hard to remove
100									|									 0
High Score -------------------------|--------------------------- Low Score
									|
Low Priority Employee				1				High Priority Employee
Available More than 20 hours		2				Under max hours wanted
Top or bottom of shift				3					   Middle of shift
>4 students avail					4					<=4 students avail

Score of 0 -- CANNOT REMOVE
Score of 100 -- Remove the shit out of this shift

[1] Last check when comparing two equal scores

[2] As trimming hours get lower than max hrs wanted, 
		-- 10% lower: 80pts
		-- 20% lower: 60pts
		-- 30% lower: 40pts
		-- 40% lower: 20pts
		(Set max percentage difference allowed at program run)

[3] If in the middle of the shift
		-- higher score if longer shift 9hrs
			-- get break when working long shifts
		-- lower score if shorter shift 3hrs
			-- dont want tiny blocks of 1-2 hr shifts

[4] <=2 automatic score of 0
		-- don't want less than two people working
		-- <=4 lower score
		-- >4 higher score

Plan of attack:
Score the entire schedule, then remove the highest rated items.
	-- don't remove or trim more than one shift per slot
		--- maybe make exceptions for slots with +4 available students
		--- DONT remove same person in multiple spots. only remove each person ONCE per round
Repeat as necessary
==========================================================================
==========================================================================
==========================================================================
	 */
	public static double scoreRemoval(StudentEmployee stud, int day, int hour) {
		double score = 100.0;

		Shift s = stud.getShift(day, hour);	

		// [4] students available in time slot
		if(spacesFilled[hour][day] <= 2) {
			// don't remove if there are only two people available
			return 0;
		} else if(spacesFilled[hour][day] <= 3) {
			score -= 65;
		}  else if(spacesFilled[hour][day] <= 4) {
			score -= 45;
		} else {
			score -= 30-((spacesFilled[hour][day]-4)*10);
		}


		// [3] middle, beginning, or end of shift
		if(hour == s.start || hour == s.stop) {
			// Edges are easier to remove
			// but it doesn't effect if they are removed
			score -= 0;
		} else {
			// middle are not advised
			score -= 30;
		}

		// [2] how many hours left
		double percentage = (double)stud.totalHours/(double)stud.maxHours;
		if(stud.totalHours-1 > 20 || stud.totalHours-1 > stud.maxHours) {
			// not allowed to work more than 20 hours, remove!
			score -= 0;
		} else {
			score *= percentage;
		}

		percentage = (double)stud.maxHours/(double)20.0;
		score *= percentage;


		// [1] priority

		return score;
	}

	public static void scoreTable() {
		scoreTable = new ArrayList<Score>();
		for(int day = 0; day < 5; day++) {
			for(int hour = 0; hour < 9; hour++) {
				ArrayList<Double> scores = new ArrayList<Double>();
				/*for(LockedIn lock: schedule.get(day).get(hour).lockedStudents) {
					//add the scores here!!!!
					scores.add(scoreRemoval(lock.student, day, hour));
					schedule.get(day).get(hour).setScores(scores);
					Score sc = new Score(day, hour, lock.student, scoreRemoval(lock.student, day, hour));
					if(sc.score > 30)
						scoreTable.add(sc);
				}*/
				for(StudentEmployee stud: schedule.get(day).get(hour).students) {
					//add the scores here!!!!
					scores.add(scoreRemoval(stud, day, hour));
					schedule.get(day).get(hour).setScores(scores);
					Score sc = new Score(day, hour, stud, scoreRemoval(stud, day, hour));
					if(sc.score > 30)
						scoreTable.add(sc);
				}
			}
		}
	}

	public static void printScoreTable() {
		/*for(int day = 0; day < 5; day++) {
			for(int hour = 0; hour < 9; hour++) {
				System.out.println(schedule.get(day).get(hour).scores);
			}
			System.out.println();
		}*/
		Collections.sort(scoreTable);
		System.out.println("\n"+scoreTable);
	}

	public static void prepareSlots() {
		for(int i = 0; i < 5; i++) {
			ArrayList<TimeSlot> daySlot = new ArrayList<TimeSlot>();
			schedule.add(daySlot);
			for(int j = 0; j < 9; j++) {
				daySlot.add(new TimeSlot());
			}
		}
	}

	public static void fillSpaces() {
		for(StudentEmployee stud: students) {
			for(int i = 0; i < 5; i++) {
				ArrayList<Shift> day = stud.availability.get(i);
				for(Shift dayShift: day) {
					for(int j = dayShift.start; j < dayShift.stop; j++) {
						spacesFilled[j][i]++;
						//schedule.get(i).get(j).addLocked(new LockedIn(stud));
						schedule.get(i).get(j).addStudent(stud);
					}
				}
			}
		}
	}

	public static void printSpaces() {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 5; j++) {
				//System.out.print(schedule.get(j).get(i).numLocked()+"/"+spacesFilled[i][j]+"\t");
				System.out.print(spacesFilled[i][j]+"\t");
			}
			System.out.println();
		}
	}

	public static int fixHour(int hour) {
		if(hour > 5) {
			return hour - 8;
		}

		return hour + 4;
	}

	public static int revertHour(int hour) {
		if(hour >= 5)
			return hour-4;

		return hour+8;
	}

	public static int revertMillitaryHour(int hour) {		
		return hour+8;
	}

	public static String twoDigit(int time) {
		if(time < 10)
			return "0"+time;

		return ""+time;
	}


	public static String icsFormat(int year, int month, int date, int count) {
		String icsString = "BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//Cal Poly, ITS//ShiftMaker v1.0//EN:\r\n";

		for(StudentEmployee stud: students) {
			for(int day = 0; day < 5; day++) {
				for(int shift = 0; shift < stud.availability.get(day).size(); shift++) {
					Shift thisShift = stud.availability.get(day).get(shift);

					icsString += "BEGIN:VEVENT\r\n";

					icsString += "DTSTART:"+year+twoDigit(month)+twoDigit(date+day)+"T";
					icsString += twoDigit(revertMillitaryHour(thisShift.start))+"0000\r\n";

					icsString += "DTEND:"+year+twoDigit(month)+twoDigit(date+day)+"T";
					icsString += twoDigit(revertMillitaryHour(thisShift.stop))+"0000\r\n";

					icsString += "RRULE:FREQ=WEEKLY;COUNT="+count+"\r\n";

					icsString += "SUMMARY:"+stud.name+"\r\n";

					icsString += "END:VEVENT\r\n";
				}
			}
		}

		icsString += "END:VCALENDAR\r\n";

		return icsString;
	}

	/*
	 * Does error checking on date and number of weeks field
	 * returns array: [ Month, Day, Year, Iterations] | null
	 */
	public ArrayList<Integer> errorCheckDate() {
		try {
			String date = startDate.getText();
			String[] temp = date.split("\\|\\s+|/");
			ArrayList<Integer> intDate = stringToInt(temp);
			intDate.add(Integer.parseInt(numWeeks.getText()));
			System.out.println(intDate.toString());
			return intDate;
		}
		catch(NumberFormatException e) {
			return null;
		}
	}
}
