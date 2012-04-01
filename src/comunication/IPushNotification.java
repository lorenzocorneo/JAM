package comunication;

/**
 * This interface defines all methods for a device that manages PUSH notification.
 * 
 * @author Lorenzo Corneo
 *
 */

public interface IPushNotification {
	/**
	 * Enables push notifications for a sensor.
	 */
	public void enablePushNotification();
	
	/**
	 * Disables push notifications for a sensor.
	 */
	public void disablePushNotification();
	
	/**
	 * Says if push notifications are enabled for current sensor.
	 * @return True if push notifications are enabled, false otherwise.
	 */
	public boolean isPushNotificatoinEnabled();
}
