public class Main {

    /* For database connection */
    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/jammer";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "root";
    public static int loggeduser = 1;

    static Post mypost = new Post(1, 1, "Hallo", "photos_temp_tennis.png", "event",
            "2025-05-26", "Here", 10);
    public static void main(String[] args) {
        new PostScreen(mypost);
    }
}
