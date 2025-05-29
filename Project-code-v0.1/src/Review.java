public class Review {
    private int reviewId;
    private int ratedId;
    private String content;
    private int rating;

    public Review(int reviewId, int ratedId, String content, int rating) {
        this.reviewId = reviewId;
        this.ratedId = ratedId;
        this.content = content;
        this.rating = rating;
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getRatedId() {
        return ratedId;
    }

    public String getContent() {
        return content;
    }

    public int getRating() {
        return rating;
    }
}

