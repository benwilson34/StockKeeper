package edu.temple.stockkeeper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationFragment.NavigationPaneListener,
        NewStockSymbolDialogFragment.NoticeDialogListener {

    private FragmentManager fm;
    private NavigationFragment navigationFragment;

    private StockDetailFragment stockDetailFragment;
    private Boolean isDualMode = false;
    private StockFile stockFile;

    private StockInfoService mService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isDualMode = findViewById(R.id.frag2) != null;

        stockFile = new StockFile(this);

        fm = getSupportFragmentManager();
        navigationFragment = new NavigationFragment();
        fm.beginTransaction().replace(R.id.frag1, navigationFragment).commit();

        Button addSymButton = (Button) findViewById(R.id.buttonNewSymbol);
        addSymButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("stockmsg", "MAINACT: clicked on add new symbol button...");
                addNewSymbol();
            }
        });

        Button clearFileButton = (Button) findViewById(R.id.buttonClearFile);
        clearFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("stockmsg", "MAINACT: clicked on clear file button...");
                stockFile.clearFile();
                navigationFragment.updateSymList();
            }
        });
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            StockInfoService.LocalBinder binder = (StockInfoService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            if(stockFile == null)
                throw new NullPointerException();
            mService.setStockFile(stockFile);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, StockInfoService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }

    public StockFile getStockFile() { return stockFile; }

    public void sendSymbol(String symbol){
        int containerId = R.id.frag1;

        if(isDualMode)
            containerId = R.id.frag2;

        StockDetailFragment frag = StockDetailFragment.getInstance(symbol);
        fm.beginTransaction().replace(containerId, frag).addToBackStack("").commit();
    }

    public void navigationOptionSelected(String symbol){
        sendSymbol(symbol);
    }

    public void addNewSymbol() {
        navigationFragment = new NavigationFragment();
        fm.beginTransaction().replace(R.id.frag1, navigationFragment).commit();

        DialogFragment newFragment = new NewStockSymbolDialogFragment();
        newFragment.show(getSupportFragmentManager(), "newSymbolDialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        TextView tv = (TextView) dialog.getView().findViewById(R.id.inputSymbol);
        String newSymbol = tv.getText().toString();
        Log.d("stockmsg","MAINACT: clicked on confirm in dialog, newSymbol="+newSymbol);

        mService.getInfo(newSymbol, new StockInfoService.GotInfoHandler() {
            @Override
            public void handleJSON(final String symbol, JSONObject obj) {
                stockFile.addSymbol(symbol, obj);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Resources res = getResources();
                        Toast.makeText(getBaseContext(), res.getString(R.string.dialog_toast)+" "+symbol,
                                Toast.LENGTH_SHORT).show();

                        navigationFragment.updateSymList();
                        sendSymbol(symbol);
                    }
                });
            }
        });
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // close dialog
        Log.d("stockmsg","MAINACT: clicked on cancel in dialog");
        dialog.dismiss();
    }
}
