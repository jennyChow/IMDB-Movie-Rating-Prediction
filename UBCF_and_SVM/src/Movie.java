public class Movie {
    private int movieID;
    private boolean genres[];
    Movie(int mid, boolean[] genr) {
        movieID = mid;
        genres = genr;
    }

    public boolean[] getGenres() {
        return genres;
    }

    public int getMovieID() {
        return movieID;
    }
}
