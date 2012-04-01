package domain;

/**
 * This class is part of the Event-Listener pattern for a Sensor, in partucular this class defines all methods for generating events:
 * 1 - public short valueChanged() return the new value of the sensor.
 * 
 * @author Lorenzo Corneo
 * @version 1.0
 *
 */
public class SensorEvent {
	private short value;
	
	/**
	 * Constructor that generates the event
	 * @param value New value of the sensor.
	 */
	public SensorEvent(short value) {
		this.value = value;
	}
	
	/**
	 * Returns the new value of the sensor.
	 * @return New value of the sensor.
	 */
	public short valueChanged() {
		return this.value;
	}
}
