package rs.fn;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Parcel;
import android.os.Parcelable;


import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

/** 
 * Дополнительный функционал
 * @author nick
 *
 */
public class Utils {
	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;

	private static class StringPair {
		String f;
		String s;
		StringPair(String f, String s) {
			this.f  = f;
			this.s  = s;
		}
	}
	
	/**
	 * Ширина устройства печати ККТ 9000 в символах
	 */
	public static final int PAGE_WIDTH = 384;
	public static final int PAGE_WIDTH_CH = 32;
	/**
	 * Размер шрифта по умолчанию в пикселях
	 */
	public static final int FONT_SIZE = 20;
	/**
	 * Размер шрифта по умолчанию в точках устройства печати
	 */
	public static final int FONT_SIZE_W = (int)(PAGE_WIDTH/(float)PAGE_WIDTH_CH);
	
	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat DF = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	/**
	 * Шрифт для печати по умолчанию
	 */
	public static final String FONT_NAME = "monospace";
	/**
	 * Строка заполненная символом * по все ширине
	 * @return сформированная строка
	 */
	public static String line() {
		String s  = "*";
		while(s.length()*FONT_SIZE_W < PAGE_WIDTH)
			s+="*";
		return s;
	}
	/**
	 * Дополнить строку нулями слева до указаной длины
	 * @param value строка
	 * @param length длина
	 * @return
	 */
	public static String zeroPad(int value, int length) {
		return String.format("%0"+length+"d", value);
	}
	/**
	 * Дополнить строку нулями слева до указаной длины
	 * @param value строка
	 * @param length длина
	 * @return
	 */
	public static String zeroPad(String value, int length) {
		String r = value;
		while(r.length() < length)
			r = "0"+r;
		return r;
	}
	/**
	 * Дополнить строку слева до указаной длины
	 * @param value строка
	 * @param length длина
	 * @return
	 */
	public static String wsPad(String value, int length) {
		String r = value;
		while(r.length() < length)
			r = r+' ';
		return r;
	}
	
	private static String align(String s, String s1, char lastLine) {
		int maxColumnWidth = PAGE_WIDTH_CH/2;  
		List<StringPair> columns = new ArrayList<>();
		StringPair line = new StringPair(s, s1);
		columns.add(line);
		
		String result = "";
		int i=0, end = columns.size();
		if((s+s1+" ").length() <= PAGE_WIDTH_CH) {
			String rString = s;
			while((rString+s1).length() < PAGE_WIDTH_CH)
				rString+=lastLine;
			rString+=s1;
			return rString;
		}
		while(i < end) {
			line = columns.get(i++);
			if(line.f.length() > maxColumnWidth) {
				StringPair next;
				if(i ==columns.size()) {
					next = new StringPair("", "");
					columns.add(next);
				} else 
					next =  columns.get(i);
				next.f = line.f.substring(maxColumnWidth);
				line.f = line.f.substring(0,maxColumnWidth);
			}
			if(line.s.length() > maxColumnWidth) {
				StringPair next;
				if(i ==columns.size()) {
					next = new StringPair("", "");
					columns.add(next);
				} else 
					next =  columns.get(i);
				next.s = line.s.substring(maxColumnWidth);
				line.s = line.s.substring(0,maxColumnWidth);
			}
			end = columns.size();
		}
		i = 0;
		while(i < columns.size()) {
			StringPair p = columns.get(i++);
			if(!result.isEmpty()) result += "\n";
			result += p.f;
			int l = p.f.length(); 
			while( l++ < maxColumnWidth ) result += (i == columns.size() ? lastLine : ' ');
			l = p.s.length();
			while( l++ < maxColumnWidth ) result +=(i == columns.size() ? lastLine : ' ');
			result += p.s;
		}
		return result;
	}
	
	/**
	 * Прижать строку s к левому краю, s1 к правому по ширине устройства печати
	 * @param s "левая" строка
	 * @param s1 "правая" строка
	 * @return
	 */
	public static String align(String s, String s1) {
		return align(s,s1,' ');
	}
	/**
	 * Прижать строку s к левому краю, s1 к правому по ширине устройства печати,
	 * запонить промежуток между ними символом .
	 * @param s "левая" строка
	 * @param s1 "правая" строка
	 * @return
	 */
	public static String alignDot(String s, String s1) {
		return align(s,s1,'.');
	}

	/**
	 * Выровнять строку по центру устройства печати 
	 * @param s строка для выравнивания
	 * @return
	 */
	public static String center(String s) {
		boolean l = false;
		while(s.length() < PAGE_WIDTH_CH) {
			if(l)
				s = " "+s;
			else s += " ";
			l = !l;
		}
		return s;
		
	}
	/**
	 * Прижать строку к правому краю устройства печати
	 * @param s строка
	 * @return
	 */
	public static String right(String s) {
		while(s.length()< PAGE_WIDTH_CH)
			s = " "+s;
		return s;
	}

	/**
	 * Расчет CRC16
	 * @param data данные
	 * @param offset смещение
	 * @param length длина
	 * @param nPoly полином
	 * @return
	 */
	public static short CRC16(byte []data, int offset, int length, short nPoly) {
		short crc = (short)0xFFFF;
		for (int j=0;j<length;j++) {
			byte b = data[offset+j];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b   >> (7-i) & 1) == 1);
                boolean c15 = ((crc >> 15    & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= nPoly;
            }
        }
		crc &= 0xffff;
		return crc;
	}
	/**
	 * Проверить валидность регистрационного номера ККТ
	 * @param number номер
	 * @param inn ИНН пользователя
	 * @param device серийный номер ККТ
	 * @return номер, который ожидается для указанных параметров
	 */
	public static String checkRegNo(String number, String inn, String device) {
		if (number == null || number.length() != 16) {
			byte b[] = "0000000001".getBytes(Const.ENCODING);
			int sCRC = (CRC16(b, 0, b.length, (short) 0x1021) & 0xFFFF);
			return "0000000001"+String.format("%06d", sCRC);
		}
		while(inn.length() < 12) inn = "0"+inn;
		while(device.length() < 20) device = "0"+device;
		String num = number.substring(0, 10) + inn+device;
		String crc = number.substring(10);
		byte b[] = num.getBytes(Const.ENCODING);
		int sCRC = (CRC16(b, 0, b.length, (short) 0x1021) & 0xFFFF);
		try {
			if(Integer.parseInt(crc) == sCRC)
				return number;
			return number.substring(0, 10)+String.format("%06d", sCRC);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
	/**
	 * Поверить ИНН на валидность
	 * @param inn ИНН для проверки
	 * @return
	 */
	public static boolean checkINN(String inn) {
		if(inn.length() != 10 && inn.length() != 12) return false;
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
	/**
	 * Сформатировать дату в виде ДД.ММ.ГГГГ ЧЧ:мм
	 * @param date дата в милисекундах
	 * @return
	 */
	public static String formateDate(long date) {
		return DF.format(new Date(date));
	}
	/**
	 * Дополнить строку пробелами справа до указаной длины
	 * @param s строка
	 * @param i  длина
	 * @return
	 */
	public static String padRight(String s, int i) {
		while(s.length() < i)
			s += " ";
		return s;
	}
	
	/**
	 * Получить QR код как изображение
	 * @param contents строка для кодирования
	 * @param img_width ширина в пикселях
	 * @param img_height высота в пикселях
	 * @return
	 */
	public static Bitmap encodeAsBitmap(String contents, int img_width, int img_height,BarcodeFormat format) {
		String contentsToEncode = contents;
		if (contentsToEncode == null) {
			return Bitmap.createBitmap(img_width, img_height, Config.ARGB_8888);
		}
		Hashtable<EncodeHintType, Object> hints = null;
		String encoding = guessAppropriateEncoding(contentsToEncode);
		if (encoding != null) {
			hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result;
		try {
			result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
		} catch (Exception iae) {
			return Bitmap.createBitmap(img_width, img_height, Config.ARGB_8888);
		}
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	
	/**
	 * Получить hex-dump массива байт
	 * @param b
	 * @return
	 */
	public static String dump(byte [] b) {
		return dump(b,0,b.length);
	}
	/**
	 * Получить hex-dump массива байт
	 * @param b
	 * @param offset
	 * @param size
	 * @return
	 */
	public static String dump(byte [] b,int offset, int size) {
		String s  = "";
		for(int i=0;i<size;i++)
			s+=String.format("%02X",b[i+offset]);
		return s;
	}
	
	protected static String guessAppropriateEncoding(CharSequence contents) {
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}

	/**
	 * Обновиить CRC16 
	 * @param crc предыдущее значение CRC
	 * @param data данные
	 * @param offset смещение
	 * @param length длина
	 * @param nPoly полином
	 * @return
	 */
	public static int CRC16(int crc, byte[] data, int offset, int length, short nPoly) {
		if (crc == 0)
			crc = (short) 0xFFFF;
		for (int j = 0; j < length; j++) {
			byte b = data[offset + j];
			for (int i = 0; i < 8; i++) {
				boolean bit = ((b >> (7 - i) & 1) == 1);
				boolean c15 = ((crc >> 15 & 1) == 1);
				crc <<= 1;
				if (c15 ^ bit)
					crc ^= nPoly;
			}
		}
		crc &= 0xffff;
		return crc;
	}

	/**
	 * Прочитать дату из буфера считанного с ФН
	 * @param bb
	 * @return
	 */
	public static long readDate(ByteBuffer bb) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.YEAR,bb.get()+2000);
		cal.set(Calendar.MONTH,bb.get()-1);
		cal.set(Calendar.DAY_OF_MONTH,bb.get());
		cal.set(Calendar.HOUR_OF_DAY,bb.get());
		cal.set(Calendar.MINUTE,bb.get());
		return cal.getTimeInMillis();
	}

	/**
	 * Упаковать Parcelable в byte []
	 * @param parceable
	 * @return
	 */
	public static byte[] marshall(Parcelable parceable) {
        Parcel parcel = Parcel.obtain();
        parceable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle(); // not sure if needed or a good idea
        return bytes;
    }
	/**
	 * Получить Parcelable из byte []
	 * @param bytes
	 * @param creator
	 * @return
	 */
	public static <T extends Parcelable> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        return creator.createFromParcel(parcel);
    }

    private static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0); // this is extremely important!
        return parcel;
    }

}
