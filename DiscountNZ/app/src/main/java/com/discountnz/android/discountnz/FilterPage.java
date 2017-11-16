package com.discountnz.android.discountnz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.discountnz.android.discountnz.model.Product;
import com.discountnz.android.discountnz.util.ImageHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FilterPage extends AppCompatActivity {
    private String sBrand;
    private String sCategory;
    private String sDate;

    ImageHandler handler = new ImageHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_page);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("categoryBundle");
//        Bundle args2 = intent.getBundleExtra("brandBundle");
        List categoryList = (ArrayList<String>)args.getSerializable("category");
        List brandList = (ArrayList<String>)args.getSerializable("brand");
        List dateList = (ArrayList<String>)args.getSerializable("date");


        final Button btnBack = (Button)findViewById(R.id.btnBack);
        final Button btnConfirm = (Button)findViewById(R.id.btnConfirm);
        final TextView txtCategory = (TextView)findViewById(R.id.txtCategory);
        final TextView txtBrand = (TextView)findViewById(R.id.txtBrand);
        final TextView txtDate = (TextView)findViewById(R.id.txtDate);
        final ListView categoryListView = (ListView)findViewById(R.id.categoryListView);
        final ListView brandListView = (ListView)findViewById(R.id.brandListView);
        final ListView dateListView = (ListView)findViewById(R.id.dateListView);

        String[] brandItems = new String[brandList.size()];
        for (int i = 0; i < brandList.size(); i++) {
            brandItems[i] = brandList.get(i).toString();
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, brandItems);
        brandListView.setAdapter(adapter);
        brandListView.setPadding(72, 0, 0, 0);
        justifyListViewHeightBasedOnChildren(brandListView);


        String[] categoryItems = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            categoryItems[i] = categoryList.get(i).toString();
        }
        ArrayAdapter adapterCat = new ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryItems);
        categoryListView.setAdapter(adapterCat);
        categoryListView.setPadding(72, 0, 0, 0);
        justifyListViewHeightBasedOnChildren(categoryListView);

        String[] dateItems = new String[dateList.size()];
        for (int i = 0; i < dateList.size(); i++) {
            dateItems[i] = dateList.get(i).toString();
        }
        ArrayAdapter adapterDate = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dateItems);
        dateListView.setAdapter(adapterDate);
        dateListView.setPadding(72, 0, 0, 0);
        justifyListViewHeightBasedOnChildren(dateListView);

//        final ImageView productImage = (ImageView)findViewById(R.id.productImage);
//        final ImageButton btnLocation = (ImageButton)findViewById(R.id.btnLocation);
//        final Product thisProduct = (Product)getIntent().getSerializableExtra("product");
//
//        txtName.setText("Name: " + thisProduct.getName());
//        txtBrand.setText("Brand: " + thisProduct.getBrand());
//        txtAddress.setText("Address: " + thisProduct.getAddr());
//        txtPrice.setText("Price: " + thisProduct.getPrice());

//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                Message msg = handler.obtainMessage();
//                Map<String, Object> map = new HashMap();
//                map.put("imageView", productImage);
//                map.put("bitMap", returnBitMap(thisProduct.getImgUrl()));
//                msg.obj = map;
//                msg.sendToTarget();
//            }
//        }).start();


        brandListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sBrand = (String) adapterView.getItemAtPosition(i);
                txtBrand.setText("Brand - " + adapterView.getItemAtPosition(i));
            }
        });

        dateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sDate = (String) adapterView.getItemAtPosition(i);
                txtDate.setText("Date - " + adapterView.getItemAtPosition(i));
            }
        });

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sCategory = (String) adapterView.getItemAtPosition(i);
                txtCategory.setText("Category - " + adapterView.getItemAtPosition(i));
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent();
                setResult(RESULT_OK, back);
                finish();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent filterIntent = new Intent(FilterPage.this, MainActivity.class);
                filterIntent.putExtra("category", sCategory);
                filterIntent.putExtra("brand", sBrand);
                filterIntent.putExtra("date", sDate);
                startActivityForResult(filterIntent,100);
            }
        });

//        btnLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent viewLocation = new Intent(InfoPage.this, ProductAddressPage.class);
////                viewLocation.putExtra("longitude", thisProduct.getLongitude());
////                viewLocation.putExtra("latitude", thisProduct.getLatitude());
////                viewLocation.putExtra("name", thisProduct.getName());
////                startActivityForResult(viewLocation,101);
//            }
//        });
    }

//    public Bitmap returnBitMap(String url){
//        URL myFileUrl = null;
//        Bitmap bitmap = null;
//        try {
//            myFileUrl = new URL(url);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        try {
//            HttpURLConnection conn = (HttpURLConnection) myFileUrl
//                    .openConnection();
//            conn.setDoInput(true);
//            conn.connect();
//            InputStream is = conn.getInputStream();
//            bitmap = BitmapFactory.decodeStream(is);
//            is.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }

    public void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }
}