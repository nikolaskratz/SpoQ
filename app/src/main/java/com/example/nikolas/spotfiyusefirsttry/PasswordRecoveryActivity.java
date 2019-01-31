package com.example.nikolas.spotfiyusefirsttry;

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

public class PasswordRecoveryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PasswordRec_debug";
    private EditText emailEt;
    private Button recoveryBt;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        mAuth = FirebaseAuth.getInstance();

        emailEt = findViewById(R.id.pass_rec_et);
        recoveryBt = findViewById(R.id.pass_rec_bt);

        recoveryBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == recoveryBt.getId()) {
            Log.d(TAG, "onClick:  ACTION");
            sendRecoveryEmail();
        }
    }

    private void sendRecoveryEmail() {
        if (emailEt.length() > 0) {
            String emailAddress = "kojandreas@gmail.com";

            mAuth.sendPasswordResetEmail(emailEt.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                            }
                        }
                    });
        }
    }
}