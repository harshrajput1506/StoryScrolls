package in.hypernation.storyscrolls.auth;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import in.hypernation.storyscrolls.R;

import java.util.Objects;

public class ForgetActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private TextInputLayout emailInputLayout;
    private Button forgetBtn;
    private FirebaseAuth auth;
    private final String TAG = "ForgetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.back_btn);
        emailInputLayout = findViewById(R.id.emailField);
        forgetBtn = findViewById(R.id.forget_btn);

        auth = FirebaseAuth.getInstance();

        forgetBtn.setOnClickListener( v-> {
            String emailInput = Objects.requireNonNull(emailInputLayout.getEditText()).getText().toString();
            if(!emailInput.isEmpty()){
                sendResetEmail(emailInput);
            } else {
                emailInputLayout.setError("Enter your Email");
            }
        });

        backBtn.setOnClickListener( v-> {
            super.getOnBackPressedDispatcher().onBackPressed();
        });

        Objects.requireNonNull(emailInputLayout.getEditText()).setOnClickListener(v -> {
            if(emailInputLayout.isErrorEnabled()) emailInputLayout.setError(null);
        });

    }


    private void sendResetEmail(String email){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                    Toast.makeText(ForgetActivity.this, "Email Sent", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}