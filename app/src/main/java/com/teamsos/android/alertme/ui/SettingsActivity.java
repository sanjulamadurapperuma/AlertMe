/*
 *  Copyright (c) Sanjula Madurapperuma and Team SOS. All Rights Reserved.
 *
 *  Sanjula Madurapperuma and Team SOS licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.teamsos.android.alertme.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.teamsos.android.alertme.chat.data.FriendDB;
import com.teamsos.android.alertme.chat.data.GroupDB;
import com.teamsos.android.alertme.chat.data.SharedPreferenceHelper;
import com.teamsos.android.alertme.chat.data.StaticConfig;
import com.teamsos.android.alertme.chat.model.Configuration;
import com.teamsos.android.alertme.chat.model.User;
import com.teamsos.android.alertme.chat.service.ServiceUtils;
import com.teamsos.android.alertme.chat.util.ImageUtils;
import com.teamsos.android.alertme.ui.help_and_support.HelpActivity;
import com.teamsos.android.alertme.ui.map.MapsActivity;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView tvUserName;
    ImageView avatar;

    private List<Configuration> listConfig = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserInfoAdapter infoAdapter;

    private static final String USERNAME_LABEL = "Username";
    private static final String EMAIL_LABEL = "Email";
    private static final String SIGNOUT_LABEL = "Sign out";
    private static final String RESETPASS_LABEL = "Change Password";

    private static final int PICK_IMAGE = 1994;
    private LovelyProgressDialog waitingDialog;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private DatabaseReference userDB;
    private FirebaseAuth mAuth;
    private User myAccount;

    public SettingsActivity() {
        // Required empty public constructor
    }

    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //Get the user's information and update to the interface
            listConfig.clear();
            myAccount = dataSnapshot.getValue(User.class);

            setupArrayListInfo(myAccount);
            if(infoAdapter != null){
                infoAdapter.notifyDataSetChanged();
            }

            if(tvUserName != null){
                tvUserName.setText(myAccount.name);
            }

            setImageAvatar(SettingsActivity.this, myAccount.avata);
            SharedPreferenceHelper preferenceHelper = SharedPreferenceHelper.getInstance(SettingsActivity.this);
            preferenceHelper.saveUserInfo(myAccount);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //An error occured : not getting data
            Log.e(SettingsActivity.class.getName(), "loadPost:onCancelled", databaseError.toException());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userDB = FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID);
        userDB.addListenerForSingleValueEvent(userListener);
        mAuth = FirebaseAuth.getInstance();
        setTitle("Settings");
        drawerLayout = findViewById(R.id.settings_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_barSettings);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View header=navigationView.getHeaderView(0);
        TextView navBarTitle = header.findViewById(R.id.Name);
        navBarTitle.setVisibility(View.VISIBLE);
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
                                spinner.setAdapter(new ArrayAdapter<String>(SettingsActivity.this,android.R.layout.simple_spinner_dropdown_item,items ));
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        Toast.makeText(SettingsActivity.this,items[position],Toast.LENGTH_SHORT).show();

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
        avatar = (ImageView) findViewById(R.id.img_avatar);
        avatar.setOnClickListener(onAvatarClick);
        tvUserName = (TextView) findViewById(R.id.tv_username);

        SharedPreferenceHelper prefHelper = SharedPreferenceHelper.getInstance(SettingsActivity.this);
        myAccount = prefHelper.getUserInfo();
        setupArrayListInfo(myAccount);
        setImageAvatar(SettingsActivity.this, myAccount.avata);
        tvUserName.setText(myAccount.name);

        recyclerView = (RecyclerView) findViewById(R.id.info_recycler_view);
        infoAdapter = new UserInfoAdapter(listConfig);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(infoAdapter);

        waitingDialog = new LovelyProgressDialog(SettingsActivity.this);
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        finish();
    }
    /**
     * When you click on the avatar it opens the default viewer to select an image
     */
    private View.OnClickListener onAvatarClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Avatar")
                    .setMessage("Are you sure want to change avatar profile?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_PICK);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "An error occured, please try again", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));

                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                imgBitmap = ImageUtils.cropToSquare(imgBitmap);
                InputStream is = ImageUtils.convertBitmapToInputStream(imgBitmap);
                final Bitmap liteImage = ImageUtils.makeImageLite(is,
                        imgBitmap.getWidth(), imgBitmap.getHeight(),
                        ImageUtils.AVATAR_WIDTH, ImageUtils.AVATAR_HEIGHT);

                String imageBase64 = ImageUtils.encodeBase64(liteImage);
                myAccount.avata = imageBase64;

                waitingDialog.setCancelable(false)
                        .setTitle("Avatar updating....")
                        .setTopColorRes(R.color.colorPrimary)
                        .show();

                userDB.child("avata").setValue(imageBase64)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    waitingDialog.dismiss();
                                    SharedPreferenceHelper preferenceHelper = SharedPreferenceHelper.getInstance(SettingsActivity.this);
                                    preferenceHelper.saveUserInfo(myAccount);
                                    avatar.setImageDrawable(ImageUtils.roundedImage(SettingsActivity.this, liteImage));

                                    new LovelyInfoDialog(SettingsActivity.this)
                                            .setTopColorRes(R.color.colorPrimary)
                                            .setTitle("Success")
                                            .setMessage("Updated avatar successfully!")
                                            .show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDialog.dismiss();
                                Log.d("Update Avatar", "failed");
                                new LovelyInfoDialog(SettingsActivity.this)
                                        .setTopColorRes(R.color.colorAccent)
                                        .setTitle("False")
                                        .setMessage("False to update avatar")
                                        .show();
                            }
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    /**
     * Delete the old list and update the new list data
     * @param myAccount
     */
    public void setupArrayListInfo(User myAccount){
        listConfig.clear();
        Configuration userNameConfig = new Configuration(USERNAME_LABEL, myAccount.name, R.mipmap.ic_account_box);
        listConfig.add(userNameConfig);

        Configuration emailConfig = new Configuration(EMAIL_LABEL, myAccount.email, R.mipmap.ic_email);
        listConfig.add(emailConfig);

        Configuration resetPass = new Configuration(RESETPASS_LABEL, "", R.mipmap.ic_restore);
        listConfig.add(resetPass);

        Configuration signout = new Configuration(SIGNOUT_LABEL, "", R.mipmap.ic_power_settings);
        listConfig.add(signout);
    }

    private void setImageAvatar(Context context, String imgBase64){
        try {
            Resources res = getResources();
            //If you do not have the avatar leave the default image
            Bitmap src;
            if (imgBase64.equals("default")) {
                src = BitmapFactory.decodeResource(res, R.drawable.default_avata);
            } else {
                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }

            avatar.setImageDrawable(ImageUtils.roundedImage(SettingsActivity.this, src));
        }catch (Exception e){
        }
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
    public void onDestroy (){
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationView navigationView = findViewById(R.id.nav_barSettings);
        navigationView.setNavigationItemSelectedListener(this);
        int id = item.getItemId();
        if (id == R.id.nav_chat) {
            Intent chat = new Intent(SettingsActivity.this, MainActivity.class);
            overridePendingTransition(0, 0);
            chat.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(chat);

        } else if (id == R.id.nav_map) {
            Intent map = new Intent(SettingsActivity.this, MapsActivity.class);
            map.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            overridePendingTransition(0, 0);
            startActivity(map);
        } else if (id == R.id.nav_settings) {
            Intent settings = new Intent(SettingsActivity.this, SettingsActivity.class);
            overridePendingTransition(0, 0);
            settings.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(settings);
        } else if (id == R.id.nav_help) {
            Intent help = new Intent(SettingsActivity.this, HelpActivity.class);
            overridePendingTransition(0, 0);
            help.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(help);
        } else if (id == R.id.nav_logout) {
            try {
                mAuth.signOut();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DrawerLayout drawerLayout = findViewById(R.id.settings_drawer);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder>  {
        private List<Configuration> profileConfig;

        public UserInfoAdapter(List<Configuration> profileConfig){
            this.profileConfig = profileConfig;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_info_item_layout, parent, false);
            return new ViewHolder(itemView);
        }



        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Configuration config = profileConfig.get(position);
            holder.label.setText(config.getLabel());
            holder.value.setText(config.getValue());
            holder.icon.setImageResource(config.getIcon());
            ((RelativeLayout)holder.label.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(config.getLabel().equals(SIGNOUT_LABEL)){
                        FirebaseAuth.getInstance().signOut();
                        FriendDB.getInstance(getApplicationContext()).dropDB();
                        GroupDB.getInstance(getApplicationContext()).dropDB();
                        ServiceUtils.stopServiceFriendChat(getApplicationContext().getApplicationContext(), true);
                        finish();
                    }

                    if(config.getLabel().equals(USERNAME_LABEL)){
                        View vewInflater = LayoutInflater.from(SettingsActivity.this)
                                .inflate(R.layout.dialog_edit_username, null);
                        final EditText input = (EditText)vewInflater.findViewById(R.id.edit_username);
                        input.setText(myAccount.name);
                        /*Displaying a dialog with editText allows the user to enter a new username*/
                        new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle("Edit username")
                                .setView(vewInflater)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String newName = input.getText().toString();
                                        if(!myAccount.name.equals(newName)){
                                            changeUserName(newName);
                                        }
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }

                    if(config.getLabel().equals(RESETPASS_LABEL)){
                        new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle("Password")
                                .setMessage("Are you sure want to reset password?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        resetPassword(myAccount.email);
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }
                }
            });
        }

        /**
         * Update the new username to SharedPreference and change the interface
         */
        private void changeUserName(String newName){
            userDB.child("name").setValue(newName);


            myAccount.name = newName;
            SharedPreferenceHelper prefHelper = SharedPreferenceHelper.getInstance(SettingsActivity.this);
            prefHelper.saveUserInfo(myAccount);

            tvUserName.setText(newName);
            setupArrayListInfo(myAccount);
        }


        void resetPassword(final String email) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new LovelyInfoDialog(SettingsActivity.this) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.drawable.ic_pass_reset)
                                    .setTitle("Password Recovery")
                                    .setMessage("Sent email to " + email)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new LovelyInfoDialog(SettingsActivity.this) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorAccent)
                                    .setIcon(R.drawable.ic_pass_reset)
                                    .setTitle("False")
                                    .setMessage("False to sent email to " + email)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return profileConfig.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView label, value;
            public ImageView icon;
            public ViewHolder(View view) {
                super(view);
                label = (TextView)view.findViewById(R.id.tv_title);
                value = (TextView)view.findViewById(R.id.tv_detail);
                icon = (ImageView)view.findViewById(R.id.img_icon);
            }
        }

    }

}