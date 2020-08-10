package com.leonardorick.olx_clone.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseConfig {
    private static FirebaseAuth auth;
    private static DatabaseReference db;
    private static StorageReference storageRef;

    /**
     * static method to keep FirebaseAuth as one instance on entire app
     * @return Fireabase global instance
     */
    public static FirebaseAuth getAuth() {
        if (auth == null) auth = FirebaseAuth.getInstance();
        return auth;
    }

    /**
     * static method to keep FirebaseFirestore reference as one instance on entire app
     * @return FirebaseFirestore global instance
     */
    public static DatabaseReference getFirebaseDatabase() {
        if (db == null) db = FirebaseDatabase.getInstance().getReference();
        return db;
    }

    /**
     * static method to keep StorageReference as one instance on entire app
     * @return StorageReference global instance
     */
    public static StorageReference getFirebaseStorage() {
        if (storageRef == null) storageRef = FirebaseStorage.getInstance().getReference();
        return storageRef;
    }

    public static String getUserId() {
        return FirebaseConfig.getAuth().getCurrentUser().getUid();
    }
}