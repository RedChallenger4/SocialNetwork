package demo.appium.com.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        NeedNewAccountLink = (TextView) findViewById(R.id.register_account_link);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        LoginButton = (Button) findViewById(R.id.login_button);
        loadingBar = new ProgressDialog(this);

        // sets up hyperlink for New Account users
        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // method that will send User to register activity
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AllowingUserToLogin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // checks if there is an active user instance
        // if not, null means user is not connected to Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // check if token/user exists
        if (currentUser == null) {
            // if user is already logged in, send to main activity
            SendUserToMainActivity();
        }
    }

    private void AllowingUserToLogin()
    {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please write your email...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please wait, while we are logging you into your Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true); // dialog box won't disappear until error or auth issue occurs

            // signis in with user
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                SendUserToMainActivity();

                                Toast.makeText(LoginActivity.this, "you are Logged In successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error occurred : " + message, Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity()
    {
        // sends user to Register Activity
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

        //finish();
    }
}
