package com.nine.jay_joints.boost;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.nio.Buffer;
import java.util.Collections;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    String message = null;

    TextView tv;
    EditText ed;
    FloatingActionButton fab;
    BufferedReader reader;
    Socket socket;

    public final static String TAG = "ClientHandle";

    public void receiveMessage(String message) {
        Log.i(TAG,"Inside receive method" + message);
        tv.setText(message);
    }

    public void sendMessage(final PrintWriter writer) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writer.println(ed.getText());
                writer.flush();
                ed.setText("");
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv = (TextView) findViewById(R.id.textview);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        ed = (EditText) findViewById(R.id.edittext);
        ed.setHint("Write something..");

        Thread netWorkThread = new Thread(new HandleNetworkThread(this));
        netWorkThread.start();



        tv.setText("Connecting...");
        tv.setWidth(700);



        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface ni : Collections.list(nis)) {
                Enumeration<InetAddress> address = ni.getInetAddresses();
                for(InetAddress ad : Collections.list(address)) {
                    Log.i(TAG, ad.getHostAddress());
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        }

    }


    public class HandleNetworkThread implements Runnable {

        MainActivity activity;

        public HandleNetworkThread(MainActivity activity) {
            this.activity = activity;
        }

        public String mss;

        @Override
        public void run() {

            while(true) {
                try {
                    InetAddress address = InetAddress.getByName("192.168.43.228");
                    final Socket socket = new Socket(address, 12060);//192.168.42.146//105.0.5.77//192.168.43.228//10.118.33.217 105.12.0.10
                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader reader = new BufferedReader(inputStreamReader);

                    final PrintWriter writer = new PrintWriter(socket.getOutputStream());

                    InetAddress add = socket.getInetAddress();

                    Log.i(TAG,add.toString());

                    while ((message = reader.readLine()) != null) {
                        mss = message;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.receiveMessage(message);

                            }
                        });
                        activity.sendMessage(writer);
                        Log.i(TAG, mss);

                    }

                } catch (IOException io) {
                    io.printStackTrace();
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
