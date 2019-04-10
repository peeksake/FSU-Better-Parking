package com.parkingoracle.parkingoracle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String className = MainActivity.class.getSimpleName();
    List<Integer> maxCapacities = Arrays.asList(786, 839, 1186, 795, 1118, 928, 132, 288);
    List<String> GarageCapacityList = new ArrayList<>();
    TextView[] textViewArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewArray = new TextView[]{
                findViewById(R.id.currentcall), findViewById(R.id.currentaug), findViewById(R.id.currentspirit),
                findViewById(R.id.currenttraditions), findViewById(R.id.currentpen), findViewById(R.id.currentwood),
                findViewById(R.id.currentcallf), findViewById(R.id.currentspiritf)};
        new GetGarageCapacities().execute();
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
                        if (capacity >= maxCapacities.get(i)) {
                            GarageCapacityList.add(i, "FULL");
                        } else {
                            GarageCapacityList.add(i, Integer.toString(maxCapacities.get(i) - capacity));
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
            for (int i = 0; i < textViewArray.length; i++) {
                textViewArray[i].setText((GarageCapacityList.get(i)));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.capacity_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
