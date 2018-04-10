package edu.temple.stockkeeper;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class StockInfoService extends IntentService {

    private static final int SLEEP_INTERVAL = 10; // in seconds
    private final IBinder mBinder = new LocalBinder();
    private Handler handler;
    private StockFile stockFile;

    public class LocalBinder extends Binder {
        StockInfoService getService() {
            // Return this instance of LocalService so clients can call public methods
            return StockInfoService.this;
        }
    }

    public StockInfoService() {
        super("StockInfoService");
//        this.stockFile = new StockFile(this);
    }

    public void setStockFile(StockFile stockFile){ this.stockFile = stockFile; }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("stockmsg", "STOCKINFOSERVICE: onStartCommand called.");
        handler = new Handler();
        handler.postDelayed(StockInfoRunnable, SLEEP_INTERVAL * 1000);
        return mBinder;
    }

    final Runnable StockInfoRunnable = new Runnable(){
        @Override
        public void run() {
            for(String symbol : stockFile.getSymbolList())
                getInfo(symbol, new GotInfoHandler() {
                    @Override
                    public void handleJSON(String symbol, JSONObject obj) {
                        stockFile.updateStockInfo(symbol, obj);
                    }
                });
            handler.postDelayed( StockInfoRunnable, SLEEP_INTERVAL * 1000);
        }
    };

    public interface GotInfoHandler{
        public void handleJSON(String symbol, JSONObject obj);
    }

    public void getInfo(final String symbol, final GotInfoHandler handler){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    handler.handleJSON(symbol, StockAPI.getInfo(symbol));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        if (intent != null) {
//            final String action = intent.getAction();
//            // nothing?
//        }
    }

}
