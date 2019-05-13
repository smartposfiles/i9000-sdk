package rs.fn;
import rs.fn.data.Document;
import rs.fn.data.OU;
import rs.fn.data.KKMInfo;
import rs.fn.data.WorkDay;
import rs.fn.data.FiscalReport;
import rs.fn.data.ArchiveReport;
import rs.fn.data.KKMSettings;
import rs.fn.data.VerificationInfo;
import rs.fn.data.Correction;
import rs.fn.data.SellOrder;

interface FiscalService {
	int initialize();
	int getKKMInfo(out KKMInfo info);
	int doRegistration(int reason, in KKMInfo info, out KKMInfo signed);
	void resetDocument(out KKMInfo info);
	int reset(out KKMInfo info);
	int openWorkDay(in OU casier, out WorkDay workDay);
	int closeWorkDay(in OU casier, out WorkDay workDay);
	int requestFiscalReport(out FiscalReport report);
	int closeFN(in OU casier, out ArchiveReport report, out KKMInfo info);
	int doCorrection( in Correction doc, out Correction signed);
	int doSellOrder( in SellOrder order, out SellOrder signed, boolean doPrint, String additional);
	KKMSettings getKKMSettings();
	void setKKMSettings(in KKMSettings settings);
	VerificationInfo getVerificationInfo();
	VerificationInfo compare(String path);
	int closeWorkDayImmediately(in KKMInfo info);
	int sendDocuments(out KKMInfo info);
	int changeDeviceSerial(String number, String check);
	int readKKMInfo(out KKMInfo info);
	int printXReport();
	void setDefaultFontSize(int size);
	void setPageSize(int chars);
	int getDefaultFontSize();
	int getPageSize();
	int finalizeSellOrder(in SellOrder order, out SellOrder signed);
	
	int openTransaction();
	int writeData(int transaction, in byte [] data);
	int readData(int transaction, out byte [] data);
	void closeTransaction(int transaction); 

	String getInternalVersion();
}