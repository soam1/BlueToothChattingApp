package com.example.bluetoothchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Context context;
    BluetoothManager bluetoothManager ;
    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        initBluetooth();
    }

    private void initBluetooth() {
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "No Bluetooth found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_search_devices) {
            Toast.makeText(context, "searching for devices...", Toast.LENGTH_SHORT).show();
            checkPermissions();
            return true;
        }
        if (item.getItemId() == R.id.menu_enable_bluetooth) {
//            Toast.makeText(context, "Turning on Bluetooth...", Toast.LENGTH_SHORT).show();
            enableBluetooth();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
    private  void checkPermissions(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        else{
            Intent intent = new Intent(context, DeviceListActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_LOCATION_PERMISSION){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(context, DeviceListActivity.class);
                startActivity(intent);

            }else {
                new AlertDialog.Builder(context).setCancelable(false).setMessage("Please grant location permission(required)").setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkPermissions();
                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                    }
                }).show();
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void enableBluetooth() {
        if (bluetoothAdapter.isEnabled())
            Toast.makeText(context, "Bluetooth already enable", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(context, "Turning on Bluetooth", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                bluetoothAdapter.enable();
                // Permission has not been granted, so we need to request it
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
//                Toast.makeText(context, "Could not turn on", Toast.LENGTH_SHORT).show();
                return;
            }else{
                bluetoothAdapter.enable();
            }
        }
        if(bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE){
            Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoveryIntent);
        }
    }




//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(context,"permission granted", Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(context,"permission NOT granted", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


}