package com.example.nathalie.nathalieborst_pset5;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends android.support.v4.app.ListFragment {
    RequestQueue requestQueue;
    String url = "https://resto.mprog.nl/menu";
    JSONObject item;
    private List<Product> mProductlist = new ArrayList<>();
    String chosenCategory, chosenItem;
    private ProductListAdapter productListAdapter;
    private RestoAdapter restoAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        chosenCategory = "entrees";
        Bundle arguments = this.getArguments();
        chosenCategory = arguments.getString("category");

        // Initialize a new RequestQueue instance
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        // Initialize a new JsonObjectRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // Process the JSON
                        try {
                            // Get the JSON array
                            JSONArray array = response.getJSONArray("items");
                            Log.d("hallo_arr", String.valueOf(array));

                            // Loops trough every item of the JSON array
                            for (int j = 0; j < array.length(); j++) {

                                // Get the j'th JSON object
                                item = array.getJSONObject(j);

                                // Get the category of j'th array-item
                                String checkCategory = item.getString("category");
                                Log.d("hallo_cat", checkCategory);

                                // adds items of chosen category to lists
                                if (checkCategory.equals(chosenCategory)) {
                                    int count = 0;

                                    int id = item.getInt("id");
                                    String name = item.getString("name");
                                    String description = item.getString("description");
                                    int price = item.getInt("price");
                                    String image = item.getString("image_url");
                                    Log.d("hallo_name", name);

                                    fillListView(id, name, description, price, image);
                                }
                            }
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
    }

    public void fillListView(int id, String name, String description, int price, String image) {

        chosenItem = name;

        // Get listview from XML
        final ListView lvProduct = getListView();

        // Fill productlist
        mProductlist.add(new Product(id, name, price, description, image));
        productListAdapter = new ProductListAdapter(getActivity().getApplicationContext(), mProductlist);
        lvProduct.setAdapter(productListAdapter);

        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String clickedItem = String.valueOf(view.getTag());
                showAlert(clickedItem);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        return view;
    }

    public void showAlert (String clickedItem) {

        final List<String> itemDetails = Arrays.asList(clickedItem.split(","));
        Log.d("hallo_details", String.valueOf(itemDetails));

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Want to add " + itemDetails.get(1) + " to order?\n");
        adb.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity().getApplicationContext(), "Added " + itemDetails.get(1) + " to order", Toast.LENGTH_LONG).show();

                // Add item to database
                addOrderToDatabase(Integer.valueOf(itemDetails.get(0)), itemDetails.get(1), Integer.valueOf(itemDetails.get(2)));

                dialog.dismiss();

            } });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            } });
        adb.show();


    }

    public void addOrderToDatabase (int id, String name, int price) {

        RestoDatabase db = RestoDatabase.getInstance(getActivity().getApplicationContext());
        db.addItem(id, name, price);
    }
}


