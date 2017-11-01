package com.discountnz.android.discountnz.manage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Message;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.discountnz.android.discountnz.model.AutoSplitTextView;
import com.discountnz.android.discountnz.model.Product;
import com.discountnz.android.discountnz.util.ImageHandler;
import com.discountnz.android.discountnz.util.UtilServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by apple on 29/10/17.
 */

public class TableManage {
    ImageHandler handler = new ImageHandler();
    public TableLayout getGridTableLayout(final Context context, final Product product){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        TableLayout tableLayout = new TableLayout(context);
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(
                (width/2-2), width/2);
//            tableLayoutParams.weight = 7;
        tableLayoutParams.width = width/2;
        tableLayoutParams.height = width/2 + 600;
        tableLayoutParams.setMargins(3,3,3,3);
        tableLayout.setLayoutParams(tableLayoutParams);


        LinearLayout subImageLinearLayout = new LinearLayout(context);
        final ImageView imageView = new ImageView(context);
        new Thread(new Runnable(){
            @Override
            public void run() {
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                int width = wm.getDefaultDisplay().getWidth();
                UtilServer myUtilServer = new UtilServer();
                Message msg = handler.obtainMessage();
                Map<String, Object> map = new HashMap();
                map.put("imageView", imageView);
                map.put("bitMap", returnBitMap(product.getImgUrl()));
                map.put("width", width/2-2);
                map.put("height", width/2);
                msg.obj = map;
                msg.sendToTarget();
            }
        }).start();
        subImageLinearLayout.addView(imageView);

        LinearLayout subLinearLayout1 = new LinearLayout(context);
//            subTableRow1.setLayoutParams(new TableRow.LayoutParams(
//                    width - 400, 300));

        AutoSplitTextView textView1 = new AutoSplitTextView(context);
        textView1.setLayoutParams(new ViewGroup.LayoutParams(width/2, 400));
        textView1.setText(product.getAddr()+product.getStartDate()+product.getEndDate());
        subLinearLayout1.addView(textView1);

        LinearLayout subLinearLayout2 = new LinearLayout(context);
//            subTableRow2.setLayoutParams(new TableRow.LayoutParams(
//                    width - 400, 100));
        AutoSplitTextView textView2 = new AutoSplitTextView(context);
        textView2.setLayoutParams(new ViewGroup.LayoutParams(width/2, 200));
        textView2.setText(product.getBrand()+product.getCategory()+product.getName());
        subLinearLayout2.addView(textView2);

        tableLayout.addView(subImageLinearLayout);
        tableLayout.addView(subLinearLayout1);
        tableLayout.addView(subLinearLayout2);
        return tableLayout;
    }


    public LinearLayout getListLineLayout(Context context, final Product product){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();

        LinearLayout lineLayout = new LinearLayout(context);
//            tableRow.setLayoutParams(new TableRow.LayoutParams(
//                    100, 30));

        final ImageView imageView = new ImageView(context);

        new Thread(new Runnable(){
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                Map<String, Object> map = new HashMap();
                map.put("imageView", imageView);
                map.put("bitMap", returnBitMap(product.getImgUrl()));
                map.put("width", 400);
                map.put("height", 400);
                msg.obj = map;
                msg.sendToTarget();
            }
        }).start();


        TableLayout tableLayout = new TableLayout(context);
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(
                (width - 400), 400);
//            tableLayoutParams.weight = 7;
        tableLayoutParams.width = width - 400;
        tableLayoutParams.height = 400;
        tableLayoutParams.setMargins(3,3,3,3);
        tableLayout.setLayoutParams(tableLayoutParams);
        LinearLayout subLinearLayout1 = new LinearLayout(context);
//            subTableRow1.setLayoutParams(new TableRow.LayoutParams(
//                    width - 400, 300));

        AutoSplitTextView textView1 = new AutoSplitTextView(context);
        textView1.setLayoutParams(new ViewGroup.LayoutParams(width - 400, 300));
        textView1.setText("Wwwwwww wwwwwwwcccc ccccccccccw wccccwwwww wwwww"+product.getAddr()+product.getStartDate()+product.getEndDate());
        subLinearLayout1.addView(textView1);

        LinearLayout subLinearLayout2 = new LinearLayout(context);
//            subTableRow2.setLayoutParams(new TableRow.LayoutParams(
//                    width - 400, 100));
        AutoSplitTextView textView2 = new AutoSplitTextView(context);
        textView2.setLayoutParams(new ViewGroup.LayoutParams(width - 400, 100));
        textView2.setText(product.getBrand()+product.getCategory()+product.getName());
        subLinearLayout2.addView(textView2);

        tableLayout.addView(subLinearLayout1);
        tableLayout.addView(subLinearLayout2);
        lineLayout.addView(imageView);
        lineLayout.addView(tableLayout);
        return lineLayout;
    }

    public Bitmap returnBitMap(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
