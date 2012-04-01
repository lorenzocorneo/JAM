package comunication;

/**
 * This interface define all methods for IComunicator.
 * 
 * @author Lorenzo Corneo
 * @version 1.0
 *
 */

public interface IComunicator {
	/**
	 * Reads a sequence of bytes.
	 * @return Byte's array reads from the net.
	 */
	public byte[] read();
	
	/**
	 * Writes an array of bytes.
	 * @param buffer Byte's array to send on the net.
	 * @return True if it ends well, else false.
	 */
	public boolean write(byte[] buffer);
}
