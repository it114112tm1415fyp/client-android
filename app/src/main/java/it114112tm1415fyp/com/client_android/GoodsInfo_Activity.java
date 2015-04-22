package it114112tm1415fyp.com.client_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
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

public class GoodsInfo_Activity extends Activity {

    private DataBaseHelper helper;
    private SimpleAdapter lstGoodsLocationAd;

    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lstGoodsLocation;
    private TextView txtViewId, txtViewWeight, txtViewFragile, txtViewFlammable, txtViewLocation, txtViewLastUpdateTime;

    private List<HashMap<String, Object>> locationList = new ArrayList<>();

    private String fragile, flammable, location, lastUpdateTime;
    private String goodId;
    private double weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_info);
        goodId = getIntent().getStringExtra("goodId");
        helper = new DataBaseHelper(getApplicationContext());
        findAllView();
        // Check goods exist or not? Get goods from local database : Send get goods detail request
        if (helper.checkSpecifyGoodExist(goodId)) {
            getGoodFromDatabase(goodId);
            setAllView();
        } else {
            // Check network state
            if (!PublicData.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            // Send get good detail request
            if (progressDialog == null)
                progressDialog = ProgressDialog.show(GoodsInfo_Activity.this, "Loading...", "Waiting for server response", true);
            new Thread(sendGetGoodDetailRequest).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(1);
    }

    public void findAllView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.laySwipe);
        lstGoodsLocation = (ListView) findViewById(R.id.lstGoodsLocation);
        txtViewId = (TextView) findViewById(R.id.txtViewGoodsId);
        txtViewWeight = (TextView) findViewById(R.id.txtViewWeight);
        txtViewFragile = (TextView) findViewById(R.id.txtViewFragile);
        txtViewFlammable = (TextView) findViewById(R.id.txtViewFlammable);
        txtViewLocation = (TextView) findViewById(R.id.txtViewLocation);
        txtViewLastUpdateTime = (TextView) findViewById(R.id.txtViewLastUpdateTime);
    }

    public void setAllView() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Thread(sendGetGoodDetailRequest).start();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
//        lstGoodsLocationAd = new SimpleAdapter(getApplicationContext(), locationList, R.layout.goods_location_item,
//                new String[]{"img", "location"}, new int[]{R.id.imgViewLocation, R.id.txtViewLocation});
        lstGoodsLocationAd = new SimpleAdapter(getApplicationContext(), locationList, R.layout.goods_location_item,
                new String[]{"time", "location"}, new int[]{R.id.txtViewTime, R.id.txtViewLocation});
        lstGoodsLocation.setAdapter(lstGoodsLocationAd);
        lstGoodsLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        swipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }

                return false;
            }
        });
        txtViewId.setText(goodId);
        txtViewWeight.setText(weight + "");
        txtViewFragile.setText(fragile);
        txtViewFlammable.setText(flammable);
        txtViewLocation.setText(location);
        txtViewLastUpdateTime.setText(lastUpdateTime);
    }

    // Get good from local database
    public void getGoodFromDatabase(String goodId) {
        locationList.clear();
        Cursor cursor = helper.getGoodsDetails(goodId);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            weight = cursor.getDouble(0);
            fragile = cursor.getString(1);
            flammable = cursor.getString(2);
            lastUpdateTime = cursor.getString(4);
            try {
                JSONObject locationItem = new JSONObject(cursor.getString(3));
                if (locationItem.getString("type").equals("SpecifyAddress"))
                    location = locationItem.getString("long_name");
                else
                    location = locationItem.getString("short_name");
                JSONArray locationArray = new JSONArray(cursor.getString(5));
                for (int i = 0; i < locationArray.length(); i++) {
                    JSONObject logsObject = locationArray.getJSONObject(i);
                    JSONObject locationObject = logsObject.getJSONObject("location");
                    String locationType = locationObject.getString("type");
                    HashMap locationMap = new HashMap();
                    switch (locationType) {
                        case "SpecifyAddress":
                            locationMap.put("location", locationObject.getString("short_name"));
                            break;
                        case "Car":
                            locationMap.put("location", "Car");
                            break;
                        case "Store":
                            locationMap.put("location", locationObject.getString("short_name"));
                            break;
                        case "Shop":
                            locationMap.put("location", locationObject.getString("short_name"));
                            break;
                        default:
                            break;
                    }
                    locationMap.put("time", DateFormator.getDateTime(logsObject.getString("time")));
                    locationList.add(locationMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
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
            case 0:     // Send get good details request
                progressDialog = ProgressDialog.show(GoodsInfo_Activity.this, "Loading...", "Waiting for server response", true);
                new Thread(sendGetGoodDetailRequest).start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    Thread sendGetGoodDetailRequest = new Thread() {
        public void run() {
            final JSONObject result = HTTP.getGoodDetail(goodId);
            try {
                if (result != null) {
                    if (result.getBoolean("success")) {
                        // Save good to local database
                        helper.updateGoodsDetails(result.getJSONObject("content"), goodId);
                        getGoodFromDatabase(goodId);
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
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }
    };
}
