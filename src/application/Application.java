package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import comunication.NetworkProtocol;

import example.ThresholdPull;
import example.ThresholdPush;
import system.Middleware;

public class Application {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		system.Middleware system = new Middleware();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Welcome in Arduino Java Middleware.");
			System.out.println("Do you want to configure the VirtualBoard ? [y/n]");
			
			String line = "";
			
			if(in.readLine().toLowerCase().equals("y")) {
				System.out.println("Add device with syntax: (actuator|sensor) NAME PIN IS-DIGITAL(True|False), type \"stop\" for ending configuration.");
				while(!(line = in.readLine()).toLowerCase().equals("stop")) {
					String[] split = line.split(" ");
					if(split[0].toLowerCase().equals("sensor"))
						system.addDevice("sensor", split[1], Integer.parseInt(split[2]), (split[3].equals("true")?(true):(false)));
					else if(split[0].toLowerCase().equals("actuator"))
						system.addDevice("actuator", split[1], Integer.parseInt(split[2]), (split[3].equals("true")?(true):(false)));
					else {
						System.out.println("Error: bad type.");
					}
				}
				
				system.generateConfigurationFile();
				system.configureMiddleware();
			}
			
			system.configureMiddleware();
			
			System.out.println("Choose one example:");
			System.out.println("1 - UDP Threshold PULL.");
			System.out.println("2 - UDP Threshold PUSH.");
			System.out.println("3 - TCP Threshold PULL.");
			
			line = in.readLine();
			if(line.equals("1") || line.equals("2")) {
				system.configureNetworkProtocol(NetworkProtocol.UDP);
				System.out.print("Enter threshold: ");
				if(line.equals("1")) {
					ThresholdPull example = new ThresholdPull(system, Integer.parseInt(in.readLine()));
					example.start();
				} else if(line.equals("2")) {
					ThresholdPush example = new ThresholdPush(system, Integer.parseInt(in.readLine()));
					example.start();
				}
			} else if(line.equals("3")) {
				system.configureNetworkProtocol(NetworkProtocol.TCP);
				System.out.print("Enter threshold: ");
				ThresholdPush example = new ThresholdPush(system, Integer.parseInt(in.readLine()));
				example.start();
			}
		
			System.out.println("Example started, type \"quit\" for exit.");
			
			while(!line.equals("quit")) {
				line = in.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		System.exit(0);
	}
}
