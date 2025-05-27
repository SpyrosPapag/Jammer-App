DROP DATABASE IF EXISTS jammer;
CREATE DATABASE jammer;
USE jammer;

CREATE TABLE user(
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(20) NOT NULL,
    password VARCHAR(20) NOT NULL,
    push_notifications BOOL NOT NULL DEFAULT 0,
    listing_notifications BOOL NOT NULL DEFAULT 0,
    event_notifications BOOL NOT NULL DEFAULT 0,
    chat_notifications BOOL NOT NULL DEFAULT 0,
    avatar_url TEXT,
    bio VARCHAR(255),
    verified BOOL,
    preferences_json TEXT,
    PRIMARY KEY(user_id)
);

CREATE TABLE followers(
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    PRIMARY KEY(follower_id, following_id),
    CONSTRAINT followers_follower_key FOREIGN KEY(follower_id)
    REFERENCES user(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT followers_following_key FOREIGN KEY(following_id)
    REFERENCES user(user_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE post(
    post_id BIGINT NOT NULL AUTO_INCREMENT,
    poster_id BIGINT,
    pictures_url_json TEXT,
    description TEXT NOT NULL,
    type ENUM('event','venue','lesson', 'other') NOT NULL,
    date DATE DEFAULT (CURRENT_DATE),
    location TEXT,
    popularity BIGINT DEFAULT 0,
    PRIMARY KEY(post_id),
    CONSTRAINT post_user_key FOREIGN KEY(poster_id)
    REFERENCES user(user_id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE review(
    review_id BIGINT NOT NULL AUTO_INCREMENT,
    rated_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    rating SMALLINT,
    PRIMARY KEY(review_id),
    CONSTRAINT review_post_key FOREIGN KEY(rated_id)
    REFERENCES post(post_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE interested(
    interested_user_id BIGINT NOT NULL,
    target_post_id BIGINT NOT NULL,
    PRIMARY KEY(interested_user_id, target_post_id),
    CONSTRAINT interested_user_key FOREIGN KEY(interested_user_id)
    REFERENCES user(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT interested_post_key FOREIGN KEY(target_post_id)
    REFERENCES post(post_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE chat(
    chat_id BIGINT NOT NULL AUTO_INCREMENT,
    member1 BIGINT,
    member2 BIGINT,
    PRIMARY KEY(chat_id),
    CONSTRAINT chat_member1_key FOREIGN KEY(member1)
    REFERENCES user(user_id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chat_member2_key FOREIGN KEY(member2)
    REFERENCES user(user_id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE message(
    message_id BIGINT NOT NULL AUTO_INCREMENT,
    sender_id BIGINT,
    target_chat_id BIGINT,
    content VARCHAR(1023) NOT NULL,
    send_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(message_id),
    CONSTRAINT message_sender_key FOREIGN KEY(sender_id)
    REFERENCES user(user_id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT message_chat_key FOREIGN KEY(target_chat_id)
    REFERENCES chat(chat_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE chat_request(
    chat_request_id BIGINT NOT NULL AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    source_id BIGINT,
    status VARCHAR(8) DEFAULT 'pending',
    PRIMARY KEY(chat_request_id),
    CONSTRAINT chat_request_sender_key FOREIGN KEY(sender_id)
    REFERENCES user(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chat_request_recipient_key FOREIGN KEY(recipient_id)
    REFERENCES user(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chat_request_source_key FOREIGN KEY(source_id)
    REFERENCES post(post_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE notification(
    notification_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    source_id BIGINT NOT NULL,
    source_type TINYINT NOT NULL,
    PRIMARY KEY(notification_id),
    CONSTRAINT notification_user_key FOREIGN KEY(user_id)
    REFERENCES user(user_id) ON UPDATE CASCADE ON DELETE CASCADE
);
