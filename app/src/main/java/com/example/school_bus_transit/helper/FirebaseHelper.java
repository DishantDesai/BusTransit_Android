package com.example.school_bus_transit.helper;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.school_bus_transit.model.BusModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;


public class FirebaseHelper
{

    public static String updateDocument(String collection,String doc_id,HashMap doc)
    {

//        FirebaseFirestore.getInstance().collection(collection).document(doc_id)
//                .update("capital", true)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully updated!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error updating document", e);
//                    }
//                });


        return "";
    }

    public static boolean updateField(String collection, String doc_id, String key, String value)
    {
        final boolean[] result = {false};
        FirebaseFirestore.getInstance().collection(collection).document(doc_id)
                .update(key, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        result[0] = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error updating document", e);
                        result[0] = false;
                    }
                });


        return result[0];
    }

    public static void getBusModel()
    {
        FirebaseFirestore.getInstance().collection("Bus").document(constants.CurrentUser.getbus_id().trim()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   DocumentSnapshot doc = task.getResult();

                                                   boolean as = (boolean) doc.getData().get("active_sharing");
                                                   boolean gs = (boolean) doc.getData().get("going_to_school");
                                                   constants.CurrentBus = new BusModel(
                                                           as,
                                                           gs,
                                                           doc.getData().get("bus_id").toString(),
                                                           doc.getData().get("bus_number").toString(),
                                                           doc.getData().get("current_lat").toString(),
                                                           doc.getData().get("current_long").toString(),
                                                           doc.getData().get("destination").toString(),
                                                           doc.getData().get("destination_lat").toString(),
                                                           doc.getData().get("destination_long").toString(),
                                                           doc.getData().get("school_id").toString(),
                                                           doc.getData().get("source").toString(),
                                                           doc.getData().get("source_lat").toString(),
                                                           doc.getData().get("source_long").toString()
                                                   );
                                               }
//                    showDriverLocationOnMap();

                                           }
                                       }
                );
    }


    public static boolean update2Field(String collection, String doc_id, String key1, String value1,String key2, String value2)
    {
        final boolean[] result = {false};
        FirebaseFirestore.getInstance().collection(collection).document(doc_id)
                .update(key1, value1,key2,value2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        result[0] = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error updating document", e);
                        result[0] = false;
                    }
                });


        return result[0];
    }

}
