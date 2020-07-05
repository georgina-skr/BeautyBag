package com.example.beautyapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/** It's about the visibility and management of products at "My Bag"**/
class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {

    List<Product> products;
    List<Product> productsFull;
    Context context;

    OnProductListener pOnProductListener;

    public ProductAdapter(List<Product> products, Context context, OnProductListener onProductListener){
        this.products = products;
        this.context = context;
        productsFull = new ArrayList<>(products);
        this.pOnProductListener = onProductListener;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row,parent,false);
        return  new ViewHolder(view, pOnProductListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        holder.prName.setText(products.get(position).getName());
        if (products.get(position).getButtonLike() == 1){
            holder.prLike.setImageResource(R.drawable.ic_thumb_up_like);
        }
        else if (products.get(position).getButtonLike() == -1){
            holder.prLike.setImageResource(R.drawable.ic_thumb_down_dislike);
        }
        if (products.get(position).getReminder() == 1){
            holder.prReminder.setVisibility(View.VISIBLE);
        }else {
            holder.prReminder.setVisibility(View.INVISIBLE);
        }
        holder.prPrice.setText("Price: " + products.get(position).getPrice());
        holder.prPDate.setText("P.Date: " + products.get(position).getPurchaseDate());
        holder.prCategory.setText(products.get(position).getCategory());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    //Click on a product
    public interface OnProductListener{
        void onProductClick(int position);
    }

    //Return the asked products
    @Override
    public Filter getFilter() {
        return productsFilter;
    }

    private Filter productsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(productsFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Product product : productsFull){
                    if (product.getName().toLowerCase().contains(filterPattern) || product.getCategory().toLowerCase().contains(filterPattern)){
                        filteredList.add(product);
                    }
                }

            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            products.clear();
            products.addAll((List) results.values);
            notifyDataSetChanged();

        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView prName;
        public ImageView prLike;
        public ImageView prReminder;
        public TextView prPrice;
        public TextView prPDate;
        public Button prCategory;

        OnProductListener onProductListener;

        public ViewHolder(@NonNull View itemView, OnProductListener onProductListener) {
            super(itemView);
            prName = itemView.findViewById(R.id.name);
            prLike = itemView.findViewById(R.id.likeDislike);
            prReminder = itemView.findViewById(R.id.reminder);
            prPrice = itemView.findViewById(R.id.price);
            prPDate = itemView.findViewById(R.id.pDate);
            prCategory = itemView.findViewById(R.id.category);

            this.onProductListener = onProductListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onProductListener.onProductClick(getAdapterPosition());
        }
    }


}
