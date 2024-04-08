package com.hn.farm.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hn.farm.ProductDetailActivity;
import com.hn.farm.R;
import com.hn.farm.models.Product;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    private Product product;
    private TextInputLayout categoriesField, nameField, descriptionField, priceField;
    private MaterialAutoCompleteTextView categoriesMenu;
    private TextView addedData;
    private ImageView image;

    private Map<String, Boolean> uploadStatus;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Button saveBtn, deleteBtn;
    private MaterialToolbar toolbar;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    image.setImageURI(uri);
                    uploadImage(uri);
                    //uploadImage(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        categoriesMenu = findViewById(R.id.categories_menu);
        nameField = findViewById(R.id.nameField);
        descriptionField = findViewById(R.id.descriptionField);
        priceField = findViewById(R.id.priceField);
        categoriesField = findViewById(R.id.categoriesField);
        addedData = findViewById(R.id.added_date);
        image = findViewById(R.id.image);
        saveBtn = findViewById(R.id.save_btn);
        deleteBtn = findViewById(R.id.delete_btn);
        toolbar = findViewById(R.id.topAppBar);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        if(getIntent().hasExtra("product_detail")) {
            product = getIntent().getParcelableExtra("product_detail");
        }

        String[] items = new String[]{"Fertilizer", "Pesticides", "Herbicides", "Fungicide", "Insecticide", "Rodenticides"};
        categoriesMenu.setSimpleItems(items);


        updateData();
        Date date = product.getAddDateTime().toDate();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateString = sdf.format(date);
        String addedDateTxt = "Added Date & Time : "+dateString;
        addedData.setText(addedDateTxt);


        image.setOnClickListener( v-> {
            mGetContent.launch("image/*");
        });

        categoriesMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriesField.setHelperText(items[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveBtn.setOnClickListener( v -> {
            String nameInput = nameField.getEditText().getText().toString();
            String descriptionInput = descriptionField.getEditText().getText().toString();
            String priceInput = priceField.getEditText().getText().toString();
            String categoryInput = categoriesMenu.getText().toString();
            if(!nameInput.isEmpty()) product.setName(nameInput); else nameField.setError("Name can't be blank");
            if(!descriptionInput.isEmpty()) product.setDescription(descriptionInput); else descriptionField.setError("Name can't be blank");
            if(!priceInput.isEmpty()) product.setPrice(Long.valueOf(priceInput)); else priceField.setError("Name can't be blank");
            if(!categoryInput.isEmpty()) product.setCategory(categoryInput); else product.setCategory(categoriesField.getHelperText().toString());

            if(uploadStatus!=null){
                if(Boolean.TRUE.equals(uploadStatus.get("onProgress"))){
                    Toast.makeText(this, "Please wait! Image is uploading", Toast.LENGTH_SHORT).show();
                } else {
                    if(Boolean.TRUE.equals(uploadStatus.get("isUploaded"))){
                        updateProduct();
                    } else {
                        Toast.makeText(this, "Something Went Wrong! Image not uploaded", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                updateProduct();
            }

        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProductActivity.super.getOnBackPressedDispatcher().onBackPressed();
            }
        });

        deleteBtn.setOnClickListener( v-> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Confirmation")
                    .setMessage("Please confirm that you are trying to delete this product.")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteProduct();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        });


    }

    private void deleteProduct() {
        db.collection("products").document(product.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(EditProductActivity.this, "Product deleted", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(EditProductActivity.this, AdminMainActivity.class);
                startActivity(i);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProductActivity.this, "Product can't deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProduct() {
        db.collection("products").document(product.getId()).set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(EditProductActivity.this, "Product Saved", Toast.LENGTH_SHORT).show();
                updateData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProductActivity.this, "Product not Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateData() {
        assert product!=null;
        nameField.getEditText().setText(product.getName());
        descriptionField.getEditText().setText(product.getDescription());
        String priceInt = String.valueOf(product.getPrice());
        priceField.getEditText().setText(priceInt);
        categoriesField.setHelperTextEnabled(true);
        categoriesField.setHelperText(product.getCategory());
        Glide.with(this).load(product.getImage()).into(image);
    }

    private void uploadImage(Uri uri){
        uploadStatus = new HashMap<>();
        uploadStatus.put("onProgress", true);
        uploadStatus.put("isUploaded", false);
        StorageReference storageReference = storage.getReference();
        StorageReference productsReference = storageReference.child("images/"+uri.getLastPathSegment());
        UploadTask uploadTask = productsReference.putFile(uri);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(EditProductActivity.this, "Image can't uploaded", Toast.LENGTH_SHORT).show();
                uploadStatus.put("isUploaded", false);
                uploadStatus.put("onProgress", false);
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
                        product.setImage(imageUrl);
                        uploadStatus.put("isUploaded", true);
                        uploadStatus.put("onProgress", false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProductActivity.this, "Image can't uploaded", Toast.LENGTH_SHORT).show();
                        uploadStatus.put("isUploaded", false);
                        uploadStatus.put("onProgress", false);
                    }
                });
            }
        });
    }

}