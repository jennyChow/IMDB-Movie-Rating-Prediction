import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class MovieInfoReader {
    private String movieInfoFileName;
    private HashMap<Integer, Movie> moviesMap;

    MovieInfoReader(String movie_info_file_name) {
        this.movieInfoFileName = movie_info_file_name;
        moviesMap = new HashMap<Integer, Movie>();
    }

    public Movie getMovieByID(int movie_id) {
        return moviesMap.get(movie_id);
    }

    public void processLine(String line) {
        String[] splited_line = line.split("\\|");
        if (splited_line.length != 24) {
            return;
        }
        Integer movie_id = Integer.parseInt(splited_line[0]);
        boolean genres[] = new boolean[19];
        for (int i=0; i<19; i++) {
            genres[i] = splited_line[i+5].equals("1");
        }
        Movie movie = new Movie(movie_id, genres);
        moviesMap.put(movie_id, movie);
    }

    public void readData() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(this.movieInfoFileName));
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

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Integer key : moviesMap.keySet()) {
            Movie movie = moviesMap.get(key);
        }
    }

    public static void main(String[] args) {
        MovieInfoReader movieInfoReader = new MovieInfoReader("u.item");
        movieInfoReader.readData();
    }

}
