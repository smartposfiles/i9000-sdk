package rs.fn.data;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Данные о версии и криптографической информации ПО ККТ
 * @author nick
 *
 */
public class VerificationInfo implements Parcelable {

	protected byte [] _hash = {};
	protected byte [] _signature = {};
	protected String _versionStr = "1.0";
	protected int _size;
	public VerificationInfo() {
	}
	/**
	 * Хеш-сумма ПО ККТ
	 * @return
	 */
	public byte [] hash() { return _hash; }
	/**
	 * Открытый ключ подписи, которой подписано ПО ККТ
	 * @return
	 */
	public byte [] signature() { return _signature; }
	/**
	 * Версия ПО ККТ
	 * @return
	 */
	public String version() { return _versionStr; }
	/**
	 * Размер ПО ККТ в байтах
	 * @return
	 */
	public int size() { return _size; }

	@Override
	public int describeContents() {
		return 0;
	}

	public void copyTo(VerificationInfo dest) {
		dest._hash = new byte[_hash.length];
		System.arraycopy(dest._hash, 0, _hash, 0, _hash.length);
		dest._signature = new byte[_signature.length];
		System.arraycopy(dest._signature, 0, _signature, 0, _signature.length);
		dest._versionStr = _versionStr;
		dest._size = _size;
		
	}
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		
		dest.writeInt(_hash.length);
		for(int i=0;i<_hash.length;i++)
			dest.writeByte(_hash[i]);
		dest.writeInt(_signature.length);
		for(int i=0;i<_signature.length;i++)
			dest.writeByte(_signature[i]);
		dest.writeString(_versionStr);
		dest.writeInt(_size);
	}
	
	public void readFromParcel(Parcel in) {
		_hash = new byte[in.readInt()];
		for(int i=0;i<_hash.length;i++) 
			_hash[i] = in.readByte();
		_signature = new byte[in.readInt()];
		for(int i=0;i<_signature.length;i++)
			_signature[i] = in.readByte();
		_versionStr = in.readString();
		_size = in.readInt();
	}
	
	public static final Parcelable.Creator<VerificationInfo> CREATOR = new Parcelable.Creator<VerificationInfo>() {

		@Override
		public VerificationInfo createFromParcel(Parcel p) {
			VerificationInfo result = new VerificationInfo();
			result.readFromParcel(p);
			return result;
		}

		@Override
		public VerificationInfo[] newArray(int size) {
			return new VerificationInfo[size];
		}
		
	};

}

