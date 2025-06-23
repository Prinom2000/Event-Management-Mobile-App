package edu.ewubd.cse4892021260098;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UpcomingActivity extends AppCompatActivity {

    private ArrayList<Event> eventsList = new ArrayList<>();
    private ListView listViewEvents;
    private CustomEventAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming);

        listViewEvents= findViewById(R.id.listview);
        adapter = new CustomEventAdapter(this, eventsList);
        listViewEvents.setAdapter(adapter);

        Button exit= findViewById(R.id.btnExit);
        Button addNew= findViewById(R.id.btnAddNew);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(UpcomingActivity.this, NewEventActivity.class);
                startActivity(i);
            }
        });

        listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = eventsList.get(position);

                Intent i = new Intent(UpcomingActivity.this, NewEventActivity.class);
                i.putExtra("eventId", e.eventId);
                i.putExtra("title", e.title);
                i.putExtra("venue", e.vanue);
                i.putExtra("description", e.description);
                i.putExtra("datetime", e.datetime);
                i.putExtra("numParticipation", e.numParticipation);
                startActivity(i);
            }
        });

        listViewEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = eventsList.get(position);
                showConsentDialog(e);

                return true;
            }
        });

    }

    private void showConsentDialog(Event e) {
        new AlertDialog.Builder(this)
                .setTitle("Consent Required ")  // assuming Event has getName()
                .setMessage("Do you consent to participate in " + e.title + "?")
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventDB db = new EventDB(UpcomingActivity.this);
                        db.deleteEvent(e.eventId);
                        db.close();

                        deleteDataToremoteDB(e.eventId);

                        eventsList.remove(e);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Deleted.....!", Toast.LENGTH_SHORT);
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: Handle consent denied
                        dialog.dismiss();
                    }
                })
                .setCancelable(false) // force a decision
                .show();
    }



    @Override
    protected void onStart() {
        super.onStart();
        this.loadDateFromSqlite();
//        this.loadDataFromRemoteServer();
    }

    public void  loadDateFromSqlite(){
        eventsList.clear();
        EventDB db= new EventDB(this);
        Cursor cur= db.selectEvents("SELECT * FROM events ORDER BY datetime DESC");
        if(cur != null){
            while (cur.moveToNext()){
                String eventId= cur.getString(0);
                String title= cur.getString(1);
                String venue= cur.getString(2);
                long datetime= cur.getLong(3);
                int numParticipation = cur.getInt(4);
                String description= cur.getString(5);

                Event e =new Event(eventId,title, venue, datetime, numParticipation, description );
                System.out.println(e);
                eventsList.add(e);
            }
        }
        adapter.notifyDataSetChanged();
    }


    /////
    private void loadDataFromRemoteServer(){
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("sid", "2021-2-60-098"));
            params.add(new BasicNameValuePair("semester", "2025-1"));
            params.add(new BasicNameValuePair("action", "restore"));
            Handler h = new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String dataFromServer = RemoteAccess.getInstance().makeHttpRequest(params);
                    if(dataFromServer != null){
                        try {
                            JSONObject json = new JSONObject(dataFromServer);
                            if(json.has("msg") && json.has("key-value")){
                                String msg = json.getString("msg");
                                if(msg != null && msg.equals("OK")){
                                    JSONArray rows = json.getJSONArray("key-value");

                                    // write code to store data in SQLite
                                    EventDB db = new EventDB(UpcomingActivity.this);

                                    for(int i=0; i<rows.length(); i++){
                                        String eventID = rows.getJSONObject(i).getString("key");
                                        String value = rows.getJSONObject(i).getString("value");
                                        JSONObject eventJson = new JSONObject(value);
                                        String  title = eventJson.getString("title");
                                        String  venue = eventJson.getString("venue");
                                        long dateTime = eventJson.getLong("dateTime");
                                        int  numParticipants = eventJson.getInt("numParticipants");
                                        String  description = eventJson.getString("description");
                                        if(doesEventExist(eventID)){
                                            db.updateEvent(eventID, title, venue, dateTime, numParticipants, description);
                                        } else{
                                            db.insertEvent(eventID, title, venue, dateTime, numParticipants, description);
                                        }
                                    }
                                    db.close();

                                }
                                h.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadDateFromSqlite();
                                    }
                                });
                                return;
                            }
                        }catch (Exception e){}
                    }
                    System.out.println("Something went wrong");
                }
            }).start();
        }catch (Exception e){}
    }

    private boolean doesEventExist(String eventId){
        for (Event e: eventsList) {
            if(e.eventId.equals(eventId)){
                return true;
            }
        }
        return false;
    }



    // delete data from remote db

    private void deleteDataToremoteDB(String eventID){
        try {

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("sid", "2021-2-60-098"));
            params.add(new BasicNameValuePair("semester", "2025-1"));
            params.add(new BasicNameValuePair("key", eventID));
            params.add(new BasicNameValuePair("action", "remove"));


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
                                        Toast.makeText(UpcomingActivity.this, msg, Toast.LENGTH_SHORT).show();
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