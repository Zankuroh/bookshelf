package com.eip.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Maxime on 28/04/2017.
 */

public class Profil extends Fragment implements View.OnClickListener
{
    public Profil()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.profil, container, false);

        v.findViewById(R.id.BEditProfil).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.BEditProfil:
                startActivity(new Intent(getActivity(), EditProfil.class));
                break;
            default:
                break;
        }
    }

//    private void onClickEditProfil()
//    {
//        startActivity(new Intent(getActivity(), EditProfil.class));
//    }

    //Todo: Récupérer les info user dans la BDD
    //Todo: Rechercher un user ?
}
