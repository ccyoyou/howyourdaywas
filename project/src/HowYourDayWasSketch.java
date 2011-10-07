import java.util.List;

import processing.core.PApplet;
import edu.umich.eecs498f11.howyourdaywas.DataSource;
import edu.umich.eecs498f11.howyourdaywas.TwitterSource;

public class HowYourDayWasSketch extends PApplet {

	private static final long serialVersionUID = -450426086211320391L;

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "HowYourDayWasSketch" });
	}

	public void setup() {
		// TODO
		// for display, we'll likely steal a lot of the fonts and full-screen stuff from cdzombak's NoiseSketch (in processing-exercises)
		
		// Example Twitter usage:
		final DataSource twitter = new TwitterSource();
		final List<String> tweets = twitter.getData();
		
		for (final String tweet : tweets) {
			System.out.println(tweet);
		}
	}
	
	public void draw() {
		// TODO
	}
	
}
