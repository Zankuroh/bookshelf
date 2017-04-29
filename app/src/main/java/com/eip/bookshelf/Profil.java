package com.eip.bookshelf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Maxime on 28/04/2017.
 */

public class Profil extends Fragment
{
    private View _v;

    public Profil()
    {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _v = inflater.inflate(R.layout.profil, container, false);

        return _v;
    }
    //Todo: Récupérer les info user dans la BDD
    //Todo: Rechercher un user ?
    //Todo: Modifier les infos user (nouvelle vue ou popup)
}
