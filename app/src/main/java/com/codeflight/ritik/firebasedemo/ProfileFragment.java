package com.codeflight.ritik.firebasedemo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;
import android.util.ArrayMap;
import android.widget.Button;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.*;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private EditText etUsername;
    private EditText etEmail;
    private EditText etMobile;
    private EditText etDob;
    private String mUId;
    private Button mSaveBt;
    private ProgressDialog mProgressDialog;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUId = mFirebaseUser.getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("users").child(mUId);
        mDatabaseReference.keepSynced(true);

        mProgressDialog = new ProgressDialog(getContext());

        etUsername = (EditText) root.findViewById(R.id.username_profile);
        etEmail = (EditText) root.findViewById(R.id.email_profile);
        etMobile = (EditText) root.findViewById(R.id.mobile_profile);
        etDob = (EditText) root.findViewById(R.id.dob_profile);
        mSaveBt = (Button) root.findViewById(R.id.save_profile);

        ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                etUsername.setText(user.getUserName());
                etEmail.setText(user.getEmail());
                etMobile.setText(user.getMobile());
                etDob.setText(user.getDob());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabaseReference.addValueEventListener(userDataListener);

        mSaveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFirebaseUser!=null)
                {
                    showDialog();
                    User user = new User(mFirebaseUser.getDisplayName(), mFirebaseUser.getEmail(),
                            etMobile.getText().toString(),etDob.getText().toString());
                    mDatabaseReference.setValue(user);
                    hideDialog();
                    showToast("Data Updated Successfully");
                }
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void showDialog()
    {
        mProgressDialog.setMessage("Updating Data...");
        mProgressDialog.show();
    }

    private void hideDialog()
    {
        mProgressDialog.cancel();
    }

    public void showToast(String s)
    {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

}
