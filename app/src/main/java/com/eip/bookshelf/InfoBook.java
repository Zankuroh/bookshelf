package com.eip.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.api.GoogleBooksApi;
import com.eip.utilities.model.Books;
import com.eip.utilities.model.IndustryIdentifier;
import com.eip.utilities.model.Item;
import com.eip.utilities.model.ModifBook.ModifBook;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maxime on 25/04/2017.
 */

public class InfoBook extends AppCompatActivity
{
    private ListView _lvCom;
    private ArrayList<ComAdapter> _modelListCom = new ArrayList<>();
    private MainActivity.shelfType _type;
    private RelativeLayout _rl;

    public InfoBook()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info);
        _rl = (RelativeLayout)findViewById(R.id.RLBookInfo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        _lvCom = (ListView) findViewById(R.id.LVCom);
        Intent i = getIntent();
        Bundle b = i.getBundleExtra("book");
        _type = (MainActivity.shelfType)i.getSerializableExtra("shelf");
        HashMap<String, String> info = (HashMap<String, String>)b.getSerializable("info");

        setAdapters();
        moreDataBook(info);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Snackbar snackbar;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.IAddBook:
                AddToBookShelf();
                break;
            case R.id.IRemoveBook:
                deleteToBookShelf();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        if (_type == MainActivity.shelfType.MAINSHELF) {
            menu.findItem(R.id.IAddBook).setVisible(false);
        } else {
            menu.findItem(R.id.IRemoveBook).setVisible(false);
        }
        return true;
    }


    private void setAdapters()
    {
        customAdapterCom _adapterCom = new customAdapterCom(this, _modelListCom);
        _lvCom.setAdapter(_adapterCom);
        //Todo: Récupérer les vrais commentaires :|
        _modelListCom.add(new ComAdapter("Maxime", "23/02/2016 à 13h42", "Super livre !"));
        getTotalHeightofListView();
    }

    private void moreDataBook(HashMap<String, String> info)
    {
        TextView tv = (TextView) findViewById(R.id.TVInfoBook);
        TextView tvt = (TextView) findViewById(R.id.TVTitreBook);
        TextView tvr = (TextView) findViewById(R.id.TVResum);
        TextView tvi = (TextView) findViewById(R.id.TVIsbnBook);
        ImageView iv = (ImageView) findViewById(R.id.IVBook);
        //TODO remplacer par la requête qui est dans getInfoBook dans Book.java
        tvt.setText(info.get("title"));
        tv.setText("Date de sortie : " + info.get("date"));
        tv.setText(tv.getText() + "\nAuteur : " + info.get("author"));
        tv.setText(tv.getText() + "\nIsbn : " + info.get("isbn"));
        tv.setText(tv.getText() + "\nGenre : " + info.get("genre"));
        tv.setText(tv.getText() + "\nNote : " + info.get("note"));
        tvr.setText(info.get("resume"));
        tvi.setText("ISBN = " + info.get("isbn"));
        if (info.get("picture") != null && !info.get("picture").equals("")) {
            Picasso.with(this).load(info.get("picture")).fit().into(iv);
        }
    }

    private void getTotalHeightofListView()
    {
        ListAdapter mAdapter = _lvCom.getAdapter();
        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++)
        {
            View mView = mAdapter.getView(i, null, _lvCom);

            mView.measure( View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = _lvCom.getLayoutParams();
        params.height = totalHeight + (_lvCom.getDividerHeight() * (mAdapter.getCount() - 1));
        _lvCom.setLayoutParams(params);
        _lvCom.requestLayout();
    }

    public void onClickSendCom(View v)
    {
        EditText et = (EditText) findViewById(R.id.ETCom);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH'h'mm", Locale.FRANCE);
        String currentDateandTime = sdf.format(new Date());
        _modelListCom.add(new ComAdapter("Nicolas", currentDateandTime, et.getText().toString()));
        et.setText("");
        MainActivity.hideSoftKeyboard(InfoBook.this);
        getTotalHeightofListView();
    }


    public void AddToBookShelf(){


        TextView tv = (TextView) findViewById(R.id.TVInfoBook);
        int start = tv.getText().toString().indexOf("Isbn : ");
        String isbn = tv.getText().toString().substring(start + 7, start +7+13);
        Log.i("ADDBOOK", isbn);
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifBook> call = bookshelfApi.AddBook(MainActivity.token, isbn);
        call.enqueue(new Callback<ModifBook>() {
            @Override
            public void onResponse(Call<ModifBook> call, Response<ModifBook> response) {
                if (response.isSuccessful()) {
                    ModifBook modif = response.body();
                    Snackbar snackbar = Snackbar.make(_rl, "Le livre a été ajouté à votre bibliothèque", Snackbar.LENGTH_LONG);
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
            public void onFailure(Call<ModifBook> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public void deleteToBookShelf(){
        TextView tv = (TextView) findViewById(R.id.TVInfoBook);
        int start = tv.getText().toString().indexOf("Isbn : ");
        String isbn = tv.getText().toString().substring(start + 7, start +7+13);
        Log.i("DELBOOK", isbn);
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifBook> call = bookshelfApi.DelBook(MainActivity.token, isbn, "deleted");
        call.enqueue(new Callback<ModifBook>() {
            @Override
            public void onResponse(Call<ModifBook> call, Response<ModifBook> response) {
                if (response.isSuccessful()) {
                    ModifBook modif = response.body();
                    Snackbar snackbar = Snackbar.make(_rl, "Le livre a été supprimé de votre bibliothèque", Snackbar.LENGTH_LONG);
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
            public void onFailure(Call<ModifBook> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public void AddToWishList(){
        TextView tv = (TextView) findViewById(R.id.TVInfoBook);
        int start = tv.getText().toString().indexOf("Isbn : ");
        String isbn = tv.getText().toString().substring(start + 7, start +7+13);
        Log.i("ADDBOOK", isbn);
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifBook> call = bookshelfApi.AddWishBook(MainActivity.token, isbn);
        call.enqueue(new Callback<ModifBook>() {
            @Override
            public void onResponse(Call<ModifBook> call, Response<ModifBook> response) {
                if (response.isSuccessful()) {
                    ModifBook modif = response.body();
                    Snackbar snackbar = Snackbar.make(_rl, "Le livre a été ajouté à votre liste de souhait", Snackbar.LENGTH_LONG);
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
            public void onFailure(Call<ModifBook> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public void deleteToWishList(){
        TextView tv = (TextView) findViewById(R.id.TVInfoBook);
        int start = tv.getText().toString().indexOf("Isbn : ");
        String isbn = tv.getText().toString().substring(start + 7, start +7+13);
        Log.i("DELBOOK", isbn);
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifBook> call = bookshelfApi.DelWishBook(MainActivity.token, isbn, "deleted");
        call.enqueue(new Callback<ModifBook>() {
            @Override
            public void onResponse(Call<ModifBook> call, Response<ModifBook> response) {
                if (response.isSuccessful()) {
                    ModifBook modif = response.body();
                    Snackbar snackbar = Snackbar.make(_rl, "Le livre a été supprimé de votre liste de souhait", Snackbar.LENGTH_LONG);
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
            public void onFailure(Call<ModifBook> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public void getInfoBook(String isbn) {
        GoogleBooksApi googleBooksApi = new Retrofit.Builder()
                .baseUrl(GoogleBooksApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleBooksApi.class);

        Call<Books> call = googleBooksApi.searchByIsbn("isbn:"+isbn);
        call.enqueue(new Callback<Books>() {
            @Override
            public void onResponse(Call<Books> call, Response<Books> response) {
                if (response.isSuccessful()) {
                    Books book = response.body();
                    if (book.getTotalItems() > 0) {
                        Item item = book.getItems().get(0);
                        String titre = item.getVolumeInfo().getSubtitle() + item.getVolumeInfo().getTitle();
                        String author = "";
                        List<String> authors = item.getVolumeInfo().getAuthors();
                        ListIterator<String> it = authors.listIterator();
                        while(it.hasNext()){
                            String name = it.next();
                            author += "/"+name;
                        }
                        author = author.substring(1);
                        String resum = item.getVolumeInfo().getDescription();
                        String genre = "";
                        List<String> genres = item.getVolumeInfo().getCategories();
                        ListIterator<String> it3 = genres.listIterator();
                        while(it3.hasNext()){
                            String kind = it3.next();
                            genre += "/"+kind;
                        }
                        genre = genre.substring(1);
                        String date_publisher = item.getVolumeInfo().getPublishedDate();
                        String isbn13 = "0";
                        List<IndustryIdentifier> isbns = item.getVolumeInfo().getIndustryIdentifiers();
                        ListIterator<IndustryIdentifier> it2 = isbns.listIterator();
                        while(it2.hasNext()){
                            IndustryIdentifier isbn = it2.next();
                            if (isbn.getType().contains("13")) {
                                isbn13 = isbn.getIdentifier();
                            }
                        }
                        String urlImage = item.getVolumeInfo().getImageLinks().getThumbnail();
                    }
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_rl, "Erreur !!!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<Books> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }
}
