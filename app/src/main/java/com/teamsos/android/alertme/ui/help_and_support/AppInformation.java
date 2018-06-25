package com.teamsos.android.alertme.ui.help_and_support;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.teamsos.android.alertme.BuildConfig;
import com.teamsos.android.alertme.R;


public class AppInformation extends AppCompatActivity {
    Toolbar toolbar;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_information);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("FAQ");
        textView = findViewById(R.id.appInfo);
        textView.setText(String.valueOf("Version: " + BuildConfig.VERSION_NAME));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
