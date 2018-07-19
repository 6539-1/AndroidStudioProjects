package jsip_ua.impl;

import android.content.Context;

import jsip_ua.IDevice;
import jsip_ua.NotInitializedException;
import jsip_ua.SipProfile;
import jsip_ua.SipUAConnectionListener;
import jsip_ua.SipUADeviceListener;
import jsip_ua.impl.SipEvent.SipEventType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

// ISSUE#17: commented those, as we need to decouple the UI details
//import org.mobicents.restcomm.android.sdk.ui.IncomingCall;
//import org.mobicents.restcomm.android.sdk.ui.NotifyMessage;

public class DeviceImpl implements IDevice,Serializable {

	private static DeviceImpl device;
	//private String ServiceIp = "sip:alice@192.168.43.73:5006";
	private String ServiceIp = "sip:alice@10.206.17.106:5006";
	Context context;
	SipManager 	 sipManager;
	SipProfile 	 sipProfile;
	SoundManager soundManager;
	String reciveMessage;
	Handler mHandler;
	boolean isInitialized;
	public SipUADeviceListener sipuaDeviceListener = new SipUADeviceListener() {
		@Override
		public void onSipUAConnectionArrived(SipEvent event) {

		}

		@Override
		public void onSipUAMessageArrived(SipEvent event) {
			String msg = event.content;
			System.out.println("m"+msg);
			setReciveMessage(msg);
			Message msg_rec = mHandler.obtainMessage();
			msg_rec.what = 1;
			msg_rec.obj = msg;
			mHandler.sendMessage(msg_rec);
		}
	};



	public SipUAConnectionListener sipuaConnectionListener = null;

	private DeviceImpl(){
		
	}
	public static DeviceImpl getInstance(){
		if(device == null){
			device = new DeviceImpl();
		}
		return device;
	}
    public void Initialize(Context context, SipProfile sipProfile, HashMap<String,String> customHeaders){
        this.Initialize(context,sipProfile);
        sipManager.setCustomHeaders(customHeaders);
    }
	public void Initialize(Context context, SipProfile sipProfile){
		this.context = context;
		this.sipProfile = sipProfile;
		sipManager = new SipManager(sipProfile);
		soundManager = new SoundManager(context,sipProfile.getLocalIp());
		sipManager.addSipEventListener(this);
	}
	public void setReciveMessage(String msg){
		this.reciveMessage = msg;
	}
	public String getReciveMessage(){
		return reciveMessage;
	}
	@Override
	public void onSipMessage(final SipEvent sipEventObject) {
		System.out.println("Sip Event fired");
		if (sipEventObject.type == SipEventType.MESSAGE) {
			if (this.sipuaDeviceListener != null) {
				this.sipuaDeviceListener.onSipUAMessageArrived(new SipEvent(this, SipEvent.SipEventType.MESSAGE, sipEventObject.content, sipEventObject.from));
			}
		} else if (sipEventObject.type == SipEventType.BYE) {
			this.soundManager.releaseAudioResources();
			if (this.sipuaConnectionListener != null) {
				// notify our listener that we are connected
				this.sipuaConnectionListener.onSipUADisconnected(null);
			}
		} else if (sipEventObject.type == SipEventType.REMOTE_CANCEL) {
			//this.soundManager.releaseAudioResources();
			if (this.sipuaConnectionListener != null) {
				// notify our listener that we are connected
				this.sipuaConnectionListener.onSipUACancelled(null);
			}
		} else if (sipEventObject.type == SipEventType.DECLINED) {
			//this.soundManager.releaseAudioResources();
			if (this.sipuaConnectionListener != null) {
				// notify our listener that we are connected
				this.sipuaConnectionListener.onSipUADeclined(null);
			}
		}else if (sipEventObject.type == SipEventType.BUSY_HERE) {
			this.soundManager.releaseAudioResources();
		} else if (sipEventObject.type == SipEventType.SERVICE_UNAVAILABLE) {
			this.soundManager.releaseAudioResources();

		} else if (sipEventObject.type == SipEventType.CALL_CONNECTED) {
			this.soundManager.setupAudio(sipEventObject.remoteRtpPort, this.sipProfile.getRemoteIp());
			if (this.sipuaConnectionListener != null) {
				// notify our listener that we are connected
				this.sipuaConnectionListener.onSipUAConnected(null);
			}
		} else if (sipEventObject.type == SipEventType.REMOTE_RINGING) {
			if (this.sipuaConnectionListener != null) {
				// notify our listener that we are connecting
				this.sipuaConnectionListener.onSipUAConnecting(null);
			}
		} else if (sipEventObject.type == SipEventType.LOCAL_RINGING) {
			if (this.sipuaDeviceListener != null) {
				this.sipuaDeviceListener.onSipUAConnectionArrived(null);
			}
		}
	}

	@Override
	public void Call(String to) {
		try {
			this.sipManager.Call(to,this.soundManager.setupAudioStream(sipProfile.getLocalIp()));
		} catch (NotInitializedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void Accept() {
		sipManager.AcceptCall(soundManager.setupAudioStream(sipProfile.getLocalIp()));
	}

	@Override
	public void Reject() {
		sipManager.RejectCall();
	}

	@Override
	public void Cancel() {
		try {
			sipManager.Cancel();
		} catch (NotInitializedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void Hangup() {
		if (this.sipManager.direction == this.sipManager.direction.OUTGOING ||
				this.sipManager.direction == this.sipManager.direction.INCOMING) {
			try {
				this.sipManager.Hangup();
			} catch (NotInitializedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void SendMessage(String to, String message) {
		try {

			this.sipManager.SendMessage(ServiceIp, message);
			//Log.i("sbbbbbbbbbbbb", message);
		} catch (NotInitializedException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void SendDTMF(String digit) {
		try {
			this.sipManager.SendDTMF(digit);
		} catch (NotInitializedException e) {
			e.printStackTrace();
		}

	}
	@Override
	public void Register() {
		this.sipManager.Register();
	}

	@Override
	public SipManager GetSipManager() {
		// TODO Auto-generated method stub
		return sipManager;
	}

	@Override
	public void Mute(boolean muted)
	{
		soundManager.muteAudio(muted);
	}

	@Override
	public SoundManager getSoundManager() {
		// TODO Auto-generated method stub
		return soundManager;
	}
	public static byte[] serialize(Object o) {
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    try {   
	        ObjectOutput out = new ObjectOutputStream(bos);
	        out.writeObject(o);                                       //This is where the Exception occurs
	        out.close();     
	        // Get the bytes of the serialized object    
	        byte[] buf = bos.toByteArray();   
	        return buf;    
	    } catch(IOException ioe) {
	        //Log.e("serializeObject", "error", ioe);           //"ioe" says java.io.NotSerializableException exception
	        return null; 
	    }  

	}


	public static Object deserialize(byte[] b) {
	        try {    
	            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
	            Object object = in.readObject();
	            in.close();  
	            return object;  
	        } catch(ClassNotFoundException cnfe) {
	            //Log.e("deserializeObject", "class not found error", cnfe);   
	            return null;  
	        } catch(IOException ioe) {
	            //Log.e("deserializeObject", "io error", ioe);    
	            return null; 
	        } 
	    }

	    public void setHandler(Handler handler){
			this.mHandler = handler;
		}
	
	

}
