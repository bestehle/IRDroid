package de.htwg.mc.irdroid.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import de.htwg.mc.irdroid.R;
import de.htwg.mc.irdroid.config.Provider;
import de.htwg.mc.irdroid.database.Repository;
import de.htwg.mc.irdroid.database.implementation.specification.DeviceAllSpecification;
import de.htwg.mc.irdroid.model.Device;

public class ManageDeviceActivity extends ActionBarActivity {
    private static final String TAG = ManageDeviceActivity.class.getSimpleName();

    private Repository<Device> deviceRepository = Provider.getInstance().getFactory().provideDevice();
    private Device device;
    private Spinner deviceSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_device);

        deviceSpinner = (Spinner) findViewById(R.id.spinner_manage_device);
        updateSpinner();
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
            default:
                return super.onOptionsItemSelected(item);
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
        //device = newDevice;
        // TODO set selected spinner item to new device
    }
}
