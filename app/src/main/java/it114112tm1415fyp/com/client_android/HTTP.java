package it114112tm1415fyp.com.client_android;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import it114112tm1415fyp.com.util.AddressItem;
import it114112tm1415fyp.com.util.PublicData;

public final class HTTP {

    public final static HashMap<String, String> cookie = new HashMap<String, String>();

    private final static String ServerUrl = "http://it114112tm1415fyp1.redirectme.net:6083/";


    public final static JSONObject request(String postposition, HashMap<String, String> parameters) {
        return request(postposition, parameters, "");
    }

    public final static JSONObject request(String postposition, HashMap<String, String> parameters,
                                           String formatedParameters) {
        return request(postposition, parameters, formatedParameters, true);
    }

    public final static JSONObject request(String postposition, HashMap<String, String> parameters,
                                           boolean post) {
        return request(postposition, parameters, "", post);
    }

    public final static JSONObject request(String postposition, HashMap<String, String> parameters,
                                           String formatedParameters, boolean post) {
        URL url = null;
        String formatedParameter = "";
        try {
            if (parameters != null && parameters.size() != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (HashMap.Entry<String, String> entry : parameters.entrySet()) {
                    stringBuilder.append(URLEncoder.encode(entry.getKey(), "utf-8")).append('=');
                    stringBuilder.append(URLEncoder.encode(entry.getValue(), "utf-8")).append('&');
                }
                stringBuilder.append(formatedParameters);
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                formatedParameter = stringBuilder.toString();
            }
            url = new URL(ServerUrl + postposition
                    + (!post && formatedParameter.length() > 0 ? "?" + formatedParameter : ""));
            Log.d("URL", url.toString());
            Log.d("Parameters", formatedParameter);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if (cookie.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (HashMap.Entry<String, String> entry : cookie.entrySet()) {
                    stringBuilder.append(entry.getKey()).append('=');
                    stringBuilder.append(entry.getValue()).append("; ");
                }
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                httpURLConnection.setRequestProperty("cookie", stringBuilder.toString());
                Log.d("cookie", stringBuilder.toString());
            } else {
                Log.d("cookie", "no cookie");
            }
            if (post) {
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                httpURLConnection.connect();
                DataOutputStream dataOutputStream = new DataOutputStream(
                        httpURLConnection.getOutputStream());
                dataOutputStream.writeBytes(formatedParameter);
            }
            List<String> header_set_cookie = httpURLConnection.getHeaderFields().get("Set-Cookie");
            if (header_set_cookie != null) {
                for (String x : header_set_cookie) {
                    System.out.println(x);
                    String key = x.substring(0, x.indexOf("="));
                    String value = x.substring(x.indexOf("=") + 1, x.indexOf(";"));
                    cookie.put(key, value);
                }
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    httpURLConnection.getInputStream()));
            String result = bufferedReader.readLine();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result += '\n' + line;
            }
            bufferedReader.close();
            Log.d("result", result);
            JSONObject resultObject = new JSONObject(result);
            if (!checkResult(resultObject))
                return request(postposition, parameters, formatedParameters, post);
            else
                return resultObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Boolean checkResult(JSONObject result) throws JSONException {
        if (result.has("success")) {
            if (!result.getBoolean("success")) {
                if (result.getString("error").equals("Connection expired")) {
                    login(PublicData.username, PublicData.password);
                    return false;
                }
                if (result.getString("error").equals("Login require")) {
                    login(PublicData.username, PublicData.password);
                    return false;
                }
            }
        }
        return true;
    }

    private static String md5(String s) {
        try {
            final char md5Chars[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                    '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(s.getBytes());
            byte[] bytes = messageDigest.digest();
            int length = messageDigest.digest().length;
            StringBuffer stringbuffer = new StringBuffer(2 * length);
            for (int t = 0; t < length; t++) {
                stringbuffer.append(md5Chars[(bytes[t] & 0xf0) >> 4]);
                stringbuffer.append(md5Chars[bytes[t] & 0xf]);
            }
            return stringbuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static JSONObject login(String username, String password) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", md5(md5(password)));
        return request("account/customer_login", parameters);
    }

    public static JSONObject register(String username, String password, String realName,
                                      String email, String phone, ArrayList<AddressItem> addressList) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", md5(md5(password)));
        parameters.put("name", realName);
        parameters.put("email", email);
        parameters.put("phone", phone);
        StringBuilder formatedParameter = new StringBuilder();
        for (int i = 0; i < addressList.size(); i++) {
            try {
                formatedParameter.append(URLEncoder.encode("addresses[][address]", "utf-8"));
                formatedParameter.append("=");
                formatedParameter.append(URLEncoder.encode(addressList.get(i).shortAddress, "utf-8"));
                formatedParameter.append("&");
                formatedParameter.append(URLEncoder.encode("addresses[][region_id]", "utf-8"));
                formatedParameter.append("=");
                formatedParameter.append(URLEncoder.encode(addressList.get(i).regionId + "", "utf-8"));
                formatedParameter.append("&");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return request("account/register", parameters, formatedParameter.toString());
    }

    public static JSONObject editProfile(String password, String newPassword, String realName,
                                         String phone, String email,
                                         ArrayList<AddressItem> addressList) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("password", md5(md5(password)));
        if (!newPassword.equals(""))
            parameters.put("new_password", md5(md5(newPassword)));
        parameters.put("name", realName);
        parameters.put("email", email);
        parameters.put("phone", phone);
        StringBuilder formatedParameter = new StringBuilder();
        for (int i = 0; i < addressList.size(); i++) {
            try {
                formatedParameter.append(URLEncoder.encode("addresses[][address]", "utf-8"));
                formatedParameter.append("=");
                formatedParameter.append(URLEncoder.encode(addressList.get(i).shortAddress, "utf-8"));
                formatedParameter.append("&");
                formatedParameter.append(URLEncoder.encode("addresses[][region_id]", "utf-8"));
                formatedParameter.append("=");
                formatedParameter.append(URLEncoder.encode(addressList.get(i).regionId + "", "utf-8"));
                formatedParameter.append("&");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return request("account/edit_profile", parameters, formatedParameter.toString());
    }

    public static JSONObject getLocationList() {
        HashMap<String, String> parameters = new HashMap<>();
        return request("location/get_list", parameters);
    }

    public static JSONObject getRegionList() {
        HashMap<String, String> parameters = new HashMap<>();
        return request("region/get_list", parameters);
    }

    public static JSONObject findUserInfo(String username, String phone) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("phone", phone);
        return request("account/find_user_info", parameters);
    }

    public static JSONObject getSpecifyAddressId(String address, int regionId) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("address", address);
        parameters.put("region_id", regionId + "");
        return request("location/get_specify_address_id", parameters);
    }

    public static JSONObject makeNewOrder(String orderType, int receiverId, String receiverRealName,
                                          String receiverEmail, String receiverPhone,
                                          int departureId, String departureType,
                                          int destinationId, String destinationType,
                                          int goodsNumber, ArrayList<Boolean> timeList) {
        HashMap<String, String> parameters = new HashMap<>();
        if (orderType.equals("member"))
            parameters.put("receiver[id]", receiverId + "");
        else {
            parameters.put("receiver[name]", receiverRealName);
            parameters.put("receiver[email]", receiverEmail);
            parameters.put("receiver[phone]", receiverPhone);
        }
        parameters.put("departure_id", departureId + "");
        parameters.put("departure_type", departureType);
        parameters.put("destination_id", destinationId + "");
        parameters.put("destination_type", destinationType);
        parameters.put("goods_number", goodsNumber + "");
        if (timeList != null) {
            StringBuilder formatedParameter = new StringBuilder();
            for (Boolean x : timeList) {
                try {
                    formatedParameter.append(URLEncoder.encode("time[]", "utf-8"));
                    formatedParameter.append("=");
                    formatedParameter.append(URLEncoder.encode(x.toString(), "utf-8"));
                    formatedParameter.append("&");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return request("order/make", parameters, formatedParameter.toString());
        }
        return request("order/make", parameters);
    }

    public static JSONObject getReceiveOrders() {
        HashMap<String, String> parameters = new HashMap<>();
        return request("order/get_receive_orders", parameters);
    }

    public static JSONObject getSendOrders() {
        HashMap<String, String> parameters = new HashMap<>();
        return request("order/get_send_orders", parameters);
    }

    public static JSONObject getOrderDetails(int orderId) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("order_id", orderId + "");
        return request("order/get_details", parameters);
    }

    public static JSONObject editOrder(HashMap<String, String> parameters, ArrayList<Boolean> timeList) {
        if (timeList != null) {
            StringBuilder formatedParameter = new StringBuilder();
            for (Boolean x : timeList) {
                try {
                    formatedParameter.append(URLEncoder.encode("time[]", "utf-8"));
                    formatedParameter.append("=");
                    formatedParameter.append(URLEncoder.encode(x.toString(), "utf-8"));
                    formatedParameter.append("&");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return request("order/edit", parameters, formatedParameter.toString());
        }
        return request("order/edit", parameters);
    }

    public static JSONObject cancelOrder(int orderId) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("order_id", orderId + "");
        return request("order/cancel", parameters);
    }

    public static JSONObject getGoodImage(String goodId) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("goods_id", goodId);
        return request("picture/goods", parameters);
    }

    public static JSONObject getGoodDetail(String goodId) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("goods_id", goodId);
        return request("goods/get_details", parameters);
    }

    public static JSONObject getOrderReceiveFreeTime(int orderId) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("order_id", orderId + "");
        return request("time/get_order_receive_free_time", parameters);
    }

    public static JSONObject getReceiveTimeSegments() {
        HashMap<String, String> parameters = new HashMap<>();
        return request("time/get_receive_time_segments", parameters);
    }

}
