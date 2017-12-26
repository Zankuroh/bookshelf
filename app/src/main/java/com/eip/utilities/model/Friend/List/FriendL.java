
package com.eip.utilities.model.Friend.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FriendL {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("social_auth")
    @Expose
    private String socialAuth;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("friend_id")
    @Expose
    private String friendId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("common_nbr_friends")
    @Expose
    private String commonNbrFriends;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSocialAuth() {
        return socialAuth;
    }

    public void setSocialAuth(String socialAuth) {
        this.socialAuth = socialAuth;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCommonNbrFriends() {
        return commonNbrFriends;
    }

    public void setCommonNbrFriends(String commonNbrFriends) {
        this.commonNbrFriends = commonNbrFriends;
    }

}
