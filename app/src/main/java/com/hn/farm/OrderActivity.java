package com.hn.farm;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hn.farm.adapters.OrderAdapter;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView rv;
    private FirebaseAuth auth;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv = findViewById(R.id.orders_rv);
        toolbar = findViewById(R.id.topAppBar);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        assert user!=null;
        getOrders(user.getUid());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderActivity.super.getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void getOrders(String uid){
        db.collection("orders").whereEqualTo("uid", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    ArrayList<QueryDocumentSnapshot> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG", document.getId() + " => " + document.getData());
                        orders.add(document);
                    }
                    rv.setLayoutManager(new LinearLayoutManager(OrderActivity.this, LinearLayoutManager.VERTICAL, false));
                    rv.setAdapter(new OrderAdapter(OrderActivity.this, orders, false));


                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());

                }
            }
        });
    }
}