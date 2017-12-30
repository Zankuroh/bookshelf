package com.eip.bookshelf;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nicolas on 30/12/2017.
 */

class customAdapterNotification extends BaseAdapter
{
    private Context _c;
    private ArrayList<NotificationAdapter> _als;

    customAdapterNotification(View view, ArrayList<NotificationAdapter> modelList)
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
            v = mInflater.inflate(R.layout.new_notif_adapter, parent, false);
        } else {
            v = convertView;
        }

        NotificationAdapter iadapt = _als.get(position);
        TextView tva = v.findViewById(R.id.TVAffAuthorName);
        TextView tvt = v.findViewById(R.id.TVBookTitre);
        tva.setText(iadapt.get_author());
        tvt.setText(iadapt.get_title());
        return v;
    }
}