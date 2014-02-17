package com.thenathanjones.ibeacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by thenathanjones on 24/01/2014.
 */
public class IBeaconService implements BluetoothAdapter.LeScanCallback {

    private static final String TAG = IBeaconService.class.getName();

    private final Context mContext;
    private final Collection<IBeaconListener> mListeners = new ArrayList<IBeaconListener>();
    private Map<String, IBeacon> mKnownBeacons = new HashMap<String, IBeacon>();
    private Timer mTimer;

    public IBeaconService(Context context) {
        mContext = context;
    }

    public void registerListener(IBeaconListener listener) {
        if (mListeners.isEmpty()) {
            startScanning();
        }

        mListeners.add(listener);
    }

    public void unregisterListener(IBeaconListener listener) {
        mListeners.remove(listener);

        if (mListeners.isEmpty()) {
           stopScanning();
        }
    }

    private void startScanning() {
        if (bluetoothAdapter().isEnabled()) {
            bluetoothAdapter().startLeScan(this);

            startListenerUpdates();
        }
        else {
            Log.w(TAG, "Bluetooth is disabled, unable to scan.");
        }
    }

    private void startListenerUpdates() {
        mTimer = new Timer();
        TimerTask updateListeners  = new TimerTask() {
            @Override
            public void run() {
                updateListenersWith(mKnownBeacons.values());
            }
        };

        mTimer.scheduleAtFixedRate(updateListeners, IBeaconConstants.UPDATE_PERIOD, IBeaconConstants.UPDATE_PERIOD);
    }

    private void updateListenersWith(Collection<IBeacon> beacons) {
        cullStaleBeacons(beacons);

        for (IBeaconListener listener : mListeners) {
            listener.beaconsFound(mKnownBeacons.values());
        }
    }

    private void cullStaleBeacons(Collection<IBeacon> beacons) {
        long now = System.currentTimeMillis();
        Collection<IBeacon> toRemove = new HashSet<IBeacon>();
        for (IBeacon beacon : beacons) {
            if ((now - beacon.lastReport()) > IBeaconConstants.CULL_DELAY) {
                toRemove.add(beacon);
            }
        }

        for (IBeacon beacon : toRemove) {
            mKnownBeacons.remove(beacon.hash);
        }
    }

    private void stopScanning() {
        bluetoothAdapter().stopLeScan(this);

        stopListenerUpdates();
    }

    private void stopListenerUpdates() {
        mTimer.cancel();
    }

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter bluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        return mBluetoothAdapter;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        parseBeaconFrom(rssi, scanRecord);
    }

    private void parseBeaconFrom(int rssi, byte[] scanRecord) {
        if (IBeacon.isBeacon(scanRecord)) {
            Log.d(TAG, "Congratulations, you have found an iBeacon!");
            IBeacon scannedBeacon = IBeacon.from(scanRecord);

            IBeacon existingBeacon = mKnownBeacons.get(scannedBeacon.hash);
            scannedBeacon.calculateDistanceFrom(rssi, existingBeacon);
            Log.d(TAG + ":distanceVars", scannedBeacon.minor + "," + scannedBeacon.txPower + "," + rssi);

            mKnownBeacons.put(scannedBeacon.hash, scannedBeacon);
            Log.d(TAG, "Beacon " + scannedBeacon.hash + " located approx. " + scannedBeacon.distanceInMetres() + "m");
        }
        else {
            Log.d(TAG, "Record is not an iBeacon");
        }
    }
}
