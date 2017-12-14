package com.eip.bookshelf;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.api.GoogleBooksApi;
import com.eip.utilities.model.Books;
import com.eip.utilities.model.BooksLocal.BooksLocal;
import com.eip.utilities.model.Item;
import com.eip.utilities.model.VolumeInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maxime on 02/03/2017.
 */

public class ShelfContainer extends Fragment
{
    private ArrayList<BiblioAdapter> _modelListBiblio = new ArrayList<>();
    private String _status;
    private View _v;
    private MainActivity.shelfType _type;
    private customAdapterBiblio _adapterBiblio;
    private RequestDBLocal _req;
    private boolean _currentTab = false;
    private TextWatcher _tWatcher = null;

    public ShelfContainer()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        _status = "-1";
        Bundle b = getArguments();
        if (b != null) {
            _type = (MainActivity.shelfType)b.getSerializable("type");
            if (_type == MainActivity.shelfType.MAINSHELF) {
                _status = b.getString("status");
            }
        } else {
            _type = null;
        }
        _req = new RequestDBLocal(_type, getContext());
//        _req.deletePrimaryInfo(null, null);
        if (_type == MainActivity.shelfType.MAINSHELF) {
            _v = inflater.inflate(R.layout.shelf_container, container, false); //Anciennement shelf_container !
            setAdapters();
            if (_currentTab) {
                setTextWatcher();
                mainShelf();
            }
        } else if (_type == MainActivity.shelfType.PROPOSHELF) {
            _v = inflater.inflate(R.layout.shelf_simple, container, false);
            setTextWatcher();
            setAdapters();
        } else if (_type == MainActivity.shelfType.WISHSHELF) {
            _v = inflater.inflate(R.layout.shelf_simple, container, false);
            setTextWatcher();
            setAdapters();
        }
        return _v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (getFragmentManager() == null) {
            return;
        }
        Fragment myFragment = getFragmentManager().findFragmentByTag("SHELF");
        if (myFragment != null && myFragment.isVisible()) {
            if (getActivity() == null) {
                return;
            }
            EditText field = getActivity().findViewById(R.id.ETkeyword);
            if (field == null) {
                field = _v.findViewById(R.id.ETkeyword);
            }
            field.setText("");
            if (_type == MainActivity.shelfType.PROPOSHELF) {
                propoShelf();
            } else if (_type == MainActivity.shelfType.WISHSHELF) {
                wishShelf();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        _currentTab = isVisibleToUser;
        if (isVisibleToUser && _v != null) {
            setTextWatcher();
            if (_modelListBiblio.size() == 0) {
                mainShelf();
            }
        } else {
            unsetTextWatcher();
        }
    }

    private void setTextWatcher()
    {
        if (getActivity() != null) {
            EditText field = getActivity().findViewById(R.id.ETkeyword);
            if (field == null) {
                field = _v.findViewById(R.id.ETkeyword);
            }
            _tWatcher = new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String typeSearch = "";
                    String content = s.toString();
                    if (!content.equals("")) {
                        Spinner sp = getActivity().findViewById(R.id.Stype);
                        if (sp == null) {
                            sp = _v.findViewById(R.id.Stype);
                        }
                        typeSearch = sp.getSelectedItem().toString();
                    }
                    _modelListBiblio.clear();
                    searchBookInShelf(typeSearch, content);
                }
            };
            field.addTextChangedListener(_tWatcher);
        }
    }

    private void unsetTextWatcher()
    {
        if (_tWatcher != null && getActivity() != null) {
            EditText field = getActivity().findViewById(R.id.ETkeyword);
            field.removeTextChangedListener(_tWatcher);
        }
    }

    private void mainShelf()
    {
        MainActivity.startLoading();
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<BooksLocal> call = bookshelfApi.getBookshelf(MainActivity.token);
        call.enqueue(new Callback<BooksLocal>() {
            @Override
            public void onResponse(Call<BooksLocal> call, Response<BooksLocal> response) {
                _modelListBiblio.clear();
                if (response.isSuccessful()) {
                    BooksLocal bookshelf = response.body();
                    List<com.eip.utilities.model.BooksLocal.Book> list = bookshelf.getData();
                    ListIterator<com.eip.utilities.model.BooksLocal.Book> it = list.listIterator();
                    ArrayList<String> isbns = new ArrayList<>();
                    Map<String, String> map = new HashMap<>();

                    while(it.hasNext()){
                        com.eip.utilities.model.BooksLocal.Book book = it.next();
                        isbns.add(book.getIsbn());
                        map.put(book.getIsbn(), book.getStatusId());
                    }
                    if (!isbns.isEmpty())
                        getBackBookInShelf(isbns, map);
                    else {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.RLShelf), "Votre bibliothèque est vide", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                } else {
                    try {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.RLShelf), "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                MainActivity.stopLoading();
            }

            @Override
            public void onFailure(Call<BooksLocal> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }

    private void propoShelf()
    {
        //MainActivity.startLoading();
        //Todo: Appel à la BDD pour recup les vrais PROPOS
        //MainActivity.stopLoading();
    }

    private void wishShelf()
    {
        MainActivity.startLoading();
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<BooksLocal> call = bookshelfApi.getWishBookshelf(MainActivity.token);
        call.enqueue(new Callback<BooksLocal>() {
            @Override
            public void onResponse(Call<BooksLocal> call, Response<BooksLocal> response) {
                _modelListBiblio.clear();
                if (response.isSuccessful()) {
                    BooksLocal bookshelf = response.body();
                    List<com.eip.utilities.model.BooksLocal.Book> list = bookshelf.getData();

                    ListIterator<com.eip.utilities.model.BooksLocal.Book> it = list.listIterator();
                    ArrayList<String> isbns = new ArrayList<>();
                    Map<String, String> map = new HashMap<>();

                    while(it.hasNext()){
                        com.eip.utilities.model.BooksLocal.Book book = it.next();
                        isbns.add(book.getIsbn());
                        map.put(book.getIsbn(), book.getStatusId());
                    }
                    if (!isbns.isEmpty())
                        getBackBookInShelf(isbns, map);
                    else {
                        Snackbar snackbar = Snackbar.make(_v, "Votre liste de souhaits est vide", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } else {
                    try {
                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar.make(_v, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        e.printStackTrace();
                    }
                }
                MainActivity.stopLoading();
            }

            @Override
            public void onFailure(Call<BooksLocal> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_v, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
                MainActivity.stopLoading();
            }
        });
    }

    private void setAdapters()
    {
        GridView gvBiblio = _v.findViewById(R.id.GVBiblio);
        _adapterBiblio = new customAdapterBiblio(_v, _modelListBiblio);
        gvBiblio.setAdapter(_adapterBiblio);
        _modelListBiblio.clear();

        gvBiblio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String isbn = ((TextView) view.findViewById(R.id.TVISBN)).getText().toString();
                Bundle b = new Bundle();
                b.putString("isbn", isbn);
                Intent in = new Intent(getActivity(), InfoBook.class);
                in.putExtra("book", b);
                startActivity(in);
            }
        });
    }

    private void searchBookInShelf(final String typeSearch, final String content)
    {
        Thread t = new Thread(new Runnable() {
            public void run() {
                Cursor cursor = _req.readFromSearch(typeSearch, content, _status);
                while(cursor.moveToNext()) {
                    String isbn = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDB.COLUMN_NAME_ISBN));
                    String title = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDB.COLUMN_NAME_TITLE));
                    String pic = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDB.COLUMN_NAME_PIC));
                    _modelListBiblio.add(new BiblioAdapter(title, pic, isbn));
                }
                cursor.close();
            }
        });
        t.start();
        try {
            t.join();
            _adapterBiblio.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getBackBookInShelf(final ArrayList<String> isbns, final Map<String, String> map)
    {
        Thread t = new Thread(new Runnable() {
            public void run() {
                if (!_status.equals("-1")) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        if (!entry.getValue().equals(_status)) {
                            isbns.remove(entry.getKey());
                        }
                    }
                }

                if (isbns.size() > 0) {
                    Cursor cursor = _req.readPrimaryInfo(isbns, _status);
                    while(cursor.moveToNext()) {
                        String isbn = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDB.COLUMN_NAME_ISBN));
                        String title = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDB.COLUMN_NAME_TITLE));
                        String pic = cursor.getString(cursor.getColumnIndex(LocalDBContract.LocalDB.COLUMN_NAME_PIC));
                        _modelListBiblio.add(new BiblioAdapter(title, pic, isbn));
                        isbns.remove(isbn);
                    }
                    cursor.close();
                    for (String isbn : isbns) {
                        VolumeInfo vi = getInfoBook(isbn);
                        String img;
                        if (vi.getImageLinks() == null || vi.getImageLinks().getThumbnail() == null) {
                            img = "https://puu.sh/wm9pR/adf0d3f814.jpg";
                        } else {
                            img = vi.getImageLinks().getThumbnail();
                        }
                        String genre = null;
                        String authors = null;
                        if (vi.getAuthors() != null) {
                            authors = TextUtils.join(", ", vi.getAuthors());
                        }
                        if (vi.getCategories() != null) {
                            genre = TextUtils.join(", ", vi.getCategories());
                        }
                        _modelListBiblio.add(new BiblioAdapter(vi.getTitle(), img, isbn));
                        _req.writePrimaryInfo(vi.getTitle(), img, isbn, authors, genre, map.get(isbn));
                    }
                }
            }
        });
        t.start();
        try {
            t.join();
            _adapterBiblio.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static VolumeInfo getInfoBook(String isbn)
    {
        GoogleBooksApi gbi = new Retrofit.Builder()
                .baseUrl(GoogleBooksApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleBooksApi.class);
        Call<Books> call = gbi.searchByIsbn("isbn:"+isbn);
        VolumeInfo vi = null;

        try {
            Books b = call.execute().body();
            if (b.getTotalItems() > 0) {
                Item item = b.getItems().get(0);
                vi = item.getVolumeInfo();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vi;
    }
}
