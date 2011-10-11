package edu.umich.eecs498f11.howyourdaywas;

import java.util.List;

/**
 * Defines an interface used to fetch posts from a data source.
 * 
 * Initialization can be performed in your class's constructor.
 * 
 * @author cdzombak
 *
 */
public interface DataSource {
	
	/**
	 * Gets data from the data source. May block.
	 * 
	 * @return
	 */
	public List<DataPoint> getData();
}
