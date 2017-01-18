package com.codeflight.ritik.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Handler;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.prefill.PreFillType;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private TextView tvUserName;
    private TextView tvEmail;
    private ImageView ivUserPhoto;
    private ImageView ivNavHeaderBg;
    private String mUsername;
    private String mEmail;
    private String mPhotoUrl;
    private String mUId;
    private View mNavHeader;
    private ProgressDialog mProgressDialog;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Handler mHandler;
    private FloatingActionButton fab;

    private static final String TAG_HOME = "Home";
    private static final String TAG_PROFILE = "Profile";
    private static final String TAG_CONTACT_US = "Contact Us";
    private static String CURRENT_TAG = TAG_HOME;
    private static int navItemIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mNavHeader = navigationView.getHeaderView(0);

        tvUserName = (TextView) mNavHeader.findViewById(R.id.username);
        tvEmail = (TextView) mNavHeader.findViewById(R.id.email);
        ivUserPhoto = (ImageView) mNavHeader.findViewById(R.id.userphoto);
        ivNavHeaderBg = (ImageView) mNavHeader.findViewById(R.id.img_header_bg);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser != null)
        {
            mUsername = mFirebaseUser.getDisplayName();
            mEmail = mFirebaseUser.getEmail();
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            mUId = mFirebaseUser.getUid();

            loadNavHeader();
        }
        else
        {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mProgressDialog = new ProgressDialog(this);
        if(savedInstanceState==null)
        {
            navItemIndex=0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    private void loadNavHeader()
    {
        tvUserName.setText(mUsername);
        tvEmail.setText(mEmail);

        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivNavHeaderBg);

        Glide.with(this).load(mPhotoUrl)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivUserPhoto);
    }

    private void loadHomeFragment()
    {
        selectNavMenu();

        setToolBarTitle();

        if(getSupportFragmentManager().findFragmentByTag(CURRENT_TAG)!=null)
        {
            drawer.closeDrawers();
            toggleFab();
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if(mPendingRunnable!=null)
        {
            mHandler.post(mPendingRunnable);
        }

        drawer.closeDrawers();
        toggleFab();
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment()
    {
        switch (navItemIndex)
        {
            case 0:
                return new HomeFragment();
            case 1:
                return new ProfileFragment();
            default:
                return new HomeFragment();
        }
    }

    private void selectNavMenu()
    {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setToolBarTitle()
    {
        getSupportActionBar().setTitle(CURRENT_TAG);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.nav_home:
            {
                navItemIndex=0;
                CURRENT_TAG = TAG_HOME;
                break;
            }
            case R.id.nav_profile:
            {
                navItemIndex=1;
                CURRENT_TAG = TAG_PROFILE;
                break;
            }
            case R.id.nav_sign_out:
            {
                showDialog();
                mFirebaseAuth.signOut();

                Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                hideDialog();
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                finish();
            }
        }
        if(item.isChecked())
            item.setChecked(false);
        else
            item.setChecked(true);

        item.setChecked(true);

        loadHomeFragment();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed: "+connectionResult);
        showToast("Google Play Services error");
    }

    public void showToast(String s)
    {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    private void showDialog()
    {
        mProgressDialog.setMessage("Logging out...");
        mProgressDialog.show();
    }

    private void hideDialog()
    {
        mProgressDialog.cancel();
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }
}
