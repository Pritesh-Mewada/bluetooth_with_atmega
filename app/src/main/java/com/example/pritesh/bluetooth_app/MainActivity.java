package com.example.pritesh.bluetooth_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static Boolean server=false;
    private static Boolean Connection=false;
    private static final int REQUEST_ENABLE_BT = 1;
    private Button onBtn;
    private Button offBtn;
    private Button listBtn;
    private Button findBtn;
    private TextView text;
    EditText tx;
    Button send;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    BluetoothDevice device;
    private ListView myListView;
    ConnectThread th;
    AcceptThread th1;


    private ArrayAdapter BTArrayAdapter;
    LinkedList<BluetoothDevice> bluetoothDevices =new LinkedList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //REGISTERING RECEIVER
        registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(receiver, new IntentFilter("pritesh"));

        myListView = (ListView)findViewById(R.id.listView1);
        offBtn = (Button)findViewById(R.id.turnOff);
        listBtn = (Button)findViewById(R.id.paired);
        text = (TextView) findViewById(R.id.text);
        onBtn = (Button)findViewById(R.id.turnOn);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        //starting to connect to server
        myListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(myBluetoothAdapter.isEnabled()){
                    device=bluetoothDevices.get(position);
                    th = new ConnectThread(device,MainActivity.this);
                    th.start();
                    Connection=true;
                    Toast.makeText(getApplicationContext(), "connecting to:"+bluetoothDevices.get(position).getName(),Toast.LENGTH_LONG).show();
                }
            }
        });


        // create the arrayAdapter that contains the BTDevices, and set it to the ListView
        BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        myListView.setAdapter(BTArrayAdapter);

        findBtn = (Button)findViewById(R.id.search);

        if(myBluetoothAdapter == null) {

            onBtn.setEnabled(false);
            offBtn.setEnabled(false);
            listBtn.setEnabled(false);
            findBtn.setEnabled(false);
            text.setText("Status: not supported");

            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",Toast.LENGTH_LONG).show();
        } else {

            //turning on bluetooth
            onBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (!myBluetoothAdapter.isEnabled()) {
                        Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);


                        Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
                                Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            //turning off bluetooth
            offBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    myBluetoothAdapter.disable();
                    text.setText("Status: Disconnected");

                    Toast.makeText(getApplicationContext(),"Bluetooth turned off",
                            Toast.LENGTH_LONG).show();

                    if(th!=null){
                        th.close();
                    }
                }
            });

            //Code to connect to open the server
            listBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    myBluetoothAdapter =BluetoothAdapter.getDefaultAdapter();
                    pairedDevices = myBluetoothAdapter.getBondedDevices();
                    bluetoothDevices.clear();
                    BTArrayAdapter.clear();
                    for(BluetoothDevice device2 : pairedDevices){
                        bluetoothDevices.add(device2);
                        BTArrayAdapter.add(device2.getName().toString());
                    }

                    Toast.makeText(getApplicationContext(),"Show Paired Devices",Toast.LENGTH_SHORT).show();
                    BTArrayAdapter.notifyDataSetChanged();
                }
            });



            //finding devices and starting server
            findBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    bluetoothDevices.clear();
                    // TODO Auto-generated method stub
                    if (myBluetoothAdapter.isDiscovering()) {
                        // the button is pressed when it discovers, so cancel the discovery
                        myBluetoothAdapter.cancelDiscovery();
                    }
                    else {
                        BTArrayAdapter.clear();
                        myBluetoothAdapter.startDiscovery();



                        th1 = new AcceptThread(MainActivity.this);
                        th1.start();
                        Toast.makeText(getApplicationContext(), "Server Initiated", Toast.LENGTH_LONG).show();
                        server=true;
                    }
                }
            });





        }





    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            if(myBluetoothAdapter.isEnabled()) {
                text.setText("Status: Enabled");
            } else {
                text.setText("Status: Disabled");
            }
        }
    }








    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                bluetoothDevices.add(device);
                BTArrayAdapter.notifyDataSetChanged();

            }

        }
    };

    final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getBaseContext(),"i am data receiver",Toast.LENGTH_SHORT).show();
            String action = intent.getAction().toString();
            Bundle b = intent.getExtras();
            String message = b.getString("hello","default value");

            if(action =="pritesh"){

                Toast.makeText(getBaseContext(),message,Toast.LENGTH_SHORT).show();

                if(message.equals("connected")){
                    Intent startActivity = new Intent(getBaseContext(),DataActivity.class);
                    startActivity(startActivity);
                }
            }


        }
    };




    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(bReceiver);
        unregisterReceiver(receiver);

        if(server==true){
            th1.cancel();
        }else if(Connection==true){
            th.close();
        }
    }




}