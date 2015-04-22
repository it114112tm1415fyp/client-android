package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import it114112tm1415fyp.com.util.PublicData;

public class Home_Activity extends Activity {

    private SharedPreferences sharePref;

    private LinearLayout layLinearNewShipment, layLinearMyShipments, layLinearOther, layLinearUserProfile;
    private ImageView imgViewNewShipment, imgViewMyShipment, imgViewOther, imgViewUserProfile;

    private String username, password;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        sharePref = getSharedPreferences("Logistic_System", Context.MODE_PRIVATE);
        username = sharePref.getString("username", "");
        password = sharePref.getString("password", "");
        if (sharePref.getInt("saveMe", 0) == 1 && !((username.equals("")) && (password.equals("")))) {
            PublicData.username = username;
            PublicData.password = password;
            new Thread(sendLoginRequest).start();
        }
        if (PublicData.regionList.size() == 0)
            new Thread(sendGetRegionListRequest).start();
        if (PublicData.receiveTimeSegments.size() == 0)
            new Thread(sendGetReceiveTimeSegmentsRequest).start();
        findAllView();
        setAllView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sharePref.getInt("saveMe", 0) == 0) {
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putString("username", "");
            editor.putString("password", "");
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        if (PublicData.logined == 1) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home_Activity.this);
            alertDialog.setTitle("Do you want exit?");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        } else
            super.onBackPressed();
    }

    public void findAllView() {
        layLinearMyShipments = (LinearLayout) findViewById(R.id.layLinearMyShipments);
        layLinearNewShipment = (LinearLayout) findViewById(R.id.layLinearNewShipment);
        layLinearOther = (LinearLayout) findViewById(R.id.layLinearOther);
        layLinearUserProfile = (LinearLayout) findViewById(R.id.layLinearUserProfile);
        imgViewMyShipment = (ImageView) findViewById(R.id.imgViewMyShipments);
        imgViewNewShipment = (ImageView) findViewById(R.id.imgViewNewShipment);
        imgViewOther = (ImageView) findViewById(R.id.imgViewOther);
        imgViewUserProfile = (ImageView) findViewById(R.id.imgViewUserProfile);
    }

    public void setAllView() {
        layLinearNewShipment.setOnClickListener(onClickListener);
        layLinearMyShipments.setOnClickListener(onClickListener);
        layLinearOther.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CustomerService.class));
            }
        });
        layLinearUserProfile.setOnClickListener(onClickListener);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int intScreenWidth = dm.widthPixels;
        ViewGroup.LayoutParams layoutParams = imgViewUserProfile.getLayoutParams();  //需import android.view.ViewGroup.LayoutParams;
        layoutParams.width = intScreenWidth / 2 - 50;
        layoutParams.height = intScreenWidth / 2 - 50;
        imgViewUserProfile.setLayoutParams(layoutParams);
        imgViewOther.setLayoutParams(layoutParams);
        imgViewNewShipment.setLayoutParams(layoutParams);
        imgViewMyShipment.setLayoutParams(layoutParams);
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Check login or not ? Go to specify Activity : Go to login Activity
            if (PublicData.logined == 1) {
                switch (v.getId()) {
                    case R.id.layLinearNewShipment:
                        startActivity(new Intent(Home_Activity.this, NewShipment_Activity.class));
                        break;
                    case R.id.layLinearMyShipments:
                        startActivity(new Intent(Home_Activity.this, ShipmentsList_Activity.class));
                        break;
                    case R.id.layLinearUserProfile:
                        startActivity(new Intent(Home_Activity.this, UserProfile_Activity.class));
                        break;
                    default:
                        break;
                }
            } else {
                if (v.getId() != R.id.layLinearUserProfile) {
                    Toast.makeText(Home_Activity.this, "Login For More Function", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(Home_Activity.this, Login_Activity.class));
            }
        }
    };

    Thread sendLoginRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.login(username, password);
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        PublicData.logined = 1;
                        JSONObject content = result.getJSONObject("content");
                        PublicData.userId = content.getString("id");
                        PublicData.realName = content.getString("name");
                        PublicData.email = content.getString("email");
                        PublicData.phone = content.getString("phone");
                        PublicData.register_time = DateFormator.getDateTime(content.getString("created_at"));
                        PublicData.last_modify_time = DateFormator.getDateTime(content.getString("updated_at"));
                        JSONArray addressesList = content.getJSONArray("specify_addresses");
                        PublicData.UserAddresses = addressesList;
                        SharedPreferences.Editor editor = sharePref.edit();
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.apply();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Welcome, " + username, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    Toast.makeText(getApplicationContext(), result.getString("error"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Thread sendGetRegionListRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.getRegionList();
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        JSONArray regionList = result.getJSONObject("content").getJSONArray("list");
                        PublicData.regionList.clear();
                        for (int i = 0; i < regionList.length(); i++) {
                            JSONObject region = regionList.getJSONObject(i);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", region.getInt("id"));
                            map.put("name", region.getString("name"));
                            PublicData.regionList.add(map);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Thread sendGetReceiveTimeSegmentsRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.getReceiveTimeSegments();
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        JSONArray timeSegments = result.getJSONArray("content");
                        for (int i = 0; i < timeSegments.length(); i++) {
                            JSONObject timeSegmentsObject = timeSegments.getJSONObject(i);
                            PublicData.receiveTimeSegments.add(
                                    DateFormator.getShortTime(timeSegmentsObject.getString("start_time")) + "\n-\n"
                                            + DateFormator.getShortTime(timeSegmentsObject.getString("end_time")));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
