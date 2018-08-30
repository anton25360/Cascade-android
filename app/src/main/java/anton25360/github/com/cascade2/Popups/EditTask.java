package anton25360.github.com.cascade2.Popups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import anton25360.github.com.cascade2.R;
import anton25360.github.com.cascade2.Reminder;
import anton25360.github.com.cascade2.ReminderHolder;
import anton25360.github.com.cascade2.Tab;
import anton25360.github.com.cascade2.TabHolder;
import butterknife.BindView;
import butterknife.ButterKnife;

import static anton25360.github.com.cascade2.MainActivity.*;

public class EditTask extends Activity{

    private static final String TAG = "EditTask";

    //todo use butterknife throughout app, dont forget to init in onCreate
    //bind all views here
    @BindView(R.id.edit_btnEdit) Button edit;
    @BindView(R.id.edit_btnDelete) Button delete;
    @BindView(R.id.edit_background) ImageView background;
    @BindView(R.id.edit_title) TextView titleField;
    @BindView(R.id.edit_date) TextView dateField;
    @BindView(R.id.edit_time) TextView timeField;
    @BindView(R.id.edit_checkbox)CheckBox mCheckBox;

    @BindView(R.id.edit_rv)RecyclerView mRecyclerView;
    @BindView(R.id.edit_rvInputString)TextInputEditText mInput;
    @BindView(R.id.edit_rvSend)ImageButton mSend;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public static FirestoreRecyclerAdapter adapter;
    String title, date, time, note, colour;

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

        edit.setTransformationMethod(null); //sets button text to lowercase
        delete.setTransformationMethod(null); //sets button text to lowercase

        getDocInfo(); //gets title, date, and time from doc (in db)
        ButtonCode(); //controls onclicklisteners
        createAdaper(); // creates adapter

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
    }

    private void createAdaper() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userID = user.getUid();
        String docID_collection = docID + "collection";

        final Query query = FirebaseFirestore.getInstance()
                .collection("Cascade").document(" " + userID).collection("reminders").document(docID).collection(docID_collection)
                .orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Tab> options = new FirestoreRecyclerOptions.Builder<Tab>()
                .setQuery(query, Tab.class)
                .build();

        //create new FirestoreRecyclerAdapter:
        adapter = new FirestoreRecyclerAdapter<Tab, TabHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final TabHolder holder, final int position, @NonNull Tab model) {
                holder.bind(model);

                //checkbox
                holder.itemView.findViewById(R.id.tab_checkbox).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CheckBox checkBox = holder.itemView.findViewById(R.id.tab_checkbox);
                        boolean isChecked;
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        String tabID = snapshot.getString("title");
                        String snapshotID = snapshot.getId();
                        String userID = user.getUid();

                        if (checkBox.isChecked()) {
                            isChecked = true;
                        } else {
                            isChecked = false;
                        }

                        Tab tab = new Tab(tabID, isChecked); //uses our custom Tab class to easily add the item to db.
                        String docID_collection = docID + "collection";
                        db.collection("Cascade").document(" " + userID).collection("reminders").document(docID).collection(docID_collection).document(snapshotID).set(tab); //Because document parameter is empty, Firebase auto generates the document id (So reminders don't get overwritten)
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

        db.collection("Cascade").document(" " + userID).collection("reminders")
                .document(docID) //gets docID from MainActivity (when card is first clicked)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        title = documentSnapshot.getString("title");
                        date = documentSnapshot.getString("date");
                        time = documentSnapshot.getString("time");
                        note = documentSnapshot.getString("note");
                        colour = documentSnapshot.getString("colour");

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
            background.setBackgroundResource(R.drawable.gradient_blue);

        } else if (colour.equals("orange")) { //orange
            background.setBackgroundResource(R.drawable.gradient_orange);

        } else if (colour.equals("green")) { //green
            background.setBackgroundResource(R.drawable.gradient_green);

        } else if (colour.equals("red")) { //red
            background.setBackgroundResource(R.drawable.gradient_red);

        } else if (colour.equals("purple")) { //purple
            background.setBackgroundResource(R.drawable.gradient_purple);

        } else if (colour.equals("peach")) { //peach
            background.setBackgroundResource(R.drawable.gradient_peach);

        } else {
            background.setBackgroundResource(R.drawable.gradient_cascade);
        } //blank
        
        
    }

    private void ButtonCode() {

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditReminder();
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteReminder();
                finish();
            }
        });
    } //controls onclicklisteners

    private void DeleteReminder() {

        String userID = user.getUid();

        db.collection("Cascade").document(" " + userID).collection("reminders")
                .document(docID) //gets docID from MainActivity (when card is first clicked)
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

    private void EditReminder() {
        //apply edits

        String userID = user.getUid();

        final DocumentReference documentReference = db.collection("Cascade").document(" " + userID).collection("reminders").document(docID); //gets docID from MainActivity (when card is first clicked)

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String title = titleField.getText().toString();
                        //String date = dateField.toString();
                        //String time = timeField.toString();

                        Reminder reminder = new Reminder(title, date, time, colour);

                        documentReference.set(reminder);

                        Log.d(TAG, "onSuccess: edit");


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                         Log.d(TAG, "onFailure: "+ e.getLocalizedMessage());
                    }
                });

    }

    private void addItem() {

        final String titleString = mInput.getText().toString(); //converts editText field to string so it can be inserted into db.
        final String userID = user.getUid();

        //FIREBASE DB SAVING STARTS HERE

        Tab tab = new Tab(titleString, false); //uses our custom Tab class to easily add the item to db. (checkbox is unchecked by default)
        String docID_collection = docID + "collection";

        db.collection("Cascade").document(" " + userID).collection("reminders").document(docID).collection(docID_collection).document().set(tab) //Because document parameter is empty, Firebase auto generates the document id (So reminders don't get overwritten)

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
}
