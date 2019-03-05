package com.precious.pets;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.precious.pets.PetDatabaseContract.*;

public class PetRecyclerViewAdapter extends RecyclerView.Adapter<PetRecyclerViewAdapter.PetRecyclerViewHolder> {

    Context context;
    Cursor petCursor;

    final PetOnclickListner mListener;
    private int petNameColumn;
    private int petTypeColumn;
    private int petAgeColumn;
    private int petSexColumn;
    private int petIdColumn;
    private int petCursorId;

    public PetRecyclerViewAdapter(Context context, Cursor petCursor, PetOnclickListner listner) {
        this.context = context;

        this.petCursor = petCursor;
        mListener = listner;
    }

    @NonNull
    @Override
    public PetRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_pet, viewGroup, false);
        return new PetRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetRecyclerViewHolder petRecyclerViewHolder, int i) {

        petCursor.moveToPosition(i);

       String petName = petCursor.getString(petNameColumn);
       String petType = petCursor.getString(petTypeColumn);
       String petAge = petCursor.getString(petAgeColumn);
       String petSex = petCursor.getString(petSexColumn);
        petCursorId = petCursor.getInt(petIdColumn);

       petRecyclerViewHolder.petName.setText(petName);
       petRecyclerViewHolder.petBreed.setText(petType);
       petRecyclerViewHolder.petSex.setText(petSex);
       petRecyclerViewHolder.petAge.setText(petAge);
       petRecyclerViewHolder.mPetId = petCursorId;


    }

    public int getItemCursorid(int adapterPosition){
        petCursor.moveToPosition(adapterPosition);

        return petCursor.getInt(petIdColumn);
    }

    public interface PetOnclickListner{
        void onClick(int position);
    }

    @Override
    public int getItemCount() {
        if(petCursor != null){
            return petCursor.getCount();
        }
        return 0;
    }

    public void changeCursor(Cursor cursor){

        if(petCursor != null){
           // petCursor.close();
        }
        petCursor = cursor;
        populateColumnId();
        notifyDataSetChanged();
    }

    private void populateColumnId() {

        if(petCursor == null){
            return;
        }

        petNameColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_NAME);
        petTypeColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_TYPE);
        petAgeColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_AGE);
        petSexColumn = petCursor.getColumnIndex(PetInfoEntry.COLUMN_PET_SEX);
        petIdColumn = petCursor.getColumnIndex(PetInfoEntry._ID);
    }


    public void alertAdapter(){
        notifyDataSetChanged();
    }

    public class PetRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView petName;
        private final TextView petBreed;
        private final TextView petSex;
        private final TextView petAge;
        int mPetId;

        public PetRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            petName = (TextView) itemView.findViewById(R.id.textview_pet_name);
            petBreed = (TextView) itemView.findViewById(R.id.textview_pet_breed);
            petSex = (TextView) itemView.findViewById(R.id.textview_pet_sex);
            petAge = (TextView) itemView.findViewById(R.id.textview_pet_age);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(mPetId);
        }
    }

}
