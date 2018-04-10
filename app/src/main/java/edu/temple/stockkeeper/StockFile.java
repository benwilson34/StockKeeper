package edu.temple.stockkeeper;


import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class StockFile {

    private static final String STOCK_FILENAME = "stockinfo.json";

    private Semaphore mutex;

    private Context context;

    public StockFile(Context context){
        this.context = context;
        mutex = new Semaphore(1, true);
    }

    public boolean addSymbol(String symbol, JSONObject obj){
        Log.d("stockmsg", "adding "+symbol+" and obj to file.");
        // TODO check for dupes
        writeToFile(symbol, obj);
        return true;
    }

    public ArrayList<String> getSymbolList(){
        Log.d("stockmsg", "STOCKFILE: Getting symbol list...");

        ArrayList<String> syms = new ArrayList<>();
        HashMap<String, JSONObject> map = readFromFile();
        if(map != null) {
            // populate list
            Log.d("stockmsg", "STOCKFILE: Number of symbols: " + map.keySet().size());

            for (String sym : map.keySet()) {
                Log.d("stockmsg", "STOCKFILE: read " + sym + " from file.");
                syms.add(sym);
            }

            Collections.sort(syms); // put in alphabetical order
        }
        return syms;
    }

    public void updateStockInfo(String symbol, JSONObject obj){
        Log.d("stockmsg", "STOCKFILE: updating " + symbol + " to "+obj);
        HashMap<String, JSONObject> map = readFromFile();
        if(map == null)
            return;

        map.put(symbol, obj);
        writeMap(map);
    }

    public JSONObject getStockInfo(String symbol){
        HashMap<String, JSONObject> map = readFromFile();
        if(map != null)
            return map.get(symbol);
        else return null;
    }


    public void clearFile(){
        try {
            mutex.acquire();

            ObjectOutputStream outputStream = new ObjectOutputStream(
                    context.openFileOutput(STOCK_FILENAME, Context.MODE_PRIVATE)
            );
            outputStream.writeObject(null);
            outputStream.flush();
            outputStream.close();

            mutex.release();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(String sym, JSONObject obj) {
        HashMap<String, JSONObject> map = readFromFile();
        if(map == null)
            map = new HashMap<>();
        map.put(sym, obj);

        writeMap(map);
    }

    private void writeMap(HashMap<String, JSONObject> map){
        Log.d("stockmsg", "writing map..."+map.toString());
        HashMap<String, String> datamap = new HashMap<>();
        for (String key : map.keySet()){
            datamap.put(key, map.get(key).toString());
        }

        try {
            mutex.acquire();

            ObjectOutputStream outputStream = new ObjectOutputStream(
                    context.openFileOutput(STOCK_FILENAME, Context.MODE_PRIVATE)
            );
            outputStream.writeObject(datamap);
            outputStream.flush();
            outputStream.close();

            mutex.release();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public HashMap<String, JSONObject> readFromFile() {
        HashMap<String, JSONObject> ret = new HashMap<>();

        try {
            Log.d("stockmsg", "trying to read..." + mutex.availablePermits());
            mutex.acquire();
            Log.d("stockmsg", "acquired, about to read..." + mutex.availablePermits());

            InputStream inputStream = context.openFileInput(STOCK_FILENAME);

            if ( inputStream != null ) {
                HashMap<String, String> datamap;

                ObjectInputStream s = new ObjectInputStream(inputStream);
                datamap = (HashMap<String, String>) s.readObject();
                s.close();
                Log.d("stockmsg", "     >>>GETTING HERE<<<");

                if(datamap == null) {
                    mutex.release();
                    return null;
                }

                // TODO make JSONObject from ret
                for (String key : datamap.keySet()) {
                    ret.put(key, new JSONObject(datamap.get(key)));
                }
                Log.d("stock", ret.toString());
            }

            mutex.release();
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
