package hippocraticapps.glucopro.bluetooth;

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import hippocraticapps.glucopro.database.GlucoDBAdapter;
import hippocraticapps.glucopro.database.SugarRecord;
import hippocraticapps.glucopro.database.SugarTable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import glucopro.sqlhelper.*;
import glucopro.sqlmodel.Databasemanager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothChatService extends Activity {
    // Debugging
    private static final String TAG = "BluetoothChatService";
    private static final boolean D = true;

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    //private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    //private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  
    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    GlucometerInput gi;
    // custom code declarations
    private int num_records;
    private final static byte STX = (byte) 0x80;
    private final static byte CMD_NUM_RECORDS = (byte)0x00;
    private final static byte CMD_UNITS = (byte)0x05;
    private final static byte CMD_RECORD = (byte)0x01;
    private final static byte CMD_SERIAL = (byte)0x09;

    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    Databasemanager db;
 

    private final static int WRITE_RETRIES = 5;
    private final static int WRITE_AVAILABLE_RETRIES = 50;
    private final static int WRITE_TIMEOUT = 50;
    private final static int READ_AVAILABLE_RETRIES = 50;
    private final static int READ_TIMEOUT = 50;
    private final static int DIALOG_RETRIES_IN_CASE_OF_WRONG_RESPONCE = 3;
    public static final int DIALOG_PROGRESS = 2;

    private Activity mActivity;
    private GlucoDBAdapter mAdptr;
    private int connectState;
    private String mac;
    //private ProgressDialog mDialogProgress;
    
    SugarRecord sugarR;
    SugarTable sTable;
    GlucometerInput newentry;
    /**
     * Constructor. Prepares a new BluetoothChat session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public BluetoothChatService(Context context, Handler handler, GlucoDBAdapter adptr) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
        mActivity = (Activity) context;
        
        mAdptr = adptr;
        sTable = new SugarTable();
    }



    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }



    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }



    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        if (D)
            Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }

        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
    }



    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
    	Log.d("ALERT!","GOT HERE!");
        if (D)
        	Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }



    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        if (D)
            Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        mac = device.getAddress();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }



    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D)
            Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        setState(STATE_NONE);
    }



    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }

        // Perform the write unsynchronized
        writeToMeter(out);
        //r.write(out);
    }



    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothChat.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothChatService.this.start();
    }



    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothChat.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothChatService.this.start();
    }



    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;


        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure":"Insecure";

            // Create a new listening server socket
            try {
                if (secure)
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
                        MY_UUID_SECURE);
                else
                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                            NAME_INSECURE, MY_UUID_INSECURE);
            }
            catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }

            mmServerSocket = tmp;
        }



        public void run() {
            if (D)
                Log.d(TAG, "Socket Type: " + mSocketType + "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                }
                catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothChatService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                Log.d(TAG,socket.getRemoteDevice().getName()+" Socket Type: "+mSocketType);
                                connected(socket, socket.getRemoteDevice(),
                                        mSocketType);
                                break;

                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                }
                                catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }

            if (D)
                Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);
        }



        public void cancel() {
            if (D)
                Log.d(TAG, "Socket Type: " + mSocketType + " :cancel " + this);

            try {
                mmServerSocket.close();
            }
            catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + " :close() of server failed", e);
            }
        }
    }



    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure)
                    tmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID_SECURE);
                else
                    tmp = device.createInsecureRfcommSocketToServiceRecord(
                            MY_UUID_INSECURE);
            }
            catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }

            mmSocket = tmp;
        }



        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            }
            catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                }
                catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }

                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }



        public void cancel() {
            try {
                mmSocket.close();
            }
            catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }



    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Looper.prepare();
            Log.d(TAG,"create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) {
                Log.d(TAG,"temp sockets not created: " + e.getMessage());
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }



        @Override
        public void run() {
            Looper.prepare();

            try  {
                ((BluetoothConnectListener)mActivity).onBluetoothConnected(BluetoothChatService.this, mState);
            }
            catch (Exception e) {
                Log.d(TAG,"Error in connected thread: " + e.getMessage());
                //e.printException(e);
            }

            if(mState == BluetoothConnectListener.STATE_LINK) {
                //mDialogHandler.sendEmptyMessage(MSG_DIALOG_CONNECT_HIDE);
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                mConnectedThread = null;
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            }
            catch (IOException e) {
                Log.d(TAG,"close() of connect socket failed: "+ e);
            }
        }
    }



    // ===== Beginning of custom code ============================

    public void GetGlucodata() {
        Thread mGetDataThread = new GetDataThread();
        //mDialogHandler.sendEmptyMessage(MSG_DIALOG_PROGRESS_SHOW);
        mGetDataThread.start();
    }



    public class GetDataThread extends Thread {
        public GetDataThread() {

        }

        public void run() {
            try {
                Boolean tmp = readDataFromMeter();
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }



    public void closeConnection() {
        try {
            mmSocket.close();
            connectionLost();
        }
        catch (IOException e) {
            Log.d(TAG,"close() of connect socket failed: " + e.getMessage());
        }
    }

    /**
     * Write to the connected OutStream.
     * @param buffer  The bytes to write
     */
    private boolean writeToMeter(byte[] send){
        try {
            if((mmOutStream != null) && (mmInStream != null)){
//              if (mmInStream.available() > 0) return true;
                if (mmInStream.available() > 0) {
                    Log.d(TAG,"There are " + mmInStream.available() + " bytes in input stream, skip it");
                    mmInStream.skip(mmInStream.available());
                }

                for (int j = 0; j < WRITE_RETRIES; j++) {
                    mmOutStream.write(send);
                    Log.d(TAG,"Wrote " + send.length + " bytes");
//                  mmOutStream.flush();
                    for (int i = 0; i < WRITE_AVAILABLE_RETRIES; i++) {
                        if (mmInStream.available() > 0) return true;
                        Thread.sleep(WRITE_TIMEOUT);
                    }
                    Log.d(TAG,"No answer, repeat...");
                }

                return false;
            }
            else {
                Log.d(TAG,"Can not write to meter");
            }
        } catch (Exception e) {
            Log.d(TAG,"Error while write to meter: " + e.getMessage());
            //Logger.printException(e);
        }
        return false;
    }

    private byte[] readFromMeter(int paramInt) throws Exception
    {
        int i;
        byte[] arrayOfByte = new byte[paramInt];
        Log.d(TAG,"Need to read " + paramInt + " bytes, available " + mmInStream.available() + " bytes");
        for (i = 0; i < paramInt; ++i)
        {
            for (int k = 0; k < READ_AVAILABLE_RETRIES; k++) {
                if (mmInStream.available() > 0)
                    break;
                Thread.sleep(READ_TIMEOUT);
            }
            if (mmInStream.available() == 0)
                return null;

          int j = mmInStream.read();
          if (j == -1)
            throw new IOException();
          arrayOfByte[i] = (byte)j;
        }

        Log.d(TAG,"Read " + i + " bytes");
//      for (int t = 0; t < i; t++) {
//          Logger.debug(" "+arrayOfByte[t]);
//      }
        return arrayOfByte;
    }

    private byte[] dialogWithMeter(byte[] send, int noOfBytes) throws Exception{
        byte receive[];
        for (int i = 0; i < DIALOG_RETRIES_IN_CASE_OF_WRONG_RESPONCE; i++) {
            boolean isWritten = writeToMeter(send);
            if(!isWritten)
                throw new IOException("Unable to write into device");


            receive = readFromMeter(noOfBytes);

            /* Corrupted or no data */
            if (receive == null) {
                /* Try again */
                Thread.sleep(100);
                continue;
            }

            if(receive[3] == send[3]){
                return receive;
            }
            Log.d(TAG,"Wrong: req:" + send[3]+" res:" + receive[3]);
            for (int j = 0; j < noOfBytes; j++) {
                Log.d(TAG," "+receive[j]);
            }
            /* Try again */
            Thread.sleep(100);
        }
        throw new IOException("Wrong response from device");
    }



    public boolean readDataFromMeter() throws Exception {
    	
    	
    	
    	Message msg;
        Bundle bundle;
        Record record;
        Record lastRecord = null;

        int recordsCount = 0;

        byte  sc_sendBuf1[] = new byte[6];
        byte  sc_receiveBuf1[] = new byte[7];

        // get the number of records
        sc_sendBuf1[0] = STX;//stx
        sc_sendBuf1[1] = (byte)0x01;//size
        sc_sendBuf1[2] = (byte) ~ ( sc_sendBuf1[1] );//~size
        sc_sendBuf1[3] = CMD_NUM_RECORDS;//command
        sc_sendBuf1[4] = (byte) ~(sc_sendBuf1[0] ^ sc_sendBuf1[2]); //checksum L
        sc_sendBuf1[5] = (byte) ~(sc_sendBuf1[1] ^ sc_sendBuf1[3]); // checksum H
        try{
            sc_receiveBuf1 = dialogWithMeter(sc_sendBuf1, 7);
        }
        catch(Exception e)
        {
            closeConnection();
            //Logger.printException(e);
            return false;
        }
        num_records = sc_receiveBuf1[4];
        Log.d(TAG,"NUM_REC = " + num_records);
        msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_PROGRESS_MAX);
        bundle = new Bundle();
        bundle.putInt("progressMax", num_records);
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        String currentSync = "";
        //String lastSync = AppConfig.getInstance().getLastSync();
        //Results.getInstance().clear();
        byte  sc_sendBuf2[] = new byte[7];
        byte  sc_receiveBuf2[] = new byte[13];

        while( true ) {
            /**
             * one record request, 0x01
             */
            sc_sendBuf2[0] = STX;//stx
            sc_sendBuf2[1] = (byte)0x02;//size
            sc_sendBuf2[2] = (byte) ~ ( sc_sendBuf2[1] );//~size
            sc_sendBuf2[3] = CMD_RECORD;//command
            sc_sendBuf2[4] = (byte)recordsCount;//data
            sc_sendBuf2[5] = (byte) ~((sc_sendBuf2[0] ^ sc_sendBuf2[2]) ^ sc_sendBuf2[4]); //checksum L
            sc_sendBuf2[6] = (byte) ~(sc_sendBuf2[1] ^ sc_sendBuf2[3]); // checksum H

            try{
                sc_receiveBuf2 = dialogWithMeter(sc_sendBuf2, 13);
            }
            catch(Exception e)
            {
                //Logger.printException(e);
                closeConnection();
                return false;
            }

            record = new Record(sc_receiveBuf2, 5);

            if ( record.getResult() == 0 ) {
                if ( recordsCount == 0 ) {
                    Log.d(TAG,"Error, meter records not found!");
                }
                /* No data in meter */
                break;
            } else {
                if ( record.equals(lastRecord) ) {
                    lastRecord = record; /* the same record, try again */
                } else {
                    lastRecord = record;

                    Log.d(TAG,"Record " + Integer.toString(recordsCount));

                    //Results.getInstance().addRecord(record);
                    // Send the reading results to the UI Activity
                    msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_RESULT);
                    bundle = new Bundle();
                    bundle.putString("result", record.toString());
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);

                    //Toast.makeText(mActivity, record.toString(), Toast.LENGTH_SHORT).show();
                    recordsCount++;

                    currentSync = record.convertToLastSync();
                    //if(currentSync.equals(lastSync)){
                    //  break;
                    //}
                    
                    int yr = 2000+record.getYear();
                    int mo = record.getMon();
                    int dy = record.getDay();
                    int hr = record.getHour();
                    int mn = record.getMin();
                    Log.d("DATE INFORMATION",yr+"/"+mo+"/"+dy+"  "+9+":"+mn);
                    
                    GregorianCalendar gc = new GregorianCalendar(yr,mo,dy,hr,mn,0);
                    Log.d("george",String.valueOf(gc));
                    int sugarLevel = record.getResult();
                    int temp = record.getTemp();
                    int pre = record.getEvent();
                    Log.d("READING TIME OBJECT",""+gc.getTimeInMillis());
                    Log.d("Sugar Level: ",""+sugarLevel);
                    Log.d("Event?: ",""+pre);
                    if (record.getEvent() == 8)
                    	pre = 0;
                    else
                    	pre = 1;
                    
                    sugarR = new SugarRecord( 0, 1, pre, (float)sugarLevel, gc.getTimeInMillis()/1000 );
                    sTable.insert(mAdptr,sugarR);
                    Log.d(TAG,"Read " + recordsCount + " records");
                    db = new Databasemanager(mActivity);
                    gi = new GlucometerInput(sugarLevel,pre,yr,mo,dy,hr,mn);
                    db.glucoentry(gi);
                }
                Thread.sleep(100); /* Required -- ori. 55 */

            }
            //mDialogProgress.incrementProgressBy(1);
			mHandler.sendEmptyMessage(BluetoothChat.MESSAGE_PROGRESS);
			
		}
       
       
        
		//closeConnection(); // TODO check return value ?

		if ( recordsCount == 0 ) {
			return false;
		}
		
		/*int yr = record.getYear();
		int mo = record.getMon();
		int dy = record.getDay();
		int hr = record.getHour();
		int mn = record.getMin();
		Log.d("DATE INFORMATION",yr+"/"+mo+"/"+"/"+dy+"  "+hr+":"+mn);
		
		GregorianCalendar gc = new GregorianCalendar(yr,mo,dy,hr,mn);
		Log.d("READING TIME OBJECT",""+gc.getTimeInMillis());
		
		Log.d(TAG,"Read " + recordsCount + " records");*/
		//mDialogHandler.sendEmptyMessage(MSG_DIALOG_PROGRESS_HIDE);
		return true;
	}
	
    
    
    public boolean getSerial(){
        Message msg;
        Bundle bundle;

        byte  sc_sendBuf[] = new byte[6];
        byte  sc_receiveBuf[] = new byte[15];
        sc_sendBuf[0] = STX;//stx
        sc_sendBuf[1] = (byte)0x01;//size
        sc_sendBuf[2] = (byte) ~ ( sc_sendBuf[1] );//~size
        sc_sendBuf[3] = CMD_SERIAL;//command
        sc_sendBuf[4] = (byte) ~(sc_sendBuf[0] ^ sc_sendBuf[2]); //checksum L
        sc_sendBuf[5] = (byte) ~(sc_sendBuf[1] ^ sc_sendBuf[3]); // checksum H

        try{
            sc_receiveBuf = dialogWithMeter(sc_sendBuf, 15);
            Thread.sleep(100); // Required
        }
        catch(Exception e)
        {
            closeConnection();
            return false;
        }

        String serial = "";
        for (int idx = 4; idx < 14; idx++){
            char symbol = (char)sc_receiveBuf[idx];
            //if(Character.isDigit(symbol))
                serial += symbol;
        }

        msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_SN);
        bundle = new Bundle();
        bundle.putString("sn", serial);
        bundle.putString(BluetoothChat.DEVICE_MAC, mac);
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        Log.e(TAG,"Serial Number" + serial);
        return true;
    }

    public boolean setUnits(int units){

        byte  sc_sendBuf[] = new byte[7];
        byte  sc_receiveBuf[] = new byte[14];
        sc_sendBuf[0] = STX ;//stx
        sc_sendBuf[1] = (byte)0x02;//size
        sc_sendBuf[2] = (byte) ~ ( sc_sendBuf[1] );//~size
        sc_sendBuf[3] = CMD_UNITS;
        sc_sendBuf[4] = (byte)(units);  // DATA: 0x01:mg/dL  0x00:mmol/L
        sc_sendBuf[5] = (byte) ~(sc_sendBuf[0] ^ sc_sendBuf[2] ^ sc_sendBuf[4] );
        sc_sendBuf[6] = (byte) ~(sc_sendBuf[1] ^ sc_sendBuf[3] );

        try {
            sc_receiveBuf = dialogWithMeter(sc_sendBuf, 7);
            Thread.sleep(100); // Required
            Log.e(TAG,"Successfully changed units: "+(byte)(units));
            Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(BluetoothChat.TOAST, "Units set successfully");
            msg.setData(bundle);
            mHandler.sendMessage(msg);
            return true;
        }
        catch (Exception e) {
            Log.e(TAG,"Error : Communication error " + e.getMessage() + e.getClass());
            //Logger.printException(e);
            return false;
        }
    }
}
