import java.util.*;

public class Suggested {


    public ArrayList<Post> suggestPosts(Integer user, String type)
    {
        // Make some suggestions based on user and type
        List<Integer> suggestedPostIDs = Arrays.asList(1,2,3,4);

        // get suggested posts from DB
        DBManager manager = new DBManager();
        return manager.getPosts(suggestedPostIDs);
    }
}
