package com.teamsos.android.alertme.ui.map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamsos.android.alertme.Account_Switch.Callback;
import com.teamsos.android.alertme.Account_Switch.CheckUser;
import com.teamsos.android.alertme.BuildConfig;
import com.teamsos.android.alertme.MainActivity;
import com.teamsos.android.alertme.R;
//import com.teamsos.android.alertme.ui.SettingsActivity;
import com.teamsos.android.alertme.ui.SettingsActivity;
import com.teamsos.android.alertme.ui.help_and_support.HelpActivity;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("View Friends");
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.maps);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_barMap);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View header=navigationView.getHeaderView(0);
        final Spinner spinner = header.findViewById(R.id.Type);
        spinner.setVisibility(View.GONE);
        new CheckUser().isUser(new Callback() {//To check if the user is a device owner
            @Override
            public void onCallback(boolean value) {
                if (value){
                    new CheckUser().isFriend(new Callback() {
                        @Override
                        public void onCallback(boolean value) {
                            if (value){//To check if the user is a friend
                                final String[] items = new String[2];
                                items[0]="Owner";
                                items[1]="Friend";
                                spinner.setVisibility(View.VISIBLE);
                                spinner.setAdapter(new ArrayAdapter<String>(MapsActivity.this,android.R.layout.simple_spinner_dropdown_item,items ));
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        Toast.makeText(MapsActivity.this,items[position],Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0,     spanString.length(), 0);
            item.setTitle(spanString);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //This is for the About menu item in the top-right hand corner
        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            Toast.makeText(this, "AlertMe version "+ BuildConfig.VERSION_NAME, Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            Intent settings = new Intent(MapsActivity.this, SettingsActivity.class);
            overridePendingTransition(0, 0);
            settings.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(settings);
        } else if (id == R.id.nav_help) {
            Intent help = new Intent(MapsActivity.this, HelpActivity.class);
            overridePendingTransition(0, 0);
            help.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(help);
        } else if (id == R.id.nav_logout) {
            try {
                new MainActivity().mAuth.signOut();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        DrawerLayout drawerLayout = findViewById(R.id.maps);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}