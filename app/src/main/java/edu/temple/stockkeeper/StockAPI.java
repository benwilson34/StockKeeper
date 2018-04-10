package edu.temple.stockkeeper;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StockAPI {

    private static final String STOCK_INFO_URL = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=";


    public static JSONObject getInfo(String symbol){
        JSONObject obj = null;

        try {
            URL url = new URL(STOCK_INFO_URL + symbol);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                obj = new JSONObject(stringBuilder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                conn.disconnect();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
    }

}
