package anton25360.github.com.cascade2.Popups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.uniquestudio.library.CircleCheckBox;

import anton25360.github.com.cascade2.Classes.Reminder;
import anton25360.github.com.cascade2.Classes.Tab;
import anton25360.github.com.cascade2.Classes.TabHolder;
import anton25360.github.com.cascade2.Login.LoginActivity;
import anton25360.github.com.cascade2.MainActivity;
import anton25360.github.com.cascade2.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import static anton25360.github.com.cascade2.MainActivity.*;

public class EditTask extends Activity{

    private static final String TAG = "EditTask";

    //todo use butterknife throughout app, dont forget to init in onCreate

    @BindView(R.id.edit_background) ImageView background;
    @BindView(R.id.edit_title) TextView titleField;
    @BindView(R.id.edit_date) TextView dateField;
    @BindView(R.id.edit_time) TextView timeField;

    @BindView(R.id.edit_rv)RecyclerView mRecyclerView;
    @BindView(R.id.edit_input)TextInputEditText mInput;
    @BindView(R.id.edit_inputSend)Button mSend;
    @BindView(R.id.edit_delete)Button mDelete;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public static FirestoreRecyclerAdapter adapter;
    String title, date, time, note, colour; //todo delete "note"

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
    }

    private void createAdaper() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userID = user.getUid();
        String docID_collection = docID + "collection";

        final Query query = FirebaseFirestore.getInstance()
                .collection("Cascade").document(" " + userID).collection("reminders").document(docID).collection(docID_collection)
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
                            Toast.makeText(EditTask.this, "checked", Toast.LENGTH_SHORT).show();
                            checked = true;

                        } else {
                            Toast.makeText(EditTask.this, "unchecked", Toast.LENGTH_SHORT).show();
                            checked = false;
                        }


                        Tab tab = new Tab(tabID, checked); //uses our custom Tab class to easily add the item to db.
                        String docID_collection = docID + "collection";
                        db.collection("Cascade").document(" " + userID).collection("reminders").document(docID).collection(docID_collection).document(snapshotID).set(tab);
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

        } else {
            background.setBackgroundResource(R.drawable.gradient_cascade);
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