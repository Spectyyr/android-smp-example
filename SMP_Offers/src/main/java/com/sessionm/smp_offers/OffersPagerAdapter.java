package com.sessionm.smp_offers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by pmattheis on 10/2/17.
 */

class OffersPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private final MyOffersFragment _myOffersFragment;
    private final StoreOffersFragment _storeOffersFragment;

    public OffersPagerAdapter(FragmentManager supportFragmentManager, MyOffersFragment myOffersFragment, StoreOffersFragment storeOffersFragment) {
        super(supportFragmentManager);

        _myOffersFragment = myOffersFragment;
        _storeOffersFragment = storeOffersFragment;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "My Offers";
            case 1:
                return "Reward Store";
            default:
                return "Extra Tab";
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return _myOffersFragment;
            case 1:
                return _storeOffersFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
