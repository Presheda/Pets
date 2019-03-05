package com.precious.pets;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

import static com.precious.pets.PetContentProviderContract.*;
import static com.precious.pets.PetDatabaseContract.*;

public class MainActivity extends AppCompatActivity implements PetRecyclerViewAdapter.PetOnclickListner,
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {


    public static final int LOADER_PET = 0;
    private PetRecyclerViewAdapter recyclerViewAdapter;
    private List<PetHolder> petHolders;
    private PetOpenHelper openHelper;
    private Cursor cursor;
    private boolean userWantsToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openHelper = new PetOpenHelper(this);


        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);

        setUpDisplay();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.action_upload_pets:
                schedulePetUpload();
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            case R.id.action_send_broadcast:
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts the pet upload job
     */

    private void schedulePetUpload() {

        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putString("", "");

        ComponentName componentName = new ComponentName(this, PetUploaderService.class);
        JobInfo jobInfo = new JobInfo.Builder(777, componentName)
                .setExtras(persistableBundle)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);



    }

    /**
     * Does the basic house keeping of initializing the activity fields
     * e.g fab button, recyclerVie
     */
    private void setUpDisplay() {

        FloatingActionButton fab_new_pet = (FloatingActionButton) findViewById(R.id.fab_button_new_pet);

        fab_new_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EditPetActivity.class));
            }
        });


        RecyclerView petRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_pet);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        petRecyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        petRecyclerView.addItemDecoration(itemDecoration);


        recyclerViewAdapter = new PetRecyclerViewAdapter(this, cursor, this);
        petRecyclerView.setAdapter(recyclerViewAdapter);


        callDeletePet(petRecyclerView);



    }

    /**
     * Associate a touchHelper to the recyclerView to enable swipe delete
     * @param petRecyclerView
     */
    private void callDeletePet(RecyclerView petRecyclerView) {
        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                int id = recyclerViewAdapter.getItemCursorid(viewHolder.getAdapterPosition());
                Uri petUri = ContentUris.withAppendedId(Pets.CONTENT_URI, id);

                userWantsToDelete = true;

                //getContentResolver().delete(petUri, null, null);

               Snackbar snackbar =  Snackbar.make(petRecyclerView, "Item deleted Successfully", Snackbar.LENGTH_LONG);
               snackbar.setAction("UNDO", new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       userWantsToDelete = false;
                       recyclerViewAdapter.alertAdapter();
                   }
               });

               snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                   @Override
                   public void onDismissed(Snackbar transientBottomBar, int event) {
                       super.onDismissed(transientBottomBar, event);

                       if(userWantsToDelete){
                           @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Cursor> asyncTask = new AsyncTask<Void, Void, Cursor>() {
                               @Override
                               protected Cursor doInBackground(Void... voids) {
                                   getContentResolver().delete(petUri, null, null);

                                   String[] columns = {
                                           Pets._ID,
                                           Pets.COLUMN_PET_NAME,
                                           Pets.COLUMN_PET_TYPE,
                                           Pets.COLUMN_PET_AGE,
                                           Pets.COLUMN_PET_SEX
                                   };

                                   Cursor cursor = getContentResolver().query(Pets.CONTENT_URI, columns, null, null, null);

                                   return cursor;
                               }

                               @Override
                               protected void onPostExecute(Cursor cursor) {
                                   recyclerViewAdapter.changeCursor(cursor);
                               }
                           };
                           asyncTask.execute();
                       }

                   }

                   @Override
                   public void onShown(Snackbar transientBottomBar) {
                       super.onShown(transientBottomBar);
                   }
               });
               snackbar.show();


            }
        }).attachToRecyclerView(petRecyclerView);
    }


    @Override
    public void onClick(int position) {
        Intent intent = new Intent(this, EditPetActivity.class);
        intent.putExtra(EditPetActivity.PET_POSITION, position);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_PET, null, this);
        //loadPetData();
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        CursorLoader loader = null;

        if (id == LOADER_PET) {
            String[] columns = {
                    Pets._ID,
                    Pets.COLUMN_PET_NAME,
                    Pets.COLUMN_PET_TYPE,
                    Pets.COLUMN_PET_AGE,
                    Pets.COLUMN_PET_SEX
            };

            loader = new CursorLoader(this, Pets.CONTENT_URI, columns,
                    null, null, null);
        }

        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == LOADER_PET) {
            recyclerViewAdapter.changeCursor(cursor);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_PET) {
            recyclerViewAdapter.changeCursor(null);

        }
    }

}