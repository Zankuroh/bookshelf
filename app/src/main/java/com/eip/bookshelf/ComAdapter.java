package com.eip.bookshelf;

/**
 * Created by Maxime on 23/02/2016.
 */

class ComAdapter
{
    private String _who;
    private String _date;
    private String _content;
    private String _rate;

    ComAdapter(String w, String d, String c, String r)
    {
        _who = w;
        _date = d;
        _content = c;
        _rate = r;
    }

    String get_who() {
        return _who;
    }

    public void set_who(String _who) {
        this._who = _who;
    }

    String get_content() {
        return _content;
    }

    public void set_content(String _content) {
        this._content = _content;
    }

    String get_date()
    {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    String get_rate() { return _rate; }

    public void set_rate(String _rate) { this._rate = _rate; }
}
