package in.hypernation.storyscrolls.admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import in.hypernation.storyscrolls.R;
import in.hypernation.storyscrolls.admin.fragments.AddProductFragment;
import in.hypernation.storyscrolls.admin.fragments.OrdersFragment;
import in.hypernation.storyscrolls.admin.fragments.ProductFragment;
import in.hypernation.storyscrolls.auth.LoginActivity;
import in.hypernation.storyscrolls.fragments.CartFragment;
import in.hypernation.storyscrolls.fragments.HomeFragment;
import in.hypernation.storyscrolls.fragments.ProfileFragment;

public class AdminMainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private final String TAG = "AdminMainActivity";
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private ProductFragment productFragment;
    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.topAppBar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        auth = FirebaseAuth.getInstance();
        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences(getResources().getString(R.string.sp_file), Context.MODE_PRIVATE);

        //setSupportActionBar(toolbar);

        productFragment = new ProductFragment();
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_layout, productFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.filter){
                    if(productFragment!=null){
                        productFragment.showFilter();
                    }
                }
                return false;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.product) {
                    switchToProduct();
                    return true;
                } else if (menuItem.getItemId() == R.id.add_product) {
                    switchToAddProduct();

                    return true;
                } else if (menuItem.getItemId() == R.id.orders) {
                    switchToOrders();
                    return true;
                }
                return false;
            }
        });

        toolbar.setNavigationOnClickListener( v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Confirmation")
                    .setMessage("Please confirm that you are trying to logout from the admin side.")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            auth.signOut();
                            sharedPreferences.edit().putBoolean("isAdmin", false).apply();
                            Intent i = new Intent(AdminMainActivity.this, LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        });

    }

    private void switchToProduct(){
        toolbar.setTitle("Admin");
        toolbar.inflateMenu(R.menu.main_app_bar_menu);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, new ProductFragment()).commit();
    }

    private void switchToAddProduct(){
        toolbar.setTitle("Add Product");
        toolbar.getMenu().clear();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, new AddProductFragment()).commit();
    }

    private void switchToOrders(){
        toolbar.setTitle("Orders");
        toolbar.getMenu().clear();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frame_layout, new OrdersFragment()).commit();
    }
}

