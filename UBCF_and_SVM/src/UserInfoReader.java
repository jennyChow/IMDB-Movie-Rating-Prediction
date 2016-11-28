import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class UserInfoReader {
    private String userInfoFileName;
    private HashMap<Integer, User> usersMap;
    UserInfoReader(String user_info_file_name) {
        userInfoFileName = user_info_file_name;
        usersMap = new HashMap<Integer, User>();
    }

    public User getUserByID(int uid) {
        return usersMap.get(uid);
    }

    public void processLine(String line) {
        String[] splited_line = line.split("\\|");
        int user_id = Integer.parseInt(splited_line[0]);
        int age = Integer.parseInt(splited_line[1]);
        boolean is_male = splited_line[2].equals("M");
        User user = new User(user_id, age, is_male);
        usersMap.put(user_id, user);
    }

    public void readData() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(this.userInfoFileName));
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
    }

    public static void main(String[] args) {
        UserInfoReader userInfoReader = new UserInfoReader("u.user");
        userInfoReader.readData();
    }
}
