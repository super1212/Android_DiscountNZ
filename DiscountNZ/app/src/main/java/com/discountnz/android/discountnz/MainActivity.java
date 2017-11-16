package com.discountnz.android.discountnz;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.R.attr.end;
import static android.R.attr.value;


public class MainActivity extends AppCompatActivity {
    List categoryList = new ArrayList<String>();
    List providerList = new ArrayList<String>();
    List dateList = Arrays.asList("Today", "Tomorrow", "This Week", "Next Week");
    List<Product> productList = new ArrayList<Product>();
    List<Product> allProductList = new ArrayList<Product>();
    int showGridType = 0;//0:list table, 1: grid Table
    RequestQueue queue = null; //request queue
    PolylineOptions polylineOptions = null;
    ImageHandler handler = new ImageHandler();
    TableManage tableManage = new TableManage();

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

    //get requestQueue and keep the only one object in this class
    public RequestQueue getRequestQueue(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        return queue;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Intent intent = getIntent();
        String category = intent.getStringExtra("category");
        String brand = intent.getStringExtra("brand");
        String dateStr = intent.getStringExtra("date");
        boolean isFilter = intent.getBooleanExtra("isFilter", false);


        Button viewButton = (Button) findViewById(R.id.viewButton);
        viewButton.setOnClickListener(imageButtonListener);
        //check is it from filter page go back
        if(isFilter){
            doFilter(category, brand, dateStr);
            showGridData();
        }else{
            getProductsInfo();
        }
//        showGridData();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void doFilter(String category, String brand, String dateStr){
        productList = new ArrayList<Product>();
        for(Product proItem : allProductList){
            if(!category.isEmpty() && !proItem.getCategory().equals(category)){
                continue;
            }
            if(!brand.isEmpty() && !proItem.getBrand().equals(brand)){
                continue;
            }
            LocalDate todayDate = LocalDate.now();
            DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern("YYYY-MM-DD");
            LocalDate startDate = LocalDate.parse(proItem.getStartDate(), myFormatter);
            LocalDate endDate = LocalDate.parse(proItem.getEndDate(), myFormatter);

            if("Today".equals(dateStr)){
                if(todayDate.isAfter(startDate) || todayDate.isBefore(endDate)){
                    continue;
                }
            }

            if("Tomorrow".equals(dateStr)){
                if(todayDate.plusDays(1).isAfter(startDate) || todayDate.plusDays(1).isBefore(endDate)){
                    continue;
                }
            }
            if("This Week".equals(dateStr)){
                LocalDate lastday = todayDate.plusDays(7);
                if(todayDate.isAfter(endDate)|| lastday.isBefore(startDate)){
                    continue;
                }
            }

            if("Next Week".equals(dateStr)){
                LocalDate fromDate = todayDate.plusDays(7);
                LocalDate lastday = todayDate.plusDays(14);
                if(fromDate.isAfter(endDate)|| lastday.isBefore(startDate)){
                    continue;
                }
            }
            productList.add(proItem);
        }
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
            int num = 1;
            if(row + 1 < productList.size()){
                num++;
            }
            for(int i = 0; i < num; i++){
                final Product product = (Product) productList.get(row);
                TableLayout tableLayout = tableManage.getGridTableLayout(this, product);
                TableLayout.LayoutParams tableLayoutParams = (TableLayout.LayoutParams) tableLayout.getLayoutParams();
                if(num == 1){
                    tableLayoutParams.setMargins(0,0,0,1);
                }else{
                    tableLayoutParams.setMargins(0,0,1,1);
                }
                tableLayout.setLayoutParams(tableLayoutParams);
                tableLayout.setBackgroundColor(Color.rgb(255,255,255));
                lineLayout.addView(tableLayout);
                lineLayout.setId(row);
                lineLayout.setOnClickListener(lineOnClickListener);
                row++;
            }
            listTable.addView(lineLayout);
            countBGColor++;
        }
    }
}
