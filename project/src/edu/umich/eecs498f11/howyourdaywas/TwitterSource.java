package edu.umich.eecs498f11.howyourdaywas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterSource implements DataSource {

	public  static final String TWITTER_USER = "cdzombak";
	private static final int    TWITTER_PAGE_COUNT = 20;
	
	private final Twitter mTwitter;
	
	/**
	 * performs initialization; doesn't block
	 */
	public TwitterSource() {
		mTwitter = TwitterFactory.getSingleton();
	}

	/**
	 * Get all tweets from TWITTER_USER from the last 24 hours
	 * 
	 * blocks.
	 */
	public List<DataPoint> getData() {
		final ArrayList<DataPoint> tweets = new ArrayList<DataPoint>();
		
		final Calendar twentyFourHoursAgo = Calendar.getInstance();
		twentyFourHoursAgo.add(Calendar.HOUR, -24);
		
		final Paging paging = new Paging(1, TWITTER_PAGE_COUNT);
		boolean findMoreTweets = true;
		try {
			while (findMoreTweets) {
				final ResponseList<Status> currentPage = mTwitter.getUserTimeline(TWITTER_USER, paging);

				for (final Status s : currentPage) {
					// assuming we're getting Tweets in descending chronological order, as soon as we see
					// one more than 24 hours old we stop saving them and stop requesting more pages
					final Calendar tweetDate = Calendar.getInstance();
					tweetDate.setTime(s.getCreatedAt());
					if (tweetDate.before(twentyFourHoursAgo)) {
						findMoreTweets = false;
						break;
					}
					DataPoint newPoint = new DataPoint();
					newPoint.date = tweetDate;
					newPoint.text = s.getText();
					tweets.add(newPoint);
				}
				
				paging.setPage(paging.getPage()+1);
			}
		} catch (TwitterException e) {
			System.err.println("Failed fetching Tweets!" + e.getMessage());
			e.printStackTrace();
			System.exit(ExitCodes.TWITTER_ERROR);
		}
		
		return tweets;
	}

	public static class ExitCodes {
		public static final int TWITTER_ERROR = 1;
	}
}
