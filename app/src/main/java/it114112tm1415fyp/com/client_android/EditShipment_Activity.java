package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import it114112tm1415fyp.com.util.AddressItem;
import it114112tm1415fyp.com.util.PublicData;

public class EditShipment_Activity extends Activity {

    public static String departure, destination, departureType, destinationType;
    public static int orderId, departureId, destinationId;
    public static boolean departureChange, destinationChange = false;

    private DataBaseHelper helper;

    private ProgressDialog progressDialog;
    private TableRow tableRowDeparture, tableRowDestination;
    private TextView tvtViewOrderId, txtViewSender, txtViewReceiver, txtViewDeparture, txtViewDestination;
    private EditText editTxtNumberOfGoods;
    private Button btnSubmit;

    public static ArrayList timeList = new ArrayList();
    private String sender, receiver;
    private int numberOfGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_shipment);
        departureChange = destinationChange = false;
        if (!PublicData.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (PublicData.regionList.size() == 0)
            new Thread(sendGetRegionListRequest).start();
        if (PublicData.shopsList.size() == 0)
            new Thread(sendGetShopListRequest).start();
        helper = new DataBaseHelper(getApplicationContext());
        orderId = getIntent().getIntExtra("orderId", 0);
        findAllView();
        getOrderMoreDetailsFromDataBase(orderId);
        setAllView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            updateView();
        }
    }

    private void findAllView() {
        tableRowDeparture = (TableRow) findViewById(R.id.tableRowDeparture);
        tableRowDestination = (TableRow) findViewById(R.id.tableRowDestination);
        tvtViewOrderId = (TextView) findViewById(R.id.txtViewOrderId);
        txtViewSender = (TextView) findViewById(R.id.txtViewSender);
        txtViewReceiver = (TextView) findViewById(R.id.txtViewReceiver);
        txtViewDeparture = (TextView) findViewById(R.id.txtViewDeparture);
        txtViewDestination = (TextView) findViewById(R.id.txtViewDestination);
        editTxtNumberOfGoods = (EditText) findViewById(R.id.edTxtNumberOfGoods);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
    }

    private void setAllView() {
        updateView();
        tableRowDeparture.setOnClickListener(onClickListener);
        tableRowDestination.setOnClickListener(onClickListener);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfGoods = Integer.parseInt(editTxtNumberOfGoods.getText().toString());
                if (numberOfGoods == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter number of goods", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!PublicData.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (progressDialog == null)
                    progressDialog = ProgressDialog.show(EditShipment_Activity.this, "Loading...", "Waiting for server response", true);
                new Thread(sendEditOrderRequest).start();
            }
        });
    }

    private void updateView() {
        if (destinationChange)
            tableRowDestination.setBackgroundColor(Color.parseColor("#FBFF76"));
        if (departureChange)
            tableRowDeparture.setBackgroundColor(Color.parseColor("#FBFF76"));
        txtViewDeparture.setText(departure);
        txtViewDestination.setText(destination);
        tvtViewOrderId.setText(orderId + "");
        txtViewSender.setText(sender);
        editTxtNumberOfGoods.setText(numberOfGoods + "");
        txtViewReceiver.setText(receiver);
    }

    private void getOrderMoreDetailsFromDataBase(int orderId) {
        Cursor cursor = helper.getOrderDetails(orderId);
        cursor.moveToFirst();
        try {
            JSONObject senderObject = new JSONObject(cursor.getString(0));
            sender = senderObject.getString("name");
            JSONObject receiverObject = new JSONObject(cursor.getString(1));
            receiver = receiverObject.getString("name");
            PublicData.receiverAddressList.clear();
            JSONArray address = receiverObject.getJSONArray("specify_addresses");
            PublicData.receiverAddressList = new ArrayList<>();
            for (int i = 0; i < address.length(); i++) {
                JSONObject addressObject = address.getJSONObject(i);
                JSONObject regionObject = addressObject.getJSONObject("region");
                AddressItem addressItem = new AddressItem(
                        addressObject.getInt("id"), addressObject.getString("short_name"),
                        addressObject.getString("long_name"),
                        regionObject.getInt("id") - 1, regionObject.getString("name"));
                PublicData.receiverAddressList.add(addressItem);
            }
            JSONObject departureObject = new JSONObject(cursor.getString(2));
            if (departureObject.getString("type").equals("SpecifyAddress"))
                departure = departureObject.getString("long_name");
            else
                departure = departureObject.getString("short_name");
            departureId = departureObject.getInt("id");
            departureType = departureObject.getString("type");
            JSONObject destinationObject = new JSONObject(cursor.getString(3));
            if (destinationObject.getString("type").equals("SpecifyAddress"))
                destination = destinationObject.getString("long_name");
            else
                destination = destinationObject.getString("short_name");
            destinationId = destinationObject.getInt("id");
            destinationType = destinationObject.getString("type");
            numberOfGoods = cursor.getInt(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!PublicData.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (PublicData.regionList.size() == 0)
                new Thread(sendGetRegionListRequest).start();
            if (PublicData.shopsList.size() == 0)
                new Thread(sendGetShopListRequest).start();
            Intent i = new Intent();
            switch (v.getId()) {
                case R.id.tableRowDeparture:
                    i = new Intent(getApplicationContext(), ShipmentDepartureList_Activity.class);
                    break;
                case R.id.tableRowDestination:
                    i = new Intent(getApplicationContext(), ShipmentDestinationList_Activity.class);
                    break;
            }
            i.putExtra("activity", "EditOrder");
            startActivityForResult(i, 1);
        }
    };

    Thread sendGetShopListRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.getLocationList();
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        JSONArray shops = result.getJSONObject("content").getJSONArray("shops");
                        PublicData.shopsList = new ArrayList<>();
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
                        JSONArray list = result.getJSONObject("content").getJSONArray("list");
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

    Thread sendEditOrderRequest = new Thread() {
        public void run() {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("order_id", orderId + "");
            parameters.put("goods_number", numberOfGoods + "");
            if (departureChange) {
                parameters.put("departure_id", departureId + "");
                parameters.put("departure_type", departureType);
            }
            if (destinationChange) {
                parameters.put("destination_id", destinationId + "");
                parameters.put("destination_type", destinationType);
            }
            JSONObject result;
            if (departureChange && departureType.equals("SpecifyAddress"))
                result = HTTP.editOrder(parameters, timeList);
            else
                result = HTTP.editOrder(parameters, null);
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Order Edit Success", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    //Toast.makeText(getApplicationContext(), result.getString("error"), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Fail to server. \nPlease try again later.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }
    };

}
