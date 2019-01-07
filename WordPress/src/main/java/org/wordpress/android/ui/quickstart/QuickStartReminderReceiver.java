package org.wordpress.android.ui.quickstart;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import org.wordpress.android.R;
import org.wordpress.android.WordPress;
import org.wordpress.android.fluxc.store.QuickStartStore;
import org.wordpress.android.fluxc.store.QuickStartStore.QuickStartTask;
import org.wordpress.android.ui.main.MySiteFragment;
import org.wordpress.android.ui.main.WPMainActivity;
import org.wordpress.android.ui.prefs.AppPrefs;

import javax.inject.Inject;

import static android.content.Context.NOTIFICATION_SERVICE;
import static org.wordpress.android.ui.RequestCodes.QUICK_START_REMINDER_NOTIFICATION;

public class QuickStartReminderReceiver extends BroadcastReceiver {
    public static final String ARG_QUICK_START_TASK_BATCH = "ARG_QUICK_START_TASK_BATCH";

    @Inject QuickStartStore mQuickStartStore;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((WordPress) context.getApplicationContext()).component().inject(this);

        Bundle bundleWithQuickStartTaskDetails = intent.getBundleExtra(ARG_QUICK_START_TASK_BATCH);

        if (bundleWithQuickStartTaskDetails == null) {
            return;
        }

        int siteLocalId = AppPrefs.getSelectedSite();

        QuickStartTaskDetails quickStartTaskDetails = (QuickStartTaskDetails) bundleWithQuickStartTaskDetails
                .getSerializable(QuickStartTaskDetails.KEY);

        // Failsafes
        if (quickStartTaskDetails == null || siteLocalId == -1 || AppPrefs.isQuickStartDisabled()
            || !mQuickStartStore.hasDoneTask(siteLocalId, QuickStartTask.CREATE_SITE)
            || mQuickStartStore.getQuickStartCompleted(siteLocalId)
            || mQuickStartStore.hasDoneTask(siteLocalId, quickStartTaskDetails.getTask())) {
            return;
        }


        Intent resultIntent = new Intent(context, WPMainActivity.class);
        resultIntent.putExtra(MySiteFragment.ARG_QUICK_START_TASK, quickStartTaskDetails.getTask());
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                              | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent notificationContentIntent = PendingIntent.getActivity(context, QUICK_START_REMINDER_NOTIFICATION,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context,
                context.getString(R.string.notification_channel_normal_id))
                .setSmallIcon(R.drawable.ic_my_sites_24dp)
                .setContentTitle(context.getString(quickStartTaskDetails.getTitleResId()))
                .setContentText(context.getString(quickStartTaskDetails.getSubtitleResId()))
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setContentIntent(notificationContentIntent)
                .build();


        if (notificationManager != null) {
            notificationManager.notify(QUICK_START_REMINDER_NOTIFICATION, notification);
        }
    }
}
