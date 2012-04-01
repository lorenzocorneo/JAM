package comunication;

/**
 * This class implements all methods for events generated by Push Comunicator UDP.
 * @author Lorenzo Corneo
 *
 */
public class UdpPushEvent {
	
	private byte[] read_bytes;
	
	/**
	 * Constructor that generates the event.
	 * @param read_bytes Bytes read.
	 */
	public UdpPushEvent(byte[] read_bytes) {
		this.read_bytes = read_bytes;
	}
	
	/**
	 * This method returns the buffer read inside the notification.
	 * @return Read bytes.
	 */
	public byte[] readPushBuffer() {
		return this.read_bytes;
	}
}
