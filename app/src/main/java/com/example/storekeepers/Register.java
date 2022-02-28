package com.example.storekeepers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText name, email, password, cpassword;
    Button register;

    FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog pd;

    String usertype;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ProductDestination.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);
        register = findViewById(R.id.register);

        Spinner spinner = findViewById(R.id.spinnerGen);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.UserType, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

        auth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(Register.this);
                pd.setTitle("Saving your details");
                pd.setMessage("Please wait...");
                pd.show();

                String str_name = name.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();
                String str_cpassword = cpassword.getText().toString();


                if (TextUtils.isEmpty(str_name) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)
                        || TextUtils.isEmpty(str_cpassword))
                {
                    pd.dismiss();
                    Toast.makeText(Register.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
                else if (!str_password.equals(str_cpassword))
                {
                    pd.dismiss();
                    Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else if (str_password.length() < 6) {
                    pd.dismiss();
                    Toast.makeText(Register.this, "Your password must have more than 6 characters.", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(str_name, str_email, str_password);
                }
            }
        });

    }

    private void register(final String name, final String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            Map<String, Object> User = new HashMap<>();
                            User.put("User ID", userid);
                            User.put("email", email);
                            User.put("Last name", name);
                            User.put("Password", password);
                            User.put("User Type", usertype);


                            //Adding a new document with the userid as the document id
                            db.collection("Users").document(userid)
                                    .set(User)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Register.this, "Save successful", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Register.this, "Error saving your details", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else {
                            pd.dismiss();
                            Toast.makeText(Register.this, "You can't be registered with this email or password", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        usertype = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
