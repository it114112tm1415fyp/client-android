package it114112tm1415fyp.com.client_android;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import it114112tm1415fyp.com.util.AddressItem;
import it114112tm1415fyp.com.util.DataList;
import it114112tm1415fyp.com.util.PublicData;

public class EditProfile_Activity extends Activity {

    private SharedPreferences sharePref;

    private ProgressDialog progressDialog;
    private LinearLayout layLinearAddress;
    private ImageButton imgBtnAddAddress;
    private Button btnSubmit;
    private EditText edTxtRealName, edTxtPhone, edTxtEmail, edTxtNewPassword, edTxtRePassword;
    private TextView txtViewId, txtViewUsername;

    private ArrayList<AddressItem> addressItemList;
    private String username, realName, phone, email, newPassword, rePassword;
    private int layLinearChildCount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        sharePref = getSharedPreferences("Logistic_System", Context.MODE_PRIVATE);
        username = sharePref.getString("username", "");
        realName = PublicData.realName;
        phone = PublicData.phone.substring(5);
        email = PublicData.email;
        findAllView();
        if (PublicData.regionList.size() == 0) {
            if (progressDialog == null)
                progressDialog = ProgressDialog.show(EditProfile_Activity.this, "Loading...", "Waiting for server response", true);
            new Thread(sendGetRegionListRequest).start();
        } else {
            setAllView();
        }
    }

    private void findAllView() {
        layLinearAddress = (LinearLayout) findViewById(R.id.layLinearAddress);
        txtViewId = (TextView) findViewById(R.id.txtViewUserId);
        txtViewUsername = (TextView) findViewById(R.id.txtViewUsername);
        edTxtNewPassword = (EditText) findViewById(R.id.edTxtNewPassword);
        edTxtRePassword = (EditText) findViewById(R.id.edTxtRePassword);
        edTxtRealName = (EditText) findViewById(R.id.edTxtRealName);
        edTxtPhone = (EditText) findViewById(R.id.edTxtPhone);
        edTxtEmail = (EditText) findViewById(R.id.edTxtEmail);
        imgBtnAddAddress = (ImageButton) findViewById(R.id.imgBtnAddAddress);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
    }

    private void setAllView() {
        txtViewId.setText(PublicData.userId);
        txtViewUsername.setText(username);
        edTxtRealName.setText(realName);
        edTxtPhone.setText(phone);
        edTxtEmail.setText(email);
        JSONArray addressArray = PublicData.UserAddresses;
        for (int i = 0; i < addressArray.length(); i++) {
            JSONObject addressObject;
            try {
                addressObject = addressArray.getJSONObject(i);
                JSONObject regionObject = addressObject.getJSONObject("region");
                createAddressView(addressObject.getString("short_name"), PublicData.regionList.indexOfId(regionObject.getInt("id")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        imgBtnAddAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddressView("", -1);
            }
        });
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                newPassword = edTxtNewPassword.getText().toString();
                rePassword = edTxtRePassword.getText().toString();
                realName = edTxtRealName.getText().toString();
                email = edTxtEmail.getText().toString();
                phone = edTxtPhone.getText().toString();
                addressItemList = new ArrayList<>();
                layLinearChildCount = layLinearAddress.getChildCount();

                for (int i = 0; i < layLinearChildCount; i++) {
                    EditText inner_ed1 = (EditText) layLinearAddress.getChildAt(i).findViewById(R.id.edTxtSubAddress1);
                    EditText inner_ed2 = (EditText) layLinearAddress.getChildAt(i).findViewById(R.id.edTxtSubAddress2);
                    EditText inner_ed3 = (EditText) layLinearAddress.getChildAt(i).findViewById(R.id.edTxtSubAddress3);
                    Spinner innerSpnnrRegion = (Spinner) layLinearAddress.getChildAt(i).findViewById(R.id.spnnrRegion);
                    String subAddress1 = inner_ed1.getText().toString();
                    String subAddress2 = inner_ed2.getText().toString();
                    String subAddress3 = inner_ed3.getText().toString();
                    // Check Address
                    if (subAddress1.equals("") || subAddress2.equals("") || subAddress3.equals("") || innerSpnnrRegion.getSelectedItem().toString().equals("--")) {
                        Toast.makeText(getApplicationContext(), "Address is not complete!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int regionId = PublicData.regionList.idOfIndex(innerSpnnrRegion.getSelectedItemPosition());
                    if (!subAddress1.substring(subAddress1.length() - 1).equals(","))
                        subAddress1 = subAddress1 + ",";
                    if (!subAddress2.substring(subAddress2.length() - 1).equals(","))
                        subAddress2 = subAddress2 + ",";
                    AddressItem addressItem = new AddressItem(0, subAddress1 + "\n" + subAddress2 + "\n" + subAddress3,
                            "", regionId, innerSpnnrRegion.getSelectedItem().toString());
                    addressItemList.add(addressItem);
                }
                // Check data complete or not
                if (realName.equals("") || email.equals("") || phone.equals("") || addressItemList.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Please finish the form!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check phone format
                phone = "+852-" + phone;
                String phoneFormat = getResources().getString(R.string.format_phone);
                if (!phone.matches(phoneFormat)) {
                    Toast.makeText(getApplicationContext(), "Phone is invalid!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check password empty or not
                if (!(newPassword.equals("") || rePassword.equals(""))) {
                    if (newPassword.length() < 8) {
                        Toast.makeText(getApplicationContext(), "Password is too short!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Check new password match to re password or not
                    if (!newPassword.equals(rePassword)) {
                        Toast.makeText(getApplicationContext(), "New password not match!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // Check network state
                if (!PublicData.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Send edit profile request
                if (progressDialog == null)
                    progressDialog = ProgressDialog.show(EditProfile_Activity.this, "Loading...", "Waiting for server response", true);
                new Thread(sendEditProfileRequest).start();
            }
        });
    }

    private void createAddressView(String address, final int regionId) {
        layLinearChildCount = layLinearAddress.getChildCount();
        if (layLinearChildCount < 6) {
            LayoutInflater LayInf = LayoutInflater.from(getApplicationContext());
            View view = LayInf.inflate(R.layout.add_address_item, null);
            layLinearAddress.addView(view, 0);
            String[] addressList = new String[3];
            if (!address.equals("")) {
                addressList = address.split("\n");
                EditText innerEdTxtSubAddress1 = (EditText) view.findViewById(R.id.edTxtSubAddress1);
                EditText innerEdTxtSubAddress2 = (EditText) view.findViewById(R.id.edTxtSubAddress2);
                EditText innerEdTxtSubAddress3 = (EditText) view.findViewById(R.id.edTxtSubAddress3);
                innerEdTxtSubAddress1.setText(addressList[0]);
                innerEdTxtSubAddress2.setText(addressList[1]);
                innerEdTxtSubAddress3.setText(addressList[2]);
            }
            final Spinner innerSpnnrRegion = (Spinner) view.findViewById(R.id.spnnrRegion);
            final DataList region = new DataList(PublicData.regionList);
            if (regionId == -1) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", 0);
                map.put("name", "--");
                region.add(0, map);
            }
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
            innerSpnnrRegion.setAdapter(simpleAdapter);
            innerSpnnrRegion.setSelection(regionId);
            innerSpnnrRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0)
                        if (regionId == -1)
                            if (region.size() > PublicData.regionList.size()) {
                                region.remove(0);
                                simpleAdapter.notifyDataSetChanged();
                                innerSpnnrRegion.setSelection(position - 1);
                            }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            ImageButton innerImgBtn = (ImageButton) findViewById(R.id.imgBtnRemoveAddress);
            innerImgBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ViewGroup) v.getParent().getParent()).removeView((View) v.getParent());
                    if (layLinearAddress.getChildCount() == 0) {
                        createAddressView("", 0);
                    }
                }
            });
        }
    }

    Thread sendEditProfileRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.editProfile(
                    sharePref.getString("password", ""), newPassword,
                    realName, phone, email, addressItemList);
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        // Save user data
                        if (!newPassword.equals("")) {
                            SharedPreferences.Editor editor = sharePref.edit();
                            PublicData.password = newPassword;
                            editor.putString("password", newPassword);
                            editor.apply();
                        }
                        JSONObject content = result.getJSONObject("content");
                        PublicData.realName = content.getString("name");
                        PublicData.email = content.getString("email");
                        PublicData.phone = content.getString("phone");
                        PublicData.register_time = DateFormator.getDateTime(content.getString("created_at"));
                        PublicData.last_modify_time = DateFormator.getDateTime(content.getString("updated_at"));
                        PublicData.UserAddresses = content.getJSONArray("specify_addresses");
                        setResult(RESULT_OK);
                        finish();
                    } else
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
            } catch (Exception e) {
                e.printStackTrace();
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
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }
    };
}
