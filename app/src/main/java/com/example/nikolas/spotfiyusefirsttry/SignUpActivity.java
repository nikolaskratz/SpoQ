package com.example.nikolas.spotfiyusefirsttry;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "SignUpActivity_debug";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText nicknameEt;
    private EditText emailEt;
    private EditText passwordEt;
    private EditText verifyPasswordEt;
    private ImageView profileImage;
    private String profileImageString;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Intent CropIntent;
    private Uri imageUri;

    private boolean validateForm() {

        boolean valid = true;

        String nickname = nicknameEt.getText().toString();
        String email = emailEt.getText().toString();
        String pass1 = passwordEt.getText().toString();
        String pass2 = verifyPasswordEt.getText().toString();

        // NICKNAME
        // if user exists is an async task. The logic is the onFocusChanged Listener.
        
        if (containsWhitespace(nickname)) {
            nicknameEt.setError("Whitespaces not allowed.");
            valid = false;
        }
        else if (nickname.length() < 3) {
            nicknameEt.setError("Must be longer than 3.");
            valid = false;
        }

        else if(nickname.length() > 15) {
            nicknameEt.setError("Must be shorter than 15.");
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

        //PASSWORD
        if (!pass1.equals(pass2)) {
            verifyPasswordEt.setError("Passwords are not matching");
            valid = false;
        }

         else if (passwordEt.length() < 6) {
             passwordEt.setError("Must be longer than 5.");
             valid = false;
         }

        return valid;
    }

    private boolean nicknameExists(String nickname) {

        FirebaseOperator.getInstance().readData(database.getReference().child("Identities").child(nickname), new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    nicknameEt.setError("Nickname in use!");
                }
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFailure() {
            }
        });

        return true;
    }

    // Check if has whitespaces in a string.
    private Boolean containsWhitespace(String nickname) {

        for (char ch: nickname.toCharArray()) {
            if(Character.isWhitespace(ch)){
                return true;
            }
        }
        return false;
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

        nicknameEt.setOnFocusChangeListener(this);
    }

    // Plan for pictures :
    // Get pic
    // Croop it
    // Compress -find best compressing algo
    // send it to FB

    private void CropImage() {
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");
            Log.d(TAG, "CropImage:   FAIL" + imageUri);
            CropIntent.setDataAndType(imageUri, "image/*");
            CropIntent.putExtra("crop","true");
            CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);
            CropIntent.putExtra("aspectX", 3);
            CropIntent.putExtra("aspectY", 4);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent,1 );
        }
        catch (ActivityNotFoundException ex) {
            Log.d(TAG, "CropImage:   exception");
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        Log.d(TAG, "onActivityResult: res " + resultCode );
        Log.d(TAG, "onActivityResult: req " + reqCode );


        if (resultCode == -1 && reqCode == 1) {
            CropImage();
        }
        else if (reqCode == 2 ) {
            imageUri = data.getData();
        }
        else if (reqCode == 1) {
                if(data != null){
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = bundle.getParcelable("data");
                    profileImage.setImageBitmap(bitmap);
                }



//            try {
//
//                imageUri = data.getData();
//                Log.d(TAG, "onActivityResult 1: " + imageUri);
//                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                Log.d(TAG, "onActivityResult 2: " + imageUri);
//                // TODO: 1/24/2019 implement better solution for profile image. Another thread? Compressing after pressing sing up? Cropping?
//
//                //profileImage.setImageBitmap(Bitmap.createScaledBitmap(selectedImage,  (int)(selectedImage.getWidth()*0.4), (int)(selectedImage.getHeight()*0.4), false));
//                CropImage();
//                profileImage.setImageBitmap(selectedImage);
//
//                //converting Bitmap to String stream
//                //profileImageString = BitMapToString(selectedImage);
//
//
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
//            }

        }else {
            Toast.makeText(SignUpActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus && nicknameEt.length() > 0){
            nicknameExists(nicknameEt.getText().toString());
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
            int RESULT_LOAD_IMG = 1;
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        }
    }

    private void createAccount(final String nickname, String email, String password, String verifyPassword) {
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed - in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            addUserToFirebase(user, nickname, email);

                            updateUI();
                        } else {
                                //Firebase validation
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                }
                                catch(FirebaseAuthWeakPasswordException e) {
                                    passwordEt.setError("Weak password");
                                }
                                catch(FirebaseAuthInvalidCredentialsException e) {
                                    emailEt.setError("Wrong format.");
                                }
                                catch(FirebaseAuthUserCollisionException e) {
                                    emailEt.setError("This email exists.");
                                }
                                catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                        }
                    }
                });
    }
    private void addUserToFirebase(FirebaseUser firebaseuser, String nickname, String email ) {
        if  (firebaseuser == null) return;

        //get user ID from firebase auth.
        String uid = firebaseuser.getUid();

        //create new UserInfo object
        UserInfo userInfo = new UserInfo(nickname,0, profileImageString, email);
        mDatabase.child(uid).setValue(userInfo);

        // add new identity
        mDatabase = FirebaseDatabase.getInstance().getReference("Identities");
        mDatabase.child(nickname).setValue(uid);

    }

    private void updateUI() {
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
