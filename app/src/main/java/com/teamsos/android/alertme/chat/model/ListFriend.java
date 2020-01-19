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

package com.teamsos.android.alertme.chat.model;

import java.util.ArrayList;

public class ListFriend {
    private ArrayList<Friend> listFriend;

    public ArrayList<Friend> getListFriend() {
        return listFriend;
    }

    public ListFriend(){
        listFriend = new ArrayList<>();
    }

    public String getAvataById(String id){
        for(Friend friend: listFriend){
            if(id.equals(friend.id)){
                return friend.avata;
            }
        }
        return "";
    }

    public void setListFriend(ArrayList<Friend> listFriend) {
        this.listFriend = listFriend;
    }

}
