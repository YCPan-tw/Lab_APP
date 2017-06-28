package com.looper.andremachado.cleanwater.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.looper.andremachado.cleanwater.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowDataActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        String url_base = "http://127.0.0.1:8000/retrieve/";
        String url = url_base + "QR";
        // Request a string response from the provided URL.
        ListView listview = (ListView) findViewById(R.id.feed_list);
        Log.d("test","hi");
        String[] vv = new String[]{"Latitude = 129.12323, longitude = 123.124 ", "Temperature = 23 C", "atmospheric pressure ＝ 1024 hPA","Latitude = 129.12323, longitude = 123.124 ", "Temperature = 23 C", "atmospheric pressure ＝ 1024 hPA","Latitude = 129.12323, longitude = 123.124 ", "Temperature = 23 C", "atmospheric pressure ＝ 1024 hPA","Latitude = 129.12323, longitude = 123.124 ", "Temperature = 23 C", "atmospheric pressure ＝ 1024 hPA","Latitude = 129.12323, longitude = 123.124 ", "Temperature = 23 C", "atmospheric pressure ＝ 1024 hPA","Latitude = 129.12323, longitude = 123.124 ", "Temperature = 23 C", "atmospheric pressure ＝ 1024 hPA",
                "Latitude = 129.12323, longitude = 123.124 ", "Temperature = 23 C", "atmospheric pressure ＝ 1024 hPA"};
        Log.d("test","hi");
        final ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < vv.length; ++i) {
            Log.d("m","f");
            list.add(vv[i]);
            Log.d("m","f");
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, vv);
        //final ShowDataActivity.StableArrayAdapter adapter = new ShowDataActivity.StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

    }

    public boolean onClick(View botton){
        Intent mainIntent = new Intent(ShowDataActivity.this, CameraActivity.class);
        getApplicationContext().startActivity(mainIntent);

        return true;
    }
}