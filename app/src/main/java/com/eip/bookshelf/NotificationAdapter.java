package com.eip.bookshelf;

/**
 * Created by Nicolas on 30/12/2017.
 */

class NotificationAdapter
{
    private String _authorName;
    private String _bookTitle;

    NotificationAdapter(String authorName, String bookTitle) {
        this._authorName = authorName;
        this._bookTitle = bookTitle;
    }

    String get_author() {
        return _authorName;
    }

    public void set_author(String author) {
        this._authorName = author;
    }

    String get_title() {
        return _bookTitle;
    }

    public void set_title(String title) {
        this._bookTitle = title;
    }

}
