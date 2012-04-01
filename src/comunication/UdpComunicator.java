package comunication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpComunicator implements IComunicator{
	
	public static final String ARDUINO_IP_ADDRESS = "192.168.0.13";
	public static final int ARDUINO_UDP_PORT = 8888;
	protected static final int UDP_MAX_RETURN_VALUE = 2;
	
	protected String target_address;
	protected int target_port;
	protected int source_port;
	protected DatagramSocket socket;
	
	/*
	 * Inseriamo un metodo che ci chiede se il servizio deve essere confermato ??
	 * tipo:
	 * public void setReliablity(boolean value);
	 */
	
	/**
	 * Specify this port's socket and target's parameters.
	 * 
	 * @param target_addres Target IP address.
	 * @param target_port Target port.
	 * @param source_port Port of this DatagramSocket.
	 */
	public UdpComunicator(String target_address, int target_port, int source_port) {
		this.target_address = target_address;
		this.target_port = target_port;
		this.source_port = source_port;
		try {
			this.socket = new DatagramSocket(source_port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Specify target's parameters and create a DatagramSocket with ephimeral port.
	 * 
	 * @param target_addres Target IP address.
	 * @param target_port Target port.
	 */
	public UdpComunicator(String target_address, int target_port) {
		this.target_address = target_address;
		this.target_port = target_port;
		this.source_port = 0;
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send data bytes to target_address on port target_port.
	 * 
	 * @param buffer Buffer of bytes.
	 * @return True if the socket receives value 1, False if it receives other number.
	 */
	public boolean write(byte[] buffer) {
		DatagramPacket packet = null;
		try {
			packet = new DatagramPacket(buffer, buffer.length, 
					InetAddress.getByName(this.target_address), this.target_port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		/*
		 * Non aspettiamo una risposta ? Lo facciamo confermato o no ? 
		 * SI, la dobbiamo aspettare. Potremmo farlo scegliere all'utente.
		 * E' da implementare!!!!!!!!!!!!!
		 */
		return true;
	}
	
	/**
	 * Socket listen for packet.
	 * 
	 * @return Data received, null if somethings goes wrong.
	 */
	public byte[] read(){
		byte[] buffer = new byte[UDP_MAX_RETURN_VALUE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return packet.getData();
	}
}
