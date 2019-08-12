package com.example.nikolas.spotfiyusefirsttry;

import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class PasswordRecoveryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PasswordRec_debug";
    private EditText emailEt;
    private Button recoveryBt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        mAuth = FirebaseAuth.getInstance();
        emailEt = findViewById(R.id.pass_rec_et);
        recoveryBt = findViewById(R.id.pass_rec_bt);

        recoveryBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == recoveryBt.getId()) {
            sendRecoveryEmail();
        }
    }

    private void sendRecoveryEmail() {
        if (emailEt.length() > 0) {
            mAuth.sendPasswordResetEmail(emailEt.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (Exception e) {
                                    emailEt.setError( e.getLocalizedMessage());
                                }
                            }
                        }});
        }}
    }
