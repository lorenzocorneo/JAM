package comunication;

/**
 * This interface defines methods that is executed when an event is verified.
 * @author Lorenzo Corneo
 *
 */
public interface TransportLayerListener {
	
	/**
	 * This method is executed when is verified an event from TransportLayer.
	 * @param e Event from TransportLayer.
	 */
	public void newPushValue(TransportLayerEvent e);
}
