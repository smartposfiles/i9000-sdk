package rs.fn.data;

import java.util.Collection;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;


/**
 * Предмет рассчета
 * @author nick
 *
 */
public class SellItem extends SparseArray<Tag> implements Parcelable {
	
	public static final int ITEM_TYPE_GOOD = 1;
	public static final int ITEM_TYPE_ACSIZES_GOOD = 2;
	public static final int ITEM_TYPE_WORK = 3;
	public static final int ITEM_TYPE_SERVICE = 4;
	public static final int ITEM_TYPE_BET = 5;
	public static final int ITEM_TYPE_GAIN = 6;
	public static final int ITEM_TYPE_LOTTERY_TICKET = 7;
	public static final int ITEM_TYPE_LOTTERY_GAIN = 8;
	public static final int ITEM_TYPE_RID = 9;
	public static final int ITEM_TYPE_PAYMENT = 10;
	public static final int ITEM_TYPE_AGENT_COMISSIONS = 11;
	public static final int ITEM_TYPE_COMPOSITE = 12;
	public static final int ITEM_TYPE_MISC = 13;

	public static final int PAY_TYPE_PAY_AHEAD_100 = 1;
	public static final int PAY_TYPE_PAY_AHEAD = 2;
	public static final int PAY_TYPE_PAY_ADVANCE = 3;
	public static final int PAY_TYPE_FULL = 4;
	public static final int PAY_TYPE_PARTIAL_CREDIT = 5;
	public static final int PAY_TYPE_CREDID = 6;
	public static final int PAY_TYPE_CREDIT_PAYMENT = 7;
	public static final int VAT_TYPE_20 = 0;
	public static final int VAT_TYPE_10 = 1;
	public static final int VAT_TYPE_20_120 = 2;
	public static final int VAT_TYPE_10_110 = 3;
	public static final int VAT_TYPE_0 = 4;
	public static final int VAT_TYPE_NONE = 5;
	public static final int VAT_TYPE_18 = 6;
	public static final int VAT_TYPE_18_118 = 7;		
	
	/**
	 * Наименование предмета расчета
	 */
	public String NAME = "";
	/**
	 * Тип предмета расчета
	 */
	public int ITEM_TYPE = 1;
	/**
	 * Признак способа расчета
	 */
	public int PAY_TYPE = 4; 
	/**
	 * Количество предмета расчета
	 */
	public float  QTTY = 1.00f; 
	/**
	 * Стоимость единицы предмета расчета
	 */
	public float  PRICE = 0f;
	/**
	 * Тип налогообложения
	 */
	public int    VAT_TYPE;
	/**put(1199, new Tag(1199,(byte) (VAT_TYPE + 1)));
	 * Тип агентской услуги
	 */
	public int    AGENT_TYPE = -1;
	/**
	 * Дополнительные теги
	 */
	
	public SellItem() {}
	public SellItem(Parcel source) {
		ITEM_TYPE = source.readInt();
		NAME = source.readString();
		QTTY = source.readFloat();
		PRICE = source.readFloat();
		VAT_TYPE = source.readInt();
		AGENT_TYPE = source.readInt();
		PAY_TYPE = source.readInt();
		Tag.readCollection(this, source);
		
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(ITEM_TYPE);
		dest.writeString(NAME);
		dest.writeFloat(QTTY);
		dest.writeFloat(PRICE);
		dest.writeInt(VAT_TYPE);
		dest.writeInt(AGENT_TYPE);
		dest.writeInt(PAY_TYPE);
		Tag.writeCollection(this, dest, flags);
	}
	/**
	 * Получить сумму НДС
	 * @return
	 */
	public float vatValue() {
		switch (VAT_TYPE) {
		case VAT_TYPE_10:	
		case VAT_TYPE_10_110:
			return (PRICE * QTTY) * 10f / 110f;
		case VAT_TYPE_20:
		case VAT_TYPE_20_120:	
			if(System.currentTimeMillis() > 1546300800000L) 
				return (PRICE * QTTY) * 20f / 120f;
		case VAT_TYPE_18:	
		case VAT_TYPE_18_118:
			return (PRICE * QTTY) * 18f / 118f;
		}
		
		return (PRICE * QTTY);

	}
	/**
	 * Общая стоимость предмета расчета
	 * @return
	 */
	public float sum() {
		return (QTTY*PRICE * 100f) / 100f;
	}

	public Tag pack() {
		if(AGENT_TYPE > 0) 
			put(1222,new Tag(1222,(byte)AGENT_TYPE));
		put(1214,new Tag(1214,(byte)PAY_TYPE));
		put(1212,new Tag(1212,(byte)ITEM_TYPE));
		put(1030,new Tag(1030,NAME));
		put(1079,new Tag(1079,PRICE));
		put(1023,new Tag(1023,QTTY,3));
		if(VAT_TYPE < 6)
			put(1199, new Tag(1199,(byte) (VAT_TYPE + 1)));
		else {
			if(VAT_TYPE == 6)
				put(1199, new Tag(1199,(byte)1));
			else
				put(1199, new Tag(1199,(byte)3));
		}
		put(1043,new Tag(1043,PRICE*QTTY));
		if (VAT_TYPE < 4)
			put(1200, new Tag(1200,vatValue()));
		return new Tag(1059, this);
	}
	
	public static void writeCollection(Collection<SellItem> list, Parcel dest, int flags) {
		dest.writeInt(list.size());
		for(SellItem p : list)
			p.writeToParcel(dest, flags);
	}
	
	public static void readCollection(Collection<SellItem> list, Parcel in) {
		list.clear();
		int nCount = in.readInt();
		while(nCount-- > 0)
			list.add(new SellItem(in));
	}

	public void add(int ID, byte value) {
		put(ID, new Tag(ID, value));
	}

	public void add(int ID, boolean value) {
		put(ID, new Tag(ID, value));
	}

	public void add(int ID, short value) {
		put(ID, new Tag(ID, value));
	}

	public void add(int ID, int value) {
		put(ID, new Tag(ID, value));
	}

	public void add(int ID, String value) {
		put(ID, new Tag(ID, value));
	}

	public void add(int ID, float value, int desnsity) {
		put(ID, new Tag(ID, value, desnsity));
	}

	public void add(int ID, float value) {
		put(ID, new Tag(ID, value));
	}

	public void add(int ID, Tag... values) {
		put(ID, new Tag(ID, values));
	}
	
	public static final Parcelable.Creator<SellItem> CREATOR = new Parcelable.Creator<SellItem>() {

		@Override
		public SellItem createFromParcel(Parcel in) {
			return new SellItem(in);
		}

		@Override
		public SellItem[] newArray(int size) {
			return new SellItem[size];
		}
		
	};
	
}

