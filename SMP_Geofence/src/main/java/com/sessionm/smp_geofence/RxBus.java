package com.sessionm.smp_geofence;

import com.sessionm.geofence.api.data.GeofenceEvent;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

public class RxBus {
    private static RxBus instance;

    private PublishSubject<String> subject = PublishSubject.create();
    private PublishSubject<GeofenceLog> logSubject = PublishSubject.create();
    private PublishSubject<List<GeofenceEvent>> geofenceSubject = PublishSubject.create();

    public static RxBus getInstance() {
        if (instance == null) {
            instance = new RxBus();
        }
        return instance;
    }

    public void setString(String string) {
        subject.onNext(string);
    }

    public Observable<String> getStringObservable() {
        return subject;
    }

    public void setGeofenceList(List<GeofenceEvent> geofenceEvents) {
        geofenceSubject.onNext(geofenceEvents);
    }

    public Observable<List<GeofenceEvent>> getGeofenceEventsObservable() {
        return geofenceSubject;
    }

    public void setLog(GeofenceLog log) {
        logSubject.onNext(log);
    }

    public Observable<GeofenceLog> getLogObservable() {
        return logSubject;
    }
}
