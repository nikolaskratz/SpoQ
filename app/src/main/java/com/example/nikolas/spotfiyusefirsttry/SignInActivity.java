package com.example.nikolas.spotfiyusefirsttry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText emailEt;
    private EditText passwordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailEt = findViewById(R.id.signIn_form_email_et);
        passwordEt = findViewById(R.id.signIn_form_password_et);

        // Initialize listeners on buttons
        findViewById(R.id.signIn_signIn_b).setOnClickListener(this);
        findViewById(R.id.signIn_signUp_b).setOnClickListener(this);
        findViewById(R.id.signIn_reset_b).setOnClickListener(this);
    }

    // Check if user is signed in (non-null) and update UI accordingly.
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    // Update user's UI if the user is signed in --> go to MainAppActivity
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            startActivity( new Intent (this, MainAppActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
            if(i == R.id.signIn_signIn_b) {
                signIn(emailEt.getText().toString(), passwordEt.getText().toString());
            }
            else if (i == R.id.signIn_signUp_b) {
                startActivity( new Intent(this, SignUpActivity.class));
            }
            else if ( i == R.id.signIn_reset_b) {

                Log.d("CustomTag", "send mail: ");
                startActivity( new Intent(this, PasswordRecoveryActivity.class));
            }
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MyTag","signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Wrong email or password!",
                                    Toast.LENGTH_SHORT).show();
                            // create object in shared preferences
                        }
//  seems to be not useful
//                        if (!task.isSuccessful()) {
//                            Toast.makeText(SignInActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailEt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Required.");
            valid = false;
        } else {
            emailEt.setError(null);
        }

        String password = passwordEt.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Required.");
            valid = false;
        } else {
            passwordEt.setError(null);
        }

        return valid;
    }
}
