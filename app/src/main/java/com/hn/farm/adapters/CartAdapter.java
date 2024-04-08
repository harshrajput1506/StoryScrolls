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
import com.hn.farm.R;
import com.hn.farm.listeners.CartListener;
import com.hn.farm.models.Product;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private ArrayList<Product> products;
    private Context context;
    private CartListener cartListener;

    public CartAdapter(ArrayList<Product> products, Context context, CartListener cartListener) {
        this.products = products;
        this.context = context;
        this.cartListener = cartListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cart_product, parent, false);
        return new ViewHolder(view, cartListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindData(products.get(position), position);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image, add, remove, delete;
        private TextView name, totalPrice, quantity;
        private CartListener cartListener;

        private final String RUPEE_SYMBOL = "₹";
        private int quantityInt;
        public ViewHolder(@NonNull View itemView, CartListener listener) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            add = itemView.findViewById(R.id.quantity_add_btn);
            remove = itemView.findViewById(R.id.quantity_remove_btn);
            delete = itemView.findViewById(R.id.delete_btn);
            name = itemView.findViewById(R.id.product_name);
            totalPrice = itemView.findViewById(R.id.total_price);
            quantity = itemView.findViewById(R.id.quantity);

            cartListener = listener;
        }

        private void onBindData(Product product, int index){
            quantityInt = (int) product.getAvailableQuantity();
            Glide.with(itemView).load(product.getImage()).into(image);
            name.setText(product.getName());

            int totalPriceInt = (int) (product.getPrice()*quantityInt);
            String totalPriceTxt = RUPEE_SYMBOL+" "+totalPriceInt;
            totalPrice.setText(totalPriceTxt);
            String quantityTxt = String.valueOf(quantityInt);
            quantity.setText(quantityTxt);


            delete.setOnClickListener( v-> {
                cartListener.onDelete(product.getId(), index);
            });

            add.setOnClickListener( v-> {
                changeQuantity(product, true);
                cartListener.onAddQuantity(product, quantityInt);

            });

            remove.setOnClickListener( v-> {
                changeQuantity(product, false);
                cartListener.onRemoveQuantity(product, quantityInt);

            });
        }

        private void changeQuantity(Product product, Boolean isAdd){
            if(isAdd) quantityInt++; else quantityInt--;
            int totalPriceInt = (int) (product.getPrice()*quantityInt);
            String totalPriceTxt = RUPEE_SYMBOL+" "+totalPriceInt;
            totalPrice.setText(totalPriceTxt);
            String quantityTxt = String.valueOf(quantityInt);
            quantity.setText(quantityTxt);
        }
    }

    public void changeList(ArrayList<Product> list){
        this.products = list;
    }
}
