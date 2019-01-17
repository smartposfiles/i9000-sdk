package rs.fn.data;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import rs.fn.Const;

/**
 * 
 * @author nick Информация о регистрации ККМ
 */
public class KKMInfo extends Document {

	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat DF = new SimpleDateFormat("dd/mm/yyy HH:MM");
//	private static Pattern EMAIL_CHECKER = Pattern.compile(
//			"(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");

	public static final String KKM_VERSION = "1.23";

	private static final int ENCRYPTION_MODE = 1;
	private static final int OFFLINE_MODE    = 2;
	private static final int AUTO_MODE       = 4;
	private static final int SERVICE_MODE    = 8;
	private static final int INTERNET_MODE   = 0x20;
	private static final int BSO_MODE        = 0x10;

	/**
	 * Причина регистрации - регистрация ФН
	 */
	public static final int REASON_REGISTER = 0;
	/**
	 * Причина регистрации - замена ФН
	 */
	public static final int REASON_REPLACE_FN = 1;
	/**
	 * Причина регистрации - замена ОФД
	 */
	public static final int REASON_CHANGE_OFD = 2;
	/**
	 * Причина регистрации - изменение реквизитов
	 */
	public static final int REASON_CHANGE_SETTINGS = 3;
	/**
	 * Причина регистрации - Изменение настроек ККТ
	 */
	public static final int REASON_CHANGE_KKT_SETTINGS = 4;
	/**
	 * Причина регистрации - восстановление данных (внутренее использование)
	 */
	public static final int REASON_RESTORE_DATA = 5;

	/**
	 * СНО Общая (битовый флаг)
	 */
	public static final int SNO_BF_COMMON = 0;
	/**
	 * СНО Упрощенная доход (битовый флаг)
	 */
	public static final int SNO_BF_SIMPLE_INCOME = 1;
	/**
	 * СНО Упрощенная доход-расход (битовый флаг)
	 */
	public static final int SNO_BF_SIMPLE_INCOME_EXPENSE = 2;
	/**
	 * СНО Единый налог на вменяемый доход (битовый флаг)
	 */
	public static final int SNO_BF_UNITED_TAX = 3;
	/**
	 * СНО Единый сельскохозяйственный налог (битовый флаг)
	 */
	public static final int SNO_BF_UNITED_AGRO_TAX = 4;
	/**
	 * СНО Патентная система налогообложения (битовый флаг)
	 */
	public static final int SNO_BF_PATENT = 5;

	/**
	 * банковский агент (битовый флаг)
	 */
	public static final int AGENT_BF_BANK_AGENT = 0;
	/**
	 * банковский субагент (битовый флаг)
	 */
	public static final int AGENT_BF_BANK_SUBAGENT = 1;
	/**
	 * платежный агент (битовый флаг)
	 */
	public static final int AGENT_BF_PAYMENT_AGENT = 2;
	/**
	 * платежный субагент (битовый флаг)
	 */
	public static final int AGENT_BF_PAYMENT_SUBAGENT = 3;
	/**
	 * поверенный (битовый флаг)
	 */
	public static final int AGENT_BF_ATTORNEY = 4;
	/**
	 * комиссионер (битовый флаг)
	 */
	public static final int AGENT_BF_COMISSIONARE = 5;
	/**
	 * другой тип агента (битовый флаг)
	 */
	public static final int AGENT_BF_AGENT_NOT_OTHER = 6;

	/**
	 * Маска агента
	 */
	public static final int IS_AGENT = 0x4 | 0x8 | 0x40;
	/**
	 * Маска банковского агента
	 */
	public static final int IS_BANK_AGENT = 0x3;

	protected int _modes;
	protected int _taxModes;
	protected byte _fnState;
	protected byte _fnWarnings;
	protected byte _fnUnsavedDocument;
	protected boolean _isWDOpen;
	protected int  _wdNumber;
	protected long _wdWhenOpen;
	protected int _dCount = 0, _cCount = 0;
	protected long _lastDocumentDate;
	protected int _lastDocumentNumber;
	protected int _automateNumber;
	

	protected String _fnSerial;


	private OU _casier = new OU();
	private OU _owner = new OU();
	private OU _ofd = new OU();

	private FNStatistics _statistic = new FNStatistics(); 
	
	private String _regNo = Const.EMPTY_STRING;
	private int _snoModes;

	
	
	public KKMInfo() {
		super();
		add(1193, false);
		add(1126, false);
		add(1207, false);
		add(1013, Const.EMPTY_STRING);
		add(1209, (byte) 2);
		add(1189, (byte) 2);
		add(1188, KKM_VERSION);
		add(1117, Const.EMPTY_STRING);
		add(1057, (byte) 0);
		add(1060,"www.nalog.ru");
	}

	public KKMInfo(Parcel in) {
		super(in);
	}
	/**
	 * Серийный номер ФН
	 * @return
	 */
	public String FNSerial() {
		return _fnSerial;
	}

	public void clear() {
		add(1193, false);
		add(1126, false);
		add(1207, false);
		add(1013, Const.EMPTY_STRING);
		add(1209, (byte) 2);
		add(1189, (byte) 2);
		add(1188, KKM_VERSION);
		add(1117, Const.EMPTY_STRING);
		add(1057, (byte) 0);
		add(1060,"www.nalog.ru");
		_modes = 0;
		_automateNumber = 0;
		_isWDOpen = false;
		_wdNumber = _lastDocumentNumber = 0;
		_fnState = 0;
		_snoModes = 0;
	}
	
	/**
	 * Серийный номер ККТ
	 * @return
	 */
	public String DeviceSerial() {
		return get(1013).asString();
	}

	/**
	 * Реквизиты кассира
	 * @return
	 */
	public OU casier() {
		return _casier;
	}

	/**
	 * Реквизиты владельца ККТ
	 * @return
	 */
	public OU owner() {
		return _owner;
	}

	/**
	 * Реквизиты ОФД
	 * @return
	 */
	public OU ofd() {
		return _ofd;
	}

	/**
	 * Регистрационный номер ККТ
	 * @return
	 */
	public String getRegistrationNo() {
		return _regNo;
	}

	/**
	 * Установить регистрационный номер ККТ
	 * @param value
	 */
	public void setRegistrationNo(String value) {
		_regNo = value;
	}

	/**
	 * Получить признак "автономная работа"
	 * @return
	 */
	public boolean isOfflineMode() {
		return (_modes & OFFLINE_MODE) == OFFLINE_MODE;
	}
	/**
	 * Установить признак "автономная работа"
	 * @param val
	 */
	public void setOfflineMode(boolean val) {
		if (val)
			_modes |= OFFLINE_MODE;
		else
			_modes &= ~OFFLINE_MODE;
	}
	/**
	 * Получить признак "ККТ для Интернет"
	 * @return
	 */
	public boolean isInternetMode() {
		return (_modes & INTERNET_MODE) == INTERNET_MODE;
	}
	/**
	 * Установить признак "ККТ для Интернет"
	 * @param val
	 */
	public void setInternetMode(boolean val) {
		if (val)
			_modes |= INTERNET_MODE;
		else
			_modes &= ~INTERNET_MODE;
	}

	/**
	 * Получить признак "режим шифрования"
	 * @return
	 */
	public boolean isEncryptionMode() {
		return (_modes & ENCRYPTION_MODE) == ENCRYPTION_MODE;
	}
	/**
	 * Установить признак "режим шифрования"
	 * @param val
	 */
	public void setEncryptionMode(boolean val) {
		if (val)
			_modes |= ENCRYPTION_MODE;
		else
			_modes &= ~ENCRYPTION_MODE;
	}
	/**
	 * Получить признак "Продажа подакцизного товара"
	 * @return
	 */
	public boolean isExcisesMode() {
		return get(1207).asBoolean();
	}
	/**
	 * Установить признак "Продажа подакцизного товара" 
	 * @param val
	 */
	public void setExcisesMode(boolean val) {
		add(1207, val);
	}
	/**
	 * Получить признак "Оказание услуг"
	 * @return
	 */
	public boolean isServiceMode() {
		return (_modes & SERVICE_MODE) == SERVICE_MODE;
	}
	/**
	 * Установить признак "Оказание услуг"
	 * @param val
	 */
	public void setServiceMode(boolean val) {
		if (val)
			_modes |= SERVICE_MODE;
		else
			_modes &= ~SERVICE_MODE;
	}
	/**
	 * Получить признак "Использование БСО"
	 * @return
	 */
	public boolean isBSOMode() {
		return (_modes & BSO_MODE) == BSO_MODE;
	}
	/**
	 * Установить признак "Использование БСО"
	 * @param val
	 */
	public void setBSOMode(boolean val) {
		if (val)
			_modes |= BSO_MODE;
		else
			_modes &= ~BSO_MODE;
	}
	/**
	 * Получить признак "Проведение лотереи"
	 * @return
	 */
	public boolean isLotteryMode() {
		return get(1126).asBoolean();
	}

	/**
	 * Установить признак "Проведение лотереи"
	 * @param val
	 */
	public void setLotteryMode(boolean val) {
		add(1126, val);
	}
	/**
	 * Получить признак "Проведение азартных игр"
	 * @return
	 */
	public boolean isCasinoMode() {
		return get(1193).asBoolean();
	}
	/**
	 * Установить признак "Проведение азартных игр" 
	 * @param val
	 */
	public void setCasinoMode(boolean val) {
		add(1193, val);
	}
	/**
	 * Получить признак "Установлен в автомате"
	 * @return
	 */
	public boolean isAutomaticMode() {
		return (_modes & AUTO_MODE) == AUTO_MODE;
	}
	/**
	 * Установить признак "Установлен в автомате"
	 * @param val
	 */
	public void setAutomaticMode(boolean val) {
		if (val) 
			_modes |= AUTO_MODE;
		else 
			_modes &= ~AUTO_MODE;
		
	}
	/**
	 * Получить номер автомата
	 * @return
	 */
	public int getAutomateNumber() {
		return _automateNumber;
	}
	/**
	 * Установить номер автомата
	 * @param value
	 */
	public void setAutomateNumber(int value) {
		_automateNumber = value;
	}
	/**
	 * Получить версию протокола ФН/ОФД
	 * @return 1 - 1.00 2 - 1.05 3 - 1.10
	 */
	public byte getProtocolVersion() {
		if(hasTag(1209))
			return get(1209).asByte();
		return get(1189).asByte();
	}
	/**
	 * Установить версию протокола ФН/ОФД
	 * @param val
	 */
	public void setProtocolVersion(int val) {
		add(1209, (byte) (val & 0xFF));
		add(1189, (byte) (val & 0xFF));
	}
	/**
	 * Получить e-mail отправителя
	 * @return
	 */
	public String getSenderEmail() {
		return get(1117).asString();
	}
	/**
	 * Установить e-mail отправителя
	 * @param value
	 */
	public void setSenderEmail(String value) {
		add(1117, value);
	}

	/**
	 * Получить биты агентских услуг (см AGENT_BF_xxx)
	 * @return
	 */
	public int getAgentTypes() {
		return hasTag(1057) ? get(1057).asByte() : 0;
	}

	/**
	 * Установить биты агентских услуг (см AGENT_BF_xxx)
	 * @param value
	 */
	public void setAgentTypes(int value) {
		if (value != 0)
			add(1057, (byte) (value & 0xFF));
		else
			remove(1057);
	}

	/**
	 * ФН присутствует и доступен для работы
	 * 
	 * @return
	 */
	public boolean isFNAvailable() {
		return _fnState != 0;
	}

	/**
	 * ФН Фискализирован
	 * 
	 * @return
	 */
	public boolean isFNReady() {
		return (_fnState & 0x3) == 0x3;
	}

	/**
	 * ФН закрыт
	 * 
	 * @return
	 */
	public boolean isFNClosed() {
		return (_fnState & 4) == 0x4;
	}

	/**
	 * ФН архивный
	 * 
	 * @return
	 */
	public boolean isFNArchived() {
		return (_fnState & 8) == 0x8;
	}

	/**
	 * Предупреждения ФН
	 * 
	 * @return
	 */
	public int getFNWarnings() {
		return _fnWarnings;
	}
	/**
	 * Есть незавершенные документы
	 * @return
	 */
	public boolean isFNHasUnsavedDocument() { return _fnUnsavedDocument != 0; }
	/**
	 * Получить тип незавершенного документа
	 * @return
	 */
	public int getUnsavedDocumentType() { return _fnUnsavedDocument; }
	/**
	 * Признак открытой смены	
	 * @return
	 */
	public boolean isWorkDayOpen() { return _isWDOpen; }
	/**
	 * Номер открытой(последней) смены
	 * @return
	 */
	public int workDayNumber() { return _wdNumber; }
	/**
	 * Дата открытия последней смены
	 * @return
	 */
	public long whenWorkdayisOpen() { return _wdWhenOpen; }
	/**
	 * Количество чеков за смену
	 * @return
	 */
	public int getChecksCount() { return _cCount; }
	/**
	 * Количество фискальных документов за смену
	 * @return
	 */
	public int getDocumentsCount() { return _dCount; }
	/**
	 * Дата последнего фискализированного документа
	 * @return
	 */
	public long lastDocumentDate() { return _lastDocumentDate; }
	/**
	 * Номер последнего фискализированного документа
	 * @return
	 */
	public int lastDocumentNumber() { return _lastDocumentNumber; }
	/**
	 * Получить адрес сайта ФНС
	 * @return
	 */
	public String getFNSUrl() { return get(1060).asString(); }
	/**
	 * Установить адрес сайта ФНС
	 * @param value
	 */
	public void setFNSUrl(String value) {
		add(1060,value);
	}
	/**
	 * Получить битовые флаги СНО (см SNO_BF_xxx)
	 * @return
	 */
	public byte getTaxModes() { return (byte)(_taxModes & 0xFF); }
	/**
	 * Установить битовые флаги СНО (см SNO_BF_xxx)
	 * @param value
	 */
	public void setTaxModes(int value) { _taxModes = value; }
	
	/**
	 * Информация о неотправленных документах 
	 * @return
	 */
	public FNStatistics OFDStatistic() {
		return _statistic; 
	}
	
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(_modes);
		dest.writeByte(_fnState);
		dest.writeByte(_fnWarnings);
		dest.writeString(_fnSerial);
		_statistic.writeToParcel(dest);
		_casier.writeToParcel(dest, flags);
		_owner.writeToParcel(dest, flags);
		_ofd.writeToParcel(dest, flags);
		dest.writeString(_regNo);
		dest.writeInt(_snoModes);
		dest.writeByte(_fnUnsavedDocument);
		dest.writeInt(_isWDOpen ? 1 : 0);
		dest.writeInt(_wdNumber);
		dest.writeLong(_lastDocumentDate);
		dest.writeInt(_lastDocumentNumber);
		dest.writeInt(_taxModes);
		dest.writeInt(_automateNumber);
		dest.writeLong(_wdWhenOpen);
		dest.writeInt(_cCount);
		dest.writeInt(_dCount);
	}

	@Override
	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		_modes = (in.readInt() & 0xFF);
		_fnState = in.readByte();
		_fnWarnings = in.readByte();
		_fnSerial = in.readString();
		_statistic.readFromParcel(in);
		_casier.readFromParcel(in);
		_owner.readFromParcel(in);
		_ofd.readFromParcel(in);
		_regNo = in.readString();
		_snoModes = in.readInt();
		_fnUnsavedDocument = in.readByte();
		_isWDOpen = in.readInt() != 0;
		_wdNumber = in.readInt();
		_lastDocumentDate = in.readLong();
		_lastDocumentNumber = in.readInt();
		_taxModes = in.readInt();
		_automateNumber = in.readInt();
		_wdWhenOpen = in.readLong();
		_cCount = in.readInt();
		_dCount = in.readInt();
	}

	
	public static final Parcelable.Creator<KKMInfo> CREATOR = new Parcelable.Creator<KKMInfo>() {
		@Override
		public KKMInfo createFromParcel(Parcel in) {
			KKMInfo result = new KKMInfo();
			result.readFromParcel(in);
			return result;
		}

		@Override
		public KKMInfo[] newArray(int size) {
			return new KKMInfo[size];
		}
		
	};
	/**
	 * Версия ПО ККТ
	 * @return
	 */
	public String getVersion() {
		return KKM_VERSION;
	}
	
	
}


