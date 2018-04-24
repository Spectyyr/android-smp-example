package com.sessionm.smp_geofence;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LogsFragment extends Fragment {

    private RecyclerView _recyclerView;
    private LogsRecAdapter _recyclerAdapter;
    private Subscription subscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logs, container, false);

        final List<GeofenceLog> items = new ArrayList<>();

        _recyclerView = v.findViewById(R.id.logs_recylerview);
        _recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        _recyclerView.setLayoutManager(llm);
        _recyclerAdapter = new LogsRecAdapter(this, items);
        _recyclerView.setAdapter(_recyclerAdapter);

        subscription = RxBus.getInstance().getLogObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<GeofenceLog>() {
            @Override
            public void call(GeofenceLog log) {
                items.add(0, log);
                _recyclerAdapter.notifyDataSetChanged();
            }
        });

        return v;
    }

    public static LogsFragment newInstance(String text) {
        LogsFragment f = new LogsFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }
}
