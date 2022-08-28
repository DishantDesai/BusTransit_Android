package com.example.school_bus_transit.helper;

import android.os.AsyncTask;

import com.example.school_bus_transit.admin.DriverBusInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class fetchRoute extends AsyncTask<String, String, String> {

    String result = "";
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // display a progress dialog to show the user what is happening


    }
    @Override
    protected String doInBackground(String... params) {


        try {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(params[0]);
                //open a URL coonnection

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader isw = new InputStreamReader(in);

                int data = isw.read();

                while (data != -1)
                {
                    result += (char) data;
                    data = isw.read();
                }

                // return the data to onPostExecute method

                try
                {
                    constants.routes = new DirectionsJSONParser().parse(new JSONObject(result));
                    System.out.println(constants.routes);

                    new DriverBusInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        }
        return result;


    }

    @Override
    protected void onPostExecute(String s)
    {
        // show results
    }

}

