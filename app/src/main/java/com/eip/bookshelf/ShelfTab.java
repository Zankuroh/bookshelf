package com.eip.bookshelf;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.eip.utilities.api.BookshelfApi;
import com.eip.utilities.model.Notification.Notification;
import com.eip.utilities.model.Notification.Notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maxime on 02/03/2017.
 */

public class ShelfTab extends Fragment
{
    public ShelfTab()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.shelf_tab, container, false);

        ViewPager viewPager = v.findViewById(R.id.VPTab);
        setupViewPager(viewPager);

        TabLayout tabLayout = v.findViewById(R.id.TLTab);
        tabLayout.setupWithViewPager(viewPager);

        Bundle b = getArguments();
        if (b != null) {
            if (b.getBoolean("connection", false)) {
                getNotification(v);
            }
        }

        return v;
    }

    private void setupViewPager(ViewPager viewPager)
    {
        if (this.getActivity() == null) {
            return;
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getActivity().getSupportFragmentManager());


        ShelfContainer tabAll = new ShelfContainer();
        Bundle arg = new Bundle();
        arg.putSerializable("type", MainActivity.shelfType.MAINSHELF);
        arg.putString("status", "-1");
        tabAll.setArguments(arg);

        ShelfContainer tabFav = new ShelfContainer();
        Bundle argFav = new Bundle();
        argFav.putSerializable("type", MainActivity.shelfType.MAINSHELF);
        argFav.putString("status", "4");
        tabFav.setArguments(argFav);

        ShelfContainer tabR = new ShelfContainer();
        Bundle argR = new Bundle();
        argR.putSerializable("type", MainActivity.shelfType.MAINSHELF);
        argR.putString("status", "0");
        tabR.setArguments(argR);

        ShelfContainer tabNR = new ShelfContainer();
        Bundle argNR = new Bundle();
        argNR.putSerializable("type", MainActivity.shelfType.MAINSHELF);
        argNR.putString("status", "1");
        tabNR.setArguments(argNR);

        ShelfContainer tabB = new ShelfContainer();
        Bundle argB = new Bundle();
        argB.putSerializable("type", MainActivity.shelfType.MAINSHELF);
        argB.putString("status", "3");
        tabB.setArguments(argB);

        adapter.addFrag(tabAll, "TOUS");
        adapter.addFrag(tabFav, "FAVORIS");
        adapter.addFrag(tabR, "LU");
        adapter.addFrag(tabNR, "NON LU");
        adapter.addFrag(tabB, "PRÊTÉ");
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }

    private void getNotification(final View v)
    {
        if (v == null) {
            return;
        }
        BookshelfApi bookshelfApi = new Retrofit.Builder()
                .baseUrl(BookshelfApi.APIPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BookshelfApi.class);
        Call<Notifications> call = bookshelfApi.getNotifications(MainActivity.token);
        call.enqueue(new Callback<Notifications>() {
            @Override
            public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                if (response.isSuccessful()) {
                    List<Notification> notifications = response.body().getData();
                    if (!notifications.isEmpty()) {
                        final Dialog dial = new Dialog(v.getContext());
                        dial.setContentView(R.layout.notification_popup);
                        dial.setTitle("Vos nouveautées");
                        dial.show();
                        ArrayList<NotificationAdapter> _modelListNotif = new ArrayList<>();
                        customAdapterNotification _adapterNotif = new customAdapterNotification(v, _modelListNotif);
                        ListView lv = dial.findViewById(R.id.LVNew);
                        lv.setAdapter(_adapterNotif);
                        _modelListNotif.clear();
                        ListIterator<Notification> it = notifications.listIterator();
                        while (it.hasNext()) {
                            Notification newNotif = it.next();
                            String name = newNotif.getFirstName();
                            if (newNotif.getLastName() != null)
                                name += " " + newNotif.getLastName();
                            _modelListNotif.add(new NotificationAdapter(name, newNotif.getTitle()));
                        }
                        _adapterNotif.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<Notifications> call, Throwable t)
            {
                t.printStackTrace();
            }
        });
    }
}
