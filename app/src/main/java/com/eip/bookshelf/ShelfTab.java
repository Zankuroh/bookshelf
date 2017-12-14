package com.eip.bookshelf;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
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
        View v = inflater.inflate(R.layout.shelf_tab, container, false);

        ViewPager viewPager = v.findViewById(R.id.VPTab);
        setupViewPager(viewPager);

        TabLayout tabLayout = v.findViewById(R.id.TLTab);
        tabLayout.setupWithViewPager(viewPager);

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
}
