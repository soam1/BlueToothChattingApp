package com.example.bluetoothchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {
    private ListView listPairedDevices, listAvailableDevices;
    private ArrayAdapter<String> adapterPairedDevices;
    private ArrayAdapter<String> adapterAvailableDevices;
    private  static final int REQUEST_BLUETOOTH_PERMISSION = 101;
    private Context context;


    private BluetoothAdapter bluetoothAdapter;

    private ProgressBar progressScanDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        context = this;
        init();
    }

    private void init() {
        listAvailableDevices = findViewById(R.id.list_available_devices);
        listPairedDevices = findViewById(R.id.list_paired_devices);
        progressScanDevices = findViewById(R.id.progress_scan_devices);

        adapterAvailableDevices = new ArrayAdapter<>(context, R.layout.device_list_item);
        adapterPairedDevices = new ArrayAdapter<>(context, R.layout.device_list_item);

        listPairedDevices.setAdapter(adapterPairedDevices);
        listPairedDevices.setAdapter(adapterAvailableDevices);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices=null;
            pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices!=null && pairedDevices.size()>0){
            for (BluetoothDevice device: pairedDevices){
                adapterPairedDevices.add(device.getName()+"\n"+device.getAddress());
            }
        }
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothDeviceListener, intentFilter);
        IntentFilter intentFilter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothDeviceListener, intentFilter1);
    }


    private BroadcastReceiver bluetoothDeviceListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device =  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState()!=BluetoothDevice.BOND_BONDED){
                    adapterAvailableDevices.add(device.getName()+"\n"+device.getAddress());
                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                progressScanDevices.setVisibility(View.GONE);
                if(adapterAvailableDevices.getCount()==0){
                    Toast.makeText(context, "no new devices found", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "select the device to start the chat", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() ==R.id.menu_scan_devices){
            Toast.makeText(context, "scanning for devices...", Toast.LENGTH_SHORT).show();
            scanDevices();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void scanDevices(){
        progressScanDevices.setVisibility(View.VISIBLE);
        adapterAvailableDevices.clear();
        Toast.makeText(context, "scan started", Toast.LENGTH_SHORT).show();
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }
}