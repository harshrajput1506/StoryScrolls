package com.hn.farm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hn.farm.R;
import com.hn.farm.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderProductAdapter extends BaseAdapter {

    private List<HashMap<String, Object>> products;
    private Context context;

    public OrderProductAdapter(List<HashMap<String, Object>> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_order_product, parent, false);
        }
        TextView name = convertView.findViewById(R.id.name);
        TextView quantityNPrice = convertView.findViewById(R.id.quantity_n_price);
        TextView totalPrice = convertView.findViewById(R.id.total_product_price);
        String nameTxt = (String) products.get(position).get("name");
        name.setText(nameTxt);
        String quantityNPriceTxt = products.get(position).get("availableQuantity")+" X "+products.get(position).get("price");
        quantityNPrice.setText(quantityNPriceTxt);
        long quantity = (long) products.get(position).get("availableQuantity");
        long price = (long) (long)(products.get(position).get("price"));
        int totalPriceInt = (int) (quantity*price);
        String totalPriceTxt = "₹ "+totalPriceInt;
        totalPrice.setText(totalPriceTxt);
        return convertView;
    }
}
