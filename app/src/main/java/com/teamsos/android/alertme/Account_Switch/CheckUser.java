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

package com.teamsos.android.alertme.Account_Switch;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckUser {
    public void isUser(@NonNull final com.teamsos.android.alertme.Account_Switch.Callback callback){

        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child("user").child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    callback.onCallback(dataSnapshot.exists());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void isFriend(@NonNull final com.teamsos.android.alertme.Account_Switch.Callback callback){
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child("friend").child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    callback.onCallback(dataSnapshot.exists());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
