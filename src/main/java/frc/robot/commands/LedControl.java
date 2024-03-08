package frc.robot.commands.led;
import static edu.wpi.first.util.ErrorMessages.requireNonNullParam;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.I2CJNI;
import edu.wpi.first.hal.util.BoundaryException;
import java.nio.ByteBuffer;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

/**
 * LED Controller bus interface class class.
 *
 * 
 * 
 *
 * 
 * 
 * 
 */

public class LedControl {
    
    private final boolean m_confirm;
    private final int m_deviceAddress;
    private final boolean m_commConfirm;
    private final ByteBuffer m_bufferS;
    private final ByteBuffer m_bufferR;
    private int m_redDumb;
    private int m_greenDumb;
    private int m_blueDumb;
    private byte m_byte1;
    private byte m_byte2;
    private byte m_byte3;
    private byte m_byte4;

    static I2C Wire = new I2C(Port.kMXP, 0x55);
    /**
     * Constructs the LedControl class with the Paramaters: 
     * @param confirm 
     * @param address
     * confirm(bool) tells the class to wait for a byte return of 10101010(0xAA) and will return an error if it doesnt receive within a second.
     * address(int) tells the class where to talk 
     */

    public LedControl(boolean confirm, int address) {
        m_confirm = confirm;
        m_deviceAddress = address;
        m_bufferS = ByteBuffer.allocate(10);
        m_bufferR = ByteBuffer.allocate(10);

        m_commConfirm = Wire.addressOnly();
        if (m_commConfirm == true) {
            System.err.println("Err(LedControl): LEDI2C Device Not Detected.");
        }
    }
    

    /**
     * Sets the LEDs to a solid color
     * @param red (int) 0 - 255 indicating how red the LEDs should be
     * @param green (int) 0 - 255 indicating how green the LEDs should be
     * @param blue (int) 0 - 255 indicating how blue the LEDs should be
     */
    public void setColor(int red, int green, int blue) {
        if (red > 255) {
            m_redDumb = 255;
        } else {
            m_redDumb = red;
        }
        if (green > 255) {
            m_greenDumb = 255;
        } else {
            m_greenDumb = green;
        }
        if (blue > 255) {
            m_blueDumb = 255;
        } else {
            m_blueDumb = blue;
        }
        if (red < 0 || green < 0 || blue < 0) {
            System.err.println("Err(SetColor):How?... Why?... Red, Green, Or Blue should not be negative.");
        }

        m_byte1 = 0x01; //0x01 is the "parse" byte for the Dumb Color setting
        
        m_byte2 = (byte) m_redDumb;
        m_byte3 = (byte) m_greenDumb;
        m_byte4 = (byte) m_blueDumb;

        boolean success = sendData(m_byte1, m_byte2, m_byte3, m_byte4);

        if (success = true) {
            return;
        } else {
            System.err.println("Err(setColor): SendData Failed. Check SendData error message.");
            return;
        }
        

    }

    /**
     * Alt form of SetColor that sets to a particular team color
     * @param team (String) RED or BLU (capitalization doesnt matter) / RED or BLUE if you dont get the reference 
     */
    public void setColor(String team) {
        
        if (team.toLowerCase() == "red") {
            setColor(255, 0, 0);
            return;
        } else if (team.toLowerCase() == "blu" || team.toLowerCase() == "blue"){
            setColor(0, 0, 255);
            return;
        } else {
            System.err.println("Err(SetColorAlt): No team was specified. Check Spelling?");
        }




        m_byte1 = 0x01; //0x01 is the "parse" byte for the Dumb Color setting
        
        m_byte2 = (byte) m_redDumb;
        m_byte3 = (byte) m_greenDumb;
        m_byte4 = (byte) m_blueDumb;

        boolean success = sendData(m_byte1, m_byte2, m_byte3, m_byte4);

        if (success = true) {
            return;
        } else {
            System.err.println("Err(setColor): SendData Failed. Check SendData error message.");
            return;
        }

    }



    /**
     * Tells the controller to start up a demo based on the demoNumber
     * @param demoNumber (int) which demo to display
     * Current Available Demos:
     * (0): Rainbow
     * (1): Firebird **UNDER CONSTRUCTION**
     */
    public void setDemo(int demoNumber) {
        
        m_byte1 = 0x02; //Parse byte indicating "Demo Mode"

        if (demoNumber == 0) {

            m_byte2 = 0x01; //Data byte indicating the demo "Rainbow"
        
        } else if (demoNumber == 1) {

            m_byte2 = 0x02; //Data byte indicating the demo "Firebird"

        } else {
            System.err.println("Err(setDemo): demoNumber out of range. Check the function description to see what demos are available. If thats unavailable AFAIK the currently available demos are 0 and 1");
        }

        boolean success = sendData(m_byte1, m_byte2, m_byte3, m_byte4);

        if (success = true) {
            return;
        } else {
            System.err.println("Err(setDemo): SendData Failed. Check SendData error message.");
            return;
        }

    }

    
    
    /**
     * 
     * @param gameEvent
     */
    public void setGameEvent(int gameEvent) {
        
        m_byte1 = 0x03; //Parse byte indicating "Game Event Mode"

        if (gameEvent == 0) {

            m_byte2 = 0x01; //Data byte indicating the robot has picked up a note
        
        } else if (gameEvent == 1) {

            m_byte2 = 0x02; //Data byte indicating the demo "Firebird"

        } else {
            System.err.println("Err(setDemo): demoNumber out of range. Check the function description to see what demos are available. If thats unavailable AFAIK the currently available demos are 0 and 1");
        }

        boolean success = sendData(m_byte1, m_byte2, m_byte3, m_byte4);

        if (success = true) {
            return;
        } else {
            System.err.println("Err(setDemo): SendData Failed. Check SendData error message.");
            return;
        }

    }


    private boolean sendData(byte byte1, byte byte2, byte byte3, byte byte4){
        
        m_bufferS.put(0, (byte) byte1);
        m_bufferS.put(1, (byte) byte2);
        m_bufferS.put(2, (byte) byte3);
        m_bufferS.put(3, (byte) byte4);
        if (m_confirm == false){
            
            Wire.writeBulk(m_bufferS, 4);
            
            byte1 = 0x00;
            byte2 = 0x00;
            byte3 = 0x00;
            byte4 = 0x00;
            clearBuffers();

            return true;
        
        } else {
            Wire.transaction(m_bufferS, 4, m_bufferR, 1);
            if (m_bufferR.get(0) == 0xAA) {
                return true;
            } else {
                System.err.println("Err(SendData): LEDI2C Response Not Detected. Is the device set to confirm?");
                byte1 = 0x00;
                byte2 = 0x00;
                byte3 = 0x00;
                byte4 = 0x00;
                clearBuffers();
                return false;
            }

        }   
    }

    private void clearBuffers(){
        m_bufferR.put(0, (byte) 0x00);
        m_bufferS.put(0, (byte) 0x00);
        m_bufferS.put(1, (byte) 0x00);
        m_bufferS.put(2, (byte) 0x00);
        m_bufferS.put(3, (byte) 0x00);
        m_byte1 = 0x00;
        m_byte2 = 0x00;
        m_byte3 = 0x00;
        m_byte4 = 0x00;

    }

    }
    
    




    

