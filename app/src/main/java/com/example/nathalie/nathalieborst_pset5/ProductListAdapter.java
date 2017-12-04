package com.example.nathalie.nathalieborst_pset5;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Nathalie on 27-11-2017.
 */

public class ProductListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Product> mProductlist;

    public ProductListAdapter(Context mContext, List<Product> mProductlist) {
        this.mContext = mContext;
        this.mProductlist = mProductlist;
    }

    @Override
    public int getCount() {
        return mProductlist.size();
    }

    @Override
    public Object getItem(int i) {
        return mProductlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(mContext, R.layout.item_product_list, null);
        TextView tvName = (TextView)v.findViewById(R.id.tv_name);
        TextView tvPrice = (TextView)v.findViewById(R.id.tv_price);
        TextView tvDescription = (TextView)v.findViewById(R.id.tv_description);
        ImageView ivIcon = (ImageView)v.findViewById(R.id.iv_icon);

        tvName.setText(mProductlist.get(i).getName());
        tvPrice.setText(String.valueOf(mProductlist.get(i).getPrice()) + "$");
        tvDescription.setText(mProductlist.get(i).getDescription());

        String imageUrl = mProductlist.get(i).getIcon();
        Picasso.with(mContext).load(imageUrl).into(ivIcon);

        String data = String.valueOf(mProductlist.get(i).getId()) + "," + mProductlist.get(i).getName() + "," + String.valueOf(mProductlist.get(i).getPrice());



        v.setTag(data);

        return v;
    }

}
