package com.example.maxime.bookshelf;

/**
 * Created by Maxime on 17/02/2016.
 */

public class BiblioAdapter {
    private String _name;
    private int _id;

    public BiblioAdapter(String name, int id) {
        this._name = name;
        this._id = id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int id) {
        this._id = id;
    }
}
