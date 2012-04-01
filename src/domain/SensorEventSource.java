package domain;

/**
 * This class defines all the methods useful to represents a Sensor event source.
 * 
 * @author Lorenzo Corneo
 * @version 1.0
 *
 */
public interface SensorEventSource {
	/**
	 * Add a SensorListener to the list of listeners.
	 * @param l SensorListener interested at the event.
	 */
	public void addSensorListener(SensorListener l);
	
	/**
	 * Remove a SensorListener to the list of listeners.
	 * @param l SensorListener to remove.
	 */
	public void removeSensorListener(SensorListener l);
}
