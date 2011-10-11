package edu.umich.eecs498f11.howyourdaywas;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;

public class FacebookSource implements DataSource {
	
	FacebookClient fbClient = new DefaultFacebookClient("AAACEdEose0cBAEgKQZBFrQnyBsJBBMFoJiJ09Ff0RPW24v0r5wo5ZCIvMKIPnLOD7O1AXQdGTjLzB2EpAufW72Hbn9dlEIgLUtDxg7WPC1KZCQ81Uw4");
	

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
