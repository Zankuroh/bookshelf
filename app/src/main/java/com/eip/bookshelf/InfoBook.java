package com.eip.bookshelf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.AuthorSubscription.SubscriptionValidator;
import com.eip.utilities.model.Authors.Author;
import com.eip.utilities.model.Authors.Authors;
import com.eip.utilities.model.ChangeStatus.ChangeStatus;
import com.eip.utilities.model.ModifAuthor.ModifAuthor;
import com.eip.utilities.model.ModifReview.ModifReview;
import com.eip.utilities.model.ModifBook.ModifBook;
import com.eip.utilities.model.Reviews.Review;
import com.eip.utilities.model.Reviews.Reviews;
import com.eip.utilities.model.VolumeInfo;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class InfoBook extends AppCompatActivity
{
    private ListView _lvCom;
    private ArrayList<ComAdapter> _modelListCom = new ArrayList<>();
    private RelativeLayout _rl;
    private String _isbn;
    private VolumeInfo _vi;
    private boolean _inMain;
    private boolean _inWish;
    private Menu _menu;
    private RequestDBLocal _req;
    private String _myCom = "";
    private float _myRate = 0.0f;
    private int _myId = -1;
    private boolean _isValid = true;

    public InfoBook()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info);
        _rl = findViewById(R.id.RLBookInfo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        _lvCom = findViewById(R.id.LVCom);
        Intent i = getIntent();
        Bundle b = i.getBundleExtra("book");
        _isbn = b.getString("isbn");

        setButtons();
        setAdapters();
        moreDataBook();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.IAddBookBiblio:
                addToBookShelf();
                _menu.findItem(R.id.IAddBookBiblio).setVisible(false);
                _menu.findItem(R.id.IRemoveBookBiblio).setVisible(true);
                break;
            case R.id.IRemoveBookBiblio:
                deleteToBookShelf();
                _menu.findItem(R.id.IAddBookBiblio).setVisible(true);
                _menu.findItem(R.id.IRemoveBookBiblio).setVisible(false);
                break;
            case R.id.IAddBookWish:
                addToWishList();
                _menu.findItem(R.id.IAddBookWish).setVisible(false);
                _menu.findItem(R.id.IRemoveBookWish).setVisible(true);
                break;
            case R.id.IRemoveBookWish:
                deleteToWishList();
                _menu.findItem(R.id.IAddBookWish).setVisible(true);
                _menu.findItem(R.id.IRemoveBookWish).setVisible(false);
                break;
            case R.id.ICatFav:
                switchBookState("4");
                break;
            case R.id.ICatUnread:
                switchBookState("1");
                break;
            case R.id.ICatRead:
                switchBookState("0");
                break;
            case R.id.ICatBorrow:
                switchBookState("3");
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        _menu = menu;

        if (MainActivity.token != null && _isValid) {
            menu.findItem(R.id.IAddFriend).setVisible(false);
            menu.findItem(R.id.IRemoveFriend).setVisible(false);
            if (_inMain) {
                menu.findItem(R.id.IAddBookBiblio).setVisible(false);
            } else {
                menu.findItem(R.id.IRemoveBookBiblio).setVisible(false);
                menu.findItem(R.id.ICategories).setVisible(false);
            }

            if (_inWish) {
                menu.findItem(R.id.IAddBookWish).setVisible(false);
            } else {
                menu.findItem(R.id.IRemoveBookWish).setVisible(false);
            }
        } else {
            hideButtons();
        }

        return true;
    }

    private void switchBookState(final String state)
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ChangeStatus> call = bookshelfApi.ChangeStatus(MainActivity.token, _isbn, state);
        call.enqueue(new Callback<ChangeStatus>() {
            @Override
            public void onResponse(Call<ChangeStatus> call, Response<ChangeStatus> response) {
                if (response.isSuccessful()) {
                    _req.updateStateBook(_isbn, String.valueOf(state));
                    Snackbar snackbar = Snackbar.make(_rl, "Le statut a bien été changé", Snackbar.LENGTH_LONG);
                    snackbar.show();
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
            }

            @Override
            public void onFailure(Call<ChangeStatus> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    private void hideButtons()
    {
        _menu.findItem(R.id.IRemoveBookBiblio).setVisible(false);
        _menu.findItem(R.id.IAddBookBiblio).setVisible(false);
        _menu.findItem(R.id.IAddBookWish).setVisible(false);
        _menu.findItem(R.id.IRemoveBookWish).setVisible(false);
        _menu.findItem(R.id.IAddFriend).setVisible(false);
        _menu.findItem(R.id.IRemoveFriend).setVisible(false);
        _menu.findItem(R.id.ICategories).setVisible(false);
        findViewById(R.id.BReview).setVisibility(View.GONE);
    }

    private void setButtons()
    {
        if (MainActivity.token != null) {
            _req = new RequestDBLocal(MainActivity.shelfType.MAINSHELF, this);
            ArrayList<String> isbns = new ArrayList<>();
            isbns.add(_isbn);

            Cursor c = _req.readPrimaryInfo(isbns, "-1");
            _inMain = c.getCount() != 0;
            c.close();
            _req.setType(MainActivity.shelfType.WISHSHELF);
            c = _req.readPrimaryInfo(isbns, "-1");
            _inWish = c.getCount() != 0;
            c.close();
        }
    }

    private void setAdapters()
    {
        if (MainActivity.token != null) {
            customAdapterCom _adapterCom = new customAdapterCom(this, _modelListCom);
            _lvCom.setAdapter(_adapterCom);
            getReview();
        }
    }

    private void moreDataBook()
    {
        TextView tv = findViewById(R.id.TVInfoBook);
        TextView tvt = findViewById(R.id.TVTitreBook);
        TextView tvr = findViewById(R.id.TVResum);
        ImageView iv = findViewById(R.id.IVBook);
        Thread t = new Thread(new Runnable() {
            public void run() {
                _vi = ShelfContainer.getInfoBook(_isbn);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (_vi == null) {
            _isValid = false;
            Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + "Le livre n'a pas été trouvé", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        tvt.setText(_vi.getTitle());

        if (_vi.getPublishedDate() != null) {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            try {
                date = dt.parse(_vi.getPublishedDate());
            } catch (ParseException e) {
                dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                try {
                    date = dt.parse(_vi.getPublishedDate());
                } catch (ParseException e1) {
                    date = null;
                }
            }
            if (date != null) {
                SimpleDateFormat dt1 = new SimpleDateFormat("dd'/'MM'/'yyyy");
                tv.setText("Date de sortie : " + dt1.format(date));
            } else {
                tv.setText("Date de sortie : " + _vi.getPublishedDate());
            }
        } else {
            tv.setText("Date de sortie : -");
        }

        ArrayList<ClickableSpan> spans = new ArrayList<>();
        if (_vi.getAuthors() == null || _vi.getAuthors().size() == 0) {
            tv.setText(tv.getText() + "\nAuteur : -");
        } else {
            tv.setText(tv.getText() + "\nAuteur : ");
            for (int i = 0; i < _vi.getAuthors().size(); i++) {
                final int cloneI = i;
                tv.setText(tv.getText() + _vi.getAuthors().get(i));
                if (i != _vi.getAuthors().size() - 1) {
                    tv.setText(tv.getText() + ", ");
                }
                spans.add(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        onClickLinks(_vi.getAuthors().get(cloneI));
                    }
                });
            }
        }

        if (_vi.getCategories() == null || _vi.getCategories().size() == 0) {
            tv.setText(tv.getText() + "\nGenre : -");
        } else {
            tv.setText(tv.getText() + "\nGenre : ");
            for (int i = 0; i < _vi.getCategories().size(); i++) {
                tv.setText(tv.getText() + _vi.getCategories().get(i));
                if (i != _vi.getCategories().size() - 1) {
                    tv.setText(", ");
                }
            }
        }

        if (_vi.getRatingsCount() == null) {
            tv.setText(tv.getText() + "\nNote : -");
        } else {
            tv.setText(tv.getText() + "\nNote : " + _vi.getRatingsCount());
        }

        if (_vi.getDescription() != null) {
            tvr.setText(_vi.getDescription());
        }

        if (_vi.getImageLinks() != null && _vi.getImageLinks().getThumbnail() != null) {
            Picasso.with(this).load(_vi.getImageLinks().getThumbnail()).fit().into(iv);
        }
        if (_vi.getAuthors() != null && MainActivity.token != null) {
            this.makeLinks(tv, _vi.getAuthors(), spans);
        }
    }

    private void makeLinks(TextView textView, List<String> links, ArrayList<ClickableSpan> clickableSpans)
    {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.size(); i++) {
            ClickableSpan clickableSpan = clickableSpans.get(i);
            String link = links.get(i);

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    public void onClickLinks(String author)
    {
        final String fauthor = author;
        String msg = "Voulez-vous suivre l'auteur " + author + " ?\nVous serez averti par notification des dernières sorties.";
        AlertDialog.Builder adb = new AlertDialog.Builder(_rl.getContext());
                    adb.setTitle("Suivre");
                    adb.setMessage(msg);
                    adb.setNegativeButton("Annuler", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                    });
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getAllAuthor(fauthor);
                        }
                    });
        adb.show();
    }

    private void updateDisplayedReviews()
    {
        _modelListCom.clear();
        getReview();
        getTotalHeightofListView();
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

    public void onClickReview(View v)
    {
        final Dialog dial = new Dialog(this);
        dial.setContentView(R.layout.review_popup);
        dial.setTitle("Votre critique");
        Button btnDelete = dial.findViewById(R.id.BReviewDelete);
        Button btnConfirm = dial.findViewById(R.id.BReviewConfirm);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReview(_myId);
                dial.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RatingBar rb = dial.findViewById(R.id.ratingBar);
                EditText et = dial.findViewById(R.id.ETCom);
                if (_myId == -1) {
                    addReview(et.getText().toString(), String.valueOf(rb.getRating()));
                } else {
                    changeReview(_myId, et.getText().toString(), String.valueOf(rb.getRating()));
                }
                dial.dismiss();
            }
        });
        dial.show();
        TextView tv = dial.findViewById(R.id.ETCom);
        RatingBar rb = dial.findViewById(R.id.ratingBar);
        tv.setText(_myCom);
        rb.setRating(_myRate);
        if (_myId == -1) {
            dial.findViewById(R.id.BReviewDelete).setVisibility(View.GONE);
        }
    }

    public void onClickBuy(View v)
    {
        TextView tvt = findViewById(R.id.TVTitreBook);

        String url = "https://www.amazon.fr/s/ref=nb_sb_noss_1?__mk_fr_FR=ÅMÅŽÕÑ&url=search-alias=aps&field-keywords=" + tvt.getText();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void addToBookShelf()
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifBook> call = bookshelfApi.AddBook(MainActivity.token, _isbn, "1");
        call.enqueue(new Callback<ModifBook>() {
            @Override
            public void onResponse(Call<ModifBook> call, Response<ModifBook> response) {
                if (response.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(_rl, "Le livre a été ajouté à votre bibliothèque", Snackbar.LENGTH_LONG);
                    snackbar.show();
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

    public void deleteToBookShelf()
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifBook> call = bookshelfApi.DelBook(MainActivity.token, _isbn, "yes");
        call.enqueue(new Callback<ModifBook>() {
            @Override
            public void onResponse(Call<ModifBook> call, Response<ModifBook> response) {
                if (response.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(_rl, "Le livre a été supprimé de votre bibliothèque", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    _req.deletePrimaryInfo(_isbn, MainActivity.shelfType.MAINSHELF);
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

    public void addToWishList()
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifBook> call = bookshelfApi.AddWishBook(MainActivity.token, _isbn);
        call.enqueue(new Callback<ModifBook>() {
            @Override
            public void onResponse(Call<ModifBook> call, Response<ModifBook> response) {
                if (response.isSuccessful()) {
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

    public void deleteToWishList()
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifBook> call = bookshelfApi.DelWishBook(MainActivity.token, _isbn, "yes");
        call.enqueue(new Callback<ModifBook>() {
            @Override
            public void onResponse(Call<ModifBook> call, Response<ModifBook> response) {
                if (response.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(_rl, "Le livre a été supprimé de votre liste de souhaits", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    _req.deletePrimaryInfo(_isbn, MainActivity.shelfType.WISHSHELF);
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

    private void getReview()
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<Reviews> call = bookshelfApi.getReview(MainActivity.token, _isbn);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                if (response.isSuccessful()) {
                    Reviews rev = response.body();
                    List<Review> list_reviews = rev.getData().getReviews();
                    for (Review r : list_reviews) {
                        if (Boolean.parseBoolean(r.getCanEdit())) {
                            _myId = r.getId();
                            _myCom = r.getContent();
                            _myRate = Float.parseFloat(r.getRate());
                        }
                        _modelListCom.add(new ComAdapter(r.getUserName(), r.getCreatedAt(), r.getContent(), r.getRate()));
                    }
                    getTotalHeightofListView();
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
            public void onFailure(Call<Reviews> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public void addReview(String content, String rate)
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifReview> call = bookshelfApi.AddReview(MainActivity.token, _isbn, content, rate);
        call.enqueue(new Callback<ModifReview>() {
            @Override
            public void onResponse(Call<ModifReview> call, Response<ModifReview> response) {
                if (response.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(_rl, "Le commentaire a bien été ajouté", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    updateDisplayedReviews();
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
            public void onFailure(Call<ModifReview> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public void changeReview(int reviewId, String content, String rate)
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifReview> call = bookshelfApi.ChangeReview(MainActivity.token, reviewId, content, rate);
        call.enqueue(new Callback<ModifReview>() {
            @Override
            public void onResponse(Call<ModifReview> call, Response<ModifReview> response) {
                if (response.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(_rl, "Le commentaire a bien été modifié", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    updateDisplayedReviews();
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
            public void onFailure(Call<ModifReview> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    public void deleteReview(int reviewId)
    {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifReview> call = bookshelfApi.DelReview(MainActivity.token, reviewId, "true");
        call.enqueue(new Callback<ModifReview>() {
            @Override
            public void onResponse(Call<ModifReview> call, Response<ModifReview> response) {
                if (response.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(_rl, "Le commentaire a bien été supprimé", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    _myId = -1;
                    _myRate = 0.0f;
                    _myCom = "";
                    updateDisplayedReviews();
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
            public void onFailure(Call<ModifReview> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    private void getAllAuthor(final String Author) {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<Authors> call = bookshelfApi.getAuthors(MainActivity.token);
        call.enqueue(new Callback<Authors>() {
            @Override
            public void onResponse(Call<Authors> call, Response<Authors> response) {
                if (response.isSuccessful()) {
                    boolean check = false;
                    Authors rep = response.body();
                    List<Author> authors = rep.getData();
                    ListIterator<Author> it = authors.listIterator();
                    while (it.hasNext()) {
                        Author author = it.next();
                        String comp = author.getFirstName();
                        if (author.getLastName() != null)
                        {
                            comp += " "+author.getLastName();
                        }
                        if (comp.equals(Author)) {
                            check = true;
                            addToSub(author.getId());
                        }
                    }
                    if (!check) {
                        addAuthor(Author);
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
            }

            @Override
            public void onFailure(Call<Authors> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    private void addAuthor(String author) {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<ModifAuthor> call = bookshelfApi.AddAuthor(MainActivity.token, author, "");
        call.enqueue(new Callback<ModifAuthor>() {
            @Override
            public void onResponse(Call<ModifAuthor> call, Response<ModifAuthor> response) {
                if (response.isSuccessful()) {
                    ModifAuthor author = response.body();
                    addToSub(author.getData().getId());
                } else {
                    Snackbar snackbar = Snackbar.make(_rl, "Une erreur est survenue.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<ModifAuthor> call, Throwable t)
            {
                Snackbar snackbar = Snackbar.make(_rl, "Erreur : " + t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
                t.printStackTrace();
            }
        });
    }

    private void addToSub(String Id) {
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<SubscriptionValidator> call = bookshelfApi.AddAuthorSubscription(MainActivity.token, Id);
        call.enqueue(new Callback<SubscriptionValidator>() {
            @Override
            public void onResponse(Call<SubscriptionValidator> call, Response<SubscriptionValidator> response) {
                if (response.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(_rl, "Vous serez notifié lors de ses prochaines sorties", Snackbar.LENGTH_LONG);
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

