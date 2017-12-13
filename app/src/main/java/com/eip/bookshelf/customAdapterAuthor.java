package com.eip.bookshelf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.AuthorSubscription.SubscriptionValidator;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maxime on 22/02/2016.
 */
class customAdapterAuthor extends BaseAdapter {
    private Context _c;
    private RelativeLayout _rl;
    private ArrayList<Pair<String, String>> _als;

    customAdapterAuthor(Context context, ArrayList<Pair<String, String>> modelList)
    {
        this._c = context;
        this._als = modelList;
    }

    @Override
    public int getCount() {
        return this._als.size();
    }

    @Override
    public Object getItem(int position) {
        return this._als.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View v;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) _c.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = mInflater.inflate(R.layout.author_adapter, parent, false);
        } else {
            v = convertView;
        }

        _rl = (RelativeLayout)v.findViewById(R.id.RLAuthor);// TODO: 13/12/2017 recup un truc qui marche

        final Pair<String, String> iadapt = _als.get(position);
        final TextView tv = v.findViewById(R.id.TVAutor);
        final customAdapterAuthor scope = this;
        tv.setText(iadapt.first);
        v.findViewById(R.id.IVDelete).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(v.getContext());
                adb.setTitle("Désabonnement");
                adb.setMessage("Voulez-vous arrêter de suivre " + iadapt.first + " ?");
                adb.setNegativeButton("Annuler", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        _als.remove(position);
                        scope.notifyDataSetChanged();
                        // TODO: 11/12/2017 supprime l'auteur
                        deleteAuthorSub(iadapt.second);
                    }});
                adb.show();
            }
        });
        return v;
    }

    private void deleteAuthorSub(String id) {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<SubscriptionValidator> call = bookshelfApi.DelAuthorSubscription(MainActivity.token, id);
        call.enqueue(new Callback<SubscriptionValidator>() {
            @Override
            public void onResponse(Call<SubscriptionValidator> call, Response<SubscriptionValidator> response) {
                if (response.isSuccessful()) {
                    //SubscriptionValidator modif = response.body();
                    Snackbar snackbar = Snackbar.make(_rl, "Vous ne serez plus notifié lors de ses prochaines sortis", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Snackbar snackbar = Snackbar.make(_rl, "Erreur : " +  jObjError.getString("title"), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<SubscriptionValidator> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }
}
