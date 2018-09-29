package anton25360.github.com.cascade2;

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
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.util.Calendar;

import anton25360.github.com.cascade2.Classes.AlarmReciever;
import anton25360.github.com.cascade2.Classes.Reminder;
import anton25360.github.com.cascade2.Classes.ReminderHolder;
import anton25360.github.com.cascade2.Classes.TabSub;
import anton25360.github.com.cascade2.Classes.TabSubHolder;
import anton25360.github.com.cascade2.DateTimeFragments.DatePickerFragment;
import anton25360.github.com.cascade2.DateTimeFragments.TimePickerFragment;
import anton25360.github.com.cascade2.Login.LoginActivity;
import anton25360.github.com.cascade2.Popups.EditTask;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    private static final String TAG = "MainActivity";

    private String colour = "";
    Button mAdd, mBlue, mOrange, mGreen, mRed, mPurple, mPeach, mSylvia, openEdit;
    FloatingActionButton FAB;
    RecyclerView recyclerView, recyclerViewSub;
    String userID, sortID, timeString, dateString;
    public static String docID, titleString;
    TextInputLayout title;
    Query query;
    Dialog dialogCreate, dialogLogout;
    Calendar calendar = Calendar.getInstance();
    TextView reminderText;
    Switch reminderSwitch;
    long alarmTime, alarmDate;
    boolean hasAlarm;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static FirestoreRecyclerAdapter adapter, adapterSub;


    @Override
    protected void onStart() {
        super.onStart();

        if (user != null) { //if user is logged in...

            adapter.startListening(); //connects to firebase collection
            Toast.makeText(this, "listening...", Toast.LENGTH_SHORT).show();

        } else {

            userID = null;
            openLogin(); //if user is not logged in, go to login / sign up page
        }
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        dialogCreate = new Dialog(this);
        dialogLogout = new Dialog(this);

        //sets the toolbar
        initToolbar();
        FAB = findViewById(R.id.btnFAB2);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openNewTaskDialog();
            }
        });

        initRecyclerView();

    }//end of onCreate

    private void openNewTaskDialog() {

        dialogCreate.setContentView(R.layout.dialog_create);
        dialogCreate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //makes bg transparent
        dialogCreate.show();

        Window window = dialogCreate.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //width + height
        layoutParams.gravity = Gravity.BOTTOM; //anchors the popup to the bottom of the screen
        window.setAttributes(layoutParams); //set changes

        mBlue = dialogCreate.findViewById(R.id.popup_buttonBlue);
        mOrange = dialogCreate.findViewById(R.id.popup_buttonOrange);
        mGreen = dialogCreate.findViewById(R.id.popup_buttonGreen);
        mRed = dialogCreate.findViewById(R.id.popup_buttonRed);
        mPurple = dialogCreate.findViewById(R.id.popup_buttonPurple);
        mPeach = dialogCreate.findViewById(R.id.popup_buttonPeach);
        mSylvia = dialogCreate.findViewById(R.id.popup_buttonSylvia);

        mBlue.setOnClickListener(this);
        mOrange.setOnClickListener(this);
        mGreen.setOnClickListener(this);
        mRed.setOnClickListener(this);
        mPurple.setOnClickListener(this);
        mPeach.setOnClickListener(this);
        mSylvia.setOnClickListener(this);

        mAdd = dialogCreate.findViewById(R.id.popup_addTask);
        mAdd.setTransformationMethod(null);
        mAdd.setOnClickListener(this);

        title = dialogCreate.findViewById(R.id.popup_titleInput);

        setReminderSwitch();

    } //opens dialog to create a new list

    private void setReminderSwitch() {

        reminderSwitch = dialogCreate.findViewById(R.id.popup_ReminderSwitch);
        reminderText = dialogCreate.findViewById(R.id.popup_ReminderText);
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
        reminderText.setText(dateString + " at " + timeString); //sets the date field to selected date from calendar //todo replate @ symbol

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay); //gets hour of day from the time picker
        calendar.set(Calendar.MINUTE, minute); //gets minute from the time picker
        alarmTime = calendar.getTimeInMillis();

        timeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()); //for the reminder set for the... string
        reminderText.setText(dateString + " at " + timeString); //sets the date field to selected date from calendar //todo replate @ symbol

    }

    private void initRecyclerView() { //init rv
        recyclerView = findViewById(R.id.rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sortID = PreferenceManager.getDefaultSharedPreferences(this).getString("sortID", "colour");

        if (user != null) {
            userID = user.getUid();
        } else {
            userID = "paramount";
        }

        query = FirebaseFirestore.getInstance()
                .collection(userID)
                .orderBy(sortID, Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Reminder> options = new FirestoreRecyclerOptions.Builder<Reminder>()
                .setQuery(query, Reminder.class)
                .build();

        //create new FirestoreRecyclerAdapter:
        adapter = new FirestoreRecyclerAdapter<Reminder, ReminderHolder>(options) {
            @Override
            public void onBindViewHolder(final ReminderHolder holder, int position, final Reminder model) {

                DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                docID = snapshot.getId();

                openEdit = holder.itemView.findViewById(R.id.btnOpenEdit);
                openEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        openEditPopup();

                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        docID = snapshot.getId();
                    }
                });

                holder.bind(model);

                recyclerViewSub = holder.itemView.findViewById(R.id.widget_rvSub);

                createSubAdaper();
                recyclerViewSub.setAdapter(adapterSub);
                adapterSub.startListening();
                adapterSub.notifyDataSetChanged();

            }

            @Override
            public ReminderHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item, group, false);
                return new ReminderHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

    } //initialises the recyclerView and FirebaseRecyclerAdapter

    private void initToolbar() {
        //sets the custom toolbar + title
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cascade"); //set toolbar title
        toolbar.setTitleTextColor(0xFFFFFFFF); //set toolbar colour in UJML to white(0xFFFFFFFF) OR black(0xFF000000 )
    } //initialises the toolbar

    //shows the buttons on toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_buttons, menu);
        return true;
    }

    //this bit controls the onclicks for the toolbar buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) { //gets button id

            case R.id.action_profile: //if user clicks profile button
                openProfileDialog();
                return true;

            case R.id.menu_sortByColour:
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("sortID", "colour").apply();
                initRecyclerView(); //re-creates the adapter with the new Query
                adapter.startListening();
                return true;

            case R.id.menu_sortByDate:
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("sortID", "date").apply();
                initRecyclerView(); //re-creates the adapter with the new Query
                adapter.startListening();
                return true;

            case R.id.menu_sortByAlphabet:
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("sortID", "title").apply();
                initRecyclerView(); //re-creates the adapter with the new Query
                adapter.startListening();
                return true;

            default: //default message below
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void openProfileDialog() {

        dialogLogout.setContentView(R.layout.dialog_confirm_logout);
        dialogLogout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //makes bg transparent
        dialogLogout.show();

        Window window = dialogLogout.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //width + height
        layoutParams.gravity = Gravity.BOTTOM; //anchors the popup to the bottom of the screen
        window.setAttributes(layoutParams); //set changes

        String email = user.getEmail();
        TextView textView = dialogLogout.findViewById(R.id.logoutText);
        textView.setText("Signed in as " + email);

        Button logout = dialogLogout.findViewById(R.id.logoutButton);
        logout.setTransformationMethod(null);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut(); //logs the user out
                finish();
                openLogin();

            }
        });

        //todo cancel dialogs padding
        TextView cancel = dialogLogout.findViewById(R.id.cancelLogout);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogLogout.dismiss();
            }
        });

    } //opens a profile dialog

    private void openEditPopup() {

        Intent intent = new Intent(this, EditTask.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }

    } //onStop

    private void createSubAdaper() {

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewSub.setLayoutManager(layoutManager);

        String userID = user.getUid();

        final Query query = FirebaseFirestore.getInstance()
                .collection(userID).document(docID).collection(docID + "collection")
                .orderBy("checked", Query.Direction.ASCENDING)
                .whereEqualTo("checked", false); //to limit number of subtasks, use .limit(3)

        FirestoreRecyclerOptions<TabSub> options = new FirestoreRecyclerOptions.Builder<TabSub>()
                .setQuery(query, TabSub.class)
                .build();

        //create new FirestoreRecyclerAdapter: (SUB)
        adapterSub = new FirestoreRecyclerAdapter<TabSub, TabSubHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TabSubHolder holder, final int position, @NonNull TabSub model) {
                holder.bind(model);
            }

            @Override
            public TabSubHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tab_sub, group, false);
                return new TabSubHolder(view);
            }
        };

        Log.d(TAG, "adapterSub: created");
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.popup_buttonBlue:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_checked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                mSylvia.setBackgroundResource(R.drawable.gradient_sylvia_unchecked);
                colour = "blue";
                break;

            case R.id.popup_buttonOrange:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_checked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                mSylvia.setBackgroundResource(R.drawable.gradient_sylvia_unchecked);
                colour = "orange";
                break;

            case R.id.popup_buttonGreen:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_checked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                mSylvia.setBackgroundResource(R.drawable.gradient_sylvia_unchecked);
                colour = "green";
                break;

            case R.id.popup_buttonRed:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_checked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                mSylvia.setBackgroundResource(R.drawable.gradient_sylvia_unchecked);
                colour = "red";
                break;

            case R.id.popup_buttonPurple:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_checked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                mSylvia.setBackgroundResource(R.drawable.gradient_sylvia_unchecked);
                colour = "purple";
                break;

            case R.id.popup_buttonPeach:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_checked);
                mSylvia.setBackgroundResource(R.drawable.gradient_sylvia_unchecked);
                colour = "peach";
                break;

            case R.id.popup_buttonSylvia:
                mBlue.setBackgroundResource(R.drawable.gradient_blue_unchecked);
                mOrange.setBackgroundResource(R.drawable.gradient_orange_unchecked);
                mGreen.setBackgroundResource(R.drawable.gradient_green_unchecked);
                mRed.setBackgroundResource(R.drawable.gradient_red_unchecked);
                mPurple.setBackgroundResource(R.drawable.gradient_purple_unchecked);
                mPeach.setBackgroundResource(R.drawable.gradient_peach_unchecked);
                mSylvia.setBackgroundResource(R.drawable.gradient_sylvia_checked);
                colour = "sylvia";
                break;

            case R.id.popup_addTask:

                if (hasAlarm) {
                    setAlarm();
                } else {
                    //no alarm set
                }

                titleString = title.getEditText().getText().toString().trim(); //gets title from editText

                if (titleString.isEmpty()) {
                    title.setError("Field can't be empty");

                } else {

                    //adds the card to the main screen (and db)

                    Reminder reminder = new Reminder(titleString, dateString, timeString, colour, hasAlarm);

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

                    dialogCreate.dismiss(); //closes popup
                }
                break;
        }
    } //control popup colour selection here

    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

    }

}
