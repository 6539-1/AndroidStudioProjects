package jsip_ua;

import jsip_ua.impl.SipEvent;

public interface SipUADeviceListener {
    public abstract void onSipUAConnectionArrived(SipEvent event);
    public abstract void onSipUAMessageArrived(SipEvent event);
}