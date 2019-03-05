package com.precious.pets;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class PetUploaderService extends JobService {

    public PetUploader petUploader;

    public PetUploaderService() {
    }

    @Override
    public boolean onStartJob(JobParameters thisjobParameters) {

        @SuppressLint("StaticFieldLeak") AsyncTask<JobParameters, Void, Void> task = new AsyncTask<JobParameters, Void, Void>() {
            @Override
            protected Void doInBackground(JobParameters... jobParameters) {

                JobParameters parameters = jobParameters[0];
                //String name = parameters.getExtras().getString("");

                petUploader.startUpload();

                if(!petUploader.isCanceled()){
                    jobFinished(thisjobParameters, false);
                }

                return null;
            }
        };

        petUploader = new PetUploader(this);
        task.execute(thisjobParameters);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        petUploader.setCanceled(true);
        return true;
    }

}
