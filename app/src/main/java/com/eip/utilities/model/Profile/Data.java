
package com.eip.utilities.model.Profile;

import java.util.List;

import com.eip.utilities.model.BooksLocal.Book;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("books")
    @Expose
    private List<Book> books = null;
    @SerializedName("profile")
    @Expose
    private Profile_ profile;

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public Profile_ getProfile() {
        return profile;
    }

    public void setProfile(Profile_ profile) {
        this.profile = profile;
    }

}
