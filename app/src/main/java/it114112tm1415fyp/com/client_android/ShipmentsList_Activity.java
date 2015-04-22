
package it114112tm1415fyp.com.client_android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it114112tm1415fyp.com.util.PublicData;

public class ShipmentsList_Activity extends Activity {
    public static int NEED_UPDATE = 0;
    private DataBaseHelper helper;
    private SimpleAdapter orderListAdapter;

    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layLinear;
    private ListView lstOrdersList;

    private List<HashMap<String, Object>> orderList = new ArrayList<>();
    private String showBy = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipments_list);
        helper = new DataBaseHelper(getApplicationContext());
        findAllView();
        setAllView();
        // Check network state
        if (PublicData.isConnected(getApplicationContext())) {
            if (progressDialog == null)
                progressDialog = ProgressDialog.show(ShipmentsList_Activity.this, "Loading...", "Waiting for server response", true);
            new Thread(sendGetOrderRequest).start();
        } else {
            getOrderFromDatabase(showBy);
            orderListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == 1 && resultCode == RESULT_OK) || resultCode == RESULT_OK) {
            if (!PublicData.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (progressDialog == null)
                progressDialog = ProgressDialog.show(ShipmentsList_Activity.this, "Loading...", "Waiting for server response", true);
            new Thread(sendGetOrderRequest).start();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void findAllView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.laySwipe);
        lstOrdersList = (ListView) findViewById(R.id.lstOrdersList);
        layLinear = (LinearLayout) findViewById(R.id.layLinear);
    }

    private void checkOrderSize() {
        if (orderList.size() == 0) {
            if (layLinear.getChildCount() < 2) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                TextView textView = new TextView(getApplicationContext());
                textView.setText("No Record");
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(20);
                textView.setPadding(3, 3, 3, 3);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                layLinear.addView(textView, 0, params);
            }
        } else {
            if (layLinear.getChildCount() > 1) {
                layLinear.removeViewAt(0);
            }
        }
    }

    private void setAllView() {
        checkOrderSize();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Thread(sendGetOrderRequest).start();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        orderListAdapter = new SimpleAdapter(getApplicationContext(), orderList,
                R.layout.shipments_list_item, new String[]{"img", "id", "user", "goods", "state", "updateTime"},
                new int[]{R.id.img, R.id.txtViewId, R.id.txtViewUser, R.id.txtViewGoods, R.id.txtViewState, R.id.txtViewUpdateTime});
        lstOrdersList.setAdapter(orderListAdapter);
        lstOrdersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ShipmentInfo_Activity.class);
                i.putExtra("orderId", Integer.parseInt(((String) orderList.get(position).get("id")).substring(5)));
                startActivityForResult(i, 1);
            }
        });
        lstOrdersList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
    }

    // Get order from local database
    private void getOrderFromDatabase(String showBy) {
        //initData();
        Cursor cursor = helper.getOrderShowBy(showBy);
        int rows = cursor.getCount();
        if (cursor != null && rows != 0) {
            cursor.moveToFirst();
            for (int i = 0; i < rows; i++) {
                HashMap<String, Object> orderMap = new HashMap<>();
                try {
                    orderMap.put("id", "ID : " + cursor.getInt(0));
                    orderMap.put("goods", "Goods : " + cursor.getInt(3));
                    orderMap.put("state", "State : " + cursor.getString(4));
                    orderMap.put("updateTime", "Update Time : " + cursor.getString(5));
                    orderList.add(orderMap);
                    String ordertype = cursor.getString(6);
                    if (ordertype.equals("send")) {
                        orderMap.put("img", R.drawable.order_out);
                        JSONObject senderObject = new JSONObject(cursor.getString(1));
                        orderMap.put("user", "Sender : " + senderObject.getString("name"));
                    } else {
                        orderMap.put("img", R.drawable.order_in);
                        JSONObject receiverObject = new JSONObject(cursor.getString(2));
                        orderMap.put("user", "Receiver : " + receiverObject.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    // Save order to local database
    private void saveOrderToDatabase(JSONArray orderList, String orderType) {
        for (int i = 0; i < orderList.length(); i++) {
            try {
                JSONObject orderObject = orderList.getJSONObject(i);
                if (helper.checkOrderExist(orderObject.getInt("id")))
                    helper.updateOrderDetails(orderObject, orderObject.getInt("id"));
                else
                    helper.insertOrder(orderObject, orderType);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        menu.add(0, 1, 0, "Show All");
        menu.add(0, 2, 0, "Show Sended Order");
        menu.add(0, 3, 0, "Show Received Order");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        orderList.clear();
        switch (item.getItemId()) {
            case 0:     // Refresh order list
                showBy = "";
                // Send get order request
                progressDialog = ProgressDialog.show(ShipmentsList_Activity.this, "Loading...", "Waiting for server response", true);
                new Thread(sendGetOrderRequest).start();
                break;
            case 1:     // Show all order record
                showBy = "";
                getOrderFromDatabase(showBy);
                break;
            case 2:     // Show all send order record
                showBy = "send";
                getOrderFromDatabase(showBy);
                break;
            case 3:     // Show all receive order record
                showBy = "receive";
                getOrderFromDatabase(showBy);
                break;
        }
        orderListAdapter.notifyDataSetChanged();
        checkOrderSize();
        return super.onOptionsItemSelected(item);
    }

    Thread sendGetOrderRequest = new Thread() {
        public void run() {
            final JSONObject receiveOrderResult = HTTP.getReceiveOrders();
            final JSONObject sendOrderResult = HTTP.getSendOrders();
            try {
                if (receiveOrderResult != null && sendOrderResult != null) {
                    orderList.clear();
                    if (receiveOrderResult.getBoolean("success")) {
                        saveOrderToDatabase(receiveOrderResult.getJSONObject("content").getJSONArray("receive_order"), "receive");
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    Toast.makeText(getApplicationContext(), receiveOrderResult.getString("error"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if (sendOrderResult.getBoolean("success")) {
                        saveOrderToDatabase(sendOrderResult.getJSONObject("content").getJSONArray("send_order"), "send");
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    Toast.makeText(getApplicationContext(), sendOrderResult.getString("error"), Toast.LENGTH_SHORT).show();
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
                getOrderFromDatabase(showBy);
                runOnUiThread(new Runnable() {
                    public void run() {
                        orderListAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        checkOrderSize();
                    }
                });
            }
        }
    };
}