package comunication;

public class TransportLayerEvent {
	private short push_value;
	
	public TransportLayerEvent(short value) {
		this.push_value = value;
	}
	
	public short readPushValue() {
		return this.push_value;
	}
}
