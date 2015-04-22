package it114112tm1415fyp.com.util;

import android.content.Context;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

public class PublicData {
    public static DataList regionList = new DataList();
    public static ArrayList<AddressItem> shopsList = new ArrayList<>();
    public static ArrayList<String> receiveTimeSegments  = new ArrayList<>();
    public static ArrayList<AddressItem> receiverAddressList = new ArrayList<>();
    public static ArrayList<Date> dateTimeList = new ArrayList<>();
    public static JSONArray UserAddresses;

    public static String username;
    public static String password;
    public static String userId;
    public static String realName;
    public static String email;
    public static String phone;
    public static String register_time;
    public static String last_modify_time;
    public static int logined;

    // Check network state
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static void resetUserDate() {
        PublicData.logined = 0;
        PublicData.username = "";
        PublicData.password = "";
        PublicData.userId = "";
        PublicData.realName = "";
        PublicData.phone = "";
        PublicData.email = "";
        PublicData.register_time = "";
        PublicData.last_modify_time = "";
    }
}
