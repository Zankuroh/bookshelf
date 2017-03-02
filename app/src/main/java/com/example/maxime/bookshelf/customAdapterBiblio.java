package com.example.maxime.bookshelf;

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

class customAdapterBiblio extends BaseAdapter
{
    private Context _c;
    private ArrayList<BiblioAdapter> _als;

    public customAdapterBiblio(Context context, ArrayList<BiblioAdapter> modelList)
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
        /*convertView = null;*/

        if (convertView == null)
        {
            LayoutInflater mInflater = (LayoutInflater) _c.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.biblio_adapter, parent, false);

            BiblioAdapter iadapt = _als.get(position);
            TextView tv = (TextView) convertView.findViewById(R.id.TVAff);
            ImageView iv = (ImageView) convertView.findViewById(R.id.IVAff);
            tv.setText(iadapt.get_name());
            iv.setImageBitmap(OptimizeBitmap.decodeSampledBitmapFromResource(convertView.getResources(), iadapt.get_id(), 110, 110));
        }
        return convertView;
    }
}