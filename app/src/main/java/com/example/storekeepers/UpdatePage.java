package com.example.storekeepers;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdatePage extends AppCompatActivity {

    EditText productTitle, productDescription, productPrice, productDetail;
    ImageView productImage;
    Button update;
    Uri imageUri;
    String downloadUri;
    ProgressDialog pd;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;
    final String randomKey = UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_page);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Status, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        productTitle = findViewById(R.id.productName);
        productDescription = findViewById(R.id.productDescription);
        productPrice = findViewById(R.id.productPrice);
        productDetail = findViewById(R.id.productDetail);
        productImage = findViewById(R.id.productImage);
        update = findViewById(R.id.update);

        String descc, detail, pricee, imagee = null, namee, status, usertype, documentID = null;

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            descc = extras.getString("desc");
            detail = extras.getString("detail");
            pricee = extras.getString("price");
            imagee = extras.getString("image");
            namee = extras.getString("title");
            status = extras.getString("status");
            documentID = extras.getString("documentID");

            productTitle.setText(namee);
            productDescription.setText(descc);
            productDetail.setText(detail);
            productPrice.setText(pricee);
//            spinner.setOnItemSelectedListener(status);

            Picasso.get().load(imagee).into(productImage);
        }

        String finalDocumentID = documentID;


        String finalImagee = imagee;
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                pd = new ProgressDialog(UpdatePage.this);
                pd.setMessage("Uploading...");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                String productStatus = spinner.getSelectedItem().toString();

                String str_pTitle = productTitle.getText().toString();
                String str_pDesc = productDescription.getText().toString();
                String str_pPrice = productPrice.getText().toString();
                String str_pDetail = productDetail.getText().toString();

                if (TextUtils.isEmpty(str_pTitle) || TextUtils.isEmpty(str_pDetail)
                        || TextUtils.isEmpty(str_pDesc) || TextUtils.isEmpty(str_pPrice)) {
                    pd.dismiss();
                    Toast.makeText(UpdatePage.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
                else {
                    UpdateDatabase(str_pTitle, productStatus, str_pDesc, str_pPrice, str_pDetail, finalImagee, finalDocumentID);
                }
            }
        });
    }

    private void UpdateDatabase(String str_pTitle, String productStatus, String str_pDesc, String str_pPrice, String str_pDetail, String finalImagee, String finalDocumentID) {

        Map<String, Object> product = new HashMap<>();
        product.put("productTitle", str_pTitle);
        product.put("productDesc", str_pDesc);
        product.put("productPrice", str_pPrice);
        product.put("productDetail", str_pDetail);
        product.put("productImage", finalImagee);
        product.put("productStatus", productStatus);
        product.put("documentID", finalDocumentID);

        db.collection("Products").document(finalDocumentID)
                .update(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdatePage.this, "Successfully uploaded your product", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdatePage.this, ViewProducts.class);
                        ViewProducts i = new ViewProducts();
                        i.finish();
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UpdatePage.this, "Error uploading your product", Toast.LENGTH_SHORT).show();
            }
        });

    }
}