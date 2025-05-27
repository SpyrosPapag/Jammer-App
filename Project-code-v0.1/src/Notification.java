public class Notification {
    private long notification_id;
    private long source_id;
    private int source_type;

    public Notification(long notification_id, long source_id, int source_type) {
        this.notification_id = notification_id;
        this.source_id = source_id;
        this.source_type = source_type;
    }

    public static void notify(Post post){
        DBManager notifManager = new DBManager();
        //notifManager.distributePostNotifications(poster_id);
    }

    //public static void notify(Chat chat){
    //TODO AFTER CHAT IMPLEMENTATION
    //}

    public void push(){

    }
}
