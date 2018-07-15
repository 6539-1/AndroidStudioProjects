package jsip_ua;

import jsip_ua.impl.SipEvent;

public interface SipUAConnectionListener {
    public abstract void onSipUAConnecting(SipEvent event);
    public abstract void onSipUAConnected(SipEvent event);
    public abstract void onSipUADisconnected(SipEvent event);
    public abstract void onSipUACancelled(SipEvent event);
    public abstract void onSipUADeclined(SipEvent event);
}
