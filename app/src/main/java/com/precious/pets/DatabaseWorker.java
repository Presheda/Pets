package com.precious.pets;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import static com.precious.pets.PetDatabaseContract.*;

/**
 * At creation of the database, this class is responsible for inserting dummy pet data into the database for
 * practice purpose
 */
public class DatabaseWorker {

    private SQLiteDatabase database;

    /**
     * create an instance of the classe and accept
     * @param database
     */
    public DatabaseWorker(SQLiteDatabase database) {
        this.database = database;
    }


    /**
     * This method list through a list of pets and begin the work of inserting them into t
     * he database
     * @param petHolders
     */

    public void insertNote(List<PetHolder> petHolders){

        for(int i = 0; i<petHolders.size(); i++){
            String petName = petHolders.get(i).getPetName();
            String petType = petHolders.get(i).getPetBreed();
            int petSex = petHolders.get(i).getPetSex();
            int petAge = petHolders.get(i).getPetAge();
            String petMainSex = "";
            if(petSex == 0){
                petMainSex = "Male";
            } else {
                petMainSex = "Female";
            }

            insertIntoDataBase(petName, petType, petAge, petMainSex);

        }

    }

    /**
     * Insert  dummy pet into the database
     * @param petName
     * @param petType
     * @param petAge
     * @param petMainSex
     */

    private void insertIntoDataBase(String petName, String petType, int petAge, String petMainSex) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetInfoEntry.COLUMN_PET_NAME, petName);
        contentValues.put(PetInfoEntry.COLUMN_PET_TYPE, petType);
        contentValues.put(PetInfoEntry.COLUMN_PET_SEX, petMainSex);
        contentValues.put(PetInfoEntry.COLUMN_PET_AGE, petAge);

        long rowId = database.insert(PetInfoEntry.TABLE_NAME, null, contentValues);
    }

}
