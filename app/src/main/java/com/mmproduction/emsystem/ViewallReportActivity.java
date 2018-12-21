package com.mmproduction.emsystem;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class ViewallReportActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ListView mlistview, mlistview1;

    //    FirebaseDatabase mdatabase;
//    FirebaseListAdapter adapter ;
//    DatabaseReference mdatabasereferense;
    FirebaseListAdapter adapter, adapter1;
    // /User is a class which i am going to map my data
    ArrayList<String> mlist;
    ArrayAdapter<String> madapter;
    ViewReports malltext;
    TextView name, email, message, date, time,location;
    ImageView image;
    String urlImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_report);


        mlistview = (ListView) findViewById(R.id.view_all_report_listview);
        mlistview1 = (ListView) findViewById(R.id.view_all_report_listview);
        mtoolbar = (Toolbar) findViewById(R.id.Register_toolbar);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Activity Reported by User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Query query = FirebaseDatabase.getInstance().getReference().child("reportmsg");
        FirebaseListOptions<ViewReports> options = new FirebaseListOptions.Builder<ViewReports>().setLayout(R.layout.view_all_report_info_layout).setQuery(query, ViewReports.class).build();

        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, Object model, int position) {

                name = v.findViewById(R.id.idnameinfo);
                //TextView contact_no = v.findViewById(R.id.idcontactinfo);
                email = v.findViewById(R.id.idemailinfo);
                message = v.findViewById(R.id.idtextinfo);
                date = v.findViewById(R.id.iddateinfo);
                time = v.findViewById(R.id.idtimeinfo);
                location = v.findViewById(R.id.idlocationinfo);
                image = v.findViewById(R.id.idimageinfo);

                //ImageView image = v.findViewById(R.id.idimageinfo);

                ViewReports rep = (ViewReports) model;


                date.setText("Date : " + rep.getDate().toString());
                time.setText("Time : " + rep.getTime().toString());
                name.setText("Name : " + rep.getName().toString());
                email.setText("Email : " + rep.getEmail().toString());
                message.setText("Message : " + rep.getMessage().toString());
                location.setText("Location : " + rep.getLocation().toString());
                urlImage = rep.getURL();
                //Picasso.get().load(rep.getURL()).into(image);
                Glide.with(ViewallReportActivity.this).load(urlImage).into(image);
            }
        };
        mlistview.setAdapter(adapter);
    }
    //hithere

    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();
    }
}
