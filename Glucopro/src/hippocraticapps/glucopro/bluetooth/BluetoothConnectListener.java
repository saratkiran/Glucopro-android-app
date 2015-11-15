package hippocraticapps.glucopro.bluetooth;

public interface BluetoothConnectListener {
    public static final int STATE_LINK = 0;
    public static final int STATE_READ_RESULTS = 1;

    public abstract void onBluetoothConnected(BluetoothChatService bluetooth, int state);
}
