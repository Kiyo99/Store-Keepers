package com.example.storekeepers;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MulProducts extends AppCompatActivity {
    Button upload;
    EditText productTitle, productDescription, productPriceMin, productPriceMax, productDetail, num;
    ImageView productImage;
    Uri imageUri;
    FirebaseStorage storage;
    String downloadUri;
    StorageReference storageReference;
    ProgressDialog pd;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    String userid = firebaseUser.getUid();
    DocumentReference docRef = db.collection("Users").document(userid);
    final String randomKey = UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mul_products);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Status, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        productTitle = findViewById(R.id.productName);
        productDescription = findViewById(R.id.productDescription);
        productPriceMin = findViewById(R.id.productPriceMin);
        productPriceMax = findViewById(R.id.productPriceMax);
        num = findViewById(R.id.num);
        productDetail = findViewById(R.id.productDetail);
        productImage = findViewById(R.id.productImage);
        upload = findViewById(R.id.upload);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(MulProducts.this);
                pd.setMessage("Uploading...");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                String imageChecker = downloadUri;
                String productStatus = spinner.getSelectedItem().toString();

                String str_pTitle = productTitle.getText().toString();
                String str_pDesc = productDescription.getText().toString();
                String str_pPriceMin = productPriceMin.getText().toString();
                String str_pPriceMax = productPriceMax.getText().toString();
                String str_pnum = num.getText().toString();
                String str_pDetail = productDetail.getText().toString();

                if (TextUtils.isEmpty(str_pTitle) || TextUtils.isEmpty(str_pDetail) || TextUtils.isEmpty(str_pPriceMax)
                        || TextUtils.isEmpty(str_pDesc) || TextUtils.isEmpty(str_pPriceMin) || TextUtils.isEmpty(str_pnum)) {
                    pd.dismiss();
                    Toast.makeText(MulProducts.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
                else if (imageChecker == null) {
                    pd.dismiss();
                    Toast.makeText(MulProducts.this, "Please upload an image first", Toast.LENGTH_SHORT).show();
                }
                else {
                    savetoDatabase(str_pTitle, productStatus, str_pDesc, str_pPriceMin, str_pPriceMax, str_pnum, str_pDetail, downloadUri);
                }
            }
        });
    }

    private void savetoDatabase(String str_pTitle, String productStatus, String str_pDesc, String str_pPriceMin, String str_pPriceMax,String str_pnum, String str_pDetail, String downloadUri) {

        //Parsing Int to string
        int number = Integer.parseInt(str_pnum);
        int max = Integer.parseInt(str_pPriceMax);
        int min = Integer.parseInt(str_pPriceMin);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        //Entering a loop to add multiple products
                        for (int i = 0; i < number; i++){
                            //Getting a random price within the range given by the user
                            final int randomPrice = new Random().nextInt((max - min) + 1) + min;
                            String str_pPrice = String.valueOf(randomPrice);

                            //Getting random names to apend to the name given by the user
                            final String randomName = UUID.randomUUID().toString().substring(0,6);
                            final String finalName = str_pTitle + randomName;

                            Map<String, Object> product = new HashMap<>();
                            product.put("productTitle", finalName);
                            product.put("productDesc", str_pDesc);
                            product.put("productPrice", str_pPrice);
                            product.put("productDetail", str_pDetail);
                            product.put("productImage", downloadUri);
                            product.put("productStatus", productStatus);

                            db.collection("Products").add(product).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                                    pd.dismiss();
                                    Toast.makeText(MulProducts.this, "Successfully uploaded your product", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(MulProducts.this, "Error uploading your product", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }}

                else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(MulProducts.this, "get failed with " + task.getException(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        // Create a reference to "ProductImage.jpg"
        StorageReference productsImage = storageReference.child("Product images");

        // Create a reference to 'Product Images'
        StorageReference productsImageRef = storageReference.child("Product images/" + randomKey);

        // While the file names are the same, the references point to different files
        productsImage.getName().equals(productsImageRef.getName());    // true
        productsImage.getPath().equals(productsImageRef.getPath());    // false

        UploadTask uploadTask = productsImageRef.putFile(imageUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception exception) {

                // Handle unsuccessful uploads
                pd.dismiss();
                Log.d(TAG, "get failed with ", uploadTask.getException());
                String error = uploadTask.getException().toString();
                Toast.makeText(MulProducts.this, error, Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                pd.dismiss();
                Snackbar.make(findViewById(R.id.content4), "Image uploaded", Snackbar.LENGTH_LONG).show();

                productsImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Uri> task) {
                        downloadUri = task.getResult().toString();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(MulProducts.this, "Failed to get URL", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@androidx.annotation.NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Progress: " + (int) progressPercent + "%");
            }
        });

    }
}