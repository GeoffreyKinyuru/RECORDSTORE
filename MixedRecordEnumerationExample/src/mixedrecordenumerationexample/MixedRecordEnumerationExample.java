package mixedrecordenumerationexample;

import javax.microedition.rms.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.io.*;

public class MixedRecordEnumerationExample extends MIDlet implements CommandListener {

    private TextField fname, lname, address;

    private ChoiceGroup gender;
    private int currentIndex;

    private Display display;
    private Alert alert;
    private Form form;
    private Command exit;
    private Command start;
    private RecordStore recordstore = null;
    private RecordEnumeration recordEnumeration = null;

    public MixedRecordEnumerationExample() {
        display = Display.getDisplay(this);

        fname = new TextField("FIRSTNAME: ","", 15, 0);
        lname = new TextField("LASTNAME: ", "", 15, 0);
        gender = new ChoiceGroup("GENDER", Choice.EXCLUSIVE);
        gender.append("Female", null);
        currentIndex = gender.append("Male ", null);
        gender.setSelectedIndex(currentIndex, true);
        address = new TextField("ADDRESS", "", 30, 0);

        exit = new Command("Exit", Command.SCREEN, 1);
        start = new Command("Start", Command.SCREEN, 1);
        form = new Form("Personal BioData ");

       form.append(fname);
        form.append(lname);
        form.append(gender);
        form.append(address);

        form.addCommand(exit);
        form.addCommand(start);
        form.setCommandListener(this);
    }

    public void startApp() {
        display.setCurrent(form);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }
public String fullName(String first, String last){
    String name = first +" "+ last ;
    return name;
}
    public void commandAction(Command command,
            Displayable displayable) {

        if (command == exit) {
            destroyApp(true);
            notifyDestroyed();
        } else if (command == start) {
            try {
                recordstore = RecordStore.openRecordStore(
                        "myRecordStore", true);
            } catch (Exception error) {
                alert = new Alert("Error Creating", error.toString(), null, AlertType.WARNING);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
            try {
                String outputData[] = {fullName(fname.getString(), lname.getString()),
                     gender.getString(gender.getSelectedIndex()), address.getString()};
                for (int x = 0; x < outputData.length; x++) {
                    byte[] byteOutputData = outputData[x].getBytes();
                    recordstore.addRecord(byteOutputData,
                            0, byteOutputData.length);
                }
            } catch (Exception error) {
                alert = new Alert("Error Writing", error.toString(), null, AlertType.WARNING);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
            try {
                StringBuffer buffer = new StringBuffer();

                recordEnumeration = recordstore.enumerateRecords(null, null, true);
                while (recordEnumeration.hasPreviousElement()) {
                    buffer.append(new String(recordEnumeration.previousRecord()));
                    buffer.append("\n");
                }
                alert = new Alert("Reading", buffer.toString(), null, AlertType.WARNING);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } catch (Exception error) {
                alert = new Alert("Error Reading", error.toString(), null, AlertType.WARNING);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
            try {
                recordstore.closeRecordStore();
            } catch (Exception error) {
                alert = new Alert("Error Closing", error.toString(), null, AlertType.WARNING);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            }
            if (RecordStore.listRecordStores() != null) {
                try {
                    RecordStore.deleteRecordStore("myRecordStore");
                    recordEnumeration.destroy();
                } catch (Exception error) {
                    alert = new Alert("Error Removing", error.toString(), null, AlertType.WARNING);

                    alert.setTimeout(Alert.FOREVER);
                    display.setCurrent(alert);
                }
            }
        }
    }
}
