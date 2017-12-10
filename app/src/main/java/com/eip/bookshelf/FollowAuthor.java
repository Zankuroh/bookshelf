package com.eip.bookshelf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Maxime on 25/04/2017.
 */

public class FollowAuthor extends Fragment
{
    private View _v;
    private ListView _lvAuthor;
    private ArrayList<String> _modelListAuthor = new ArrayList<>();

    public FollowAuthor()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _v = inflater.inflate(R.layout.follow_author, container, false);
        _lvAuthor = (ListView) _v.findViewById(R.id.LVAuthor);

        setAdapter();
        return _v;
    }

    private void setAdapter()
    {
        customAdapterAuthor adapterAuthor = new customAdapterAuthor(_v.getContext(), _modelListAuthor);
        _lvAuthor.setAdapter(adapterAuthor);
        _modelListAuthor.add("J. K. Rowling");
        _modelListAuthor.add("R. R. Martin");
    }
}
