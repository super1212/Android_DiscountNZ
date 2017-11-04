package com.discountnz.android.discountnz.util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Map;

/**
 * Created by apple on 29/10/17.
 */

public class ImageHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Map myMap = (Map) msg.obj;
        ImageView myImageView = (ImageView) myMap.get("imageView");
        Bitmap myBitMap = (Bitmap) myMap.get("bitMap");
        myImageView.setImageBitmap(myBitMap);
    }
}
