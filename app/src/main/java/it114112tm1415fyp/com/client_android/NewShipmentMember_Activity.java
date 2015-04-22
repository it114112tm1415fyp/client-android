package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it114112tm1415fyp.com.util.AddressItem;
import it114112tm1415fyp.com.util.PublicData;

public class NewShipmentMember_Activity extends Activity {

    private ProgressDialog progressDialog;
    private LinearLayout layLinearUserResult;
    private EditText edTxtUsername, edTxtPhone;
    private Button btnFindUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_shipment_member);
        NewShipment_Activity.receiverType = "member";
        findAllView();
        setAllView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    public void findAllView() {
        layLinearUserResult = (LinearLayout) findViewById(R.id.layLinearUserResult);
        edTxtUsername = (EditText) findViewById(R.id.edTxtUsername);
        edTxtPhone = (EditText) findViewById(R.id.edTxtPhone);
        btnFindUser = (Button) findViewById(R.id.btnFindUser);
    }

    public void setAllView() {
        btnFindUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String receiverUsername = edTxtUsername.getText().toString();
                String receiverPhone = edTxtPhone.getText().toString();
                if (receiverUsername.equals("") || receiverPhone.equals("")) {
                    Toast.makeText(getApplicationContext(), "Username or Phone can not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (receiverUsername.length() < 5) {
                    Toast.makeText(getApplicationContext(), "Username is too short!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String phoneString = "+852-" + receiverPhone;
                String phoneFormat = getResources().getString(R.string.format_phone);
                if (!phoneString.matches(phoneFormat)) {
                    Toast.makeText(getApplicationContext(), "Phone format is wrong!", Toast.LENGTH_SHORT).show();
                }

                NewShipment_Activity.receiverUsername = edTxtUsername.getText().toString();
                NewShipment_Activity.receiverPhone = edTxtPhone.getText().toString();
                // Check network state
                if (!PublicData.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (progressDialog == null)
                    progressDialog = ProgressDialog.show(NewShipmentMember_Activity.this, "Loading...", "Waiting for server response", true);
                new Thread(sendFindUserRequest).start();
            }
        });
    }

    public void createView() {
        layLinearUserResult.removeAllViews();
        TextView textView = new TextView(this);
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setText("Result");
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layLinearUserResult.addView(textView, p);
        LayoutInflater Li = LayoutInflater.from(getApplicationContext());
        View view = Li.inflate(R.layout.result_result_list_item, null);
        layLinearUserResult.addView(view);

        TableLayout innerTableLayout = (TableLayout) layLinearUserResult.getChildAt(1);
        TextView tv_id = (TextView) innerTableLayout.findViewById(R.id.user_result_item_id);
        TextView tv_realName = (TextView) innerTableLayout.findViewById(R.id.user_result_item_realname);
        tv_id.setText(NewShipment_Activity.receiverId + "");
        tv_realName.setText(NewShipment_Activity.receiverRealName);
        innerTableLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NewShipmentMember_Activity.this, ShipmentDestinationList_Activity.class);
                i.putExtra("activity", "NewOrder");
                startActivityForResult(i, 1);
            }
        });
    }

    Thread sendFindUserRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.findUserInfo(NewShipment_Activity.receiverUsername, "+852-" + NewShipment_Activity.receiverPhone);
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        JSONObject content = result.getJSONObject("content");
                        NewShipment_Activity.receiverId = content.getInt("id");
                        NewShipment_Activity.receiverRealName = content.getString("name");
                        JSONArray address = content.getJSONArray("specify_addresses");
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
                        runOnUiThread(new Runnable() {
                            public void run() {
                                createView();
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
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }
    };
}
