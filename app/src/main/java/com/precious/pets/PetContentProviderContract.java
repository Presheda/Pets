package com.precious.pets;

import android.net.Uri;
import android.provider.BaseColumns;

public class PetContentProviderContract {



    private PetContentProviderContract() {
    }

    public static final String AUTHORITY = "com.precious.pets.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);


    protected interface PetColumns{
        public static final String COLUMN_PET_NAME = "pet_name";
        public static final String COLUMN_PET_TYPE = "pet_type";
        public static final String COLUMN_PET_SEX = "pet_sex";
        public static final String COLUMN_PET_AGE = "pet_age";
    }

    public static final class Pets implements BaseColumns, PetColumns {
        public static final String PATH = "pets";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }
}
