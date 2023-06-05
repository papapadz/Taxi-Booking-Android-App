package com.tukla.www.tukla;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.List;

public class HistoryListAdapter extends BaseAdapter {

    private Context context;
    private List<History> data;

    public HistoryListAdapter(Context context, List<History> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        if(data.isEmpty())
            return 1;
        else
            return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DecimalFormat df = new DecimalFormat("0.00");
        if (data.isEmpty()) {
            convertView = LayoutInflater.from(context).inflate(R.layout.empty_list, parent, false);
            return convertView;
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.history_list, parent, false);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        TextView distance = convertView.findViewById(R.id.distance);
        TextView historyDestination = convertView.findViewById(R.id.historyDestination);
        TextView historyName = convertView.findViewById(R.id.historyName);
        TextView historyFare = convertView.findViewById(R.id.historyFare);
        TextView historyFeedback = convertView.findViewById(R.id.list_history_feedback);
        TextView txtRating = convertView.findViewById(R.id.starRating);

        History data = (History) getItem(position);
        distance.setText(data.getSession().getBooking().getDistance()+"KM");
        historyDestination.setText(data.getSession().getBooking().getDestinationText());
        historyFeedback.setText("");
        FirebaseDatabase.getInstance().getReference("feedbacks").child(data.getSession().getBooking().getBookingID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    historyFeedback.setText("Feedback: " + dataSnapshot.child("feedback").getValue(String.class));
                    txtRating.setText(dataSnapshot.child("rating").getValue(Integer.class) + " star");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(data.getSession().getDriver().getUserID().equals(mAuth.getUid())) {
            historyFeedback.setVisibility(View.VISIBLE);
            historyName.setText("Passenger: "+data.getSession().getBooking().getUser().getFullname());
            historyFare.setText("Php "+df.format(data.getFare()));

        } else if(data.getSession().getBooking().getUser().getUserID().equals(mAuth.getUid())) {
            historyName.setText("Driver: "+data.getSession().getDriver().getFullname());
            historyFare.setText("Php "+df.format(data.getFare()));
        } else {
            historyFeedback.setVisibility(View.VISIBLE);
            historyName.setText("Passenger: "+data.getSession().getBooking().getUser().getFullname() + "\n" + "Driver: "+data.getSession().getDriver().getFullname() + "\n" + "Fare: PHP "+df.format(data.getFare()));
        }
        return convertView;
    }
}
