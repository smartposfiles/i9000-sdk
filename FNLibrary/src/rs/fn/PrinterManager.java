package rs.fn;

import com.google.zxing.BarcodeFormat;

import android.graphics.Bitmap;
/**
 * Устройство печати ККТ 9000
 * @author nick
 *
 */
public class PrinterManager implements IPrintManager {

	private static long COUNTER = 0;

	// Cчетчик пробега головки
	public static long getCounter() { return COUNTER; }
	// Сбросить счетчик пробега
	public static void resetCounter() { COUNTER = 0; }
	
	private android.device.PrinterManager _printer;
	private static PrinterManager _instance;
	

	public static PrinterManager getInstance() {
		if (_instance == null)
			_instance = new PrinterManager();
		return _instance;
	}

	
	public PrinterManager() {
		try {
			_printer = new android.device.PrinterManager();
		} catch (Exception e) {
			_printer = null;
		}
	}

	public int open() {
		try {
			int result = _printer.getStatus();
			if (result != 0 && result != -3) 
				return result;
			result = _printer.open();
			if (result == 0) {
				_printer.setupPage(Utils.PAGE_WIDTH, -1);
				_printer.setGrayLevel(3);
			}
			return result;
		} catch (Exception e) {
			return -1;
		}
	}

	public int drawText(String line, int x, int y) {
		return drawText(line, x, y, Utils.FONT_NAME, Utils.FONT_SIZE, false, false);
	}

	public int drawText(String line, int x, int y, boolean bold) {
		return drawText(line, x, y, Utils.FONT_NAME, Utils.FONT_SIZE, bold, false);
	}

	public int drawText(String line, int x, int y, String fontName, int fontSize, boolean bold, boolean italic) {
		if(line == null || line.isEmpty()) return 0;
		if(Utils.USE_UPCASE) line = line.toUpperCase();
		try {
			int height = 0;
			for(String s : line.split("\n")) {
				while(s.length() > Utils.PAGE_WIDTH_CH) {
					int l =  Utils.PAGE_WIDTH_CH;
					height += _printer.drawText(s.substring(0,l), x, y+height, fontName, fontSize, bold, italic, 0);
					s = s.substring(l);
				} 
				if(!s.isEmpty())
					height += _printer.drawText(s, x, y+height, fontName, fontSize, bold, italic, 0);
			}
			COUNTER += height;
			return height;
			
		} catch (Exception e) {
			return -1;
		}
	}

	public void printPage() {
		try {
			_printer.printPage(0);
		} catch (Exception e) {
		}
	}

	public void close() {
		if (_printer == null)
			return;
		try {
			_printer.close();
		} catch (Exception e) {
		}
	}

	public int getState() {
		if (_printer == null)
			return 0;
		try {
			return _printer.getStatus();
		} catch (Exception e) {
			return -1;
		}

	}

	public int printQR(String value, int x, int y, int w, int h) {
		Bitmap barcode = Utils.encodeAsBitmap(value, w, h,BarcodeFormat.QR_CODE);
		try {
			try {
				_printer.drawBitmap(barcode, x, y);
				COUNTER+=h;
			} catch (Exception e) {
				return -1;
			}
		} finally {
			barcode.recycle();
		}
		return h;
	}


	public String getLastError() {
		int s = getState();
		switch (s) {
		case 0:
			return "Нет ошибок";
		case -1:
			return "Нет бумаги";
		case -2:
			return "Перегрев";
		case -3:
			return "Аккумулятор разряжен";
		}
		return String.format("Другая ошибка (%d)", s);
	}

	@Override
	public int printLinear(String value, int x, int y, String type) {
		BarcodeFormat bf = BarcodeFormat.EAN_13;
		int h = (Utils.PAGE_WIDTH_CH*Utils.FONT_SIZE_W -x)/3;
		int w = Utils.PAGE_WIDTH_CH*Utils.FONT_SIZE_W-x;
		if("EAN8".equals(type)) bf = BarcodeFormat.EAN_8;
		if("CODE128".equals(type)) bf = BarcodeFormat.CODE_128;
		if("CODE93".equals(type)) bf = BarcodeFormat.CODE_39;
		if("CODE39".equals(type)) bf = BarcodeFormat.CODE_39;
		if("UPCA".equals(type)) bf = BarcodeFormat.UPC_A;
		if("QR".equals(type)) {
			bf = BarcodeFormat.QR_CODE;
			w = h = (Utils.PAGE_WIDTH_CH*Utils.FONT_SIZE_W-x)/2;
		}
		Bitmap barcode = Utils.encodeAsBitmap(value, w,h ,bf);
		_printer.drawBitmap(barcode, x, y);
		barcode.recycle();
		COUNTER+=h;
		return h;
	}


	@Override
	public int printBitmap(Bitmap b, int x, int y) {
		_printer.drawBitmap(b, x, y);
		COUNTER+=b.getHeight();
		return b.getHeight();
	}

}
