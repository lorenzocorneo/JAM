package comunication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

public class UdpPushNotificator extends UdpComunicator implements Runnable, UdpPushEventSource {
	
	protected static final int DEFAULT_PUSH_PORT = 8889;
	
	private List<UdpPushListener> listeners;

	public UdpPushNotificator() {
		super(UdpComunicator.ARDUINO_IP_ADDRESS, UdpComunicator.ARDUINO_UDP_PORT, DEFAULT_PUSH_PORT);
		this.listeners = new ArrayList<UdpPushListener>();
	}
	
	public UdpPushNotificator(int port) {
		super(UdpComunicator.ARDUINO_IP_ADDRESS, UdpComunicator.ARDUINO_UDP_PORT, port);
		this.listeners = new ArrayList<UdpPushListener>();
	}

	@Override
	public byte[] read() {
		byte[] buffer = new byte[UdpComunicator.UDP_MAX_RETURN_VALUE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return packet.getData();
	}

	@Override
	public void run() {
		while(true) {
			// Send notification to all listeners
			byte[] buffer = this.read();
			UdpPushEvent ev = new UdpPushEvent(buffer);
			for(UdpPushListener l : this.listeners) {
				l.newPushNotification(ev);
			}
		}
	}

	@Override
	public void addUdpPushListener(UdpPushListener l) {
		this.listeners.add(l);
	}

	@Override
	public void removeUdpPushListener(UdpPushListener l) {
		this.listeners.remove(l);
	}
}
