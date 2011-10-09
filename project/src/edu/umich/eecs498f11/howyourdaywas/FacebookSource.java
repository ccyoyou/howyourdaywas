package edu.umich.eecs498f11.howyourdaywas;
import java.util.ArrayList;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;

public class FacebookSource implements DataSource {
	
	FacebookClient fbClient = new DefaultFacebookClient("AAACEdEose0cBAOribywQZBP7YLqBjqDD7uw1gqymDB1TSgF9sNSALbWHPGRmeWVVNag4cDCxe4b8kphOVfyWwCuwlBJHPua6GRlRgRZBYYP6vIE5dT");
	
	public List<String> getData() {
		final Connection<Post> feedConnection = fbClient.fetchConnection("me/statuses", Post.class);
		final ArrayList<String> statuses = new ArrayList<String>();

		final List<Post> posts = feedConnection.getData();
		for(final Post post : posts) {
			System.out.println(post.getUpdatedTime() + ": " + post.getMessage());
			statuses.add(post.getMessage());
		}

		return statuses;
	}
}
