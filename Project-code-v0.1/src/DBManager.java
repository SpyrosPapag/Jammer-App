import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class DBManager {

    /* For database connection */
    private static final String JDBC_URL = Main.JDBC_URL;
    private static final String DB_USER = Main.DB_USER;
    private static final String DB_PASSWORD = Main.DB_PASSWORD;

    public Integer credentialsCheck(String username, String password)
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "SELECT user_id FROM user WHERE username = ? AND password = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query))
                {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    try (ResultSet resultSet = preparedStatement.executeQuery())
                    {
                        if(resultSet.next()) // Returns true if the result set is not empty (valid credentials)
                            return resultSet.getInt(1); // return user_id
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return 0; // 0 for invalid
    }

    public Integer insertUser(String username, String password)
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "INSERT INTO user(username, password) VALUES(?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
                {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    preparedStatement.executeUpdate();
                    try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                        if (resultSet.next())
                            return resultSet.getInt(1);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return 0; // 0 for invalid
    }

    public ArrayList<Post> getPosts(List<Integer> suggestedPostIDs)
    {
        ArrayList<Post> postResults = new ArrayList<>();
        String placeholders = suggestedPostIDs.stream().map(id -> "?").collect(Collectors.joining(","));
        String query = "SELECT * FROM post WHERE post_id IN (" + placeholders + ")";
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                try (PreparedStatement preparedStatement = connection.prepareStatement(query))
                {

                    for (int i = 0; i < suggestedPostIDs.size(); i++)
                        preparedStatement.setInt(i + 1, suggestedPostIDs.get(i));

                    try (ResultSet resultSet = preparedStatement.executeQuery())
                    {
                        Integer post_id, poster_id, popularity;
                        String description, pictures_url_json, type, location, date;
                        while(resultSet.next())
                        {
                            post_id = resultSet.getInt(1);
                            poster_id = resultSet.getInt(2);
                            pictures_url_json = resultSet.getString(3);
                            description = resultSet.getString(4);
                            type = resultSet.getString(5);
                            date = resultSet.getString(6);
                            location = resultSet.getString(7);
                            popularity = resultSet.getInt(8);
                            postResults.add(new Post(post_id, poster_id, description, pictures_url_json, type, date, location, popularity));
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return postResults;
    }

    public ArrayList<Post> getPosts(Integer user)
    {
        ArrayList<Post> postResults = new ArrayList<>();
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "SELECT * FROM post WHERE poster_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query))
                {
                    preparedStatement.setInt(1, user);

                    try (ResultSet resultSet = preparedStatement.executeQuery())
                    {
                        Integer post_id, poster_id, popularity;
                        String description, pictures_url_json, type, location, date;
                        while(resultSet.next())
                        {
                            post_id = resultSet.getInt(1);
                            poster_id = resultSet.getInt(2);
                            pictures_url_json = resultSet.getString(3);
                            description = resultSet.getString(4);
                            type = resultSet.getString(5);
                            date = resultSet.getString(6);
                            location = resultSet.getString(7);
                            popularity = resultSet.getInt(8);
                            postResults.add(new Post(post_id, poster_id, description, pictures_url_json, type, date, location, popularity));
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return postResults;
    }

    public ArrayList<Object> getProfileInfo(Integer user)
    {
        ArrayList<Object> userInfoResults = new ArrayList<Object>();
        userInfoResults.add(user);
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "SELECT avatar_url,bio,verified FROM user WHERE user_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query))
                {
                    preparedStatement.setInt(1, user);
                    try (ResultSet resultSet = preparedStatement.executeQuery())
                    {
                        if(resultSet.next())
                        {
                            userInfoResults.add(resultSet.getString(1));
                            userInfoResults.add(resultSet.getString(2));
                            userInfoResults.add(resultSet.getBoolean(3));
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return userInfoResults;
    }

    public Integer addPost(Post post)
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "INSERT INTO post(poster_id, pictures_url_json, description, type, date, location) VALUES(?, ?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
                {
                    preparedStatement.setInt(1, post.getPoster_id());
                    preparedStatement.setString(2,post.getPictures_url_json());
                    preparedStatement.setString(3, post.getDescription());
                    preparedStatement.setString(4, post.getType());
                    preparedStatement.setString(5, post.getDate());
                    preparedStatement.setString(6, post.getLocation());

                    preparedStatement.executeUpdate();
                    try (ResultSet resultSet = preparedStatement.getGeneratedKeys())
                    {
                        if (resultSet.next())
                            return resultSet.getInt(1);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public ArrayList<Object> getProfileDetails(Integer userId) {
        ArrayList<Object> profileDetails = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT username, bio, verified FROM user WHERE user_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, userId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            profileDetails.add(resultSet.getString("username"));
                            profileDetails.add(resultSet.getString("bio"));
                            profileDetails.add(resultSet.getBoolean("verified"));
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return profileDetails;
    }
    
    public boolean checkVerificationRequest(Integer userId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT request FROM user WHERE user_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return rs.getInt("request") == 1;
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean submitVerificationRequest(Integer userId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
                String query = "UPDATE user SET request = 1 WHERE user_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setInt(1, userId);
                    return stmt.executeUpdate() > 0;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    public String getUserPreferences(Integer userId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
                // Changed column name from preferences_json to preferences
                String query = "SELECT preferences_json FROM user WHERE user_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return rs.getString("preferences_json");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateAvatarUrl(int userId, String avatarFileName) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
                String query = "UPDATE user SET avatar_url = ? WHERE user_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, avatarFileName);
                    stmt.setInt(2, userId);
                    return stmt.executeUpdate() > 0;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();

        }
        return false;
    }

    public boolean saveUserPreferences(Integer userId, String preferences) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
                // Changed column name from preferences_json to preferences
                String query = "UPDATE user SET preferences_json = ? WHERE user_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, preferences);
                    stmt.setInt(2, userId);
                    return stmt.executeUpdate() > 0;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
