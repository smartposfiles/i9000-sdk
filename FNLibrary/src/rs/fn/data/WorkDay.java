package rs.fn.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Состояние смены
 * @author nick
 *
 */
public class WorkDay extends Document {

	protected boolean _state;
	protected int _number;
	protected int _documentsCount;
	protected int _checksCount;
	protected boolean _isAutoMode;
	protected int _automateNumber;
	protected OU _casier = new OU();
	protected FNStatistics _statistic = new FNStatistics();
	
	public WorkDay() {
		super();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(_state ? 1 : 0);
		dest.writeInt(_number);
		dest.writeInt(_documentsCount);
		dest.writeInt(_checksCount);
		dest.writeInt(_isAutoMode ? 1 :0 );
		dest.writeInt(_automateNumber);
		_statistic.writeToParcel(dest);
		_casier.writeToParcel(dest, flags);
	}
	@Override
	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		_state = in.readInt() != 0;
		_number = in.readInt();
		_documentsCount = in.readInt();
		_checksCount = in.readInt();
		_isAutoMode = in.readInt() != 0;
		_automateNumber = in.readInt();
		_statistic.readFromParcel(in);
		_casier.readFromParcel(in);
	}
	
	/**
	 * Признак "установлен в автомате"
	 * @return
	 */
	public boolean isAutoMode() { return _isAutoMode; }
	/**
	 * Номер автомата
	 * @return
	 */
	public int automateNumber() { return _automateNumber; }
	/**
	 * Количество документов
	 * @return
	 */
	public int documentCount() {
		return _documentsCount;
	}
	/**
	 * Количество  чеков за смену
	 * @return
	 */
	public int checksCount() { 
		return _checksCount;
	}
	public WorkDay(Parcel in) {
		super(in);
	}
	/**
	 * Признак открытой смены
	 * @return
	 */
	public boolean isOpen() { return _state; }
	/**
	 * Данные о неотправленных документах
	 * @return
	 */
	public FNStatistics OFDStatistic() { return _statistic; }
	/**
	 * Номер смены 
	 * @return
	 */
	public int number() { return _number; }
	/**
	 * Реквизиты кассира
	 * @return
	 */
	public OU casier() { return _casier; }
	
	public static final Parcelable.Creator<WorkDay> CREATOR = new Parcelable.Creator<WorkDay>() {

		@Override
		public WorkDay createFromParcel(Parcel in) {
			WorkDay result = new WorkDay();
			result.readFromParcel(in);
			return result;
		}

		@Override
		public WorkDay[] newArray(int size) {
			return new WorkDay[size];
		}
		
	};
	

}
