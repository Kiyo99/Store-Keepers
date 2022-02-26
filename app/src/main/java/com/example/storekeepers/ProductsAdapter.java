package com.example.storekeepers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyViewHolder> {

    Context context;
    ArrayList <Products> productsArrayList;
    SelectListener selectListener;

    public ProductsAdapter(Context context, ArrayList<Products> productsArrayList, SelectListener selectListener) {
        this.context = context;
        this.productsArrayList = productsArrayList;
        this.selectListener = selectListener;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Products products = productsArrayList.get(position);

        holder.name.setText(products.getProductTitle());
        holder.desc.setText(products.getProductDesc());
        holder.price.setText(products.getProductPrice());

        Picasso.get().load(products.productImage).into(holder.image);

        holder.viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectListener.onItemClicked(productsArrayList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return productsArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, desc, price;
        ImageView image;
        Button viewMore;

        public MyViewHolder (@NonNull View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);
            viewMore = itemView.findViewById(R.id.viewMore);

//            donate.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    itemView.getContext().startActivity(new Intent(itemView.getContext(),DetailedView.class));
//                }
//            });

        }
    }
}
