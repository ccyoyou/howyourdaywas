package edu.umich.eecs498f11.howyourdaywas;
import java.util.ArrayList;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Post;

public class FacebookSource implements DataSource {
	FacebookClient fbClient = new DefaultFacebookClient("AAACEdEose0cBAOribywQZBP7YLqBjqDD7uw1gqymDB1TSgF9sNSALbWHPGRmeWVVNag4cDCxe4b8kphOVfyWwCuwlBJHPua6GRlRgRZBYYP6vIE5dT");
	public List<String> getData() {
		Connection<Post> myFeed = fbClient.fetchConnection("me/statuses", Post.class);
		final ArrayList<String> statuses = new ArrayList<String>();
		for (List<Post> myFeedConnectionPage : myFeed) {
			for(Post post : myFeedConnectionPage) {
				System.out.print(post.getCreatedTime());
				System.out.print(post.getMessage());
				statuses.add(post.toString());
			}
		}
		
		return statuses;
	}
}
