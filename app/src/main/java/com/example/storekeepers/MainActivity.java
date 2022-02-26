package com.example.storekeepers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    CardView user, product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = findViewById(R.id.users);
        product = findViewById(R.id.product);

         user.setOnClickListener(new View.OnClickListener() {
           @Override
             public void onClick(View view) {
               Intent k = new Intent(MainActivity.this, Register.class);
               startActivity(k);
             }
         });

          product.setOnClickListener(new View.OnClickListener() {
           @Override
             public void onClick(View view) {
               Intent emma = new Intent(MainActivity.this, ProductDestination.class);
               startActivity(emma);
             }
         });

    }
}