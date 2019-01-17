package rs.fn;

import java.nio.charset.Charset;

import android.content.Intent;

public class Const {
	/**
	 * Коды ошибок ФН
	 * @author nick
	 *
	 */
	public static class Errors {
		public static final int INITIALIZATION_IN_PROGRESS = -1;
		public static final int NO_ERROR = 0;
		public static final int SUM_MISMATCH = 0xD0;
		public static final int INVALID_WORKDAY_STATE = 0xF0;
		public static final int DEVICE_ABSEND = 0xF1;
		public static final int READ_TIMEOUT = 0xF2;
		public static final int OPERATION_ABORTED = 0xF3;
		public static final int CRC_ERROR = 0xF4;
		public static final int WRITE_ERROR = 0xF5;
		public static final int READ_ERROR = 0xF6;
		public static final int DATA_ERROR = 0xF7;
		public static final int FN_MISMATCH = 0xF8;
		public static final int SYSTEM_ERROR = 0xF9;
		public static final int INVALID_CHECK_ITEMS  =0xFA;
		public static final int DATE_MISMATCH  = 0xFC;
		public static final int HAS_UNSENT_DOCS = 0xFD;
		public static final int SETTINGS_LOST = 0xFE;
		public static final int NEW_FN = 0xCA;
		public static final int OLD_FN_HAS_DATA = 0xCB;
		public static final int FN_REPLACEMENT = 0xCD;
		public static final int NO_CASH = 0xCE;

		private Errors() {
		}
	}
	public static class DocTypes {
		public static final int DOC_TYPE_REGISTRATION = 1;
		public static final int DOC_TYPE_REPORT = 2;
		public static final int DOC_TYPE_OPENWD = 3;
		public static final int DOC_TYPE_CLOSEWD = 4;
		public static final int DOC_TYPE_CLOSEFN = 5;
		public static final int DOC_TYPE_CORRECTION = 6;
		public static final int DOC_TYPE_ORDER = 7;
		private DocTypes() { }
	}
	/**
	 * Константа - пустая строка
	 */
	public static final String EMPTY_STRING = "";
	/**
	 * Пустой (отсутствующий) ИНН
	 */
	public static final String EMPTY_INN = "0000000000";
	
	/**
	 * Полином для расчета контрольных сумм  
	 */
	public static final short CCITT = (short) 0x1021;
	/**
	 * Кодовая страница строк используемая на ФН
	 */
	public static final Charset ENCODING = Charset.forName("CP866");
	
	private Const() { }
	/**
	 * Интент для соединения с сервисом ФН
	 */
	public static final Intent FN_SERVICE = new Intent("rs.fn.service");
	public static final String FN_INIT_DONE_ACTION = "rs.fncore.initialized";
	public static final String FN_FISCALIZE_ACTION = "rs.fnintf.fiscalize";
	
	static {
		FN_SERVICE.setPackage("rs.fncore");
	}
}
