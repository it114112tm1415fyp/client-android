package it114112tm1415fyp.com.util;

import android.graphics.Bitmap;

import java.io.Serializable;

public class GoodsItem {
    public String goodId;
    public Bitmap bitmap;

    public GoodsItem(String goodId, Bitmap bitmap) {
        this.goodId = goodId;
        this.bitmap = bitmap;
    }

    public String getGoodId() {
        return goodId;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}

