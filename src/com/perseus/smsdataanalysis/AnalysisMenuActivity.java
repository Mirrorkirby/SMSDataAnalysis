package com.perseus.smsdataanalysis;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AnalysisMenuActivity extends Activity {
	private final static String LOG_TAG = "AnalysisMenuActivity_tag";
	private Spinner analysisType;
	private Button btnSubmit;
	private TextView startDate, endDate;

	private int start_year, end_year;
	private int start_month, end_month;
	private int start_day, end_day;
	private DatePickerDialog startDatePickerDialog;
	private DatePickerDialog endDatePickerDialog;

	static final int START_DATE_DIALOG_ID = 0;
	static final int END_DATE_DIALOG_ID = 1;

	static final int CURR_YEAR = Calendar.getInstance().get(Calendar.YEAR);
	static final int CURR_MONTH = Calendar.getInstance().get(Calendar.MONTH);
	static final int CURR_DAY = Calendar.getInstance().get(
			Calendar.DAY_OF_MONTH);
	
	private static final int CONTACT_PICKER_RESULT = 1001;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analysis_menu);
		Log.v(LOG_TAG, "A verbose message");

		this.setTitle("Data Analysis Menu");
		startDate = (TextView) findViewById(R.id.start_date_display);
		endDate = (TextView) findViewById(R.id.end_date_display);

		addListenerOnSpinnerItemSelection();
		setCurrentDateOnView();

		startDatePickerDialog = new DatePickerDialog(this,
				startDatePickerListener, start_year, start_month, start_day);
		endDatePickerDialog = new DatePickerDialog(this, endDatePickerListener,
				end_year, end_month, end_day);
	}
	public void doLaunchContactPicker(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		startActivityForResult(intent, CONTACT_PICKER_RESULT);
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case CONTACT_PICKER_RESULT:
            	Uri result = data.getData();
            	Log.v(LOG_TAG, "Got a result: " + result.toString());
/*
            	// get the phone number id from the Uri
            	String id = result.getLastPathSegment();

            	// query the phone numbers for the selected phone number id
            	Cursor c = getContentResolver().query(
            	    Phone.CONTENT_URI, null,
            	    Phone._ID + "=?",
            	    new String[]{id}, null);

            	int phoneIdx = c.getColumnIndex(Phone.NUMBER);

            	if(c.getCount() == 1) { // contact has a single phone number
            	    // get the only phone number
            	    if(c.moveToFirst()) {
            	        String phone = c.getString(phoneIdx);
            	        Log.v(TAG, "Got phone number: " + phone);

            	        loadContactInfo(phone); // do something with the phone number

            	    } else {
            	        Log.w(TAG, "No results");
            	    }
            	}*/
                break;
            }
        } else {
            // gracefully handle failure
            Log.w(LOG_TAG, "Warning: activity result not ok");
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void addListenerOnSpinnerItemSelection() {
		analysisType = (Spinner) findViewById(R.id.analysis_type);
		analysisType.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}

	private class CustomOnItemSelectedListener implements
			OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			Toast.makeText(
					parent.getContext(),
					"OnItemSelectedListener : "
							+ parent.getItemAtPosition(pos).toString(),
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	}

	// display current date
	public void setCurrentDateOnView() {
		setCurrentDate();
		// set current date into textview
		startDate.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(start_month + 1).append("-").append(start_day)
				.append("-").append(start_year).append(" "));
		endDate.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(end_month + 1).append("-").append(end_day).append("-")
				.append(end_year).append(" "));
	}

	private void setCurrentDate() {
		start_year = CURR_YEAR;
		start_month = CURR_MONTH;
		start_day = CURR_DAY;
		end_year = start_year;
		end_month = start_month;
		end_day = start_day;
	}

	@SuppressWarnings("deprecation")
	public void pickStartDate(View view) {
		showDialog(START_DATE_DIALOG_ID);
	}

	@SuppressWarnings("deprecation")
	public void pickEndDate(View view) {
		showDialog(END_DATE_DIALOG_ID);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case START_DATE_DIALOG_ID:
			// set date picker as current date
			startDatePickerDialog
					.updateDate(start_year, start_month, start_day);
			return startDatePickerDialog;
		case END_DATE_DIALOG_ID:
			// set date picker as current date
			endDatePickerDialog.updateDate(end_year, end_month, end_day);
			return endDatePickerDialog;
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener startDatePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			if ((selectedYear > end_year)
					|| (selectedYear == end_year && selectedMonth > end_month)
					|| (selectedYear == end_year
							&& selectedMonth == start_month && selectedDay > end_day)) {
				Toast.makeText(AnalysisMenuActivity.this, "Invalid start date!",
						Toast.LENGTH_SHORT).show();
				startDatePickerDialog.updateDate(start_year, start_month, start_day);
			} else {
				start_day = selectedDay;
				start_month = selectedMonth;
				start_year = selectedYear;
				// set selected date into textview
				startDate.setText(new StringBuilder().append(start_month + 1)
						.append("-").append(start_day).append("-")
						.append(start_year).append(" "));
			}
		}
	};

	private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			if ((selectedYear > CURR_YEAR)
					|| (selectedYear == CURR_YEAR && selectedMonth > CURR_MONTH)
					|| (selectedYear == CURR_YEAR
							&& selectedMonth == CURR_MONTH && selectedDay > CURR_DAY)) {
				Toast.makeText(AnalysisMenuActivity.this,
						"Invalid end date! Cannot analyze future texts!",
						Toast.LENGTH_SHORT).show();
				endDatePickerDialog.updateDate(end_year, end_month,
						end_day);
			} else {
				if ((selectedYear < start_year)
						|| (selectedYear == start_year && selectedMonth < start_month)
						|| (selectedYear == start_year
								&& selectedMonth == start_month && selectedDay < start_day)) {
					start_day = end_day;
					start_month = end_month;
					start_year = end_year;
					// set selected date into textview
					startDate.setText(new StringBuilder().append(start_month + 1)
							.append("-").append(start_day).append("-")
							.append(start_year).append(" "));
					startDatePickerDialog
							.updateDate(start_year, start_month, start_day);
				}
				end_year = selectedYear;
				end_month = selectedMonth;
				end_day = selectedDay;
				// set selected date into textview
				endDate.setText(new StringBuilder().append(end_month + 1)
						.append("-").append(end_day).append("-")
						.append(end_year).append(" "));
			}
		}
	};
	
	public void analyze(View view) {
		Intent myIntent = new Intent(AnalysisMenuActivity.this, AnalysisResultActivity.class);
		myIntent.putExtra("type", analysisType.getSelectedItem().toString());
		myIntent.putExtra("start_date", startDate.getText().toString());
		myIntent.putExtra("end_date", endDate.getText().toString());
		AnalysisMenuActivity.this.startActivity(myIntent);
	}
}