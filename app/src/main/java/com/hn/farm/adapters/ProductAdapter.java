package com.hn.farm.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.hn.farm.R;
import com.hn.farm.listeners.ProductListener;
import com.hn.farm.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final ArrayList<Product> products;
    private ArrayList<Product> filteredList;
    private final Context context;
    private final ProductListener productListener;

    public ProductAdapter(ArrayList<Product> products, Context context, ProductListener productListener) {
        this.products = products;
        this.context = context;
        this.productListener = productListener;
        this.filteredList = new ArrayList<>(products);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_product, parent, false);
        return new ViewHolder(view, productListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(filteredList.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filterByCategory(List<String> categories){
        filteredList.clear();
        for (Product product : products) {
            if (categories.contains(product.getCategory())) {
                filteredList.add(product);
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView image;
        private TextView name, description, price;
        private MaterialCardView card;

        private ProductListener productListener;

        private final String RUPEE_SYMBOL = "₹";
        public ViewHolder(@NonNull View itemView, ProductListener productListener) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            card = itemView.findViewById(R.id.product_card);
            this.productListener = productListener;
        }

        public void bind(Product product){
            Glide.with(itemView).load(product.getImage()).into(image);
            name.setText(product.getName());
            description.setText(product.getDescription());
            String formattedPrice  = RUPEE_SYMBOL+" "+product.getPrice();
            price.setText(formattedPrice);

            card.setOnClickListener( v-> {
                productListener.onClick(product);
            });
        }
    }
}
