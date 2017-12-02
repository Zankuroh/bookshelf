package com.eip.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.support.v7.app.AppCompatActivity;
import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.Register.Register;

import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jolyn on 23/11/2016.
 */

public class SignUp extends AppCompatActivity
{
    private RelativeLayout _lp;

    public SignUp()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        _lp = (RelativeLayout)findViewById(R.id.RLSignUp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
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

    public void onClickSignUp(View v)
    {
        EditText name = (EditText) findViewById(R.id.SignUpFirstName);
        EditText password = (EditText) findViewById(R.id.SignUpPwd);
        EditText password2 = (EditText) findViewById(R.id.SignUpPwd2);
        EditText email = (EditText) findViewById(R.id.SignUpMail);
        String errors = "";
        if (name.getText().toString().equals("")) {
            errors += "Le champ pseudo est obligatoire.";
        }
        if (password.getText().toString().equals("") || password2.getText().toString().equals("")) {
            if (!errors.equals("")) {
                errors += "\n";
            }
            errors += "Les champs mot de passe et validation mots de passe sont obligatoires.";
        }
        if (email.getText().toString().equals("")) {
            if (!errors.equals("")) {
                errors += "\n";
            }
            errors += "Le champ email est obligatoire.";
        }

        if (errors.equals("")) {
            if (!SignUp.verifyEmail(email.getText().toString())) {
                errors += "L'adresse mail n'est pas valide.";
            }
            if (password.getText().toString().length() < 5) {
                if (!errors.equals("")) {
                    errors += "\n";
                }
                errors += "Le mots de passe doit contenir 5 caractères ou plus.";
            }
            if (!SignUp.checkPassword(password.getText().toString(), password2.getText().toString())) {
                if (!errors.equals("")) {
                    errors += "\n";
                }
                errors += "Les mots de passe ne sont pas identiques.";
            }
        }
        if (!errors.equals("")) {
            Snackbar snackbar = Snackbar.make(_lp, errors, Snackbar.LENGTH_LONG);
            snackbar.show();
            return ;
        }
        register(name.getText().toString(), email.getText().toString(), password.getText().toString());
    }

    private void register(String name, final String email, final String password)
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<Register> call = bookshelfApi.Register(name, password, email);
        call.enqueue(new Callback<Register>() {
            @Override
            public void onResponse(Call<Register> call, Response<Register> response) {
                if (response.isSuccessful()) {
                    //Register auth = response.body();
                    Snackbar snackbar = Snackbar.make(_lp, "Création réussie !", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    Intent intent = new Intent();
                    intent.putExtra("login", email);
                    intent.putExtra("pwd", password);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    try {
                        JSONObject jObj = new JSONObject(response.errorBody().string());
                        JSONObject jObjError = jObj.getJSONObject("errors");
                        String error = "";
                        error = jObj.getString("title");
                        JSONArray password;
                        JSONArray email;
                        JSONArray name;
                        try {
                            name = jObjError.getJSONArray("name");
                            error += "\n" + name.getString(0);
                        } catch (Exception e) {}
                        try {
                            email = jObjError.getJSONArray("email");
                            error += "\n" + email.getString(0);
                        } catch (Exception e) {}
                        try {
                            password = jObjError.getJSONArray("password");
                            error += "\n" + password.getString(0);
                        } catch (Exception e) {}
                        Snackbar snackbar = Snackbar.make(_lp, "Erreur : " + error, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_lp, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Register> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_lp, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    private static Boolean verifyEmail(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static Boolean checkPassword(String pwd1, String pwd2)
    {
        return pwd1.equals(pwd2);
    }
}