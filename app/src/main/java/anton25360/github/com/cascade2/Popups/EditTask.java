package anton25360.github.com.cascade2.Popups;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import butterknife.BindView;
import butterknife.ButterKnife;

import static anton25360.github.com.cascade2.MainActivity.*;

public class EditTask extends Activity{

    private static final String TAG = "EditTask";

    //todo use butterknife throughout app, dont forget to init in onCreate
    //bind all views here
    @BindView(R.id.edit_btnEdit) Button edit;
    @BindView(R.id.edit_btnDelete) Button delete;
    @BindView(R.id.edit_descriptionSET) TextInputEditText titleField;
    @BindView(R.id.edit_noteSET) TextInputEditText noteField;
    @BindView(R.id.edit_date) TextView dateField;
    @BindView(R.id.edit_time) TextView timeField;
    @BindView(R.id.edit_checkbox)CheckBox mCheckBox;
    //@BindView(R.id.edit_palette) SpectrumPalette mPalette;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String title, date, time, note;
    boolean hasNote;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_edit);
        ButterKnife.bind(this);

        //mPalette.setColors(getResources().getIntArray(R.array.rainbow));

        //sets button text to lowercase
        edit.setTransformationMethod(null);
        delete.setTransformationMethod(null);

        getDocInfo(); //gets title, date, and time from doc (in db)
        setDisplayMetics();
        ButtonCode(); //controls onclicklisteners
        getHasNote();

    }

    private void getHasNote() {
        if (TextUtils.isEmpty(noteField.toString())) {
            hasNote = false;
        } else {
            hasNote = true;
        }
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

                        titleField.setText(title);
                        dateField.setText(date);
                        timeField.setText(time);
                        noteField.setText(note);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+ e.getLocalizedMessage());
                    }
                });

    }

    private void setDisplayMetics() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.8));

    } //sets the popup size

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
                        String note = noteField.getEditableText().toString();
                        String colour = "";

                        Reminder reminder = new Reminder(title, date, time, note, hasNote, colour);

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
}
