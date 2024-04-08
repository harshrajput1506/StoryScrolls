package in.hypernation.storyscrolls.auth;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import in.hypernation.storyscrolls.MainActivity;
import in.hypernation.storyscrolls.R;
import in.hypernation.storyscrolls.models.User;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private final String TAG = "LoginActivity";
    private TextInputLayout emailTextInput;
    private TextInputLayout passwordTextInput;
    private Button registerBtn;
    private TextView loginBtn;
    private ImageButton backBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regsiter);

        emailTextInput = findViewById(R.id.emailField);
        passwordTextInput = findViewById(R.id.passwordField);
        registerBtn = findViewById(R.id.register_btn);
        loginBtn = findViewById(R.id.login_button);
        backBtn = findViewById(R.id.back_btn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences(getResources().getString(R.string.sp_file), Context.MODE_PRIVATE);


        registerBtn.setOnClickListener(v -> {

            String emailInput = emailTextInput.getEditText().getText().toString();
            String passwordInput = passwordTextInput.getEditText().getText().toString();
            if(!emailInput.isEmpty()){
                if(!passwordInput.isEmpty()) {
                    register(emailInput, passwordInput);
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

        loginBtn.setOnClickListener( v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        backBtn.setOnClickListener( v-> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void register(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser firebaseUser = task.getResult().getUser();

                            //updateUI(user);

                            assert firebaseUser != null;
                            String email = firebaseUser.getEmail();
                            User user = new User(firebaseUser.getUid(), email);
                            addUser(user);
                            sharedPreferences.edit().putString("email", email).apply();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void addUser(User user){
        db.collection("users").document(user.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
                Toast.makeText(RegisterActivity.this, "Register Successful",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error add user document", e);
            }
        });
    }
}