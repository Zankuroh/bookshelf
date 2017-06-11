package com.eip.bookshelf;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Maxime on 28/04/2017.
 */

public class EditProfil extends AppCompatActivity
{
    public EditProfil()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profil);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.RLEditProfil);
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

    public void onClickValidate(View v)
    {
        //Todo: Update les info dans la BDD
        //Todo: Si mdp rempli, check les deux champs puis update en BDD
        onBackPressed();
    }

    public void onClickCancel(View v)
    {
        onBackPressed();
    }
}
