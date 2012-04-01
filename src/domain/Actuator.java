package domain;

import comunication.TransportLayer;

/**
 * This class represents an abstraction of physical actuator and implements IActuator interface that defines all methods for an actuator.
 * 
 * @author Lorenzo Corneo
 * @version 1.0
 * @created 26-Jan-2012 3:12:02 PM
 * 
 */
public class Actuator implements IActuator {

	protected static final int MIN = 0;
	protected static final int MAX = 255;
	
	protected String name;
	protected int pin;
	protected boolean is_digital;
	protected TransportLayer middleware;
	
	/**
	 * Constructor that initialize the name, the pin and a flag that says if the device is digital.
	 * @param name Name of device.
	 * @param pin Pin's number of device.
	 * @param is_digital Flag that says if the actuator is digital.
	 */
	public Actuator(String name, int pin, boolean is_digital){
		this.name = name;
		this.pin = pin;
		this.is_digital = is_digital;
		this.middleware = TransportLayer.getInstance();
	}

	/**
	 * Returns the name of the actuator.
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * Returns the pin's number of the actuator.
	 */
	public int getPin(){
		return this.pin;
	}
	
	/**
	 * Returns true if the actuator is digital.
	 */
	public boolean isDigital() {
		return this.is_digital;
	}

	/**
	 * Sets a value on the actuator.
	 * @param value Value to set up.
	 * @return True if the operation goes well.
	 */
	public boolean setValue(int value) {
		if(value >= MIN && value <= MAX) {
			return this.middleware.write(this, value);
		} else {
			return false;
		}
	}

}