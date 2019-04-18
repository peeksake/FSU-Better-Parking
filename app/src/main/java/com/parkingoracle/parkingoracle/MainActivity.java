package com.parkingoracle.parkingoracle;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
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
    private boolean viewFacultyMode; //Student or Faculty
    private String className = MainActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigationView;

    List<String> maxCapacities = Arrays.asList("786", "839", "1186", "795", "1118", "928", "132", "288");
    List<String> garageNames = Arrays.asList("Call Street", "Saint Augustine Street", "Spirit Way", "Traditions Way", "Pensacola Street", "Woodward Avenue", "Pensacola Street", "Woodward Avenue");
    List<String> spotsOpen = new ArrayList<>();
    List<String> liveFeed = new ArrayList<>();
    List<String> maxCapacitiesFeed = new ArrayList<>();
    List<String> garageNamesFeed = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        viewFacultyMode = true;
        toolbar = findViewById(R.id.toolbar);
        swipeContainer = findViewById(R.id.swipeContainer);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        setSupportActionBar(toolbar);

        updateList();
        initRecyclerView();

        // Bottom Navigation Bar
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.CurrentCapacity:
                                break;
                            case R.id.now:
                                startActivity(new Intent(MainActivity.this, Maps.class));
                                break;
                            case R.id.Settings:
                                startActivity(new Intent(MainActivity.this, Settings.class));
                                break;
                        }
                        return false;
                    }
                });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
            }
        });
        invalidateOptionsMenu();
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
        }
    }

    //Inflates the toolbar with options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.capacity_toolbar, menu);

        if(viewFacultyMode) {
            menu.getItem(1).setVisible(false);
            menu.getItem(0).setVisible(true);
        }
        else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        }
        return true;
    }

    //Makes buttons clickable
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Faculty) {
            viewFacultyMode = false;
            initRecyclerView();
            invalidateOptionsMenu();// calls onCreateOptionsMenu
        }
        if (id == R.id.Student) {
            viewFacultyMode = true;
            initRecyclerView();
            invalidateOptionsMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    //Intializes the garageviews
    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setRecyclerBasedOffMode();
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setElevation(1);
    }

    private void setRecyclerBasedOffMode() {
        liveFeed.clear();
        maxCapacitiesFeed.clear();//resets lists
        garageNamesFeed.clear();

        if (viewFacultyMode == true) {
            garageNamesFeed.addAll(garageNames.subList(0, 6));
            maxCapacitiesFeed.addAll(maxCapacities.subList(0, 6));
            liveFeed.addAll(spotsOpen.subList(0, 6));
        } else {
            garageNamesFeed.addAll(garageNames.subList(6, 8));
            maxCapacitiesFeed.addAll(maxCapacities.subList(6, 8));
            liveFeed.addAll(spotsOpen.subList(6, 8));
        }

        recyclerViewAdapter = new RecyclerViewAdapter(this, garageNamesFeed, maxCapacitiesFeed, liveFeed);
    }
}
