package de.htwg.mc.irdroid.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Map;

import de.htwg.mc.irdroid.R;
import de.htwg.mc.irdroid.model.Command;
import de.htwg.mc.irdroid.model.Device;


public class MainActivity extends Activity implements View.OnClickListener {

    private Button bPower;
    private Button bVolumeUp;
    private Button bVolumeDown;
    private Button bChannelUp;
    private Button bChannelDown;
    private Button bDigits;

    private Device device;
    private Map<Device.CommandType, Command> commandMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            case R.id.bDigits:
                showDigitsField();
                break;
            default:

                break;
        }

    }

    protected void power() {
        Command command = commandMap.get(Device.CommandType.power);
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
