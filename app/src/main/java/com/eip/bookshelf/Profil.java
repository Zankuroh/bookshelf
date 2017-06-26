package com.eip.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.Profile.Profile;
import com.eip.utilities.model.Profile.Profile_;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private TextView _pseudo;
    private TextView _email;
    private TextView _birth;
    private TextView _genre;
    private TextView _book;
    private TextView _create;
    private TextView _last;
    public static Profile_ prof;

    public Profil()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.profil, container, false);
        _rl = (RelativeLayout)v.findViewById(R.id.RLProfil);
        v.findViewById(R.id.BEditProfil).setOnClickListener(this);
        _pseudo = (TextView)v.findViewById(R.id.TVPseudo);
        _email = (TextView)v.findViewById(R.id.TVEmail);
        _birth = (TextView)v.findViewById(R.id.TVBirth);
        _genre = (TextView)v.findViewById(R.id.TVGenre);
        _book = (TextView)v.findViewById(R.id.TVBook);
        _create = (TextView)v.findViewById(R.id.TVCrea);
        _last = (TextView)v.findViewById(R.id.TVLast);
        getInfo();
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
                    prof = response.body().getData().getProfile();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); //
                    Date date = new Date(); //
                    _pseudo.setText(prof.getName());
                    _email.setText(prof.getEmail());
                    _last.setText(dateFormat.format(date)); //
                    //Todo: set les autres champs
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
