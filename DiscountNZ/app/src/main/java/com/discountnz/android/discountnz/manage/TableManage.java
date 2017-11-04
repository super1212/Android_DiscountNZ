package com.discountnz.android.discountnz.manage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Message;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

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
        tableLayout.setLayoutParams(tableLayoutParams);


        LinearLayout subImageLinearLayout = new LinearLayout(context);
        final ImageView imageView = new ImageView(context);

        ViewGroup.LayoutParams imageViewparams = new ViewGroup.LayoutParams(width/2-2,width/2);
        imageView.setLayoutParams(imageViewparams);

        new Thread(new Runnable(){
            @Override
            public void run() {
//                UtilServer myUtilServer = new UtilServer();
                Message msg = handler.obtainMessage();
                Map<String, Object> map = new HashMap();
                map.put("imageView", imageView);
                map.put("bitMap", returnBitMap(product.getImgUrl()));
                msg.obj = map;
                msg.sendToTarget();
            }
        }).start();
        subImageLinearLayout.addView(imageView);

        LinearLayout subLinearLayout1 = new LinearLayout(context);
        AutoSplitTextView textView1 = new AutoSplitTextView(context);
        textView1.setLayoutParams(new ViewGroup.LayoutParams(width/2, 400));
        textView1.setText(product.getAddr()+" "+product.getStartDate()+" "+product.getEndDate());
        subLinearLayout1.addView(textView1);

        LinearLayout subLinearLayout2 = new LinearLayout(context);
        AutoSplitTextView textView2 = new AutoSplitTextView(context);
        textView2.setLayoutParams(new ViewGroup.LayoutParams(width/2, 200));
        textView2.setText(product.getBrand()+" "+product.getCategory()+" "+product.getName());
        textView2.setTextColor(Color.BLUE);
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
        final ImageView imageView = new ImageView(context);

        ViewGroup.LayoutParams imageViewparams = new ViewGroup.LayoutParams(400, 400);
        imageView.setLayoutParams(imageViewparams);

        imageView.setBackgroundColor(Color.rgb(255,255,255));
        new Thread(new Runnable(){
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                Map<String, Object> map = new HashMap();
                map.put("imageView", imageView);
                map.put("bitMap", returnBitMap(product.getImgUrl()));
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
        tableLayoutParams.setMargins(1,0,1,1);
        tableLayout.setLayoutParams(tableLayoutParams);
        LinearLayout subLinearLayout1 = new LinearLayout(context);
//            subTableRow1.setLayoutParams(new TableRow.LayoutParams(
//                    width - 400, 300));

        AutoSplitTextView textView1 = new AutoSplitTextView(context);
        textView1.setLayoutParams(new ViewGroup.LayoutParams(width - 400, 300));
        textView1.setText(product.getAddr()+" "+product.getStartDate()+" "+product.getEndDate());
        subLinearLayout1.addView(textView1);

        LinearLayout subLinearLayout2 = new LinearLayout(context);
//            subTableRow2.setLayoutParams(new TableRow.LayoutParams(
//                    width - 400, 100));
        AutoSplitTextView textView2 = new AutoSplitTextView(context);
        textView2.setLayoutParams(new ViewGroup.LayoutParams(width - 400, 100));
        textView2.setText(product.getBrand()+" "+product.getCategory()+" "+product.getName());
        textView2.setTextColor(Color.BLUE);
        subLinearLayout2.addView(textView2);

        tableLayout.setBackgroundColor(Color.rgb(255,255,255));
        tableLayout.addView(subLinearLayout1);
        tableLayout.addView(subLinearLayout2);
        lineLayout.addView(imageView);
        lineLayout.addView(tableLayout);

//        GradientDrawable drawable = new GradientDrawable();
//        drawable.setShape(GradientDrawable.RECTANGLE);
//        drawable.setStroke(1, Color.BLACK);
        lineLayout.setDividerPadding(2);

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
