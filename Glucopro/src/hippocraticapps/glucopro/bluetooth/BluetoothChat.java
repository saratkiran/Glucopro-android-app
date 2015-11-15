package hippocraticapps.glucopro.bluetooth;

import hippocraticapps.glucopro.R;
import hippocraticapps.glucopro.database.GlucoDBAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothChat extends Activity {
	private BluetoothAdapter mBlutoothAdapter;
	
	 private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_RESULT = 6;
    public static final int MESSAGE_SN = 7;
    public static final int MESSAGE_PROGRESS_MAX = 8;
    public static final int MESSAGE_PROGRESS = 9;
        

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_MAC = "device_mac";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    //private TextView mTitle;
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    private Button mGetRecord;
    private Button mSNButton;
    private RadioButton rd1;
    private RadioButton rd2;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    
    private ProgressBar mProgress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_main);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("OnCreateStatus","Adapter Initialized");
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }
    
    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
        case R.id.secure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        case R.id.insecure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            return true;
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
        }
        return false;
    }
	
    private void setupChat() {
        Log.d(TAG, "setupChat()");
        
        mProgress = (ProgressBar) findViewById(R.id.progressBar1);

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        //mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        //mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_close);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	mChatService.closeConnection();
            }
        });
        
        mSNButton = (Button) findViewById(R.id.btn_GetSN);
        mSNButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	mChatService.getSerial();
            }
        });
        
        rd1 = (RadioButton)findViewById(R.id.radio0);
        rd2 = (RadioButton)findViewById(R.id.radio1);

        
        Button mUnitsButton = (Button) findViewById(R.id.btnSetUnits);
        mUnitsButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if (rd1.isChecked()) {
            		mChatService.setUnits(0);
            	} else {
            		mChatService.setUnits(1);
            	}
            }
        });
        
        mGetRecord = (Button) findViewById(R.id.btn_reading);
        mGetRecord.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	try {
					//mChatService.readDataFromMeter();
					mChatService.GetGlucodata();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                // Send a message using content of the edit text widget
                //TextView view = (TextView) findViewById(R.id.edit_text_out);
                //String message = view.getText().toString();
                //sendMessage(message);
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler, GlucoDBAdapter.getInstance());
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
    
    
    
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    //mTitle.setText(R.string.title_connected_to);
                    //mTitle.append(mConnectedDeviceName);
                	Log.d("HANDLER","connected");
                    mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    //mTitle.setText(R.string.title_connecting);
                    Log.d("HANDLER","connecting");
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    //mTitle.setText(R.string.title_not_connected);
                	Log.d("HANDLER","listening");
                    break;
                }
                break;
            case MESSAGE_WRITE:
            	Log.d("HANDLER","writing");
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                mConversationArrayAdapter.add("Me:  " + writeMessage);
                Log.d("DATA TO WRITE",writeMessage);
                break;
            case MESSAGE_READ:
            	Log.d("HANDLER","read");
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                Log.d("DATA TO READ",readMessage);
                break;
            case MESSAGE_DEVICE_NAME:
            	Log.d("HANDLER","device name");
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_RESULT:
            	Log.d("HANDLER","result");
                //Toast.makeText(getApplicationContext(), msg.getData().getString("result"),Toast.LENGTH_SHORT).show();
                mConversationArrayAdapter.add(msg.getData().getString("result"));
                break;
            case MESSAGE_SN:
            	Log.d("HANDLER","message sn and mac");
                mConversationArrayAdapter.add("Serial Number: "+msg.getData().getString("sn"));
                mConversationArrayAdapter.add("Mac Address: "+msg.getData().getString(DEVICE_MAC));
                break;                
            case MESSAGE_PROGRESS_MAX:
            	Log.d("HANDLER","progress max");
            	mProgress.setProgress(0);
            	mProgress.setMax(msg.getData().getInt("progressMax"));
                break;    
            case MESSAGE_PROGRESS:
            	Log.d("HANDLER","progress");
            	mProgress.incrementProgressBy(1);
                break;    
            case MESSAGE_TOAST:
            	Log.d("HANDLER","a toast!");
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
            
        }
        
    };
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
        	setupChat();
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
    	Log.d("CONNECTING DEVICE","Begin Connection");
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        Log.d("CONNECTING DEVICE",address);
        Log.d("ADAPTER_STATUS",mBluetoothAdapter.getName());
        // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        if (mChatService == null){
        	Log.d("mChatService"," IS NULL! ");
        	return;
        }
        mChatService.connect(device, secure);
    }
    
	public void launchConnectionActivity(View view){
		Intent intent = new Intent(this,DeviceListActivity.class);
		startActivity(intent);
	}
	
	private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

}