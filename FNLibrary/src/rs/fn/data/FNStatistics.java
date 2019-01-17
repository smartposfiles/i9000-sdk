package rs.fn.data;

import java.nio.ByteBuffer;

import android.os.Parcel;
import rs.fn.Utils;
/**
 * Информация о неотправленных документах
 * @author nick
 *
 */
public class FNStatistics {

	protected int _unsentDocumentsCount;
	protected int _unsentDocumentNumber;
	protected long _unsentDocumentDate;
	protected int _exchangeState;
	protected boolean _inRead;

	public FNStatistics() {
	}
	
	public void read(ByteBuffer bb) {
		_exchangeState = bb.get();
		_inRead = bb.get() != 0;
		_unsentDocumentsCount = bb.getShort();
		_unsentDocumentNumber = bb.getInt();
		_unsentDocumentDate = Utils.readDate(bb);
		
	}
	public void writeToParcel(Parcel out) {
		out.writeInt(_exchangeState);
		out.writeInt(_inRead ? 1 : 0);
		out.writeInt(_unsentDocumentsCount);
		out.writeInt(_unsentDocumentNumber);
		out.writeLong(_unsentDocumentDate);
	}
	public void readFromParcel(Parcel in) {
		_exchangeState = in.readInt();
		_inRead = in.readInt() != 0;
		_unsentDocumentsCount = in.readInt();
		_unsentDocumentNumber = in.readInt();
		_unsentDocumentDate = in.readLong();
	}
	/**
	 * Идет обмен сОФД
	 * @return
	 */
	public byte exchangeState() { return (byte)(_exchangeState & 0xFF); }
	/**
	 * Идет чтение документа ОФД
	 * @return
	 */
	public boolean isReading() { return _inRead; }
	/**
	 * Количество неотправленных документов
	 * @return
	 */
	public int unsentDocumentsCount() { return _unsentDocumentsCount;}
	/**
	 * Номер первого неотправленного документа
	 * @return
	 */
	public int firstUnsentNumber() { return _unsentDocumentNumber; }
	/**
	 * Дата первого неотправленного документа
	 * @return
	 */
	public long firstUnsentDate() { return _unsentDocumentDate; }

	public void copyFrom(FNStatistics s) {
		_exchangeState = s._exchangeState;
		_inRead = s._inRead;
		_unsentDocumentNumber = s._unsentDocumentNumber;
		_unsentDocumentsCount = s._unsentDocumentsCount;
		_unsentDocumentDate = s._unsentDocumentDate;
		
	}
	public void reset() {
		_exchangeState = 0;
		_inRead = false;
		_unsentDocumentNumber = _unsentDocumentsCount = 0;
		_unsentDocumentDate = 0;
	}

}
