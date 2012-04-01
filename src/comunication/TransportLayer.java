package comunication;

import java.util.ArrayList;
import java.util.List;

import domain.IDevice;

/**
 * This class resolves comunication protocol request for sending/receiving bytes to/from Arduino and manages
 * events for PUSH notifications.
 * 
 * @author Lorenzo Corneo
 *
 */
public class TransportLayer implements UdpPushListener, TransportLayerEventSource{
	
	private final byte OP_WRITE = 0x08;	//00001000
	private final byte OP_DIGITAL = 0x04;
	// port and value mask refers to most significative byte (b[1])
	private final byte PORT_MASK = (byte) 0xF0;
	private final byte VALUE_MASK = 0x03;
	
	private final byte PUSH_ON = 0x01;
	private final byte PUSH_OFF = 0x02;
	
	private List<TransportLayerListener> listeners = new ArrayList<TransportLayerListener>();
	private IComunicator comunicator = null;
	private UdpPushNotificator push_notifier;
	
	// Implementation of Middleware class as Singleton Pattern
	private static TransportLayer middleware = null;
	
	private TransportLayer() {
		
	}
	
	/**
	 * Create only one Middleware and get the instance.
	 * @return Middleware object.
	 */
	public static synchronized TransportLayer getInstance() {
		if(middleware == null) {
			middleware = new TransportLayer();
		}
		return middleware;
	}
	
	/**
	 * Creates comunicator for polling mode.
	 * @param protocol Network protocol.
	 * @param target_ip Arduino IP.
	 * @param target_port Arduino port.
	 * @param source_port Polling socket port.
	 */
	public void createPullComunicator(NetworkProtocol protocol, String target_ip, int target_port, int source_port) {
		if(protocol == NetworkProtocol.UDP && source_port != 0) {
			this.comunicator = new UdpComunicator(target_ip, target_port, source_port);
		}
	}
	
	/**
	 * Creates comunicator for polling mode.
	 * @param protocol Network protocol.
	 * @param target_ip Arduino IP.
	 * @param target_port Arduino port.
	 */
	public void createPullComunicator(NetworkProtocol protocol, String target_ip, int target_port) {
		if(protocol == NetworkProtocol.UDP) {
			this.comunicator = new UdpComunicator(target_ip, target_port);
		}
		
		if(protocol == NetworkProtocol.TCP) {
			this.comunicator = new TcpComunicator(target_ip, target_port);
		}
	}
	
	/**
	 * Creates comunicator for event driven interaction with sensors.
	 * @param protocol Network protocol.
	 * @param port Comunicator port.
	 */
	public void createPushComunicator(NetworkProtocol protocol, int port) {
		if(protocol == NetworkProtocol.UDP && port != 0) {
			this.push_notifier = new UdpPushNotificator(port);
			this.push_notifier.addUdpPushListener(this);
			new Thread(push_notifier).start();
		}
	}
	
	/**
	 * Creates comunicator for event driven interaction with sensors.
	 * @param protocol Network protocol.
	 */
	public void createPushComunicator(NetworkProtocol protocol) {
		this.createPushComunicator(protocol, UdpPushNotificator.DEFAULT_PUSH_PORT);
	}

	/**
	 * Read the value of specific device.
	 * 
	 * @param device Virtual device on Virtual Arduino Board.
	 * @return Value of device.
	 */
	public short read(IDevice device) {
		// Prepare protocol message for Arduino
		byte[] buffer = new byte[] { (byte) 0, (byte)(device.getPin() << 4) };
		if(device.isDigital())
			buffer[1] |= this.OP_DIGITAL;
		
		// Send request to Arduino
		this.comunicator.write(buffer);
		// Wait response from Arduino, elaborate it and return value
		return this.bytesToShort(this.comunicator.read());
	}

	/**
	 * Send request to Arduino.
	 * 
	 * @param device Virtual device on Virtual Arduino Board.
	 * @param value Value to set on Arduino Board.
	 * @return True if the process ends correctly, False if not.
	 */
	public boolean write(IDevice device, int value) {
		/*
		 * Controllare se il messaggio è giusto 
		 */
		// Prepare protocol message for Arduino
		byte[] buffer = new byte[] { 
				(byte) value, (byte)((device.getPin() << 4) | this.OP_WRITE) };
		if(device.isDigital())
			buffer[1] |= this.OP_DIGITAL;
		// Send request to Arduino
		this.comunicator.write(buffer);
		/*
		 * IN QUESTO CASO IL SERVIZIO E' CONFERMATO, MA POTREBBE ANCHE NON ESSERLO!!!
		 * RIFLETTICI BENE!!!!!!!!!!!!!!!!!!!!!!!!!!!
		 */
		return (this.bytesToShort(this.comunicator.read()) == 1)?(true):(false);
	}
	
	private short bytesToShort(byte[] buffer) {
		return (short)(buffer[0] & 0xFF | buffer[1] << 8);
	}

	/**
	 * This method notify to TransportLayerListeners a variation of sensor's state.
	 */
	@Override
	public void newPushNotification(UdpPushEvent e) {
		byte[] buffer = e.readPushBuffer();
		int pin = (buffer[1] & PORT_MASK) >> 4;
		boolean isDigital = ((buffer[1] & OP_DIGITAL) != 0)?(true):(false);
		buffer[1] &= VALUE_MASK;
		short value = this.bytesToShort(buffer);
		// Create event and pass the value
		TransportLayerEvent ev = new TransportLayerEvent(value);
		// The notification will arrive only to specific device
		for(TransportLayerListener l : this.listeners) {
			if(((IDevice)l).getPin() == pin && ((IDevice)l).isDigital() == isDigital) {
				l.newPushValue(ev);
			}
		}
	}

	/**
	 * Adds a TransportLayerListener to listener's list.
	 * @param l TransportLayerListener.
	 */
	@Override
	public void addTranportLayerListener(TransportLayerListener l) {
		this.listeners.add(l);
	}

	/**
	 * Removes a TransportLayerListener to listener's list.
	 * @param l TransportLayerListener.
	 */
	@Override
	public void removeTransportLayerListener(TransportLayerListener l) {
		this.listeners.remove(l);
	}
	
	/**
	 * Sends to Arduino a push registration's request.
	 * @param device Device who wants receive push notifications.
	 */
	public void pushRegistration(IDevice device) {
		this.comunicator.write(new byte[]{this.PUSH_ON, (byte)(device.getPin() << 4)});
	}
	
	/**
	 * Sends to Arduino a push deregistration's request.
	 * @param device Device who wants receive push notifications.
	 */
	public void pushDeregistration(IDevice device) {
		this.comunicator.write(new byte[]{this.PUSH_OFF, (byte)(device.getPin() << 4)});
	}
}
