package rs.fnsample;

import android.content.Context;
import android.os.RemoteException;
import rs.fn.AppCore;
import rs.fn.data.Correction;
import rs.fn.data.FiscalReport;
import rs.fn.data.SellOrder;

public class Core extends AppCore {

	private static Core _instance;
	public static Core getInstance() { return _instance; }
	public Core() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate() {
		_instance = this;
		super.onCreate();
	}
	public void doCorrection(Context context, int messageId, Correction correction) {
		new FNOperaionTask(context,messageId) {
			private Correction _correction;
			@Override
			protected Integer doInBackground(Object... params) {
				_correction = (Correction)params[0];
				publishProgress("Выполняется операция коррекции...");
				try {
					return getFNService().doCorrection(_correction, _correction);
				} catch(RemoteException re) {
					return -5;
				}
			}
			protected Object getResultData() {
				return _correction;
			}; 
		}.execute(correction);
	}
	public void doPayment(Context context, int messageId, SellOrder order ) {
		new FNOperaionTask(context, messageId) {
			private SellOrder _order;
			protected Integer doInBackground(Object... params) {
				publishProgress("Проведение операции...");
				_order = (SellOrder) params[0];
				try {
					return getFNService().doSellOrder(_order, _order, true, null);
				} catch (RemoteException re) {
					return -5;
				}
			}

			protected Object getResultData() {
				return _order;
			}

		}.execute(order);

	}
	public void requestFReport(Context context, int messageId) {
		new FNOperaionTask(context, messageId) {
			private FiscalReport _rep = new FiscalReport();

			@Override
			protected Integer doInBackground(Object... arg0) {
				publishProgress("Формирование отчета о расчетах");

				try {
					return getFNService().requestFiscalReport(_rep);
				} catch (RemoteException re) {
					return -5;
				}
			}

			protected Object getResultData() {
				return _rep;
			}
		}.execute();
	}


}
