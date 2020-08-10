package com.leonardorick.olx_clone.model;

import com.leonardorick.olx_clone.helper.Constants;
import com.leonardorick.olx_clone.helper.FirebaseConfig;

public class AdvertHelper {

    public static void saveAdOnDatabase(Advert ad) {
        String userId = FirebaseConfig.getUserId();
        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.MyAdvertsNode.KEY)
                .child(userId)
                .child(ad.getId())
                .setValue(ad);
        savePublicAd(ad);
    }

    public static void savePublicAd(Advert ad) {
        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.PublicAdvertsNode.KEY)
                .child(ad.getState())
                .child(ad.getCategory())
                .child(ad.getId())
                .setValue(ad);
    }

    public static void removeAd(Advert ad) {
        String userId = FirebaseConfig.getUserId();

        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.MyAdvertsNode.KEY)
                .child(userId)
                .child(ad.getId())
                .removeValue();

        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.PublicAdvertsNode.KEY)
                .child(ad.getState())
                .child(ad.getCategory())
                .child(ad.getId())
                .removeValue();
    }
}