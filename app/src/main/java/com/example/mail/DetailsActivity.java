package com.example.mail;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {
    //lh-改成textview
    private TextView edfrom;
    private TextView edto;
    private TextView eddate;
    private TextView edsubject;
    private TextView edcontent;
    private TextView btReturn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //找到控件
        btReturn = findViewById(R.id.returnbtn);
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
        btReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}