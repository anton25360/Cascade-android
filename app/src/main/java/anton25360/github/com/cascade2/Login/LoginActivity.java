package anton25360.github.com.cascade2.Login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import anton25360.github.com.cascade2.MainActivity;
import anton25360.github.com.cascade2.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.loginEmail) TextInputLayout inputEmail;
    @BindView(R.id.loginPassword) TextInputLayout inputPassword;
    @BindView(R.id.loginButton) Button loginEmail;
    @BindView(R.id.loginGoogle) SignInButton loginGoogle;
    @BindView(R.id.loginProgress) ProgressBar progressLogin;
    @BindView(R.id.loginSignUp) TextView signUp;
    @BindView(R.id.loginLayout) ConstraintLayout layout;
    @BindView(R.id.loginForgottenPassword) TextView forgottenPW;
    private String email, password;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); //firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        progressLogin.setVisibility(View.INVISIBLE); // makes progressbar invisible

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

        forgottenPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPasswordReset();
            }
        });

        loginEmail.setTransformationMethod(null); //forces button font to not be uppercase
        loginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputEmail.getEditText().getText().toString();
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

                progressLogin.setVisibility(View.VISIBLE); // makes progressbar visible, so user knows something is happening


                // When the user presses "Log In", hide the keyboard so they can see the progressbar
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                LoginNewUser();
            }

            private void LoginNewUser() {

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail: success");

                                    progressLogin.setVisibility(View.INVISIBLE);


                                    openMainActivity();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    progressLogin.setVisibility(View.INVISIBLE);

                                }
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
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); //todo intent animations
    }

    private void startPasswordReset() {

        email = inputEmail.getEditText().getText().toString();

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Field can't be empty");
            return;
        } else {
            inputEmail.setError(null); //remove error and continue
        }

        //todo sort login animation here too
        progressLogin.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LoginActivity.this, "Success, check your inbox.", Toast.LENGTH_SHORT).show();
                        progressLogin.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error, please try again.", Toast.LENGTH_SHORT).show();
                        progressLogin.setVisibility(View.INVISIBLE);
                    }
                });
    }
}
