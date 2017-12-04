package com.example.nathalie.nathalieborst_pset5;


import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends DialogFragment {
    private RestoAdapter restoAdapter;
    RequestQueue requestQueue;
    String wait;
    String url = "https://resto.mprog.nl/order";
    View viewx;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        viewx = view;

        // Initialize adapter and listview
        RestoDatabase db = RestoDatabase.getInstance(getActivity().getApplicationContext());
        Cursor cursor = db.selectAll();

        restoAdapter = new RestoAdapter(getActivity().getApplicationContext(), cursor) {
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);
            }
        };

        ListView listView = (ListView) view.findViewById(R.id.orderlv);
        listView.setAdapter(restoAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String clickedItem = String.valueOf(view.getTag());
                showAlert(clickedItem);
            }
        });

        TextView totalPrice = (TextView) view.findViewById(R.id.priceTotaltv);

        // Show updated data in listview
        UpdateData();

        // Initialize buttons and listeners
        Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        Button placeOrderButton = (Button) view.findViewById(R.id.placeOrderButton);
        cancelButton.setOnClickListener(new GoCancelButtonClickListener());
        placeOrderButton.setOnClickListener(new PlaceOrderButtonClickListener());

        return view;
    }

    private void UpdateData() {
        RestoDatabase db = RestoDatabase.getInstance(getActivity().getApplicationContext());

        // Show total order value in dialog
        TextView totalValue = (TextView) viewx.findViewById(R.id.priceTotaltv);
        int totalOrderValue = db.totalOrderPrice();

        if(totalOrderValue == 0) {
            totalValue.setText(String.valueOf(0));
        } else {
            totalValue.setText(String.valueOf(totalOrderValue));
        }

        Cursor cursor = db.selectAll();
        restoAdapter.swapCursor(cursor);
    }

    public void showAlert (String clickedItem) {

        final List<String> itemDetails = Arrays.asList(clickedItem.split(","));

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Want to remove " + itemDetails.get(1) + " from order?\n");
        adb.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity().getApplicationContext(), "Deleted 1 " + itemDetails.get(1) + " from order", Toast.LENGTH_LONG).show();

                removeOrderFromDatabase(Integer.valueOf(itemDetails.get(0)), (Integer.valueOf(itemDetails.get(2)) / Integer.valueOf(itemDetails.get(3))) , Integer.valueOf(itemDetails.get(3)));
                UpdateData();
                dialog.dismiss();

            } });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity().getApplicationContext(), "NO", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            } });
        adb.show();
    }

    public void removeOrderFromDatabase (int id, int price, int amount) {

        RestoDatabase db = RestoDatabase.getInstance(getActivity().getApplicationContext());
        db.deleteItem(id, price, amount);
    }

    private class GoCancelButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            getDialog().dismiss();
        }
    }

    private class PlaceOrderButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            PlaceOrder();
        }
    }

    private void PlaceOrder () {

        // Checks if database is empty so you can't place an empty order
        RestoDatabase db = RestoDatabase.getInstance(getActivity().getApplicationContext());
        boolean checkIfEmpty = db.checkIfEmpty();

        if(checkIfEmpty) {
            Toast.makeText(getActivity().getApplicationContext(), "Please add items to your order", Toast.LENGTH_LONG).show();
        } else {


            // Initialize a new RequestQueue instance
            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

            // Initialize a new JsonObjectRequest instance
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            // Process the JSON
                            try {
                                wait = response.getString("preparation_time");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Do something when error occurred
                            Log.d("JSONERROR", "something wrong with JSON-request, check code");
                        }
                    }
            );

            // Add JsonObjectRequest to the RequestQueue
            requestQueue.add(jsonObjectRequest);

            String waitingTime = "Your waiting time is " + String.valueOf(wait) + " minutes";
            Toast.makeText(getActivity().getApplicationContext(), waitingTime, LENGTH_SHORT).show();

            // Delete database
            db.deleteAll();

            UpdateData();

        }

    }
}
