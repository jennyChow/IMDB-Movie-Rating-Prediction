public class User {
    private int userID;
    private int age;
    private boolean isMale;
    User(int uid, int age, boolean is_male) {
        this.userID = uid;
        this.age = age;
        this.isMale = is_male;
    }

    public int getUserID() {
        return userID;
    }

    public int getAge() {
        return age;
    }

    public boolean isMale() {
        return isMale;
    }
}
