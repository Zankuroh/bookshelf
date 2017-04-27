package com.eip.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Maxime on 25/04/2017.
 */

public class InfoBook extends AppCompatActivity
{
    private customAdapterCom _adapterCom;
    private ListView _lvCom;
    private ArrayList<ComAdapter> _modelListCom = new ArrayList<>();

    public InfoBook()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info);

        _lvCom = (ListView) findViewById(R.id.LVCom);
        Bundle b = getIntent().getExtras();

        Log.d("test serializable", b.getSerializable("info").toString());
        setAdapters();
//        moreDataBook();
    }

    private void setAdapters()
    {
        _adapterCom = new customAdapterCom(this, _modelListCom);
        _lvCom.setAdapter(_adapterCom);
        //FAKE
        _modelListCom.add(new ComAdapter("Maxime", "23/02/2016 à 13h42", "Super livre !"));
    }

//    protected void moreDataBook(String title, List<Book> infos)
//    {
//        int i;
//
//        if (infos == null) {
//            infos = tempHardTest();
//        }
//        if (infos != null) {
//            for (i = 0; i < infos.size(); i++) {
//                if (title.equals(infos.get(i).getTitre())) {
//                    Book b = infos.get(i);
//                    TextView tv = (TextView) findViewById(R.id.TVInfoBook);
//                    TextView tvt = (TextView) findViewById(R.id.TVTitreBook);
//                    TextView tvr = (TextView) findViewById(R.id.TVResum);
//                    tvt.setText(b.getTitre());
//                    tv.setText("Date de sortie : " + b.getDate());
//                    tv.setText(tv.getText() + "\nAuteur : " + b.getAuteur());
//                    tv.setText(tv.getText() + "\nGenre : " + b.getGenre());
//                    tv.setText(tv.getText() + "\nNote : " + b.getNote().toString());
//                    tvr.setText(b.getResum());
//
//                    /*ImageView iv = (ImageView) findViewById(R.id.IVBook);
//                    iv.setImageBitmap(OptimizeBitmap.decodeSampledBitmapFromResource(getResources(), b.getImage(), 200, 200));*/
//                }
//            }
//        }
//    }

    public void getTotalHeightofListView()
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
        Log.i("sendCom", currentDateandTime);
        _modelListCom.add(new ComAdapter("Nicolas", currentDateandTime, et.getText().toString()));
        et.setText("");
        MainActivity.hideSoftKeyboard(InfoBook.this);
        getTotalHeightofListView();
    }
}
