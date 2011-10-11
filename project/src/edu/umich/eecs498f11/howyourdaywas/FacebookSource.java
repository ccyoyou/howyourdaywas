package edu.umich.eecs498f11.howyourdaywas;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;

public class FacebookSource implements DataSource {
	
	FacebookClient fbClient = new DefaultFacebookClient("AAACEdEose0cBAAXi9XD4vwfshqSWgLYVziJgU8aFzGotA6fiUOboH61ZBd9C1tu9ORDZCMNIJM1tICFiXz2LqUHwUW4JS6Y0d2TcWyT70D9sLHgLmK");
	

	public List<DataPoint> getData() {
		final Calendar twentyFourHoursAgo = Calendar.getInstance();
		twentyFourHoursAgo.add(Calendar.HOUR, -24);
		
		final Connection<Post> feedConnection = fbClient.fetchConnection("me/statuses", Post.class);
		final List<DataPoint> statuses = new ArrayList<DataPoint>();
		
		final List<Post> posts = feedConnection.getData();
		for(final Post post : posts) {
			final Calendar statusDate = Calendar.getInstance();
			statusDate.setTime(post.getUpdatedTime());
			if(statusDate.after(twentyFourHoursAgo)) {
				DataPoint newPoint = new DataPoint();
				newPoint.date = statusDate;
				newPoint.text = post.getMessage();
				statuses.add(newPoint);
			} else {
				break;
			}
		}

		return statuses;
	}
}
