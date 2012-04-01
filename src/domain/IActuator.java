package domain;

/**
 * This interface defines all the methods that an actuator must implements:
 * 1 - public boolean setValue(int value): set up a value on the actuator.
 * 
 * @author Lorenzo Corneo
 * @version 1.0
 * @created 26-Jan-2012 3:12:02 PM
 * 
 */
public interface IActuator extends IDevice {

	/**
	 * Sets up a value on the actuator.
	 * @param value Value to sets up.
	 * @return True if the process ends correctly.
	 */
	public boolean setValue(int value);

}