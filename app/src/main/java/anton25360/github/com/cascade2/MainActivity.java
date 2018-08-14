package anton25360.github.com.cascade2;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import anton25360.github.com.cascade2.Login.LoginActivity;
import anton25360.github.com.cascade2.Login.ProfileFragment;
import anton25360.github.com.cascade2.Popups.EditTask;
import anton25360.github.com.cascade2.Popups.PopupFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    FloatingActionButton FAB;
    RecyclerView recyclerView;
    CardView cvItem;
    String userID, sortID;
    public static String docID;
    CoordinatorLayout mCoordinatorLayout;
    //SearchView mSearchView;

    //TODO DISABLE GOING BACK ON LOGOUT !!! (finish; ?)

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static FirestoreRecyclerAdapter adapter;


    @Override
    protected void onStart() {
        super.onStart();

        if (user != null) { //if user is logged in...

            adapter.startListening(); //connects to firebase collection
            adapter.notifyDataSetChanged();



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

        //sets the toolbar
        initToolbar();

        mCoordinatorLayout = findViewById(R.id.mainLayout);
        FAB = findViewById(R.id.btnFAB2);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openPopupFragment();
            }
        });

        initRecyclerView();

    }//end of onCreate

    private void openPopupFragment() {
        Intent intent = new Intent(this, PopupFragment.class);
        startActivity(intent);
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

        final Query query = FirebaseFirestore.getInstance()
                .collection("Cascade").document(" " + userID).collection("reminders")
                .orderBy(sortID, Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Reminder> options = new FirestoreRecyclerOptions.Builder<Reminder>()
                .setQuery(query, Reminder.class)
                .build();

        //create new FirestoreRecyclerAdapter:
        adapter = new FirestoreRecyclerAdapter<Reminder, ReminderHolder>(options) {
            @Override
            public void onBindViewHolder(final ReminderHolder holder, int position, final Reminder model) {

                holder.bind(model);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        openEditPopup();

                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        docID = snapshot.getId();

                    }
                });
                }


            @Override
            public ReminderHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item, group, false); //todo use beta item layout
                return new ReminderHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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


    //this bit controls the onclicks for the toolbar buttons (search and profile)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) { //gets button id

            case R.id.action_search: //if user clicks search button
                //todo implement search function
                return true;

            case R.id.action_profile: //if user clicks profile button
                openProfileTab();
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

    private void openProfileTab() {
        Intent intent = new Intent(this, ProfileFragment.class);
        startActivity(intent);
    }

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
}
