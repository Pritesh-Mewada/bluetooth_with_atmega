package com.example.pritesh.bluetooth_app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class ConnectedThread extends  Thread{
    public static BluetoothSocket  socket;
    public static InputStream input;
    public  static OutputStream output;
    public static Context context;

    public ConnectedThread(){
    }
    public ConnectedThread(BluetoothSocket socket,Context context){
        this.socket=socket;
        this.context = context;
        OutputStream tmpo=null;
        InputStream tmpi=null;

        try{
            tmpo=socket.getOutputStream();
            tmpi=socket.getInputStream();

        }catch(IOException e){

        }
        input=tmpi;
        output=tmpo;

        Log.d("ConnectedThread","changes done 2");

        broadcast("connected");

    }
    public void run() {
        // buffer store for the stream
        int bytes,data =0; // bytes returned from read()
        Log.d("ConnectedThread","thread Started");
        // Keep listening to the InputStream until an exception occurs
        while (true)
            try {
            // Read from the InputStream
            byte[] buffer = new byte[1024];
            // bytes = input.read(buffer);
            int i;
            String s = new String();
            StringBuilder builder = new StringBuilder();
            while ((i=input.read())!=(int) '#'){

                s = s+(char)i;
            }
            Log.d("ConnectedThread", s);
                broadcast(s);


            Log.d("ConnectedThread","broadcast send");

            s="";




        } catch (IOException e) {
            Log.d("ConnectedThread", "exception occurs during reading");
            break;
        }
    }

    public void write(byte[] bytes) {
        try {
            output.write(bytes);
        } catch (IOException e) { }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { }
    }

    public void broadcast(String name){
        Intent dataString = new Intent();
        dataString.setAction("pritesh");

        Bundle b = new Bundle();
        b.putString("hello",name);
        dataString.putExtras(b);
        context.sendBroadcast(dataString);

    }
}
