package in.hypernation.storyscrolls;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import in.hypernation.storyscrolls.adapters.ProductAdapter;
import in.hypernation.storyscrolls.fragments.CartFragment;
import in.hypernation.storyscrolls.fragments.HomeFragment;
import in.hypernation.storyscrolls.fragments.ProfileFragment;
import in.hypernation.storyscrolls.models.Product;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private final String TAG = "MainActivity";
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.topAppBar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        //setSupportActionBar(toolbar);


        homeFragment = new HomeFragment();
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_layout, homeFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.filter){
                    if(homeFragment!=null){
                        homeFragment.showFilter();
                        return true;
                    }
                } else if (item.getItemId()==R.id.user_order) {
                    Intent i = new Intent(MainActivity.this, OrderActivity.class);
                    startActivity(i);
                }

                return false;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.home_navigation) {
                    switchToHome();
                    return true;
                } else if (menuItem.getItemId() == R.id.cart_navigation) {
                    switchToCart();
                    return true;
                } else if (menuItem.getItemId() == R.id.profile_navigation) {
                    switchToProfile();
                    return true;
                }
                return false;
            }
        });

    }

    private void switchToHome(){
        toolbar.setTitle(R.string.app_name);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.main_app_bar_menu);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
    }

    private void switchToCart(){
        toolbar.getMenu().clear();
        toolbar.setTitle("Cart");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, new CartFragment()).commit();
    }

    private void switchToProfile(){
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.user_order_menu);
        toolbar.setTitle("Profile");
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, new ProfileFragment()).commit();
    }





    private void addData(){
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Product Name 2");
        data.put("image", "https://firebasestorage.googleapis.com/v0/b/test-593cf.appspot.com/o/Screenshot%202024-04-02%20000659.png?alt=media&token=0a1dc9f4-0ac0-405a-9f07-52401f309d9f");
        data.put("description", "This is test description");
        data.put("price", 1000);
        data.put("availableQuantity", 10);
        data.put("addDateTime", new Timestamp(Calendar.getInstance().getTime()));

        db.collection("products").add(data).addOnSuccessListener(
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
    }


}