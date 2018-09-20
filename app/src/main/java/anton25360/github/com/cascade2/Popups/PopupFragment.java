package anton25360.github.com.cascade2.Popups;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import anton25360.github.com.cascade2.Cascade;
import anton25360.github.com.cascade2.Classes.AlarmReciever;
import anton25360.github.com.cascade2.R;
import anton25360.github.com.cascade2.Classes.Reminder;
import anton25360.github.com.cascade2.DateTimeFragments.DatePickerFragment;
import anton25360.github.com.cascade2.DateTimeFragments.TimePickerFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PopupFragment extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener{

    private static final String TAG = "Popup";
    public static String notificationTitle;

    @BindView(R.id.popup_description)TextInputLayout desc;
    @BindView(R.id.popup_date)TextView date;
    @BindView(R.id.popup_time)TextView time;
    @BindView(R.id.popup_timeAllDay) TextView timeAllDay;
    @BindView(R.id.popup_addTaskTEMP)Button addTask;
    @BindView(R.id.popup_cancel)Button cancel;
    @BindView(R.id.popup_checkbox)CheckBox mCheckBox;

    //colour buttons
    private String colour = "";
    @BindView(R.id.popup_buttonBlue)Button mBlue;
    @BindView(R.id.popup_buttonOrange)Button mOrange;
    @BindView(R.id.popup_buttonGreen)Button mGreen;
    @BindView(R.id.popup_buttonRed)Button mRed;
    @BindView(R.id.popup_buttonPurple)Button mPurple;
    @BindView(R.id.popup_buttonPeach)Button mPeach;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String userID;
    long notificationTime, notificationDate;

    Calendar calendar = Calendar.getInstance();

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NotificationManagerCompat notificationManager;



    @Override
    protected void onStart() {
        super.onStart();

        if (user != null) { //if user is logged in...

            userID = user.getUid();

        } else {
            userID = null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);
        ButterKnife.bind(this);

        notificationManager = NotificationManagerCompat.from(this);

        prefs = getApplicationContext().getSharedPreferences("prefs", 0);
        editor = prefs.edit();

        editor.putBoolean("allDay", false); // sets has time to true by default
        editor.apply();

        setDisplayMetics();
        getCurrentDate();
        chooseDate();
        chooseTime();
        checkSwitch();


        //init buttons for onclick
        mBlue.setOnClickListener(this);
        mOrange.setOnClickListener(this);
        mGreen.setOnClickListener(this);
        mRed.setOnClickListener(this);
        mPurple.setOnClickListener(this);
        mPeach.setOnClickListener(this);


        cancel.setTransformationMethod(null); //set button to lowercase
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //addTask.setTransformationMethod(null); //set button to lowercase
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateDesc();

            }
        });

    }

    private void checkSwitch() {

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    time.setTextColor(Color.GRAY);
                    time.setClickable(false);
                    timeAllDay.setTextColor(Color.BLUE);

                    editor.putBoolean("allDay", true);
                    editor.apply();

                } else { //ALL DAY not checked...
                    time.setTextColor(Color.BLUE);
                    time.setClickable(true);

                    timeAllDay.setTextColor(Color.GRAY);
                    editor.putBoolean("allDay", false);
                    editor.apply();

                }
            }
        });
    }

    private void ValidateDesc() {

        String descValidate = desc.getEditText().getText().toString().trim();

        if (descValidate.isEmpty()) {
            desc.setError("Field can't be empty");

        } else {
            addItem(); //adds the card to the main screen (and db)
            finish(); //closes popup
        }
    }

    private void setDisplayMetics() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.8));


    } //sets the popup size

    private void getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        date.setText(currentDate);

        //todo formant. short
        DateFormat timeDF = new SimpleDateFormat("HH:mm");
        String currentTime = timeDF.format(calendar.getTime());


        String currentTime2 = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        time.setText(currentTime2);

    } //uses Calendar to set current date and time to TextViews (in popup)

    private void chooseDate() {

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });
    } //opens date picker

    private void chooseTime() {

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
    } //opens time picker

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        notificationDate = calendar.getTimeInMillis();

        String selectedDateString = DateFormat.getDateInstance().format(calendar.getTime());
        date.setText(selectedDateString); //sets the date field to selected date from calendar

    } //sets date

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay); //gets hour of day from the time picker
        calendar.set(Calendar.MINUTE, minute); //gets minute from the time picker
        notificationTime = calendar.getTimeInMillis();

        String selectedTimeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        time.setText(selectedTimeString); //sets the date field to selected date from calendar



    } //sets time textView as selected time using timePicker

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.popup_buttonBlue:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_checked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                colour = "blue";
                break;

            case R.id.popup_buttonOrange:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_checked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                colour = "orange";
                break;

            case R.id.popup_buttonGreen:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_checked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                colour = "green";
                break;

            case R.id.popup_buttonRed:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_checked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                colour = "red";
                break;

            case R.id.popup_buttonPurple:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_checked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                colour = "purple";
                break;

            case R.id.popup_buttonPeach:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_checked);
                colour = "peach";
                break;

        }
    } //control colour selection here

    private void addItem() {

        //converts editText fields to strings so they can be inserted into db.
        final String titleString = desc.getEditText().getText().toString();
        final String dateString = date.getText().toString();
        String timeString = time.getText().toString();

        notificationTitle = titleString;

        //FIREBASE DB SAVING STARTS HERE
        Boolean allDay = prefs.getBoolean("allDay" ,false);

        if (allDay) {
            timeString = "All day";
        } else {
            //set time to user defined time
        }

        //uses our custom Reminder class to easily add the "reminder" to Cloud Firestore
        Reminder reminder = new Reminder(titleString, dateString, timeString, colour);

        userID = user.getUid();
        db.collection(userID).document().set(reminder) //document id is auto generated

                .addOnSuccessListener(new OnSuccessListener<Void>() { //if upload to Firebase db was successful...
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccessUpload: " + titleString);
                    }
                })

                .addOnFailureListener(new OnFailureListener() { //if upload to Firebase db was unsuccessful...
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        Log.d(TAG, "Error: " + e.toString()); //tells us the error
                    }
                });






        startAlarm();



        //todo notifications start here
        //sendOnChannel1();




    } //end of addItem

    private void startAlarm() {
        String time = String.valueOf(notificationTime);
        String date = String.valueOf(notificationDate);

        Toast.makeText(this, "time: " + time, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "time: " + date, Toast.LENGTH_SHORT).show();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0); //todo request code must be unique for eqch pending intent !
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
    }

    public void sendOnChannel1() {

        Notification notification = new NotificationCompat.Builder(this, Cascade.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_chat_bubble_black) //todo change notification icon
                .setContentTitle(notificationTitle) //todo change notification title
                .setContentText("Here is your reminder.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();

        notificationManager.notify(1, notification);

    } //sends a notification when reminder is created

}
