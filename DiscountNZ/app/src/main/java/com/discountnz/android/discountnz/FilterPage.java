package com.discountnz.android.discountnz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
    private Boolean isFilter = true;

    ImageHandler handler = new ImageHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_page);

        // use the intent, get the Lists
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("categoryBundle");
        List categoryList = (ArrayList<String>)args.getSerializable("category");
        List brandList = (ArrayList<String>)args.getSerializable("brand");
        List dateList = (ArrayList<String>)args.getSerializable("date");

        setTitle("Filter Settings");

        // get the padding value from dp to pixel
        int padding_in_dp = 72;
        int padding_in_dp_right = 16;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
        int padding_in_px_right = (int) (padding_in_dp * scale + 0.5f);

        final Button btnBack = (Button)findViewById(R.id.btnBack);
        final Button btnConfirm = (Button)findViewById(R.id.btnConfirm);
        final TextView txtCategory = (TextView)findViewById(R.id.txtCategory);
        final TextView txtBrand = (TextView)findViewById(R.id.txtBrand);
        final TextView txtDate = (TextView)findViewById(R.id.txtDate);
        final ListView categoryListView = (ListView)findViewById(R.id.categoryListView);
        final ListView brandListView = (ListView)findViewById(R.id.brandListView);
        final ListView dateListView = (ListView)findViewById(R.id.dateListView);

        // populate the brandListView
        String[] brandItems = new String[brandList.size()];
        for (int i = 0; i < brandList.size(); i++) {
            brandItems[i] = brandList.get(i).toString();
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, brandItems);
        brandListView.setAdapter(adapter);
        brandListView.setPadding(padding_in_px, 0, padding_in_px_right, 0);
        justifyListViewHeightBasedOnChildren(brandListView);

        // populate the categoryListView
        String[] categoryItems = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            categoryItems[i] = categoryList.get(i).toString();
        }
        ArrayAdapter adapterCat = new ArrayAdapter(this, android.R.layout.simple_list_item_1, categoryItems);
        categoryListView.setAdapter(adapterCat);
        categoryListView.setPadding(padding_in_px, 0, padding_in_px_right, 0);
        justifyListViewHeightBasedOnChildren(categoryListView);

        // populate the dateListView
        String[] dateItems = new String[dateList.size()];
        for (int i = 0; i < dateList.size(); i++) {
            dateItems[i] = dateList.get(i).toString();
        }
        ArrayAdapter adapterDate = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dateItems);
        dateListView.setAdapter(adapterDate);
        dateListView.setPadding(padding_in_px, 0, padding_in_px_right, 0);
        justifyListViewHeightBasedOnChildren(dateListView);

        brandListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sBrand = (String) adapterView.getItemAtPosition(i);
                for (int j = 0; j < adapterView.getChildCount(); j++)
                    adapterView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                // change the background color of the selected element
                view.setBackgroundColor(Color.LTGRAY);
                isFilter = true;
            }
        });

        dateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sDate = (String) adapterView.getItemAtPosition(i);
                for (int j = 0; j < adapterView.getChildCount(); j++)
                    adapterView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                // change the background color of the selected element
                view.setBackgroundColor(Color.LTGRAY);
                isFilter = true;
            }
        });

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sCategory = (String) adapterView.getItemAtPosition(i);
                for (int j = 0; j < adapterView.getChildCount(); j++)
                    adapterView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                // change the background color of the selected element
                view.setBackgroundColor(Color.LTGRAY);
                isFilter = true;
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

        // setup the on click listener, return the filter settings
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent filterIntent = new Intent();
                filterIntent.putExtra("category", sCategory);
                filterIntent.putExtra("brand", sBrand);
                filterIntent.putExtra("date", sDate);
                filterIntent.putExtra("isFilter", isFilter);
                setResult(RESULT_OK, filterIntent);
                finish();
            }
        });

    }

    // set the height of the listview
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