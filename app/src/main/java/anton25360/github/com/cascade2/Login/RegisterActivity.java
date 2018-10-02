package anton25360.github.com.cascade2.Login;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import anton25360.github.com.cascade2.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.registerEmail) TextInputLayout inputEmail;
    @BindView(R.id.registerPassword) TextInputLayout inputPassword;
    @BindView(R.id.registerButton) Button register;
    @BindView(R.id.registerLogin) TextView login;
    @BindView(R.id.registerProgress) ProgressBar progressBar;

    private String email, password;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        login.setOnClickListener(v -> startLogin());

        progressBar.setVisibility(View.INVISIBLE);
        register.setTransformationMethod(null);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputEmail. getEditText().getText().toString();
                password = inputPassword.getEditText().getText().toString();

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Field can't be empty");
                    return;
                } else {
                    inputEmail.setError(null);
                }

                if (TextUtils.isEmpty(password)) {
                    inputPassword.setError("Field can't be empty");
                    return;
                } else {
                    inputPassword.setError(null);
                }

                progressBar.setVisibility(View.VISIBLE);
                RegisterNewUser();
            }

            private void RegisterNewUser() {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);

                                startLogin(); //goes to login tab so user can login

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Account creation failed", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        });
    }

    private void startLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
