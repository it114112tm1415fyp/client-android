package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import it114112tm1415fyp.com.util.AddressItem;
import it114112tm1415fyp.com.util.PublicData;

public class NewShipment_Activity extends Activity {

    private Button btnNonMember, btnUser;

    public static String receiverType, receiverUsername, receiverPhone, receiverRealName, receiverEmail, departureType, departure, destinationType, destination;
    public static int receiverId, departureId, destinationId, goodsNumber;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_shipment);
        new Thread(SendGeInformationRequest).start();
        findAllView();
        setAllView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            ShipmentsList_Activity.NEED_UPDATE = 1;
        }
    }

    public void findAllView() {
        btnNonMember = (Button) findViewById(R.id.btnNonUser);
        btnUser = (Button) findViewById(R.id.btnUser);
    }

    public void setAllView() {
        btnUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivityForResult(new Intent(getApplicationContext(), NewShipmentMember_Activity.class), 1);
            }
        });
        btnNonMember.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivityForResult(new Intent(getApplicationContext(), NewShipmentNonMember_Activity.class), 1);
            }
        });
    }

    Thread SendGeInformationRequest = new Thread() {
        public void run() {
            try {
                if (PublicData.shopsList.size() == 0) {
                    final JSONObject shopResult = HTTP.getLocationList();
                    if (shopResult != null) {
                        if (shopResult.getBoolean("success")) {
                            JSONArray shops = shopResult.getJSONObject("content").getJSONArray("shops");
                            PublicData.shopsList.clear();
                            for (int i = 0; i < shops.length(); i++) {
                                JSONObject shopObject = shops.getJSONObject(i);
                                PublicData.shopsList.add(
                                        new AddressItem(shopObject.getInt("id"),
                                                shopObject.getString("short_name"),
                                                shopObject.getString("long_name"),
                                                shopObject.getJSONObject("region").getInt("id"),
                                                shopObject.getJSONObject("region").getString("name")));
                            }
                        }
                    }
                }
                if (PublicData.regionList.size() == 0) {
                    final JSONObject regionResult = HTTP.getRegionList();
                    if (regionResult != null) {
                        if (regionResult.getBoolean("success")) {
                            JSONArray regionList = regionResult.getJSONObject("content").getJSONArray("list");
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
