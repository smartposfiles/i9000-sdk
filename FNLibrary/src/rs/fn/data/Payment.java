package rs.fn.data;

import java.util.Collection;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Пункт оплаты
 * @author nick
 *
 */
public class Payment implements Parcelable  {

	/** 
	 * Тип оплаты - наличными
	 */
	public static final int PAYMENT_TYPE_CASH = 0;
	/**
	 * Тип оплаты - электронными
	 */
	public static final int PAYMENT_TYPE_CARD = 1;
	/**
	 * Тип оплаты - предоплата
	 */
	public static final int PAYMENT_TYPE_PREPAYMENT = 2;
	/**
	 * Тип оплаты - кредит
	 */
	public static final int PAYMENT_TYPE_CREDIT = 3;
	/**
	 * Тип оплаты - встречная
	 */
	public static final int PAYMENT_TYPE_AHEAD = 4;
	
	/**
	 * Тип оплаты
	 */
	public int TYPE;
	/**
	 * Сумма оплаты
	 */
	public float SUM;
	
	public Payment() { }
	public Payment(Parcel source) {
		TYPE = source.readInt();
		SUM  = source.readFloat();
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(TYPE);
		dest.writeFloat(SUM);
	}
	
	public static void writeCollection(Collection<Payment> list, Parcel dest, int flags) {
		dest.writeInt(list.size());
		for(Payment p : list)
			p.writeToParcel(dest, flags);
	}
	public static void readCollection(Collection<Payment> list, Parcel in) {
		list.clear();
		int nCount = in.readInt();
		while(nCount-- > 0)
			list.add(new Payment(in));
	}
}
