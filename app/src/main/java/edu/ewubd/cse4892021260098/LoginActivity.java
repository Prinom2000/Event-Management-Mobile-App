package edu.ewubd.cse4892021260098;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPass;
    private CheckBox loginRimemberUser, loginRemeberLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail= findViewById(R.id.loginEmail);
        loginPass= findViewById(R.id.loginPass);

        loginRimemberUser= findViewById(R.id.loginRimemberUser);
        loginRemeberLogin= findViewById(R.id.loginRemeberLogin);

        // Create DB object for signUP & login
        SignupDB db = new SignupDB(this);

        Button loginDoNotHaveAccount = findViewById(R.id.loginDoNotHaveAccount);
        Button login_bt_Exit = findViewById(R.id.login_bt_Exit);
        Button login_bt_login = findViewById(R.id.login_bt_login);

        loginDoNotHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Back to login......!");
                Intent i = new Intent(LoginActivity.this, SingupActivity.class);
                startActivity(i);
                finishAffinity();
            }
        });

        login_bt_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        login_bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email= loginEmail.getText().toString();
                String pass= loginPass.getText().toString();

                // ===== Validation =====
                if (!email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
                    Toast.makeText(LoginActivity.this, "Invalid email format. Use: example@gmail.com", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.length() < 8) {
                    Toast.makeText(LoginActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }


                boolean valid = db.validateUser(email, pass);
                if (valid) {
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, UpcomingActivity.class);
                    i.putExtra("email", email);
                    startActivity(i);
                    finishAffinity();
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }
}