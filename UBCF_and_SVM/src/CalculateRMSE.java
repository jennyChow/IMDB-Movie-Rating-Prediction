import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CalculateRMSE {
    private String testFile;
    private String predictionFile;

    CalculateRMSE(String test_file, String prediction_file) {
        this.testFile = test_file;
        this.predictionFile = prediction_file;
    }

    public void run() {
        BufferedReader br1 = null, br2 = null;
        try {
            br1 = new BufferedReader(new FileReader(testFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            br2 = new BufferedReader(new FileReader(predictionFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line1 = null, line2 = null;
        long total_sum = 0;
        int total_count = 0;
        while (true) {
            try {
                line1 = br1.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line1 == null || line1.isEmpty()) {
                break;
            }
            try {
                line2 = br2.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int real_value = Integer.parseInt(line1.split("\\s+")[0]);
            int prediction_value = Integer.parseInt(line2.split("\\s+")[0]);
            total_count += 1;
            total_sum += (real_value - prediction_value) * (real_value - prediction_value);
        }
        double rmse = Math.sqrt(new Double(total_sum)/(double)total_count);
        System.out.println("The RMSE using SVM is: " + rmse);
        try {
            br1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String test_file = args[0];
        String output_file = args[1];
        CalculateRMSE calc = new CalculateRMSE(test_file, output_file);
        calc.run();
    }
}
