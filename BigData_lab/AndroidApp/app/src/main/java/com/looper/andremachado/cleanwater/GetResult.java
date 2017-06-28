package com.looper.andremachado.cleanwater;

/**
 * Created by MichaelShen on 2017/6/23.
 */

public class GetResult {
    private static String username;
    private static String first_name;
    private static String last_name;
    private static String longitude;
    private static String latitude;
    private static String qrCodeId;
    private static String time ;
    private static String email;

    public GetResult(String username, String first_name, String last_name, String longitude, String latitude, String qrCodeId, String time, String email){
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.qrCodeId = qrCodeId;
        this.time = time;
        this.email = email;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        GetResult.username = username;
    }

    public static String getFirst_name() {
        return first_name;
    }

    public static void setFirst_name(String first_name) {
        GetResult.first_name = first_name;
    }

    public static String getLast_name() {
        return last_name;
    }

    public static void setLast_name(String last_name) {
        GetResult.last_name = last_name;
    }

    public static String getLongitude() {
        return longitude;
    }

    public static void setLongitude(String longitude) {
        GetResult.longitude = longitude;
    }

    public static String getLatitude() {
        return latitude;
    }

    public static void setLatitude(String latitude) {
        GetResult.latitude = latitude;
    }

    public static String getQrCodeId() {
        return qrCodeId;
    }

    public static void setQrCodeId(String qrCodeId) {
        GetResult.qrCodeId = qrCodeId;
    }

    public static String getTime() {
        return time;
    }

    public static void setTime(String time) {
        GetResult.time = time;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        GetResult.email = email;
    }
}
