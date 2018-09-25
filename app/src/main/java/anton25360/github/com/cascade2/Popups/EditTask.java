package anton25360.github.com.cascade2.Popups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import anton25360.github.com.cascade2.Classes.Reminder;
import anton25360.github.com.cascade2.Classes.Tab;
import anton25360.github.com.cascade2.Classes.TabHolder;
import anton25360.github.com.cascade2.MainActivity;
import anton25360.github.com.cascade2.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import static anton25360.github.com.cascade2.MainActivity.*;

public class EditTask extends Activity implements View.OnClickListener{

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
    String title, date, time, colour;
    Dialog mDialog;
    TextInputEditText titleEdit;
    Button mAdd, mBlue, mOrange, mGreen, mRed, mPurple, mPeach;
    boolean hasAlarm;

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
        Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show();

        //todo finish edit dialog

        mDialog.setContentView(R.layout.popup_beta);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //makes bg transparent
        mDialog.show();

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); //width + height
        layoutParams.gravity = Gravity.BOTTOM; //anchors the popup to the bottom of the screen
        window.setAttributes(layoutParams); //set changes

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

                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
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


    }

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

    }

    private void setBackground() {

        if (colour.equals("blue")) { //blue
            background.setBackgroundResource(R.drawable.gradient_blue_bg);

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


    }

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

    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

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

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
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


            case R.id.popup_addTask:

                String bool = Boolean.toString(hasAlarm);
                Toast.makeText(this, bool, Toast.LENGTH_SHORT).show();
                break;

            /*...

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

                    mDialog.dismiss(); //closes popup
                }
                break;

                ...*/
        }
    } //control popup colour selection here

}