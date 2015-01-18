package de.htwg.mc.irdroid.app.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.htwg.mc.irdroid.R;
import de.htwg.mc.irdroid.config.Provider;
import de.htwg.mc.irdroid.database.Repository;
import de.htwg.mc.irdroid.database.implementation.specification.DeviceAllSpecification;
import de.htwg.mc.irdroid.model.Command;
import de.htwg.mc.irdroid.model.CommandType;
import de.htwg.mc.irdroid.model.Device;

public class ManageDeviceActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = ManageDeviceActivity.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private RBLService mBluetoothLeService;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 3000;
    private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<>();
    public static List<BluetoothDevice> mDevices = new ArrayList<>();

    private String mDeviceName;
    private String mDeviceAddress;


    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    /**
     * Name of the connected device
     */
    private String connectedDeviceName = null;

    private Repository<Device> deviceRepository = Provider.getInstance().getFactory().provideDevice();
    private Device device;
    private Spinner deviceSpinner;
    private CommandType currentCommandType;

    private BroadcastReceiver mGattUpdateReceiver;

    private StringBuilder code = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_device);

        deviceSpinner = (Spinner) findViewById(R.id.spinner_manage_device);
        deviceSpinner.setOnItemSelectedListener(this);
        updateSpinner();

        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mGattUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                switch (action) {
                    case RBLService.ACTION_GATT_DISCONNECTED:
                        Log.i(TAG, "Received code: " + code.toString());
                        Toast.makeText(getBaseContext(), "Received code !", Toast.LENGTH_SHORT).show();
                        break;
                    case RBLService.ACTION_GATT_SERVICES_DISCOVERED:
                        getGattService(mBluetoothLeService.getSupportedGattService());
                        break;
                    case RBLService.ACTION_DATA_AVAILABLE:
                        storeData(intent.getByteArrayExtra(RBLService.EXTRA_DATA));
                        break;
                }
            }
        };
    }

    private void setupBtConnection(Intent data) {

        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        mDeviceName = device.getName();
        mDeviceAddress = device.getAddress();

        ServiceConnection mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName,
                                           IBinder service) {
                mBluetoothLeService = ((RBLService.LocalBinder) service).getService();
                if (!mBluetoothLeService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                    finish();
                }
                // Automatically connects to the device upon successful start-up
                // initialization.
                mBluetoothLeService.connect(mDeviceAddress);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mBluetoothLeService = null;
            }
        };

        Intent gattServiceIntent = new Intent(this, RBLService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }


    private void storeData(byte[] byteArrayExtra) {
        Log.d("BLE DATA", new String(byteArrayExtra));
        code.append(new String(byteArrayExtra));
    }

    private void getGattService(BluetoothGattService gattService) {
        if (gattService == null)
            return;

        BluetoothGattCharacteristic characteristic = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
        map.put(characteristic.getUuid(), characteristic);

        BluetoothGattCharacteristic characteristicRx = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
        mBluetoothLeService.setCharacteristicNotification(characteristicRx,
                true);
        mBluetoothLeService.readCharacteristic(characteristicRx);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mGattUpdateReceiver != null) {
            unregisterReceiver(mGattUpdateReceiver);
        }

        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
            mBluetoothLeService.close();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // should not be possible, just to be sure
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    setupBtConnection(data);
                }
                break;
            /*case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupBluetoothService();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                }*/
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_new:
                showNewDevicePopup();
                return true;
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * ask for new device name in popup
     */
    private void showNewDevicePopup() {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(ManageDeviceActivity.this)
                .setTitle(getString(R.string.add_device))
                .setMessage("Name:")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        addDevice(input.getText().toString());
                    }
                }).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    /**
     * update the deviceSpinner
     */
    private void updateSpinner() {
        List<Device> devices = deviceRepository.read(new DeviceAllSpecification());

        ArrayAdapter<Device> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, devices);
        //adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        deviceSpinner.setAdapter(adapter);
        device = (Device) deviceSpinner.getSelectedItem();
    }

    /**
     * add a new device to the repo and refresh spinner
     *
     * @param name name of new device
     */
    private void addDevice(String name) {
        Device newDevice = new Device(name);
        deviceRepository.create(newDevice);
        updateSpinner();
    }

    /**
     * try to get data via bluetooth
     *
     * @param view calling view
     */
    public void attemptSetCommand(View view) {
        switch (view.getId()) {
            case R.id.bManagePower:
                currentCommandType = CommandType.power;
                addCommandToCurrentDevice(code.toString());
                Log.i(TAG, "managing power button for device " + device);
                break;
            default:
                Log.i(TAG, "no such button");
                break;
        }
    }

    /**
     * split command string into int[] pattern and add it to the current device
     *
     * @param commandString command string to add
     */
    private void addCommandToCurrentDevice(String commandString) {
        List<String> list = new ArrayList<>(Arrays.asList(commandString.split(" ")));
        list.remove(0); // dummy
        int frequency = (int) (1000000 / ((Integer.parseInt(list.remove(0), 16) * 0.241246)));
        list.remove(0); // seq1
        list.remove(0); // seq2

        int[] pattern = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            pattern[i] = Integer.decode("0x" + (list.get(i)));
        }

        Command command = new Command(frequency, pattern);
        device.addCommand(currentCommandType, command);
        deviceRepository.update(device);
        Log.i(TAG, "updated device: " + device.getName() + " with command: " + currentCommandType.name());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        device = (Device) parent.getSelectedItem();
        Log.d(TAG, "selected device " + device.getName());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        device = null;
        Log.d(TAG, "no device selected");
    }
}
