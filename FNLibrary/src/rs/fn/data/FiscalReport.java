package rs.fn.data;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * Отчет о расчетах
 * @author nick
 *
 */
public class FiscalReport extends Document {

	protected FNStatistics _statistic = new FNStatistics();
	protected boolean _isOffline;
	public FiscalReport() {
	}

	
	public FiscalReport(Parcel in) {
		super(in);
	}
	/**
	 * Информация о неотправленых документах
	 * @return
	 */
	public FNStatistics OFDStatistic() { return _statistic; }
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(_isOffline ? 1 :0);
		_statistic.writeToParcel(dest);
	}
	@Override
	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		_isOffline = in.readInt() != 0;
		_statistic.readFromParcel(in);
	}
	/**
	 * Признак автономной работы
	 * @return
	 */
	public boolean isOfflineMode() { return _isOffline; }
	/**
	 * Признак открытой смены
	 * @return
	 */
	public boolean isWDOpen() { return hasTag(1038); }
	/**
	 * Номер открытой смены
	 * @return
	 */
	public int workDayNumber() { 
		return hasTag(1038) ? get(1038).asInt() : 0;}
	
	public static final Parcelable.Creator<FiscalReport> CREATOR = new Parcelable.Creator<FiscalReport>() {

		@Override
		public FiscalReport createFromParcel(Parcel in) {
			FiscalReport result = new FiscalReport();
			result.readFromParcel(in);
			return result;
		}

		@Override
		public FiscalReport[] newArray(int size) {
			return new FiscalReport[size];
		}
		
	};
}
