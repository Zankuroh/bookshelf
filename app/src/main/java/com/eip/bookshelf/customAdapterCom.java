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
 * Created by Maxime on 23/02/2016.
 */

class customAdapterCom extends BaseAdapter
{
    private Context _c;
    private ArrayList<ComAdapter> _als;

    customAdapterCom(Context context, ArrayList<ComAdapter> modelList)
    {
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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) _c.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.com_adapter, parent, false);

            ComAdapter iadapt = _als.get(position);
            TextView tv1 = (TextView) convertView.findViewById(R.id.TVComWho);
            TextView tv2 = (TextView) convertView.findViewById(R.id.TVComDate);
            TextView tv3 = (TextView) convertView.findViewById(R.id.TVComContent);
            tv1.setText(iadapt.get_who());
            tv2.setText(iadapt.get_date());
            tv3.setText(iadapt.get_content());
        }
        return convertView;
    }

}
