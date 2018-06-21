package com.teamsos.android.alertme.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.teamsos.android.alertme.R;
import com.teamsos.android.alertme.chat.data.FriendDB;
import com.teamsos.android.alertme.chat.data.GroupDB;
import com.teamsos.android.alertme.chat.data.SharedPreferenceHelper;
import com.teamsos.android.alertme.chat.data.StaticConfig;
import com.teamsos.android.alertme.chat.model.Configuration;
import com.teamsos.android.alertme.chat.model.User;
import com.teamsos.android.alertme.chat.service.ServiceUtils;
import com.teamsos.android.alertme.chat.util.ImageUtils;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity{
    TextView tvUserName;
    ImageView avatar;

    private List<Configuration> listConfig = new ArrayList<>();
    private RecyclerView recyclerView;
    private SettingsActivity.UserInfoAdapter infoAdapter;

    private static final String USERNAME_LABEL = "Username";
    private static final String EMAIL_LABEL = "Email";
    private static final String SIGNOUT_LABEL = "Sign out";
    private static final String RESETPASS_LABEL = "Change Password";

    private static final int PICK_IMAGE = 1994;
    private LovelyProgressDialog waitingDialog;

    private DatabaseReference userDB;
    private FirebaseAuth mAuth;
    private User myAccount;
    private Context context;

    public SettingsActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userDB = FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID);
        userDB.addListenerForSingleValueEvent(userListener);
        mAuth = FirebaseAuth.getInstance();

        avatar = (ImageView) findViewById(R.id.img_avatar);
        avatar.setOnClickListener(onAvatarClick);
        tvUserName = (TextView)findViewById(R.id.tv_username);

        SharedPreferenceHelper prefHelper = SharedPreferenceHelper.getInstance(this);
        myAccount = prefHelper.getUserInfo();
        setupArrayListInfo(myAccount);
        setImageAvatar(this, myAccount.avata);
        tvUserName.setText(myAccount.name);

        recyclerView = (RecyclerView)findViewById(R.id.info_recycler_view);
        infoAdapter = new UserInfoAdapter(listConfig);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(infoAdapter);

        waitingDialog = new LovelyProgressDialog(this);
    }

    private ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
        public void onCancelled(@NonNull DatabaseError databaseError) {
            //An error occured : not getting data
            Log.e(SettingsActivity.class.getName(), "loadPost:onCancelled", databaseError.toException());
        }
    };

    /**
     * When you click on the avatar it opens the default viewer to select an image
     */
    private View.OnClickListener onAvatarClick = new View.OnClickListener(){
        @Override
        public void onClick(View view){

            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Avatar")
                    .setMessage("Are you sure you want to change your avatar profile?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_PICK);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            if(data == null){
                Toast.makeText(this, "An error occured, please try again", Toast.LENGTH_LONG).show();
                return;
            }
            try{
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());

                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                imgBitmap = ImageUtils.cropToSquare(imgBitmap);
                InputStream is = ImageUtils.convertBitmapToInputStream(imgBitmap);
                final Bitmap liteImage = ImageUtils.makeImageLite(is,
                        imgBitmap.getWidth(), imgBitmap.getHeight(),
                        ImageUtils.AVATAR_WIDTH, ImageUtils.AVATAR_HEIGHT);

                String imageBase64 = ImageUtils.encodeBase64(liteImage);
                myAccount.avata = imageBase64;

                waitingDialog.setCancelable(false)
                        .setTitle("Avatar updating...")
                        .setTopColorRes(R.color.colorPrimary)
                        .show();

                userDB.child("avata").setValue(imageBase64)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    waitingDialog.dismiss();
                                    SharedPreferenceHelper preferenceHelper = SharedPreferenceHelper
                                            .getInstance(SettingsActivity.this);
                                    preferenceHelper.saveUserInfo(myAccount);
                                    avatar.setImageDrawable(ImageUtils
                                            .roundedImage(SettingsActivity.this, liteImage));

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
                                Log.d("Update avatar", "failed");
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

            avatar.setImageDrawable(ImageUtils.roundedImage(context, src));
        }catch (Exception e){
        }
    }

    @Override
    public void onDestroy (){
        super.onDestroy();
    }

    public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {
        private List<Configuration> profileConfig;

        public class ViewHolder extends RecyclerView.ViewHolder {


            public ViewHolder(View view) {
                super(view);
            }
        }

        public UserInfoAdapter(List<Configuration> profileConfig) {
            this.profileConfig = profileConfig;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_info_item_layout, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 3;
        }

    }
}
