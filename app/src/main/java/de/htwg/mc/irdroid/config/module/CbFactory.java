package de.htwg.mc.irdroid.config.module;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.htwg.mc.irdroid.config.Factory;
import de.htwg.mc.irdroid.database.Repository;
import de.htwg.mc.irdroid.database.implementation.repository.couchbase.CbConnector;
import de.htwg.mc.irdroid.database.implementation.repository.couchbase.CbRepository;
import de.htwg.mc.irdroid.database.implementation.specification.DeviceAllSpecification;
import de.htwg.mc.irdroid.model.Command;
import de.htwg.mc.irdroid.model.CommandType;
import de.htwg.mc.irdroid.model.Device;

/**
 * Provide access to the Repository cb implementations.
 */
public class CbFactory implements Factory {
    //    private final String syncUrl = "http://10.0.2.2:4984/sync_gateway";
    private final String syncUrl = "http://54.154.11.153:4984/sync_gateway";
    private final String databaseName = "irdroid";
    private final CbConnector connector;
    private final Repository<Device> deviceRepository;

    public CbFactory(Context context) {
        connector = new CbConnector(context, databaseName, syncUrl);
        deviceRepository = new CbRepository<>(connector, Device.class);

//        generateDummyCommands();
    }

    @Override
    public boolean login(String email, String password) {
        return connector.login(email, password);
    }

    @Override
    public boolean logout() {
        return connector.logout();
    }

    @Override
    public Repository<Device> provideDevice() {
        return deviceRepository;
    }

    public void generateDummyCommands() {

        String samsungPower = "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f 0015 003f 0015 0015 0015"
                + " 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015"
                + " 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015"
                + " 0015 0015 0040 0015 0015 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 0702"
                + " 00a9 00a8 0015 0015 0015 0e6e";

        String samsungVolUp = "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f" +
                " 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f" +
                " 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f" +
                " 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015" +
                " 0015 0015 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 0702 00a9 00a8" +
                " 0015 0015 0015 0e6e";

        String samsungVolDown = "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f 0015 003f 0015" +
                " 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f 0015" +
                " 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 0015 0015" +
                " 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015" +
                " 0015 0015 003f 0015 003f 0015 003f 0015 003f 0015 0702 00a9 00a8 0015 0015 0015 0e6e";

        String samsungChannelUp = "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f 0015 003f" +
                " 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f" +
                " 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015" +
                " 0015 0015 0015 003f 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015 0015 003f" +
                " 0015 003f 0015 0015 0015 0040 0015 003f 0015 003f 0015 0702 00a9 00a8 0015 0015" +
                " 0015 0e6e";

        String samsungChannelDown = "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f 0015 003f" +
                " 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f" +
                " 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015" +
                " 0015 0015 0015 003f 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f" +
                " 0015 003f 0015 0015 0015 003f 0015 003f 0015 003f 0015 0702 00a9 00a8 0015 0015" +
                " 0015 0e6e";

        String hitachiPowerOn = "0000 006d 0022 0002 0156 00aa 0016 0040 0016 0040 0016 0040 0016" +
                " 0016 0016 0016 0016 0016 0016 0016 0016 0040 0016 0040 0016 0016 0016 0040 0016" +
                " 0016 0016 0016 0016 0016 0016 0040 0016 0016 0016 0040 0016 0016 0016 0040 0016" +
                " 0040 0016 0040 0016 0016 0016 0016 0016 0016 0016 0016 0016 0040 0016 0016 0016" +
                " 0016 0016 0016 0016 0040 0016 0040 0016 0040 0016 0638 0156 0055 0016 0e64";

        String hitachiPoweroff = "0000 006d 0022 0002 0156 00aa 0016 0040 0016 0040 0016 0040 " +
                "0016 0016 0016 0016 0016 0016 0016 0016 0016 0040 0016 0040 0016 0016 0016 0040 " +
                "0016 0016 0016 0016 0016 0016 0016 0040 0016 0016 0016 0016 0016 0040 0016 0040 " +
                "0016 0040 0016 0040 0016 0016 0016 0016 0016 0016 0016 0040 0016 0016 0016 0016 " +
                "0016 0016 0016 0016 0016 0040 0016 0040 0016 0040 0016 0638 0156 0055 0016 0e64";

        Device dummy = deviceRepository.create(new Device("Dummy"));
        deviceRepository.delete(dummy.getId());

        List<Device> devices = deviceRepository.read(new DeviceAllSpecification());
        Device samsung = null;
        if (devices.isEmpty())
            samsung = deviceRepository.create(new Device("Samsung Default"));
        else {
            for (Device device : devices)
                if (device.getName().equals("Samsung Default"))
                    samsung = deviceRepository.update(devices.get(0));
        }
        addCommand(samsung, CommandType.power, samsungPower);
        addCommand(samsung, CommandType.volumeUp, samsungVolUp);
        addCommand(samsung, CommandType.volumeDown, samsungVolDown);
        addCommand(samsung, CommandType.channelUp, samsungChannelUp);
        addCommand(samsung, CommandType.channelDown, samsungChannelDown);
        deviceRepository.update(samsung);

        List<Device> devicesBeamer = deviceRepository.read(new DeviceAllSpecification());
        Device hitachi = null;
        if (devices.isEmpty())
            hitachi = deviceRepository.create(new Device("Hitachi Beamer Default"));
        else {
            for (Device device : devices)
                if (device.getName().equals("Hitachi Beamer Default"))
                    hitachi = deviceRepository.update(devices.get(0));
        }
        addCommand(hitachi, CommandType.power, hitachiPoweroff);
        addCommand(hitachi, CommandType.volumeDown, hitachiPowerOn);
        deviceRepository.update(hitachi);

       /*deviceRepository.create(new Device("Sony TV"));
        deviceRepository.create(new Device("LG TV"));
        deviceRepository.create(new Device("Beamer"));*/

    }

    private void addCommand(Device device, CommandType commandType, String commandString) {
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
        device.addCommand(commandType, command);

    }
}
