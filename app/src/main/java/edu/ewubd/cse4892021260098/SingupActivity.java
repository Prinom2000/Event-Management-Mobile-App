package edu.ewubd.cse4892021260098;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class SingupActivity extends AppCompatActivity {

    private EditText  etName, etEmail, etPhone, etPass, etConPass;
    private CheckBox cbRememberUser, cbRememberLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //
        Button btnSingUp = findViewById(R.id.btnSingUP);
        Button btnExit = findViewById(R.id.btnExit);
        Button btnHaveAccout = findViewById(R.id.btnHaveAccount);


        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPass = findViewById(R.id.etPass);
        etConPass = findViewById(R.id.etConPass);

        // Create DB object for signUP & login
        SignupDB db = new SignupDB(this);

        // Create SharedPreferences object for short time memory
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);


        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSingUp.setOnClickListener(new View.OnClickListener() { /////////////
            @Override
            public void onClick(View v) {
                System.out.println("btnSingUp tapped");

                String name= etName.getText().toString();
                String email= etEmail.getText().toString();
                String phone= etPhone.getText().toString();
                String pass= etPass.getText().toString();
                String conPass= etConPass.getText().toString();


                // ==== Validation Starts ====
                if (name.length() < 8) {
                    Toast.makeText(SingupActivity.this, "Name must be at least 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
                    Toast.makeText(SingupActivity.this, "Email must be in the format: example@gmail.com", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!phone.matches("^\\d+$")) {
                    Toast.makeText(SingupActivity.this, "Phone number must be numeric only", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ((phone.startsWith("01") && phone.length() != 11) ||
                        (phone.startsWith("+880") && phone.length() != 14)) {
                    Toast.makeText(SingupActivity.this, "Invalid phone number length", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pass.length() < 8) {
                    Toast.makeText(SingupActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (conPass.length() < 8) {
                    Toast.makeText(SingupActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pass.equals(conPass)) {
                    Toast.makeText(SingupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ==== Validation Passed ====

                System.out.println("Name: "+name);  // to print in tarminal
                System.out.println("Email: "+email);
                System.out.println("Phone: "+phone);
                System.out.println("Password: "+pass);
                System.out.println("Con Password: "+conPass);




                // insert info into SignupDB
                if (db.isUserExists(email)) {
                    Toast.makeText(SingupActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean inserted = db.insertUser(name, email, phone, pass);
                if (inserted) {
                    Toast.makeText(SingupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();

                    // do SharedPreferences for email & phone
                    SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("email", email);
                    editor.putString("phone", phone);
                    editor.apply(); // commit user data

                    Intent i = new Intent(SingupActivity.this, LoginActivity.class);
                    startActivity(i);
                    finishAffinity();
                } else {
                    Toast.makeText(SingupActivity.this, "Signup failed!", Toast.LENGTH_SHORT).show();
                }




            }
        });
        btnHaveAccout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email= etEmail.getText().toString();
                String phone= etPhone.getText().toString();

                System.out.println("You click btnHaveAccout..........!");
                Intent i= new Intent(SingupActivity.this, LoginActivity.class);
                i.putExtra("email", email);
                i.putExtra("phone", phone);
                startActivity(i);
                finishAffinity();


            }
        });




    }
}