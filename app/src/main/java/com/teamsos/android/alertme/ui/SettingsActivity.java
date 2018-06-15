//package com.teamsos.android.alertme.ui;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.design.widget.NavigationView;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.teamsos.android.alertme.MainActivity;
//import com.teamsos.android.alertme.R;
//import com.teamsos.android.alertme.chat.data.FriendDB;
//import com.teamsos.android.alertme.chat.data.GroupDB;
//import com.teamsos.android.alertme.chat.service.ServiceUtils;
//import com.teamsos.android.alertme.ui.help_and_support.HelpActivity;
//import com.teamsos.android.alertme.ui.map.MapsActivity;
//
//public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//    private Toolbar toolbar;
//    private DrawerLayout drawerLayout;
//    private ActionBarDrawerToggle drawerToggle;
//    private Button btnChangeEmail, btnChangePassword, btnRemoveUser,
//            changeEmail, changePassword, sendEmail, remove, inviteFriends, privacy, nofications;
//
//    private android.widget.EditText oldEmail, newEmail, password, newPassword;
//    private android.widget.ProgressBar progressBar;
//    private FirebaseAuth.AuthStateListener authListener;
//    private FirebaseAuth auth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
//        toolbar = findViewById(R.id.toolbar_settings);
//        setSupportActionBar(toolbar);
//        setTitle("Settings");
//        drawerLayout = findViewById(R.id.settings_drawer);
//        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
//        drawerLayout.addDrawerListener(drawerToggle);
//        drawerToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        NavigationView navigationView = findViewById(R.id.nav_barSettings);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        inviteFriends = findViewById(R.id.invite_friends);
//
//        inviteFriends.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent shareIntent = new Intent(Intent.ACTION_SENDTO);
//                shareIntent.setData(Uri.parse("mailto:"));
//                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "AlertMe\nAssault Prevention Application");
//                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=co.feeld&_branch_match_id=458664803132089955");
//                startActivity(Intent.createChooser(shareIntent, "Share app via..."));}
//        });
//
//        auth = FirebaseAuth.getInstance();
//
//        //get current user
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user == null) {
//                    // user auth state is changed - user is null
//                    // launch login activity
//                    startActivity(new Intent(SettingsActivity.this, com.teamsos.android.alertme.chat.ui.LoginActivity.class));
//                    finish();
//                }
//            }
//        };
//
//        btnChangeEmail =  findViewById(R.id.change_email_button);
//        btnChangePassword =  findViewById(R.id.change_password_button);
//        btnRemoveUser =  findViewById(R.id.remove_user_button);
//        changeEmail =  findViewById(R.id.changeEmail);
//        changePassword =  findViewById(R.id.changePass);
//        sendEmail =  findViewById(R.id.send);
//        remove =  findViewById(R.id.remove);
//        privacy = findViewById(R.id.privacy);
//        nofications = findViewById(R.id.notifs);
//
//        oldEmail =  findViewById(R.id.old_email);
//        newEmail =  findViewById(R.id.new_email);
//        password =  findViewById(R.id.password);
//        newPassword =  findViewById(R.id.newPassword);
//
//        oldEmail.setVisibility(View.GONE);
//        newEmail.setVisibility(View.GONE);
//        password.setVisibility(View.GONE);
//        newPassword.setVisibility(View.GONE);
//        changeEmail.setVisibility(View.GONE);
//        changePassword.setVisibility(View.GONE);
//        sendEmail.setVisibility(View.GONE);
//        remove.setVisibility(View.GONE);
//
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//
//        if (progressBar != null) {
//            progressBar.setVisibility(View.GONE);
//        }
//
//        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                oldEmail.setVisibility(View.GONE);
//                newEmail.setVisibility(View.VISIBLE);
//                password.setVisibility(View.GONE);
//                newPassword.setVisibility(View.GONE);
//                changeEmail.setVisibility(View.VISIBLE);
//                changePassword.setVisibility(View.GONE);
//                sendEmail.setVisibility(View.GONE);
//                remove.setVisibility(View.GONE);
//            }
//        });
//
//        changeEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
//                if (user != null && !newEmail.getText().toString().trim().equals("")) {
//                    user.updateEmail(newEmail.getText().toString().trim())
//                            .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        android.widget.Toast.makeText(SettingsActivity.this, "Email address is updated. Please sign in with new email id!", android.widget.Toast.LENGTH_LONG).show();
//                                        auth.signOut();
//                                        progressBar.setVisibility(View.GONE);
//                                    } else {
//                                        Toast.makeText(SettingsActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
//                                        progressBar.setVisibility(View.GONE);
//                                    }
//                                }
//                            });
//                } else if (newEmail.getText().toString().trim().equals("")) {
//                    newEmail.setError("Enter email");
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        btnChangePassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                oldEmail.setVisibility(View.GONE);
//                newEmail.setVisibility(View.GONE);
//                password.setVisibility(View.GONE);
//                newPassword.setVisibility(View.VISIBLE);
//                changeEmail.setVisibility(View.GONE);
//                changePassword.setVisibility(View.VISIBLE);
//                sendEmail.setVisibility(View.GONE);
//                remove.setVisibility(View.GONE);
//            }
//        });
//
//        changePassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
//                if (user != null && !newPassword.getText().toString().trim().equals("")) {
//                    if (newPassword.getText().toString().trim().length() < 6) {
//                        newPassword.setError("Password too short, enter minimum 6 characters");
//                        progressBar.setVisibility(View.GONE);
//                    } else {
//                        user.updatePassword(newPassword.getText().toString().trim())
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Toast.makeText(SettingsActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
//                                            auth.signOut();
//                                            progressBar.setVisibility(View.GONE);
//                                        } else {
//                                            Toast.makeText(SettingsActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
//                                            progressBar.setVisibility(View.GONE);
//                                        }
//                                    }
//                                });
//                    }
//                } else if (newPassword.getText().toString().trim().equals("")) {
//                    newPassword.setError("Enter password");
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });
//
//
//
//
//        final String message = "Are you sure you want to delete your account?";
//        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(SettingsActivity.this).setTitle("Confirm Account Termination").setMessage(message).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//
//                        progressBar.setVisibility(View.VISIBLE);
//                        if (user != null) {
//                            user.delete()
//                                    .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                android.widget.Toast.makeText(SettingsActivity.this, "Your profile is deleted:( Create a account now!", android.widget.Toast.LENGTH_SHORT).show();
//                                                startActivity(new Intent(SettingsActivity.this, com.teamsos.android.alertme.chat.ui.LoginActivity.class));
//                                                finish();
//                                                progressBar.setVisibility(View.GONE);
//                                            } else {
//                                                android.widget.Toast.makeText(SettingsActivity.this, "Failed to delete your account!", android.widget.Toast.LENGTH_SHORT).show();
//                                                progressBar.setVisibility(View.GONE);
//                                            }
//                                        }
//                                    });
//                        }
//                    }
//                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        return;
//                    }
//                }).show();
//            }
//        });
//        privacy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://policies.google.com/privacy")));
//            }
//        });
//
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (drawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(SettingsActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
//        finish();
//    }
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        NavigationView navigationView = findViewById(R.id.nav_barSettings);
//        navigationView.setNavigationItemSelectedListener(this);
//        int id = item.getItemId();
//        if (id == R.id.nav_chat) {
//            Intent chat = new Intent(SettingsActivity.this, MainActivity.class);
//            overridePendingTransition(0, 0);
//            chat.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(chat);
//
//        } else if (id == R.id.nav_map) {
//            Intent map = new Intent(SettingsActivity.this, MapsActivity.class);
//            map.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            overridePendingTransition(0, 0);
//            startActivity(map);
//        } else if (id == R.id.nav_settings) {
//            Intent settings = new Intent(SettingsActivity.this, SettingsActivity.class);
//            overridePendingTransition(0, 0);
//            settings.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(settings);
//        } else if (id == R.id.nav_help) {
//            Intent help = new Intent(SettingsActivity.this, HelpActivity.class);
//            overridePendingTransition(0, 0);
//            help.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(help);
//        } else if (id == R.id.nav_logout) {
//            FirebaseAuth.getInstance().signOut();
//            FriendDB.getInstance(this).dropDB();
//            GroupDB.getInstance(this).dropDB();
//            ServiceUtils.stopServiceFriendChat(this.getApplicationContext(), true);
//            overridePendingTransition(0, 0);
//            finish();
//        }
//
//        DrawerLayout drawerLayout = findViewById(R.id.settings_drawer);
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
//}
