package com.discountnz.android.discountnz;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.discountnz.android.discountnz.ProductDetailsDisplay.InfoPage;
import com.discountnz.android.discountnz.manage.TableManage;
import com.discountnz.android.discountnz.model.Product;
import com.discountnz.android.discountnz.util.ImageHandler;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    List categoryList = new ArrayList<String>(); //category list
    List providerList = new ArrayList<String>(); // provider(brand) list
    List dateList = Arrays.asList("Today", "Tomorrow", "This Week", "Next Week");
    List<Product> productList = new ArrayList<Product>(); //current product list
    List<Product> allProductList = new ArrayList<Product>(); // all products list
    int showGridType = 0;//0:list table, 1: grid Table
    RequestQueue queue = null; //request queue
    TableManage tableManage = new TableManage();

    //
    View.OnClickListener lineOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            int index = arg0.getId();
            Product currentProduct = (Product) productList.get(index);
            Intent info = new Intent(MainActivity.this, InfoPage.class);
            info.putExtra("product", currentProduct);
            startActivityForResult(info,100);
        }
    };

    View.OnClickListener imageButtonListener = new View.OnClickListener(){
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View arg0) {
            if(showGridType == 1){
                showGridType = 0;
                arg0.setBackground(getDrawable(R.drawable.list));
            }else{
                showGridType = 1;
                arg0.setBackground(getDrawable(R.drawable.p4));
            }
            showGridData();
        }

    };
    View.OnClickListener filterButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent filter = new Intent(MainActivity.this, FilterPage.class);
            Bundle cat = new Bundle();
            cat.putSerializable("category", (Serializable) categoryList);
            cat.putSerializable("brand", (Serializable) providerList);
            cat.putSerializable("date", (Serializable) dateList);
            filter.putExtra("categoryBundle", cat);
            filter.putExtra("brandBundle", cat);
            filter.putExtra("dateBundle", cat);
            startActivityForResult(filter,100);
        }
    };
    //get requestQueue and keep the only one object in this class
    public RequestQueue getRequestQueue(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        return queue;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //initial map button
        initMapButton();


        Button filterButton = (Button) findViewById(R.id.button2);
        filterButton.setOnClickListener(filterButtonListener);

        Button viewButton = (Button) findViewById(R.id.viewButton);
        viewButton.setOnClickListener(imageButtonListener);
        //check is it from filter page go back

        getProductsInfo();
//        showGridData();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            String category = data.getStringExtra("category");
            String brand = data.getStringExtra("brand");
            String dateStr = data.getStringExtra("date");
            boolean isFilter = data.getBooleanExtra("isFilter", false);
            if(isFilter){
                this.doFilter(category, brand, dateStr);
                showGridData();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void initMapButton(){
        Button mapButton = (Button)findViewById(R.id.button3);
        mapButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent map = new Intent(MainActivity.this, com.discountnz.android.discountnz.manage.Map.class);

                startActivity(map);

            }
        });
    }


    private void doFilter(String category, String brand, String dateStr){
        productList = new ArrayList<Product>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for(Product proItem : allProductList){
            if((category != null  && !category.isEmpty()) && !proItem.getCategory().equals(category)){
                continue;
            }
            if((brand != null && !brand.isEmpty()) && !proItem.getBrand().equals(brand)){
                continue;
            }

            Date todayDate = new Date();
            String todayStr = sdf.format(todayDate);
            String startDate = proItem.getStartDate();
            String endDate = proItem.getEndDate();

            if("Today".equals(dateStr)){
                if(todayStr.compareTo(startDate) < 0 || todayStr.compareTo(endDate) > 0){
                    continue;
                }
            }

            if("Tomorrow".equals(dateStr)){
                Date tomorrow = addDay(todayDate, 1);
                String tomorrowStr = sdf.format(tomorrow);
                if(tomorrowStr.compareTo(startDate) < 0 || tomorrowStr.compareTo(endDate) > 0){
                    continue;
                }
            }
            if("This Week".equals(dateStr)){
                Date after7day = addDay(todayDate, 7);
                String after7dayStr = sdf.format(after7day);
                if(after7dayStr.compareTo(startDate) < 0 || todayStr.compareTo(endDate) > 0){
                    continue;
                }
            }

            if("Next Week".equals(dateStr)){
                Date fromDay = addDay(todayDate, 7);
                String fromDayStr = sdf.format(fromDay);

                Date endDay = addDay(todayDate, 14);
                String endDayStr = sdf.format(endDay);
                if(endDayStr.compareTo(startDate) < 0 || fromDayStr.compareTo(endDate) > 0){
                    continue;
                }
            }
            productList.add(proItem);
        }
    }

    private Date addDay(Date date, int days){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, days);
        Date myDate = c.getTime();
        return myDate;
    }
    /**
     * Get the products information from my online json
     * This function get product information from online json
     *
     */
    private void getProductsInfo(){
        //get the request queue
        queue = getRequestQueue(getApplicationContext());
        //current response url
        String url = "https://gist.githubusercontent.com/super1212/82fe7cdbd3c4286c5bb5e98c7ee07c73/raw/products_Android.json";
        //use get method to get the json result
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //get information form the json object
                    try {
                        JSONArray dataList = response.getJSONArray("data");
                        if(dataList != null){
                            for(int i = 0; i < dataList.length(); i++){
                                JSONObject myJson = dataList.getJSONObject(i);
                                if(myJson == null){
                                    continue;
                                }
                                Product productItem = new Product();
                                productItem.setAddr((String) myJson.get("addr"));
                                productItem.setBrand((String) myJson.get("brand"));
                                productItem.setCategory((String) myJson.get("category"));
                                productItem.setEndDate((String) myJson.get("endDate"));
                                productItem.setImgUrl((String) myJson.get("imgUrl"));
                                productItem.setLatitude((String) myJson.get("latitude"));
                                productItem.setLongitude((String) myJson.get("longitude"));
                                productItem.setName((String) myJson.get("name"));
                                productItem.setPrice((String) myJson.get("price"));
                                productItem.setStartDate((String) myJson.get("startDate"));
                                productItem.setDesc((String) (myJson.get("desc")==null?"":myJson.get("desc")));

                                productList.add(productItem);
                                allProductList.add(productItem);

                                if(!categoryList.contains((String) myJson.get("category"))){
                                    categoryList.add((String) myJson.get("category"));
                                }
                                if(!providerList.contains((String) myJson.get("brand"))){
                                    providerList.add((String) myJson.get("brand"));
                                }
                                System.out.println(i);
                            }

                            showGridData();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    System.err.println(error.getMessage());
                }
            });
        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }
    private void showGridData(){
        TableLayout listTable = (TableLayout) findViewById(R.id.listTable); // get my undo button
        listTable.removeAllViews();
        listTable.setBackgroundColor(Color.rgb(0,0,0));
        if(showGridType == 0){
            setListView(productList, listTable);
        }else{
            setGridView(productList, listTable);
        }
    }


    private void setListView(List<Product> productList, TableLayout listTable){

        for (int row = 0; row < productList.size(); row ++){
            final Product product = (Product) productList.get(row);
            LinearLayout lineLayout = tableManage.getListLineLayout(this, product);
            lineLayout.setId(row);
            lineLayout.setOnClickListener(lineOnClickListener);
            listTable.addView(lineLayout);

        }
    }

    private void setGridView(List<Product> productList, TableLayout listTable){

        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int countBGColor = 0;
        for (int row = 0; row < productList.size();){
            LinearLayout lineLayout = new LinearLayout(this);
            int num = 2;

            for(int i = 0; i < num; i++){
                TableLayout tableLayout;
                if(row < productList.size()){
                    final Product product = (Product) productList.get(row);
                    tableLayout = tableManage.getGridTableLayout(this, product);
                    tableLayout.setId(row);
                    tableLayout.setOnClickListener(lineOnClickListener);
                }else{
                    tableLayout = new TableLayout(getApplicationContext());
                    TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(
                            (width/2-2), width/2);
                    tableLayoutParams.width = width/2;
                    tableLayoutParams.height = width/2 + 700;
                    tableLayout.setLayoutParams(tableLayoutParams);
                }
                TableLayout.LayoutParams tableLayoutParams = (TableLayout.LayoutParams) tableLayout.getLayoutParams();
                if(tableLayoutParams != null){
                    if(num == 1){
                        tableLayoutParams.setMargins(0,0,0,1);
                    }else{
                        tableLayoutParams.setMargins(0,0,1,1);
                    }
                    tableLayout.setLayoutParams(tableLayoutParams);
                }
                tableLayout.setBackgroundColor(Color.rgb(255,255,255));

                lineLayout.addView(tableLayout);
                row++;
            }
            listTable.addView(lineLayout);
            countBGColor++;
        }
    }
}
