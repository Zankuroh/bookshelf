
package com.eip.utilities.model.Suggestion;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("latest_suggestions")
    @Expose
    private List<String> latestSuggestions = null;
    @SerializedName("overall_suggestions")
    @Expose
    private List<String> overallSuggestions = null;
    @SerializedName("friends_suggestions")
    @Expose
    private List<String> friendsSuggestions = null;
    @SerializedName("friends_latest_books")
    @Expose
    private List<String> friendsLatestBooks = null;

    public List<String> getLatestSuggestions() {
        return latestSuggestions;
    }

    public void setLatestSuggestions(List<String> latestSuggestions) {
        this.latestSuggestions = latestSuggestions;
    }

    public List<String> getOverallSuggestions() {
        return overallSuggestions;
    }

    public void setOverallSuggestions(List<String> overallSuggestions) {
        this.overallSuggestions = overallSuggestions;
    }

    public List<String> getFriendsSuggestions() {
        return friendsSuggestions;
    }

    public void setFriendsSuggestions(List<String> friendsSuggestions) {
        this.friendsSuggestions = friendsSuggestions;
    }

    public List<String> getFriendsLatestBooks() {
        return friendsLatestBooks;
    }

    public void setFriendsLatestBooks(List<String> friendsLatestBooks) {
        this.friendsLatestBooks = friendsLatestBooks;
    }
}
