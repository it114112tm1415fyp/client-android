package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it114112tm1415fyp.com.util.PublicData;

public class ShipmentChooseReceiveTime_Activity extends Activity {

    public static boolean[][] timeList = new boolean[3][7];

    private ProgressDialog progressDialog;
    private TableRow tableRowDay, tableRowTimeSlot1, tableRowTimeSlot2, tableRowTimeSlot3;
    private Button btnNext;

    private ArrayList<String> datesList = new ArrayList<>();
    private String previousActivity = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipment_choose_receive_time);
        timeList = new boolean[][]{{true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true},
                {true, true, true, true, true, true, true}};
        previousActivity = getIntent().getStringExtra("activity");
        if (!PublicData.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        findAllView();
        if (previousActivity.equals("EditOrder")) {
            progressDialog = ProgressDialog.show(ShipmentChooseReceiveTime_Activity.this, "Loading...", "Getting Details...", true);
            new Thread(sendGetOrderReceiveFreeTimeRequest).start();
        } else {
            progressDialog = ProgressDialog.show(ShipmentChooseReceiveTime_Activity.this, "Loading...", "Getting Details...", true);
            new Thread(sendGetReceiveTimeSegmentsRequest).start();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void findAllView() {
        btnNext = (Button) findViewById(R.id.btnNext);
        tableRowDay = (TableRow) findViewById(R.id.tableRowDay);
        tableRowTimeSlot1 = (TableRow) findViewById(R.id.tableRowTime1);
        tableRowTimeSlot2 = (TableRow) findViewById(R.id.tableRowTime2);
        tableRowTimeSlot3 = (TableRow) findViewById(R.id.tableRowTime3);
    }

    private void setAllView() {
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(160, 250);
        layoutParams.setMargins(3, 3, 3, 3);
        tableRowTimeSlot1.addView(timeSlotView(PublicData.receiveTimeSegments.get(0)), layoutParams);
        tableRowTimeSlot2.addView(timeSlotView(PublicData.receiveTimeSegments.get(1)), layoutParams);
        tableRowTimeSlot3.addView(timeSlotView(PublicData.receiveTimeSegments.get(2)), layoutParams);
        TableRow.LayoutParams newLayoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f);
        layoutParams.setMargins(3, 3, 3, 3);
        SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM");
        for (int i = 0; i < 7; i++) {
            if (datesList.size() > i && previousActivity.equals("EditOrder")) {
                tableRowDay.addView(dayTextView(DateFormator.getShortDate(datesList.get(i))), newLayoutParams);
            } else {
                Calendar date = Calendar.getInstance();
                date.add(Calendar.DATE, i);
                tableRowDay.addView(dayTextView(newFormat.format(date.getTime())), newLayoutParams);
            }
            tableRowTimeSlot1.addView(timeSlotView(0, i), newLayoutParams);
            tableRowTimeSlot2.addView(timeSlotView(1, i), newLayoutParams);
            tableRowTimeSlot3.addView(timeSlotView(2, i), newLayoutParams);
        }
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousActivity.equals("EditOrder")) {
                    EditShipment_Activity.timeList = new ArrayList();
                    for (int i = 0; i < 7; i++) {
                        EditShipment_Activity.timeList.add(timeList[0][i]);
                        EditShipment_Activity.timeList.add(timeList[1][i]);
                        EditShipment_Activity.timeList.add(timeList[2][i]);
                    }
                    setResult(RESULT_OK);
                    finish();
                    return;
                }
                startActivityForResult(
                        new Intent(ShipmentChooseReceiveTime_Activity.this, NewShipmentConfirm_Activity.class), 1);
            }
        });
    }

    private TextView dayTextView(String day) {
        TextView textView = new TextView(this);
        textView.setText(day);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(-3, -3, -3, -5);
        return textView;
    }

    private ImageView timeSlotView(final int timeSlotIndex, final int index) {
        Date now = new Date();
        int hours = now.getHours();
        int minutes = now.getMinutes();
        final ImageView imageView = new ImageView(this);
        if (index >= 1 || PublicData.dateTimeList.get(timeSlotIndex).getHours() > hours ||
                PublicData.dateTimeList.get(timeSlotIndex).getHours() == hours && PublicData.dateTimeList.get(timeSlotIndex).getMinutes() > minutes) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (timeList[timeSlotIndex][index]) {
                        imageView.setImageResource(R.drawable.tick_icon_dis);
                        timeList[timeSlotIndex][index] = false;
                    } else {
                        imageView.setImageResource(R.drawable.tick_icon);
                        timeList[timeSlotIndex][index] = true;
                    }
                }
            });
        } else {
            timeList[timeSlotIndex][index] = false;
        }
        if (timeList[timeSlotIndex][index])
            imageView.setImageResource(R.drawable.tick_icon);
        else
            imageView.setImageResource(R.drawable.tick_icon_dis);
        return imageView;
    }

    private TextView timeSlotView(String timeSlotString) {
        final TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setText(timeSlotString);
        textView.setPadding(3, 3, 3, 3);
        return textView;
    }

    Thread sendGetOrderReceiveFreeTimeRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.getOrderReceiveFreeTime(EditShipment_Activity.orderId);
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        JSONObject contentObject = result.getJSONObject("content");
                        JSONArray dates = contentObject.getJSONArray("dates");
                        if (dates.length() > 0)
                            for (int i = 0; i < dates.length(); i++) {
                                datesList.add(dates.getString(i));
                            }
                        PublicData.dateTimeList.clear();
                        PublicData.receiveTimeSegments.clear();
                        JSONArray timeSegments = contentObject.getJSONArray("receive_time_segments");
                        Date now = new Date();
                        for (int i = 0; i < timeSegments.length(); i++) {
                            JSONObject timeSegmentsObject = timeSegments.getJSONObject(i);
                            Date dateTime = DateFormator.serverFormat.parse(timeSegmentsObject.getString("end_time"));
                            dateTime.setDate(1);
                            dateTime.setYear(now.getYear());
                            dateTime.setMonth(now.getMonth());
                            dateTime.setDate(now.getDate() + 1);
                            PublicData.dateTimeList.add(dateTime);
                            PublicData.receiveTimeSegments.add(
                                    DateFormator.getShortTime(timeSegmentsObject.getString("start_time")) + "\n-\n"
                                            + DateFormator.getShortTime(timeSegmentsObject.getString("end_time")));
                        }
                        JSONArray freeArray = contentObject.getJSONArray("free");
                        if (freeArray.length() > 0)
                            for (int i = 0; i < freeArray.length(); i++) {
                                JSONArray array = freeArray.getJSONArray(i);
                                for (int x = 0; x < array.length(); x++) {
                                    timeList[x][i] = array.getBoolean(x);
                                }
                            }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (progressDialog != null)
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAllView();
                    }
                });
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
                        PublicData.dateTimeList.clear();
                        PublicData.receiveTimeSegments.clear();
                        Date now = new Date();
                        for (int i = 0; i < timeSegments.length(); i++) {
                            JSONObject timeSegmentsObject = timeSegments.getJSONObject(i);
                            Date dateTime = DateFormator.serverFormat.parse(timeSegmentsObject.getString("end_time"));
                            dateTime.setDate(1);
                            dateTime.setYear(now.getYear());
                            dateTime.setMonth(now.getMonth());
                            dateTime.setDate(now.getDate() + 1);
                            PublicData.dateTimeList.add(dateTime);
                            PublicData.receiveTimeSegments.add(
                                    DateFormator.getShortTime(timeSegmentsObject.getString("start_time")) + "\n-\n"
                                            + DateFormator.getShortTime(timeSegmentsObject.getString("end_time")));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (progressDialog != null)
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAllView();
                    }
                });
            }
        }
    };
}
