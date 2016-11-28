public class SVMDataPreProcessor {
    private String userInfoFile;
    private String movieInfoFile;
    private String trainingFile;
    private String testFile;

    SVMDataPreProcessor(String user_info_file, String movie_info_file, String training_file, String test_file) {
        this.userInfoFile = user_info_file;
        this.movieInfoFile = movie_info_file;
        this.trainingFile = training_file;
        this.testFile = test_file;
    }

    public void run() {
        UserInfoReader userInfoReader = new UserInfoReader(this.userInfoFile);
        MovieInfoReader movieInfoReader  = new MovieInfoReader(this.movieInfoFile);
        userInfoReader.readData();
        movieInfoReader.readData();
        UserBasedPrediction userBasedPrediction = new UserBasedPrediction(trainingFile, testFile);
        userBasedPrediction.computeCorrelation();
        userBasedPrediction.transformDataToSVMFormat(trainingFile, "new_" + trainingFile, movieInfoReader, userInfoReader);
        userBasedPrediction.transformDataToSVMFormat(testFile, "new_" + testFile, movieInfoReader, userInfoReader);
    }

    public static void main(String[] args) {
        String user_file = args[0]; //"u.user";
        String movie_file = args[1]; //"u.item";
        String train_file = args[2]; //"u2.base";
        String test_file = args[3]; //"u2.test";
        SVMDataPreProcessor svmDataPreProcessor = new SVMDataPreProcessor(user_file, movie_file, train_file, test_file);
        svmDataPreProcessor.run();
    }
}
