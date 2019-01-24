package com.example.nikolas.spotfiyusefirsttry;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuthException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUpActivity_debug";
    private static int RESULT_LOAD_IMG = 1;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText nicknameEt;
    private EditText emailEt;
    private EditText passwordEt;
    private EditText verifyPasswordEt;
    private ImageView profileImage;
    private String profileImageString;

    private boolean validateForm() {

        boolean valid = true;

        String nickname = nicknameEt.getText().toString();
        String email = emailEt.getText().toString();

        // NICKNAME
        if (nickname.length() < 3) {
            nicknameEt.setError("Too short.");
            valid = false;
        }

        else if(nickname.length() > 15) {
            nicknameEt.setError("Too long.");
            valid = false;
        }

        // EMAIL
        if (TextUtils.isEmpty(email)){
            emailEt.setError("Can't be empty.");
            valid = false;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Wrong format.");
            valid = false;
        }

//        if (TextUtils.isEmpty(email)) {
//            emailEt.setError("Required.");
//            valid = false;
//        } else {
//            emailEt.setError(null);
//        }
//
//        if (e instanceof FirebaseAuthWeakPasswordException);
//        String password = passwordEt.getText().toString();
//        if (TextUtils.isEmpty(password)) {
//            passwordEt.setError(e.getMessage());
//            valid = false;
//        } else {
//            passwordEt.setError(null);
//        }

        return valid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        nicknameEt = findViewById(R.id.sign_up_nickname_et);
        emailEt = findViewById(R.id.sign_up_email_et);
        passwordEt = findViewById(R.id.sign_up_password1_et);
        verifyPasswordEt = findViewById(R.id.sign_up_password2_et);

        profileImage = findViewById(R.id.profile_image);

        findViewById(R.id.sign_up_submit_b).setOnClickListener(this);
        findViewById(R.id.profile_image).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // TODO: 1/24/2019 implement better solution for profile image. Another thread? Compressing after pressing sing up? Cropping?

                //profileImage.setImageBitmap(Bitmap.createScaledBitmap(selectedImage,  (int)(selectedImage.getWidth()*0.4), (int)(selectedImage.getHeight()*0.4), false));
                profileImage.setImageBitmap(selectedImage);
                //converting Bitmap to String stream
                profileImageString = BitMapToString(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(SignUpActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.sign_up_submit_b) {

            createAccount(nicknameEt.getText().toString(), emailEt.getText().toString(), passwordEt.getText().toString(),
                    verifyPasswordEt.getText().toString());
        }
        else if (i == R.id.profile_image) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        }
    }

    // TODO: 29/11/2018 Implement passwords comparision, nickname has to be checked if already exists.

    private void createAccount(final String nickname, String email, String password, String verifyPassword) {
        if (!validateForm()) {
            return;
        }

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
        UserInfo userInfo = new UserInfo(nickname,0,profileImageString, email);
        mDatabase.child(uid).setValue(userInfo);

        // add new identity
        mDatabase = FirebaseDatabase.getInstance().getReference("Identities");
        mDatabase.child(nickname).setValue(uid);
        
    }

    private void updateUI(FirebaseUser user) {
        startActivity( new Intent(this, MainAppActivity.class));
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        // quality  100 = best quality, 0 = worst quality
        bitmap.compress(Bitmap.CompressFormat.PNG,30, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


}
