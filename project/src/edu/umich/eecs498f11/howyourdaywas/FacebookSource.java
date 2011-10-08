package edu.umich.eecs498f11.howyourdaywas;
import java.util.ArrayList;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Post;

public class FacebookSource implements DataSource {
	FacebookClient fbClient = new DefaultFacebookClient("AAACEdEose0cBAEZC7tQ9uXeod7zckUWWJEviofstfas0Lsjr2dbtOZBDv8MY6eUFeNfv7fU03YXbgpCkApAuTvBSdAP6PpwZAokFPfMBKqOwlFzN5fR");
	public List<String> getData() {
		Connection<Post> publicSearch = fbClient.fetchConnection("search", Post.class, Parameter.with("q", "double"), Parameter.with("type", "post"));
		final ArrayList<String> statuses = new ArrayList<String>();
		statuses.add(publicSearch.getData().get(0).getMessage());
		
		return statuses;
	}
}
