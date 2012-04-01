package comunication;

public class UdpPushEvent {
	
	private byte[] read_bytes;
	
	public UdpPushEvent(byte[] read_bytes) {
		this.read_bytes = read_bytes;
	}
	
	public byte[] readPushBuffer() {
		return this.read_bytes;
	}
}
