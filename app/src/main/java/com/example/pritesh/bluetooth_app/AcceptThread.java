package com.example.pritesh.bluetooth_app;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class AcceptThread extends Thread{

    private final BluetoothServerSocket myfinalsocket;

    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    public final Context context;
    public AcceptThread(Context context){
        this.context = context;
        BluetoothServerSocket mysocket =null;
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try {
            mysocket=adapter.listenUsingRfcommWithServiceRecord("com.example.Projectapp2", uuid);
        }catch (IOException e) {}
        myfinalsocket=mysocket;
    }

    public void run(){
        BluetoothSocket socket=null;

        Log.d("AcceptThread", "Inside run method and going inside");

        while(true){
            try {
                Log.d("AcceptThread","Accepting socket");
                socket=myfinalsocket.accept();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d("AcceptThread","exception occurs during accepting in Run");
                break;
            }
            if (socket != null) {
                try {
                    Log.d("AcceptThread","trying to close ServerSocket");
                    myfinalsocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("AcceptThread", "Going to manage socket");
                manageConnecedSocket(socket);
                break;

            }



        }



    }

	private void manageConnecedSocket(BluetoothSocket socket) {

		ConnectedThread connectedThread = new ConnectedThread(socket,this.context);
        connectedThread.start();
        Log.d("AcceptThread","started Connected message through accept thread");

	}

    public void cancel() {
        try {
            myfinalsocket.close();
        } catch (IOException e) { }
    }
}
