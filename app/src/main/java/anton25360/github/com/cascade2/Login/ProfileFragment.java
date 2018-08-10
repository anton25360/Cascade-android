package anton25360.github.com.cascade2.Login;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import anton25360.github.com.cascade2.R;

public class ProfileFragment extends AppCompatActivity {

    TextView username, useremail, logout;
    String name, email;
    public static String displayName;
    ImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_profile);

        getFirebaseUser();

        initViews();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LogUserOut();
            }
        });
    }

    private void getFirebaseUser() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Name, email address, and profile photo Url
        assert user != null;
        name = user.getDisplayName();
        email = user.getEmail();
        displayName = user.getDisplayName();

    }

    private void LogUserOut() {

        FirebaseAuth.getInstance().signOut(); //logs the user out
        Toast.makeText(ProfileFragment.this, "You have been logged out", Toast.LENGTH_SHORT).show(); //tells the user he is logged out
        openLoginFragment(); //opens the login activity so user can sign back in

    }

    private void openLoginFragment() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void initViews() {

        username = findViewById(R.id.txt_CardName);
        username.setText(name);

        useremail = findViewById(R.id.txt_CardEmail);
        useremail.setText(email);

        picture = findViewById(R.id.profile_picture);
        logout = findViewById(R.id.profile_logout);

        //username.setPaintFlags(username.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

    }
}