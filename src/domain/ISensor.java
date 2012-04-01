package domain;

/**
 * This interface define all the method that a Sensor must implements:
 * 1 - public int getValue() has to return the value of the current state of the sensor.
 * 
 * @author Lorenzo Corneo
 * @version 1.0
 * @created 26-Jan-2012 3:12:02 PM
 *
 */
public interface ISensor extends IDevice {

	/**
	 * Returns the current state af the sensor.
	 * @return Sensor's value.
	 */
	public int getValue();

}