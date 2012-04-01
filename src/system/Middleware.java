package system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import comunication.TransportLayer;
import comunication.NetworkProtocol;
import comunication.TcpComunicator;
import comunication.UdpComunicator;
import domain.Actuator;
import domain.Sensor;



public class Middleware {
	
	public final String DEFAULT_CONFIGURATION_FILE_NAME = "conf.ini";
	public final String DEFAULT_VIRTUAL_BOARD_SOURCE_PATH = "src/system/VirtualBoard.java";
	public final String DEFAULT_VIRTUAL_BOARD_DESTINATION_PATH = "bin";
	
	private String configuration_file_content = "";
	private TransportLayer middleware = TransportLayer.getInstance();
	
	private List<Sensor> sensors_list = new ArrayList<Sensor>();
	private List<Actuator> actuators_list = new ArrayList<Actuator>();
	
	public void configureNetworkProtocol(NetworkProtocol protocol, String target_ip, int target_port, int source_pull_port, int source_push_port) {
		this.middleware.createPullComunicator(protocol, target_ip, target_port, source_pull_port);
		this.middleware.createPushComunicator(protocol, source_push_port);
	}
	
	public void configureNetworkProtocol(NetworkProtocol protocol, String target_ip, int target_port) {
		this.middleware.createPullComunicator(protocol, target_ip, target_port);
	}
	
	public void configureNetworkProtocol(NetworkProtocol protocol) {
		if(protocol == NetworkProtocol.UDP) {
			this.middleware.createPullComunicator(protocol, UdpComunicator.ARDUINO_IP_ADDRESS, UdpComunicator.ARDUINO_UDP_PORT);
			this.middleware.createPushComunicator(protocol);
		}
		if(protocol == NetworkProtocol.TCP) {
			this.middleware.createPullComunicator(protocol, TcpComunicator.ARDUINO_IP_ADDRESS, TcpComunicator.ARDUINO_TCP_PORT);
		}
	}
	
	/**
	 * Add device in the configuration file.
	 * 
	 * @param type Class name.
	 * @param name Name of device, for convention write it in lower-case.
	 * @param pin Number of pin on Arduino board.
	 * @param is_digital True if the device is digital, False if not.
	 */
	public void addDevice(String type, String name, int pin, boolean is_digital){
		// "#" Separation character
		if(type.toLowerCase().equals("sensor"))
			configuration_file_content += "sensor" + "#";
		else if(type.toLowerCase().equals("actuator"))
			configuration_file_content += "actuator" + "#";
		else 
			configuration_file_content += "unknown" + "#";
		configuration_file_content += name + "#";
		configuration_file_content += pin + "#";
		configuration_file_content += is_digital + "\n";
	}
	
	/**
	 * This method generates configuration file
	 * 
	 * @param filename Name of configuration file
	 * @return True if the process end correctly, False if not.
	 */
	public boolean generateConfigurationFile(String filename){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			out.write(this.configuration_file_content);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * This method generates configuration file with default name DEFAULT_CONFIGURATION_FILE_NAME
	 * 
	 * @return True if the process end correctly, False if not.
	 */
	public boolean generateConfigurationFile() {
		return this.generateConfigurationFile(DEFAULT_CONFIGURATION_FILE_NAME);
	}
	
	/**
	 * This method populates list of sensors and actuators
	 * 
	 * @param config_file_name Name of configuration file
	 * @return True if the process end correctly, False if not.
	 */
	public boolean configureMiddleware(String config_file_name) {
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(config_file_name));
			String line = "";

			while((line = in.readLine()) != null) {
				String[] split = line.split("#");
				if(split[0].toLowerCase().equals("sensor")) {
					this.sensors_list.add(new Sensor(split[1], Integer.parseInt(split[2]), 
							(split[3].toLowerCase().equals("true")?(true):(false))));
				} else if(split[0].toLowerCase().equals("actuator")) {
					this.actuators_list.add(new Actuator(split[1], Integer.parseInt(split[2]),
							(split[3].toLowerCase().equals("true")?(true):(false))));
				} else {
					return false;
				}
			}
			
		} catch(FileNotFoundException e) {
			e.getStackTrace();
			return false;
		} catch(IOException e) {
			e.getStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean configureMiddleware() {
		return this.configureMiddleware(DEFAULT_CONFIGURATION_FILE_NAME);
	}
	

	public Sensor getSensor(String device_name) {
		for(Sensor s : this.sensors_list) {
			if(s.getName().equals(device_name)) {
				return s;
			}
		}
		return null;
	}
	
	public Actuator getActuator(String device_name) {
		for(Actuator a : this.actuators_list) {
			if(a.getName().equals(device_name)) {
				return a;
			}
		}
		return null;
	}
	
}
