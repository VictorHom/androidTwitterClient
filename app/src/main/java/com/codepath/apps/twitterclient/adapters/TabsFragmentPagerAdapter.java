package com.codepath.apps.twitterclient.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.codepath.apps.twitterclient.fragments.TimelineFragment;
import com.codepath.apps.twitterclient.utils.SmartFragmentStatePagerAdapter;

/**
 * Created by victorhom on 11/5/16.
 */
public class TabsFragmentPagerAdapter extends SmartFragmentStatePagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "All", "Mentions"};
    private Context context;

    public TabsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getItemPosition(Object object) {
        int position = super.getItemPosition(object);
        return position;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        // 0 is the home timeline
        // 1 is the mentions timeline
        if (position == 0) {
            return TimelineFragment.newInstance(position + 1);
        } else {
            return TimelineFragment.newInstance(position + 1);
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}
