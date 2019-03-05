package com.precious.pets;

import android.provider.BaseColumns;

public final class PetDatabaseContract {

    private PetDatabaseContract() {
    }

    public static final class PetInfoEntry implements BaseColumns{
        public static final String TABLE_NAME = "pet_info";
        public static final String COLUMN_PET_NAME = "pet_name";
        public static final String COLUMN_PET_TYPE = "pet_type";
        public static final String COLUMN_PET_SEX = "pet_sex";
        public static final String COLUMN_PET_AGE = "pet_age";

        //CREATE INDEX pet_info_index1 ON  pet_info(pet_name)
        private static final String INDEX1 = TABLE_NAME + "_index1";
        public  static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                "(" + COLUMN_PET_NAME + ")";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_PET_NAME + " TEXT NOT NULL, " +
                        COLUMN_PET_TYPE + " TEXT NOT NULL, " +
                        COLUMN_PET_SEX + " TEXT NOT NULL, " +
                        COLUMN_PET_AGE + " TEXT NOT NULL)";

    }
}
