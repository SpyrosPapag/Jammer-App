import java.util.*;

public class Suggested {


    public ArrayList<Post> suggestPosts(Integer user, String type)
    {
        // Make some suggestions based on user and type

        DBManager manager = new DBManager();

        return manager.getPosts(user, type);
    }


}
