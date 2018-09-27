package anton25360.github.com.cascade2.Popups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.uniquestudio.library.CircleCheckBox;

import java.text.DateFormat;
import java.util.Calendar;

import anton25360.github.com.cascade2.Classes.AlarmReciever;
import anton25360.github.com.cascade2.Classes.Reminder;
import anton25360.github.com.cascade2.Classes.Tab;
import anton25360.github.com.cascade2.Classes.TabHolder;
import anton25360.github.com.cascade2.DateTimeFragments.DatePickerFragment;
import anton25360.github.com.cascade2.DateTimeFragments.TimePickerFragment;
import anton25360.github.com.cascade2.MainActivity;
import anton25360.github.com.cascade2.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import static anton25360.github.com.cascade2.MainActivity.*;

public class EditTask extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    private static final String TAG = "EditTask";

    @BindView(R.id.edit_background) ImageView background;
    @BindView(R.id.edit_title) TextView titleField;
    @BindView(R.id.edit_date) TextView dateField;
    @BindView(R.id.edit_time) TextView timeField;

    @BindView(R.id.edit_rv)RecyclerView mRecyclerView;
    @BindView(R.id.edit_input)TextInputEditText mInput;
    @BindView(R.id.edit_inputSend)Button mSend;
    @BindView(R.id.edit_delete)Button mDelete;
    @BindView(R.id.edit_edit)Button mEdit;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public static FirestoreRecyclerAdapter adapter;
    String title, date, time, colour, userID, dateString, timeString; //xxxString used for time & datepickers. (others used for cloud import)
    Dialog mDialog;
    TextInputEditText titleEdit;
    Button mAdd, mBlue, mOrange, mGreen, mRed, mPurple, mPeach;
    boolean hasAlarm;
    long alarmTime, alarmDate;
    TextView reminderText;
    Switch reminderSwitch;
    Calendar calendar  = Calendar.getInstance();
    DocumentSnapshot snapshot;

    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening(); //connects to firebase collection
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_edit);
        ButterKnife.bind(this);

        mDialog = new Dialog(this);
        getDocInfo(); //gets title, date, and time from doc (in db)
        createAdaper(); // creates adapter

        mSend.setTransformationMethod(null);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInput.length() == 0) {
                    //do nothing
                } else {
                    addItem(); //adds the item to the sub rv
                }
            }
        });

        mDelete.setTransformationMethod(null);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo task edit


                openEditDialog();
            }
        });
    }

    private void openEditDialog() {

        //todo finish edit dialog

        mDialog.setContentView(R.layout.popup_beta);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //makes bg transparent
        mDialog.show();

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //width + height
        layoutParams.gravity = Gravity.BOTTOM; //anchors the popup to the bottom of the screen
        window.setAttributes(layoutParams); //set changes

        setButtonsBackground();
        setReminderSwitch();

        /*reminderText = mDialog.findViewById(R.id.popup_ReminderText);
        reminderSwitch = mDialog.findViewById(R.id.popup_ReminderSwitch);
        if (hasAlarm) {
            reminderSwitch.setChecked(true);
            reminderText.setText("Reminder set for " + date + " @ " + time); //sets the date field to selected date from calendar //todo replate @ symbol

        } else {
            reminderText.setText("No Reminder set");
            reminderSwitch.setChecked(false);
        }*/

        mBlue = mDialog.findViewById(R.id.popup_buttonBlue);
        mOrange = mDialog.findViewById(R.id.popup_buttonOrange);
        mGreen = mDialog.findViewById(R.id.popup_buttonGreen);
        mRed = mDialog.findViewById(R.id.popup_buttonRed);
        mPurple = mDialog.findViewById(R.id.popup_buttonPurple);
        mPeach = mDialog.findViewById(R.id.popup_buttonPeach);

        mBlue.setOnClickListener(this);
        mOrange.setOnClickListener(this);
        mGreen.setOnClickListener(this);
        mRed.setOnClickListener(this);
        mPurple.setOnClickListener(this);
        mPeach.setOnClickListener(this);

        mAdd = mDialog.findViewById(R.id.popup_addTask);
        mAdd.setTransformationMethod(null);
        mAdd.setText("Done"); //todo change this text?
        mAdd.setOnClickListener(this);

        titleEdit = mDialog.findViewById(R.id.popup_titleInputSetTitle);
        titleEdit.setText(title);

    } //open the edit dialog

    private void setReminderSwitch() {

        reminderSwitch = mDialog.findViewById(R.id.popup_ReminderSwitch);
        reminderText = mDialog.findViewById(R.id.popup_ReminderText);

        if (hasAlarm) {
            reminderSwitch.setChecked(true);
            reminderText.setText("Reminder set for " + date + " @ " + time); //sets the date field to selected date from calendar //todo replate @ symbol

        } else {
            reminderText.setText("No Reminder set");
            reminderSwitch.setChecked(false);
        }


        dateString = DateFormat.getDateInstance().format(calendar.getTime()); //uses Calendar to set current date (default if user chooses no reminder)
        timeString = "";
        //cancelAlarm(); //set no alarm by default

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    hasAlarm = true;

                    //open TimePicker
                    DialogFragment timePicker = new TimePickerFragment();
                    timePicker.show(getSupportFragmentManager(), "time picker");

                    //open DatePicker
                    DialogFragment datePicker = new DatePickerFragment();
                    datePicker.show(getSupportFragmentManager(), "date picker");

                } else {
                    hasAlarm = false;
                    reminderText.setText("No Reminder set");
                }
            }
        });

    } //controls dialog's reminder (using Date & Time picker)

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        alarmDate = calendar.getTimeInMillis();

        dateString = DateFormat.getDateInstance().format(calendar.getTime());
        reminderText.setText("Reminder set for " + dateString + " @ " + timeString); //sets the date field to selected date from calendar //todo replate @ symbol

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay); //gets hour of day from the time picker
        calendar.set(Calendar.MINUTE, minute); //gets minute from the time picker
        alarmTime = calendar.getTimeInMillis();

        timeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()); //for the reminder set for the... string
        reminderText.setText("Reminder set for " + dateString + " @ " + timeString); //sets the date field to selected date from calendar //todo replate @ symbol

    }

    private void createAdaper() {


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userID = user.getUid();

        final Query query = FirebaseFirestore.getInstance()
                .collection(userID).document(docID).collection(docID + "collection") //gets all the documents (or "tabs")
                .orderBy("checked", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Tab> options = new FirestoreRecyclerOptions.Builder<Tab>()
                .setQuery(query, Tab.class)
                .build();

        //create new FirestoreRecyclerAdapter:
        adapter = new FirestoreRecyclerAdapter<Tab, TabHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TabHolder holder, final int position, @NonNull Tab model) {
                holder.bind(model);


                //checkbox
                final CircleCheckBox checkBox = holder.itemView.findViewById(R.id.tab_checkBox);
                checkBox.setListener(new CircleCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(boolean isChecked) {

                        snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        String tabID = snapshot.getString("title");
                        String snapshotID = snapshot.getId();
                        String userID = user.getUid();
                        boolean checked;

                        if (checkBox.isChecked()){
                            checked = true;

                        } else {
                            checked = false;
                        }


                        Tab tab = new Tab(tabID, checked); //uses our custom Tab class to easily add the item to db.
                        db.collection(userID).document(docID).collection(docID + "collection").document(snapshotID).set(tab);
                    }
                });


            }

            @Override
            public TabHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tab, group, false);
                return new TabHolder(view);
            }




        };

        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //todo ith here?



    } //creates adapter for subtasks

    private void getDocInfo() {

        String userID = user.getUid();

        db.collection(userID).document(docID) //gets docID from MainActivity (when card is first clicked)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        title = documentSnapshot.getString("title");
                        date = documentSnapshot.getString("date");
                        time = documentSnapshot.getString("time");
                        colour = documentSnapshot.getString("colour");
                        hasAlarm = documentSnapshot.getBoolean("hasAlarm");


                        titleField.setText(title);
                        dateField.setText(date);
                        timeField.setText(time);

                        setBackground();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+ e.getLocalizedMessage());
                    }
                });

    } //get info frm cloud to name the list attributes (title, date...)

    private void setBackground() {

        if (colour.equals("blue")) { //blue
            background.setBackgroundResource(R.drawable.gradient_blue_bg); //BLUE IS CHECKED BY DEFAULT

        } else if (colour.equals("orange")) { //orange
            background.setBackgroundResource(R.drawable.gradient_orange_bg);

        } else if (colour.equals("green")) { //green
            background.setBackgroundResource(R.drawable.gradient_green_bg);

        } else if (colour.equals("red")) { //red
            background.setBackgroundResource(R.drawable.gradient_red_bg);

        } else if (colour.equals("purple")) { //purple
            background.setBackgroundResource(R.drawable.gradient_purple_bg);

        } else if (colour.equals("peach")) { //peach
            background.setBackgroundResource(R.drawable.gradient_peach_bg);

        } else { //default is blue
            background.setBackgroundResource(R.drawable.gradient_blue_bg);
        } //blank


    } //set initial bg colour

    private void setButtonsBackground() {

        if (colour.equals("orange")) { //orange
            Button button = mDialog.findViewById(R.id.popup_buttonOrange);
            button.setBackgroundResource(R.drawable.gradient_orange_checked);

            Button blue = mDialog.findViewById(R.id.popup_buttonBlue);
            blue.setBackgroundResource(R.drawable.gradient_blue_unchecked);

        } else if (colour.equals("green")) { //green
            Button button = mDialog.findViewById(R.id.popup_buttonGreen);
            button.setBackgroundResource(R.drawable.gradient_green_checked);

            Button blue = mDialog.findViewById(R.id.popup_buttonBlue);
            blue.setBackgroundResource(R.drawable.gradient_blue_unchecked);

        } else if (colour.equals("red")) { //red
            Button button = mDialog.findViewById(R.id.popup_buttonRed);
            button.setBackgroundResource(R.drawable.gradient_red_checked);

            Button blue = mDialog.findViewById(R.id.popup_buttonBlue);
            blue.setBackgroundResource(R.drawable.gradient_blue_unchecked);

        } else if (colour.equals("purple")) { //purple
            Button button = mDialog.findViewById(R.id.popup_buttonPurple);
            button.setBackgroundResource(R.drawable.gradient_purple_checked);

            Button blue = mDialog.findViewById(R.id.popup_buttonBlue);
            blue.setBackgroundResource(R.drawable.gradient_blue_unchecked);

        } else if (colour.equals("peach")) { //peach
            Button button = mDialog.findViewById(R.id.popup_buttonPeach);
            button.setBackgroundResource(R.drawable.gradient_peach_checked);

            Button blue = mDialog.findViewById(R.id.popup_buttonBlue);
            blue.setBackgroundResource(R.drawable.gradient_blue_unchecked);

        } else { //default is blue
            background.setBackgroundResource(R.drawable.gradient_blue_bg);
        } //blank


    } //set dialog buttons colour

    private void deleteTask() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                openMainActivity();

                String userID = user.getUid();
                db.collection(userID).document(docID) //gets docID from MainActivity (when card is first clicked)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: deleted");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Error: " + e.getLocalizedMessage());
                            }
                        });
                adapter.notifyDataSetChanged();



            }
        });
        builder.setNegativeButton("No", null);
        builder.show();

    } //delete entire list

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    } //go to main activity

    private void addItem() {

        final String titleString = mInput.getText().toString(); //converts editText field to string so it can be inserted into db.
        final String userID = user.getUid();

        //FIREBASE DB SAVING STARTS HERE

        Tab tab = new Tab(titleString, false); //uses our custom Tab class to easily add the item to db. (checkbox is unchecked by default)

        db.collection(userID).document(docID).collection(docID + "collection").document().set(tab)

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: " + titleString);
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error: " + e.toString());
                    }
                });

        mInput.getText().clear(); //clear edittext field

    } //adds subtask

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    } //onStop activity

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
                background.setBackgroundResource(R.drawable.gradient_blue_bg);
                break;

            case R.id.popup_buttonOrange:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_checked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                colour = "orange";
                background.setBackgroundResource(R.drawable.gradient_orange_bg);
                break;

            case R.id.popup_buttonGreen:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_checked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                colour = "green";
                background.setBackgroundResource(R.drawable.gradient_green_bg);
                break;

            case R.id.popup_buttonRed:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_checked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                colour = "red";
                background.setBackgroundResource(R.drawable.gradient_red_bg);
                break;

            case R.id.popup_buttonPurple:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_checked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                colour = "purple";
                background.setBackgroundResource(R.drawable.gradient_purple_bg);
                break;

            case R.id.popup_buttonPeach:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_checked);
                colour = "peach";
                background.setBackgroundResource(R.drawable.gradient_peach_bg);
                break;

            //...

            case R.id.popup_addTask:

                if (hasAlarm) {
                    setAlarm();
                    dateField.setText(dateString);
                    timeField.setText(timeString);
                } else {
                    //no alarm set
                    timeField.setText("");
                }

                title = titleEdit.getText().toString().trim(); //gets title from editText
                MainActivity.titleString = title;

                date = dateString;
                time = timeString;

                if (title.isEmpty()) {
                    titleEdit.setError("Field can't be empty");

                } else {

                    Reminder reminder = new Reminder(title, date, time, colour, hasAlarm);

                    userID = user.getUid();
                    db.collection(userID).document(docID).set(reminder) //update fields

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

                    mDialog.dismiss(); //closes popup

                    titleField.setText(title);
                    //todo update all fields


                }
                break;

                //...
        }
    } //control popup colour selection here + edit commits

    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

    }



}