package com.precious.pets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PetBCReceiver extends BroadcastReceiver {

    public static final String COM_PRECIOUS_PETS_PET_ID_EXTRA = "com.precious.pets_petId_extra";
    public static final String COM_PRECIOUS_PETS_TITLE_EXTRA = "com.precious.pets_title_extra";


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.



        int petId = intent.getIntExtra(COM_PRECIOUS_PETS_PET_ID_EXTRA, 0);
        String title = intent.getStringExtra(COM_PRECIOUS_PETS_TITLE_EXTRA);


        PetNotification.notify(context, title, petId);

    }
}
