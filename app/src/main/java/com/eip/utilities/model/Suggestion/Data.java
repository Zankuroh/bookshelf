
package com.eip.utilities.model.Suggestion;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("suggestions")
    @Expose
    private List<String> suggestions = null;
    @SerializedName("friends_suggestions")
    @Expose
    private List<String> friendsSuggestions = null;
    @SerializedName("friends_latest_books")
    @Expose
    private List<String> friendsLatestBooks = null;

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
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
