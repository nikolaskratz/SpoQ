package com.example.nikolas.spotfiyusefirsttry;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText nickname;
    private EditText email;
    private EditText password;
    private EditText verifyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        nickname = findViewById(R.id.sign_up_nickname_et);
        email = findViewById(R.id.sign_up_email_et);
        password = findViewById(R.id.sign_up_password1_et);
        verifyPassword = findViewById(R.id.sign_up_password2_et);

        findViewById(R.id.sign_up_submit_b).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.sign_up_submit_b) {
            createAccount(nickname.getText().toString(), email.getText().toString(), password.getText().toString(),
                    verifyPassword.getText().toString());
        }
    }

    // TODO: 29/11/2018 Implement passwords comparision, nickname has to be checked if already exists.

    private void createAccount(final String nickname, String email, String password, String verifyPassword) {
        Log.d("MyTag", "createAccount:" + email);
        // add validation
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MyTag", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            addUserToFirebase(user, nickname, email);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MyTag", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void addUserToFirebase(FirebaseUser firebaseuser, String nickname, String email ) {
        if  (firebaseuser == null) return;

        //get user ID from firebase auth.
        String uid = firebaseuser.getUid();

        //create new UserInfo object
        UserInfo userInfo = new UserInfo(nickname,0,"pictureCode123", email);
        mDatabase.child(uid).setValue(userInfo);

        // add new identity
        mDatabase = FirebaseDatabase.getInstance().getReference("Identities");
        mDatabase.child(nickname).setValue(uid);

    }

    private void updateUI(FirebaseUser user) {
        startActivity( new Intent(this, MainAppActivity.class));
    }

}
