package rs.fn.data;


import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import rs.fn.Const;


/**
 * 	Организация/пользователь
 * @author nick
 *
 */
public class OU implements Parcelable {
	private static final String NAME_KEY = "name";
	private static final String INN_KEY = "inn";
	private String _name = Const.EMPTY_STRING;
	private String _inn = Const.EMPTY_INN;
	public OU() { }
	public OU(Parcel in) {
		readFromParcel(in);
	}
	public void readFromJSON(JSONObject json) throws JSONException {
		if(json.has(NAME_KEY)) setName(json.getString(NAME_KEY));
		if(json.has(INN_KEY)) setINN(json.getString(INN_KEY));
	}
	public JSONObject toJSON()  throws JSONException {
		JSONObject result = new JSONObject();
		result.put(NAME_KEY, _name);
		result.put(INN_KEY, _inn);
		return result;
	}
	public void readFromParcel(Parcel in) {
		_name = in.readString();
		_inn = in.readString();
	}
	/**
	 * Рекевизиты заполнены?
	 * @return
	 */
	public boolean isSet() { 
		return !Const.EMPTY_INN.equals(_inn) || (_name != null  && !_name.isEmpty()); 
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(_name);
		dest.writeString(_inn);
		
	}
	/**
	 * Наименование
	 * @return
	 */
	public String getName() { return _name; }
	public boolean setName(CharSequence value) {
		return setName(value.toString());
	}
	/**
	 * 
	 * @param value
	 * @return
	 */
	public boolean setName(String value) {
		if(value == null || value.isEmpty() || value.length() > 255) return false;
		_name = value;
		return true;
	}
	/**
	 * ИНН 
	 * @param padded true если дополнять пробелами слева до 12 символов 
	 * @return
	 */
	public String getINN(boolean padded) {
		String s = _inn;
		if(padded)
			while(s.length() < 12) s += " ";
		return s; 
	}
	/**
	 * Получить ИНН без дополнения пробелами
	 * @return
	 */
	public String getINN() {
		if(Const.EMPTY_INN.equals(_inn)) return Const.EMPTY_STRING;
		return getINN(false);
	}
	
	public boolean setINN(CharSequence e) {
		return setINN(e.toString());
	}
	/**
	 * Установить значение ИНН
	 * @param value
	 * @return true если значение прошло проверку контрольной суммы
	 */
	public boolean setINN(String value) {
		if(value == null) return false;
		if(Const.EMPTY_INN.equals(value)) {
			_inn = value;
			return true;
		}
		while(value.startsWith("0")) value = value.substring(1);
		if(!checkINN(value)) return false;
		_inn = value.trim();
		return true;
	}
	
	public static boolean checkINN(String inn) {
		if(inn == null || inn.length() != 10 && inn.length() != 12) return false;
		inn = inn.trim();
		int [] d = new int[inn.length()];
		try {
		for(int i=0;i<inn.length();i++) 
			d[i] = Integer.parseInt(""+inn.charAt(i));
		} catch(NumberFormatException nfe) {
			return false;
		}
		if(d.length == 10) {
			int sum = d[0]*2 + d[1]*4 + d[2]*10+d[3]*3 +d[4]*5 + d[5]*9 + d[6]*4 +d[7]*6 + d[8]*8;
			sum = (sum % 11) % 10;
			return sum == d[9];
		} else {
			int sum = d[0]*7 + d[1]*2 + d[2]*4 + d[3]*10 + d[4]*3 + d[5]*5 + d[6]*9 +d[7]*4 + d[8]*6+ d[9]*8;
			sum = (sum % 11) % 10;
			if(sum != d[10]) return false;
			sum = d[0]*3 + d[1]*7 + d[2]*2 + d[3]*4 + d[4]*10 + d[5]*3 + d[6]*5 +d[7]*9 + d[8]*4+ d[9]*6+d[10]*8;
			sum = (sum % 11) % 10;
			return sum == d[11];
		}
		
	}
	
	public void cloneTo(OU dest) {
		dest._name = _name;
		dest._inn = _inn;
	}
	
	public static final Parcelable.Creator<OU> CREATOR = new Parcelable.Creator<OU>() {
		@Override
		public OU createFromParcel(Parcel source) {
			return new OU(source);
		}

		@Override
		public OU[] newArray(int size) {
			return new OU[size];
		}
		
	};
	
}
