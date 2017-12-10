package com.eip.bookshelf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Maxime on 22/02/2016.
 */
class customAdapterAuthor extends BaseAdapter {
    private Context _c;
    private ArrayList<String> _als;
    private boolean _acceptCall = true;

    customAdapterAuthor(Context context, ArrayList<String> modelList)
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
            v = mInflater.inflate(R.layout.author_adapter, parent, false);
        } else {
            v = convertView;
        }

        final String iadapt = _als.get(position);
        final TextView tv = (TextView) v.findViewById(R.id.TVAutor);
        final Switch sw = (Switch) v.findViewById(R.id.SFollow);
        final customAdapterAuthor scope = this;
        tv.setText(iadapt);
        v.findViewById(R.id.IVDelete).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
                adb.setTitle("Suppression");
                adb.setMessage("Voulez-vous supprimer l'auteur " + iadapt + " ?");
                final int positionToRemove = position;
                adb.setNegativeButton("Annuler", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _als.remove(positionToRemove);
                        scope.notifyDataSetChanged();
                        // Ici on supprime l'auteur avec l'API
                    }});
                adb.show();
            }
        });
        // set switch on or off with API
        // sw.setChecked(true); // false
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (_acceptCall) {
                    String msg = "Voulez-vous arrêter de suivre l'auteur " + iadapt + " ?\nVous ne serez plus averti par notification des dernières sorties.";
                    if (isChecked) {
                        msg = "Voulez-vous suivre l'auteur " + iadapt + " ?\nVous serez averti par notification des dernières sorties.";
                    }
                    AlertDialog.Builder adb = new AlertDialog.Builder(buttonView.getContext());
                    adb.setTitle("Suivre");
                    adb.setMessage(msg);
                    adb.setNegativeButton("Annuler", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            _acceptCall = false;
                            sw.setChecked(!isChecked);
                        }
                    });
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (isChecked) {
                                // Ici on suit l'auteur avec l'API
                            } else {
                                // Ici on arrête de suivre l'auteur avec l'API
                            }
                        }
                    });
                    adb.show();
                } else {
                    _acceptCall = true;
                }
            }
        });
        return v;
    }
}
