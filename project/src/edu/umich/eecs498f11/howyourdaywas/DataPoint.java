package edu.umich.eecs498f11.howyourdaywas;

import java.util.Calendar;
import java.util.Comparator;

public class DataPoint {
	
	public String text;
	public Calendar date;
	
	public DataPoint(final String t, final Calendar d) {
		text = t;
		date = d;
	}
	
	@Override
	public String toString() {
		return text;
	}
	
	public static class DataPointComparator implements Comparator<DataPoint> {
		public int compare(DataPoint arg0, DataPoint arg1) {
			//if 0, dates are equal
			if(arg0.date.compareTo(arg1.date) == 0) {
				return 0;
			}
			//if less than 0, arg0 is before arg1
			else if(arg0.date.compareTo(arg1.date) < 0) {
				return -1;
			}
			//if greater than 0, arg0 is after arg1
			else if(arg0.date.compareTo(arg1.date) > 0) {
				return 1;
			}
			return 0;
		}
	}
	
}
