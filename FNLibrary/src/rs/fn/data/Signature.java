package rs.fn.data;

import java.nio.ByteBuffer;

import android.os.Parcel;
import rs.fn.Const;

/**
 * Фискальная подпись документа
 * @author nick
 *
 */
public class Signature {

	protected long DATE;
	protected int DOC_NO;
	protected long DOC_SIGNATURE;
	protected int OFD_ANSWER = -1;
	protected byte[] OFD_REPLY = {};
	protected String OWNER_NAME = Const.EMPTY_STRING;
	protected String OWNER_INN = Const.EMPTY_INN;
	protected String FN_SERIAL = Const.EMPTY_STRING;
	protected String DEVICE_SERIAL = Const.EMPTY_STRING;
	protected String KKT_NUMBER = Const.EMPTY_STRING;
	protected int FN_WARINGS;
	protected int FN_PROTOCOL;
	protected int WD_NUMBER;
	
	public Signature() {
	}
	
	public void updateOFDAnswer(int answer, byte [] signature) {
		OFD_ANSWER = answer;
		OFD_REPLY = signature;
	}
	public void init(KKMInfo info) {
		FN_WARINGS = info.getFNWarnings();
		FN_PROTOCOL = info.getProtocolVersion();
		OWNER_NAME = info.owner().getName();
		OWNER_INN = info.owner().getINN();
		FN_SERIAL = info.FNSerial();
		DEVICE_SERIAL = info.DeviceSerial();
		KKT_NUMBER = info.getRegistrationNo();
		WD_NUMBER = info.workDayNumber();
	}
	
	public void readFromParcel(Parcel in) {
		DATE = in.readLong();
		DOC_NO = in.readInt();
		DOC_SIGNATURE = (in.readLong() & 0xffffffffL);
		OFD_ANSWER = in.readInt();
		OWNER_INN = in.readString();
		OWNER_NAME = in.readString();
		FN_WARINGS = in.readInt();
		FN_PROTOCOL = in.readInt();
		FN_SERIAL = in.readString();
		DEVICE_SERIAL = in.readString();
		KKT_NUMBER = in.readString();
		WD_NUMBER = in.readInt();
		
		
		OFD_REPLY = new byte[in.readInt()];
		for(int i=0;i<OFD_REPLY.length;i++)
			OFD_REPLY[i] = in.readByte();
		
	}
	public void writeToParcel(Parcel p) {
		p.writeLong(DATE);
		p.writeInt(DOC_NO);
		p.writeLong(DOC_SIGNATURE);
		p.writeInt(OFD_ANSWER);
		p.writeString(OWNER_INN);
		p.writeString(OWNER_NAME);
		p.writeInt(FN_WARINGS);
		p.writeInt(FN_PROTOCOL);
		p.writeString(FN_SERIAL);
		p.writeString(DEVICE_SERIAL);
		p.writeString(KKT_NUMBER);
		p.writeInt(WD_NUMBER);
		
		p.writeInt(OFD_REPLY.length);
		for(int i=0;i<OFD_REPLY.length;i++)
			p.writeByte(OFD_REPLY[i]);
	}
	
	public void sign(long when, ByteBuffer bb) {
		DATE = when;
		DOC_NO = bb.getInt();
		DOC_SIGNATURE = (bb.getInt() & 0xffffffffL);
	}
	/**
	 * Документ подписан?
	 * @return
	 */
	public boolean hasOFDReply() {
		return OFD_ANSWER != -1; 
	}
	/**
	 * Номер смены
	 * @return
	 */
	public int workDayNumber() { return WD_NUMBER; }
	/**
	 * Фискальный номер документа
	 * @return
	 */
	public int number() { return DOC_NO; }
	/**
	 * Фискальная подпись документа
	 * @return
	 */
	public long signature() { return DOC_SIGNATURE; }
	/**
	 * Дата документа
	 * @return
	 */
	public long date() { return DATE; }
	/**
	 * код ответа оператора ОФД
	 * @return
	 */
	public int OFDAnswer() { return OFD_ANSWER; }
	/**
	 * Квитанция оператора ОФД
	 * @return
	 */
	public byte [] OFDReply() { return OFD_REPLY; }
	/**
	 * Наименование владельца ККТ
	 * @return
	 */
	public String ownerName() { return OWNER_NAME; }
	/**
	 * ИНН владельца ККТ
	 * @return
	 */
	public String ownerINN() { return OWNER_INN; }
	/**
	 * Предупреждения от ФН
	 * @return
	 */
	public int FNWarnings() { return FN_WARINGS; }
	/**
	 * Версия протокола ФН
	 * @return
	 */
	public int protocolVersion() { return FN_PROTOCOL; }
	/**
	 * Серийный номер ФН
	 * @return
	 */
	public String FNSerial() { return FN_SERIAL; }
	/**
	 * Серийный номер ККТ
	 * @return
	 */
	public String DeviceSerial() { return DEVICE_SERIAL; }
	/**
	 * Регистрационный номер ККТ
	 * @return
	 */
	public String KKTNumber() { return KKT_NUMBER; }
}
