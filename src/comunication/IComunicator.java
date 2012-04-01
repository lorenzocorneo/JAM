package comunication;

/**
 * 
 * @author Lorenzo Corneo
 * @version 1.0
 *
 */

public interface IComunicator {
	public byte[] read();
	public boolean write(byte[] buffer);
}
