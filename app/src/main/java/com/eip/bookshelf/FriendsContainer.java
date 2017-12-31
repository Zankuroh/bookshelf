package com.eip.bookshelf;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.Friend.Friend;
import com.eip.utilities.model.Friend.List.FriendL;
import com.eip.utilities.model.Friend.List.FriendList;
import com.eip.utilities.model.Friend.Search.FriendSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maxime on 25/04/2017.
 */

public class FriendsContainer extends Fragment implements View.OnClickListener
{
    private ArrayList<AmisAdapter> _modelListFriend = new ArrayList<>();
    private customAdapterAmis _adapterFriend;

    public FriendsContainer()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.amis_container, container, false);
        v.findViewById(R.id.okSearchFriend).setOnClickListener(this);

        setAdapter(v);
        getAllFriends();
        return v;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.okSearchFriend:
                searchFriend();
                break;
            default:
                break;
        }
    }

    private void searchFriend()
    {
        MainActivity.hideSoftKeyboard(getActivity());
        String email = "";
        if (getView() != null) {
            TextView search = getView().findViewById(R.id.searchFieldFriend);
            email = search.getText().toString().trim();
        }
        if (!SignUp.verifyEmail(email)) {
            if (getView() != null) {
                Snackbar snackbar = Snackbar.make(getView(), "Veuillez entrer une adresse mail valide.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } else {
            BookshelfApi bookshelfApi = new Retrofit.Builder()
                    .baseUrl(BookshelfApi.APIPath)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(BookshelfApi.class);
            Call<FriendSearch> call = bookshelfApi.searchFriend(MainActivity.token, email);
            call.enqueue(new Callback<FriendSearch>() {
                @Override
                public void onResponse(Call<FriendSearch> call, Response<FriendSearch> response) {
                    if (response.isSuccessful()) {
                        List<Friend> Flist = response.body().getData();
                        if (!Flist.isEmpty()) {
                            Friend ami = Flist.get(0);
                            Bundle b = new Bundle();
                            b.putString("idFriend", ami.getId());
                            b.putString("fname", ami.getName());
                            b.putString("femail", ami.getEmail());
                            b.putBoolean("isFriend", false);
                            Profil profilFrag = new Profil();
                            profilFrag.setArguments(b);
                            if (getFragmentManager() != null) {
                                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, profilFrag);
                                fragmentTransaction.commit();
                            }
                        }
                        else {
                            if (getView() != null) {
                                Snackbar snackbar = Snackbar.make(getView(), "Amis non trouvé", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    } else if (getView() != null) {
                        Snackbar snackbar = Snackbar.make(getView(), "Une erreur est survenue", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    MainActivity.stopLoading();
                }

                @Override
                public void onFailure(Call<FriendSearch> call, Throwable t)
                {
                    if (getView() != null) {
                        Snackbar snackbar = Snackbar.make(getView(), "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    t.printStackTrace();
                    MainActivity.stopLoading();
                }
            });
        }
    }

    private void getAllFriends() {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<FriendList> call = bookshelfApi.getFriends(MainActivity.token);
        call.enqueue(new Callback<FriendList>() {
            @Override
            public void onResponse(Call<FriendList> call, Response<FriendList> response) {
                if (response.isSuccessful()) {
                    List<FriendL> Flist = response.body().getData();

                    if (!Flist.isEmpty()) {
                        ListIterator<FriendL> it = Flist.listIterator();
                        while (it.hasNext()) {
                            FriendL ami = it.next();
                            _modelListFriend.add(new AmisAdapter(ami.getName(), ami.getFriendId(), ami.getEmail()));
                        }
                        if (_adapterFriend != null) {
                            _adapterFriend.notifyDataSetChanged();
                        }
                    }
                    else {
                        if (getView() != null) {
                            Snackbar snackbar = Snackbar.make(getView(), "Vous n'avez pas d'amis", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                } else if (getView() != null) {
                    Snackbar snackbar = Snackbar.make(getView(), "Une erreur est survenue", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                MainActivity.stopLoading();
            }

            @Override
            public void onFailure(Call<FriendList> call, Throwable t)
            {
                if (getView() != null) {
                    Snackbar snackbar = Snackbar.make(getView(), "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }

    private void setAdapter(View v)
    {
        if (v != null) {
            GridView gvFriend = v.findViewById(R.id.GVFriend);
            _adapterFriend = new customAdapterAmis(v, _modelListFriend);
            gvFriend.setAdapter(_adapterFriend);
            _modelListFriend.clear();

            gvFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AmisAdapter ami = (AmisAdapter) _adapterFriend.getItem(position);
                    Bundle b = new Bundle();
                    b.putString("idFriend", ami.get_id());
                    b.putBoolean("isFriend", true);
                    b.putString("fname", ami.get_name());
                    b.putString("femail", ami.get_email());
                    Profil profilFrag = new Profil();
                    profilFrag.setArguments(b);
                    if (getFragmentManager() != null) {
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, profilFrag);
                        fragmentTransaction.commit();
                    }
                }
            });
        }
    }
}
