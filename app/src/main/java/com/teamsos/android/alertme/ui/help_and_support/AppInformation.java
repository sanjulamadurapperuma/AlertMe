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
