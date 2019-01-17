package rs.fnsample;

import com.google.zxing.BarcodeFormat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import rs.fn.IPrintManager;
import rs.fn.PrinterManager;
import rs.fn.Reports;
import rs.fn.Utils;
import rs.fn.Const.DocTypes;
import rs.fn.data.ArchiveReport;
import rs.fn.data.Correction;
import rs.fn.data.Document;
import rs.fn.data.FiscalReport;
import rs.fn.data.KKMInfo;
import rs.fn.data.SellOrder;
import rs.fn.data.WorkDay;

public class DocumentView extends Activity implements OnClickListener {
	private int _type;
	private Document _doc;
	private class ImagePrinter implements IPrintManager {
		private Paint P = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
		private Bitmap _SURFACE;
		private Canvas _Canvas;
		@Override
		public int open() {
			_SURFACE = Bitmap.createBitmap(Utils.PAGE_WIDTH, 400, Config.ARGB_8888);
			_Canvas = new Canvas(_SURFACE);
			_Canvas.drawColor(Color.WHITE);
			P.setColor(Color.BLACK);
			P.setTypeface(Typeface.create(Utils.FONT_NAME, 0));
			P.setTextSize(Utils.FONT_SIZE);
			return 0;
		}

		@Override
		public int drawText(String line, int x, int y) {
			return drawText(line, x, y, Utils.FONT_NAME, Utils.FONT_SIZE, false, false);
		}

		@Override
		public int drawText(String line, int x, int y, boolean bold) {
			return drawText(line, x, y, Utils.FONT_NAME, Utils.FONT_SIZE, bold, false);
		}

		@Override
		public int drawText(String line, int x, int y, String fontName, int fontSize, boolean bold, boolean italic) {
			if (_SURFACE == null)
				return -1;
			P.setTextSize(fontSize);
			float height = -P.ascent() + P.descent();
			if (y + height > _SURFACE.getHeight()) {
				Bitmap B = Bitmap.createBitmap(Utils.PAGE_WIDTH, _SURFACE.getHeight() + 400, Config.ARGB_8888);
				_Canvas = new Canvas(B);
				_Canvas.drawColor(Color.WHITE);
				_Canvas.drawBitmap(_SURFACE, 0, 0, P);
				_SURFACE.recycle();
				_SURFACE = B;
			}
			_Canvas.drawText(line, x, y + fontSize, P);
			return (int) height;
		}

		@Override
		public void printPage() {
			((ImageView)findViewById(R.id.iv_doc)).setImageBitmap(_SURFACE);
		}

		@Override
		public void close() {
			
		}

		@Override
		public int printQR(String value, int x, int y, int w, int h) {
			if (y + h > _SURFACE.getHeight()) {
				Bitmap B = Bitmap.createBitmap(Utils.PAGE_WIDTH, _SURFACE.getHeight() + 400, Config.ARGB_8888);
				_Canvas = new Canvas(B);
				_Canvas.drawColor(Color.WHITE);
				_Canvas.drawBitmap(_SURFACE, 0, 0, P);
				_SURFACE.recycle();
				_SURFACE = B;
			}
			Bitmap b = Utils.encodeAsBitmap(value, w, h,BarcodeFormat.QR_CODE);
			_Canvas.drawBitmap(b, x, y, P);
			b.recycle();
			return h;
		}

		@Override
		public int printLinear(String value, int x, int y, String type) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getState() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}

	private ImagePrinter _ip = new ImagePrinter();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doc_view);
		byte [] b = getIntent().getByteArrayExtra("BLOB");
		_type =  getIntent().getIntExtra("Type",0);
		
		
		findViewById(R.id.btn_print).setOnClickListener(this);
		switch(_type) {
		case DocTypes.DOC_TYPE_REGISTRATION:
			setTitle("Отчет о регистарации");
			_doc = Utils.unmarshall(b,KKMInfo.CREATOR);
			Reports.printRegistrationReport(this, (KKMInfo)_doc,_ip);
			break;
		case DocTypes.DOC_TYPE_OPENWD:
			setTitle("Открытие смены");
			_doc = Utils.unmarshall(b,WorkDay.CREATOR);
			Reports.printWOReport(this, (WorkDay)_doc,_ip);
			break;
		case DocTypes.DOC_TYPE_CLOSEWD:
			setTitle("Закрытие смены");
			_doc = Utils.unmarshall(b,WorkDay.CREATOR);
			Reports.printWCReport(this, (WorkDay)_doc,_ip);
			break;
		case DocTypes.DOC_TYPE_ORDER:	
			setTitle("Приход/расход");
			_doc = Utils.unmarshall(b,SellOrder.CREATOR);
			Reports.printCheck(this, (SellOrder)_doc,_ip,null);
			break;
		case DocTypes.DOC_TYPE_CORRECTION:
			setTitle("Чек коррекции");
			_doc = Utils.unmarshall(b,Correction.CREATOR);
			Reports.printCorrection(this, (Correction)_doc,_ip);
			break;
		case DocTypes.DOC_TYPE_CLOSEFN:	
			setTitle("Закрытие ФН");
			_doc = Utils.unmarshall(b,ArchiveReport.CREATOR);
			Reports.printCloseReport(this, (ArchiveReport)_doc,_ip);
			break;
		case DocTypes.DOC_TYPE_REPORT:
			setTitle("Отчет о расчетах");
			_doc = Utils.unmarshall(b,FiscalReport.CREATOR);
			Reports.printReport(this, (FiscalReport)_doc,_ip);
			break;
			
		}
	}
	@Override
	public void onClick(View v) {
		IPrintManager _ip = PrinterManager.getInstance();
		switch(_type) {
		case DocTypes.DOC_TYPE_REGISTRATION:
			Reports.printRegistrationReport(this, (KKMInfo)_doc,_ip);
			break;
		case DocTypes.DOC_TYPE_OPENWD:
			Reports.printWOReport(this, (WorkDay)_doc,_ip);
			break;
		case DocTypes.DOC_TYPE_CLOSEWD:
			Reports.printWCReport(this, (WorkDay)_doc,_ip);
			break;
		case DocTypes.DOC_TYPE_ORDER:	
			Reports.printCheck(this, (SellOrder)_doc,_ip,null);
			break;
		case DocTypes.DOC_TYPE_CORRECTION:
			Reports.printCorrection(this, (Correction)_doc,_ip);
			break;
		case DocTypes.DOC_TYPE_CLOSEFN:	
			Reports.printCloseReport(this, (ArchiveReport)_doc,_ip);
			break;
		case DocTypes.DOC_TYPE_REPORT:
			Reports.printReport(this, (FiscalReport)_doc,_ip);
			break;
			
		}
		
	}
}
