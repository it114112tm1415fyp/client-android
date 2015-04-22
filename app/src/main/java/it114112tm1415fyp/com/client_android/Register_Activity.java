package it114112tm1415fyp.com.client_android;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import it114112tm1415fyp.com.util.AddressItem;
import it114112tm1415fyp.com.util.DataList;
import it114112tm1415fyp.com.util.PublicData;

public class Register_Activity extends Activity {

    private ProgressDialog progressDialog;
    private LinearLayout layLinearAddress;
    private EditText edTxtUsername, edTxtRealName, edTxtPhone, edTxtEmail, edTxtPassword, edTxtRePassword;
    private TextView txtViewTerm;
    private Button btnConfirm;
    private ImageButton imgBtnAddAddress;
    private CheckBox chBxAgree;

    private ArrayList<AddressItem> addressItemList;

    private String username, password, rePassword, realName, phone, email;
    int childCount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PublicData.regionList.size() == 0) {
            if (progressDialog == null)
                progressDialog = ProgressDialog.show(Register_Activity.this, "Loading...", "Waiting for server response", true);
            new Thread(sendGetRegionListRequest).start();
        } else {
            setContentView(R.layout.register);
            findAllView();
            createAddressView();
            setAllView();
        }
    }

    public void findAllView() {
        edTxtUsername = (EditText) findViewById(R.id.edTxtUsername);
        edTxtPassword = (EditText) findViewById(R.id.edTxtPassword);
        edTxtRePassword = (EditText) findViewById(R.id.edTxtRePassword);
        edTxtRealName = (EditText) findViewById(R.id.edTxtRealName);
        edTxtPhone = (EditText) findViewById(R.id.edTxtPhone);
        edTxtEmail = (EditText) findViewById(R.id.edTxtEmail);
        layLinearAddress = (LinearLayout) findViewById(R.id.layLinearAddress);
        imgBtnAddAddress = (ImageButton) findViewById(R.id.imgBtnAddAddress);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        chBxAgree = (CheckBox) findViewById(R.id.chBxAgree);
        txtViewTerm = (TextView) findViewById(R.id.txtViewTerm);
    }

    public void setAllView() {
        txtViewTerm.setText(getString(R.string.user_licence_agreement));
        txtViewTerm.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Register_Activity.this);
                        alertDialog.setTitle("Terms & Conditions of Use");
                        TextView textView = new TextView(getApplicationContext());
                        textView.setTextSize(14);
                        textView.setPadding(3, 3, 3, 3);
                        textView.setTextColor(Color.BLACK);
                        textView.setText(
                                "Access to and use of this Logistic System site and Logistic System’ official sites in social media networks such as Facebook, Twitter, blogs and wikis (collectively \"Sites\") is subject to these terms and conditions. Use of the Sites constitutes acceptance of these terms and conditions in full.\n" +
                                        "\n" +
                                        "\n" +
                                        "Access to and use of the information, services and products provided on the Sites by Logistic System Network Limited and its subsidiaries or affiliated companies (collectively \"Logistic System\") are subject to the following terms and conditions:\n" +
                                        "\n" +
                                        "\n" +
                                        "The Sites and the information, names, images, pictures, logos and icons regarding or relating to Logistic System or its products and services is provided \"AS IS\" without any representation or endorsement made and without warranty of any kind, whether expressed or implied, including, but not limited to the implied warranties of  merchantability, fitness for a particular purpose or non-infringement. \n" +
                                        "\n" +
                                        "\n" +
                                        "The texts, images, audio/video clips, information, services, software and other materials made available to you on the Sites (\"Contents\") are protected by copyrights or other intellectual property rights and laws. You also agree not to adopt, alter or create a derivative work from any of the Contents or reproduce, republish, download, distribute or exploit the Contents for any use other than your personal non-commercial use. Any other use of the Contents requires the prior written permission of Logistic System or the relevant third party owner. \n" +
                                        "\n" +
                                        "\n" +
                                        "Logistic System does not verify nor warrant the correctness and accuracy of the Contents and assume no liabilities whatsoever with respect to any of your loss or damage incurred directly or indirectly due to the use of the Contents. Moreover, the information provided on the Sites has not been written to meet your individual requirements and it is your sole responsibility to satisfy yourself prior to using the Contents in any way that it is suitable for your purposes. \n" +
                                        "\n" +
                                        "\n" +
                                        "You agree that Logistic System is not liable for any liability you incur as a result of using the Contents. In no event shall Logistic System or any of its contractors or employees be liable for any damages whatsoever, including direct, special, indirect or consequential damages, resulting from or in connection with the access to or use of the Sites or the use and dissemination of the Contents.\n" +
                                        "\n" +
                                        "\n" +
                                        "The Contents contained on the Sites may be periodically updated. Logistic System reserves the rights at any time and without notice to make modifications, improvements and/or changes to the Contents, these conditions, names, images, pictures, logos and icons or the products and services relating to Logistic System referred to on the Sites. As the Internet is maintained independently at thousands of sites around the world, the Contents or any part thereof that may be accessed through the Sites may originate outside the boundaries of the Sites. Therefore, Logistic System excludes any obligation or responsibility regarding any Contents or part thereof which is/are derived, obtained, accessed within, through or outside the Sites.\n" +
                                        "\n" +
                                        "\n" +
                                        "The products and services mentioned in the Sites are at all times subject to Logistic System' Standard Terms and Conditions for Services and other relevant conditions. Local variations may exist and apply depending on the country of origin of the shipment. Please contact the nearest Logistic System' local office to obtain a copy of the local terms and conditions. \n" +
                                        "\n" +
                                        "\n" +
                                        "Whilst Logistic System makes all reasonable attempts to exclude viruses from the Sites, it cannot ensure such exclusion and no liability is accepted for viruses. Thus, you are recommended to take all appropriate safeguards before downloading information from the Sites. In other words, it is up to you to take precautions to ensure that whatever you select for your use from the Site is free of such items as viruses, worms, trojan horses and other forms of malware. \n" +
                                        "\n" +
                                        "\n" +
                                        "You agree to neither disturb the normal operation of the Sites nor infringe the integrity of the Sites by hacking, altering the information contained in the Sites, prevent or limit access to the Sites to other users, or otherwise. \n" +
                                        "\n" +
                                        "\n" +
                                        "You are responsible for complying with the laws of the jurisdiction from which you are accessing the Sites and you agree that you will not access or use the information on the Sites in violation of such laws. Unless expressly stated otherwise herein, any information submitted by you through the Sites shall be deemed non-confidential and non-proprietary. You represent that you have the lawful right to submit such information and agree that you will not submit any information unless you are legally entitled to do so. \n" +
                                        "\n" +
                                        "\n" +
                                        "You agree to indemnify and hold Logistic System, its directors and officers from and against all claims, losses, damage, liabilities and expenses resulting from your breach of these Terms & Conditions. \n" +
                                        "\n" +
                                        "\n" +
                                        "Any legal action or proceeding relating to the Sites shall be governed by the laws of The Hong Kong SAR. If you attempt to bring any legal proceedings against Logistic System you specifically acknowledge that Logistic System is free to choose the jurisdiction of our preference as to where such action against us may be held. \n" +
                                        "\n" +
                                        "\n" +
                                        "Logistic System may suspend, block, and/ or terminate your use of the Sites at any time, for any reason or for no reason at all. You are personally liable for any orders that you place or charges that you incur prior to termination. Logistic System hereby expressly reserves its rights to revise and amend these terms and conditions or to change, suspend, or discontinue all or any aspects of the Sites at its absolute discretion without prior notice."
                        );
                        ScrollView scrollView = new ScrollView(getApplicationContext());
                        scrollView.addView(textView);
                        alertDialog.setView(scrollView);
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                }
        );
        imgBtnAddAddress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddressView();
            }
        });

        btnConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                username = edTxtUsername.getText().toString();
                password = edTxtPassword.getText().toString();
                realName = edTxtRealName.getText().toString();
                rePassword = edTxtRePassword.getText().toString();
                email = edTxtEmail.getText().toString();
                phone = edTxtPhone.getText().toString();

                addressItemList = new ArrayList<>();
                childCount = layLinearAddress.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    EditText inner_ed1 = (EditText) layLinearAddress.getChildAt(i).findViewById(R.id.edTxtSubAddress1);
                    EditText inner_ed2 = (EditText) layLinearAddress.getChildAt(i).findViewById(R.id.edTxtSubAddress2);
                    EditText inner_ed3 = (EditText) layLinearAddress.getChildAt(i).findViewById(R.id.edTxtSubAddress3);
                    Spinner innerSpnnrRegion = (Spinner) layLinearAddress.getChildAt(i).findViewById(R.id.spnnrRegion);
                    String subAddress1 = inner_ed1.getText().toString();
                    String subAddress2 = inner_ed2.getText().toString();
                    String subAddress3 = inner_ed3.getText().toString();
                    // Check Address
                    if (subAddress1.equals("") || subAddress2.equals("") || subAddress3.equals("")
                            || innerSpnnrRegion.getSelectedItem().toString().equals("--")) {
                        Toast.makeText(getApplicationContext(), "Address is not complete!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int regionId = PublicData.regionList.idOfIndex(innerSpnnrRegion.getSelectedItemPosition());
                    if (!subAddress1.substring(subAddress1.length() - 1).equals(","))
                        subAddress1 = subAddress1 + ",";
                    if (!subAddress2.substring(subAddress2.length() - 1).equals(","))
                        subAddress2 = subAddress2 + ",";
                    AddressItem addressItem = new AddressItem(
                            0, subAddress1 + "\n" + subAddress2 + "\n" + subAddress3,
                            "", regionId, innerSpnnrRegion.getSelectedItem().toString());
                    addressItemList.add(addressItem);
                }
                // Check data complete or not
                if (username.equals("") || password.equals("") || rePassword.equals("") ||
                        realName.equals("") || email.equals("") || phone.equals("") ||
                        addressItemList.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Please finish the form!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check username length
                if (username.length() < 5) {
                    Toast.makeText(getApplicationContext(), "Username is too short!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check password length
                if (password.length() < 8) {
                    Toast.makeText(getApplicationContext(), "Password is too short!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check password match or not
                if (!password.equals(rePassword)) {
                    Toast.makeText(getApplicationContext(), "Password not match!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check phone format
                phone = "+852-" + phone;
                String phoneFormat = getResources().getString(R.string.format_phone);
                if (!phone.matches(phoneFormat)) {
                    Toast.makeText(getApplicationContext(), "Phone is invalid!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check checkbox check or not
                if (!chBxAgree.isChecked()) {
                    Toast.makeText(getApplicationContext(), "You are not agree all the term!", Toast.LENGTH_SHORT).show();
                    return;
                }
                PublicData.username = username;
                PublicData.password = password;
                // Check network state
                if (!PublicData.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Send send register request
                if (progressDialog == null)
                    progressDialog = ProgressDialog.show(Register_Activity.this, "Loading...", "Waiting for server response", true);
                new Thread(sendRegisterRequest).start();
            }
        });
    }

    private void createAddressView() {
        childCount = layLinearAddress.getChildCount();
        if (childCount < 6) {
            LayoutInflater LayInf = LayoutInflater.from(getApplicationContext());
            View view = LayInf.inflate(R.layout.add_address_item, null);
            layLinearAddress.addView(view);

            final Spinner innerSpnnrRegion = (Spinner) layLinearAddress.getChildAt(childCount).findViewById(R.id.spnnrRegion);
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
            innerSpnnrRegion.setAdapter(simpleAdapter);
            innerSpnnrRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0)
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
                        createAddressView();
                    }
                }
            });
        }
    }

    Thread sendRegisterRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.register(username, password, realName, email,
                    phone, addressItemList);
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        PublicData.logined = 1;
                        PublicData.username = username;
                        PublicData.password = password;
                        JSONObject content = result.getJSONObject("content");
                        PublicData.userId = content.getString("id");
                        PublicData.realName = content.getString("name");
                        PublicData.email = content.getString("email");
                        PublicData.phone = content.getString("phone");
                        PublicData.register_time = DateFormator.getDateTime(content.getString("created_at"));
                        PublicData.last_modify_time = DateFormator.getDateTime(content.getString("updated_at"));
                        PublicData.UserAddresses = content.getJSONArray("specify_addresses");
                        SharedPreferences sharePref = getSharedPreferences("Logistic_System", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharePref.edit();
                        editor.putInt("saveMe", 1);
                        editor.putString("username", edTxtUsername.getText()
                                .toString());
                        editor.putString("password", edTxtPassword.getText()
                                .toString());
                        editor.apply();
                        setResult(RESULT_OK);
                        finish();
                    } else
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    Toast.makeText(getApplicationContext(),
                                            result.getString("error"),
                                            Toast.LENGTH_SHORT).show();
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
                                setContentView(R.layout.register);
                                findAllView();
                                createAddressView();
                                setAllView();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Fail to server. \n Please try again later.", Toast.LENGTH_SHORT).show();
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
