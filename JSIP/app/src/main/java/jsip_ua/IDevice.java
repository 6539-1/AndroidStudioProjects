package jsip_ua;

import jsip_ua.impl.SipManager;
import jsip_ua.impl.SoundManager;

public interface IDevice  extends ISipEventListener{

    void Register();

    void Call(String to);

    void Accept();

    void Reject();

    void Cancel();

    void Hangup();

    void SendMessage(String to, String message);

    void SendDTMF(String digit);

    void Mute(boolean muted);

    SipManager GetSipManager();
    SoundManager getSoundManager();

}