package com.eip.bookshelf;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.AuthorSubscription.SubAuthor;
import com.eip.utilities.model.AuthorSubscription.SubList;

import org.json.JSONObject;

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

public class FollowAuthor extends Fragment
{
    private ListView _lvAuthor;
    private ArrayList<Pair<String, String>> _modelListAuthor = new ArrayList<>();
    private customAdapterAuthor _adapterAuthor;

    public FollowAuthor()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.follow_author, container, false);
        _lvAuthor = v.findViewById(R.id.LVAuthor);

        setAdapter(v);
        getSubAuthor();
        return v;
    }

    private void setAdapter(View v)
    {
        if (v != null) {
            _adapterAuthor = new customAdapterAuthor(v.getContext(), _modelListAuthor);
            _lvAuthor.setAdapter(_adapterAuthor);
            _modelListAuthor.clear();
        }
    }

    private void getSubAuthor()
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<SubList> call = bookshelfApi.getAuthorsSubscription(MainActivity.token);
        call.enqueue(new Callback<SubList>() {
            @Override
            public void onResponse(Call<SubList> call, Response<SubList> response) {
                _modelListAuthor.clear();
                if (response.isSuccessful()) {
                    SubList rep = response.body();
                    List<SubAuthor> authors = rep.getData();
                    if (!authors.isEmpty()) {
                        ListIterator<SubAuthor> it = authors.listIterator();
                        while (it.hasNext()) {
                            SubAuthor author = it.next();
                            String name = author.getFirstName();
                            if (author.getLastName() != null) {
                                name += " " + author.getLastName();
                            }
                            _modelListAuthor.add(new Pair<>(name, author.getAuthorId()));
                        }
                        if (_adapterAuthor != null) {
                            _adapterAuthor.notifyDataSetChanged();
                        }
                    }
                    else {
                        if (getView() != null) {
                            Snackbar snackbar = Snackbar.make(getView(), "Vous n'avez pas d'auteur suivi", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                } else {
                    try {
                        if (getView() != null) {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Snackbar snackbar = Snackbar.make(getView(), "Erreur : " + jObjError.getString("title"), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    } catch (Exception e) {
                        if (getView() != null) {
                            Snackbar snackbar = Snackbar.make(getView(), "Une erreur est survenue", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<SubList> call, Throwable t)
            {
                if (getView() != null) {
                    Snackbar snackbar = Snackbar.make(getView(), "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                t.printStackTrace();
            }
        });
    }
}
