package com.hn.farm.admin.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hn.farm.R;
import com.hn.farm.models.Product;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProductFragment extends Fragment {

    private final int PICK_IMAGE_REQUEST = 6500;
    private final String TAG = "AddProductFragment";
    private ImageView image;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private TextInputLayout categoriesField, nameField, descriptionField, priceField;
    private MaterialAutoCompleteTextView categoriesMenu;
    private Button addBtn;
    private Uri imageUri;

    public AddProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddProductFragment newInstance(String param1, String param2) {
        AddProductFragment fragment = new AddProductFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    image.setImageURI(uri);
                    imageUri = uri;
                    //uploadImage(uri);
                }
            });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        image = view.findViewById(R.id.image);
        addBtn = view.findViewById(R.id.add_product_btn);
        categoriesMenu = view.findViewById(R.id.categories_menu);
        nameField = view.findViewById(R.id.nameField);
        descriptionField = view.findViewById(R.id.descriptionField);
        priceField = view.findViewById(R.id.priceField);
        categoriesField = view.findViewById(R.id.categoriesField);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        image.setOnClickListener( v-> {
            mGetContent.launch("image/*");
        });

        String[] items = new String[]{"Fertilizer", "Pesticides", "Herbicides", "Fungicide", "Insecticide", "Rodenticides"};
        categoriesMenu.setSimpleItems(items);

        addBtn.setOnClickListener( v-> {
            String nameInput = nameField.getEditText().getText().toString();
            String descriptionInput = descriptionField.getEditText().getText().toString();
            String priceInput = priceField.getEditText().getText().toString();
            String categoryInput = categoriesMenu.getText().toString();
            if (!nameInput.isEmpty()){
                if (!descriptionInput.isEmpty()){
                    if(!priceInput.isEmpty()){
                        if(!categoryInput.isEmpty()){
                            if(imageUri!=null){
                                Map<String, Object> product = new HashMap<>();
                                product.put("name", nameInput);
                                product.put("description", descriptionInput);
                                product.put("price", Integer.valueOf(priceInput));
                                product.put("availableQuantity", 1);
                                product.put("addDateTime", new Timestamp(Calendar.getInstance().getTime()));
                                product.put("category", categoryInput);
                                uploadImage(imageUri, product);
                            } else {
                                Toast.makeText(getActivity(), "Select product image", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            categoriesField.setError("Choose Category");
                        }
                    } else {
                        priceField.setError("Enter Price");
                    }
                } else {
                    descriptionField.setError("Enter Description");
                }
            } else {
                nameField.setError("Enter Product Name");
            }
        });

        Objects.requireNonNull(nameField.getEditText()).setOnClickListener(v -> {
            if(nameField.isErrorEnabled()){
                nameField.setError(null);
            }
        });

        Objects.requireNonNull(priceField.getEditText()).setOnClickListener(v -> {
            if(priceField.isErrorEnabled()){
                priceField.setError(null);
            }
        });

        Objects.requireNonNull(descriptionField.getEditText()).setOnClickListener(v -> {
            if(descriptionField.isErrorEnabled()){
                descriptionField.setError(null);
            }
        });

        Objects.requireNonNull(categoriesField.getEditText()).setOnClickListener(v -> {
            if(categoriesField.isErrorEnabled()){
                categoriesField.setError(null);
            }
        });

        return view;
    }

    private void uploadImage(Uri uri, Map<String, Object> product){
        StorageReference storageReference = storage.getReference();
        StorageReference productsReference = storageReference.child("images/"+uri.getLastPathSegment());
        UploadTask uploadTask = productsReference.putFile(uri);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(), "Image can't uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                productsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        product.put("image", imageUrl);
                        addProduct(product);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Image can't uploaded", Toast.LENGTH_SHORT).show();
                    }
                });




            }
        });
    }

    private void addProduct(Map<String, Object> product){
        db.collection("products").add(product).addOnSuccessListener(
                new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(getActivity(), "Product Added", Toast.LENGTH_SHORT).show();
                        nameField.getEditText().setText("");
                        priceField.getEditText().setText("");
                        descriptionField.getEditText().setText("");
                        categoriesMenu.setText("");
                        image.setImageResource(R.drawable.demo);
                        imageUri=null;
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
                Toast.makeText(getActivity(), "Product not Added, Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

}