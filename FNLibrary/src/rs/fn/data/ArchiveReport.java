package rs.fn.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Отчет о закрытии фискального режима
 * @author nick
 *
 */
public class ArchiveReport extends Document {

	protected OU _casier = new OU();
	protected boolean _autoMode;
	protected int _automateNumber;
	public ArchiveReport() {
	}

	public ArchiveReport(Parcel in) {
		super(in);
	}
	/**
	 * Реквизиты кассира
	 * @return
	 */
	public OU casier() { return _casier; }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(_autoMode ? 1 :0);
		dest.writeInt(_automateNumber);
		_casier.writeToParcel(dest, flags);
	}
	@Override
	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);
		_autoMode = in.readInt() != 0;
		_automateNumber = in.readInt();
		_casier.readFromParcel(in);
	}
	/**
	 * Признак "установлен в автомате"
	 * @return
	 */
	public boolean isAutoMode() { return _autoMode; }
	/**
	 * Номер автомата
	 * @return
	 */
	public int automateNumer() { return _automateNumber; }
	
	public static Parcelable.Creator<ArchiveReport> CREATOR = new Parcelable.Creator<ArchiveReport>() {

		@Override
		public ArchiveReport createFromParcel(Parcel in) {
			ArchiveReport result = new ArchiveReport();
			result.readFromParcel(in);
			return result;
		}

		@Override
		public ArchiveReport[] newArray(int size) {
			return new ArchiveReport[size];
		}
		
	};

}
