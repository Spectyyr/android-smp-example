package com.sessionm.smp_geofence;

import java.util.Date;

public class GeofenceLog {
    private long timeStamp;
    private String name;
    private String message;

    public GeofenceLog(String name, String message) {
        this.timeStamp = System.currentTimeMillis();
        this.name = name;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return Long.toString(timeStamp);
    }

    public String getDisplayTime() {
        Date date = new Date(timeStamp);
        return date.toString();
    }

    public String getName() {
        return name;
    }
}
