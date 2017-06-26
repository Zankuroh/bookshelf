package com.eip.bookshelf;

/**
 * Created by Maxime on 17/02/2016.
 */

class BiblioAdapter
{
    private String _name;
    private String _id;
    private String _isbn;

    BiblioAdapter(String name, String id, String isbn) {
        this._name = name;
        this._id = id;
        this._isbn = isbn;
    }

    String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    String get_id() {
        return _id;
    }

    public void set_id(String id) {
        this._id = id;
    }

    public String get_isbn() {
        return _isbn;
    }

    public void set_isbn(String _isbn) {
        this._isbn = _isbn;
    }
}
