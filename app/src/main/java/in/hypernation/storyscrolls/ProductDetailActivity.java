package in.hypernation.storyscrolls;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import in.hypernation.storyscrolls.models.Product;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private Product product;
    private TextView name, description, price, category;
    private ImageView image;
    private Button cartBtn;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private final String TAG = "ProductDetailActivity";
    private Boolean isAdded = false;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        category = findViewById(R.id.category);
        image = findViewById(R.id.image);
        price = findViewById(R.id.price);
        cartBtn = findViewById(R.id.add_cart_btn);
        toolbar = findViewById(R.id.topAppBar);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if(getIntent().hasExtra("product_detail")) {
            product = getIntent().getParcelableExtra("product_detail");
        }

        assert product != null;
        name.setText(product.getName());
        description.setText(product.getDescription());
        String priceTxt = getResources().getString(R.string.RUPEE_SYMBOL)+" "+product.getPrice();
        price.setText(priceTxt);
        category.setText(product.getCategory());
        Glide.with(this).load(product.getImage()).into(image);

        checkIsAdded();

        cartBtn.setOnClickListener( v-> {
            if(isAdded){
                // Open Cart Activity
                Intent i = new Intent(ProductDetailActivity.this, CartActivity.class);
                startActivity(i);
            } else {
                addProductInCart();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetailActivity.super.getOnBackPressedDispatcher().onBackPressed();
            }
        });


    }

    private void isAdded(){
        cartBtn.setText("Go To Cart");
        isAdded =true;
    }

    private void isNotAdded(){
        cartBtn.setText("Add To Cart");
        isAdded = false;
    }

    private void checkIsAdded(){
        if(user!=null){
            String id = user.getUid();
            db.collection("users").document(id).collection("cart").document(product.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        Log.d(TAG, "onComplete: exist "+doc.exists());
                        Log.d(TAG, "onComplete: data"+doc);
                        if(doc.exists()){
                            isAdded();
                        } else {
                            isNotAdded();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        } else {
            Toast.makeText(this, "Login again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addProductInCart(){
        Map<String, Object> data = new HashMap<>();
        data.put("name", product.getName());
        data.put("image", product.getImage());
        data.put("description", product.getDescription());
        data.put("price", product.getPrice());
        data.put("quantity", 1);
        data.put("add_date", new Timestamp(Calendar.getInstance().getTime()));

        if(user!=null){
            String id = user.getUid();
            db.collection("users").document(id)
                    .collection("cart")
                    .document(product.getId())
                    .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            Toast.makeText(ProductDetailActivity.this, "Added", Toast.LENGTH_SHORT).show();
                            cartBtn.setText("Go To Cart");
                            isAdded =true;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                            Toast.makeText(ProductDetailActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}