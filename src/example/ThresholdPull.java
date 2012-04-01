package example;

//import java.util.Calendar;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

import domain.Actuator;
import domain.Sensor;

import system.Middleware;

public class ThresholdPull extends Thread{
	
	private Actuator led;
	private Sensor potentiometer;
	private int threshold;
	private boolean stop;
	private boolean led_is_on;
	
	private JFrame frame;
	private JLabel led_lbl;
	private JLabel pot_lbl;
	
	public ThresholdPull(Middleware system, int threshold) {
		this.threshold = threshold;
		
		this.led = system.getActuator("l0");
		this.potentiometer = system.getSensor("p0");
		
		this.led_is_on = false;
		this.stop = false;
		
		this.frame = new JFrame("Threshold Pull GUI");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setResizable(false);
		this.frame.setSize(new Dimension(200, 80));
		this.frame.setLayout(new FlowLayout());
		
		this.led_lbl = new JLabel("LED: OFF");
		this.pot_lbl = new JLabel("POTENTIOMETER: ");
		
		this.frame.add(this.led_lbl);
		this.frame.add(this.pot_lbl);
		
		//this.frame.pack();
		
		this.frame.setVisible(true);
	}

	@Override
	public void run() {
		while(!this.stop) {
			//long start = Calendar.getInstance().getTimeInMillis();
			int value = potentiometer.getValue();
			//long stop = Calendar.getInstance().getTimeInMillis();
			this.pot_lbl.setText("POTENTIOMETER: " + value);
			//System.out.println(stop - start);
			
			if(value > this.threshold) {
				if(!this.led_is_on) {
					this.led.setValue(1);
					this.led_is_on = true;
					this.led_lbl.setText("LED: ON");
				}
			} else {
				if(this.led_is_on) {
					this.led.setValue(0);
					this.led_is_on = false;
					this.led_lbl.setText("LED: OFF");
				}
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	
	public void stopExample() {
		this.stop = true;
	}
}
