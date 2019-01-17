package rs.fn.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Документ коррекции
 * @author nick
 *
 */
public class Correction extends Document {

	/**
	 * Коррекция по предписанию
	 */
	public static final int TYPE_BY_PERCEPT = 1;
	/**
	 * Произвольная коррекция
	 */
	public static final int TYPE_BY_ARBITARITY = 0;
	
	private int _checkType;
	private int _cType;
	private int _vatType;
	private int _vatMode;
	protected int _number;
	
	private String _cDocNumber = "";
	private Calendar _cDocDate = Calendar.getInstance();
	private float _sum;
	private OU _casier = new OU();
	private List<Payment> _payments = new ArrayList<>();
	public Correction() {
	} 
	public Correction(Parcel in) {
		super(in);
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(_cType);
		dest.writeInt(_checkType);
		dest.writeInt(_vatType);
		dest.writeInt(_vatMode);
		dest.writeString(_cDocNumber);
		dest.writeLong(_cDocDate.getTimeInMillis());
		dest.writeFloat(_sum);
		dest.writeInt(_number);
		_casier.writeToParcel(dest, flags);
		Payment.writeCollection(_payments, dest, flags);
	}

	@Override
	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		_cType = in.readInt();
		_checkType = in.readInt();
		_vatType = in.readInt();
		_vatMode = in.readInt();
		_cDocNumber = in.readString();
		_cDocDate.setTimeInMillis(in.readLong());
		_sum = in.readFloat();
		_number = in.readInt();
		_casier.readFromParcel(in);
		Payment.readCollection(_payments, in);
	};
	
	/**
	 * Получить тип коррекции
	 * @return тип коррекции (см. константы TYPE_xxx)
	 */
	public byte type() { return (byte)(_checkType & 0x0F); }
	/**
	 * Установить тип коррекции
	 * @param t тип (см. константы TYPE_xxx)
	 */
	public void setType(int value) {
		_checkType = value;
	}
	/**
	 * 
	 * @return
	 */
	public byte correctionType() { return (byte)(_cType & 0x0F); }
	public void setCorrectionType(int t) {
		_cType = t;
	}
	/**
	 * Дата документа-основания 
	 * @return 
	 */
	public Calendar getDocumentDate() { return _cDocDate; }
	/**
	 * Получить номер документа основания
	 * @return призвольный номер документа
	 */
	public String getDocumentNumber() { 
		return _cDocNumber; 
	}
	/**
	 * Установить номер документа основания
	 * @param s произвольный непустой номер документа
	 */
	public void setDocumentNumber(String s) {
		_cDocNumber = s;
	}
	/**
	 * Получить сумму коррекции
	 * @return 
	 */
	public float getSum() { return _sum; }
	/**
	 * Платежи по документу коррекции
	 * @return
	 */
	public List<Payment> payments() { return _payments; }
	
	/**
	 * Установить сумму корекции
	 * @param f ненулевая неотрицательная сумма
	 */
	public void setSum(float f) {
		_sum = f;
	}
	
	/**
	 * Получить ставку налогообложения
	 * @return
	 */
	public int getVATType() { return _vatType; }
	/**
	 * Установить ставку налогообложения
	 * @param type
	 */
	public void setVATType(int type) { _vatType = type; }

	/**
	 * Получить используемую СНО
	 * @return
	 */
	public int getVATMode() { return _vatMode; }
	/**
	 * Установить используемую СНО
	 * @param type
	 */
	public void setVATMode(int type) { _vatMode = type; }
	
	/**
	 * Реквизиты кассира
	 * @return
	 */
	public OU casier() { return _casier; }

	public int number() { return _number; } 
	
	public static final Parcelable.Creator<Correction> CREATOR = new Parcelable.Creator<Correction>() {
		@Override
		public Correction createFromParcel(Parcel in) {
			Correction result = new Correction();
			result.readFromParcel(in);
			return result;
		}

		@Override
		public Correction[] newArray(int size) {
			return new Correction[size];
		} 
		
	};
}
