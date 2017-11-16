package com.discountnz.android.discountnz.ProductDetailsDisplay;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.discountnz.android.discountnz.MainActivity;
import com.discountnz.android.discountnz.ProductAddressPage;
import com.discountnz.android.discountnz.R;
import com.discountnz.android.discountnz.model.Product;
import com.discountnz.android.discountnz.util.ImageHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class InfoPage extends AppCompatActivity {
    ImageHandler handler = new ImageHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);
        getSupportActionBar().hide();

        final ImageButton btnBack = (ImageButton)findViewById(R.id.btnBack);
        final TextView txtName = (TextView)findViewById(R.id.txtName);
        final TextView txtBrand = (TextView)findViewById(R.id.txtBrand);
        final TextView txtAddress = (TextView)findViewById(R.id.txtAddress);
        final TextView txtPrice = (TextView)findViewById(R.id.txtPrice);
        final ImageView productImage = (ImageView)findViewById(R.id.productImage);
        final ImageButton btnLocation = (ImageButton)findViewById(R.id.btnLocation);
        final Product thisProduct = (Product)getIntent().getSerializableExtra("product");

        txtName.setText("Name: " + thisProduct.getName());
        txtBrand.setText("Brand: " + thisProduct.getBrand());
        txtAddress.setText("Address: " + thisProduct.getAddr());
        txtPrice.setText("Price: " + thisProduct.getPrice());

        new Thread(new Runnable(){
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                Map<String, Object> map = new HashMap();
                map.put("imageView", productImage);
                map.put("bitMap", returnBitMap(thisProduct.getImgUrl()));
                msg.obj = map;
                msg.sendToTarget();
            }
        }).start();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent();
                setResult(RESULT_OK, back);
                finish();
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewLocation = new Intent(InfoPage.this, ProductAddressPage.class);
                viewLocation.putExtra("longitude", thisProduct.getLongitude());
                viewLocation.putExtra("latitude", thisProduct.getLatitude());
                viewLocation.putExtra("name", thisProduct.getName());
                startActivityForResult(viewLocation,101);
            }
        });
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
