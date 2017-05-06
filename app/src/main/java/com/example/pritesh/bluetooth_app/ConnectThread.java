package com.example.pritesh.bluetooth_app;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ConnectThread extends Thread{
    BluetoothAdapter myadapter=BluetoothAdapter.getDefaultAdapter();
    private final BluetoothSocket mysocket;
    private final BluetoothDevice mydevice;
    public  final Context context;



    public ConnectThread(BluetoothDevice device,Context context){
        BluetoothSocket tmp = null;
        this.mydevice = device;
        this.context = context;

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try {
            tmp = this.mydevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mysocket=tmp;
    }
    public void run(){
        myadapter.cancelDiscovery();

        try {
            Log.d("ConnectThread", "connecting thread");
            mysocket.connect();
            Log.d("ConnectThread", "connected to the device");

        } catch (IOException e) {
            try {
                Log.d("ConnectThread", "Trying to close the socket ");
                mysocket.close();
            } catch (IOException v) {

            }
            return;
        }
        Log.d("ConnectThread", "Going out of Run");
        manageConnecedSocket(mysocket);
    }
    public void close(){
        try {
            mysocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void manageConnecedSocket(BluetoothSocket socket) {
        Log.d("ConnectThread","managing the connection through ConnectThread");
        ConnectedThread connectedThread = new ConnectedThread(socket,this.context);
        connectedThread.start();

        String name ="priteshmew#";
        Log.d("ConnectThread",name +"  " +"is the string");
        connectedThread.write(name.getBytes());
        Log.d("ConnectThread","written bytes into inputstream");

    }
}


