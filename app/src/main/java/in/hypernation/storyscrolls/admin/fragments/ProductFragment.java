package in.hypernation.storyscrolls.admin.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import in.hypernation.storyscrolls.ProductDetailActivity;
import in.hypernation.storyscrolls.R;
import in.hypernation.storyscrolls.adapters.ProductAdapter;
import in.hypernation.storyscrolls.admin.AdminMainActivity;
import in.hypernation.storyscrolls.admin.EditProductActivity;
import in.hypernation.storyscrolls.listeners.ProductListener;
import in.hypernation.storyscrolls.models.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {

    private RecyclerView rv;
    private FirebaseFirestore db;
    private final String TAG = "ProductFragment";
    private List<String> selectedItem;
    private boolean[] checkedItems;
    private String[] items;
    ProductAdapter productAdapter;


    public ProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductFragment newInstance(String param1, String param2) {
        return new ProductFragment();
    }

    public void showFilter(){
        showFilterDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        rv = view.findViewById(R.id.products_rv);

        db = FirebaseFirestore.getInstance();

        items = new String[]{"Fertilizer", "Pesticides", "Herbicides", "Fungicide", "Insecticide", "Rodenticides"};
        checkedItems = new boolean[items.length];
        selectedItem = new ArrayList<>();

        getProducts();

        return view;
    }

    private void getProducts(){
        db.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Product> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        String category = "Fertilizers";
                        if (document.contains("category")) category = document.getString("category");
                        list.add(new Product(
                                document.getId(),
                                document.getString("name"),
                                document.getString("image"),
                                document.getString("description"),
                                document.getLong("price"),
                                document.getLong("availableQuantity"),
                                document.getTimestamp("addDateTime"),
                                category
                        ));
                    }
                    Log.d(TAG, "onComplete: "+list);
                    setRv(list);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
    private void setRv(ArrayList<Product> list){
        rv.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        productAdapter = new ProductAdapter(list, getContext(), new ProductListener() {
            @Override
            public void onClick(Product product) {
                Intent i = new Intent(getActivity(), EditProductActivity.class);
                i.putExtra("product_detail", product);
                startActivity(i);
            }
        });
        rv.setAdapter(productAdapter);
    }

    private void showFilterDialog(){
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Filter By Category")
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which]= isChecked;
                        if(isChecked){
                            selectedItem.add(items[which]);
                        } else {
                            selectedItem.remove(items[which]);
                        }
                    }
                }).setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: "+selectedItem);
                        productAdapter.filterByCategory(selectedItem);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialogBuilder.show();
    }
}