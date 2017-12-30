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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.Friend.Modif.FriendModif;
import com.eip.utilities.model.Friend.WishList.WishList;
import com.eip.utilities.model.Profile.Profile;
import com.eip.utilities.model.Profile.Profile_;
import com.eip.utilities.model.SimpleResponse.SimpleResponse;

import org.json.JSONObject;

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
        _pseudo = _v.findViewById(R.id.TVPseudo);
        _email = _v.findViewById(R.id.TVEmail);

        Bundle b = getArguments();
        if (b != null) {
            setHasOptionsMenu(true);
            prepareFriend();
            _FriendId = b.getString("idFriend");
            _isFriend = b.getBoolean("isFriend");
            _pseudo.setText(b.getString("fname"));
            _email.setText(b.getString("femail"));
        } else {
            prepareSelf();
            _FriendId = null;
            _isFriend = false;
        }



        _rl = _v.findViewById(R.id.RLProfil);

        _birth = _v.findViewById(R.id.TVBirth);
        _genre = _v.findViewById(R.id.TVGenre);
        _book = _v.findViewById(R.id.TVBook);
        _create = _v.findViewById(R.id.TVCrea);
        _last = _v.findViewById(R.id.TVLast);
        return _v;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.BEditProfil:
                startActivityForResult(new Intent(getActivity(), EditProfil.class), 4242);
                break;
            case R.id.BFShelf:
                getFriendWishList();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.main, menu);
        _menu = menu;

        hideButtons();
        if (_isFriend) {
            _menu.findItem(R.id.IRemoveFriend).setVisible(true);
        } else if (_FriendId != null) {
            _menu.findItem(R.id.IAddFriend).setVisible(true);
        }
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
        _menu.findItem(R.id.ICategories).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.IAddFriend:
                addFriend();
                _menu.findItem(R.id.IAddFriend).setVisible(false);
                _menu.findItem(R.id.IRemoveFriend).setVisible(true);
                break;
            case R.id.IRemoveFriend:
                deletefriend();
                _menu.findItem(R.id.IAddFriend).setVisible(true);
                _menu.findItem(R.id.IRemoveFriend).setVisible(false);
                break;
        }

        return super.onOptionsItemSelected(item);
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

    private void addFriend() {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<FriendModif> call = bookshelfApi.addFriend(MainActivity.token,  Integer.parseInt(_FriendId));
        call.enqueue(new Callback<FriendModif>() {
            @Override
            public void onResponse(Call<FriendModif> call, Response<FriendModif> response) {
                if (response.isSuccessful()) {
                    FriendModif fm = response.body();
                    if (fm.getTitle() == null) {
                        Snackbar snackbar = Snackbar.make(_rl, "Ami ajouté", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        Snackbar snackbar = Snackbar.make(_rl, fm.getTitle(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + jObjError.getString("title"), Snackbar.LENGTH_LONG);
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
            public void onFailure(Call<FriendModif> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }

    private void deletefriend() {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<SimpleResponse> call = bookshelfApi.delFriend(MainActivity.token,  Integer.parseInt(_FriendId));
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful()) {
                    SimpleResponse valid = response.body();
                    if (valid.getData().getSuccess().equals("true")) {
                        Snackbar snackbar = Snackbar.make(_rl, "Ami supprimé", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        Snackbar snackbar = Snackbar.make(_rl, valid.getTitle(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + jObjError.getString("title"), Snackbar.LENGTH_LONG);
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
            public void onFailure(Call<SimpleResponse> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }

    private void getFriendWishList() {
        MainActivity.defineNameToolBar("WishList de " + _pseudo);
        Bundle b = new Bundle();
        b.putString("idFriend", _FriendId);
        b.putSerializable("type", MainActivity.shelfType.WISHSHELF);
        ShelfContainer ShelfFrag = new ShelfContainer();
        ShelfFrag.setArguments(b);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, ShelfFrag, "SHELF");
        fragmentTransaction.commit();
    }
}
