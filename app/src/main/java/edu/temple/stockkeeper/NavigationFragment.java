package edu.temple.stockkeeper;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class NavigationFragment extends Fragment {

    public NavigationPaneListener mListener;
    public StockFile stockFile;

    private ArrayAdapter adapter;
    private ArrayList<String> symList;
    private TextView emptyListText;

    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mListener = (NavigationPaneListener)activity; // will throw exception if mListener doesn't implement interface
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_navigation, container, false); // idk if this is okay

//        stockFile = new StockFile(getContext());
        stockFile = ((MainActivity)getActivity()).getStockFile();
        symList = stockFile.getSymbolList();


        final Activity activity = getActivity();
        adapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, symList);
        ListView listView = (ListView) v.findViewById(R.id.symbolListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String colorCode = adapter.getColorCode(i);
                String symbol = (String) adapter.getItem(i);
//                String choseText = getResources().getString(R.string.chose_text);
//                Toast.makeText(activity, choseText + " " + color, Toast.LENGTH_SHORT).show();

//                Log.i("color", "Color to parse is "+colorCode);
//                int c = Color.parseColor(colorCode);
//                Log.i("color", "PALETTE: color="+c);
//                findViewById(R.id.listView).setBackgroundColor(c);

//                Intent secondActivity = new Intent(activity, StockDetailActivity.class);
//                secondActivity.putExtra("color", c);
//                startActivity(secondActivity);
//                mListener.navigationOptionSelected(c);
                mListener.navigationOptionSelected(symbol);
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        emptyListText = (TextView) getView().findViewById(R.id.emptyListText);
        setEmptyTextVisibility(symList.size());
    }

    public void updateSymList(){
        symList = stockFile.getSymbolList();
        setEmptyTextVisibility(symList.size());

        adapter.clear();
        adapter.addAll(symList);
        adapter.notifyDataSetChanged();
        ListView lv = (ListView) getView().findViewById(R.id.symbolListView);
        lv.invalidateViews();
    }

    public void setEmptyTextVisibility(int size){
        if (size > 0)
            emptyListText.setVisibility(View.GONE);
        else
            emptyListText.setVisibility(View.VISIBLE);
    }


    public interface NavigationPaneListener {
        void navigationOptionSelected(String symbol);
    }
}
