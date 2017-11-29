package com.eip.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.Profile.Profile;
import com.eip.utilities.model.Profile.Profile_;

import java.io.Console;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Maxime on 28/04/2017.
 */

public class Profil extends Fragment implements View.OnClickListener
{
    private String _FriendId;
    private boolean _isFriend;
    private RelativeLayout _rl;
    private TextView _pseudo;
    private TextView _email;
    private TextView _birth;
    private TextView _genre;
    private TextView _book;
    private TextView _create;
    private TextView _last;
    public static Profile_ prof;
    private View _v;
    private Menu _menu;

    public Profil()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _v = inflater.inflate(R.layout.profil, container, false);

        Bundle b = getArguments();
        if (b != null) {
            prepareFriend();
            _FriendId = b.getString("idFriend");
            _isFriend = b.getBoolean("isFriend");
        } else {
            prepareSelf();
            _FriendId = null;
            _isFriend = false;
        }

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        onCreateOptionsMenu(toolbar.getMenu());
        _rl = (RelativeLayout)_v.findViewById(R.id.RLProfil);
        _pseudo = (TextView)_v.findViewById(R.id.TVPseudo);
        _email = (TextView)_v.findViewById(R.id.TVEmail);
        _birth = (TextView)_v.findViewById(R.id.TVBirth);
        _genre = (TextView)_v.findViewById(R.id.TVGenre);
        _book = (TextView)_v.findViewById(R.id.TVBook);
        _create = (TextView)_v.findViewById(R.id.TVCrea);
        _last = (TextView)_v.findViewById(R.id.TVLast);
        return _v;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.BEditProfil:
                startActivityForResult(new Intent(getActivity(), EditProfil.class), 4242);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 4242 && resultCode == RESULT_OK) { // EditProfil for delete
            MainActivity.token = null;
            MainActivity.MenuItemCo.setTitle("Connexion");
            MainActivity.MenuItemBiblio.setChecked(true);
            MainActivity.defineNameToolBar("Bibliothèque");
            MainActivity.accessDenied(getActivity());
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        _menu = menu;

        hideButtons();
        if (_isFriend) {
            _menu.findItem(R.id.IRemoveBookWish).setVisible(true);
        } else if (_FriendId != null) {
            _menu.findItem(R.id.IRemoveFriend).setVisible(true);
        }
        return true;
    }

    private void hideButtons()
    {
        _menu.findItem(R.id.IRemoveBookBiblio).setVisible(false);
        _menu.findItem(R.id.IAddBookBiblio).setVisible(false);
        _menu.findItem(R.id.IAddBookWish).setVisible(false);
        _menu.findItem(R.id.IRemoveBookWish).setVisible(false);
        _menu.findItem(R.id.IRemoveBookWish).setVisible(false);
        _menu.findItem(R.id.IAddFriend).setVisible(false);
        _menu.findItem(R.id.IRemoveFriend).setVisible(false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (_FriendId == null) {
            getInfo();
        } else {
            getFriendInfo();
        }
    }

    private void prepareSelf() {
        _v.findViewById(R.id.BEditProfil).setOnClickListener(this);
        _v.findViewById(R.id.BEditProfil).setVisibility(View.VISIBLE);
    }

    private void prepareFriend() {
        _v.findViewById(R.id.BFShelf).setOnClickListener(this);
        _v.findViewById(R.id.BFShelf).setVisibility(View.VISIBLE);
    }

    public void getInfo(){
        MainActivity.startLoading();
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
                MainActivity.stopLoading();
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }

    public void getFriendInfo(){

    }
    //Todo: Récupérer les restants d'info du user
    //Todo: Rechercher un user ?
}
