package com.eip.bookshelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
        final customAdapterAuthor adapterAuthor = new customAdapterAuthor(_v.getContext(), _modelListAuthor);
        _lvAuthor.setAdapter(adapterAuthor);
        _lvAuthor.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String auteur = ((TextView) view.findViewById(R.id.TVAutor)).getText().toString();
                AlertDialog.Builder adb = new AlertDialog.Builder(_v.getContext());
                adb.setTitle("Suppression");
                adb.setMessage("Voulez-vous supprimer l'auteur " + auteur + " ?");
                final int positionToRemove = position;
                adb.setNegativeButton("Annuler", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _modelListAuthor.remove(positionToRemove);
                        adapterAuthor.notifyDataSetChanged();
                        // Ici on supprimer l'auteur avec l'API
                    }});
                adb.show();
            }
        });
        _modelListAuthor.add("J. K. Rowling");
        _modelListAuthor.add("R. R. Martin");
    }
}
