package com.example.nathalie.nathalieborst_pset5;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantFragment extends android.support.v4.app.ListFragment {

    RequestQueue requestQueue;
    String url = "https://resto.mprog.nl/categories";
    List<String> categoriesList = new ArrayList<String>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                            JSONArray array = response.getJSONArray("categories");

                            // Fill list with categories from JSON
                            for (int i = 0; i < array.length(); i++) {
                                categoriesList.add(array.getString(i));
                                Log.d("hallo", categoriesList.get(i));
                            }

                            // Show categories in list view
                            fillSimpleListView();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("JSON ERROR", "Error when requesting a response from JSON");
                    }
                }
        );

        // Add JsonObjectRequest to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onListItemClick(ListView listview, View view, int position, long id) {
        super.onListItemClick(listview, view, position, id);

        // Initialize bundle to send to next fragment
        MenuFragment menuFragment = new MenuFragment();
        String s = listview.getItemAtPosition(position).toString();
        Bundle args = new Bundle();
        args.putString("category", s);
        menuFragment.setArguments(args);

        // Show fragment in activity
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, menuFragment)
                .addToBackStack(null)
                .commit();

    }

    public void fillSimpleListView () {

        ListAdapter theAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_listview_text,
                categoriesList);

        this.setListAdapter(theAdapter);
    }

}
