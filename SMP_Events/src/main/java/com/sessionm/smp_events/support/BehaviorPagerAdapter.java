/*
 * Copyright (c) 2016 SessionM. All rights reserved.
 */

package com.sessionm.smp_events.support;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sessionm.smp_events.R;

public class BehaviorPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    public BehaviorPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    BehaviorFragment _behaviorFragment = new BehaviorFragment();
    EventsFragment _eventsFragment = new EventsFragment();

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return _behaviorFragment;
            case 1:
                return _eventsFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Behavior Order";
            case 1:
                return "Events Order";
            default:
                return "";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void setBehaviors(BehaviorList behaviorList) {
        if (behaviorList == null) {
            //TODO: Clean list
            return;
        }

        _eventsFragment.setBehaviors(behaviorList);
        _behaviorFragment.setBehaviors(behaviorList);
    }

    public static class BehaviorFragment extends Fragment {
        private TextView _list;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.behavior_fragment, container, false);
            _list = v.findViewById(R.id.behavior_list);

            return v;
        }

        public void setBehaviors(BehaviorList behaviorList) {
            if (behaviorList != null) {
                behaviorList.show(BehaviorList.ByType.Behavior, _list);
            }
        }
    }

    public static class EventsFragment extends Fragment {
        private TextView _list;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.event_fragment, container, false);
            _list = v.findViewById(R.id.event_list);

            return v;
        }

        public void setBehaviors(BehaviorList behaviorList) {
            if (behaviorList != null) {
                behaviorList.show(BehaviorList.ByType.Event, _list);
            }
        }
    }
}