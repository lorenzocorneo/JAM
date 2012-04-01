package comunication;

/**
 * Thiss interface defines methods for add and remove listeners who are interested in the event of the TransportLayer.
 * @author Lorenzo Corneo
 *
 */
public interface TransportLayerEventSource {
	/**
	 * Adds a TransportLayer in the list of listeners.
	 * @param l TransportLayerListener.
	 */
	public void addTranportLayerListener(TransportLayerListener l);
	
	/**
	 * Removes a TransportLayer to the list of listeners.
	 * @param l TransportLayerListener.
	 */
	public void removeTransportLayerListener(TransportLayerListener l);
}
