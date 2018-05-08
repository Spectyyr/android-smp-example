package com.sessionm.smp_offers.my_offers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sessionm.core.api.SessionMError;
import com.sessionm.offer.api.OffersManager;
import com.sessionm.offer.api.data.user.UserOffersFetchedResponse;
import com.sessionm.smp_offers.R;

import static android.support.v7.widget.RecyclerView.HORIZONTAL;

public class MyOffersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final OffersManager offerManager = OffersManager.getInstance();


    private SwipeRefreshLayout _swipeRefreshLayout;
    private MyOffersRecAdapter _offersRecAdapter;
    private RecyclerView _recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_offers_fragment, container, false);
        ViewCompat.setElevation(rootView, 50);

        _swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        _swipeRefreshLayout.setOnRefreshListener(this);

        _recyclerView = rootView.findViewById(R.id.offers_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        _recyclerView.addItemDecoration(itemDecor);

        _offersRecAdapter = new MyOffersRecAdapter(this);
        _recyclerView.setAdapter(_offersRecAdapter);

        return rootView;
    }

    @Override
    public void onRefresh() {
        fetchOffers();
    }

    public void fetchOffers() {
        if (_swipeRefreshLayout != null) {
            _swipeRefreshLayout.setRefreshing(true);
        }

        offerManager.fetchUserOffers(new OffersManager.OnUserOffersFetched() {
            @Override
            public void onFetched(UserOffersFetchedResponse userOffersFetchedResponse, SessionMError sessionMError) {
                if (_swipeRefreshLayout.isRefreshing()) {
                    _swipeRefreshLayout.setRefreshing(false);
                }
                if (sessionMError != null) {
                    Toast.makeText(MyOffersFragment.this.getContext(), "Failure: '" + sessionMError.getCode() + "' " + sessionMError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    _offersRecAdapter.setOffers(userOffersFetchedResponse.getUserOffers());
                }
            }
        });
    }
}
