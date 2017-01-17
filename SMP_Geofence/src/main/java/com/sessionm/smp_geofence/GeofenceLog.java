package com.sessionm.smp_geofence;

public class GeofenceLog {
    private String name;
    private String timeStamp;
    private String message;

    public GeofenceLog(String name, String timeStamp, String message) {
        this.name = name;
        this.timeStamp = timeStamp;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
