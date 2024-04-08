package in.hypernation.storyscrolls.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import in.hypernation.storyscrolls.R;
import in.hypernation.storyscrolls.adapters.CartAdapter;
import in.hypernation.storyscrolls.listeners.CartListener;
import in.hypernation.storyscrolls.models.Order;
import in.hypernation.storyscrolls.models.Product;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {

    private RecyclerView rv;
    private Button checkoutBtn;
    private TextView totalPrice, emptyMsg;
    private ArrayList<Product> products;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private final String TAG = "CartFragment";
    private CartAdapter cartAdapter;
    private LinearLayout checkoutLayout;
    private MaterialAlertDialogBuilder dialogBuilder;
    private int totalItems = 0;
    private SharedPreferences preferences;
    private FirebaseUser user;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        return new CartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        rv = view.findViewById(R.id.cart_rv);
        totalPrice = view.findViewById(R.id.total_price);
        checkoutBtn = view.findViewById(R.id.checkout_btn);
        checkoutLayout = view.findViewById(R.id.checkout_layout);
        emptyMsg = view.findViewById(R.id.cart_empty_msg);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        preferences = requireContext().getSharedPreferences(getResources().getString(R.string.sp_file), Context.MODE_PRIVATE);

        getCartProduct();





        checkoutBtn.setOnClickListener( v-> {
            String email = preferences.getString("email", "");
            String number = preferences.getString("number", "");
            String address = preferences.getString("address", "");
            String name = preferences.getString("name", "");

            boolean check = email.isEmpty() || address.isEmpty() || name.isEmpty() || number.isEmpty();
            Log.d(TAG, "onCreateView: "+email.isEmpty() +" "+address.isEmpty()+" "+ name.isEmpty()+" "+number.isEmpty());
            if (check){
                Toast.makeText(getActivity(), "Complete Your Profile First!", Toast.LENGTH_SHORT).show();
            } else {
                showConfirmDialog();
            }

        });

        return view;
    }

    private void showConfirmDialog(){
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_confirm_dialog, null);
        TextView itemNo = dialogView.findViewById(R.id.items);
        TextView totalAmount = dialogView.findViewById(R.id.total_amount);
        String itemsTxt = String.valueOf(totalItems);
        itemNo.setText(itemsTxt);
        totalAmount.setText(totalPrice.getText());

        dialogBuilder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Order Confirmation")
                .setView(dialogView)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Order order = new Order(
                                user.getUid(),
                                preferences.getString("name", ""),
                                preferences.getString("email", ""),
                                preferences.getString("number", ""),
                                preferences.getString("address", ""),
                                totalPrice.getText().toString(),
                                totalItems,
                                products,
                                "COD",
                                new Timestamp(Calendar.getInstance().getTime())
                        );
                        addOrder(order);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        dialogBuilder.show();
    }

    private void showCart(Boolean isEmpty){
        if(isEmpty){
            emptyMsg.setVisibility(View.VISIBLE);
            checkoutLayout.setVisibility(View.GONE);
            rv.setVisibility(View.GONE);
        } else {
            emptyMsg.setVisibility(View.GONE);
            checkoutLayout.setVisibility(View.VISIBLE);
            rv.setVisibility(View.VISIBLE);
        }
    }

    private void changeTotalPrice(){
        int totalPriceInt = 0;
        totalItems = 0;
        for (Product product: products) {
            int productTotalPrice = (int) ((int) product.getPrice()*product.getAvailableQuantity());
            totalPriceInt = totalPriceInt+productTotalPrice;
            totalItems = (int) (totalItems+product.getAvailableQuantity());
        }
        String totalPriceTxt = getResources().getString(R.string.RUPEE_SYMBOL)+ " " + totalPriceInt;
        totalPrice.setText(totalPriceTxt);
        showCart(products.isEmpty());
    }

    private void setRv(ArrayList<Product> list){
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        cartAdapter = new CartAdapter(list, getContext(), new CartListener() {
            @Override
            public void onDelete(String id, int index) {
                deleteProduct(id, index, false);
            }

            @Override
            public void onAddQuantity(Product product, int quantity) {
                int index = products.indexOf(product);
                if(quantity!=0){
                    updateQuantity(product.getId(), quantity, index);
                } else {
                    deleteProduct(product.getId(), index, false);
                }
            }

            @Override
            public void onRemoveQuantity(Product product, int quantity) {
                int index = products.indexOf(product);
                if(quantity!=0){
                    updateQuantity(product.getId(), quantity, index);
                } else {
                    deleteProduct(product.getId(), index, false);
                }
            }
        });
        rv.setAdapter(cartAdapter);
        changeTotalPrice();
    }

    private void getCartProduct(){
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            db.collection("users")
                    .document(user.getUid())
                    .collection("cart")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                QuerySnapshot docs = task.getResult();
                                if(docs.isEmpty()){
                                    // show cart is empty
                                    Log.d(TAG, "onComplete: "+docs);
                                    showCart(true);
                                } else {
                                    // show rv and checkout layout
                                    ArrayList<Product> list = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : docs) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        String category = "Fertilizers";
                                        if (document.contains("category")) category = document.getString("category");
                                        list.add(new Product(
                                                document.getId(),
                                                document.getString("name"),
                                                document.getString("image"),
                                                document.getString("description"),
                                                document.getLong("price"),
                                                document.getLong("quantity"),
                                                document.getTimestamp("add_date"),
                                                category
                                        ));
                                    }
                                    Log.d(TAG, "onComplete: "+list);
                                    products = list;
                                    setRv(products);
                                    showCart(false);
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
        }
    }

    private void deleteProduct(String productID, int index, boolean isDeleteAll){
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            db.collection("users")
                    .document(user.getUid())
                    .collection("cart")
                    .document(productID)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            if(!isDeleteAll) {
                                products.remove(index);
                                cartAdapter.changeList(products);
                                cartAdapter.notifyItemRemoved(index);
                                changeTotalPrice();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Product can't be delete", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
        }
    }

    private void updateQuantity(String productID, int quantity, int index){
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            db.collection("users")
                    .document(user.getUid())
                    .collection("cart")
                    .document(productID)
                    .update("quantity", quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            products.get(index).setAvailableQuantity(quantity);
                            changeTotalPrice();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Quantity can't be change", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
    }

    private void addOrder(Order order){
        db.collection("orders").add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                Toast.makeText(getActivity(), "Order Placed", Toast.LENGTH_SHORT).show();
                deleteCart();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
                Toast.makeText(getActivity(), "Order can't be placed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deleteCart(){
        showCart(true);
        for (Product product: products) {
            deleteProduct(product.getId(), products.indexOf(product), true);
        }
    }

}