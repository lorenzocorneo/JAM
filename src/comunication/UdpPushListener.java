package comunication;

/**
 * This interface defines methods for manage an event generated by UdpPushEventSource.
 * @author Lorenzo Corneo
 *
 */
public interface UdpPushListener {
	/**
	 * This method is executed when an UdpPushEvent in verified.
	 * @param e UdpPushEvent.
	 */
	void newPushNotification(UdpPushEvent e);
}