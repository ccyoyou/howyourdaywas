package edu.umich.eecs498f11.howyourdaywas;
import java.util.ArrayList;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Post;

public class FacebookSource implements DataSource {
	FacebookClient fbClient = new DefaultFacebookClient("AAACEdEose0cBAN8DBtEtadKgnbx41jLFpiTMVb3RMfkBnZC7Ez4XICZATjXboO526zt9mEg60JFhhUbLQLJSkT4Pa96GLzs7fDKzHoAuKkNmF74eJC");
	public List<String> getData() {
		Connection<Post> publicSearch = fbClient.fetchConnection("me/feed", Post.class);
		final ArrayList<String> statuses = new ArrayList<String>();
		statuses.add(publicSearch.getData().get(0).getMessage());
		
		return statuses;
	}
}
