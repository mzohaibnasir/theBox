package com.madein.thebox;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText edtUsername,edtPassword,edtEmail;
    private Button btnSubmit;
    private TextView txtLogInInfo;
    private boolean isSigningUp=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail = findViewById(R.id.edtEmail);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtLogInInfo = findViewById(R.id.txtLogInInfo);


        //check if user is already logged in
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this,FriendsActivity.class));
            finish();
        }



        //register-login

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()) {
                    if (isSigningUp && edtUsername.getText().toString().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                        return;

                    }
                }


                if (isSigningUp) {
                    handleSignUp();
                } else {
                    handleLogIn();
                }
            }
        });

        //already have acccount
        txtLogInInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSigningUp) {
                    isSigningUp = false;
                    edtUsername.setVisibility(View.GONE);
                    btnSubmit.setText("LOG IN");

                    txtLogInInfo.setText("Don't have an account? \n\t\t\t\t\t\t Sign Up");
                } else {
                    isSigningUp = true;
                    btnSubmit.setText("SIGN UP");
                    edtUsername.setVisibility(View.VISIBLE);
                    txtLogInInfo.setText("Already have account \n\t\t\t\t\t\t Log in");
                }
            }

        });//set onclick listener


    }

    private void handleSignUp(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(edtUsername.getText().toString(),edtEmail.getText().toString(),"")); //to get unique path in database

                    Toast.makeText(MainActivity.this,"Signed Up Successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,FriendsActivity.class));

                }else{
                    Toast.makeText(MainActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }//handle SignUp



    private void handleLogIn(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Loged in Successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,FriendsActivity.class));

                }else{
                    Toast.makeText(MainActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }







}