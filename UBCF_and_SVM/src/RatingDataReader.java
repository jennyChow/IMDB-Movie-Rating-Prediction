import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class RatingDataReader {
    private String ratingDataFileName;
    private HashMap<Integer, HashSet<Integer>> userToRatedMoviesMap;
    private HashMap<Integer, HashSet<Integer>> movieToRatedUsersMap;
    private HashMap<Integer, Double> userAverageRating;
    private HashMap<Integer, Double> movieAverageRating;

    // User to movie rating. Stored as user_movie => rating
    private HashMap<String, Integer> ratingMap;

    RatingDataReader(String rating_file) {
        this.ratingDataFileName = rating_file;
        userToRatedMoviesMap = new HashMap<Integer, HashSet<Integer>>();
        movieToRatedUsersMap = new HashMap<Integer, HashSet<Integer>>();
        ratingMap = new HashMap<String, Integer>();
        userAverageRating = new HashMap<Integer, Double>();
        movieAverageRating = new HashMap<Integer, Double>();
    }

    public HashMap<Integer, Double> getMovieAverageRating() {
        return movieAverageRating;
    }

    public HashMap<Integer, Double> getUserAverageRating() {
        return userAverageRating;
    }

    public HashMap<String, Integer> getRatingMap() {
        return this.ratingMap;
    }

    public HashMap<Integer, HashSet<Integer>> getUserToRatedMoviesMap() {
        return this.userToRatedMoviesMap;
    }

    public HashMap<Integer, HashSet<Integer>> getMovieToRatedUsersMap() {
        return this.movieToRatedUsersMap;
    }

    public void readRatingData() {
        System.out.println("============ Reading rating data! =============");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(this.ratingDataFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = null;
        while (true) {
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (line == null || line.isEmpty()) {
                break;
            }
            this.processLine(line);
        }

        for (Integer key : userAverageRating.keySet()) {
            int cnt = userToRatedMoviesMap.get(key).size();
            double average_value = userAverageRating.get(key) / cnt;
            userAverageRating.put(key, average_value);
        }

        for (Integer key : movieAverageRating.keySet()) {
            int cnt = movieToRatedUsersMap.get(key).size();
            double average_value = movieAverageRating.get(key) / cnt;
            movieAverageRating.put(key, average_value);
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("================ Finished reading data! ================");
    }

    private void processLine(String line) {
        String[] splited_line = line.split("\\s+");
        if (splited_line.length != 4) {
            return;
        }
        int user_id = Integer.parseInt(splited_line[0]);
        int movie_id = Integer.parseInt(splited_line[1]);
        int rating = Integer.parseInt(splited_line[2]);
        if (!userToRatedMoviesMap.containsKey(user_id)) {
            userToRatedMoviesMap.put(user_id, new HashSet<Integer>());
        }
        userToRatedMoviesMap.get(user_id).add(movie_id);

        if (!movieToRatedUsersMap.containsKey(movie_id)) {
            movieToRatedUsersMap.put(movie_id, new HashSet<Integer>());
        }
        movieToRatedUsersMap.get(movie_id).add(user_id);

        ratingMap.put(this.generateRatingKey(user_id, movie_id), rating);

        if (!userAverageRating.containsKey(user_id)) {
            userAverageRating.put(user_id, 0.0);
        }
        double user_rating_new_sum = userAverageRating.get(user_id) + rating;
        userAverageRating.put(user_id, user_rating_new_sum);

        if (!movieAverageRating.containsKey(movie_id)) {
            movieAverageRating.put(movie_id, 0.0);
        }
        double movie_rating_new_sum = movieAverageRating.get(movie_id) + rating;
        movieAverageRating.put(movie_id, movie_rating_new_sum);
    }

    public String generateRatingKey(int user_id, int movie_id) {
        return user_id + "_" + movie_id;
    }
}
