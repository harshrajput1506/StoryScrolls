package com.hn.farm.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hn.farm.MainActivity;
import com.hn.farm.R;
import com.hn.farm.auth.LoginActivity;
import com.hn.farm.auth.RegisterActivity;

import java.util.Objects;

public class AdminAuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private final String TAG = "AdminAuthActivity";
    private TextInputLayout emailTextInput;
    private TextInputLayout passwordTextInput;
    private Button loginBtn;
    private ImageButton backBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailTextInput = findViewById(R.id.emailField);
        passwordTextInput = findViewById(R.id.passwordField);
        loginBtn = findViewById(R.id.login_button);
        backBtn = findViewById(R.id.back_btn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences(getResources().getString(R.string.sp_file), Context.MODE_PRIVATE);

        Objects.requireNonNull(emailTextInput.getEditText()).setOnClickListener(v -> {
            if(emailTextInput.isErrorEnabled()){
                emailTextInput.setError(null);
            }
        });

        Objects.requireNonNull(passwordTextInput.getEditText()).setOnClickListener(v -> {
            if(passwordTextInput.isErrorEnabled()){
                passwordTextInput.setError(null);
            }
        });

        loginBtn.setOnClickListener( v -> {
            String emailInput = emailTextInput.getEditText().getText().toString();
            String passwordInput = passwordTextInput.getEditText().getText().toString();
            if(!emailInput.isEmpty()){
                if(!passwordInput.isEmpty()) {
                    // check email is from admin credentials
                    checkEmail(emailInput, passwordInput);
                } else {
                    passwordTextInput.setError("Enter Password");
                }
            } else {
                emailTextInput.setError("Enter Email");
            }
        });

        backBtn.setOnClickListener( v-> {
            Intent intent = new Intent(AdminAuthActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void checkEmail(String email, String password){
        if(getResources().getString(R.string.admin_email).equals(email.trim())){
            login(email, password);
        } else {
            emailTextInput.setError("Admin credentials are incorrect");
        }
        /*db.collection("admin").whereEqualTo("email", email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){

                } else {
                    emailTextInput.setError("Admin credentials are incorrect");
                }
            }
        });*/
    }

    private void login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = task.getResult().getUser();
                            //updateUI(user);
                            Toast.makeText(AdminAuthActivity.this, "Login Successful",
                                    Toast.LENGTH_SHORT).show();
                            assert user != null;
                            String email = user.getEmail();
                            sharedPreferences.edit().putString("admin_email", email).apply();
                            sharedPreferences.edit().putBoolean("isAdmin", true).apply();
                            Intent intent = new Intent(AdminAuthActivity.this, AdminMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {


                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(AdminAuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }
}