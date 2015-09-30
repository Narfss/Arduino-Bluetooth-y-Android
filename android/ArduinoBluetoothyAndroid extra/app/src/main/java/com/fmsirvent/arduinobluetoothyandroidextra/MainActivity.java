/*
 * Copyright Francisco M Sirvent 2015 (@narfss)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Francisco M Sirvent <narfss@gmail.com>
 */
package com.fmsirvent.arduinobluetoothyandroidextra;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String SPP_UUID = "00001101-0000-1000-8000-00805f9b34fb";
    private static final int SELECT_BT = 2;
    private static final int ARDUINO_DATA = 3;
    private BluetoothDevice bluetoothDevice = null;
    private ConnectedThread connectedThread = null;
    private Handler handler = null;
    private TextView hiTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, SelectBluetoothDeviceActivity.class);
        startActivityForResult(intent, SELECT_BT);
        configureSwitch();
        configureHi();
    }

    private void configureHi() {
        hiTextView = (TextView) findViewById(R.id.text1);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == ARDUINO_DATA) {
                    byte[] streamData = (byte[]) msg.obj;
                    String streamString = new String(streamData, 0, msg.arg1);
                    int visibility = (hiTextView.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                    hiTextView.setText(streamString);
                    hiTextView.setVisibility(visibility);
                }
                return false;
            }
        });
    }

 private void configureSwitch() {
     Switch lightSwitch = (Switch) findViewById(R.id.switch1);
     lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             if (connectedThread != null) {
                 String outputData;
                 if (isChecked) {
                     outputData = "ON";
                 } else {
                     outputData = "OF";
                 }
                 connectedThread.write(outputData);
             }
         }
     });
 }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== SELECT_BT
                && data != null
                && data.getExtras() != null) {
            bluetoothDevice =
                    data.getExtras().getParcelable(SelectBluetoothDeviceActivity.SELECTED_BLUETOOTH);
            openBluetooth();
        } else {
            finish();
        }
    }

    void openBluetooth() {
        UUID uuid = UUID.fromString(SPP_UUID);
        BluetoothSocket socket = null;
        try {
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            connectedThread = new ConnectedThread(socket);
            connectedThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (socket != null) {
            Toast.makeText(this, "BT  << " + bluetoothDevice.getName() + " >> is now open ", Toast.LENGTH_LONG).show();
        }
    }

private class ConnectedThread extends Thread {
    private InputStream inputStream;
    private OutputStream outputStream;

    public ConnectedThread(BluetoothSocket socket) {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException ignored) { }
    }

    public void run() {
        byte[] buffer = new byte[2];

        while (true) {
            try {
                int bytes = inputStream.read(buffer);
                handler.obtainMessage(ARDUINO_DATA, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    public void write(String message) {
        try {
            outputStream.write(message.getBytes());
        } catch (IOException ignored) { }
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
