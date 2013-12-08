package shiftmaker.client;


public class Score implements Comparable<Score>{

	public int day;
	public int hour;
	public StudentEmployee student;
	public double score;
	
	public Score(int d, int h, StudentEmployee s, double sc) {
		day = d;
		hour = h;
		student = s;
		score = sc;
	}
	
	public int compareTo(Score that) {
		final int BEFORE = -1;
		final int AFTER = 1;
		
		if(this.score > that.score)
			return BEFORE;
		else if(this.score == that.score)
			return that.student.compareTo(this.student);
		
		return AFTER;
	}
	
	public String toString() {
		return student.name + " " + day + " " + hour + " :" + score;
	}
}