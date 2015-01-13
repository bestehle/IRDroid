package de.htwg.mc.irdroid.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.Map;

import de.htwg.mc.irdroid.R;
import de.htwg.mc.irdroid.app.IrController;
import de.htwg.mc.irdroid.config.Provider;
import de.htwg.mc.irdroid.database.Repository;
import de.htwg.mc.irdroid.database.implementation.specification.DeviceAllSpecification;
import de.htwg.mc.irdroid.model.Command;
import de.htwg.mc.irdroid.model.Device;


public class MainActivity extends Activity implements View.OnClickListener {

    private IrController ir;

    private Map<Device.CommandType, Command> commandMap;
    private Repository<Device> deviceRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ir = new IrController(this);

        deviceRepository = Provider.getInstance().getFactory().provideDevice();
        Device device = deviceRepository.read(new DeviceAllSpecification()).get(0);

        Button bPower = (Button) findViewById(R.id.bPower);
        bPower.setOnClickListener(this);
        Button bVolumeUp = (Button) findViewById(R.id.bVolUp);
        bVolumeUp.setOnClickListener(this);
        Button bVolumeDown = (Button) findViewById(R.id.bVolDown);
        bVolumeDown.setOnClickListener(this);
        Button bChannelUp = (Button) findViewById(R.id.bChannelUp);
        bChannelUp.setOnClickListener(this);
        Button bChannelDown = (Button) findViewById(R.id.bChannelDown);
        bChannelDown.setOnClickListener(this);
        Button bDigits = (Button) findViewById(R.id.bDigits);
        bDigits.setOnClickListener(this);

        //device = spinner.getDevice();
        commandMap = device.getCommandMap();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bPower:
                power();
                break;
            case R.id.bVolUp:
                volumeUp();
                break;
            case R.id.bVolDown:
                volumeDown();
                break;
            case R.id.bChannelUp:
                channelUp();
                break;
            case R.id.bChannelDown:
                channelDown();
                break;
           /* case R.id.bDigits:
                showDigitsField();
                break;*/
            default:

                break;
        }

    }

    public void sendPower(View view) {
        ir.sendCode(IrController.POWER);
    }

    public void sendVolPlus(View view) {
        ir.sendCode(IrController.VOL_PLUS);
    }

    public void logDevices(View view) {
        Repository<Device> deviceRepository = Provider.getInstance().getFactory().provideDevice();
        List<Device> devices = deviceRepository.read(new DeviceAllSpecification());
        for (Device device : devices) {
            Log.i("Model", device.getName());
        }
    }

    protected void power() {
        Command command = commandMap.get(Device.CommandType.power);
        ir.sendCode(command.getFrequency(), command.getIrCommand());
    }

    private void channelDown() {
        Command command = commandMap.get(Device.CommandType.channelDown);
        
    }

    private void channelUp() {
        Command command = commandMap.get(Device.CommandType.channelUp);
    }

    protected void volumeUp() {
        Command command = commandMap.get(Device.CommandType.volumeUp);
    }

    protected void volumeDown() {
        Command command = commandMap.get(Device.CommandType.volumeDown);
    }

    private void showDigitsField() {
        Command command = commandMap.get(Device.CommandType.digits);
    }
}
