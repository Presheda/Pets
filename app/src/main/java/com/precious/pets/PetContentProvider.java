package com.precious.pets;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static com.precious.pets.PetContentProviderContract.*;
import static com.precious.pets.PetDatabaseContract.*;

public class PetContentProvider extends ContentProvider {

    private PetOpenHelper mDbOpenHelper;

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int PETS = 0;

    public static final int PET_ROW = 1;

    static {
        uriMatcher.addURI(AUTHORITY, Pets.PATH, PETS);
        uriMatcher.addURI(AUTHORITY, Pets.PATH + "/#", PET_ROW);
    }

    public PetContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        int nRows = -1;
        switch (uriMatcher.match(uri)){
            case PET_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = Pets._ID + " =?";
                rowSelectionArgs = new String[] {
                        Long.toString(rowId)
                };
                SQLiteDatabase database = mDbOpenHelper.getWritableDatabase();
               nRows =  database.delete(PetInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
               break;
        }

        return nRows;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowId = -1;
        Uri rowUri = null;
        switch (uriMatcher.match(uri)){
            case PETS:
                SQLiteDatabase database = mDbOpenHelper.getWritableDatabase();
               rowId =  database.insert(PetInfoEntry.TABLE_NAME, null, values);
               rowUri = ContentUris.withAppendedId(Pets.CONTENT_URI, rowId);
               break;
        }

        return rowUri;

    }

    @Override
    public boolean onCreate() {
        mDbOpenHelper = new PetOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;

        SQLiteDatabase database = mDbOpenHelper.getReadableDatabase();

        Cursor cursor = null;

        int uriResult = uriMatcher.match(uri);

        switch (uriResult){
            case PETS:

               cursor =  database.query(PetInfoEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
               break;
            case PET_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = Pets._ID + " =?";
                rowSelectionArgs = new String[]{
                        Long.toString(rowId)
                };

                cursor = database.query(PetInfoEntry.TABLE_NAME,  projection, rowSelection, rowSelectionArgs,
                        null, null, sortOrder);
                break;

                }

        return cursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        int nRows = -1;

        SQLiteDatabase database = mDbOpenHelper.getWritableDatabase();


        switch (uriMatcher.match(uri)){
            case PETS:
               nRows =  database.update(PetInfoEntry.TABLE_NAME, values, selection, selectionArgs);
               break;
            case PET_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = Pets._ID + " =?";
                rowSelectionArgs = new String[]{
                  Long.toString(rowId)
                };
               nRows = database.update(PetInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);
               break;

        }

        return nRows;
    }
}
