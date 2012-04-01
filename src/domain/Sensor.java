package domain;

import java.util.ArrayList;
import java.util.List;

import comunication.IPushNotification;
import comunication.TransportLayer;
import comunication.TransportLayerEvent;
import comunication.TransportLayerListener;

/**
 * This class represent the abstraction of a physical sensor that implements various interfaces:
 * 1 - ISensor: defines all methods for a sensor.
 * 2 - TransportLayerListener: defines all methods for intercept event from TransportLayer, like new value read by push comunicator.
 * 3 - IPushNotification: defines all methods for manage PUSH notification.
 * 4 - SensorEventSource: defines all methods for generate event from sensor, like change of current state.
 * 
 * @author Lorenzo Corneo
 * @version 1.0
 * @created 26-Jan-2012 3:12:02 PM
 * 
 */
public class Sensor implements ISensor, TransportLayerListener, IPushNotification, SensorEventSource {

	protected String name;
	protected int pin;
	protected short value;
	protected boolean is_digital;
	protected TransportLayer middleware;
	protected boolean push_on;
	protected List<SensorListener> listeners = new ArrayList<SensorListener>();
	
	/**
	 * Constructor 
	 * @param name Name of device.
	 * @param pin Number of pin.
	 * @param is_digital Flag that indicates if the device is analog.
	 */
	public Sensor(String name, int pin, boolean is_digital){
		this.name = name;
		this.pin = pin;
		this.is_digital = is_digital;
		this.middleware = TransportLayer.getInstance();
	}

	/**
	 * Return the name of device.
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * Returns the pin of device.
	 */
	public int getPin(){
		return this.pin;
	}
	
	/**
	 * Returns true if the device is digital.
	 */
	public boolean isDigital() {
		return this.is_digital;
	}

	/**
	 * Reads the value of device on the Arduino Board.
	 * @return Sensor value.
	 */
	public int getValue() {
		if(this.isPushNotificatoinEnabled()) {
			return this.value;
		} else {
			return this.middleware.read(this);
		}
	}
	
	/**
	 * Register this sensor as Middleware Listeners.
	 */
	public void enablePushNotification() {
		if(!this.push_on) {
			this.middleware.addMiddlewareListener(this);
			this.middleware.pushRegistration(this);
		}
		this.push_on = true;
	}
	
	/**
	 * Unregister this sensor by Middleware Listeners.
	 */
	public void disablePushNotification() {
		if(this.push_on) {
			this.middleware.removeMiddlewareListener(this);
			this.middleware.pushDeregistration(this);
		}
		this.push_on = false;
	}

	/**
	 * Return true if push notification are enabled.
	 */
	public boolean isPushNotificatoinEnabled() {
		return this.push_on;
	}

	/**
	 * When notification from middleware arrives this method sets the value
	 */
	public void newPushValue(TransportLayerEvent e) {
		this.value = e.readPushValue();
		SensorEvent ev = new SensorEvent(this.value);
		for(SensorListener l : this.listeners) {
			l.valueChanged(ev);
		}
	}
	
	/**
	 * Add a source as this sensor listener.
	 */
	public void addSensorListener(SensorListener l) {
		this.listeners.add(l);
	}
	
	/**
	 * Remove a source from sensor listener list.
	 */
	public void removeSensorListener(SensorListener l) {
		this.listeners.remove(l);
	}
}