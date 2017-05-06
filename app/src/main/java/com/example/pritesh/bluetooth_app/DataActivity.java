package com.example.pritesh.bluetooth_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        broadcast("demo by pritesh");

        Button bn =(Button)findViewById(R.id.sendText);


        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"demo",Toast.LENGTH_SHORT).show();
                EditText ed = (EditText)findViewById(R.id.editText);
                ConnectedThread con = new ConnectedThread();
                String sendValue =ed.getText().toString()+"#";
                con.write(sendValue.getBytes());
                ed.setText("");

            }
        });

    }


    public void broadcast(String name){
        Intent dataString = new Intent();
        dataString.setAction("pritesh");

        Bundle b = new Bundle();
        b.putString("hello",name);
        dataString.putExtras(b);
        sendBroadcast(dataString);

    }
}
