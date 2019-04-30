package com.github.duc010298.android.services;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.github.duc010298.android.task.SendLocationHistoryTask;

public class ScheduleSendLocationHistory extends JobService {
    private SendLocationHistoryTask sendLocationHistoryTask;
    @Override
    public boolean onStartJob(final JobParameters params) {
        sendLocationHistoryTask = new SendLocationHistoryTask(this, false) {
            @Override
            protected void onPostExecute(Void v) {
                jobFinished(params, false);
            }
        };
        sendLocationHistoryTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        sendLocationHistoryTask.cancel(true);
        return false;
    }
}
