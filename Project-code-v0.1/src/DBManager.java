import javax.swing.*;
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
                String query = "SELECT user_id, push_notifications, listing_notifications, event_notifications, chat_notifications " +
                               "FROM user WHERE username = ? AND password = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query))
                {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    try (ResultSet resultSet = preparedStatement.executeQuery())
                    {
                        if(resultSet.next()) // Returns true if the result set is not empty (valid credentials)
                            Main.pushNotif = resultSet.getBoolean(2);
                            Main.listingNotif = resultSet.getBoolean(3);
                            Main.eventNotif = resultSet.getBoolean(4);
                            Main.chatNotif = resultSet.getBoolean(5);
                            return resultSet.getInt(1); // return user_id
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return 0; // 0 for invalid
    }

    

    public void updateNotificationSettings(){
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "UPDATE user SET push_notifications = ?, listing_notifications = ?, event_notifications = ?, chat_notifications = ? WHERE user_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
                {
                    preparedStatement.setBoolean(1, Main.pushNotif);
                    preparedStatement.setBoolean(2, Main.listingNotif);
                    preparedStatement.setBoolean(3, Main.eventNotif);
                    preparedStatement.setBoolean(4, Main.chatNotif);
                    preparedStatement.setInt(5, Main.loggeduser);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void distributeNotifications(Post post){
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "INSERT INTO notification(user_id, source_id, source_type) SELECT follower_id, ?, 0 FROM followers INNER JOIN " +
                               "user ON follower_id = user_id " +
                               "WHERE following_id = ? ";
                if(post.getType().equals("event")){
                    query += "AND event_notifications = 1";
                }
                else{
                    query += "AND listing_notifications = 1";
                }
                try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
                {
                    preparedStatement.setInt(1, post.getPost_id());
                    preparedStatement.setInt(2, post.getPoster_id());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void distributeNotifications(Integer user_id, Integer chat_id){
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "INSERT INTO notification(user_id, source_id, source_type) " +
                               "SELECT user_id, chat_id, 1 " +
                               "FROM chat INNER JOIN user ON user_id = member1 OR user_id =  member2 " +
                               "WHERE chat_notifications = TRUE AND user_id != ? AND chat_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
                {
                    preparedStatement.setInt(1, user_id);
                    preparedStatement.setInt(2, chat_id);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Notification> getNotifications(Integer user_id){
        ArrayList<Notification> notificationResults = new ArrayList<>();
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "SELECT * FROM notification WHERE user_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query))
                {
                    preparedStatement.setInt(1, user_id);

                    try (ResultSet resultSet = preparedStatement.executeQuery())
                    {
                        int notification_id, source_id, source_type;
                        while(resultSet.next())
                        {
                            notification_id = resultSet.getInt(1);
                            source_id = resultSet.getInt(3);
                            source_type = resultSet.getInt(4);
                            notificationResults.add(new Notification(notification_id, source_id, source_type));
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return notificationResults;
    }

    public void clearNotifications(Integer user_id){
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "DELETE FROM notification WHERE user_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
                {
                    preparedStatement.setInt(1, user_id);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Post getPost(Integer post_id){
        String query = "SELECT * FROM post WHERE post_id = ?";
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                try (PreparedStatement preparedStatement = connection.prepareStatement(query))
                {
                    preparedStatement.setInt(1, post_id);

                    try (ResultSet resultSet = preparedStatement.executeQuery())
                    {
                        Integer poster_id, popularity;
                        String description, pictures_url_json, type, location, date;
                        while(resultSet.next())
                        {
                            poster_id = resultSet.getInt(2);
                            pictures_url_json = resultSet.getString(3);
                            description = resultSet.getString(4);
                            type = resultSet.getString(5);
                            date = resultSet.getString(6);
                            location = resultSet.getString(7);
                            popularity = resultSet.getInt(8);
                            return new Post(post_id, poster_id, description, pictures_url_json, type, date, location, popularity);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    public String getUsernameById(int userId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT username FROM user WHERE user_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setInt(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("username");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String[]> getChatsForUser(int userId) {
        ArrayList<String[]> chatList = new ArrayList<>();
        String query = "SELECT chat_id, member1, member2 FROM chat WHERE member1 = ? OR member2 = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            int chatId = rs.getInt("chat_id");
                            int member1 = rs.getInt("member1");
                            int member2 = rs.getInt("member2");
                            int otherId = (member1 == userId) ? member2 : member1;
                            String otherUsername = getUsernameById(otherId);
                            chatList.add(new String[]{String.valueOf(chatId), otherUsername});
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return chatList;
    }

    public int getOtherUserId(int chatId, int currentUserId) {
        String query = "SELECT member1, member2 FROM chat WHERE chat_id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setInt(1, chatId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int member1 = rs.getInt("member1");
                            int member2 = rs.getInt("member2");
                            return (member1 == currentUserId) ? member2 : member1;
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return -1; // not found
    }

    public void insertMessage(int senderId, int chatId, String content) {
        String query = "INSERT INTO message (sender_id, target_chat_id, content, send_date) VALUES (?, ?, ?, NOW())";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = connection.prepareStatement(query)) {

                stmt.setInt(1, senderId);
                stmt.setInt(2, chatId);
                stmt.setString(3, content);

                stmt.executeUpdate();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String[]> getMessagesForChat(int chatId) {
        ArrayList<String[]> messages = new ArrayList<>();
        String query = "SELECT sender_id, content FROM message WHERE target_chat_id = ? ORDER BY send_date ASC";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = connection.prepareStatement(query)) {

                stmt.setInt(1, chatId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int senderId = rs.getInt("sender_id");
                        String content = rs.getString("content");
                        String senderUsername = getUsernameById(senderId);
                        messages.add(new String[]{senderUsername, content});
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public ArrayList<String> getPendingRequestUsernames(int recipientId) {
        ArrayList<String> usernames = new ArrayList<>();
        String query = "SELECT u.username FROM chat_request cr " +
                "JOIN user u ON cr.sender_id = u.user_id " +
                "WHERE cr.recipient_id = ? AND cr.status = 'pending'";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = connection.prepareStatement(query)) {

                stmt.setInt(1, recipientId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    usernames.add(rs.getString("username"));
                }

            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return usernames;
    }


    public void acceptChatRequest(int senderId, int recipientId) {
        String updateRequestStatus = "UPDATE chat_request SET status = 'Accepted' WHERE sender_id = ? AND recipient_id = ? AND status = 'pending'";
        String insertChat = "INSERT INTO chat (member1, member2) VALUES (?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
                conn.setAutoCommit(false);

                try (PreparedStatement updateStmt = conn.prepareStatement(updateRequestStatus);
                     PreparedStatement insertStmt = conn.prepareStatement(insertChat)) {

                    // Update request status
                    updateStmt.setInt(1, senderId);
                    updateStmt.setInt(2, recipientId);
                    int updatedRows = updateStmt.executeUpdate();

                    if (updatedRows > 0) {
                        // Insert into chat only if request was updated
                        insertStmt.setInt(1, senderId);
                        insertStmt.setInt(2, recipientId);
                        insertStmt.executeUpdate();

                        conn.commit();
                    } else {
                        conn.rollback();
                    }
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    public int getUserIdByUsername(String username) {
        String query = "SELECT user_id FROM user WHERE username = ?";
        int userId = -1;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }

    public void declineChatRequest(int senderId, int recipientId) {
        String updateRequestStatus = "UPDATE chat_request SET status = 'Declined' WHERE sender_id = ? AND recipient_id = ? AND status = 'pending'";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(updateRequestStatus)) {

                stmt.setInt(1, senderId);
                stmt.setInt(2, recipientId);
                stmt.executeUpdate();

            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<String> getPostsByUserId(int userId) {
        ArrayList<String> posts = new ArrayList<>();
        String query = "SELECT post_id, description, type FROM post WHERE poster_id = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = connection.prepareStatement(query)) {

                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    long postId = rs.getLong("post_id");
                    String desc = rs.getString("description");
                    String type = rs.getString("type");
                    posts.add("[" + type + "] #" + postId + ": " + desc);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return posts;
    }

    public Boolean isInterested(Integer user, Integer post){
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "SELECT interested_user_id FROM interested WHERE interested_user_id = ? AND target_post_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query))
                {
                    preparedStatement.setInt(1, user);
                    preparedStatement.setInt(2, post);
                    try (ResultSet resultSet = preparedStatement.executeQuery())
                    {
                        if(resultSet.isBeforeFirst())
                        {
                            return false;
                        }
                        else
                            return true;
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void expressInterest(Integer user, Post post) {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD))
            {
                String query = "INSERT INTO interested VALUES(?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
                {
                    preparedStatement.setInt(1, user);
                    preparedStatement.setInt(2, post.getPost_id());

                    int affected_rows = preparedStatement.executeUpdate();
                    if (affected_rows > 0) {
                        query = "INSERT INTO chat_request(sender_id, recipient_id, source_id) VALUES(?, ?, ?)";
                        try(PreparedStatement insertStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
                        {
                            insertStatement.setInt(1, user);
                            insertStatement.setInt(2, post.getPoster_id());
                            insertStatement.setInt(3, post.getPost_id());

                            insertStatement.executeUpdate();
                        }
                    }

                    query = "UPDATE post INNER JOIN " +
                            "(SELECT target_post_id, COUNT(interested_user_id) AS total FROM interested WHERE target_post_id = ?) AS interested_users " +
                            "ON post.post_id = interested_users.target_post_id " +
                            "SET popularity = interested_users.total";
                    try(PreparedStatement updateStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS))
                    {
                        updateStatement.setInt(1, post.getPost_id());

                        updateStatement.executeUpdate();
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
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

        public ArrayList<Integer> getUserPosts(Integer userId) {
        ArrayList<Integer> posts = new ArrayList<>();
        String query = "SELECT Post_id FROM post WHERE Poster_id = ?";

        try (Connection conn = DriverManager.getConnection(Main.JDBC_URL, Main.DB_USER, Main.DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                posts.add(rs.getInt("Post_id"));
            }

            return posts;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean deletePost(Integer postId) {
        String query = "DELETE FROM post WHERE post_id = ?";

        try (Connection conn = DriverManager.getConnection(Main.JDBC_URL, Main.DB_USER, Main.DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, postId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Add this method to DBManager class
    public String getPostType(Integer postId) {
        String query = "SELECT type FROM post WHERE post_id = ?";

        try (Connection conn = DriverManager.getConnection(Main.JDBC_URL, Main.DB_USER, Main.DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("type");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    // Add these methods to DBManager class
    public String getPostCaption(int postId) {
        String query = "SELECT description FROM post WHERE post_id = ?";
        try (Connection conn = DriverManager.getConnection(Main.JDBC_URL, Main.DB_USER, Main.DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("description");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updatePostCaption(int postId, String newCaption) {
        String query = "UPDATE post SET description = ? WHERE post_id = ?";
        try (Connection conn = DriverManager.getConnection(Main.JDBC_URL, Main.DB_USER, Main.DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newCaption);
            pstmt.setInt(2, postId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String getPostPictures(int postId) {
        String query = "SELECT pictures_url_json FROM post WHERE post_id = ?";
        try (Connection conn = DriverManager.getConnection(Main.JDBC_URL, Main.DB_USER, Main.DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("pictures_url_json");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean updatePostPictures(int postId, String picturesJson) {
        String query = "UPDATE post SET pictures_url_json = ? WHERE post_id = ?";
        try (Connection conn = DriverManager.getConnection(Main.JDBC_URL, Main.DB_USER, Main.DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, picturesJson);
            pstmt.setInt(2, postId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Method to check if a username already exists in the 'user' table
    public boolean usernameExists(String username) {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connect to the database
            try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
                // SQL query to find a user by username
                String query = "SELECT user_id FROM user WHERE username = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        return resultSet.next(); // If a record is found, username exists
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace(); // Log any exceptions (driver not found, SQL error, etc.)
        }
        return false; // Default: username does not exist
    }


public boolean updateUserBio(String username, String bio) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(Main.JDBC_URL, Main.DB_USER, Main.DB_PASSWORD);
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE user SET bio = ? WHERE username = ?")) {
                ps.setString(1, bio);
                ps.setString(2, username);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
	
	public Integer insertUser(String mail, String password) {
       try {
         Class.forName("com.mysql.cj.jdbc.Driver");
          try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            //Check if mail already exists
           String checkQuery = "SELECT user_id FROM user WHERE mail = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
               checkStmt.setString(1, mail);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                  //  Mail already exists
                     return 0;
                   }
              }
          }
    // Insert query with auto-generated user_id
            String query = "INSERT INTO user(mail, password) VALUES(?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, mail);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                 if (resultSet.next()) {
                   return resultSet.getInt(1); // Return newly generated user_id
               }
            }
        }
        }
       } catch (ClassNotFoundException | SQLException e) {
          e.printStackTrace(); // Log exceptions
         }
         return 0; // Return 0 for failure (e.g., insert failed)
    }
	public boolean saveUserPreferences(Integer userId, String preferences) {
        try (Connection conn = DriverManager.getConnection(Main.JDBC_URL, Main.DB_USER, Main.DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("UPDATE user SET music_preferences = ? WHERE user_id = ?")) {
            ps.setString(1, preferences);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0; // Returns true if at least one row is updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Returns false in case of an error
        }
    }
}
