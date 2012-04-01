package example;

import system.Middleware;
import domain.Actuator;
import domain.Sensor;
import domain.SensorEvent;
import domain.SensorListener;

/**
 * Threshold example with push notifications.
 * @author Lorenzo Corneo
 *
 */
public class ThresholdPush extends Thread implements SensorListener {
	
	private Actuator led;
	private Sensor potentiometer;
	private int threshold;
	private BoundedBuffer buffer;
	private boolean is_led_on;
	
	public ThresholdPush(Middleware system, int threshold) {
		this.threshold = threshold;
		
		this.led = system.getActuator("l0");
		this.potentiometer = system.getSensor("p0");
		this.potentiometer.enablePushNotification();
		this.potentiometer.addSensorListener(this);
		
		this.is_led_on = false;
		this.buffer = new BoundedBuffer();
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				int value = (Short)this.buffer.get();
				if(value >= this.threshold) {
					if(!this.is_led_on) {
						this.led.setValue(1);
						this.is_led_on = true;
					}
				} else {
					if(this.is_led_on) {
						this.led.setValue(0);
						this.is_led_on = false;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
	}

	@Override
	public void valueChanged(SensorEvent e) {
		try {
			this.buffer.put(e.valueChanged());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
}
