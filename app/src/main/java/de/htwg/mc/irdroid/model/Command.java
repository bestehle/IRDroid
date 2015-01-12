package de.htwg.mc.irdroid.model;

/**
 * Created by armin on 10/01/15.
 */
public class Command {

    private int frequency;
    private int[] irCommand;

    public Command(int frequency, int[] irCommand) {
        this.frequency = frequency;
        this.irCommand = irCommand;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int[] getIrCommand() {
        return irCommand;
    }

    public void setIrCommand(int[] irCommand) {
        this.irCommand = irCommand;
    }
}
