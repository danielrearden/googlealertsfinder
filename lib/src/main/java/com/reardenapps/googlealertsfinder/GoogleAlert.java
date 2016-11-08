package com.reardenapps.googlealertsfinder;

public class GoogleAlert {
    private String userID;
    private String feedID;
    private String query;

    public GoogleAlert(String query, String userID, String feedID) {
        this.feedID = feedID;
        this.query = query;
        this.userID = userID;
    }

    public String getFeedID() {
        return feedID;
    }

    public void setFeedID(String feedID) {
        this.feedID = feedID;
    }

    public String getFeedUrl() {
        return "https://www.google.com/alerts/feeds/"+userID+"/"+feedID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
