
package com.eip.utilities.model.Notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("author_id")
    @Expose
    private String authorId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("isbn")
    @Expose
    private String isbn;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("expiration")
    @Expose
    private String expiration;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content")
    @Expose
    private Object content;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private Object lastName;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("current_votes")
    @Expose
    private String currentVotes;
    @SerializedName("added_by")
    @Expose
    private String addedBy;
    @SerializedName("author_novels_id")
    @Expose
    private String authorNovelsId;
    @SerializedName("authors_id")
    @Expose
    private String authorsId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Object getLastName() {
        return lastName;
    }

    public void setLastName(Object lastName) {
        this.lastName = lastName;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCurrentVotes() {
        return currentVotes;
    }

    public void setCurrentVotes(String currentVotes) {
        this.currentVotes = currentVotes;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getAuthorNovelsId() {
        return authorNovelsId;
    }

    public void setAuthorNovelsId(String authorNovelsId) {
        this.authorNovelsId = authorNovelsId;
    }

    public String getAuthorsId() {
        return authorsId;
    }

    public void setAuthorsId(String authorsId) {
        this.authorsId = authorsId;
    }


}
