package com.example.storekeepers;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;

public class DetailedView extends AppCompatActivity {

    TextView name, desc, detailedDesc, price;
    ImageView image;
    Button update, delete;

    UserType UT;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DetailedView.this, ViewProducts.class);
        finish();
        startActivity(i);
    }

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

        String descc = null, detail = null, pricee = null, imagee = null, namee = null, status = null, usertype = null, documentID = null;

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
            documentID = extras.getString("documentID");


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

        String finalDocumentID = documentID;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Going into the database to delete the Product

                if(finalDocumentID != null) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("Products").document(finalDocumentID);
                    docRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(DetailedView.this, "Succesfully Deleted the product", Toast.LENGTH_SHORT).show();
                            ViewProducts i = new ViewProducts();
                            i.finish();
                            Intent intent1 = new Intent(DetailedView.this, ViewProducts.class);
                            startActivity(intent1);
                            finish();
                        }
                    });
                }
            }
        });

        String finalNamee = namee;
        String finalDetail = detail;
        String finalPricee = pricee;
        String finalDocumentID1 = documentID;
        String finalImagee = imagee;
        String finalDescc = descc;
        String finalStatus = status;
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailedView.this, UpdatePage.class);

                intent.putExtra("title", finalNamee);
                intent.putExtra("detail", finalDetail);
                intent.putExtra("price", finalPricee);
                intent.putExtra("image", finalImagee);
                intent.putExtra("desc", finalDescc);
                intent.putExtra("status", finalStatus);
                intent.putExtra("documentID", finalDocumentID1);

//                Toast.makeText(DetailedView.this, finalNamee, Toast.LENGTH_SHORT).show();

                startActivity(intent);
                finish();
            }
        });

    }

}