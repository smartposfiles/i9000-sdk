package rs.fn.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import rs.fn.BufferFactory;
import rs.fn.Const;

/**
 * Фискализируемый документ
 * 
 * @author nick
 *
 */
public class Document extends SparseArray<Tag> implements Parcelable {


	protected Signature SIGNATURE = new Signature();
	
	public Document() {
		add(1009,Const.EMPTY_STRING);
		add(1187,Const.EMPTY_STRING);
	}

	public Document(Parcel in) {
		this();
		readFromParcel(in);
	}

	/**
	 * Получить адрес расчетов
	 * @return
	 */
	public String getPayAddress() {
		return get(1009).asString();
	}
	/**
	 * Установить адрес расчетов
	 * @param value
	 */
	public void setPayAddress(String value) {
		add(1009, value);
	}
	/**
	 * Получить место расчетов
	 * @return
	 */
	public String getPayPlace() {
		return get(1187).asString();
	}
	/**
	 * Установить место расчетов
	 * @param value
	 */
	public void setPayPlace(String value) {
		add(1187, value);
	}

	/**
	 * Фискальная подпись документа
	 * @return
	 */
	public Signature signature() { return SIGNATURE; }
	/**
	 * Признак "документ подписан"
	 * @return
	 */
	public boolean isSigned() { return SIGNATURE.number() > 0; }
	
	@Override
	public int describeContents() {
		return 0;
	}

	
	
	public void readFromParcel(Parcel in) {
		Tag.readCollection(this, in);
		SIGNATURE.readFromParcel(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Tag.writeCollection(this, dest, flags);
		SIGNATURE.writeToParcel(dest);
	}
	/**
	 * Добавить таг со значением типа byte 
	 * @param ID
	 * @param value
	 */
	public void add(int ID, byte value) {
		put(ID, new Tag(ID, value));
	}
	/**
	 * Добавить таг со значением типа boolean (сохраняется как byte 0/1) 
	 * @param ID
	 * @param value
	 */
	public void add(int ID, boolean value) {
		put(ID, new Tag(ID, value));
	}
	/**
	 * Добавить тег со значением типа short (2 байта)
	 * @param ID
	 * @param value
	 */
	public void add(int ID, short value) {
		put(ID, new Tag(ID, value));
	}
	/**
	 * Добавить тег со значением типа int (4 байта)
	 * @param ID
	 * @param value
	 */
	public void add(int ID, int value) {
		put(ID, new Tag(ID, value));
	}
	/**
	 * Добавить тег со значением типа строка
	 * @param ID
	 * @param value
	 */
	public void add(int ID, String value) {
		put(ID, new Tag(ID, value));
	}
	/**
	 * Добавить тег со значением типа float c указанным количеством знаков после запятой
	 * @param ID
	 * @param value
	 * @param desnsity
	 */
	public void add(int ID, float value, int desnsity) {
		put(ID, new Tag(ID, value, desnsity));
	}
	/**
	 * Добавить тег со значением типа float с 2 знаками после запятой
	 * @param ID
	 * @param value
	 */
	public void add(int ID, float value) {
		put(ID, new Tag(ID, value));
	}
	/**
	 * Добавить тег со значением типа STLV
	 * @param ID
	 * @param values
	 */
	public void add(int ID, Tag... values) {
		put(ID, new Tag(ID, values));
	}
	public void add(int ID, Collection<Tag> values) {
		put(ID, new Tag(ID, values));
	}
	
	/**
	 * Скопировать документ
	 * @param dest
	 */
	public void cloneTo(Document dest) {
		if(dest == null) return;
        Parcel parcel = Parcel.obtain();
        writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
		dest.readFromParcel(parcel);
		parcel.recycle();
	}
	/**
	 * Получить данные для записи в ФН командой 07
	 * @return Список массивов байт, каждый из которых должен быть записан отдельной командой 07
	 */
	protected List<byte []> pack() {
		ArrayList<byte []> result = new ArrayList<>();
		ByteBuffer bb = BufferFactory.allocate(BufferFactory.BUFFER_RECORD);
		for(int i=0;i<size();i++) {
			if(bb.position() > 700) {
				byte [] array = new byte[bb.position()];
				System.arraycopy(bb.array(), 0, array, 0, array.length);
				result.add(array);
				bb.clear();
			}
			Tag t = valueAt(i);
			if(t.DATA.length > 0) {
				bb.putShort(t.ID);
				bb.putShort((short)t.DATA.length);
				bb.put(t.DATA);
			}
		}
		if(bb.position() > 0) {
			byte [] array = new byte[bb.position()];
			System.arraycopy(bb.array(), 0, array, 0, array.length);
			result.add(array);
		}
		BufferFactory.release(bb);
		return result;
	}
	/**
	 * Задан ли данный тег для документа
	 * @param key
	 * @return
	 */
	public boolean hasTag(int key) { return get(key) != null; }

}
