package com.example.mail;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class DetailsActivity extends AppCompatActivity {

    private EditText edfrom;
    private EditText edto;
    private EditText eddate;
    private EditText edsubject;
    private EditText edcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        edfrom = findViewById(R.id.afrom);
        edto = findViewById(R.id.ato);
        eddate = findViewById(R.id.adate);
        edsubject = findViewById(R.id.asubject);
        edcontent = findViewById(R.id.acontent);

        Intent intent = getIntent();
        int openMode = intent.getIntExtra("mode",1);
        if(openMode==3) {

            edfrom.setText(intent.getStringExtra("from"));
            edto.setText(intent.getStringExtra("to"));
            eddate.setText(intent.getStringExtra("date"));
            edsubject.setText(intent.getStringExtra("subject"));
            edcontent.setText(intent.getStringExtra("content"));
        }
    }
}