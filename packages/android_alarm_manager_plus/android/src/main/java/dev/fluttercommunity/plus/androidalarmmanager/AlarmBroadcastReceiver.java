// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package dev.fluttercommunity.plus.androidalarmmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager; //added to test launching app
import android.content.pm.PackageManager; // added to test launching app

public class AlarmBroadcastReceiver extends BroadcastReceiver {
  private static PowerManager.WakeLock wakeLock;
  /**
   * Invoked by the OS when a timer goes off.
   *
   * <p>The associated timer was registered in {@link AlarmService}.
   *
   * <p>In Android, timer notifications require a {@link BroadcastReceiver} as the artifact that is
   * notified when the timer goes off. As a result, this method is kept simple, immediately
   * offloading any work to {@link AlarmService#enqueueAlarmProcessing(Context, Intent)}.
   *
   * <p>This method is the beginning of an execution path that will eventually execute a desired
   * Dart callback function, as registered by the Dart side of the android_alarm_manager plugin.
   * However, there may be asynchronous gaps between {@code onReceive()} and the eventual invocation
   * of the Dart callback because {@link AlarmService} may need to spin up a Flutter execution
   * context before the callback can be invoked.
   */
  @Override
  public void onReceive(Context context, Intent intent) {
    // -- added to test launching app 
    PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
    wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK |
            PowerManager.ACQUIRE_CAUSES_WAKEUP |
            PowerManager.ON_AFTER_RELEASE, "My wakelock");

    Intent startIntent = context
            .getPackageManager()
            .getLaunchIntentForPackage(context.getPackageName());

    startIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    wakeLock.acquire();
    context.startActivity(startIntent);
    // until here added -- //

    AlarmService.enqueueAlarmProcessing(context, intent);

    // below code is added to test launching app.
    wakeLock.release();
  }
}
