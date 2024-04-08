package in.hypernation.storyscrolls.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import in.hypernation.storyscrolls.MainActivity;
import in.hypernation.storyscrolls.R;
import in.hypernation.storyscrolls.admin.AdminAuthActivity;
import in.hypernation.storyscrolls.admin.AdminMainActivity;
import in.hypernation.storyscrolls.models.User;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final String TAG = "LoginActivity";
    private TextInputLayout emailTextInput;
    private TextInputLayout passwordTextInput;
    private Button loginBtn, adminLoginBtn;
    private TextView registerBtn;
    private TextView forgetBtn;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        if(currentUser != null){
            Intent intent;
            if(isAdmin){
                intent = new Intent(LoginActivity.this, AdminMainActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailTextInput = findViewById(R.id.emailField);
        passwordTextInput = findViewById(R.id.passwordField);
        loginBtn = findViewById(R.id.login_button);
        registerBtn = findViewById(R.id.register_btn);
        forgetBtn = findViewById(R.id.forget_btn);
        adminLoginBtn = findViewById(R.id.admin_login_btn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences(getResources().getString(R.string.sp_file), Context.MODE_PRIVATE);

        loginBtn.setOnClickListener(v -> {
            String emailInput = Objects.requireNonNull(emailTextInput.getEditText()).getText().toString();
            String passwordInput = Objects.requireNonNull(passwordTextInput.getEditText()).getText().toString();
            if(!emailInput.isEmpty()){
                if(!passwordInput.isEmpty()) {
                    login(emailInput, passwordInput);
                } else {
                    passwordTextInput.setError("Enter Password");
                }
            } else {
                emailTextInput.setError("Enter Email");
            }
        });


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

        registerBtn.setOnClickListener( v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
            }
        });

        adminLoginBtn.setOnClickListener( v -> {
            Intent i = new Intent(LoginActivity.this, AdminAuthActivity.class);
            startActivity(i);
        });


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
                            Toast.makeText(LoginActivity.this, "Login Successful",
                                    Toast.LENGTH_SHORT).show();
                            assert user != null;
                            String email = user.getEmail();
                            sharedPreferences.edit().putString("email", email).apply();
                            getUser(user);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {


                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private  void getUser(FirebaseUser firebaseUser) {
        if(firebaseUser!=null){
            db.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().exists()){
                            DocumentSnapshot doc = task.getResult();
                            User user = new User(
                                    doc.getId(),
                                    doc.getString("name"),
                                    doc.getString("number"),
                                    doc.getString("email"),
                                    doc.getString("address")
                            );
                            updatePreferenceData(user);

                        }else {
                            Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void updatePreferenceData(User user){
        sharedPreferences.edit().putString("email", user.getEmail()).apply();
        sharedPreferences.edit().putString("name", user.getName()).apply();
        sharedPreferences.edit().putString("address", user.getAddress()).apply();
        sharedPreferences.edit().putString("number", user.getNumber()).apply();
    }

}