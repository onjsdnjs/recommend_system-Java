import java.util.*;

/**
 * 
 * @author Patrick_Pu
 *
 */
public class AlgorithmKNN implements Algorithm {
	private DataCenter dc;
	private int SIZE = 20;

	@Override
	public void loadDataCenter(DataCenter dc) {
		this.dc = dc;
	}

	@Override
	public double getRatingByUserAndMovie(User user, Movie movie) {
		System.out.println("Getting neighbors...");
		List<User> neighbors = getNeighbors(user, movie);
//		for (User neighbor : neighbors) {
//			System.out.println(neighbor.getId());
//		}
		System.out.println("Done!");
		System.out.println("Getting user average...");
		double avg = dc.getAvgRatingScoreByUser(user);
		double numerator = 0, denominator = 0;
		System.out.println("Done!");
		
		System.out.println("Getting kNN...");
		for (User u : neighbors) {
			double s = u.getSimilarity();
			numerator += s * (dc.getRating(u, movie) - dc.getAvgRatingScoreByUser(u));
			denominator += Math.abs(s);
		}
		System.out.println("Done!");
		
		if (denominator == 0) {
			return avg;
		}
		return avg + numerator / denominator;
	}

	@Override
	public List<Movie> getTopNRatingMovies(User user, int n) {
		List<Movie> movies = new ArrayList<>();
		PriorityQueue<MovieContainer> pq = new PriorityQueue<>();

		for (Movie movie : dc.getMovies()) {
			MovieContainer mc = new MovieContainer(movie, getRatingByUserAndMovie(user, movie));
			if (pq.size() < n) {
				pq.offer(mc);
			} else if (pq.peek().getPredict() < mc.getPredict()) {
				pq.poll();
				pq.offer(mc);
			}
		}

		while (!pq.isEmpty()) {
			movies.add(pq.poll().getMovie());
		}

		return movies;
	}

	/**
	 * 
	 * @param u1
	 * @param u2
	 * @return
	 */
	private double getSimilarity(User u1, User u2) {
		Set<Movie> commons = getCommonMovies(u1, u2);

		double numerator = 0, denominator1 = 0, denominator2 = 0;
		double avg1 = dc.getAvgRatingScoreByUser(u1);
		double avg2 = dc.getAvgRatingScoreByUser(u2);
		double score1 = 0;
		double score2 = 0;

		for (Movie m : commons) {
			score1 = dc.getRating(u1, m) - avg1;
//			System.out.println(score1);
			score2 = dc.getRating(u2, m) - avg2;
//			System.out.println(score2);
			numerator += score1 * score2;
//			System.out.println(numerator);
			denominator1 += Math.pow(score1, 2);
			denominator2 += Math.pow(score2, 2);
		}

		if (denominator1 == 0 || denominator2 == 0) {
			return 0;
		}
		return numerator / (Math.pow(denominator1 * denominator2, 0.5));
	}

	/**
	 * 
	 * @param user
	 * @return
	 */
	private List<User> getNeighbors(User user, Movie movie) {
		List<User> neighbors = new ArrayList<>();
		PriorityQueue<User> pq = new PriorityQueue<>();

		int count = 0;
		for (User neighbor : dc.getUsersByMovie(movie)) {
			count++;
			System.out.println(count);
			if (!neighbor.equals(user)) {
				neighbor.setSimilarity(getSimilarity(user, neighbor));
//				System.out.println(neighbor.toString());
				if (pq.size() < SIZE) {
					pq.offer(neighbor);
				} else if (pq.peek().getSimilarity() < neighbor.getSimilarity()) {
					pq.poll();
					pq.offer(neighbor);
				}
			}
		}

		while (!pq.isEmpty()) {
			neighbors.add(pq.poll());
		}

		return neighbors;
	}

	/**
	 * 
	 * @param u1
	 * @param u2
	 * @return
	 */
	private Set<Movie> getCommonMovies(User u1, User u2) {
		Set<Movie> m1 = dc.getMoviesByUser(u1);
		Set<Movie> m2 = dc.getMoviesByUser(u2);
		m1.retainAll(m2);
		
		return m1;
	}
}
