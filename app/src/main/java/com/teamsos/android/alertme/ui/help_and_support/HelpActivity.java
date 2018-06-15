package com.teamsos.android.alertme.ui.help_and_support;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.teamsos.android.alertme.BuildConfig;
import com.teamsos.android.alertme.MainActivity;
import com.teamsos.android.alertme.R;
import com.teamsos.android.alertme.chat.data.FriendDB;
import com.teamsos.android.alertme.chat.data.GroupDB;
import com.teamsos.android.alertme.chat.service.ServiceUtils;
//import com.teamsos.android.alertme.ui.SettingsActivity;
import com.teamsos.android.alertme.ui.map.MapsActivity;

import java.io.File;
import java.util.ArrayList;

import static android.provider.Telephony.ThreadsColumns.ERROR;

public class HelpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView fAQButton;//1
    private TextView contactUs;
    private TextView terms_privacy;
    private TextView appInformation;

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return formatSize(availableBlocks * blockSize);
        } else {
            return ERROR;
        }
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "GB";
                }
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private String deviceInfo() {
        StringBuilder deviceName = new StringBuilder("--Support Info--\n");
        ArrayList<String> userInfo = new ArrayList();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);

            String imeiSIM1 = telephonyInfo.getImsiSIM1();
            String imeiSIM2 = telephonyInfo.getImsiSIM2();

            boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
            boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

            boolean isDualSIM = telephonyInfo.isDualSIM();
            // device Id
            String deviceId = "Device ID: " + telephonyManager.getDeviceId();
            // software version
            String softwareVersion = "Software version: " + telephonyManager.getDeviceSoftwareVersion();
            //mobile number
            String phoneNumber = null;
            String phoneNumberTwo = null;
            if (isDualSIM) {
                String name = "Debug info: ";
                if (isSIM1Ready && isSIM2Ready) {
                    phoneNumber = name + imeiSIM1;
                    phoneNumberTwo = name + imeiSIM2;
                } else if (isSIM1Ready) {
                    phoneNumber = name + imeiSIM1;
                } else if (isSIM2Ready) {
                    phoneNumber = name + imeiSIM2;
                }
            } else {
                phoneNumber = "Debug info" + telephonyManager.getLine1Number();
            }
            //Description
            String Description = "Description: " + BuildConfig.VERSION_CODE;
            //Version
            String version = "Version: " + BuildConfig.VERSION_CODE;
            //App Name
            String appName = "App: com.RescueMe";
            // serial number
            String simSerialNo = "Sim Serial Number: " + telephonyManager.getSimSerialNumber();
            // sim operator name
            String sim_operator_Name = "Carrier Provider: " + telephonyManager.getSimOperatorName();
            //Manufacturer
            String deviceManufacturer = "Manufacturer: " + android.os.Build.MANUFACTURER;
            //Model
            String deviceModel = "Model: " + android.os.Build.MODEL;
            //OS
            String os = "OS: " + Build.VERSION.RELEASE;
            //External Memory available
            String availableExternalSpace = "Free Space Built in : " + getAvailableExternalMemorySize();
            //Internal memory available
            String availableInternalSpace = "Free Space Removable: " + getAvailableInternalMemorySize();
            userInfo.add(appName);
            userInfo.add(phoneNumber);
            userInfo.add(phoneNumberTwo);
            userInfo.add(Description);
            userInfo.add(version);
            userInfo.add(deviceManufacturer);
            userInfo.add(deviceModel);
            userInfo.add(os);
            userInfo.add(availableInternalSpace);
            userInfo.add(availableExternalSpace);
            userInfo.add(deviceId);
            userInfo.add(softwareVersion);
            userInfo.add(sim_operator_Name);
            userInfo.add(simSerialNo);

            for (String x : userInfo) {
                deviceName.append(x + "\n");
            }
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                Toast.makeText(this, "Device information is necessary to send feedback", Toast.LENGTH_SHORT).show();
            }
        }
        return new String(deviceName);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Help and Support");
        drawerLayout = findViewById(R.id.helpAndSupport);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.nav_barHelpAndSupport);
        navigationView.setNavigationItemSelectedListener(this);
        fAQButton = findViewById(R.id.faqText);//2
        fAQButton.setOnClickListener(new View.OnClickListener() {//3
            @Override
            public void onClick(View v) {
                Intent termsAndPrivacy = new Intent("android.intent.action.VIEW",
                        Uri.parse("https://policies.google.com/faq"));
                startActivity(termsAndPrivacy);
            }
        });
        contactUs = findViewById(R.id.contact_us);
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"custserv.teamsos@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback/Questions about RescueMe");
                intent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n" + deviceInfo());
                startActivity(Intent.createChooser(intent, "Contact support via..."));

            }
        });
        terms_privacy = findViewById(R.id.terms_privacy);
        terms_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent termsAndPrivacy = new Intent("android.intent.action.VIEW",
                        Uri.parse("https://policies.google.com/"));
                startActivity(termsAndPrivacy);
            }
        });

        appInformation = findViewById(R.id.appInformation);
        appInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( HelpActivity.this, AppInformation.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HelpActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationView navigationView = findViewById(R.id.nav_barHelpAndSupport);
        navigationView.setNavigationItemSelectedListener(this);
        int id = item.getItemId();
        if (id == R.id.nav_chat) {
            Intent chat = new Intent(HelpActivity.this, MainActivity.class);
            overridePendingTransition(0, 0);
            chat.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(chat);
        } else if (id == R.id.nav_map) {
            Intent map = new Intent(HelpActivity.this, MapsActivity.class);
            overridePendingTransition(0, 0);
            map.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(map);
        } else if (id == R.id.nav_settings) {
//            Intent settings = new Intent(HelpActivity.this, SettingsActivity.class);
            overridePendingTransition(0, 0);
//            settings.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(settings);
        } else if (id == R.id.nav_help) {
            Intent help = new Intent(HelpActivity.this, HelpActivity.class);
            overridePendingTransition(0, 0);
            help.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(help);
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            FriendDB.getInstance(this).dropDB();
            GroupDB.getInstance(this).dropDB();
            ServiceUtils.stopServiceFriendChat(this.getApplicationContext(), true);
            finish();
            overridePendingTransition(0, 0);
        }

        DrawerLayout drawerLayout = findViewById(R.id.helpAndSupport);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
