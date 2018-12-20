package com.mmproduction.emsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mSubjectEdittext,mMessageEdittext;
    private Button mEmailSubmitButton;
    String[] to = {"milanmiyani11@gmail.com","parammoradiya98@gmail.com"
            ,"akashgolakiya501@gmail.com","rajnigujarati567@gmail.com",
            "jaimiknavadiya@gmail.com"};
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        mSubjectEdittext = (EditText)findViewById(R.id.email_subject_edittext);
        mMessageEdittext = (EditText)findViewById(R.id.email_message_edittext);
        mEmailSubmitButton = (Button)findViewById(R.id.email_submit);
        mProgressDialog = new ProgressDialog(FeedbackActivity.this);


        mToolbar = (Toolbar) findViewById(R.id.Register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("FEEDBACK");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmailSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.setMessage("Wait a Second");
                mProgressDialog.show();
                mProgressDialog.setCanceledOnTouchOutside(false);

                String subject = mSubjectEdittext.getText().toString();
                String message = mMessageEdittext.getText().toString();

                if(CheckNetwork.isInternetAvailable(FeedbackActivity.this)){
                if(subject.isEmpty()&& message.isEmpty()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(FeedbackActivity.this,"please fill detail first",Toast.LENGTH_LONG).show();
                }
                else{
                    mProgressDialog.dismiss();
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setData(Uri.parse("mailto:"));
                    email.putExtra(Intent.EXTRA_EMAIL, to);
                    email.putExtra(Intent.EXTRA_SUBJECT, subject);
                    email.putExtra(Intent.EXTRA_TEXT, message);

                    email.setType("message/rfc822");

                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                }}
                else {
                    mProgressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(mEmailSubmitButton, "No Internet Connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }
}
