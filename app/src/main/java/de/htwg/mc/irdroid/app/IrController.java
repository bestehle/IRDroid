package de.htwg.mc.irdroid.app;

import android.app.Activity;
import android.hardware.ConsumerIrManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.CONSUMER_IR_SERVICE;

public class IrController {
    private ConsumerIrManager irService;

    public IrController(Activity context) {
        irService = (ConsumerIrManager) context.getSystemService(CONSUMER_IR_SERVICE);
    }

    /**
     * Sends the pattern with the frequency.
     * @param frequency
     * @param pattern
     */
    public void sendCode(int frequency, int[] pattern) {
        irService.transmit(frequency, pattern);
    }

    /**
     * Sends the Code given as pronto hex.
     * @param code String as Pronto HEX
     */
    public void sendCode(String code) {
        List<String> list = new ArrayList<String>(Arrays.asList(code.split(" ")));
        list.remove(0); // dummy
        int frequency = (int) (1000000 / ((Integer.parseInt(list.remove(0), 16) * 0.241246)));
        list.remove(0); // seq1
        list.remove(0); // seq2

        int[] pattern = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            pattern[i] = Integer.decode("0x" + (list.get(i)));
        }

        irService.transmit(frequency, pattern);
    }

    public void  sendCodeSamsungVersionSwitch(int frequency, int[] pattern) {
        // Version has same format (Major .Minor . MR)
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            int lastIdx = Build.VERSION.RELEASE.lastIndexOf(".");
            int VERSION_MR = Integer.valueOf(Build.VERSION.RELEASE.substring(lastIdx + 1));
            if (VERSION_MR < 3) {
                // Before version of Android 4.4.2
                irService.transmit(frequency, pattern);
            } else {
                // Later version of Android 4.4.3
                irService.transmit(frequency, pattern);
            }
        }
    }

    public static final String POWER = "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f 0015 003f 0015 0015 0015"
            + " 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015"
            + " 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015"
            + " 0015 0015 0040 0015 0015 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 0702"
            + " 00a9 00a8 0015 0015 0015 0e6e";

    public static final String VOL_PLUS = "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f" +
            " 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f" +
            " 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f" +
            " 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015" +
            " 0015 0015 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 0702 00a9 00a8" +
            " 0015 0015 0015 0e6e";
}
