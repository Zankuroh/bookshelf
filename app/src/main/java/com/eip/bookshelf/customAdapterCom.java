package com.eip.bookshelf;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        View v;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) _c.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = mInflater.inflate(R.layout.com_adapter, parent, false);
        } else {
            v = convertView;
        }

        ComAdapter iadapt = _als.get(position);

        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fromFormat.setLenient(false);
        DateFormat toFormat = new SimpleDateFormat("dd/MM/yyyy");
        toFormat.setLenient(false);
        String formated_date = "";
        try {
            Date date = fromFormat.parse(iadapt.get_date());
            formated_date = toFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView tv1 = v.findViewById(R.id.TVComWho);
        TextView tv2 = v.findViewById(R.id.TVComDate);
        TextView tv3 = v.findViewById(R.id.TVComContent);
        RatingBar rb = v.findViewById(R.id.ratingBarDisplay);
        tv1.setText(iadapt.get_who());
        tv2.setText(formated_date);
        tv3.setText(iadapt.get_content());
        rb.setRating(Float.parseFloat(iadapt.get_rate()));

        return v;
    }

}
