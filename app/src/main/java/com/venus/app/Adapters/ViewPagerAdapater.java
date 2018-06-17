package com.venus.app.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.venus.app.applismsinterfaces.ListeFragment;

public class ViewPagerAdapater extends FragmentStatePagerAdapter {

    public ViewPagerAdapater(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println("get item");
        return ListeFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1: return "Groupes";
            default: return  "Messages";
        }
    }
}
