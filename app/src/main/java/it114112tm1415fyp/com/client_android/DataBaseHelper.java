package it114112tm1415fyp.com.client_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

public class DataBaseHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "DataBase";
    private final static String TABLE_ORDERS = "Orders";
    private final static String TABLE_GOODS = "Goods";
    private final static int DATABASE_VERSION = 1;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private final static String CREATE_TABLE_ORDER =
            "CREATE TABLE " + TABLE_ORDERS +
                    " (id int, sender TEXT, receiver TEXT, departure TEXT, destination TEXT" +
                    ", goods_number int, goods_ids TEXT, orderType TEXT" +
                    ", state TEXT, created_at TEXT, updated_at TEXT);";

    private final static String CREATE_TABLE_GOODS =
            "CREATE TABLE " + TABLE_GOODS +
                    " (id int, weight DOUBLE, fragile Boolean, flammable Boolean" +
                    ", location TEXT, updated_at TEXT, check_logs TEXT, picture TEXT);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ORDER);
        db.execSQL(CREATE_TABLE_GOODS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

    // Delete all record from database
    public void deleteAllRecord(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + TABLE_ORDERS);
        db.execSQL("DELETE FROM " + TABLE_GOODS);
    }

    //
    // "Orders" table methods
    //

    // Check order exist or not
    public Boolean checkOrderExist(int orderId) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id FROM " + TABLE_ORDERS + " WHERE  id = " + orderId + ";", null);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    // Insert order record
    public void insertOrder(JSONObject orderObject, String orderType) {
        try {
            int id = orderObject.getInt("id");
            if (!checkOrderExist(id)) {
                ContentValues cv = new ContentValues();
                cv.put("id", id);
                cv.put("sender", orderObject.getJSONObject("sender").toString());
                cv.put("receiver", orderObject.getJSONObject("receiver").toString());
                cv.put("departure", orderObject.getJSONObject("departure").toString());
                cv.put("destination", orderObject.getJSONObject("destination").toString());
                cv.put("goods_number", orderObject.getInt("goods_number"));
                cv.put("goods_ids", orderObject.getJSONArray("goods_ids").toString());
                cv.put("state", orderObject.getString("order_state"));
                cv.put("created_at", DateFormator.getDateTime(orderObject.getString("created_at")));
                cv.put("updated_at", DateFormator.getDateTime(orderObject.getString("updated_at")));
                cv.put("orderType", orderType);

                this.getWritableDatabase().insert(TABLE_ORDERS, null, cv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update order details
    public void updateOrderDetails(JSONObject orderObject, int orderId) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("sender", orderObject.getJSONObject("sender").toString());
            cv.put("receiver", orderObject.getJSONObject("receiver").toString());
            cv.put("departure", orderObject.getJSONObject("departure").toString());
            cv.put("destination", orderObject.getJSONObject("destination").toString());
            cv.put("goods_number", orderObject.getInt("goods_number"));
            cv.put("goods_ids", orderObject.getJSONArray("goods_ids").toString());
            cv.put("state", orderObject.getString("order_state"));
            cv.put("created_at", DateFormator.getDateTime(orderObject.getString("created_at")));
            cv.put("updated_at", DateFormator.getDateTime(orderObject.getString("updated_at")));
            this.getWritableDatabase().update(TABLE_ORDERS, cv, "id = '" + orderId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete order
    public void deleteOrder(int orderId) {
        this.getWritableDatabase().delete(TABLE_ORDERS, "id = '" + orderId + "'", null);
    }

    // Get order list
    public Cursor getOrderShowBy(String showBy) {
        if (showBy.equals("")) {
            return getReadableDatabase().rawQuery(
                    "SELECT id, sender, receiver, goods_number, state, updated_at, orderType FROM " + TABLE_ORDERS +
                            " ORDER BY updated_at DESC;", null);
        } else {
            return getReadableDatabase().rawQuery(
                    "SELECT id, sender, receiver, goods_number, state, updated_at, orderType FROM " + TABLE_ORDERS +
                            " WHERE orderType = '" + showBy + "' ORDER BY updated_at DESC;", null);
        }
    }

    // Get order details
    public Cursor getOrderMoreDetails(int orderId) {
        return getReadableDatabase().rawQuery(
                "SELECT sender, receiver, departure, destination, goods_number, created_at, state, goods_ids, orderType" +
                        " FROM " + TABLE_ORDERS + " WHERE id = " + orderId + ";", null);
    }

    // Get order more details
    public Cursor getOrderDetails(int orderId) {
        return getReadableDatabase().rawQuery(
                "SELECT sender, receiver, departure, destination, goods_number" +
                        " FROM " + TABLE_ORDERS + " WHERE id = " + orderId + ";", null);
    }

    //
    // "Goods" table methods
    //

    // Check goods exist or not
    public Boolean checkSpecifyGoodExist(String goodId) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT updated_at FROM " + TABLE_GOODS + " WHERE id = '" + goodId + "';", null);
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() != 0 && cursor.getString(0) != null) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    // Check goods image exist or not
    public Boolean checkSpecifyGoodImageExist(String goodId) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT picture FROM " + TABLE_GOODS + " WHERE id = '" + goodId + "';", null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    // Insert good image
    public void insertGoodImage(String picture, String goodId) {
        ContentValues cv = new ContentValues();
        cv.put("picture", picture);
        Log.e("picture", picture);
        cv.put("id", goodId);
        this.getWritableDatabase().insert(TABLE_GOODS, null, cv);
    }

    // Get good image
    public Cursor getSpecifyGoodImage(String goodId) {
        return getReadableDatabase().rawQuery(
                "SELECT picture FROM " + TABLE_GOODS + " WHERE id = '" + goodId + "';", null);
    }

    // Update good details
    public void updateGoodsDetails(JSONObject good, String goodId) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("location", good.getJSONObject("location").toString());
            cv.put("weight", good.getDouble("weight"));
            cv.put("fragile", good.getString("fragile"));
            cv.put("flammable", good.getString("flammable"));
            cv.put("updated_at", DateFormator.getDateTime(good.getString("updated_at")));
            cv.put("check_logs", good.getJSONArray("check_logs").toString());
            this.getWritableDatabase().update(TABLE_GOODS, cv, "id = '" + goodId + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get good details
    public Cursor getGoodsDetails(String goodId) {
        return getReadableDatabase().rawQuery(
                "SELECT weight, fragile, flammable, location, updated_at, check_logs, picture FROM " + TABLE_GOODS +
                        " WHERE id = '" + goodId + "';", null);
    }

}
