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
 * Created by Maxime on 22/02/2016.
 */
class customAdapterAutor extends BaseAdapter {
    private Context _c;
    private ArrayList<String> _als;

    customAdapterAutor(Context context, ArrayList<String> modelList) {
        this._c = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        /*convertView = null;*/

        if (convertView == null)
        {
            LayoutInflater mInflater = (LayoutInflater) _c.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.autor_adapter, parent, false);

            String iadapt = _als.get(position);
            TextView tv = (TextView) convertView.findViewById(R.id.TVAutor);
            tv.setText(iadapt);
        }
        return convertView;
    }
}
