package com.teamsos.android.alertme.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamsos.android.alertme.MainActivity;
import com.teamsos.android.alertme.R;
import com.teamsos.android.alertme.chat.data.FriendDB;
import com.teamsos.android.alertme.chat.data.GroupDB;
import com.teamsos.android.alertme.chat.service.ServiceUtils;
//import com.teamsos.android.alertme.ui.SettingsActivity;
import com.teamsos.android.alertme.ui.help_and_support.HelpActivity;

import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private GoogleMap mMap;
    Marker marker;

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("location");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setTitle("View Friends");
        drawerLayout = findViewById(R.id.maps);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.nav_barMap);
        navigationView.setNavigationItemSelectedListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Location loc= new Location();
                if(dataSnapshot.getValue()!=null){
                    loc.latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    loc.longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    LatLng device = new LatLng(loc.latitude,loc.longitude);
                    mMap.clear();
                    marker = mMap.addMarker(new MarkerOptions().position(device).title("Device Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.sos_icon)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(device));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        updateMap();
    }

    public void updateMap(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Location loc= new Location();
                if(dataSnapshot.getValue()!=null){
                    loc.latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    loc.longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    LatLng device = new LatLng(loc.latitude,loc.longitude);
                    marker.setPosition(device);
                    updateMap();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(MapsActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationView navigationView = findViewById(R.id.nav_barMap);
        navigationView.setNavigationItemSelectedListener(this);
        int id = item.getItemId();
        if (id == R.id.nav_chat) {
            Intent chat = new Intent(MapsActivity.this, MainActivity.class);
            overridePendingTransition(0, 0);
            chat.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(chat);
        } else if (id == R.id.nav_map) {
            Intent map = new Intent(MapsActivity.this, MapsActivity.class);
            overridePendingTransition(0, 0);
            map.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(map);
        } else if (id == R.id.nav_settings) {
//            Intent settings = new Intent(MapsActivity.this, SettingsActivity.class);
//            overridePendingTransition(0, 0);
//            settings.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(settings);
        } else if (id == R.id.nav_help) {
            Intent help = new Intent(MapsActivity.this, HelpActivity.class);
            overridePendingTransition(0, 0);
            help.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(help);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            FriendDB.getInstance(this).dropDB();
            GroupDB.getInstance(this).dropDB();
            ServiceUtils.stopServiceFriendChat(this.getApplicationContext(), true);
            overridePendingTransition(0, 0);
            finish();
        }

        DrawerLayout drawerLayout = findViewById(R.id.maps);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
