package domain;

/**
 * This interface defines all methods that a device must implements:
 * 1 - public String getName(): returns the name of the device.
 * 2 - public int getPin(): returns the number of the pin.
 * 3 - public boolean is digital(): returns true if the device is digital.
 * 
 * @author Lorenzo Corneo
 * @version 1.0
 * @created 26-Jan-2012 3:12:02 PM
 * 
 */
public interface IDevice {

	/**
	 * Returns the name of the device.
	 * @return Name of device.
	 */
	public String getName();

	/**
	 * Returns pin's number.
	 * @return pin's number.
	 */
	public int getPin();

	/**
	 * Returns a flag that indicates if the device is digital or not.
	 * @return True if the device is digital, else false.
	 */
	public boolean isDigital();
}