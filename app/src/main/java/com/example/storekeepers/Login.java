package com.example.storekeepers;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView txt_forgot;

    private FirebaseAuth auth;

    FirebaseUser firebaseUser;

    UserType UT;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


//        This redirects if the user isnt null, to make sure the user is always signed in
        if (firebaseUser!=null){

            Intent intent;
                    intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
//
//            UT = new UserType();
//            String uT = UT.getUserType();
//
//            if (uT!=null){
//                if (uT.equalsIgnoreCase("Admin")){
//
//                    Intent intent;
//                    intent = new Intent(Login.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//                else if (uT.equalsIgnoreCase("Regular")){
//
//                    Intent intent;
//                    intent = new Intent(Login.this, ViewProducts.class);
//                    startActivity(intent);
//                    finish();
//                }
//
//                else if (uT.equalsIgnoreCase("Customer")){
//
//                    Intent intent;
//                    intent = new Intent(Login.this, ViewProducts.class);
//                    startActivity(intent);
//                    finish();
//                }
            }
//            else {
//                Intent intent;
//                intent = new Intent(Login.this, Login.class);
//                startActivity(intent);
//                finish();
    }
//        }

//        finish();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        txt_forgot = findViewById(R.id.forgot);
        login = findViewById(R.id.login);

        auth = FirebaseAuth.getInstance();

        txt_forgot.setVisibility(View.GONE);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(Login.this);
                pd.setMessage("Please wait...");
                pd.show();

                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    pd.dismiss();
                    Toast.makeText(Login.this, "All fields required!", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(str_email, str_password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){

                                        //If Email exists

                                        //Going into the database to get the usertype

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

                                                        UserType UT = new UserType();
                                                        UT.userType = usertype;

                                                        if (usertype.equalsIgnoreCase("Admin")){
                                                            pd.dismiss();
                                                            Intent intent;
                                                            intent = new Intent(Login.this, MainActivity.class);
                                                            startActivity(intent);


                                                        }
                                                        else if (usertype.equalsIgnoreCase("Regular")){
                                                            pd.dismiss();
                                                            Intent intent;
                                                            intent = new Intent(Login.this, ViewProducts.class);
                                                            startActivity(intent);

                                                        }

                                                        else if (usertype.equalsIgnoreCase("Customer")){
                                                            pd.dismiss();
                                                            Intent intent;
                                                            intent = new Intent(Login.this, ViewProducts.class);
                                                            startActivity(intent);

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
                                    else{
                                        pd.dismiss();
                                        Toast.makeText(Login.this, "Email or password incorrect", Toast.LENGTH_SHORT).show();
                                        txt_forgot.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                }
            }
        });
    }
}