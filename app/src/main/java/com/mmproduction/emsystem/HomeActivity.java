package com.mmproduction.emsystem;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;

import android.widget.AdapterViewFlipper;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.security.auth.Subject;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabaseref;
    private FirebaseDatabase mFirebaseDatabase;
    TextView txtNewBetTimer;

    private TextView txtTimerDay, txtTimerHour, txtTimerMinute, txtTimerSecond;
    private TextView tvEvent,mdrawername,mdraweremail;
    private Handler handler;
    private Runnable runnable;

    //private Toolbar mtoolbar;

    private static final String TAG = "HomeActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    AdapterViewFlipper adapterViewFlipper;
    int[] Images = {
            R.drawable.img2,
            R.drawable.infoss,
            R.drawable.pastdatass,
            R.drawable.reportss
    };
    String[] name = {
            "EMS",
            "Easy To find Boothlocation",
            "Find Past Data",
            "Report Mal-Practices"
    };

    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;

   // private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        /*setContentView(R.layout.drawer_header);*/


        /*mnavigationView.setNavigationItemSelectedListener();
        View header = mnavigationView.getHeaderView(0);*/

        //txtNewBetTimer = (TextView) findViewById(R.id.tv1);
        mListView = (ListView) findViewById(R.id.view_userinfo_listview);

        initNavigationDrawer();
        mDrawerlayout = (DrawerLayout) findViewById(R.id.activity_home_drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerlayout, R.string.open, R.string.close);
        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setTitle("EMS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

/*
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        myRef = mFirebaseDatabase.getReference().child("reportmsg").child(userID);*/


        /*myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    userProfile uInfo = new userProfile();
                   // uInfo.setName(ds.child(userID).getValue(userProfile.class).getName()); //set the name
                    //uInfo.setEmail(ds.child(userID).getValue(userProfile.class).getEmail()); //set the email
                    //  uInfo.setPhone_num(ds.child(userID).getValue(UserInformation.class).getPhone_num()); //set the phone_num

                    //display all the information
                    Log.d(TAG, "showData: name: " + uInfo.getName());
                    Log.d(TAG, "showData: email: " + uInfo.getEmail());
                    //  Log.d(TAG, "showData: phone_num: " + uInfo.getPhone_num());

                    ArrayList<String> array = new ArrayList<>();
                    array.add(uInfo.getName());
                    array.add(uInfo.getEmail());
                    // array.add(uInfo.getPhone_num());
                    ArrayAdapter adapter = new ArrayAdapter(HomeActivity.this, android.R.layout.simple_list_item_1, array);
                    mListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
        txtTimerDay = (TextView) findViewById(R.id.txtTimerDay);
        txtTimerHour = (TextView) findViewById(R.id.txtTimerHour);
        txtTimerMinute = (TextView) findViewById(R.id.txtTimerMinute);
        txtTimerSecond = (TextView) findViewById(R.id.txtTimerSecond);
        tvEvent = (TextView) findViewById(R.id.tvhappyevent);

        countDownStart();

        adapterViewFlipper = (AdapterViewFlipper) findViewById(R.id.adapterViewFlipper);
        ImageCustomAdapter imageCustomAdapter = new ImageCustomAdapter(getApplicationContext(), name, Images);
        adapterViewFlipper.setAdapter(imageCustomAdapter);
        adapterViewFlipper.setFlipInterval(3500);
        adapterViewFlipper.setAutoStart(true);

       /* if(isServicesOK()){
            init();
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void countDownStart() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    // Please here set your event date//YYYY-MM-DD
                    Date futureDate = dateFormat.parse("2019-01-01");
                    Date currentDate = new Date();
                    if (!currentDate.after(futureDate)) {
                        long diff = futureDate.getTime()
                                - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        txtTimerDay.setText("" + String.format("%02d", days));
                        txtTimerHour.setText("" + String.format("%02d", hours));
                        txtTimerMinute.setText("" + String.format("%02d", minutes));
                        txtTimerSecond.setText("" + String.format("%02d", seconds));
                    } else {
                        tvEvent.setVisibility(View.VISIBLE);
                        tvEvent.setText("Happy New Year!");
                        textViewGone();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);
    }

    public void textViewGone() {
        findViewById(R.id.LinearLayout10).setVisibility(View.GONE);
        findViewById(R.id.LinearLayout11).setVisibility(View.GONE);
        findViewById(R.id.LinearLayout12).setVisibility(View.GONE);
        findViewById(R.id.LinearLayout13).setVisibility(View.GONE);
        findViewById(R.id.textView1).setVisibility(View.GONE);
        findViewById(R.id.textView2).setVisibility(View.GONE);
    }

  /*  private void init(){
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
    }*/


    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }



 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.loged_in_user_menu, menu);
        return true;
    }*/

    public void initNavigationDrawer() {


        NavigationView mnavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mnavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                firebaseAuth = FirebaseAuth.getInstance();
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.logout: {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        break;
                    }
                    case R.id.Information: {
                        startActivity(new Intent(HomeActivity.this, InformationActivity.class));
                        break;
                    }
                    case R.id.report: {
                        startActivity(new Intent(HomeActivity.this, ReportActivity.class));
                        break;
                    }
                    case R.id.about: {
                        startActivity(new Intent(HomeActivity.this, AboutusActivity.class));
                        break;
                    }
                    case R.id.pastdata: {
                        startActivity(new Intent(HomeActivity.this, ResultActivity.class));
                        break;
                    }
                    case R.id.feedback: {

                        startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));

                        /*Intent intent = new Intent(Intent.ACTION_SENDTO);*/
                        /*intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"parammoradiya98@gmail.com","akashgolakiya501@gmail.com","milanmiyani11@gmail.com","rajnigujarati567@gmail.com","Jaimiknavadiya@gmail.com"});
                      *//*  if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }*/
                        break;
                    }
                }
                return true;
            }
        });
    }


    /*@Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }


    }*/
}