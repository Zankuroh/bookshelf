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
    public Profil()
    {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.profil, container, false);
    }
    //Todo: Récupérer les info user dans la BDD
    //Todo: Rechercher un user ?
    //Todo: Modifier les infos user (nouvelle vue ou popup)
}
