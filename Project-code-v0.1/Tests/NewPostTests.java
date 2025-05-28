import java.util.ArrayList;
import java.util.List;

public class NewPostTests {
    public void testNewPost()
    {
        // new Post object
        Integer poster = 1;
        String description = "Test Post Description";
        String photo_urls = "image1.png,image2.jpg";
        String tag = "venue";
        String date = "2023-10-01";
        String location = "Test Location";
        Integer testPopularity = 10;

        Post post = new Post(null, poster, description, photo_urls, tag, date, location, testPopularity);

        // insert new Post to DB
        DBManager newPostManager = new DBManager();
        Integer post_id = newPostManager.addPost(post);

        assert post_id > 0: "Post ID should be greater than 0";

        // get posts from DB
        ArrayList<Post> posts = newPostManager.getPosts(List.of(post_id));
        assert posts.size() == 1 : "There should be exactly one post with this id";

        Post retrievedPost = posts.getFirst();
        boolean found = retrievedPost.getPoster_id().equals(poster)             &&
                        retrievedPost.getDescription().equals(description)      &&
                        retrievedPost.getPictures_url_json().equals(photo_urls) &&
                        retrievedPost.getType().equals(tag)                     &&
                        retrievedPost.getDate().equals(date)                    &&
                        retrievedPost.getLocation().equals(location)            &&
                        retrievedPost.getPopularity().equals(testPopularity)    ;

        assert found : "Post info from DB doesnt match inserted info";
    }

    public static void main(String[] args)
    {
        NewPostTests tests = new NewPostTests();
        tests.testNewPost();
        System.out.println("Post creation test passed.");
    }
}