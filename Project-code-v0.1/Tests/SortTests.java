import java.util.ArrayList;
import java.util.Random;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SortTests {
    public void testSort(Integer numberOfPosts)
    {
        ArrayList<Post> posts = new ArrayList<>();

        // add random posts
        Random rand = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String[] locations = {"Athens", "Berlin", "Paris", "London", "Rome", "Madrid", "New York", "Tokyo", "Sydney", "Toronto",
                              "Los Angeles", "Chicago", "San Francisco", "Miami", "Boston", "Vancouver", "Montreal",
                              "Dubai", "Singapore", "Hong Kong", "Beijing", "Seoul", "Bangkok", "Kuala Lumpur", "Istanbul"};

        for (int i = 0; i < numberOfPosts; i++)
        {
            // random date in the last 5 years
            int daysAgo = rand.nextInt(5 * 365);
            LocalDate randomDate = LocalDate.now().minusDays(daysAgo);
            String dateStr = randomDate.format(formatter);

            // random location
            String location = locations[rand.nextInt(locations.length)];

            // random popularity (0-1000)
            int popularity = rand.nextInt(1001);

            posts.add(new Post(null, null, null, null, null, dateStr, location, popularity));
        }

        SortBy.sort(posts, "date");
        for (int i = 1; i < posts.size(); i++)
        {
            LocalDate date1 = LocalDate.parse(posts.get(i - 1).getDate());
            LocalDate date2 = LocalDate.parse(posts.get(i).getDate());
            assert date1.isBefore(date2) || date1.isEqual(date2) : "Posts are not sorted by date";
        }

        SortBy.sort(posts, "distance");
        for (int i = 1; i < posts.size(); i++)
        {
            String location1 = posts.get(i - 1).getLocation();
            String location2 = posts.get(i).getLocation();
            assert location1.compareTo(location2) <= 0 : "Posts are not sorted by distance";
        }

        SortBy.sort(posts, "popularity");
        for (int i = 1; i < posts.size(); i++)
        {
            int popularity1 = posts.get(i - 1).getPopularity();
            int popularity2 = posts.get(i).getPopularity();
            assert popularity1 >= popularity2 : "Posts are not sorted by popularity";
        }
    }

    public static void main(String[] args)
    {
        SortTests tests = new SortTests();
        tests.testSort(1000);
        System.out.println("All sort tests passed.");
    }
}