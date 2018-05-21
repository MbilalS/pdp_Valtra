package com.valtra.valtraapp.activities;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
//import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.UUID;

//import static android.support.v4.app.ActivityCompat.startActivityForResult;


public class BluetoothService extends Service {

    ConnectedThread ct;

    @Override
    public void onCreate() {
        UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        super.onCreate();
        //bluetooth stuff
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableAdapter, null);
        }

        BluetoothDevice device = null;
        boolean found=false;

        if(bluetoothAdapter.getBondedDevices().isEmpty()) {

            Toast.makeText(getApplicationContext(),"Please Pair the Device first",Toast.LENGTH_SHORT).show();

        } else {

            for (BluetoothDevice iterator : bluetoothAdapter.getBondedDevices()) {

                if(iterator.getName().equals("RNBT-8CB2") || iterator.getName().equals("RNBT-8C3D")) //Replace with iterator.getName() if comparing Device names.

                {

                    device=iterator; //device is an object of type BluetoothDevice

                    found=true;

                    break;

                } } }
        BluetoothSocket socket;
        //OutputStream outputStream;
        //InputStream inputStream;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            try {
                socket.connect();
            }catch (IOException e) {
                e.printStackTrace();
            }
            //outputStream=socket.getOutputStream();
            //inputStream=socket.getInputStream();
            ct = new ConnectedThread(socket);

//
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
                    ct.run();
//                }
//            }, 5000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                //Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                //Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            final int[] numBytes = new int[1]; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            new Thread(new Runnable() {
                public void run() {
                    while (true)
                    {
                        if(!mmSocket.isConnected())
                        {
                            try {
                                mmSocket.connect();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            // Read from the InputStream.
                            numBytes[0] = mmInStream.read(mmBuffer); //here the data is correct, but UI-thread is frozen
                            //send data to MainActivity
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra("acceleration_values", mmBuffer);
                            Log.d("mmBuffer = ", "" + mmBuffer);

                            LocalBroadcastManager.getInstance(BluetoothService.this).sendBroadcast(intent);

                            //mmInStream.reset();

                            // Send the obtained bytes to the UI activity.
                            /*Message readMsg = mHandler.obtainMessage(
                                    MessageConstants.MESSAGE_READ, numBytes, -1,
                                    mmBuffer);
                            readMsg.sendToTarget();*/
                        } catch (IOException e) {
                            //Log.d(TAG, "Input stream was disconnected", e);
                            break;
                        }
                    }
                }
            }).start();
        }

        // Call this from the main activity to send data to the remote device.
        /*public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }*/

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

//    public void onDestroy(){
//        ct.cancel();
//        super.onDestroy();
//    }

}
