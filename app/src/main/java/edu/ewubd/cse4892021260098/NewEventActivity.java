package edu.ewubd.cse4892021260098;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.EventLogTags;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kotlin.collections.ArrayDeque;


public class NewEventActivity extends AppCompatActivity {

    private EditText etTitle, etVenue, etDate, etNumofparticipation, etDescription;
    private RadioButton rdOnline, rdOffline;

    private String eventID= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        etTitle= findViewById(R.id.etTille);
        etVenue= findViewById(R.id.etVenue);
        etDate= findViewById(R.id.etDate);
        etNumofparticipation= findViewById(R.id.etNumofparticipation);
        etDescription= findViewById(R.id.etDescription);

        rdOnline= findViewById(R.id.rdOnline);
        rdOffline= findViewById(R.id.rdOffline);

        /////28-4-25

        Intent i = this.getIntent();
        if(i!=null && i.hasExtra("eventId")){
            eventID = i.getStringExtra("eventId");

            String title = i.getStringExtra("title");
            String venue = i.getStringExtra("venue");
            long datetime = i.getLongExtra("datetime", 0);
            int numParticipation = i.getIntExtra("numParticipation", 0);
            String description = i.getStringExtra("description");

            etTitle.setText(title);
            etVenue.setText(venue);
            try{
                etDate.setText(single_tone.getInstance().milli_to_Date(datetime, "dd-MM-yyyy HH:mm"));
            }catch (Exception e){
                e.printStackTrace();
            }
            etNumofparticipation.setText(""+numParticipation);
            etDescription.setText(description);
        }



        Button btCancle = findViewById(R.id.btCancle);
        Button btSave = findViewById(R.id.btSave);

        btCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(NewEventActivity.this, UpcomingActivity.class);
                System.out.println("You cancled add new evant.....!");
                startActivity(i);

            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title= etTitle.getText().toString().trim();
                String venue= etVenue.getText().toString().trim();
                String date= etDate.getText().toString().trim();
                String numofparticipation= etNumofparticipation.getText().toString().trim();
                String description= etDescription.getText().toString().trim();

                System.out.println("I love Coding.......!  👨‍💻");

                // send those information in database.
                System.out.println("isOnline: "+rdOnline.isChecked());
                System.out.println("isOffline: "+rdOffline.isChecked());

                System.out.println("title: "+title);
                System.out.println("venue: "+venue);
                System.out.println("dt: "+date);
                System.out.println("numParticipants: "+numofparticipation);
                System.out.println("description: "+description);
                // send those information in database.

                long dateTime= System.currentTimeMillis();

                int numParticipants = 0;
                try {
                    numParticipants = Integer.parseInt(numofparticipation);
                }catch (Exception e){

                }


                try {

                    String time = single_tone.getInstance().milli_to_Date(dateTime, "HH:mm");  // get current time
                    date= date.concat(" ");
                    date= date.concat(time); // make the date like: (dd-MM-yyyy HH:mm) format
                    dateTime= single_tone.getInstance().date_to_milli(date, "dd-MM-yyyy HH:mm");

//                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");  // with out using singleton class
//                    Date dt = sdf.parse(date);
//                    dateTime =dt.getTime();
                }catch (Exception e){}


                if(title.length() < 8){
                    Toast.makeText(NewEventActivity.this, "Title must have 8 letters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(venue.length() < 8){
                    Toast.makeText(NewEventActivity.this, "Venue must have 8 letters", Toast.LENGTH_SHORT).show();
                    return;
                }
                // check the data format here. take help from web
                // hints: find code to covert a string-date to Date object
                //          then, get the milliseconds value from the date object
                if(numParticipants <= 0){
                    Toast.makeText(NewEventActivity.this, "Invalid number of participants", Toast.LENGTH_SHORT).show();
                    return;
                }

                // if all condition ok back to UpcomingActivity & load into DB.............

                Intent i= new Intent(NewEventActivity.this, UpcomingActivity.class);
                System.out.println("You saved a new evant.....!");
                startActivity(i);

                // write code to store data in SQLite
                EventDB db = new EventDB(NewEventActivity.this);
                if(eventID.isEmpty()){
                    eventID = title+System.nanoTime();
                    db.insertEvent(eventID, title, venue, dateTime, numParticipants, description);

                }else{
                    db.updateEvent(eventID, title, venue, dateTime, numParticipants, description);

                }
                db.close();


                storDataToremoteDB(eventID, title, venue, dateTime, numParticipants, description);


                finish();
            }
        });

    }
    private void storDataToremoteDB(String eventID,String title,String venue,long dateTime,int numParticipants,String description){
        try {
            JSONObject jo= new JSONObject();

            jo.put("title", title);
            jo.put("venue", venue);
            jo.put("dateTime", dateTime);
            jo.put("numParticipants", numParticipants);
            jo.put("description", description);
//            jo.put("type", )

            String value = jo.toString();


            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("sid", "2021-2-60-098"));
            params.add(new BasicNameValuePair("semester", "2025-1"));
            params.add(new BasicNameValuePair("key", eventID));
            params.add(new BasicNameValuePair("value", value));
            params.add(new BasicNameValuePair("action", "backup"));



            // by thread:
            Handler h =new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String dataFromServer= RemoteAccess.getInstance().makeHttpRequest(params);
                    if(dataFromServer != null)
                    {
                        try {
                            JSONObject json = new JSONObject(dataFromServer);
                            if(json.has("msg")){
                                String msg = json.getString("msg");

                                h.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(NewEventActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;

                            }
                        }catch (Exception e){}
                    }
                    System.out.println("Something went wrong");

                }
            }).start();
        }
        catch (Exception e){}

    }
}