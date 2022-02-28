package com.example.storekeepers;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewProducts extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SelectListener {

    RecyclerView recyclerView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProductsAdapter productsAdapter;
    ArrayList<Products> productsArrayList;
    ProgressDialog pd;
    String usertype;
//    String problemType;

@Override
public void onBackPressed() {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    String userid = firebaseUser.getUid();
    DocumentReference docRef = db.collection("Users").document(userid);
    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    String usertype = document.getString("User Type");

                    if (usertype.equalsIgnoreCase("Admin")){
                        pd.dismiss();
                        Intent i = new Intent(ViewProducts.this, ProductDestination.class);
                        startActivity(i);
                        finish();


                    }
                    else {
                        auth.signOut();
                    }


                } else {
                    pd.dismiss();
                    Log.d(TAG, "No such document");

                }
            } else {
                pd.dismiss();
                Log.d(TAG, "get failed with ", task.getException());
            }
        }
    });
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Status, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

//        Toast.makeText(ViewProducts.this, exception.toString(), Toast.LENGTH_SHORT).show();


        pd = new ProgressDialog(this);
//        pd.setCancelable(false);
        pd.setMessage("Loading Products, please wait...");
        pd.show();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        productsArrayList = new ArrayList<>();
        productsAdapter = new ProductsAdapter(this, productsArrayList, this);
        recyclerView.setAdapter(productsAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String userid = firebaseUser.getUid();
        DocumentReference docRef = db.collection("Users").document(userid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        usertype = document.getString("User Type");

                    } else {
                        Log.d(TAG, "No such document");

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        EventChangeListener();
    }

    private void EventChangeListener() {

        pd.show();
        db.collection("Products").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (error != null){
                    pd.dismiss();
                    Log.e("Error", error.getMessage());
                    Toast.makeText(ViewProducts.this, error.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }


                for (DocumentChange dc : value.getDocumentChanges()){
                    if (dc.getType() == DocumentChange.Type.ADDED){

                        String productTitle, productDesc, productPrice;
                        Uri productImageUri;

                        productsArrayList.add(dc.getDocument().toObject(Products.class));

                    }

                    productsAdapter.notifyDataSetChanged();
                    pd.dismiss();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        problemType = adapterView.getItemAtPosition(i).toString();
//        if (problemType.equalsIgnoreCase("Education")){
//            storiesClassArrayList.clear();
//            EventChangeListener(problemType);
//        }
//        else if (problemType.equalsIgnoreCase("Business")){
//            storiesClassArrayList.clear();
//            EventChangeListener(problemType);
//        }
//        else if (problemType.equalsIgnoreCase("Medical")){
//            storiesClassArrayList.clear();
//            EventChangeListener(problemType);
//        }
//        else if (problemType.equalsIgnoreCase("Orphan")){
//            storiesClassArrayList.clear();
//            EventChangeListener(problemType);
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



    @Override
    public void onItemClicked(Products products) {
//        Toast.makeText(this, products.getProductStatus(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ViewProducts.this, DetailedView.class);

        intent.putExtra("title", products.getProductTitle());
        intent.putExtra("detail", products.getProductDetail());
        intent.putExtra("price", products.getProductPrice());
        intent.putExtra("image", products.getProductImage());
        intent.putExtra("desc", products.getProductDesc());
        intent.putExtra("status", products.getProductStatus());
        intent.putExtra("usertype", usertype);
        intent.putExtra("documentID", products.getDocumentID());

        startActivity(intent);
        finish();
    }
}