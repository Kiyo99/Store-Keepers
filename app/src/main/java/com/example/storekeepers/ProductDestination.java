package com.example.storekeepers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ProductDestination extends AppCompatActivity {

    CardView single, bulk, products;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ProductDestination.this, MainActivity.class);
        startActivity(i);
        finish();
    }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_destination);

        single = findViewById(R.id.single);
        bulk = findViewById(R.id.bulk);
        products = findViewById(R.id.viewProducts);

        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent k = new Intent(ProductDestination.this, AddProduct.class);
                startActivity(k);
            }
        });

        bulk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emma = new Intent(ProductDestination.this, MulProducts.class);
                startActivity(emma);
            }
        });

        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emma = new Intent(ProductDestination.this, ViewProducts.class);
                startActivity(emma);
            }
        });
    }
}