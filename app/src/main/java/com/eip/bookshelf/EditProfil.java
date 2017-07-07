package com.eip.bookshelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.DelProfile.DelProfile;
import com.eip.utilities.model.ProfileModification.ProfileModification;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maxime on 28/04/2017.
 */

public class EditProfil extends AppCompatActivity
{
    private RelativeLayout _rl;

    public EditProfil()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profil);
        _rl = (RelativeLayout)findViewById(R.id.RLEditProfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        ((EditText)findViewById(R.id.ETPseudo)).setText(Profil.prof.getName());
        //Todo: set les autres champs
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickValidate(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Entrez votre mot de passe actuel");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setPositiveButton("Valider",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                checkData(input.getText().toString());
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
        //onBackPressed();
    }

    public void onClickDelete(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("VOUS ALLEZ SUPPRIMER VOTRE COMPTE.\nPour confirmer, entrez votre mot de passe actuel");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setPositiveButton("Valider",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteUser(input.getText().toString());
            }
        });
        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    public void onClickCancel(View v)
    {
        onBackPressed();
    }

    private void checkData(String mdp)
    {
        String pseudo = ((EditText)findViewById(R.id.ETPseudo)).getText().toString();
        if (!pseudo.equals("") && !pseudo.equals(Profil.prof.getName())) {
            changeName(mdp, pseudo);
        }

        String mdp1 = ((EditText)findViewById(R.id.ETPassword)).getText().toString();
        String mdp2 = ((EditText)findViewById(R.id.ETPasswordVerif)).getText().toString();
        if (!mdp1.equals("") && mdp1.equals(mdp2)) {
            changePassword(mdp, mdp1);
        }
    }

    public void changePassword(String oldPassword, String newPassword)
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ProfileModification> call = bookshelfApi.ChangePwd(MainActivity.token, oldPassword, newPassword);
        call.enqueue(new Callback<ProfileModification>() {
            @Override
            public void onResponse(Call<ProfileModification> call, Response<ProfileModification> response) {
                if (response.isSuccessful()) {
                    ProfileModification modif = response.body();
                    Snackbar snackbar = Snackbar.make(_rl, "Modification réussie !", Snackbar.LENGTH_LONG);
                    MainActivity.co = true;
                    snackbar.show();
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
            public void onFailure(Call<ProfileModification> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public void changeName(String oldPassword, String newName)
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ProfileModification> call = bookshelfApi.ChangeName(MainActivity.token, oldPassword, newName);
        call.enqueue(new Callback<ProfileModification>() {
            @Override
            public void onResponse(Call<ProfileModification> call, Response<ProfileModification> response) {
                if (response.isSuccessful()) {
                    ProfileModification modif = response.body();
                    Snackbar snackbar = Snackbar.make(_rl, "Modification réussie !", Snackbar.LENGTH_LONG);
                    snackbar.show();
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
            public void onFailure(Call<ProfileModification> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    private void deleteUser(String mdp) {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<DelProfile> call = bookshelfApi.DelProfil(MainActivity.token, mdp, "yes");
        call.enqueue(new Callback<DelProfile>() {
            @Override
            public void onResponse(Call<DelProfile> call, Response<DelProfile> response) {
                if (response.isSuccessful()) {
                    DelProfile del = response.body();
                    Snackbar snackbar = Snackbar.make(_rl, "Suppression réussi !", Snackbar.LENGTH_LONG);
                    snackbar.show();
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
            public void onFailure(Call<DelProfile> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }
}
