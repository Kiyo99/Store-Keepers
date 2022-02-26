package com.example.storekeepers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;

public class DetailedView extends AppCompatActivity {

    TextView name, desc, detailedDesc, price;
    ImageView image;
    Button update, delete;

    UserType UT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        name = findViewById(R.id.name);
        desc = findViewById(R.id.condition);
        detailedDesc = findViewById(R.id.dView);
        update = findViewById(R.id.update);
        price = findViewById(R.id.price);
        image = findViewById(R.id.image);
        delete = findViewById(R.id.delete);

        String descc, detail, pricee, imagee, namee, status, usertype;

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            descc = extras.getString("desc");
            detail = extras.getString("detail");
            pricee = extras.getString("price");
            imagee = extras.getString("image");
            namee = extras.getString("title");
            status = extras.getString("status");
            usertype = extras.getString("usertype");

            if (usertype.equalsIgnoreCase("Admin")){

                update.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            }
            else {

                update.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }


            name.setText(namee);
            desc.setText(descc);
            detailedDesc.setText(detail);
            price.setText(pricee);

            Picasso.get().load(imagee).into(image);
        }

        else {
            Toast.makeText(DetailedView.this, "No values received", Toast.LENGTH_SHORT).show();
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

}