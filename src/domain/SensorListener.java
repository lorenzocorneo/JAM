package domain;

/**
 * This interface defines all the methods that SensorListener has to implementates.
 * 
 * @author Lorenzo Corneo
 * @version 1.0
 *
 */
public interface SensorListener {
	/**
	 * This method is executed when the value of the sensor changes.
	 * @param e SensorEvent.
	 */
	public void valueChanged(SensorEvent e);
}
