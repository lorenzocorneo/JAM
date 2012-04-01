package comunication;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class implements IComunicator interface and defines methods for TCP comunication with Arduino board.
 * 
 * @author Lorenzo Corneo
 *
 */
public class TcpComunicator implements IComunicator {
	
	public static final String ARDUINO_IP_ADDRESS = "192.168.0.13";
	public static final int ARDUINO_TCP_PORT = 9000;
	protected static final int TCP_MAX_RETURN_VALUE = 2;
	
	protected Socket client;
	
	/**
	 * Constructor that initialize target ip and target port.
	 * @param target_ip Arduino target IP.
	 * @param target_port Arduino target port.
	 */
	public TcpComunicator(String target_ip, int target_port) {
		try {
			this.client = new Socket(target_ip, target_port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads a sequence of bytes.
	 * @return Byte's array reads from the net.
	 */
	@Override
	public byte[] read() {
		byte[] buffer = new byte[TCP_MAX_RETURN_VALUE];
		try {
			this.client.getInputStream().read(buffer, 0, TCP_MAX_RETURN_VALUE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * Writes an array of bytes.
	 * @param buffer Byte's array to send on the net.
	 * @return True if it ends well, else false.
	 */
	@Override
	public boolean write(byte[] buffer) {
		try {
			client.getOutputStream().write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

}
