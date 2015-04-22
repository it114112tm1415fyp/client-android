package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import it114112tm1415fyp.com.util.PublicData;

public class NewShipmentConfirm_Activity extends Activity {

    private ProgressDialog progressDialog;

    private TableLayout tableLayout;
    private Button btnConfirm;
    private EditText edTxtNumberOfGoods;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_shipment_confirm);
        findAllView();
        setAllView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void findAllView() {
        tableLayout = (TableLayout) findViewById(R.id.layTable);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        edTxtNumberOfGoods = (EditText) findViewById(R.id.edTxtNumberOfGoods);
    }

    private void setAllView() {
        if (NewShipment_Activity.receiverType.equals("member")) {
            createView("Receiver ID : ", NewShipment_Activity.receiverId + "");
            createView("Receiver Username : ", NewShipment_Activity.receiverUsername);
        }
        createView("Receiver Name : ", NewShipment_Activity.receiverRealName);
        if (NewShipment_Activity.receiverType.equals("nonmember")) {
            createView("Receiver Email : ", NewShipment_Activity.receiverEmail);
        }
        createView("Receiver Phone : ", NewShipment_Activity.receiverPhone);
        createView("Departure : ", NewShipment_Activity.departure);
        createView("Destination : ", NewShipment_Activity.destination.equals("SpecifyAddress") ? NewShipment_Activity.destination + NewShipment_Activity.departureType : NewShipment_Activity.destination);
        btnConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Check editView of numberOfGood empty or not
                String editTextString = edTxtNumberOfGoods.getText().toString();
                if (editTextString.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter the number of goods.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check the value of numberOfGood
                int numberOfGoods = Integer.parseInt(editTextString);
                if (numberOfGoods < 1) {
                    Toast.makeText(getApplicationContext(), "Invalid Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                NewShipment_Activity.goodsNumber = numberOfGoods;
                // Check network state
                if (!PublicData.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Send make new order request
                progressDialog = ProgressDialog.show(NewShipmentConfirm_Activity.this, "Loading...", "Waiting for server response", true);
                new Thread(sendMakeNewOrderRequest).start();
            }
        });
    }

    private void createView(String title, String context) {
        TextView textView = new TextView(this);
        textView.setText(title);
        TextView textView2 = new TextView(this);
        textView2.setText(context);
        TableRow tableRow = new TableRow(this);
        tableRow.setBackgroundResource(R.drawable.table_row);
        tableRow.addView(textView);
        tableRow.addView(textView2);
        TableRow.LayoutParams p = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        p.setMargins(3, 3, 3, 3);
        tableLayout.addView(tableRow, tableLayout.getChildCount() - 2, p);
    }

    Thread sendMakeNewOrderRequest = new Thread() {
        public void run() {
            JSONObject result;
            if (NewShipment_Activity.departureType.equals("SpecifyAddress")) {
                // Order send from client's address
                ArrayList timeList = new ArrayList();
                for (int i = 0; i < 7; i++) {
                    timeList.add(ShipmentChooseReceiveTime_Activity.timeList[0][i]);
                    timeList.add(ShipmentChooseReceiveTime_Activity.timeList[1][i]);
                    timeList.add(ShipmentChooseReceiveTime_Activity.timeList[2][i]);
                }
                result = HTTP.makeNewOrder(
                        NewShipment_Activity.receiverType,
                        NewShipment_Activity.receiverId, NewShipment_Activity.receiverRealName,
                        NewShipment_Activity.receiverEmail, NewShipment_Activity.receiverPhone,
                        NewShipment_Activity.departureId, NewShipment_Activity.departureType,
                        NewShipment_Activity.destinationId, NewShipment_Activity.destinationType,
                        NewShipment_Activity.goodsNumber, timeList);
            } else {
                // Order send from shop
                result = HTTP.makeNewOrder(
                        NewShipment_Activity.receiverType,
                        NewShipment_Activity.receiverId, NewShipment_Activity.receiverRealName,
                        NewShipment_Activity.receiverEmail, NewShipment_Activity.receiverPhone,
                        NewShipment_Activity.departureId, NewShipment_Activity.departureType,
                        NewShipment_Activity.destinationId, NewShipment_Activity.destinationType,
                        NewShipment_Activity.goodsNumber, null);
            }
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        setResult(RESULT_OK);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Make Order Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }
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
