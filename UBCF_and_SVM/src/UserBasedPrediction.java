import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

public class UserBasedPrediction {

    private String trainingFile;
    private String testFile;

    //regularization parameter for correlation
    private static final double BETA = 0.03;

    private RatingDataReader ratingDataReader;

    // User to user correlation. Stored as user1_user2 => correlation
    private HashMap<String, Double> correlationMap;

    UserBasedPrediction(String training_file, String test_file) {
        this.trainingFile = training_file;
        this.testFile = test_file;
        ratingDataReader = new RatingDataReader(this.trainingFile);
        correlationMap = new HashMap<String, Double>();
    }

    private void readRatingData() {
        ratingDataReader.readRatingData();
    }

    public void transformDataToSVMFormat(String original_file, String new_file, MovieInfoReader movie_info_reader,
                                          UserInfoReader user_info_reader) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(original_file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        PrintWriter pr = null;
        try {
            pr = new PrintWriter(new_file);
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
            String new_line = this.getTransformedLine(line, movie_info_reader, user_info_reader);
            pr.println(new_line);
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pr.close();
    }

    private String getTransformedLine(String line, MovieInfoReader movie_info_reader, UserInfoReader user_info_reader) {
        String[] splited_line = line.split("\\s+");
        int user_id = Integer.parseInt(splited_line[0]);
        int movie_id = Integer.parseInt(splited_line[1]);
        int real_rating = Integer.parseInt(splited_line[2]);
        int predicted_rating = this.predictedRating(user_id, movie_id);
        User user = user_info_reader.getUserByID(user_id);
        Movie movie = movie_info_reader.getMovieByID(movie_id);
        String ret = "" + real_rating;
        ret += " 1:" + predicted_rating;
        ret += " 2:" + user.getAge();
        ret += " 3:" + (user.isMale() ? 1 : 0);
        for (int i=0; i<19; i++) {
            ret += " " + (i+4) + ":" + (movie.getGenres()[i] ? 1 : 0);
        }
        return ret;
    }

    // read in test data and predict
    public void predictData() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(this.testFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = null;
        long rmse_sum = 0;
        int total_cnt = 0;
        while (true) {
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line == null || line.isEmpty()) {
                break;
            }
            total_cnt += 1;
            String[] splited_line = line.split("\\s+");
            int user_id = Integer.parseInt(splited_line[0]);
            int movie_id = Integer.parseInt(splited_line[1]);
            int real_rating = Integer.parseInt(splited_line[2]);
            int predicted_rating = this.predictedRating(user_id, movie_id);
            rmse_sum += (real_rating - predicted_rating) * (real_rating - predicted_rating);
        }
        System.out.println("RMSE value: " + Math.sqrt((double)rmse_sum/(double)total_cnt));
    }

    public int predictedRating(int user_id, int movie_id) {
        HashSet<Integer> rated_users = ratingDataReader.getMovieToRatedUsersMap().get(movie_id);
        if (rated_users == null) {
            HashMap<Integer, Double> movie_average_rating = ratingDataReader.getMovieAverageRating();
            if (movie_average_rating.containsKey(movie_id)) {
                return (int)(movie_average_rating.get(movie_id) + 0.5);
            } else {
                return 3;
            }
        }

        HashMap<Integer, Double> user_average_rating = ratingDataReader.getUserAverageRating();
        double ret = user_average_rating.get(user_id);
        double sum_numerator = 0.0;
        double sum_denomitor = 0.0;
        HashMap<String, Integer> rating_map = ratingDataReader.getRatingMap();

        for (Integer rated_user : rated_users) {
            double correlation = this.getCorrelation(rated_user, user_id);
            String key = ratingDataReader.generateRatingKey(rated_user, movie_id);
            double rating = rating_map.get(key);
            rating = rating -  user_average_rating.get(rated_user);
            sum_numerator += rating * correlation;
            sum_denomitor += Math.abs(correlation);
        }
        ret += sum_numerator / sum_denomitor;
        return (int)(ret + 0.5);
    }

    public void computeCorrelation() {
        this.readRatingData();

        HashMap<Integer, HashSet<Integer>> user_to_rated_movies = ratingDataReader.getUserToRatedMoviesMap();
        HashMap<Integer, HashSet<Integer>> movie_to_rated_users = ratingDataReader.getMovieToRatedUsersMap();
        for (Integer user_id1 : user_to_rated_movies.keySet()) {
            HashSet<Integer> movies = user_to_rated_movies.get(user_id1);
            for (Integer movie : movies) {
                HashSet<Integer> rated_users = movie_to_rated_users.get(movie);
                for (Integer rated_user : rated_users) {
                    if (rated_user <= user_id1) {
                        continue;
                    }
                    String correlation_key = user_id1 + "_" + rated_user;
                    if (correlationMap.containsKey(correlation_key)) {
                        continue;
                    }
                    double correlation = this.computeCorrelation(user_id1, rated_user);
                    correlationMap.put(correlation_key, correlation);
                }
            }
        }

    }

    private double getCorrelation(int user_id1, int user_id2) {
        if (user_id1 > user_id2) {
            return getCorrelation(user_id2, user_id1);
        }
        if (user_id1 == user_id2) {
            return 1.0 + this.BETA;
        }
        String key = user_id1 + "_" + user_id2;
        if (correlationMap.containsKey(key)) {
            return correlationMap.get(key);
        } else {
            return this.BETA;
        }
    }


    private double computeCorrelation(int user_id1, int user_id2) {
        if (user_id1 > user_id2) {
            return computeCorrelation(user_id2, user_id1);
        }
        if (user_id1 == user_id2) {
            return 1.0 + this.BETA;
        }
        String key = user_id1 + "_" + user_id2;
        if (correlationMap.containsKey(key)) {
            return correlationMap.get(key);
        }

        HashMap<Integer, HashSet<Integer>> user_to_rated_movies = ratingDataReader.getUserToRatedMoviesMap();
        HashMap<String, Integer> rating_map = ratingDataReader.getRatingMap();
        HashSet<Integer> first_user_rated_movies = user_to_rated_movies.get(user_id1);
        HashSet<Integer> second_user_rated_movies = user_to_rated_movies.get(user_id2);

        double sum_multiplication = 0.0;
        double sum_first_square = 0.0;
        double sum_second_square = 0.0;

        double user1_average = ratingDataReader.getUserAverageRating().get(user_id1);
        double user2_average = ratingDataReader.getUserAverageRating().get(user_id2);

        for (Integer movie1 : first_user_rated_movies) {
            double user1_rating = rating_map.get(ratingDataReader.generateRatingKey(user_id1, movie1));
            user1_rating = user1_rating - user1_average;
            sum_first_square += user1_rating * user1_rating;
            if (second_user_rated_movies.contains(movie1)) {
                double user2_rating = rating_map.get(ratingDataReader.generateRatingKey(user_id2, movie1));
                user2_rating = user2_rating - user2_average;
                sum_second_square += user2_rating * user2_rating;
                sum_multiplication += user1_rating * user2_rating;
            }
        }

        for (Integer movie2 : second_user_rated_movies) {
            if (first_user_rated_movies.contains(movie2)) {
                continue;
            }
            double user2_rating = rating_map.get(ratingDataReader.generateRatingKey(user_id2, movie2));
            user2_rating = user2_rating - user2_average;
            sum_second_square += user2_rating * user2_rating;
        }

        double ret = sum_multiplication / Math.sqrt(sum_first_square * sum_second_square);
        return ret + this.BETA;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: ./UserBasedPrediction training_file_path test_file_path");
            return;
        }
        String training_file = args[0];
        String test_file = args[1];
        UserBasedPrediction userBasedPrediction = new UserBasedPrediction(training_file, test_file);
        userBasedPrediction.computeCorrelation();
        userBasedPrediction.predictData();
    }
}
