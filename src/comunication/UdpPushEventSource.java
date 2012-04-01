package comunication;

/**
 * This interface defines methods for adding/deleting UdpPushListener from listener's list.
 * @author Lorenzo Corneo
 *
 */
public interface UdpPushEventSource {
	/**
	 * Adds UdpPushListener to the list of listeners.
	 * @param l UdpPushListener.
	 */
	void addUdpPushListener(UdpPushListener l);
	
	/**
	 * Removes UdpPushListener to the list of listeners.
	 * @param l UdpPushListener.
	 */
	void removeUdpPushListener(UdpPushListener l);
}
