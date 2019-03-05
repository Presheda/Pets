package com.precious.pets;

import android.app.IntentService;
import android.content.Intent;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class PetService extends IntentService {



    public PetService() {
        super("PetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PetBackUp.backupPets(this);
    }


}
