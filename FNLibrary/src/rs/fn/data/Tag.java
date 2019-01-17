package rs.fn.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Collection;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import rs.fn.BufferFactory;
import rs.fn.Const;

/**
 * Произвольный тег
 * 
 * @author nick
 *
 */
public class Tag implements Parcelable {

	final short ID;
	final byte[] DATA;

	public Tag(int ID, ByteBuffer bb) {
		this.ID = (short)(ID & 0xffff);
		DATA = new byte[bb.getShort()];
		bb.get(DATA);
	}
	private Tag(ByteBuffer bb) {
		this.ID = bb.getShort();
		DATA = new byte[bb.getShort()];
		bb.get(DATA);
	}

	private Tag(Tag source) {
		ID = source.ID;
		DATA = new byte[source.DATA.length];
		System.arraycopy(source.DATA, 0, DATA, 0, DATA.length);
	}

	public Tag(int ID, byte value) {
		this.ID = (short) (ID & 0xFFFF);
		DATA = new byte[] { value };
	}

	public Tag(int ID, Calendar cal) {
		this(ID,cal,false);
	}
	public Tag(int ID, Calendar cal, boolean asDate) {
		this.ID = (short)(ID & 0xFFFF);
		if(asDate) {
			DATA = new byte[5];
			DATA[0] = (byte)(cal.get(Calendar.YEAR) - 2000);
			DATA[1] = (byte)(cal.get(Calendar.MONTH) +1 );
			DATA[2] = (byte)(cal.get(Calendar.DAY_OF_MONTH));
			DATA[3] = (byte)(cal.get(Calendar.HOUR_OF_DAY));
			DATA[4] = (byte)(cal.get(Calendar.MINUTE));
		} else {
			int value = (int)(cal.getTimeInMillis()/1000);
			DATA = new byte[] { (byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF), (byte) ((value >> 16) & 0xFF),
					(byte) ((value >> 24) & 0xFF) };
		}
	}
	
	public Tag(int ID, boolean value) {
		this.ID = (short) (ID & 0xFFFF);
		DATA = new byte[] { (byte) (value ? 1 : 0) };
	}

	public Tag(int ID, short value) {
		this.ID = (short) (ID & 0xFFFF);
		DATA = new byte[] { (byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF) };
	}

	public Tag(int ID, int value) {
		this.ID = (short) (ID & 0xFFFF);
		DATA = new byte[] { (byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF), (byte) ((value >> 16) & 0xFF),
				(byte) ((value >> 24) & 0xFF) };
	}

	
	public Tag(int ID, float value) {
		this.ID = (short) (ID & 0xFFFF);
		ByteBuffer bb = BufferFactory.allocate(BufferFactory.BUFFER_RECORD);
		try {
			long v = (int) (round2(value, 2) * 100);
			if (v < 256)
				bb.put((byte) (v & 0xFF));
			else if (v < 65536)
				bb.putShort((short) (v & 0xFFFF));
			else
				bb.putInt((int) v);
			DATA = new byte[bb.position()];
			System.arraycopy(bb.array(), 0, DATA, 0, DATA.length);
		} finally {
			BufferFactory.release(bb);
		}
	}

	public Tag clone() {
		return new Tag(this);
	}
	
	public Tag(int ID, float value, int density) {
		this.ID = (short) (ID & 0xFFFF);
		ByteBuffer bb = BufferFactory.allocate(BufferFactory.BUFFER_RECORD);
		try {
			long v = (int) (round2(value, density) * Math.pow(10, density));
			bb.put((byte) (density & 0xFF));
			if (v < 256)
				bb.put((byte) (v & 0xFF));
			else if (v < 65536)
				bb.putShort((short) (v & 0xFFFF));
			else
				bb.putInt((int) v);
			DATA = new byte[bb.position()];
			System.arraycopy(bb.array(), 0, DATA, 0, DATA.length);
		} finally {
			BufferFactory.release(bb);
		}
	}

	private static float round2(float number, int scale) {
		int pow = 10;
		for (int i = 1; i < scale; i++)
			pow *= 10;
		float tmp = number * pow;
		return ((float) ((int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp))) / pow;
	}

	public Tag(int ID, byte [] value) {
		this.ID = (short) (ID & 0xFFFF);
		DATA = new byte[value.length];
		System.arraycopy(value, 0, DATA, 0, DATA.length);
	}
	public Tag(int ID, String value) {
		this.ID = (short) (ID & 0xFFFF);
		DATA = value.getBytes(Const.ENCODING);
	}

	public Tag(int ID, SparseArray<Tag> tags) {
		this.ID = (short) (ID & 0xFFFF);
		ByteBuffer bb = BufferFactory.allocate(BufferFactory.BUFFER_RECORD);
		try {
			bb.order(ByteOrder.LITTLE_ENDIAN);
			for (int i =0 ;i < tags.size();i++) {
				Tag t = tags.valueAt(i);
				if(bb.remaining() < 4+t.DATA.length) break;
				bb.putShort(t.ID);
				bb.putShort((short) (t.DATA.length & 0xFFFF));
				bb.put(t.DATA);
			}
			DATA = new byte[bb.position()];
			System.arraycopy(bb.array(), 0, DATA, 0, DATA.length);
		} finally {
			BufferFactory.release(bb);
		}
		
	}
	public Tag(int ID, Collection<Tag> values) {
		this.ID = (short) (ID & 0xFFFF);
		ByteBuffer bb = BufferFactory.allocate(BufferFactory.BUFFER_RECORD);
		try {
			bb.order(ByteOrder.LITTLE_ENDIAN);
			for (Tag t : values) {
				if(t != null) {
					if(bb.remaining() < 4+t.DATA.length) break;
					bb.putShort(t.ID);
					bb.putShort((short) (t.DATA.length & 0xFFFF));
					bb.put(t.DATA);
				}
			}
			DATA = new byte[bb.position()];
			System.arraycopy(bb.array(), 0, DATA, 0, DATA.length);
		} finally {
			BufferFactory.release(bb);
		}
	}
	
	public Tag(int ID, Tag... values) {
		this.ID = (short) (ID & 0xFFFF);
		ByteBuffer bb = BufferFactory.allocate(BufferFactory.BUFFER_RECORD);
		try {
			bb.order(ByteOrder.LITTLE_ENDIAN);
			for (Tag t : values) {
				if(t != null) {
					if(bb.remaining() < 4+t.DATA.length) break;
					bb.putShort(t.ID);
					bb.putShort((short) (t.DATA.length & 0xFFFF));
					bb.put(t.DATA);
				}
			}
			DATA = new byte[bb.position()];
			System.arraycopy(bb.array(), 0, DATA, 0, DATA.length);
		} finally {
			BufferFactory.release(bb);
		}
	}

	public Tag(Parcel in) {
		ID = (short) (in.readInt() & 0xFFFF);
		int size = in.readInt();
		DATA = new byte[size];
		for (int i = 0; i < DATA.length; i++)
			DATA[i] = in.readByte();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeInt(ID);
		dest.writeInt(DATA.length);
		for (int i = 0; i < DATA.length; i++)
			dest.writeByte(DATA[i]);
	}

	public byte asByte() {
		return DATA[0];
	}

	public boolean asBoolean() {
		return DATA[0] > 0;
	}

	public short asShort() {
		return (short) ((DATA[0] << 8) | DATA[0]);
	}

	public int asInt() {
		return (DATA[3] << 24) | (DATA[2] << 16) | (DATA[1] << 8) | DATA[0];
	}

	public float asFloat() {
		ByteBuffer bb = ByteBuffer.wrap(DATA);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int pow = bb.get();
		long v = 0;
		if (bb.remaining() == 1)
			v = bb.get();
		else if (bb.remaining() == 2)
			v = (bb.getShort() & 0xFFFF);
		else
			v = (bb.getInt() & 0xFFFFFFFF);
		return (float) (v / Math.pow(10, pow));
	}

	public float asFFloat() {
		ByteBuffer bb = ByteBuffer.wrap(DATA);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		long v = 0;
		if (bb.remaining() == 1)
			v = bb.get();
		else if (bb.remaining() == 2)
			v = (bb.getShort() & 0xFFFF);
		else
			v = (bb.getInt() & 0xFFFFFFFF);
		return (float) (v / 100);
	}
	
	public String asString() {
		if(DATA.length == 0) return Const.EMPTY_STRING;
		return new String(DATA, Const.ENCODING);
	}

	public SparseArray<Tag> asSTLV() {
		SparseArray<Tag> result = new SparseArray<>();
		ByteBuffer bb = ByteBuffer.wrap(DATA);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		while (bb.remaining() > 0) {
			Tag T = new Tag(bb);
			result.put(T.ID, T);
		}
		return result;
	}

	public byte [] pack() {
		byte [] b = new byte[DATA.length+4];
		b[0] = (byte)(ID & 0xFF);
		b[1] = (byte)((ID >> 8 )& 0xFF);
		int size = DATA.length;
		b[2] = (byte)(size & 0xFF);
		b[3] = (byte)((size >> 8) & 0xFF);
		System.arraycopy(DATA, 0, b, 4, DATA.length);
		return b;
	}
	
	public boolean hasData() {
		return DATA != null && DATA.length > 0;
	}
	public static void writeCollection(SparseArray<Tag> list, Parcel dest, int flags) {
		dest.writeInt(list.size());
		for (int i = 0; i < list.size(); i++)
			list.valueAt(i).writeToParcel(dest, flags);
	}
	

	public static void readCollection(SparseArray<Tag> list, Parcel in) {
		list.clear();
		int nCount = in.readInt();
		while (nCount-- > 0) {
			Tag t = new Tag(in);
			list.put(t.ID, t);
		}
	}
	
	

}
