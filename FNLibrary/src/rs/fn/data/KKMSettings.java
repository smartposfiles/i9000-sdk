package rs.fn.data;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Настройки ККТ
 * @author nick
 *
 */
public class KKMSettings implements Parcelable {

	private static final String SEND_MODE_KEY = "fnT";
	private static final String SESSION_LENGTH_KEY = "sLn";
	private static final String NETWORKT_KEY = "nT";
	private static final String SERVER_KEY = "sA";
	private static final String PORT_KEY = "sP";
	private int _NetworkTimeout = 60;
	private int _OFDSendMode = 1;
	private int _OFDSessionLength = 180;
	private String _OFDServer;
	private int _OFDPort;
	public KKMSettings() {
	}

	/**
	 * Время ожидания ответа от ОФД
	 */
	public int getNetworkTimeout() { return _NetworkTimeout; }
	public void setNetworkTimeout(int value) { _NetworkTimeout = value; }

	public int getOFDSendMode() { return _OFDSendMode; }
	public void setOFDSendMode(int v) { _OFDSendMode = v; }

	public int getOFDSessionLength() { return _OFDSessionLength; }
	public void setOFDSessionLength(int v) { _OFDSessionLength = v; }
	
	/**
	 * Адрес сервера ОФД
	 * @return
	 */
	public String getOFDServer() { return _OFDServer; }
	public void setOFDServer(String value) { _OFDServer = value; }
	
	/**
	 * Порт сервера ОФД
	 * @return
	 */
	public int getOFDPort() { return _OFDPort; }
	public void setOFDPort(int value) { _OFDPort = value; }
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	public void copyTo(KKMSettings dest) {
		dest._NetworkTimeout = _NetworkTimeout;
		dest._OFDServer = _OFDServer;
		dest._OFDPort = _OFDPort;
		dest._OFDSendMode = _OFDSendMode;
		dest._OFDSessionLength = _OFDSessionLength;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_OFDSendMode);
		dest.writeInt(_OFDSessionLength);
		dest.writeInt(_NetworkTimeout);
		dest.writeString(_OFDServer);
		dest.writeInt(_OFDPort);
	}
	public void readFromParcel(Parcel in) {
		_OFDSendMode = in.readInt();
		_OFDSessionLength = in.readInt();
		_NetworkTimeout = in.readInt();
		_OFDServer = in.readString();
		_OFDPort = in.readInt();
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject result = new JSONObject();
		result.put(SEND_MODE_KEY, _OFDSendMode);
		result.put(SESSION_LENGTH_KEY,_OFDSessionLength);
		result.put(NETWORKT_KEY,_NetworkTimeout);
		result.put(SERVER_KEY,_OFDServer);
		result.put(PORT_KEY,_OFDPort);
		return result;
	}
	
	public void readFromJSON(JSONObject json) throws JSONException{
		if(json.has(SEND_MODE_KEY)) _OFDSendMode = json.getInt(SEND_MODE_KEY);
		if(json.has(SESSION_LENGTH_KEY)) _OFDSessionLength = json.getInt(SESSION_LENGTH_KEY);
		if(json.has(NETWORKT_KEY)) _NetworkTimeout = json.getInt(NETWORKT_KEY);
		if(json.has(SERVER_KEY)) _OFDServer = json.getString(SERVER_KEY);
		if(json.has(PORT_KEY)) _OFDPort = json.getInt(PORT_KEY);
	}
				
	
	
	public static final Parcelable.Creator<KKMSettings> CREATOR = new Parcelable.Creator<KKMSettings>() {

		@Override
		public KKMSettings createFromParcel(Parcel in) {
			KKMSettings result = new KKMSettings();
			result.readFromParcel(in);
			return result;
		}

		@Override
		public KKMSettings[] newArray(int size) {
			// TODO Auto-generated method stub
			return new KKMSettings[size];
		}
		
	};

}
