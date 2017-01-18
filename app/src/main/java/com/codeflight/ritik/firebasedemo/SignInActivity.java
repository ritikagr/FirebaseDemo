package com.codeflight.ritik.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ritik on 1/18/2017.
 */

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 223;
    private static final String TAG = "SignInActivity";
    private SignInButton mSignInButton;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mProgressDialog = new ProgressDialog(this);
        mSignInButton = (SignInButton) findViewById(R.id.google_sign_in);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==RC_SIGN_IN)
        {
            showDialog();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess())
            {
                //Google signin was successful,authenticate with firebase
                GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
                firebaseAuthWithGoogle(googleSignInAccount);
            }
            else
            {
                hideDialog();
                Log.d(TAG, "Google Sign In Failed!");
                showToast("Google Sign In Failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account)
    {
        Log.d(TAG, "firebaseAuthWithGoogle: "+ account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signinWithCredential: On Complete:" + task.isSuccessful());

                        hideDialog();
                        if(!task.isSuccessful())
                        {
                            Log.d(TAG, "signinWithCredential" + task.getException());
                            showToast("Authentication Failed.");
                        }
                        else
                        {
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            mDatabaseReference.child("users").child(mFirebaseUser.getUid()).child("userName").setValue(mFirebaseUser.getDisplayName());
                            mDatabaseReference.child("users").child(mFirebaseUser.getUid()).child("email").setValue(mFirebaseUser.getEmail());
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed: " + connectionResult);
        showToast("Google Play Services error.");
    }

    public void showToast(String s)
    {
        Toast.makeText(SignInActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    private void showDialog()
    {
        mProgressDialog.setMessage("Logging in...");
        mProgressDialog.show();
    }

    private void hideDialog()
    {
        mProgressDialog.cancel();
    }
}