package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it114112tm1415fyp.com.util.PublicData;

public class ShipmentInfo_Activity extends Activity {

    private DataBaseHelper helper;
    private SimpleAdapter goodsAdapter;

    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private GridView gridViewGoods;
    private TextView tvtViewOrderId, txtViewSender, txtViewReceiver, txtViewDeparture, txtViewDestination, txtViewOrderState, txtViewOrderTime, txtViewNumberOfGoods, txtViewMore;

    private List<HashMap<String, Object>> goodsList = new ArrayList<>();
    private String[] goodId;
    private String sender, receiver, departure, destination, state, orderTime, orderType;
    private int orderId, numberOfGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipment_info);
        helper = new DataBaseHelper(getApplicationContext());
        orderId = getIntent().getIntExtra("orderId", 0);
        findAllView();
        getOrderFromDatabase();
        setAllView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (!PublicData.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
            }
            // Send get order details request
            if (progressDialog == null)
                progressDialog = ProgressDialog.show(ShipmentInfo_Activity.this, "Loading...", "Waiting for server response", true);
            goodsList.clear();
            new Thread(sendGetOrderDetailRequest).start();
            setResult(RESULT_OK);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        if (state.equals("submitted") && orderType.equals("send")) {
            menu.add(0, 1, 0, "Edit");
            menu.add(0, 2, 0, "Cancel");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check network state
        if (!PublicData.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case 0:
                // Send get order details request
                if (progressDialog == null)
                    progressDialog = ProgressDialog.show(ShipmentInfo_Activity.this, "Loading...", "Waiting for server response", true);
                goodsList.clear();
                new Thread(sendGetOrderDetailRequest).start();
                break;
            case 1:
                Intent i = new Intent(getApplicationContext(), EditShipment_Activity.class);
                i.putExtra("orderId", orderId);
                i.putExtra("sender", sender);
                i.putExtra("receiver", receiver);
                i.putExtra("departure", departure);
                i.putExtra("destination", destination);
                i.putExtra("numberOfGoods", numberOfGoods + "");
                startActivityForResult(i, 1);
                break;
            case 2:
                if (progressDialog == null)
                    progressDialog = ProgressDialog.show(ShipmentInfo_Activity.this, "Loading...", "Waiting for server response", true);
                new Thread(sendCancelOrderRequest).start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void findAllView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.laySwipe);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        linearLayout = (LinearLayout) findViewById(R.id.layLinearGoods);
        gridViewGoods = (GridView) findViewById(R.id.gridViewGoods);
        tvtViewOrderId = (TextView) findViewById(R.id.txtViewOrderId);
        txtViewSender = (TextView) findViewById(R.id.txtViewSender);
        txtViewReceiver = (TextView) findViewById(R.id.txtViewReceiver);
        txtViewDeparture = (TextView) findViewById(R.id.txtViewDeparture);
        txtViewDestination = (TextView) findViewById(R.id.txtViewDestination);
        txtViewOrderState = (TextView) findViewById(R.id.txtViewOrderState);
        txtViewOrderTime = (TextView) findViewById(R.id.txtViewOrderTime);
        txtViewNumberOfGoods = (TextView) findViewById(R.id.txtViewNumberOfGoods);
        txtViewMore = (TextView) findViewById(R.id.txtViewMore);
    }

    private void setAllView() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                goodsList.clear();
                new Thread(sendGetOrderDetailRequest).start();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        tvtViewOrderId.setText(orderId + "");
        txtViewSender.setText(sender);
        txtViewReceiver.setText(receiver);
        txtViewDeparture.setText(departure);
        txtViewDestination.setText(destination);
        txtViewOrderState.setText(state);
        txtViewNumberOfGoods.setText(numberOfGoods + "");
        txtViewOrderTime.setText(orderTime);

        if (state.equals("submitted") || state.equals("confirmed") || state.equals("canceled")) {
            linearLayout.setVisibility(View.GONE);
        } else {
            gridViewGoods.setVisibility(View.GONE);
            txtViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (gridViewGoods.getVisibility() == View.GONE) {
                        gridViewGoods.setVisibility(View.VISIBLE);
                        txtViewMore.setText("Collapse");
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        scrollView.smoothScrollTo(0, scrollView.getBottom());
                    } else {
                        gridViewGoods.setVisibility(View.GONE);
                        txtViewMore.setText("Expand");
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                        scrollView.smoothScrollTo(0, scrollView.getTop());
                    }
                }
            });
            goodsAdapter = new SimpleAdapter(getApplicationContext(), goodsList, R.layout.goods_gridview_item,
                    new String[]{"img", "id"}, new int[]{R.id.imgViewGoods, R.id.txtViewGoodsId});
            goodsAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    if ((view instanceof ImageView) && (data instanceof Bitmap)) {
                        ImageView imageView = (ImageView) view;
                        Bitmap bmp = (Bitmap) data;
                        imageView.setImageBitmap(bmp);
                        return true;
                    }
                    return false;
                }
            });
            gridViewGoods.setAdapter(goodsAdapter);
            for (int i = 0; i < goodId.length; i++) {
                // Check good image exist or not? Get good image from local database : Send get good image request
                if (helper.checkSpecifyGoodImageExist(goodId[i])) {
                    HashMap<String, Object> goodsMap = new HashMap<>();
                    goodsMap.put("img", getGoodsImageFromDatabase(goodId[i]));
                    goodsMap.put("id", goodId[i]);
                    goodsList.add(goodsMap);
                    goodsAdapter.notifyDataSetChanged();
                } else {
                    // Send get good image request
                    if (progressDialog == null)
                        progressDialog = ProgressDialog.show(ShipmentInfo_Activity.this, "Loading...", "Waiting for server response", true);
                    new SendGetGoodImageRequest(goodId[i]).start();
                }
            }
            gridViewGoods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getApplicationContext(), GoodsInfo_Activity.class);
                    i.putExtra("goodId", goodId[position]);
                    startActivityForResult(i, 1);
                }
            });
            gridViewGoods.setOnScrollListener(new AbsListView.OnScrollListener() {
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
    }

    // Get order from local database
    private void getOrderFromDatabase() {
        Cursor cursor = helper.getOrderMoreDetails(orderId);
        cursor.moveToFirst();
        try {
            JSONObject senderObject = new JSONObject(cursor.getString(0));
            sender = senderObject.getString("name");
            JSONObject receiverObject = new JSONObject(cursor.getString(1));
            receiver = receiverObject.getString("name");
            JSONObject departureObject = new JSONObject(cursor.getString(2));
            if (departureObject.getString("type").equals("SpecifyAddress"))
                departure = departureObject.getString("long_name");
            else
                departure = departureObject.getString("short_name");
            JSONObject destinationObject = new JSONObject(cursor.getString(3));
            if (destinationObject.getString("type").equals("SpecifyAddress"))
                destination = destinationObject.getString("long_name");
            else
                destination = destinationObject.getString("short_name");
            numberOfGoods = cursor.getInt(4);
            orderTime = cursor.getString(5);
            state = cursor.getString(6);
            JSONArray goodIdArray = new JSONArray(cursor.getString(7));
            goodId = new String[goodIdArray.length()];
            for (int i = 0; i < goodIdArray.length(); i++) {
                goodId[i] = goodIdArray.getString(i);
            }
            orderType = cursor.getString(8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
    }

    // Get good image from local database
    private Bitmap getGoodsImageFromDatabase(String goodId) {
        Cursor cursor = helper.getSpecifyGoodImage(goodId);
        cursor.moveToFirst();
        byte bytes[] = Base64.decode(cursor.getString(0), Base64.DEFAULT);
        cursor.close();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    Thread sendGetOrderDetailRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.getOrderDetails(orderId);
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        // Save good to local database
                        helper.updateOrderDetails(result.getJSONObject("content"), orderId);
                        getOrderFromDatabase();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                setAllView();
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
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    Thread sendCancelOrderRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.cancelOrder(orderId);
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        // Save good to local database
                        helper.deleteOrder(orderId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Order Canceled", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
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

    class SendGetGoodImageRequest extends Thread {
        String goodId;

        public SendGetGoodImageRequest(String goodId) {
            this.goodId = goodId;
        }

        public void run() {
            final JSONObject result = HTTP.getGoodImage(goodId);
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        // Save good image to local database
                        helper.insertGoodImage(result.getJSONObject("content").getString("goods_photo"), goodId);
                        HashMap<String, Object> goodsMap = new HashMap<>();
                        goodsMap.put("img", getGoodsImageFromDatabase(goodId));
                        goodsMap.put("id", goodId);
                        goodsList.add(goodsMap);
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
                runOnUiThread(new Runnable() {
                    public void run() {
                        goodsAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }
}
