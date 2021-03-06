package anton25360.github.com.cascade2.Login;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
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

import anton25360.github.com.cascade2.MainActivity;
import anton25360.github.com.cascade2.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.loginEmail) TextInputLayout inputEmail;
    @BindView(R.id.loginPassword) TextInputLayout inputPassword;
    @BindView(R.id.loginButton) Button loginEmail;
    @BindView(R.id.loginSignUp) TextView signUp;
    @BindView(R.id.loginTitle) TextView title;
    @BindView(R.id.loginLayout) ConstraintLayout layout;
    @BindView(R.id.loginForgottenPassword) TextView forgottenPW;
    @BindView(R.id.loginProgress) ProgressBar progressBar;

    private String email, password;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); //firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        signUp.setOnClickListener(v -> startRegister());

        forgottenPW.setOnClickListener(v -> startPasswordReset());

        progressBar.setVisibility(View.INVISIBLE);
        loginEmail.setTransformationMethod(null); //forces button font to not be uppercase
        loginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputEmail.getEditText().getText().toString();
                password = inputPassword.getEditText().getText().toString();

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError(getText(R.string.field_mustNotBeEmpty));
                    return;
                } else {
                    inputEmail.setError(null);
                }

                if (TextUtils.isEmpty(password)) {
                    inputPassword.setError(getText(R.string.field_mustNotBeEmpty));
                    return;
                } else {
                    inputPassword.setError(null);
                }

                progressBar.setVisibility(View.VISIBLE);
                LoginNewUser();
            }

            private void LoginNewUser() {

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail: success");
                                progressBar.setVisibility(View.INVISIBLE);

                                openMainActivity();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, R.string.authentication_failure, Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
            }

            private void openMainActivity() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void startPasswordReset() {

        email = inputEmail.getEditText().getText().toString();

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getText(R.string.field_pwReset));
            return;
        } else {
            inputEmail.setError(null); //remove error and continue
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, R.string.pwReset_success, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, R.string.pwReset_failure, Toast.LENGTH_SHORT).show());
    }
}
