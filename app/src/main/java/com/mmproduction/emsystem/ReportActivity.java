package com.mmproduction.emsystem;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity implements LocationListener{

    String msg,location, currentdate, currenttime, uid;
    ImageView mImage;
    Button mbtncooseimag, mbtnuploadimage,mCaptureImageButton;
    EditText mMassage;
    TextView mtxtDate, mtxtTime;
    FirebaseStorage storage;
    StorageReference mstorageReference, ref;

    TextView locationText;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE= 101;
    private Uri mImageUri;

    private FirebaseAuth mAuth;
    FirebaseUser Current_user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseref, mDatabaseref1;
    //progress dialog
    private ProgressDialog mRegprogress;
    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        //to hide keyboard when activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mbtncooseimag = (Button) findViewById(R.id.btnChooseimage);
        mbtnuploadimage = (Button) findViewById(R.id.btnUploagImage);
        mImage = (ImageView) findViewById(R.id.add_Image);
        mMassage = (EditText) findViewById(R.id.message);
        mtoolbar = (Toolbar) findViewById(R.id.Register_toolbar);
        mDatabaseref = FirebaseDatabase.getInstance().getReference("reportmsg");
        mstorageReference = FirebaseStorage.getInstance().getReference("Images");
        mtxtDate = (TextView) findViewById(R.id.in_date);
        mtxtTime = (TextView) findViewById(R.id.in_time);
        locationText = (TextView)findViewById(R.id.address_location);

        mRegprogress = new ProgressDialog(this);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //code for take system date and time
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(" dd-MM-yyyy");
        String dateString = sdf.format(date);
        mtxtDate.setText(dateString);
        SimpleDateFormat sdf1 = new SimpleDateFormat(" h:mm a");
        String timeString = sdf1.format(date);
        mtxtTime.setText(timeString);

        //this code is foe ask for location permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        getLocation();

        mbtncooseimag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        mbtnuploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isInternetAvailable(ReportActivity.this)) {

                    storage = FirebaseStorage.getInstance();
                    mstorageReference = storage.getReference();
                    msg = mMassage.getText().toString();
                    currentdate = mtxtDate.getText().toString();
                    currenttime = mtxtTime.getText().toString();
                    location = locationText.getText().toString();
                    Current_user = FirebaseAuth.getInstance().getCurrentUser();
                    uid = Current_user.getUid();

                    mDatabaseref = FirebaseDatabase.getInstance().getReference().child("reportmsg").child(uid);
                    mDatabaseref1 = FirebaseDatabase.getInstance().getReference("user");
                    if (!validate_message() || !validate_location()) {
                        return;
                    }else {
                        uploadImage();
                    }
                    //this is changed
                    /*mbtnuploadimage.setEnabled(false);
                    mbtncooseimag.setEnabled(false);*/
                } else {
                    Snackbar snackbar = Snackbar.make(mbtnuploadimage, "No Internet Connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    public void onLocationChanged(Location location) {
        locationText.setText("Latitude (" + location.getLatitude() + ") "+ "\n Longitude (" + location.getLongitude() + ") ");

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationText.setText(locationText.getText() + "\n" + addresses.get(0).getAddressLine(0)/*+ ", " +
                    addresses.get(0).getAddressLine(1)*/ /*+ ", " + addresses.get(0).getAddressLine(2)*/);
        }catch(Exception e) {
            Toast.makeText(ReportActivity.this, "Can't obtain your location", Toast.LENGTH_SHORT).show();
        }
    }

    public void onProviderDisabled(String provider) {
        Toast.makeText(ReportActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {

    }

    //this method for choose image f
    void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                mImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                mImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    void uploadImage() {
        if (mImageUri != null) {
            mRegprogress.setTitle("Uploading...");
            mRegprogress.show();
            mRegprogress.setCanceledOnTouchOutside(false);

            FirebaseUser Current_user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = Current_user.getUid().toString();
            ref = mstorageReference.child("images/").child(uid);


            ref.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mRegprogress.dismiss();

                            /*ImageUpload upload = new ImageUpload(ref.getDownloadUrl().toString());
                            String uploadid = mDatabaseref.push().getKey();
                            mDatabaseref.setValue(upload);*/
                            Toast.makeText(ReportActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(ReportActivity.this, HomeActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mRegprogress.dismiss();
                            Toast.makeText(ReportActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            dataUpload();
                            mRegprogress.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }


    }

    public void dataUpload() {
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                    mDatabaseref1 = FirebaseDatabase.getInstance().getReference().child("user").child(uid);

                    mDatabaseref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            mDatabaseref.child("name").setValue(dataSnapshot.child("name").getValue());
                            mDatabaseref.child("email").setValue(dataSnapshot.child("email").getValue());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    mDatabaseref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            dataSnapshot.getRef().child("Message").setValue(msg);
                            dataSnapshot.getRef().child("Date").setValue(currentdate);
                            dataSnapshot.getRef().child("Time").setValue(currenttime);
                            dataSnapshot.getRef().child("Location").setValue(location);
                            dataSnapshot.getRef().child("URL").setValue(uri.toString());
                            // Toast.makeText(ReportActivity.this, "Submit", Toast.LENGTH_SHORT).show();
                                /*viewReports.setMessage(msg);
                                viewReports.setDate(date);
                                viewReports.setTime(time);
                                viewReports.setURL(uri.toString());*/
                            //ReportActivity.this.finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });

            }
        });
    }

    public boolean validate_message() {
        if (mMassage.getText().toString().isEmpty()) {
            mRegprogress.dismiss();
            Toast.makeText(this,"Message can't be empty!",Toast.LENGTH_LONG).show();;
            return false;
        } else {
            mMassage.setError(null);
            return true;
        }
    }

    public boolean validate_location() {
        if (locationText.getText().toString().isEmpty()) {
            mRegprogress.dismiss();
           Toast.makeText(this,"wait a while! we try to find your location",Toast.LENGTH_LONG).show();
            return false;
        }else if (locationText.length() < 10) {
            mRegprogress.dismiss();
            Toast.makeText(this,"wait a while! we try to find your location",Toast.LENGTH_LONG).show();
            return false;
        }  else {
            locationText.setError(null);
            return true;
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
/*}else {

            FirebaseUser Current_user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = Current_user.getUid();
            mDatabaseref = FirebaseDatabase.getInstance().getReference().child("reportmsg").child(uid);
            mDatabaseref1 = FirebaseDatabase.getInstance().getReference().child("user").child(uid);

            mDatabaseref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    mDatabaseref.child("name").setValue(dataSnapshot.child("name").getValue());
                    mDatabaseref.child("email").setValue(dataSnapshot.child("email").getValue());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            final String msg = mMassage.getText().toString();
            final String date = mtxtDate.getText().toString();
            final String time = mtxtTime.getText().toString();

            HashMap<String, String> userMap = new HashMap<>();
            userMap.put("Message", msg);
            userMap.put("Date", date);
            userMap.put("Time", time);
            //userMap.put("name", name);
           // userMap.put("email", email);

            mDatabaseref.setValue(userMap);

        }
    }

    void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
           *//* @Override
            protected void onActivityResult ( int requestCode, int resultCode, Intent data){
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                        && data != null && data.getData() != null) {
                    filePath = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        Image.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                }*//*
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                mImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void uploadImage() {
        if(mImageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            FirebaseUser Current_user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = Current_user.getUid().toString();
            final StorageReference ref = mstorageReference.child("images/").child(uid);



            ref.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                          progressDialog.dismiss();

                            *//*ImageUpload upload = new ImageUpload(ref.getDownloadUrl().toString());
                            String uploadid = mDatabaseref.push().getKey();
                            mDatabaseref.setValue(upload);*//*
                            Toast.makeText(ReportActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            *//*startActivity(new Intent(ReportActivity.this,HomeActivity.class));*//*
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ReportActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }


    public boolean validate_message() {
        if (mMassage.getText().toString().isEmpty()) {
            mMassage.setError("Last name is required");
            return false;
        } else {
            mMassage.setError(null);
            return true;
        }*/



    /*@Override
    public void onClick(View v) {

        if (v == mbtnDatePicker) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            mtxtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        if (v == mbtnTimePicker) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mtxtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
*/
/*public void btnsubmit_data(View view) {

 *//* storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();*//*


        final String msg = mMassage.getText().toString();
        final String date = mtxtDate.getText().toString();
        final String time = mtxtTime.getText().toString();


        final FirebaseUser Current_user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = Current_user.getUid();
        mDatabaseref = FirebaseDatabase.getInstance().getReference().child("reportmsg").child(uid);
        if(!validate_message() ){
           *//* || !validateDate(date)*//*
           return;
        }else {
            mDatabaseref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    dataSnapshot.getRef().child("Message").setValue(msg);
                    dataSnapshot.getRef().child("Date").setValue(date);
                    dataSnapshot.getRef().child("Time").setValue(time);

                    Toast.makeText(ReportActivity.this, "Submit", Toast.LENGTH_LONG).show();
                    ReportActivity.this.finish();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        uploadImage();


    }*/

 /*   private  void chooseImage() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            *//*Picasso.with(this).load(mImageUri).into(mImage);*//*

            mImage.setImageURI(mImageUri);
            *//*try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                mImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }*//*
        }
    }*/
/*

    //this methos is used to get extension from our file
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
*/

  /* private void uploadImage1() {


        if(mImageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            FirebaseUser Current_user = FirebaseAuth.getInstance().getCurrentUser();
            final String uid = Current_user.getUid().toString();
            mDatabaseref = FirebaseDatabase.getInstance().getReference().child("reportmsg").child(uid);
            final StorageReference fileReference = mstorageReference.child("Images/").child(uid + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Handler handler  = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mprogressbar.setProgress(0);
                                }
                            },3000);

                            Toast.makeText(ReportActivity.this, "ImageUpload successful", Toast.LENGTH_SHORT).show();

                            ImageUpload upload = new ImageUpload(fileReference.getDownloadUrl().toString());
                            String uploadid = mDatabaseref.push().getKey();
                            mDatabaseref.setValue(upload);

                            startActivity(new Intent(ReportActivity.this,HomeActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            *//*progressDialog.dismiss();*//*
                            Toast.makeText(ReportActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                          *//*  mprogressbar.setProgress((int)progress);*//*
 *//* progressDialog.setMessage("Uploaded "+(int)progress+"%");*//*
                        }
                    });
        }
        else{
            Toast.makeText(this,"No file selected",Toast.LENGTH_SHORT).show();
        }
    }*/

