package com.precious.pets;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.precious.pets.PetContentProviderContract.Pets;
import com.precious.pets.PetDatabaseContract.PetInfoEntry;



public class PetUploader {

    public static final String LOG_TAG = PetUploader.class.getSimpleName();
    private Context context;
    private boolean isCanceled;


    public boolean isCanceled() {
        return isCanceled;
    }



    public PetUploader(Context context) {
        this.context = context;

    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public void startUpload(){


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

    private  void writeBackUpLog(Cursor petCursor) {

        int petNameColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_NAME);
        int petTypeColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_TYPE);
        int petAgeColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_AGE);
        int petSexColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_SEX);
        int petIdColumn = petCursor.getColumnIndex(PetInfoEntry._ID);


        isCanceled = false;
        while (petCursor.moveToNext() && !isCanceled) {


            String petName = petCursor.getString(petNameColumn);
            String petType = petCursor.getString(petTypeColumn);
            String petAge = petCursor.getString(petAgeColumn);
            String petSex = petCursor.getString(petSexColumn);
            int petCursorId = petCursor.getInt(petIdColumn);

            Log.e(LOG_TAG, "<<<<<<<<<<< " + "Uploading  pet with id = " + petCursorId
                    + ",  and Pet Name = " + petName + " >>>>>>>>>>>>>>");

            simulateLongRunningOperation();

        }
        if(isCanceled){
            Log.e(LOG_TAG, "UPLOADING INTERRUPTED >>>>>>>>>>>>>>>>>>>>>>");
        } else {
            Log.e(LOG_TAG, "UPLOADING COMPLETED >>>>>>>>>>>>>>>>>>>>>>");
        }
        petCursor.close();

    }




    private void simulateLongRunningOperation() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
    }
}
