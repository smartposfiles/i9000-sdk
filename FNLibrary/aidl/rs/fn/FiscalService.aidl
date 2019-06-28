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
/*1*/	int initialize();
/*2*/	int getKKMInfo(out KKMInfo info);
/*3*/	int doRegistration(int reason, in KKMInfo info, out KKMInfo signed);
/*4*/	void resetDocument(out KKMInfo info);
/*5*/	int reset(out KKMInfo info);
/*6*/	int openWorkDay(in OU casier, out WorkDay workDay);
/*7*/	int closeWorkDay(in OU casier, out WorkDay workDay);
/*8*/	int requestFiscalReport(out FiscalReport report);
/*9*/	int closeFN(in OU casier, out ArchiveReport report, out KKMInfo info);
/*10*/	int doCorrection( in Correction doc, out Correction signed);
/*11*/	int doSellOrder( in SellOrder order, out SellOrder signed, boolean doPrint, String additional);
/*12*/	KKMSettings getKKMSettings();
/*13*/	void setKKMSettings(in KKMSettings settings);
/*14*/	VerificationInfo getVerificationInfo();
/*15*/	VerificationInfo compare(String path);
/*16*/	int closeWorkDayImmediately(in KKMInfo info);
/*17*/	int sendDocuments(out KKMInfo info);
/*18*/	int changeDeviceSerial(String number, String check);
/*19*/	int readKKMInfo(out KKMInfo info);
/*20*/	int printXReport();
/*21*/	void setDefaultFontSize(int size);
/*22*/	void setPageSize(int chars);
/*23*/	int getDefaultFontSize();
/*24*/	int getPageSize();
/*25*/	int finalizeSellOrder(in SellOrder order, out SellOrder signed);
/*26*/	int openTransaction();
/*27*/	int writeData(int transaction, in byte [] data);
/*28*/	int readData(int transaction, out byte [] data);
/*29*/	void closeTransaction(int transaction);
/*30*/	String getInternalVersion();

	int sendDocumentsInBackground(out KKMInfo info);
	void disablePrinting();
}