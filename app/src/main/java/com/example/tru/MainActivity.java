package com.example.tru;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.tru.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    Button btnLogIn, btnSignUp;
    RelativeLayout rootLayout;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogIn = (Button) findViewById(R.id.bt_login);
        btnSignUp = (Button) findViewById(R.id.bt_signup);
        rootLayout = (RelativeLayout) findViewById(R.id.layout_root);

        FirebaseApp firebaseApp = FirebaseApp.initializeApp(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
    }

    private void userLogin() {
        EditText et_user = (EditText) findViewById(R.id.et_un);
        EditText et_pass = (EditText) findViewById(R.id.et_pass);

        String username = et_user.getText().toString();
        String password = et_pass.getText().toString();

        btnLogIn.setEnabled(false);

        final SpotsDialog waitDialog = new SpotsDialog(MainActivity.this);
        waitDialog.show();

        //Login
        auth.signInWithEmailAndPassword(username,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        waitDialog.dismiss();
                        startActivity(new Intent(MainActivity.this,Welcome.class));
                        finish();
                        Toast.makeText(MainActivity.this,"LogedIn",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitDialog.dismiss();
                        btnLogIn.setEnabled(true);
                        Snackbar.make(rootLayout,"Failed "+e.getMessage(),Snackbar.LENGTH_SHORT)
                        .show();
                    }
                });

    }

    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Register");
        dialog.setMessage("All fields are mandatory.");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View register = layoutInflater.inflate(R.layout.layout_register,null);

        final MaterialEditText etEmail = register.findViewById(R.id.et_email);
        final MaterialEditText etPass = register.findViewById(R.id.et_password);
        final MaterialEditText etName = register.findViewById(R.id.et_name);
        final MaterialEditText etPhone = register.findViewById(R.id.et_phone);

        dialog.setView(register);
        dialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if(TextUtils.isEmpty(etEmail.getText().toString()) ||
                        TextUtils.isEmpty(etPass.getText().toString()) ||
                        TextUtils.isEmpty(etName.getText().toString()) ||
                        TextUtils.isEmpty(etPhone.getText().toString())) {
                    Snackbar.make(rootLayout,"Please enter all fields",Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                auth.createUserWithEmailAndPassword(etEmail.getText().toString(),etPass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User();
                                user.setEmail(etEmail.getText().toString());
                                user.setName(etName.getText().toString());
                                user.setPassword(etPass.getText().toString());
                                user.setPhone(etPhone.getText().toString());

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout,"Registered",Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout,"Failed"+e.getMessage(),Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout,"Failed"+e.getMessage(),Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        });
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
