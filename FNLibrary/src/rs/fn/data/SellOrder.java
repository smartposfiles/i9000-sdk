package rs.fn.data;

import java.util.ArrayList;
import java.util.List;


import android.os.Parcel;
import android.os.Parcelable;


/**
 * Кассовый чек
 * 
 * @author nick
 *
 */
public class SellOrder extends Document  {

	public static final int TYPE_INCOME = 0;
	public static final int TYPE_OUTCOME = 1;
	public static final int TYPE_RETURN_INCOME = 2;
	public static final int TYPE_RETURN_OUTCOME = 3;
	
	private int _type;
	private int _taxMode;
	protected int _number;
	protected int _wdNumber;
	protected boolean _isAutoMode;
	protected int _automateNumber;
	protected float _refund;
	private List<Payment>  PAYMENTS = new ArrayList<>();
	private List<SellItem> ENTRIES = new ArrayList<>();
	private OU _casier = new OU();

	public SellOrder() { }
	public SellOrder(int type) {
		_type = type;
	}
	public SellOrder(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		_type = in.readInt();
		_taxMode = in.readInt();
		_number = in.readInt();
		_wdNumber = in.readInt();
		_isAutoMode = in.readInt() != 0;
		_automateNumber = in.readInt(); 
		_casier.readFromParcel(in);
		_refund = in.readFloat();
		SellItem.readCollection(ENTRIES, in);
		Payment.readCollection(PAYMENTS, in);
	}
	public String getSenderEmail() { return get(1117).asString(); }
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(_type);
		dest.writeInt(_taxMode);
		dest.writeInt(_number);
		dest.writeInt(_wdNumber);
		dest.writeInt(_isAutoMode? 1 : 0);
		dest.writeInt(_automateNumber);
		_casier.writeToParcel(dest, flags);
		dest.writeFloat(_refund);
		SellItem.writeCollection(ENTRIES, dest, flags);
		Payment.writeCollection(PAYMENTS, dest, flags);
	}
	
	public float getRefund() { return _refund; }
	/**
	 * Признак "установлен в автомате"
	 * @return
	 */
	public boolean isAutoMode() { return _isAutoMode; }
	/**
	 * Номер автомата
	 * @return
	 */
	public int automateNumber() { return _automateNumber; }
	/**
	 * Адрес сайта ФНС
	 * @return
	 */
	public String fnsUrl() { return get(1060).asString(); }
	/**
	 * Номер смены
	 * @return
	 */
	public int workDayNumber() { return _wdNumber; }
	/**
	 * Тип чека (приход, расход, возврат прихода, возврат расхода) для ФН
	 * @return
	 */
	public int getType() {
		return _type;
	}

	/**
	 * Общая сумма по чеку
	 * 
	 * @return
	 */
	public float sum() {
		float s = 0;
		for (SellItem e : ENTRIES)
			s += e.PRICE * e.QTTY;
		return (s* 100f)/100f;
	}
	/**
	 * Добавить предмет расчета
	 * @param item
	 */
	public void addItem(SellItem item) {
		ENTRIES.add(item);
	}
	/**
	 * Предметы расчета
	 * @return
	 */
	public List<SellItem> items() {
		return ENTRIES;
	}

	/**
	 * Завершить чек
	 * 
	 * @param taxMode
	 *            - режим налогообложения
	 * @param payments
	 *            - платежи по чеку
	 * @param casier
	 *            - данные кассира
	 */
	public void setPaymentsDetails(int taxMode, List<Payment> payments, OU casier) {
		_taxMode = taxMode;
		PAYMENTS = payments;
		casier.cloneTo(_casier);
	}

	/**
	 * Номер чека в текущей смене
	 * 
	 * @return
	 */
	public int getNumber() {
		return _number;
	}
	/**
	 * Оплаты по чеку
	 * @return
	 */
	public List<Payment> payments() {
		return PAYMENTS;
	}
	/**
	 * Используемая СНО
	 * @return
	 */
	public int getTaxMode() {
		return _taxMode;
	}
	/**
	 * Реквизиты кассира
	 * @return
	 */
	public OU casier() { return _casier; }
	
	/**
	 * Сдача
	 * @return
	 */
	public float refund() { return _refund; }
	
	
	public static final Parcelable.Creator<SellOrder> CREATOR = new Parcelable.Creator<SellOrder>() {
		@Override
		public SellOrder createFromParcel(Parcel source) {
			return new SellOrder(source);
		}

		@Override
		public SellOrder[] newArray(int size) {
			return new SellOrder[size];
		}
		
	};
	
}
