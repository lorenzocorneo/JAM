package comunication;

public interface TransportLayerEventSource {
	public void addMiddlewareListener(TransportLayerListener l);
	public void removeMiddlewareListener(TransportLayerListener l);
}
