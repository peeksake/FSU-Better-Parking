package com.parkingoracle.parkingoracle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerViewAdapter recyclerViewAdapter;
    private SwipeRefreshLayout swipeContainer;
    private Toolbar toolbar;
    private String className = MainActivity.class.getSimpleName();
    List<String> maxCapacities = Arrays.asList("786","839", "1186", "795", "1118", "928", "132", "288");
    List<String> garageNames = Arrays.asList("Call Street", "Saint Augustine Street","Spirit Way", "Traditions Way", "Pensacola Street", "Woodward Avenue", "Pensacola Street", "Woodward Avenue");
    List<String> spotsOpen = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        toolbar = findViewById(R.id.toolbar);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        setSupportActionBar(toolbar);

        initRecyclerView();
        updateList();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                updateList();
            }
        });



    }

    private void updateList() {
        AsyncTask getData = new GetGarageCapacities().execute();
        try {
            getData.get();
            recyclerViewAdapter.notifyDataSetChanged();
            swipeContainer.setRefreshing(false);
        } catch (Exception e) {
            Log.d("Exception ", e.toString());
        }
    }

    private class GetGarageCapacities extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String url = "https://www.garageoracle.net/data";
            String jsonStr = sh.makeServiceCall(url);
            Log.e(className, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray jsonArray = jsonObj.getJSONArray("garagearrays");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Integer capacity = (Integer) jsonArray.getJSONArray(i).get(1);
                        if (capacity >= Integer.parseInt(maxCapacities.get(i))) {
                            spotsOpen.add(i, "FULL");
                        } else {
                            spotsOpen.add(i, Integer.toString(Integer.parseInt(maxCapacities.get(i)) - capacity));
                        }
                    }
                } catch (final JSONException e) {
                    Log.e(className, "Json parsing error: " + e.getMessage());
                }

            } else {
                Log.e(className, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
           /* AsyncTask getData = new GetGarageCapacities().execute();
            try {
                getData.get();
                recyclerViewAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.d("Exception ", e.toString());
            }*/

        }
    }

        //Inflates the toolbar with options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.capacity_toolbar, menu);
        return true;
    }

    //Makes buttons clickable
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    //Intializes the garageviews
    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(this, garageNames, maxCapacities, spotsOpen);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setElevation(1);
    }
}
