package com.eip.bookshelf;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Maxime on 17/02/2016.
 */

class customAdapterAmis extends BaseAdapter
{
    private Context _c;
    private ArrayList<AmisAdapter> _als;

    customAdapterAmis(View view, ArrayList<AmisAdapter> modelList)
    {
        this._c = view.getContext();
        this._als = modelList;
    }

    @Override
    public int getCount() {
        return this._als.size();
    }

    @Override
    public Object getItem(int position) {
        return this._als.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View v;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) _c.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = mInflater.inflate(R.layout.amis_adapter, parent, false);
        } else {
            v = convertView;
        }

        AmisAdapter iadapt = _als.get(position);
        TextView tv = v.findViewById(R.id.TVAffName);
        TextView tvh = v.findViewById(R.id.TVIDAmis);
        tv.setText(iadapt.get_name());
        tvh.setText(iadapt.get_id());
        return v;
    }
}