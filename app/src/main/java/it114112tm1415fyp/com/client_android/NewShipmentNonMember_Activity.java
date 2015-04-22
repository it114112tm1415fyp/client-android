package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import it114112tm1415fyp.com.util.DataList;
import it114112tm1415fyp.com.util.PublicData;

public class NewShipmentNonMember_Activity extends Activity {

    private ProgressDialog progressDialog;

    private RelativeLayout layRelativeReceiverAddress;
    private ListView lstViewShopAddress;
    private EditText edTxTRealName, edTxtEmail, edTxtPhone, edTxtSubAddress1, edTxtSubAddress2, edTxtSubAddress3;
    private Spinner spnnrRegion;
    private Button btnClear, btnNext, btnAddress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PublicData.receiverAddressList = new ArrayList<>();
        NewShipment_Activity.receiverType = "nonmember";
        if (PublicData.regionList.size() == 0) {
            // Check network state
            if (!PublicData.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                finish();
            }
            progressDialog = ProgressDialog.show(getApplicationContext(), "Loading...", "Waiting for server response", true);
            new Thread(sendGetRegionListRequest).start();
        } else {
            setContentView(R.layout.new_shipment_nonmember);
            findAllView();
            setAllView();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void findAllView() {
        layRelativeReceiverAddress = (RelativeLayout) findViewById(R.id.layRelativeReceiverAddress);
        lstViewShopAddress = (ListView) findViewById(R.id.lstShopAddress);
        btnAddress = (Button) findViewById(R.id.btnAddress);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnClear = (Button) findViewById(R.id.btnClear);
        edTxTRealName = (EditText) findViewById(R.id.edTxtRealName);
        edTxtEmail = (EditText) findViewById(R.id.edTxtEmail);
        edTxtPhone = (EditText) findViewById(R.id.edTxtPhone);
        edTxtSubAddress1 = (EditText) findViewById(R.id.edTxtSubAddress1);
        edTxtSubAddress2 = (EditText) findViewById(R.id.edTxtSubAddress2);
        edTxtSubAddress3 = (EditText) findViewById(R.id.edTxtSubAddress3);
        spnnrRegion = (Spinner) findViewById(R.id.spnnrRegion);
    }

    private void setAllView() {
        lstViewShopAddress.setVisibility(View.INVISIBLE);
        btnAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lstViewShopAddress.getVisibility() == View.INVISIBLE) {
                    lstViewShopAddress.setVisibility(View.VISIBLE);
                    layRelativeReceiverAddress.setVisibility(View.INVISIBLE);
                    btnAddress.setText("Input Address");
                } else {
                    lstViewShopAddress.setVisibility(View.INVISIBLE);
                    layRelativeReceiverAddress.setVisibility(View.VISIBLE);
                    btnAddress.setText("Choose Shop");
                }
            }
        });
        final DataList region = new DataList(PublicData.regionList);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", 0);
        map.put("name", "--");
        region.add(0, map);
        final SimpleAdapter simpleAdapter = new SimpleAdapter(
                getApplicationContext(), region, android.R.layout.simple_list_item_1,
                new String[]{"name"}, new int[]{android.R.id.text1}) {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setBackgroundColor(Color.WHITE);
                ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
                return view;
            }
        };
        spnnrRegion.setAdapter(simpleAdapter);
        spnnrRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    if (region.size() > PublicData.regionList.size()) {
                        region.remove(0);
                        simpleAdapter.notifyDataSetChanged();
                        spnnrRegion.setSelection(position - 1);
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edTxtSubAddress1.setText("");
                edTxtSubAddress2.setText("");
                edTxtSubAddress3.setText("");
                spnnrRegion.setSelection(0);
            }
        });
        ArrayList<String> shopsList = new ArrayList<>();
        for (int i = 0; i < PublicData.shopsList.size(); i++) {
            shopsList.add(PublicData.shopsList.get(i).getShortAddress());
        }
        ArrayAdapter<String> shopAddressAdapter = new ArrayAdapter<String>(
                getApplicationContext(), android.R.layout.simple_list_item_1, shopsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(14);
                textView.setPadding(3, 3, 3, 3);
                return textView;
            }
        };
        lstViewShopAddress.setAdapter(shopAddressAdapter);
        lstViewShopAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String receiverRealName = edTxTRealName.getText().toString();
                String receiverEmail = edTxtEmail.getText().toString();
                String receiverPhone = edTxtPhone.getText().toString();
                // Check receiver data
                if (receiverRealName.equals("") || receiverEmail.equals("") || receiverPhone.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please finish the form!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check phone format
                receiverPhone = "+852-" + receiverPhone;
                String phoneFormat = getResources().getString(R.string.format_phone);
                if (!receiverPhone.matches(phoneFormat)) {
                    Toast.makeText(getApplicationContext(), "Phone is invalid!", Toast.LENGTH_SHORT).show();
                    return;
                }
                NewShipment_Activity.receiverRealName = receiverRealName;
                NewShipment_Activity.receiverEmail = receiverEmail;
                NewShipment_Activity.receiverPhone = receiverPhone;
                NewShipment_Activity.destinationId = PublicData.shopsList.get(position).getAddressId();
                NewShipment_Activity.destinationType = "Shop";
                NewShipment_Activity.destination = PublicData.shopsList.get(position).getShortAddress();
                Intent i = new Intent(getApplicationContext(), ShipmentDepartureList_Activity.class);
                i.putExtra("activity", "NewOrder");
                startActivityForResult(i, 1);
            }
        });
        btnNext.setOnClickListener(onClickListener);

    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String receiverRealName = edTxTRealName.getText().toString();
            String receiverEmail = edTxtEmail.getText().toString();
            String receiverPhone = edTxtPhone.getText().toString();
            // Check receiver data
            if (receiverRealName.equals("") || receiverEmail.equals("") || receiverPhone.equals("")) {
                Toast.makeText(getApplicationContext(), "Please finish the form!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Check phone format
            receiverPhone = "+852-" + receiverPhone;
            String phoneFormat = getResources().getString(R.string.format_phone);
            if (!receiverPhone.matches(phoneFormat)) {
                Toast.makeText(getApplicationContext(), "Phone is invalid!", Toast.LENGTH_SHORT).show();
                return;
            }
            switch (v.getId()) {
                case R.id.btnNext:      // Choose receiver's address as destination
                    String subAddress1 = edTxtSubAddress1.getText().toString();
                    String subAddress2 = edTxtSubAddress2.getText().toString();
                    String subAddress3 = edTxtSubAddress3.getText().toString();
                    String destination = "";
                    if (!(subAddress1.equals("") || subAddress2.equals("") || subAddress3.equals(""))) {
                        if (!subAddress1.substring(subAddress1.length()).equals(","))
                            subAddress1 += ",\n";
                        if (!subAddress2.substring(subAddress2.length()).equals(","))
                            subAddress2 += ",\n";
                        destination = subAddress1 + subAddress2 + subAddress3;
                    } else {
                        Toast.makeText(getApplicationContext(), "Please finish the receiver address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    NewShipment_Activity.receiverRealName = receiverRealName;
                    NewShipment_Activity.receiverEmail = receiverEmail;
                    NewShipment_Activity.receiverPhone = receiverPhone;
                    NewShipment_Activity.destination = destination;
                    NewShipment_Activity.destinationType = "SpecifyAddress";
                    // Check network state
                    if (!PublicData.isConnected(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Send get specify address Id request
                    progressDialog = ProgressDialog.show(NewShipmentNonMember_Activity.this, "Loading...", "Getting Details...", true);
                    new Thread(sendGetSpecifyAddressIdRequest).start();
                    break;
                default:
                    break;
            }
        }
    };

    Thread sendGetSpecifyAddressIdRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.getSpecifyAddressId(NewShipment_Activity.destination, 1);
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        NewShipment_Activity.destinationId = result.getJSONObject("content").getInt("id");
                        Intent i = new Intent(getApplicationContext(), ShipmentDepartureList_Activity.class);
                        i.putExtra("activity", "NewOrder");
                        startActivityForResult(i, 1);
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Fail to server. \nPlease try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error \nPlease try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setContentView(R.layout.new_shipment_nonmember);
                                findAllView();
                                setAllView();
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
            } catch (final Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error \nPlease try again later.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } finally {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }
    };
}

