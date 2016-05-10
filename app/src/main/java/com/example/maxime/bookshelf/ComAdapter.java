package com.example.maxime.bookshelf;

/**
 * Created by Maxime on 23/02/2016.
 */

public class ComAdapter
{
    private String _who;
    private String _date;
    private String _content;

    ComAdapter(String w, String d, String c)
    {
        _who = w;
        _date = d;
        _content = c;
    }

    public String get_who() {
        return _who;
    }

    public void set_who(String _who) {
        this._who = _who;
    }

    public String get_content() {
        return _content;
    }

    public void set_content(String _content) {
        this._content = _content;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }
}
