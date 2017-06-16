package com.eip.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.Profile.Profile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maxime on 28/04/2017.
 */

public class Profil extends Fragment implements View.OnClickListener
{
    private RelativeLayout _rl;

    public Profil()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.profil, container, false);
        _rl = (RelativeLayout)v.findViewById(R.id.RLProfil);
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

    public void getInfo(){
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<Profile> call = bookshelfApi.getProfile(MainActivity.token);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful()) {
                    Profile profile = response.body();
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    //Todo: Récupérer les info user dans la BDD
    //Todo: Rechercher un user ?
}
