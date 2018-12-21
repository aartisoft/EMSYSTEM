package com.mmproduction.emsystem;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterViewFlipper;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminActivity extends AppCompatActivity {


    FirebaseAuth firebaseAuth;
    TextView txtNewBetTimer;

    private TextView txtTimerDay, txtTimerHour, txtTimerMinute, txtTimerSecond;
    private TextView tvEvent;
    private Handler handler;
    private Runnable runnable;

    private Toolbar mtoolbar;

    private static final String TAG = "HomeActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    AdapterViewFlipper adapterViewFlipper;
    int[] Images={
            R.drawable.img2,
            R.drawable.infoss,
            R.drawable.pastdatass,
            R.drawable.reportss
    };
    String[] name={
            "EMS",
            "Easy To find Boothlocation",
            "Find Past Data",
            "Report Mal-Practices"
    };

    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminactivity);

       // txtNewBetTimer = (TextView) findViewById(R.id.tv1);

        mtoolbar = (Toolbar) findViewById(R.id.Register_toolbar);

        initNavigationDrawer();
        mDrawerlayout = (DrawerLayout) findViewById(R.id.activity_home_drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerlayout, R.string.open, R.string.close);
        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setTitle("Admin Department");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        txtTimerDay = (TextView) findViewById(R.id.txtTimerDay);
        txtTimerHour = (TextView) findViewById(R.id.txtTimerHour);
        txtTimerMinute = (TextView) findViewById(R.id.txtTimerMinute);
        txtTimerSecond = (TextView) findViewById(R.id.txtTimerSecond);
        tvEvent = (TextView) findViewById(R.id.tvhappyevent);

        countDownStart();

        adapterViewFlipper=(AdapterViewFlipper)findViewById(R.id.adapterViewFlipper);
        ImageCustomAdapter imageCustomAdapter =new ImageCustomAdapter(getApplicationContext(),name,Images);
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



    /*public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(AdminActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(AdminActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
*/


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_user_menu,menu);
        return true;
    }
*/
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
                        startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                        break;
                    }
                    case R.id.Information:{
                        startActivity(new Intent(AdminActivity.this, InformationActivity.class));
                        break;
                    }
                    case R.id.about: {
                        startActivity(new Intent(AdminActivity.this, AboutusActivity.class));
                        break;
                    }
                    case R.id.pastdata: {
                        startActivity(new Intent(AdminActivity.this, ResultActivity.class));
                        break;
                    }
                    case R.id.View_report_alert: {
                        startActivity(new Intent(AdminActivity.this, ViewallReportActivity.class));
                        break;
                    }
                    case R.id.feedback: {
                        startActivity(new Intent(AdminActivity.this, FeedbackActivity.class));
                        break;
                    }
           /* case R.id.Account_setting: {
                startActivity(new Intent(AdminActivity.this, AccountsettingActivity.class));
                break;
            }*/
                }
                return true;
            }
        });
    }

}
