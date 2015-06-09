package com.coursecloud.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coursecloud.MainActivity;
import com.coursecloud.R;

@SuppressWarnings("deprecation")
public class AlertDialogManager {
	/**
	 * Function to display simple Alert Dialog
	 * 
	 * @param context
	 *            - application context
	 * @param title
	 *            - alert dialog title
	 * @param message
	 *            - alert message
	 * @param status
	 *            - success/failure (used to set icon) - pass null if you don't
	 *            want icon
	 * */
	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		alertDialog.setTitle(title);
		alertDialog.setMessage(message);

		if (status != null)
			// Setting alert dialog icon
			alertDialog
					.setIcon((status) ? R.drawable.success : R.drawable.fail);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	public void showEnterNameDialog(final Context context) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context)
				.create();
		alertDialog.setCancelable(false);
		alertDialog.setTitle("Quick registration on FleetChat");

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		final TextView input11 = new TextView(context);
		input11.setLayoutParams(lp);
		input11.setText("Your Name: ");

		final EditText input12 = new EditText(context);
		input12.setLayoutParams(lp);
		input12.setHint("Type name here");
		input12.setText(MainActivity.getUserName(context));

		final TextView input21 = new TextView(context);
		input21.setLayoutParams(lp);
		input21.setText("Your Email: ");

		final EditText input22 = new EditText(context);
		input22.setLayoutParams(lp);
		input22.setHint("Type email here");
		input22.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
		input22.setText(MainActivity.getUserName(context));

		final Button btn = new Button(context);
		btn.setLayoutParams(lp);
		btn.setText("Confirm");
		btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!input12.getText().toString().trim().isEmpty()
						&& !input22.getText().toString().trim().isEmpty()) {
					SharedPreferences sp = context.getSharedPreferences(
							MainActivity.PREF, Context.MODE_PRIVATE);
					SharedPreferences.Editor se = sp.edit();
					se.putString(MainActivity.PREF_NAME, input12.getText()
							.toString().trim());
					se.putString(MainActivity.PREF_EMAIL, input22.getText()
							.toString().trim());
					se.commit();
					alertDialog.dismiss();
				}
			}
		});

		final LinearLayout ll = new LinearLayout(context);
		ll.setLayoutParams(lp);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(input11);
		ll.addView(input12);
		ll.addView(input21);
		ll.addView(input22);
		ll.addView(btn);
		alertDialog.setView(ll); // uncomment this line

		// Showing Alert Message
		alertDialog.show();
	}

	public void showMessageDialog(Context context, String title, String message) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context)
				.create();

		alertDialog.setTitle(title);

		alertDialog.setMessage(message);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	/**
	 * Show Exit App Dialog.
	 * 
	 * @param context
	 */
	public void showExitDialog(final Context context) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context)
				.create();

		alertDialog.setTitle("Exit");
		alertDialog.setMessage("You are leaving CourseCloud");

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Comfirm",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						((Activity) context).finish();
					}
				});

		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
					}
				});
		// Showing Alert Message
		alertDialog.show();
	}

	public void showProgressDialog(String title, String message,
			ProgressDialog dialog) {
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setCancelable(true);
		dialog.show();
	}
}
