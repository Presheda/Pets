package com.precious.pets;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import static com.precious.pets.PetContentProviderContract.*;
import static com.precious.pets.PetDatabaseContract.*;

/**
 * This is a helper class that simulate backing up of pets
 */

public class PetBackUp {

    public static final String LOG_TAG = PetBackUp.class.getSimpleName();


    private PetBackUp() {
    }

    public static void backupPets(Context context) {

        String[] columns = {
                Pets._ID,
                Pets.COLUMN_PET_NAME,
                Pets.COLUMN_PET_AGE,
                Pets.COLUMN_PET_TYPE,
                Pets.COLUMN_PET_SEX
        };

        Cursor cursor = context.getContentResolver().query(Pets.CONTENT_URI, columns,
                null, null, null);

        writeBackUpLog(cursor);

    }

    private static void writeBackUpLog(Cursor petCursor) {

        int petNameColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_NAME);
        int petTypeColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_TYPE);
        int petAgeColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_AGE);
        int petSexColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_SEX);
        int petIdColumn = petCursor.getColumnIndex(PetInfoEntry._ID);


        while (petCursor.moveToNext()) {


            String petName = petCursor.getString(petNameColumn);
            String petType = petCursor.getString(petTypeColumn);
            String petAge = petCursor.getString(petAgeColumn);
            String petSex = petCursor.getString(petSexColumn);
            int petCursorId = petCursor.getInt(petIdColumn);

            Log.e(LOG_TAG, "<<<<<<<<<<< " + "Backing Up pet with id = " + petCursorId
                    + ",  and Pet Name = " + petName + " >>>>>>>>>>>>>>");

            simulateLongRunningOperation();

        }
        Log.e(LOG_TAG, "BACKINGUP COMPLETED >>>>>>>>>>>>>>>>>>>>>>");
        petCursor.close();

    }

    private static void simulateLongRunningOperation() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
    }
}
