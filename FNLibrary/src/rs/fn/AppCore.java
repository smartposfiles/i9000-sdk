package rs.fn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import rs.fn.Const.Errors;
import rs.fn.data.KKMInfo;
import rs.fn.data.OU;
import rs.fn.data.WorkDay;

/**
 * 
 * @author nick
 * Вспомогательный класс для работы с FNCore
 */
@SuppressWarnings("deprecation")
public class AppCore extends Application implements ServiceConnection {

	public static final class Const {
		private Const() {
		}

		public static final int MSG_KKMINFO_CHANGED = 1;
	}
	/**
	 * Интерфейс получателя локального сообщения
	 * @author nick
	 *
	 */
	public static interface LocalMessageReceiver {
		/**
		 * 
		 * @param msg передаваемое сообщение
		 * @return true если сообщение обработано, false если это "не ваше" сообщение
		 */
		public boolean onMessage(Message msg);
	}

	
	
	private static final SparseArray<String> ERRORS = new SparseArray<>();
	static {
		ERRORS.put(0, "Успешное выполнение команды");
		ERRORS.put(1, "Неизвестная команда или неверный формат");
		ERRORS.put(2, "Неверное состояние ФН");
		ERRORS.put(3, "Ошибка ФН");
		ERRORS.put(4, "Ошибка контрольной суммы");
		ERRORS.put(5, "Закончен срок эксплуатации ФН");
		ERRORS.put(6, "Архив переполнен");
		ERRORS.put(7, "Неверное дата/время");
		ERRORS.put(8, "Нет запрошенных данных");
		ERRORS.put(9, "Некорректное значение параметров команды");
		ERRORS.put(0x10, "Превышен размер TLV");
		ERRORS.put(0x11, "Нет соединния");
		ERRORS.put(0x12, "Исчерпан ресурс криптопроцессора");
		ERRORS.put(0x14, "Исчерпан ресурс хранения");
		ERRORS.put(0x15, "Превышено ожидание передачи сообщения");
		ERRORS.put(0x16, "Продолжительность смены больше 24 часов");
		ERRORS.put(0x17, "Неверная разница во времени между операциями");
		ERRORS.put(0x20, "Сообщение от ОФД не может быть принято");
		ERRORS.put(0xF1, "Устройство не найдено");
		ERRORS.put(0xF2, "Превышен интервал чтения");
		ERRORS.put(0xF3, "Операция прервана");
		ERRORS.put(0xF4, "Ошибка контрольной суммы");
		ERRORS.put(0xF5, "Ошибка записи");
		ERRORS.put(0xF6, "Ошибка чтения");
		ERRORS.put(0xF7, "Ошибка данных");
		ERRORS.put(0xF8, "Номер ФН не совпадает с последним использованным");
		ERRORS.put(0xF9, "Системная ошибка");
		ERRORS.put(0xFA, "В чеке может быть только одна оплата кредита");
		ERRORS.put(0xFB, "Нет соединения с FNCore");
		ERRORS.put(0xFD, "Имеются неотправленные документы");
		
		
	}

	private static AppCore _instance;
	/**
	 * Получить экземпляр синглтона класса
	 * @return
	 */
	public static AppCore getInstance() { return _instance; }
	
	private Set<LocalMessageReceiver> LOCAL_RECEIVERS = Collections
			.synchronizedSet(new HashSet<LocalMessageReceiver>());
	private FiscalService _service;
	/**
	 * 
	 * @return Сервис фискального накопителя
	 */
	protected FiscalService getFNService() { return _service; }
	protected KKMInfo _info = new KKMInfo();
	private Handler _handler;
	private BroadcastReceiver FN_DONE_ACTION_RECEIVER = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			try {
				Log.d("FNC", "Signal received");
				int result = _service.getKKMInfo(_info);
				
				broadcastLocalMessage(Const.MSG_KKMINFO_CHANGED,result,_info);
			} catch(RemoteException re) {
				unbindService(AppCore.this);
				broadcastLocalMessage(Const.MSG_KKMINFO_CHANGED,-5,_info);
			}
			
		}
	};

	/**
	 * Базовый класс операции выполняющийся асинхронно с поддержкой диалога выполнения
	 * @author nick
	 *
	 */
	public static abstract class TaskWithDialog extends AsyncTask<Object, Object, Integer> {
		private Context _context;
		private ProgressDialog _dialog;
		public  TaskWithDialog(Context context) {
			_context = context;
		}
		@Override
		protected void onProgressUpdate(Object... values) {
			if(_dialog == null) {
				_dialog = new ProgressDialog(_context);
				_dialog.setCancelable(false);
				_dialog.setCanceledOnTouchOutside(false);
				_dialog.setIndeterminate(true);
				_dialog.show();
			}
			if(values.length > 0 && values[0] instanceof String)
				_dialog.setMessage(values[0].toString());
		}
		@Override
		protected void onPostExecute(Integer result) {
			if(_dialog != null)
				_dialog.dismiss();
		}
		
	}
	/**
	 * Базовый класс операции с ФН. По выполнению операции отправляет внутреннее сообщение всем слушателям
	 * @author nick
	 *
	 */
	protected abstract class FNOperaionTask extends TaskWithDialog {
		protected int _message;

		/**
		 * 
		 * @param context - контекст
		 * @param message - код сообщения, отправляемый слушателям
		 */
		public FNOperaionTask(Context context, int message) {
			super(context);
			_message = message;
		}
		/**
		 * Данные, которые будут переданы в сообщении
		 * @return
		 */
		protected Object getResultData() { return null; }
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			broadcastLocalMessage(_message, result.intValue(),getResultData());
		}
	}
	
	public AppCore() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate() {
		_instance = this;
		super.onCreate();
		_handler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				synchronized (LOCAL_RECEIVERS) {
					for (LocalMessageReceiver receiver : LOCAL_RECEIVERS)
						if (receiver.onMessage(msg))
							break;
				}
			}
		};
	}
	/**
	 * Инициализация службы фискального накопителя. 
	 * После соединения отправляется сообщение всем слушателям Const.MSG_KKMINFO_CHANGED
	 * @return true если соединение со службой ФН установлено
	 */
	public boolean init() {
		if(_service == null) {
			Log.d("FNC", "Start service");
			if (startService(rs.fn.Const.FN_SERVICE) != null) {
				Log.d("FNC", "Connect to service");
				return bindService(rs.fn.Const.FN_SERVICE, this, BIND_AUTO_CREATE);
			}
			return false;
		}
		if(_info != null) 
			broadcastLocalMessage(Const.MSG_KKMINFO_CHANGED,Errors.NO_ERROR,_info);
		return true;
	}
	/**
	 * Отключение от службы ФН
	 */
	public void done() {
		if(_service != null)
			unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder binder) {
		_service = FiscalService.Stub.asInterface(binder);
		registerReceiver(FN_DONE_ACTION_RECEIVER, new IntentFilter(rs.fn.Const.FN_INIT_DONE_ACTION));
		try {
			int result = _service.getKKMInfo(_info); 
			if ( result != Errors.INITIALIZATION_IN_PROGRESS) {
				broadcastLocalMessage(Const.MSG_KKMINFO_CHANGED,result,_info);
			} 
		} catch (RemoteException re) {
			unbindService(this);
		}

	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		_service = null;
	}
	/**
	 * Зарегистрировать получатель локальных сообщений
	 * @param receiver
	 */
	public void registerLocalReceiver(LocalMessageReceiver receiver) {
		LOCAL_RECEIVERS.add(receiver);
	}
	/**
	 * Отменить регистрацию получателя локальных сообщений
	 * @param receiver
	 */
	public void unregisterLocalReceiver(LocalMessageReceiver receiver) {
		LOCAL_RECEIVERS.remove(receiver);
	}
	/**
	 * Отправить локальное сообщение
	 * @param message идентификатор сообщения
	 */
	public void broadcastLocalMessage(int message) {
		broadcastLocalMessage(message, 0, null);
	}
	/**
	 * Отправить локальное сообщение
	 * @param message идентификатор сообщения
	 * @param data данные
	 */
	public void broadcastLocalMessage(int message, Object data) {
		broadcastLocalMessage(message, 0, data);
	}
	/**
	 * Отправить локальное сообщение
	 * @param message идентификатор сообщения
	 * @param result код arg1
	 * @param data данные
	 */
	public void broadcastLocalMessage(int message, int result, Object data) {
		Message msg = _handler.obtainMessage(message);
		msg.obj = data;
		msg.arg1 = result;
		_handler.sendMessage(msg);
	}
	/**
	 * Выполнить регистрацию/перерегистрацию ФН
	 * @param context
	 * @param messageId
	 * @param reason
	 * @param info заполненные данные для регистрации
	 */
	public void doRegistration(Context context, int messageId, final int reason, final KKMInfo info) {
		if (_service == null)
			broadcastLocalMessage(messageId, -5, null);
		else
			new FNOperaionTask(context, messageId) {
				@Override
				protected Integer doInBackground(Object... params) {
					publishProgress("Регистрация ККМ...");
					
					try {
						_service.resetDocument(_info);
						if(_service.getKKMInfo(_info) == Errors.SETTINGS_LOST) {
							_service.closeWorkDayImmediately(_info);
						}
						return _service.doRegistration(reason,info,_info);
					} catch (RemoteException re) {
						unbindService(AppCore.this);
						return -5;
					}
				}
			}.execute();
	}
	/**
	 * Открыть смену
	 * @param context
	 * @param messageId
	 */
	public void openShift(Context context, int messageId) {
		openShift(context, messageId,new OU());
	}
	/**
	 * Открыть смену с указанием кассира
	 * @param context
	 * @param messageId
	 * @param casier
	 */
	public void openShift(Context context, int messageId, OU casier) {
		if (_service == null)
			broadcastLocalMessage(messageId, -5, null);
		else
			new FNOperaionTask(context, messageId) {
				private WorkDay wd = new WorkDay();
				@Override
				protected Integer doInBackground(Object... params) {
					publishProgress("Открытие смены...");
					try {
						int r = _service.openWorkDay((OU)params[0], wd);
						if(r == Errors.NO_ERROR)
							_service.getKKMInfo(_info);
						return r;
					} catch (RemoteException re) {
						unbindService(AppCore.this);
						return -5;
					}
				}
				protected Object getResultData() {return wd; }
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					if(result.intValue() == Errors.NO_ERROR)
						broadcastLocalMessage(Const.MSG_KKMINFO_CHANGED,result,_info);
				};

			}.execute(casier);
	}
	/**
	 * Отправить документы в ОФД
	 * @param messageId
	 */
	public void sendDocuments(final int messageId) {
		if (_service == null)
			broadcastLocalMessage(messageId, -5, null);
		try {
			_service.sendDocuments(_info);
		} catch (RemoteException re) {
			unbindService(AppCore.this);
		}
	}
	/**
	 * Закрыть смену
	 * @param context
	 * @param messageId
	 */
	public void closeShift(Context context, int messageId) {
		closeShift(context, messageId, new OU());
	}
	/**
	 * Закрыть смену с указанием кассира
	 * @param context
	 * @param messageId
	 * @param casier
	 */
	public void closeShift(Context context, int messageId, OU casier) {
		if (_service == null)
			broadcastLocalMessage(messageId, -5, null);
		else
			new FNOperaionTask(context, messageId) {
				private WorkDay wd = new WorkDay();
				protected Object getResultData() {return wd; }
				@Override
				protected Integer doInBackground(Object... params) {
					publishProgress("Закрытие смены...");
					try {
						
						int r = _service.closeWorkDay((OU)params[0], wd);
						if(r == Errors.NO_ERROR)
							_service.getKKMInfo(_info);
						return r;
					} catch (RemoteException re) {
						unbindService(AppCore.this);
						return -5;
					}
				}

				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					if(result.intValue() == Errors.NO_ERROR)
						broadcastLocalMessage(Const.MSG_KKMINFO_CHANGED,result,_info);
				};

			}.execute(casier);

	}
	/**
	 * Сбросить фискальный накопитель
	 * @param context
	 * @param messageId
	 */
	public void resetFN(Context context, final int messageId) {
		if (_service == null)
			broadcastLocalMessage(messageId, -5, null);
		else
			new TaskWithDialog(context) {
				@Override
				protected Integer doInBackground(Object... arg0) {
					publishProgress("Сброс ФН...");
					try {
						_service.reset(_info);
					} catch(RemoteException re) {
						
					}
					return null;
				}
				@Override
				protected void onPostExecute(Integer arg0) {
					super.onPostExecute(arg0);
					broadcastLocalMessage(messageId, Errors.NEW_FN,_info);
					broadcastLocalMessage(Const.MSG_KKMINFO_CHANGED,Errors.NEW_FN,_info);
				}
			}.execute();
		
	}
	/**
	 * Получить информацию о ККМ
	 * @return
	 */
	public KKMInfo getKKMINfo() {
		try {
			_service.getKKMInfo(_info);
		} catch(RemoteException re ) { }
		return _info;
	}
	/**
	 * Получить текст ошибки
	 * @param error
	 * @return
	 */
	public static String getError(int error) {
		return ERRORS.get(error);
	}
}
