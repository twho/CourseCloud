/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.coursecloud;

import static com.coursecloud.tools.CommonUtilities.SENDER_ID;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.coursecloud.constants.GCMConstants;
import com.coursecloud.tools.MyBroadcastReceiver;
import com.google.android.gcm.GCMBaseIntentService;

/**
 * IntentService responsible for handling GCM messages.
 */
@SuppressWarnings("deprecation")
public class GCMIntentService extends GCMBaseIntentService implements
		GCMConstants {
	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {  
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		// MyBroadcastReceiver.displayMessage(context,
		// getString(R.string.gcm_registered, registrationId));
		// ServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		MyBroadcastReceiver.displayMessage(context,
				getString(R.string.gcm_unregistered));
		// ServerUtilities.unregister(context, registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message. Extras: " + intent.getExtras());

		// String action = intent.getStringExtra(EXTRA_ACTION);
		// //
		// if (action != null) {
		// if (action.equals(ACTION_ADD_FRIEND)) {
		// MyBroadcastReceiver.addFriend(context, intent.getExtras());
		// } else if (action.equals(ACTION_SEND_MESSAGE)) {
		// }
		// }
		MyBroadcastReceiver.sendMessage(context, intent.getExtras());
		String title = getString(R.string.app_name);
		String message = getString(R.string.gcm_message);

		try {
			if (intent.getStringExtra(EXTRA_TITLE) != null) {
				title = intent.getStringExtra(EXTRA_TITLE);
			}
			if (intent.getStringExtra(EXTRA_MESSAGE) != null) {
				message = intent.getStringExtra(EXTRA_MESSAGE);
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		try {
			if (intent.getStringExtra(EXTRA_NAME) != null) {
				title = intent.getStringExtra(EXTRA_NAME);
				message = getString(R.string.gcm_add_friend);
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		// notifies user
		generateNotification(context, title, message);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String title = getString(R.string.app_name);
		String message = getString(R.string.gcm_deleted, total);
		MyBroadcastReceiver.displayMessage(context, message);
		// notifies user
		generateNotification(context, title, message);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		MyBroadcastReceiver.displayMessage(context,
				getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		MyBroadcastReceiver.displayMessage(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String title,
			String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		//TODO (Xu) change to ChatActivity
		Intent notificationIntent = new Intent(context, MainActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
	}

}
