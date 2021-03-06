import java.util.*;

/**
 * Interface of algorithm. Provides basic functionalities to users.
 * @author Han Zhu
 *
 */
public interface Algorithm {
	/**
	 * Link the algorithm to data.
	 * @param dc data storage.
	 */
	public void loadDataCenter(DataCenter dc);
	
	/**
	 * Provide functionality a: given a user and item, predict the user's preference for the item.
	 * @param user 
	 * @param item
	 * @return the predicted rating of user for item.
	 */
	public double getRatingByUserAndItem(int uid, String mid);
	
	/**
	 * Provide functionality b: given a user and a number n, predict user's top n highest-rated items.
	 * @param user
	 * @param n threshold
	 * @return user's top n highest-rated items.
	 */
	public Set<String> getTopNRatingItems(int uid, int n);
}
