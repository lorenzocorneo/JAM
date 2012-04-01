package comunication;

public interface UdpPushEventSource {
	void addUdpPushListener(UdpPushListener l);
	void removeUdpPushListener(UdpPushListener l);
}
