package com.mmproduction.emsystem;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportActivity extends AppCompatActivity {

    private Pattern pattern;
    private Matcher matcher;

    String ImageURL, msg, currentdate, currenttime, uid;

    private static final String DATE_PATTERN =
            "(0?[1-9]|1[012]) [/.-] (0?[1-9]|[12][0-9]|3[01]) [/.-] ((19|20)\\d\\d)";
//report
    //hcbhjbvhjb
    ProgressBar mprogressbar;
    ImageView mImage;
    Button mbtncooseimag, mbtnuploadimage;
    EditText mMassage;
    TextView mtxtDate, mtxtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    int PERMISSION_REQUEST_CODE = 100;
    FirebaseStorage storage;
    StorageReference mstorageReference,ref;
 private static final int REQUEST_IMAGE_CAPTURE= 101;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    private FirebaseAuth mAuth;
    FirebaseUser Current_user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseref,mDatabaseref1;
    //progress dialog
    private ProgressDialog mRegprogress;
    private Toolbar mtoolbar;
    private ValueEventListener mPostListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report1);

        //to hide keyboard when activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //report_akash
        //sxzhcbdbvc
        //this is report activity
        //mprogressbar = (ProgressBar)findViewById(R.id.report_progress_bar);
        mbtncooseimag = (Button) findViewById(R.id.btnChooseimage);
        mbtnuploadimage = (Button) findViewById(R.id.btnUploagImage);
        mImage = (ImageView) findViewById(R.id.add_Image);
        mMassage = (EditText)findViewById(R.id.message);
        mtoolbar = (Toolbar) findViewById(R.id.Register_toolbar);
        mDatabaseref = FirebaseDatabase.getInstance().getReference("reportmsg");
        mstorageReference = FirebaseStorage.getInstance().getReference("Images");
        mtxtDate=(TextView) findViewById(R.id.in_date);
        mtxtTime=(TextView) findViewById(R.id.in_time);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegprogress = new ProgressDialog(this);
        mbtncooseimag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               chooseImage();
            }
        });


        mDatabaseref1 = FirebaseDatabase.getInstance().getReference("user");

        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat(" dd-MM-yyyy");
        String dateString = sdf.format(date);
        mtxtDate.setText(dateString);

        SimpleDateFormat sdf1 = new SimpleDateFormat(" h:mm a");
        String timeString = sdf1.format(date);
        mtxtTime.setText(timeString);


            mbtnuploadimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CheckNetwork.isInternetAvailable(ReportActivity.this)) {
                        storage = FirebaseStorage.getInstance();
                        mstorageReference = storage.getReference();

                        msg = mMassage.getText().toString();
                        currentdate = mtxtDate.getText().toString();
                        currenttime = mtxtTime.getText().toString();
                        Current_user = FirebaseAuth.getInstance().getCurrentUser();
                        uid = Current_user.getUid();


                        mDatabaseref = FirebaseDatabase.getInstance().getReference().child("reportmsg").child(uid);

                        uploadImage();
                        //this is changed
                        mbtncooseimag.setEnabled(false);
                        mbtnuploadimage.setEnabled(false);
                    }
                    else{
                        Snackbar snackbar = Snackbar.make(mbtnuploadimage, "No Internet Connection", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            });


    }

    /*public void btnsubmit_data(View view) {



    }*/

    void chooseImage() {
  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        }
      /*  Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/
           /* @Override
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
                }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
           Bundle bundle=data.getExtras();
           Bitmap bitmap= (Bitmap) bundle.get("data");
           mImage.setImageBitmap(bitmap);
       }
       /* super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
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
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            FirebaseUser Current_user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = Current_user.getUid().toString();
            ref = mstorageReference.child("images/").child(uid);


            ref.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            /*ImageUpload upload = new ImageUpload(ref.getDownloadUrl().toString());
                            String uploadid = mDatabaseref.push().getKey();
                            mDatabaseref.setValue(upload);*/
                            Toast.makeText(ReportActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(ReportActivity.this,HomeActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ReportActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            dataUpload();
                           progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }


    }

    public void dataUpload() {
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                if (!validate_message()) {
                    return;
                    /*  Toast.makeText(ReportActivity.this, "Plese write about problem", Toast.LENGTH_LONG).show();
                     */
                } else {

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
            }
        });
    }

    public boolean validate_message() {
        if (mMassage.getText().toString().isEmpty()) {
            mMassage.setError("Last name is required");
            return false;
        } else {
            mMassage.setError(null);
            return true;
        }
    }

    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    /*public void btnsubmit_data(View view) {

        storage = FirebaseStorage.getInstance();
        mstorageReference = storage.getReference();
        uploadImage();


       // mDatabaseref = FirebaseDatabase.getInstance().getReference().child("reportmsg").child(uid);

        if(!validate_message()){
          return;
            *//*  Toast.makeText(ReportActivity.this, "Plese write about problem", Toast.LENGTH_LONG).show();
        *//*}else {

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
    }


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

