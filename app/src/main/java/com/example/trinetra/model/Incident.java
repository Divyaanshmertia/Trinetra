package com.example.trinetra.model;

public class Incident {
    private String key;
    private String userId;
    private String title;
    private String description;
    private String mediaUrl;
    private String type;
    private String date;
    private double latitude;
    private double longitude;
    private double upvote;
    private double downvote;

    public Incident() {
        // Default constructor required for calls to DataSnapshot.getValue(Incident.class)
    }

    public Incident(String key, String userId, String title, String description, String mediaUrl, String type, String date, double latitude, double longitude, double upvote, double downvote) {
        this.key = key;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.mediaUrl = mediaUrl;
        this.type = type;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.upvote = upvote;
        this.downvote = downvote;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getUpvote() {
        return upvote;
    }

    public void setUpvote(double upvote) {
        this.upvote = upvote;
    }
    public String getMediaType() {
        if (mediaUrl.endsWith(".mp4")) {
            return "video";
        } else {
            return "image";
        }
    }

    public double getDownvote() {
        return downvote;
    }

    public void setDownvote(double downvote) {
        this.downvote = downvote;
    }
}
