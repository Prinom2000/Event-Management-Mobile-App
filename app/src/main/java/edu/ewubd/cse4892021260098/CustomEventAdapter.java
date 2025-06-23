package edu.ewubd.cse4892021260098;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;

public class CustomEventAdapter extends ArrayAdapter<Event> {

    private final Context context;
    private final ArrayList<Event> values;

    public CustomEventAdapter(@NonNull Context context, @NonNull ArrayList<Event> items) {
        super(context, -1, items);
        this.context = context;
        this.values = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.row_event, parent, false);

        TextView eventName = convertView.findViewById(R.id.tvTitle);
        TextView eventDate = convertView.findViewById(R.id.tvDate);
        TextView eventTime = convertView.findViewById(R.id.tvTime);
        TextView eventPlaceName = convertView.findViewById(R.id.tvVenue);
        //TextView eventType = rowView.findViewById(R.id.tvEventType);



        Event e = values.get(position);
        eventName.setText(e.title);
        //convert long to date_time string
        String date = single_tone.getInstance().milli_to_Date(e.datetime, "dd-MM-yy");
        eventDate.setText(date);
        eventPlaceName.setText(""+e.vanue);
        //eventType.setText(e.eventType);
        String time = single_tone.getInstance().milli_to_Date(e.datetime, "HH:mm");
        eventTime.setText(time);
        return convertView;
    }
}
