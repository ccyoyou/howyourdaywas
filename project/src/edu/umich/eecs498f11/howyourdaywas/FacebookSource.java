package edu.umich.eecs498f11.howyourdaywas;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;

public class FacebookSource implements DataSource {
	
	FacebookClient fbClient = new DefaultFacebookClient("AAACEdEose0cBAOribywQZBP7YLqBjqDD7uw1gqymDB1TSgF9sNSALbWHPGRmeWVVNag4cDCxe4b8kphOVfyWwCuwlBJHPua6GRlRgRZBYYP6vIE5dT");
	
	public List<String> getData() {
		final Calendar twentyFourHoursAgo = Calendar.getInstance();
		twentyFourHoursAgo.add(Calendar.HOUR, -24);
		
		final Connection<Post> feedConnection = fbClient.fetchConnection("me/statuses", Post.class);
		final List<String> statuses = new ArrayList<String>();
		
		final List<Post> posts = feedConnection.getData();
		for(final Post post : posts) {
			final Calendar statusDate = Calendar.getInstance();
			statusDate.setTime(post.getUpdatedTime());
			if(statusDate.after(twentyFourHoursAgo)) {
				System.out.println(post.getMessage());
				statuses.add(post.getMessage());
			} else {
				break;
			}
		}

		return statuses;
	}
}
