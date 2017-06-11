package com.eip.bookshelf;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

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
        Log.d("viewpager", String.valueOf(R.string.appbar_scrolling_view_behavior));
        View v = inflater.inflate(R.layout.shelf_tab, container, false);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.VPTab);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.TLTab);
        tabLayout.setupWithViewPager(viewPager);

        return v;
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this.getActivity().getSupportFragmentManager());
        Bundle arg = new Bundle();
        arg.putSerializable("type", MainActivity.shelfType.MAINSHELF);
        ShelfContainer tabAll = new ShelfContainer();
        tabAll.setArguments(arg);
        ShelfContainer tabFav = new ShelfContainer();
        tabFav.setArguments(arg);
        ShelfContainer tabR = new ShelfContainer();
        tabR.setArguments(arg);
        ShelfContainer tabNR = new ShelfContainer();
        tabNR.setArguments(arg);
        adapter.addFrag(tabAll, "TOUS");
        adapter.addFrag(tabFav, "FAVORIS");
        adapter.addFrag(tabR, "LU");
        adapter.addFrag(tabNR, "NON LU");
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter
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
}
