package in.hypernation.storyscrolls.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import in.hypernation.storyscrolls.R;
import in.hypernation.storyscrolls.auth.LoginActivity;
import in.hypernation.storyscrolls.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private Button logoutBtn, editBtn, saveBtn;
    private TextInputLayout nameField, emailField, numberField, addressField;
    private boolean isEdit = false;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private User user;
    private final String TAG = "ProfileFragment";
    private SharedPreferences preferences;


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logoutBtn = view.findViewById(R.id.logout_btn);
        editBtn = view.findViewById(R.id.edit_btn);
        saveBtn = view.findViewById(R.id.save_btn);
        emailField = view.findViewById(R.id.emailField);
        addressField = view.findViewById(R.id.addressField);
        nameField = view.findViewById(R.id.nameField);
        numberField = view.findViewById(R.id.numberField);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        preferences = requireContext().getSharedPreferences(getResources().getString(R.string.sp_file), Context.MODE_PRIVATE);


        showBtn();
        getUser();

        editBtn.setOnClickListener( v-> {
            isEdit = true;
            showBtn();
            editFields();
        });

        logoutBtn.setOnClickListener( v-> {
            auth.signOut();
            Intent i = new Intent(getActivity(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            requireActivity().finish();
        });

        saveBtn.setOnClickListener( v -> {
            user.setAddress(addressField.getEditText().getText().toString());
            user.setEmail(emailField.getEditText().getText().toString());
            user.setName(nameField.getEditText().getText().toString());
            user.setNumber(numberField.getEditText().getText().toString());
            user.setUid(auth.getCurrentUser().getUid());

            updateUser(user);
        });



        return view;
    }

    private void showBtn(){
        if (isEdit) {
            saveBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.GONE);
        } else {
            saveBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.VISIBLE);
        }
    }

    private void editFields(){
        addressField.getEditText().setEnabled(isEdit);
        nameField.getEditText().setEnabled(isEdit);
        numberField.getEditText().setEnabled(isEdit);
        if(isEdit) nameField.requestFocus();
    }

    private void setUserData(User user){
        nameField.getEditText().setText(user.getName());
        emailField.getEditText().setText(user.getEmail());
        numberField.getEditText().setText(user.getNumber());
        addressField.getEditText().setText(user.getAddress());
    }

    private void updatePreferenceData(User user){
        preferences.edit().putString("email", user.getEmail()).apply();
        preferences.edit().putString("name", user.getName()).apply();
        preferences.edit().putString("address", user.getAddress()).apply();
        preferences.edit().putString("number", user.getNumber()).apply();
    }

    private  void getUser() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser!=null){
            db.collection("users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().exists()){
                            DocumentSnapshot doc = task.getResult();
                            user = new User(
                                    doc.getId(),
                                    doc.getString("name"),
                                    doc.getString("number"),
                                    doc.getString("email"),
                                    doc.getString("address")
                            );
                            setUserData(user);
                        }else {
                            Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private  void updateUser(User user) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser!=null){
            db.collection("users").document(firebaseUser.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "DocumentSnapshot successfully update!");
                    setUserData(user);
                    updatePreferenceData(user);
                    Toast.makeText(getActivity(), "Profile Saved", Toast.LENGTH_SHORT).show();
                    isEdit = false;
                    showBtn();
                    editFields();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error update user document", e);
                }
            });
        }
    }

}