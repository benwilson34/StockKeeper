package edu.temple.stockkeeper;


import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class StockDetailFragment extends Fragment {

    View view;
    private StockFile stockFile;

    public static final String IMG_URL = "https://finance.google.com/finance/getchart?p=8d&q=";
    public static final String SYMB_ARG = "symbol";

    public StockDetailFragment() {
        // Required empty public constructor
    }

    // TODO should this be the index or the symbol passed in here?
    public static StockDetailFragment getInstance(String symbol){
        StockDetailFragment f = new StockDetailFragment();
        Bundle b = new Bundle();
        b.putString(SYMB_ARG, symbol);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        stockFile = ((MainActivity)getActivity()).getStockFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_stock_detail, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        stockFile = ((MainActivity)getActivity()).getStockFile();
        showDetailsForSymbol(getArguments().getString(SYMB_ARG, ""));
    }

    public void showDetailsForSymbol(String symbol){
        if(symbol.isEmpty())
            return;

        JSONObject obj = stockFile.getStockInfo(symbol);
        if(obj == null)
            return;

        Resources res = getResources();
        TextView title = (TextView)getView().findViewById(R.id.detailTitle);
        title.setText(res.getText(R.string.detail_title) + " " + symbol);

        ImageView imageView = (ImageView) getView().findViewById(R.id.detailImage);
        Picasso.with(getContext()).load(IMG_URL + symbol).into(imageView);

        try {
            TextView companyName = (TextView) getView().findViewById(R.id.detailCompanyName);
            companyName.setText(res.getText(R.string.detail_companyName) + " " + obj.getString("Name"));

            TextView price = (TextView) getView().findViewById(R.id.detailCurrentPrice);
            price.setText(res.getText(R.string.detail_currentPrice) + " " + obj.getString("LastPrice"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
