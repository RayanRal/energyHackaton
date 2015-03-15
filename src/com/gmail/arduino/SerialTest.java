package com.gmail.arduino;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.event.Event;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Enumeration;

/**
 * Created by rayanral on 14/03/15.
 */
public class SerialTest implements SerialPortEventListener {

    SerialPort serialPort;
    ImageView batteryImage;
    Text currentCounterText;
    Text plannedCosts;
    Text leftOnAccountUah;
    BigDecimal initialCostInUah = BigDecimal.valueOf(5.0);
    BigDecimal costPerKilowatt = BigDecimal.valueOf(0.5);
    BigDecimal currentCost = BigDecimal.ZERO;

    /**
     * The port we're normally going to use.
     */
    private static final String PORT_NAMES[] = {
            "/dev/tty.usbserial-A9007UX1", // Mac OS X
            "/dev/ttyACM0", // Raspberry Pi
            "/dev/ttyUSB0", // Linux
            "COM3", // Windows
    };
    /**
     * A BufferedReader which will be fed by a InputStreamReader
     * converting the bytes into characters
     * making the displayed results codepage independent
     */
    private BufferedReader input;
    /**
     * The output stream to the port
     */
    private OutputStream output;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    private static final int DATA_RATE = 9600;

    public SerialTest(ImageView imageView, Text currentCounterText, Text plannedCostsText, Text leftOnAccountUahText) {
        this.batteryImage = imageView;
        this.currentCounterText = currentCounterText;
        this.plannedCosts = plannedCostsText;
        this.leftOnAccountUah = leftOnAccountUahText;
    }

    public void initialize() {
        // the next line is for Raspberry Pi and
        // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            processInput();
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    public void serialEvent() {
        processInput();
    }

    private void processInput() {
        try {
//            String inputLine = input.readLine(); //2.45
            String inputLine = "1.50";
            try {
                Float currentCounter = Float.valueOf(inputLine);
                changeTextLabels(currentCounter);
                System.out.println(currentCounter);
            } catch (NumberFormatException ignored) {
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    private void changeTextLabels(Float currentCounter) {
        currentCost = currentCost.add(costPerKilowatt.multiply(BigDecimal.valueOf(currentCounter)));
        BigDecimal leftOnAccount = initialCostInUah.subtract(currentCost);

        Event.fireEvent(batteryImage, new InputEvent((leftOnAccount.floatValue() * 100) / 5));
        Event.fireEvent(currentCounterText, new InputEvent(currentCounter));
        Event.fireEvent(plannedCosts, new InputEvent(currentCost.floatValue()));
        Event.fireEvent(leftOnAccountUah, new InputEvent(leftOnAccount.floatValue()));
    }


}
