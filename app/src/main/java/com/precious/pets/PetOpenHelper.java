package com.precious.pets;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import static com.precious.pets.PetDatabaseContract.*;

public class PetOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PetsBrowser.db";
    public static final int DATABASE_VERSION = 1;

    public PetOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PetInfoEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(PetInfoEntry.SQL_CREATE_INDEX1);


        DatabaseWorker worker = new DatabaseWorker(sqLiteDatabase);
        worker.insertNote(DummyPetData.getInstance().getDummyPets());

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
