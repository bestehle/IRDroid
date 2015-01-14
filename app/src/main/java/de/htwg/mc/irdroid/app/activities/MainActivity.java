package de.htwg.mc.irdroid.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.List;
import java.util.Map;

import de.htwg.mc.irdroid.R;
import de.htwg.mc.irdroid.app.IrController;
import de.htwg.mc.irdroid.config.Provider;
import de.htwg.mc.irdroid.database.Repository;
import de.htwg.mc.irdroid.database.implementation.specification.DeviceAllSpecification;
import de.htwg.mc.irdroid.model.Command;
import de.htwg.mc.irdroid.model.CommandType;
import de.htwg.mc.irdroid.model.Device;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private IrController ir;

    private Map<CommandType, Command> commandMap;
    private Repository<Device> deviceRepository;
    private List<Device> devices;
    private Button bPower;
    private Button bVolumeUp;
    private Button bVolumeDown;
    private Button bChannelUp;
    private Button bChannelDown;
    private Button bDigits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ir = new IrController(this);

        bPower = (Button) findViewById(R.id.bPower);
        bPower.setOnClickListener(this);
        bVolumeUp = (Button) findViewById(R.id.bVolUp);
        bVolumeUp.setOnClickListener(this);
        bVolumeDown = (Button) findViewById(R.id.bVolDown);
        bVolumeDown.setOnClickListener(this);
        bChannelUp = (Button) findViewById(R.id.bChannelUp);
        bChannelUp.setOnClickListener(this);
        bChannelDown = (Button) findViewById(R.id.bChannelDown);
        bChannelDown.setOnClickListener(this);
        bDigits = (Button) findViewById(R.id.bDigits);
        bDigits.setOnClickListener(this);

        deviceRepository = Provider.getInstance().getFactory().provideDevice();
        devices = deviceRepository.read(new DeviceAllSpecification());

        updateView();
    }

    private void updateView() {
        Spinner deviceSpinner = (Spinner) findViewById(R.id.spinner_devices);
        ArrayAdapter<Device> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, devices);
        //adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        deviceSpinner.setAdapter(adapter);

        Device device = (Device) deviceSpinner.getSelectedItem();

        commandMap = device.getCommandMap();

        bPower.setEnabled(commandMap.containsKey(CommandType.power));
        bVolumeUp.setEnabled(commandMap.containsKey(CommandType.volumeUp));
        bVolumeDown.setEnabled(commandMap.containsKey(CommandType.volumeDown));
        bChannelUp.setEnabled(commandMap.containsKey(CommandType.channelUp));
        bChannelDown.setEnabled(commandMap.containsKey(CommandType.channelDown));
        bDigits.setEnabled(commandMap.containsKey(CommandType.digits));
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

        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(MainActivity.this, ManageDeviceActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //

    @Override
    public void onClick(View v) {
        Command command = null;
        switch (v.getId()) {
            case R.id.bPower:
                command = commandMap.get(CommandType.power);
                break;
            case R.id.bVolUp:
                command = commandMap.get(CommandType.volumeUp);
                break;
            case R.id.bVolDown:
                command = commandMap.get(CommandType.volumeDown);
                break;
            case R.id.bChannelUp:
                command = commandMap.get(CommandType.channelUp);
                break;
            case R.id.bChannelDown:
                command = commandMap.get(CommandType.channelDown);
                break;
           /* case R.id.bDigits:
                showDigitsField();
                break;*/
            default:

                break;
        }

        if (command != null) {
            ir.sendCode(command.getFrequency(), command.getIrCommand());
        }

    }

    public void logDevices(View view) {
        Repository<Device> deviceRepository = Provider.getInstance().getFactory().provideDevice();
        List<Device> devices = deviceRepository.read(new DeviceAllSpecification());
        for (Device device : devices) {
            Log.i("Model", device.getName());
        }
    }

    private void showDigitsField() {
        Command command = commandMap.get(CommandType.digits);
    }
}
