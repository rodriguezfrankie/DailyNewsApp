package com.example.dailynewsupdate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView list_view;

    ArrayList<HashMap<String, String>> articleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        articleList = new ArrayList<>();
        list_view = (ListView) findViewById(R.id.list);

        new GetArticle().execute();
    }

    private class GetArticle extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            //make a request to the URL
            String url = "https://content.guardianapis.com/search?q=debate&tag=politics&from-date=2018-01-01&api-key=test";
            String jsonString;
            try {
                jsonString = sh.makeHttpRequest(createUrl(url));
            } catch (IOException e) {
                return null;
            }

            Log.e(TAG, "Response from url: " + jsonString);
            if (jsonString != null) {
                try {
                    //Create a new JSONObject
                    JSONObject jsonObj = new JSONObject(jsonString);

                    //get the JSON Object response
                    JSONObject response = jsonObj.getJSONObject("response");

                    //Get the JSON Array node
                    JSONArray results = response.getJSONArray("results");

                    // looping through all Contacts
                    for (int i = 0; i < results.length(); i++) {
                        //get the JSONObject
                        JSONObject c = results.getJSONObject(i);
                        String section = c.getString("sectionName");
                        String title = c.getString("webTitle");
                        String date = c.getString("webPublicationDate");
                        String urls = c.getString("webUrl");


                        // tmp hash map for a single article
                        HashMap<String, String> result = new HashMap<>();

                        // add each child node to HashMap key => value
                        result.put("section", section);
                        result.put("title", title);
                        result.put("date", date);
                        result.put("urls", urls);

                        // adding a article to our article list
                        articleList.add(result);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                return null;
            }
            return url;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, articleList,
                    R.layout.list_item, new String[]{"section", "title", "date", "urls"},
                    new int[]{R.id.section, R.id.title, R.id.date, R.id.urls});
            list_view.setAdapter(adapter);
        }
    }
}

