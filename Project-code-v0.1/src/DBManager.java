import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class DBManager {

    /* For database connection */
    private static final String JDBC_URL = Main.JDBC_URL;
    private static final String DB_USER = Main.DB_USER;
    private static final String DB_PASSWORD = Main.DB_PASSWORD;
    private static final int    SUGGESTED_AMOUNT = 100;

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

    public ArrayList<Post> getPosts(Integer user, String type)
    {
        ArrayList<Post> postResults = new ArrayList<>();
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query;
                if(type.equals("event"))
                    query = "SELECT * FROM post WHERE post.type = 'event' ORDER BY post.date DESC LIMIT ?";
                else
                    query = "SELECT * FROM post WHERE post.type = 'venue' OR post.type = 'lesson' ORDER BY post.date DESC LIMIT ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query))
                {
                    preparedStatement.setInt(1, SUGGESTED_AMOUNT);

                    try (ResultSet resultSet = preparedStatement.executeQuery())
                    {
                        Integer post_id, poster_id, popularity;
                        String description, pictures_url_json, post_type, location, date;
                        while(resultSet.next())
                        {
                            post_id = resultSet.getInt(1);
                            poster_id = resultSet.getInt(2);
                            pictures_url_json = resultSet.getString(3);
                            description = resultSet.getString(4);
                            post_type = resultSet.getString(5);
                            date = resultSet.getString(6);
                            location = resultSet.getString(7);
                            popularity = resultSet.getInt(8);
                            postResults.add(new Post(post_id, poster_id, description, pictures_url_json, post_type, date, location, popularity));
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
}
