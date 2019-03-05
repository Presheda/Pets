package com.precious.pets;

import android.content.Context;
import android.content.Intent;

public class PetEventBroadcastHelper {


    public static final String ACTION_PET_EVENT = "com.precious.pets.INTENT_ACTION";
    public static final String EXTRA_MESSAGE_ID = "EXTRA_MESSAGE_ID";
    public static final String EXTRA_PET_ID = "EXTRA_PET_ID";

    public static void sendBroadcast(Context context, String message, String messageId){

        Intent intent = new Intent(ACTION_PET_EVENT);
        intent.putExtra(EXTRA_PET_ID, messageId);
        intent.putExtra(EXTRA_MESSAGE_ID, message);


        context.sendBroadcast(intent);

    }


}
