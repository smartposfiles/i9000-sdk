package rs.fnsample;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import rs.fn.AppCore;
import rs.fn.AppCore.LocalMessageReceiver;
import rs.fn.Const;
import rs.fn.Const.Errors;
import rs.fn.Utils;
import rs.fn.data.Correction;
import rs.fn.data.Document;
import rs.fn.data.FiscalReport;
import rs.fn.data.KKMInfo;
import rs.fn.data.OU;
import rs.fn.data.Payment;
import rs.fn.data.SellItem;
import rs.fn.data.SellOrder;
import rs.fn.data.WorkDay;

public class MainActivity extends Activity implements LocalMessageReceiver, OnClickListener {

	private static final int MSG_SHIFT_ACTION_DONE = 1002;
	private static final int MSG_CORRECTION_DONE = 1003;
	private static final int MSG_SELL_ORDER_DONE = 1004;
	private static final int MSG_FISCAL_REPORT_DONE = 1006;
	
	private View _wd,_sale,_report,_cr,_docs, _rests;
	private KKMInfo _info;
	private OU casier = new OU();
	public MainActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		casier.setName("Кассир");
		casier.setINN("7704211201");
		setContentView(R.layout.no_fn);
		setupUI();
		Core.getInstance().registerLocalReceiver(this);
		Core.getInstance().init();
	}

	private void setupUI() {
		setContentView(R.layout.main);
		_wd = findViewById(R.id.fn_wd);
		_wd.setOnClickListener(this);
		_sale = findViewById(R.id.fn_sale);
		_sale.setOnClickListener(this);
		_report = findViewById(R.id.fn_report);
		_report.setOnClickListener(this);
		_cr = findViewById(R.id.fn_correct);
		_cr.setOnClickListener(this);
		_docs = findViewById(R.id.fn_docs);
		_docs.setOnClickListener(this);
		_rests = findViewById(R.id.fn_rests);
		_rests.setOnClickListener(this);
	}
	

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.fn_rests:
			startActivity(new Intent(this,RestsActivity.class));
			break;
		case R.id.fn_docs:
			startActivity(new Intent(this,Documents.class));
			break;
		case R.id.fn_correct:
			Correction correction = new Correction();
			correction.setType(Correction.TYPE_BY_PERCEPT); // самостоятельная коррекция 
			correction.setCorrectionType(SellOrder.TYPE_INCOME); // Приход
			correction.setSum(300.0f); // Сумма коррекции 300
			correction.getDocumentDate().setTimeInMillis(System.currentTimeMillis() - 300000); // дата документа -5 минут от текущего времени
			correction.setDocumentNumber("Распорядение 12/300");
			correction.setVATMode(KKMInfo.SNO_BF_COMMON); // Общая СНО
			correction.setVATType(SellItem.VAT_TYPE_NONE); // Не облагается НДС
			Payment payment = new Payment();
			payment.SUM = correction.getSum();
			payment.TYPE = Payment.PAYMENT_TYPE_CASH; // Оплата наличными
			correction.payments().add(payment);
			Core.getInstance().doCorrection(this, MSG_CORRECTION_DONE, correction);
			break;
		case R.id.fn_wd:
			if(!_info.isWorkDayOpen())
				Core.getInstance().openShift(this, MSG_SHIFT_ACTION_DONE,casier);
			else 
				Core.getInstance().closeShift(this, MSG_SHIFT_ACTION_DONE,casier);
			break;
		case R.id.fn_sale:
			SellOrder order = new SellOrder(SellOrder.TYPE_INCOME); // Приход
			SellItem item1 = new SellItem();
			item1.ITEM_TYPE = SellItem.ITEM_TYPE_GOOD; // Товар
			item1.NAME = "Товар";
			item1.PAY_TYPE = SellItem.PAY_TYPE_FULL; // Полный расчет
			item1.QTTY = 2.00f;
			item1.PRICE = 300;
			item1.VAT_TYPE = SellItem.VAT_TYPE_20; // НДС 18%/20% АВТОПЕРЕКЛЮЧЕНИЕ после 01.01.19 
			item1.add(1197, "шт."); // Добавляем тег единица измерений
			order.addItem(item1);
			SellItem item2 = new SellItem();
			item2.ITEM_TYPE = 15; // SellItem.ITEM_TYPE_GOOD; // Товар
			item2.NAME = "12";
			item2.PAY_TYPE = SellItem.PAY_TYPE_FULL; // Полный расчет
			item2.QTTY = 5.30f;
			item2.PRICE = 20;
			item2.VAT_TYPE = SellItem.VAT_TYPE_10; // НДС 10%
			item2.add(1197, "кг."); // Добавляем тег единица измерений
			order.addItem(item2);
			List<Payment> payments = new ArrayList<Payment>();
			payment = new Payment();
			payment.SUM = item1.sum() + item2.sum();
			payment.TYPE = Payment.PAYMENT_TYPE_CASH; // Оплата наличными
			payments.add(payment);
			order.setPaymentsDetails(KKMInfo.SNO_BF_COMMON, payments, casier);
			Core.getInstance().doPayment(this, MSG_SELL_ORDER_DONE,order);
			break;
		case R.id.fn_report:
			Core.getInstance().requestFReport(this, MSG_FISCAL_REPORT_DONE);
			break;
		}
		
	}

	private void updateKKMInfo() {
		TextView fnState = findViewById(R.id.v_fn_state);
		String s= _info.DeviceSerial();
		((TextView) findViewById(R.id.v_kkt_serial)).setText(s);
		_wd.setEnabled(false);
		_cr.setEnabled(false);
		_sale.setEnabled(false);
		_report.setEnabled(false);
		_docs.setEnabled(false);
		_rests.setEnabled(false);
		if (_info.isFNAvailable()) { // Подключен ли ФН
			((TextView) findViewById(R.id.v_fn_serial)).setText(_info.FNSerial());
			_docs.setEnabled(true);
			if (_info.isFNClosed()) {// состояния ФН
				fnState.setText("Архивный");
				
			} else if (_info.isFNReady()) {
				fnState.setText("Фискализирован");
				_rests.setEnabled(true);
				_wd.setEnabled(true);
				_report.setEnabled(true);
				_sale.setEnabled(_info.isWorkDayOpen());
				_cr.setEnabled(_info.isWorkDayOpen());
				if(_info.isWorkDayOpen()) 
					((TextView)_wd).setText("Закрыть смену");
				 else 
					((TextView)_wd).setText("Открыть смену");
			}
			else {
				fnState.setText("Не фискализирован");
			}
		} else
			fnState.setText("Недоступен");
	}
	@Override
	public boolean onMessage(Message msg) {
		switch(msg.what) {
		case AppCore.Const.MSG_KKMINFO_CHANGED:
			_info = (KKMInfo)msg.obj;
			updateKKMInfo();
			break;
		case MSG_CORRECTION_DONE:
		case MSG_FISCAL_REPORT_DONE:
		case MSG_SELL_ORDER_DONE:
		case MSG_SHIFT_ACTION_DONE:	
			if(msg.arg1 == Errors.NO_ERROR) {
				Document fd = (Document)msg.obj;
				Toast.makeText(this, "Операция выполнена, номер ФД "+fd.signature().number(),Toast.LENGTH_LONG).show();
			} else 
				Toast.makeText(this, "Операция выполнена с ошибкой\n"+Core.getError(msg.arg1),Toast.LENGTH_LONG).show();
			_info = Core.getInstance().getKKMINfo();
			updateKKMInfo();
			break;
			default:
				return false;
				
		}
		return true;
	}

}
