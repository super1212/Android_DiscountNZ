package com.discountnz.android.discountnz;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.discountnz.android.discountnz.manage.TableManage;
import com.discountnz.android.discountnz.model.Product;
import com.discountnz.android.discountnz.util.ImageHandler;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    List categoryList = new ArrayList<String>();
    List brandList = new ArrayList<String>();
    List dateList = Arrays.asList("All Date", "Today", "Tomorrow", "This Week", "Next Week");
    List productList = new ArrayList<Product>();
    List allProductList = new ArrayList<Product>();
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

        Button viewButton = (Button) findViewById(R.id.viewButton);

        View.OnClickListener imageButtonListener = new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                if(showGridType == 1){
                    showGridType = 0;
                }else{
                    showGridType = 1;
                }
                showGridData();
            }

        };
        viewButton.setOnClickListener(imageButtonListener);
        getProductsInfo();
//        showGridData();

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
        String url = "https://gist.githubusercontent.com/super1212/d0a3131282534ebe60b581b8a7f7be1f/raw/products.json";
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
                                productList.add(productItem);
                                allProductList.add(productItem);

                                if(!categoryList.contains((String) myJson.get("category"))){
                                    categoryList.add((String) myJson.get("category"));
                                }
                                if(!brandList.contains((String) myJson.get("brand"))){
                                    brandList.add((String) myJson.get("brand"));
                                }
                                System.out.println(i);
                            }
                            System.out.println("showGridData");

                            showGridData();
                            System.out.println("showGridData######");

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
            final Product product = (Product) productList.get(row);
            LinearLayout lineLayout = new LinearLayout(this);
            int num = 1;
            if(row + 1 < productList.size()){
                num++;
            }
            for(int i = 0; i < num; i++){
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
