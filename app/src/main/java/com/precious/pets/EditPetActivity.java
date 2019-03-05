package com.precious.pets;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import static com.precious.pets.PetContentProviderContract.*;
import static com.precious.pets.PetDatabaseContract.*;

public class EditPetActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>
{

    public static final String PET_POSITION = "pet_position";
    public static final int INVALID_PET_ID = -1;
    public static final int LOADER_PET = 0;
    public static final String PET_NAME_SAVE_INSTANT = "petNameSaveInstant";
    public static final String PET_TYPE_SAVE_INSTANT = "petTypeSaveInstant";
    public static final String PET_AGE_SAVE_INSTANT = "petAgeSaveInstant";
    public static final String PET_SEX_SAVE_INSTANT = "petSexSaveInstant";
    public static final String PET_URI_INSTANT_STATE = "petUri";
    public static final String PET_ID_SAVE_INSTANT = "petId";
    private Spinner petSpinner;
    private ArrayAdapter<CharSequence> mAdapter;
    private int petId;
    private TextInputLayout name_field_layout;
    private TextInputLayout type_field_layout;
    private TextInputLayout age_field_layout;
    private EditText name_field_editText;
    private EditText type_field_editText;
    private EditText age_field_editText;
    private AlertDialog.Builder builder;
    private long affectedRowId;
    private Uri petUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);


        setUpFields();


        builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to cancel saved changes");
        builder.create();


        //Checks wheather the saveinstance state is null so as to load pet info from in
        // it
        if(savedInstanceState != null){
            loadFromSaveInstanceSate(savedInstanceState);
        } else {
            setUpDisplay();
        }



        setError();

    }




    /**
     * determine if the petId is valid or invalid
     * if valid, initialize the LoaderManager to query the database for that petId
     * */
    private void setUpDisplay() {

        Intent intent = getIntent();
        petId = (int) intent.getIntExtra(PET_POSITION, INVALID_PET_ID);

        if(petId != INVALID_PET_ID){
            getSupportLoaderManager().initLoader(LOADER_PET, null, this);
        }

        //loadPetCursor();

    }

    /**
     * Does the basic house keeping of initializing the textfields, editText and spinner
     * of the activity
     */
    private void setUpFields() {
        Button button_save = (Button) findViewById(R.id.button_save);


        name_field_layout = (TextInputLayout) findViewById(R.id.textInputLayout_name);
        type_field_layout = (TextInputLayout) findViewById(R.id.textInputLayout_type);
        age_field_layout = (TextInputLayout) findViewById(R.id.textInputLayout_age);
        name_field_editText = (EditText) findViewById(R.id.textInputEditText_name);
        type_field_editText = (EditText) findViewById(R.id.textInputEditText_type);
        age_field_editText = (EditText) findViewById(R.id.textInputEditText_age);


        petSpinner = (Spinner) findViewById(R.id.spinner_sex);
        petSpinner.setOnItemSelectedListener(this);
        mAdapter = ArrayAdapter.createFromResource(this, R.array.pet_sex_array,
                android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        petSpinner.setAdapter(mAdapter);

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean nonIsEmpty = validateFields();
                if (nonIsEmpty) {
                    savePet(view);
                }
            }
        });
    }


    /**
     * When the save button is pressed, this method is called to check wheather the editText Fields are empty
     *
     * @return boolean
     */
    private boolean validateFields() {

        String petName = name_field_editText.getText().toString();
        String petType = type_field_editText.getText().toString();
        String petAge = age_field_editText.getText().toString();


        return !petName.isEmpty() && !petType.isEmpty() && !petAge.isEmpty();
    }


    /**
     * Called when the save button is clicked
     *
     * This method is responsible for saving/updating the pet into the database
     * Its also displays a Snackbar if the pet is saved/updated successfully
     */
    private void savePet(View view) {
        String petName = name_field_editText.getText().toString().trim();
        String petType = type_field_editText.getText().toString().trim();
        int petAge = Integer.parseInt(age_field_editText.getText().toString());
        int petSex = petSpinner.getSelectedItemPosition();

        String storablePetSex = petSex == 0 ? "Male" : "Female";

        ContentValues values = new ContentValues();
        values.put(PetInfoEntry.COLUMN_PET_NAME, petName);
        values.put(PetInfoEntry.COLUMN_PET_TYPE, petType);
        values.put(PetInfoEntry.COLUMN_PET_AGE, petAge);
        values.put(PetInfoEntry.COLUMN_PET_SEX, storablePetSex);




        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Uri> task = new AsyncTask<Void, Void, Uri>() {
            @Override
            protected Uri doInBackground(Void... voids) {
                if(petId == INVALID_PET_ID){

                    return getContentResolver().insert(Pets.CONTENT_URI, values);

                } else {
                    getContentResolver().update(petUri, values, null, null);
                }

                return petUri;
            }

            @Override
            protected void onPostExecute(Uri uri) {
                super.onPostExecute(uri);
                petUri = uri;
                petId = (int) ContentUris.parseId(uri);
                Snackbar.make(view, "Pet Saved Successfully", Snackbar.LENGTH_LONG).show();
            }
        };

        task.execute();
    }


    /**
     * This method is called after the LoaderManger finishes it's load
     * it receives a cursor and parse that cursor the get the desired field and populate the
     * views(editTex, spinner)
     * @param petCursor
     */

    private void loadPetDataFromCursor(Cursor petCursor) {

        if(petId != INVALID_PET_ID){

            int petNameColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_NAME);
            int petTypeColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_TYPE);
            int petAgeColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_AGE);
            int petSexColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_SEX);
            int petIdColumn = petCursor.getColumnIndex(PetInfoEntry._ID);

            petCursor.moveToNext();

            String petName = petCursor.getString(petNameColumn);
            String petType = petCursor.getString(petTypeColumn);
            String petAge = petCursor.getString(petAgeColumn);
            String petSex = petCursor.getString(petSexColumn).toLowerCase();
            int petId = petCursor.getInt(petIdColumn);

            name_field_editText.setText(petName);
            type_field_editText.setText(petType);
            age_field_editText.setText(petAge);

            if(petSex.matches("male"))
                petSpinner.setSelection(0);
            else
                petSpinner.setSelection(1);
        }

    }




    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

        String one = (String) adapterView.getItemAtPosition(pos);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    /**
     * Enforces that the editText field are filled correctly before saving
     * by displaying error messsages
     */
    public void setError() {
        name_field_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String petName = name_field_editText.getText().toString();
                if (petName.isEmpty()) {
                    name_field_layout.setErrorEnabled(true);
                    name_field_layout.setError(getResources().getString(R.string.layout_error_message));
                } else {
                    name_field_layout.setErrorEnabled(false);
                    name_field_layout.setError(null);
                }

            }
        });
        type_field_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    type_field_layout.setErrorEnabled(true);
                    type_field_layout.setError(getResources().getString(R.string.layout_error_message));
                } else {
                    type_field_layout.setErrorEnabled(false);
                    type_field_layout.setError(null);
                }
            }
        });
        age_field_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    age_field_layout.setErrorEnabled(true);
                    age_field_layout.setError(getResources().getString(R.string.layout_error_message));
                } else {
                    age_field_layout.setErrorEnabled(false);
                    age_field_layout.setError(null);
                }
            }
        });


    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        CursorLoader cursor = null;
        if( i == LOADER_PET){
            String[] columns = {
                    PetInfoEntry._ID,
                    PetInfoEntry.COLUMN_PET_NAME,
                    PetInfoEntry.COLUMN_PET_TYPE,
                    PetInfoEntry.COLUMN_PET_SEX,
                    PetInfoEntry.COLUMN_PET_AGE
            };


            petUri = ContentUris.withAppendedId(Pets.CONTENT_URI, petId);
            cursor = new CursorLoader(this, petUri, columns, null, null,
                    null);
        }

        return cursor;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if(loader.getId() == LOADER_PET){
            loadPetDataFromCursor(cursor);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        loadPetDataFromCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_set_reminder){
            showNotifications();
        }
        if(id == R.id.action_back_up){
            startPetBackUp();
        }
        if(id == R.id.action_send_broadcast){
            creatAndSendBroadcast();
        }

        return super.onOptionsItemSelected(item);
    }

    private void creatAndSendBroadcast() {
        String pet_name = name_field_editText.getText().toString().trim();

        PetEventBroadcastHelper.sendBroadcast(this, "Editing " + pet_name, petId+"");
    }


    /**
     * Starts the backup service
     */
    private void startPetBackUp() {
        Intent intent = new Intent(this, PetService.class);
        startService(intent);
    }


    /**
     * Triggers the notification
     */
    private void showNotifications() {


        Intent intent = new Intent(this, PetBCReceiver.class);
        intent.putExtra(PetBCReceiver.COM_PRECIOUS_PETS_PET_ID_EXTRA, petId);
        intent.putExtra(PetBCReceiver.COM_PRECIOUS_PETS_TITLE_EXTRA, "Showing Notification");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long currentTimeInMilliseconds = SystemClock.elapsedRealtime();
        long TEN_SECONDS = 10 * 1000;
        long alarmTime = currentTimeInMilliseconds + TEN_SECONDS;

        alarmManager.set(AlarmManager.ELAPSED_REALTIME, alarmTime, pendingIntent);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String petName = name_field_editText.getText().toString().trim();
        String petType = type_field_editText.getText().toString().trim();
        int petAge = Integer.parseInt(age_field_editText.getText().toString());
        int petSex = petSpinner.getSelectedItemPosition();

        outState.putString(PET_NAME_SAVE_INSTANT, petName);
        outState.putString(PET_TYPE_SAVE_INSTANT, petType);
        outState.putInt(PET_AGE_SAVE_INSTANT, petAge);
        outState.putInt(PET_SEX_SAVE_INSTANT, petSex);

        outState.putString(PET_URI_INSTANT_STATE, petUri.toString());
        outState.putInt(PET_ID_SAVE_INSTANT, petId);


    }


    /**
     * if saveInstanceSate is not null, this method assumes there's is data in it and loads from
     * it
     * @param outState
     */
    public void loadFromSaveInstanceSate(Bundle outState){

        setUpFields();

        name_field_editText.setText(outState.getString(PET_NAME_SAVE_INSTANT));
        type_field_editText.setText(PET_TYPE_SAVE_INSTANT);
        petSpinner.setSelection(outState.getInt(PET_SEX_SAVE_INSTANT));




        age_field_editText.setText(String.valueOf(outState.getInt(PET_AGE_SAVE_INSTANT)));

        petUri = Uri.parse(outState.getString(PET_URI_INSTANT_STATE));
        petId = outState.getInt(PET_ID_SAVE_INSTANT);

    }
}
