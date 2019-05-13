package rs.fn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.SparseArray;
import android.view.WindowManager;
import rs.fn.Const.DocTypes;
import rs.fn.data.ArchiveReport;
import rs.fn.data.Correction;
import rs.fn.data.Document;
import rs.fn.data.FiscalReport;
import rs.fn.data.KKMInfo;
import rs.fn.data.Payment;
import rs.fn.data.SellItem;
import rs.fn.data.SellOrder;
import rs.fn.data.Tag;
import rs.fn.data.WorkDay;
import rs.fnlibrary.R;

/**
 * Генератор встроенных отчетов
 * 
 * @author nick
 *
 */
@SuppressLint("SimpleDateFormat")
public class Reports {

	private static Pattern BARCODE = Pattern.compile("<barcode (.+)>(.+)</barcode>");

	
	private static int printText(String text, IPrintManager pm,int y) {
		String[] bb = text.split("\n");
		for (String s : bb) {
			if (s.startsWith("<barcode")) {
				Matcher m = BARCODE.matcher(s);
				if (m.find())
					y += pm.printLinear(m.group(2), 4, y, m.group(1));
				else
					y += pm.drawText(s, 0, y);
			} else
				y += pm.drawText(s, 0, y);
		}
		return y;
		
	}
	public static int printText(String text, IPrintManager pm) {
		int oResult = pm.open();
		if (oResult == 0) {
			printText(text,pm,0);
			pm.printPage();
			pm.close();
		}
		return oResult;
	}

	private static int printOFDReport(byte[] ofd, int y, IPrintManager pm) {
		if (ofd != null && ofd.length > 0) {
			y += pm.drawText("Ответ оператора ОФД", 0, y);
			String s = Utils.dump(ofd);
			while (s.length() > Utils.PAGE_WIDTH / 12) {
				int l = s.length() / 2;
				if (l % 2 != 0)
					l--;
				y += pm.drawText(s.substring(0, l), 0, y);
				s = s.substring(l);
			}
			if (!s.isEmpty())
				y += pm.drawText(s, 0, y);
		}
		return y;

	}

	private static void askRepeat(final Context ctx, final Document doc, final IPrintManager pm, final int type,
			final int oResult) {
		askRepeat(ctx, doc, pm, type, oResult, null);
	}

	private static void askRepeat(final Context ctx, final Document doc, final IPrintManager pm, final int type,
			final int oResult, final String footer) {
		new Handler(ctx.getMainLooper()).post(new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				AlertDialog.Builder b = new AlertDialog.Builder(ctx);
				b.setTitle(R.string.printer_error);
				switch (oResult) {
				case -1:
					b.setMessage(R.string.cover_open);
					break;
				case -2:
					b.setMessage(R.string.overheat);
					break;
				default:
					b.setMessage(R.string.low_voltage);
					break;
				}
				b.setNegativeButton(android.R.string.cancel, null);
				b.setPositiveButton(R.string.repeat, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						switch (type) {
						case 0:
							printRegistrationReport(ctx, (KKMInfo) doc, pm);
							break;
						case 1:
							printWOReport(ctx, (WorkDay) doc, pm);
							break;
						case 2:
							printWCReport(ctx, (WorkDay) doc, pm);
							break;
						case 3:
							printReport(ctx, (FiscalReport) doc, pm);
							break;
						case 4:
							printCheck(ctx, (SellOrder) doc, pm, footer);
							break;
						case 5:
							printCorrection(ctx, (Correction) doc, pm);
							break;
						case 6:
							printCloseReport(ctx, (ArchiveReport) doc, pm);
							break;
						case 7:
							printXReport(ctx, (KKMInfo) doc, pm);
							break;
						}

					}
				});
				AlertDialog dialog = b.create();
				if (!(ctx instanceof Activity))
					dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				dialog.show();

			}
		});

	}

	/**
	 * Напечатать отчет о регистрации
	 * 
	 * @param ctx  контекст
	 * @param info параметры зарегистрированного ККТ
	 * @param pm   устройство, на котором произвести печать
	 */
	public static void printRegistrationReport(final Context ctx, final KKMInfo info, final IPrintManager pm) {
		int oResult = pm.open();
		if (oResult == 0) {

			int reason = info.hasTag(1101) ? info.get(1101).asByte() : 0;
			int y = pm.drawText(Utils.center(info.owner().getName()), 0, 0, true);
			y += pm.drawText(Utils.center(info.owner().getINN()), 0, y, true);
			if (reason == KKMInfo.REASON_REGISTER)
				y += pm.drawText(Utils.center("ОТЧЕТ О РЕГИСТРАЦИИ"), 0, y);
			else
				y += pm.drawText(Utils.center("ИЗМ. СВЕД. О ККТ"), 0, y);
			String[] val = ctx.getResources().getStringArray(R.array.registration_reasons);
			y += pm.drawText(Utils.center(val[reason]), 0, y);

			y += pm.drawText(" ", 0, y);
			y += pm.drawText(Utils.right(Utils.formateDate(info.signature().date())), 0, y);
			y += pm.drawText(info.getPayAddress(), 0, y);
			y += pm.drawText("МЕСТО РАСЧЕТОВ", 0, y);
			y += pm.drawText(info.getPayPlace(), 0, y);
			if (info.casier().isSet())
				y += pm.drawText(Utils.align("Кассир", info.casier().getName()), 0, y);
			if (info.getTaxModes() != 0) {
				y += pm.drawText("СНО:", 0, y);
				val = ctx.getResources().getStringArray(R.array.tax_modes_p);
				String s = "";
				for (int i = 0; i < val.length; i++)
					if ((info.getTaxModes() & (1 << i)) != 0) {
						if (!s.isEmpty())
							s += ",";
						s += val[i];
						if (s.length() * Utils.FONT_SIZE_W > 300) {
							y += pm.drawText(Utils.right(s), 0, y);
							s = "";
						}
					}
				y += pm.drawText(Utils.right(s), 0, y);
				y += pm.drawText(" ", 0, y);
			}

			if (info.getAgentTypes() != 0) {
				y += pm.drawText("Признак агента:", 0, y);
				val = ctx.getResources().getStringArray(R.array.agent_types_p);
				for (int i = 0; i < val.length; i++)
					if ((info.getAgentTypes() & (1 << i)) != 0)
						y += pm.drawText(Utils.right(val[i]), 0, y);
				y += pm.drawText(" ", 0, y);
			}
			y += pm.drawText(Utils.align("АВТОНОМ.РЕЖИМ", info.isOfflineMode() ? "Да" : "Нет"), 0, y);
			y += pm.drawText(Utils.align("АС БСО", info.isBSOMode() ? "Да" : "Нет"), 0, y);
			y += pm.drawText(Utils.align("ШФД", info.isEncryptionMode() ? "Да" : "Нет"), 0, y);
			y += pm.drawText(Utils.align("АВТОМАТ.РЕЖИМ", info.isAutomaticMode() ? "Да" : "Нет"), 0, y);
			if (info.isAutomaticMode())
				y += pm.drawText(Utils.align("Номер автомата", String.valueOf(info.getAutomateNumber())), 0, y);
			y += pm.drawText(Utils.align("ККТ для Интернет", info.isInternetMode() ? "Да" : "Нет"), 0, y);
			y += pm.drawText(Utils.align("ККТ для услуг", info.isServiceMode() ? "Да" : "Нет"), 0, y);
			y += pm.drawText(Utils.align("Подакцизные товары", info.isExcisesMode() ? "Да" : "Нет"), 0, y);
			y += pm.drawText(Utils.align("Проведение азартной игры", info.isCasinoMode() ? "Да" : "Нет"), 0, y);
			y += pm.drawText(Utils.align("Проведение лотереи", info.isLotteryMode() ? "Да" : "Нет"), 0, y);

			if (!info.isOfflineMode()) {
				y += pm.drawText("Эл.Адр.Отправителя", 0, y);
				y += pm.drawText(Utils.right(info.getSenderEmail()), 0, y);
				y += pm.drawText("САЙТ ФНС", 0, y);
				y += pm.drawText(Utils.right(info.getFNSUrl()), 0, y);
				y += pm.drawText("Наименование ОФД", 0, y);
				y += pm.drawText(info.ofd().getName(), 0, y);
				y += pm.drawText(Utils.align("ИНН ОФД", info.ofd().getINN()), 0, y);
			}
			val = ctx.getResources().getStringArray(R.array.ofd_proto_types);
			if (info.getProtocolVersion() > 1)
				y += pm.drawText(Utils.align("ФФД ККТ", val[info.getProtocolVersion() - 1]), 0, y);
			y += pm.drawText(" ", 0, y);

			y += pm.drawText(Utils.align("ЗН ККТ", info.DeviceSerial()), 0, y);
			y += pm.drawText(Utils.align("РН ККТ", info.getRegistrationNo()), 0, y);
			y += pm.drawText(Utils.align("ФН №", info.FNSerial()), 0, y);
			y += pm.drawText(Utils.align("ФД №", String.valueOf(info.signature().number())), 0, y);
			y += pm.drawText(Utils.align("ВЕР. ККТ", info.getVersion()), 0, y);
			y += pm.drawText(Utils.align("ФПД", String.format("%d", info.signature().signature())), 0, y);
			y += pm.drawText(" ", 0, y);
			y = printOFDReport(info.signature().OFDReply(), y, pm);
			for(int i=0;i<5;i++)
				y += pm.drawText(" ", 0, y);
			pm.printPage();
			pm.close();
			oResult = pm.getState(); 
			if( oResult == 0)
				return;
		}
		askRepeat(ctx, info, pm, 0, oResult);
	}

	/**
	 * Напечатать отчет об открытии смены
	 * 
	 * @param ctx контекст
	 * @param wd  описание смены
	 * @param pm  устройство печати
	 */
	public static void printWOReport(Context ctx, WorkDay wd, IPrintManager pm) {
		int oResult = pm.open();

		if (oResult == 0) {

			int y = pm.drawText(Utils.center(wd.get(1048).asString()), 0, 0, true);
			y += pm.drawText(Utils.center(wd.signature().ownerINN()), 0, y, true);
			y += pm.drawText(" ", 0, y);
			y += pm.drawText(Utils.center("ОТЧЕТ ОБ ОТКРЫТИИ СМЕНЫ"), 0, y, true);
			y += pm.drawText(" ", 0, y);
			y += pm.drawText(Utils.right(Utils.formateDate(wd.signature().date())), 0, y);
			y += pm.drawText(wd.getPayAddress(), 0, y);
			y += pm.drawText("МЕСТО РАСЧЕТОВ", 0, y);
			y += pm.drawText(wd.getPayPlace(), 0, y);
			if (wd.casier().isSet())
				y += pm.drawText(Utils.align("Кассир", wd.casier().getName()), 0, y);
			if (wd.isAutoMode())
				y += pm.drawText(Utils.align("Номер автомата", String.valueOf(wd.automateNumber())), 0, y);

			y += pm.drawText(Utils.align("СМЕНА", String.valueOf(wd.number())), 0, y);
			int warnings = wd.signature().FNWarnings();
			if ((warnings & 1) == 1)
				y += pm.drawText(Utils.align("Ресурс ФН менее 3 дней", "Да"), 0, y);
			if ((warnings & 4) == 4)
				y += pm.drawText(Utils.align("Память ФН заполнена", "Да"), 0, y);
			if ((warnings & 2) == 2)
				y += pm.drawText(Utils.align("Ресурс ФН менее 30 дней", "Да"), 0, y);

			y += pm.drawText(" ", 0, y);
			y += pm.drawText(Utils.align("ЗН ККТ", wd.signature().DeviceSerial()), 0, y);
			y += pm.drawText(Utils.align("РН ККТ", wd.signature().KKTNumber()), 0, y);
			y += pm.drawText(Utils.align("ФН №", wd.signature().FNSerial()), 0, y);
			y += pm.drawText(Utils.align("ФД №", String.valueOf(wd.signature().number())), 0, y);
			String[] val = ctx.getResources().getStringArray(R.array.ofd_proto_types);
			y += pm.drawText(Utils.align("ФФД ККТ", val[wd.signature().protocolVersion() - 1]), 0, y);
			y += pm.drawText(Utils.align("ВЕР. ККТ", KKMInfo.KKM_VERSION), 0, y);
			y += pm.drawText(Utils.align("ФПД", String.format("%d", wd.signature().signature())), 0, y);
			y += pm.drawText(" ", 0, y);
			y = printOFDReport(wd.signature().OFDReply(), y, pm);
			for(int i=0;i<5;i++)
				y += pm.drawText(" ", 0, y);
			pm.printPage();
			pm.close();
			oResult = pm.getState(); 
			if( oResult == 0)
				return;
		}
		askRepeat(ctx, wd, pm, 1, oResult);
	}

	/**
	 * Напечатать отчет о закрытии смены
	 * 
	 * @param ctx контекст
	 * @param wd  состояние смены
	 * @param pm  устройство печати
	 */
	public static void printWCReport(Context ctx, WorkDay wd, IPrintManager pm) {
		int oResult = pm.open();
		if (oResult == 0) {
			int y = pm.drawText(Utils.center(wd.signature().ownerName()), 0, 0, true);
			y += pm.drawText(Utils.center(wd.signature().ownerINN()), 0, y, true);
			y += pm.drawText(" ", 0, y);
			y += pm.drawText(Utils.center("ОТЧЕТ О ЗАКРЫТИИ СМЕНЫ"), 0, y, true);
			y += pm.drawText(" ", 0, y);
			y += pm.drawText(Utils.right(Utils.formateDate(wd.signature().date())), 0, y);
			y += pm.drawText(wd.getPayAddress(), 0, y);
			y += pm.drawText("МЕСТО РАСЧЕТОВ", 0, y);
			y += pm.drawText(wd.getPayPlace(), 0, y);

			y += pm.drawText(Utils.alignDot("Чеков за смену", String.valueOf(wd.checksCount())), 0, y);
			y += pm.drawText(Utils.alignDot("ФД ЗА СМЕНУ", String.valueOf(wd.documentCount())), 0, y);
			if (wd.casier().isSet())
				y += pm.drawText(Utils.align("Кассир", wd.casier().getName()), 0, y);
			if (wd.isAutoMode())
				y += pm.drawText(Utils.align("Номер автомата", String.valueOf(wd.automateNumber())), 0, y);

			y += pm.drawText(Utils.align("Номер смены", String.valueOf(wd.number())), 0, y);
			int warings = wd.signature().FNWarnings();
			if ((warings & 1) == 1)
				y += pm.drawText(Utils.align("Ресурс ФН менее 3 дней", "Да"), 0, y);
			if ((warings & 4) == 4)
				y += pm.drawText(Utils.align("Память ФН заполнена", "Да"), 0, y);
			if ((warings & 2) == 2)
				y += pm.drawText(Utils.align("Ресурс ФН менее 30 дней", "Да"), 0, y);
			y += pm.drawText("Количество", 0, y);
			int us = wd.OFDStatistic().unsentDocumentsCount();
			y += pm.drawText(Utils.align("непереданных ФД", String.valueOf(us)), 0, y);

			if (us > 0) {
				/*
				 * y += pm.drawText("Номер первого", 0, y); y +=
				 * pm.drawText(Utils.align("непереданого ФД",
				 * String.valueOf(wd.OFDStatistic().firstUnsentNumber())), 0, y);
				 */
				y += pm.drawText("Дата и время первого из", 0, y);
				y += pm.drawText(Utils.align("непереданных ФД", Utils.formateDate(wd.OFDStatistic().firstUnsentDate())),
						0, y);
			}

			y += pm.drawText(Utils.align("ЗН ККТ", wd.signature().DeviceSerial()), 0, y);
			y += pm.drawText(Utils.align("РН ККТ", wd.signature().KKTNumber()), 0, y);
			y += pm.drawText(Utils.align("ФН №", wd.signature().FNSerial()), 0, y);
			y += pm.drawText(Utils.align("ФД №", String.valueOf(wd.signature().number())), 0, y);
			y += pm.drawText(Utils.align("ВЕР. ККТ", KKMInfo.KKM_VERSION), 0, y);
			String[] val = ctx.getResources().getStringArray(R.array.ofd_proto_types);
			y += pm.drawText(Utils.align("ФФД ККТ", val[wd.signature().protocolVersion() - 1]), 0, y);
			y += pm.drawText(Utils.align("ФПД", String.format("%d", wd.signature().signature())), 0, y);
			y += pm.drawText(" ", 0, y);
			y = printOFDReport(wd.signature().OFDReply(), y, pm);
			for(int i=0;i<5;i++)
				y += pm.drawText(" ", 0, y);
			pm.printPage();
			pm.close();
			oResult = pm.getState(); 
			if( oResult == 0)
				return;
		}
		askRepeat(ctx, wd, pm, 2, oResult);

	}

	/**
	 * Напечатать отчет о расчетах
	 * 
	 * @param ctx конекст
	 * @param cnt отчет о расчетах
	 * @param pm  устройство печати
	 */
	public static void printReport(Context ctx, FiscalReport cnt, IPrintManager pm) {
		int oResult = pm.open();
		if (oResult == 0) {
			int y = pm.drawText(Utils.center("Отчет о текущем состоянии расчетов"), 0, 0);
			y += pm.drawText(" ", 0, y);
			y += pm.drawText(Utils.right(Utils.formateDate(cnt.signature().date())), 0, y);
			y += pm.drawText(Utils.center(cnt.signature().ownerName()), 0, y, true);
			y += pm.drawText(Utils.align("ИНН пользователя", cnt.signature().ownerINN()), 0, y);
			y += pm.drawText(cnt.getPayAddress(), 0, y);
			y += pm.drawText("МЕСТО РАСЧЕТОВ", 0, y);
			y += pm.drawText(cnt.getPayPlace(), 0, y);
			y += pm.drawText(" ", 0, y);
			if (cnt.isWDOpen())
				y += pm.drawText(Utils.align("Номер смены", String.valueOf(cnt.workDayNumber())), 0, y);
			if (cnt.isOfflineMode())
				y += pm.drawText(Utils.align("Автономный режим", "Да"), 0, y);
			y += pm.drawText("Количество", 0, y);
			y += pm.drawText(Utils.align("непереданных ФД", String.valueOf(cnt.OFDStatistic().unsentDocumentsCount())),
					0, y);
			y += pm.drawText("Номер первого", 0, y);
			y += pm.drawText(Utils.align("непереданого ФД",
					cnt.OFDStatistic().unsentDocumentsCount() > 0
							? String.valueOf(cnt.OFDStatistic().firstUnsentNumber())
							: "-"),
					0, y);
			y += pm.drawText("Дата и время первого из", 0, y);
			y += pm.drawText(Utils.align("непереданных ФД",
					cnt.OFDStatistic().unsentDocumentsCount() > 0
							? Utils.formateDate(cnt.OFDStatistic().firstUnsentDate())
							: "-"),
					0, y);
			y += pm.drawText(Utils.align("РН ККТ", cnt.signature().KKTNumber()), 0, y);
			y += pm.drawText(Utils.align("ФН №", cnt.signature().FNSerial()), 0, y);
			y += pm.drawText(Utils.align("ФД №", String.valueOf(cnt.signature().number())), 0, y);
			y += pm.drawText(Utils.align("ФПД", String.format("%d", cnt.signature().signature())), 0, y);
			y += pm.drawText(" ", 0, y);
			y = printOFDReport(cnt.signature().OFDReply(), y, pm);
			for(int i=0;i<5;i++)
				y += pm.drawText(" ", 0, y);
			pm.printPage();
			pm.close();
			oResult = pm.getState(); 
			if( oResult == 0)
				return;
		}
		askRepeat(ctx, cnt, pm, 3, oResult);
	}

	/**
	 * Напечатать чек прихода/расхода/возврата
	 * 
	 * @param ctx контекст
	 * @param c   чек
	 * @param pm  устройство печати
	 */
	public static void printCheck(Context ctx, SellOrder c, IPrintManager pm, String footer) {
		int oResult = pm.open();
		if (oResult == 0) {
			/*			String fs = Utils.FONT_NAME;
			
			File f = new File(Environment.getExternalStorageDirectory(),"FNCore/f.ttf");
			if(!f.exists()) try {
				Log.d("PP", "Loading fonts");
				InputStream is = ctx.getAssets().open("14603.ttf");
				byte b [] = new byte[8192];
				int read;
				FileOutputStream fos = new FileOutputStream(f);
				while((read = is.read(b)) > 0)
					fos.write(b,0,read);
				fos.close();
				is.close();
			} catch(Exception e) {
				Log.d("PP", "Loading fonts fail",e);
			}
			if(f.exists()) {
				Utils.FONT_NAME = f.getAbsolutePath();
				Utils.setFontSize(22);
			}
			Bitmap dr = ((BitmapDrawable)ctx.getResources().getDrawable(R.drawable.d_l)).getBitmap();
			int y = pm.printBitmap(dr, (384-dr.getWidth())/2, 0); */
			int y = 0;
			y += pm.drawText(Utils.center(c.signature().ownerINN()), 0, y, true);
			y += pm.drawText(Utils.center(c.signature().ownerName()), 0, y, true);
			y += pm.drawText(Utils.center(String.format("КАССОВЫЙ ЧЕК № %d", c.getNumber())), 0, y);
			switch (c.getType()) {
			case SellOrder.TYPE_INCOME:
				y += pm.drawText(Utils.center("ПРИХОД"), 0, y, true);
				break;
			case SellOrder.TYPE_RETURN_INCOME:
				y += pm.drawText(Utils.center("ВОЗВРАТ ПРИХОДА"), 0, y, true);
				break;
			case SellOrder.TYPE_OUTCOME:
				y += pm.drawText(Utils.center("РАСХОД"), 0, y, true);
				break;
			case SellOrder.TYPE_RETURN_OUTCOME:
				y += pm.drawText(Utils.center("ВОЗВРАТ РАСХОДА"), 0, y, true);
				break;
			}
			y += pm.drawText(c.getPayAddress(), 0, y);
			y += pm.drawText("МЕСТО РАСЧЕТОВ", 0, y);
			y += pm.drawText(c.getPayPlace(), 0, y);
			y += pm.drawText(Utils.right(Utils.formateDate(c.signature().date())), 0, y);
			y += pm.drawText(Utils.align("СМЕНА №", String.valueOf(c.workDayNumber())), 0, y);
			if (c.isAutoMode())
				y += pm.drawText(Utils.align("Номер автомата", String.valueOf(c.automateNumber())), 0, y);
			else if (c.casier().isSet())
				y += pm.drawText(Utils.align("Кассир", c.casier().getName()), 0, y);

			y += pm.drawText(" ", 0, y);

			String[] ndsv; 
			if(System.currentTimeMillis() > 1546300800000L)
				ndsv = ctx.getResources().getStringArray(R.array.vat_values_n);
			else
				ndsv = ctx.getResources().getStringArray(R.array.vat_values);
			String[] gtype = ctx.getResources().getStringArray(R.array.item_types);
			String[] agents = ctx.getResources().getStringArray(R.array.agent_types_p);
			String[] pay_types = ctx.getResources().getStringArray(R.array.pay_types);
			float[] snds = { 0, 0, 0, 0, 0, 0, 0, 0 };
			for (int i = 0; i < c.items().size(); i++) {
				SellItem e = c.items().get(i);
				String name = e.NAME;
				if(e.ITEM_TYPE == 15 || e.ITEM_TYPE == 16) {
					if("1".equals(e.NAME)) name = "Доход от долевого участия в других организациях";
					if("2".equals(e.NAME)) name = "Доход в виде курсовой разницы, образующейся вследствие отклонения курса продажи (покупки) иностранной валюты от официального курса";
					if("3".equals(e.NAME)) name = "Доход в виде подлежащих уплате должником штрафов, пеней и (или) иных санкций за нарушение договорных обязательств";
					if("4".equals(e.NAME)) name = "Доход от сдачи имущества (включая земельные участки) в аренду (субаренду)";
					if("5".equals(e.NAME)) name = "Доход от предоставления в пользование прав на результаты интеллектуальной деятельности";
					if("6".equals(e.NAME)) name = "Доход в виде процентов, полученных по договорам займа и другим долговым обязательствам";
					if("7".equals(e.NAME)) name = "Доход в виде сумм восстановленных резервов";
					if("8".equals(e.NAME)) name = "Доход в виде безвозмездно полученного имущества (работ, услуг) или имущественных прав";
					if("9".equals(e.NAME)) name = "Доход в виде дохода, распределяемого в пользу налогоплательщика при его участии в простом товариществе";
					if("10".equals(e.NAME)) name = "Доход в виде дохода прошлых лет, выявленного в отчетном (налоговом) периоде";
					if("11".equals(e.NAME)) name = "Доход в виде положительной курсовой разницы";
					if("12".equals(e.NAME)) name = "Доход в виде основных средств и нематериальных активов, безвозмездно полученных атомными станциями";
					if("13".equals(e.NAME)) name = "Доход в виде стоимости полученных материалов при ликвидации выводимых из эксплуатации основных средств";
					if("14".equals(e.NAME)) name = "Доход в виде использованных не по целевому назначению имущества, работ, услуг";
					if("15".equals(e.NAME)) name = "Доход в виде использованных не по целевому назначению средств, предназначенных для формирования резервов по обеспечению безопасности производств";
					if("16".equals(e.NAME)) name = "Доход в виде сумм, на которые уменьшен уставной (складочный) капитал (фонд) организации";
					if("17".equals(e.NAME)) name = "Доход в виде сумм возврата от некоммерческой организации ранее уплаченных взносов (вкладов)";
					if("18".equals(e.NAME)) name = "Доход в виде сумм кредиторской задолженности, списанной в связи с истечением срока исковой давности или по другим основаниям";
					if("19".equals(e.NAME)) name = "Доход в виде доходов, полученных от операций с производными финансовыми инструментами";
					if("20".equals(e.NAME)) name = "Доход в виде стоимости излишков материально-производственных запасов и прочего имущества, которые выявлены в результате инвентаризации";
					if("21".equals(e.NAME)) name = "Доход в виде стоимости продукции СМИ и книжной продукции, подлежащей замене при возврате либо при списании";
					if("22".equals(e.NAME)) name = "Доход в виде сумм корректировки прибыли налогоплательщика";
					if("23".equals(e.NAME)) name = "Доход в виде возвращенного денежного эквивалента недвижимого имущества и (или) ценных бумаг, переданных на пополнение целевого капитала некоммерческой организации";
					if("24".equals(e.NAME)) name = "Доход в виде разницы между суммой налоговых вычетов из сумм акциза и указанных сумм акциза";
					if("25".equals(e.NAME)) name = "Доход в виде прибыли контролируемой иностранной компании";
					if("26".equals(e.NAME)) name = "Взносы на ОПС";
					if("27".equals(e.NAME)) name = "Взносы на ОСС в связи с нетрудоспособностью";
					if("28".equals(e.NAME)) name = "Взносы на ОМС";
					if("29".equals(e.NAME)) name = "Взносы на ОСС от несчастных случаев";
					if("30".equals(e.NAME)) name = "Пособия по временной нетрудоспособности";  
					if("31".equals(e.NAME)) name = "Платежи по добровольному личному страхованию";
				}
				
				y += pm.drawText(name, 0, y, true);
				y += pm.drawText(Utils.right(String.format("%.03f X %.02f", e.QTTY, e.PRICE)), 0, y);
				y += pm.drawText(Utils.align("Сумма", String.format("=%.02f", e.sum())), 0, y);
				int vType = e.VAT_TYPE;
				if(System.currentTimeMillis() > 1546300800000L) 
					switch(e.VAT_TYPE) {
					case SellItem.VAT_TYPE_18:
						if(snds[SellItem.VAT_TYPE_20] > 0) {
							snds[SellItem.VAT_TYPE_20] += e.vatValue();
							vType = SellItem.VAT_TYPE_20;
						}
						else
							snds[SellItem.VAT_TYPE_18] += e.vatValue();
						break;
					case SellItem.VAT_TYPE_18_118:
						if(snds[SellItem.VAT_TYPE_20_120] > 0) {
							snds[SellItem.VAT_TYPE_20_120] += e.vatValue();
							vType = SellItem.VAT_TYPE_20_120;
						}
						else
							snds[SellItem.VAT_TYPE_18_118] += e.vatValue();
						break;
					case SellItem.VAT_TYPE_20:
						if(snds[SellItem.VAT_TYPE_18] > 0) {
							snds[SellItem.VAT_TYPE_18] += e.vatValue();
							vType = SellItem.VAT_TYPE_18;
						}
						else
							snds[SellItem.VAT_TYPE_20] += e.vatValue();
						break;
					case SellItem.VAT_TYPE_20_120:
						if(snds[SellItem.VAT_TYPE_18_118] > 0) {
							snds[SellItem.VAT_TYPE_18_118] += e.vatValue();
							vType = SellItem.VAT_TYPE_18_118;
						}
						else
							snds[SellItem.VAT_TYPE_20_120] += e.vatValue();
						break;
					default:
						snds[e.VAT_TYPE] += e.vatValue();
					}
				else 
					snds[e.VAT_TYPE] += e.vatValue();
				y += pm.drawText(Utils.align(String.format("%s", ndsv[vType]),
						String.format("=%.02f", e.vatValue())), 0, y);
				
				// y += pm.drawText("Признак способа расчета",0,y);
				y += pm.drawText(Utils.right(pay_types[e.PAY_TYPE - 1]), 0, y);
				y += pm.drawText(Utils.right(gtype[e.ITEM_TYPE - 1]), 0, y);
				if (e.AGENT_TYPE != 0) {
					for (int k = 0; k < e.AGENT_TYPE; k++) {
						int mask = (1 << k);
						if ((e.AGENT_TYPE & mask) == mask) {
							y += pm.drawText(Utils.right(agents[k]), 0, y);
							break;
						}
					}
				}
				Tag t = e.get(1223);
				if (t != null) {
					SparseArray<Tag> sTags = t.asSTLV();
					y = printAgentInfo(sTags, pm, y, agents);
				}
				t = e.get(1224);
				if (t != null) {
					SparseArray<Tag> sTags = t.asSTLV();
					y = printAgentInfo(sTags, pm, y, agents);
				}
				y += pm.drawText(" ",0,y);
			}
			y += pm.drawText(" ", 0, y);
			y += pm.drawText(Utils.alignDot("ИТОГ", String.format("= %.02f", c.sum())), 0, y, true);
			y += pm.drawText(" ", 0, y);
			String[] ss = System.currentTimeMillis() > 1546300800000L ? ctx.getResources().getStringArray(R.array.payment_types_19) : ctx.getResources().getStringArray(R.array.payment_types);
			for (Payment p1 : c.payments())
				y += pm.drawText(Utils.align(ss[p1.TYPE], String.format("= %.02f", p1.SUM)), 0, y);
			ss = ctx.getResources().getStringArray(R.array.tax_modes_p);
			if (snds[1] > 0)
				y += pm.drawText(Utils.align("СУММА НДС 10%", String.format("= %.02f", snds[1])), 0, y);
			if (snds[0] > 0) {
				if(System.currentTimeMillis() > 1546300800000L)
					y += pm.drawText(Utils.align("СУММА НДС 20%", String.format("= %.02f", snds[0])), 0, y);
				else
					y += pm.drawText(Utils.align("СУММА НДС 18%", String.format("= %.02f", snds[0])), 0, y);
			}
			if (snds[3] > 0)
				y += pm.drawText(Utils.align("СУММА НДС 10/110", String.format("= %.02f", snds[3])), 0, y);
			if (snds[2] > 0) {
				if(System.currentTimeMillis() > 1546300800000L)
					y += pm.drawText(Utils.align("СУММА НДС 20/120", String.format("= %.02f", snds[2])), 0, y);
				else
					y += pm.drawText(Utils.align("СУММА НДС 18/118", String.format("= %.02f", snds[2])), 0, y);
			}
			if (snds[4] > 0)
				y += pm.drawText(Utils.align("СУММА C НДС 0%", String.format("= %.02f", snds[4])), 0, y);
			if (snds[5] > 0)
				y += pm.drawText(Utils.align("СУММА БЕЗ НДС", String.format("= %.02f", snds[5])), 0, y);
			if (snds[6] > 0 && snds[2] < 0.01f && snds[0] < 0.01f )
				y += pm.drawText(Utils.align("СУММА НДС 18%", String.format("= %.02f", snds[6])), 0, y);
			if (snds[7] > 0 && snds[2] < 0.01f  && snds[0] < 0.01f )
				y += pm.drawText(Utils.align("СУММА НДС 18/118", String.format("= %.02f", snds[6])), 0, y);

			if (c.getRefund() >= 0.01)
				y += pm.drawText(Utils.align("СДАЧА", String.format("= %.02f", c.getRefund())), 0, y);
			Tag t = c.get(1008);
			if (t != null) {
				if (t.asString().contains("@")) {
					y += pm.drawText("ЭЛ.АДРЕС.ОТПРАВИТЕЛЯ:", 0, y);
					y += pm.drawText(c.getSenderEmail(), 0, y);
					y += pm.drawText("ЭЛ.АДР.ПОКУПАТЕЛЯ", 0, y);
				} else
					y += pm.drawText("ТЕЛ. ПОКУПАТЕЛЯ", 0, y);
				y += pm.drawText(t.asString(), 0, y);
			}
			y += pm.drawText(" ", 0, y);
			y += pm.drawText(Utils.align("СНО", ss[c.getTaxMode()]), 0, y);
			if(c.hasTag(1192)) 
				y += pm.drawText(Utils.align("ДОП. РЕКВИЗИТ", c.get(1192).asString()), 0, y);
			y += pm.drawText(Utils.align("РН ККТ", c.signature().KKTNumber()), 0, y);
			y += pm.drawText(Utils.align("ФН №", c.signature().FNSerial()), 0, y);
			y += pm.drawText(Utils.align("ФД №", String.valueOf(c.signature().number())), 0, y);
			y += pm.drawText(Utils.align("ФПД", String.format("%d", c.signature().signature())), 0, y);
			y += pm.drawText("САЙТ ФНС", 0, y);
			y += pm.drawText(c.fnsUrl(), 0, y);

			y = printAgentInfo(c, pm, y, agents);
			y += pm.drawText(" ", 0, y);
			
			int qrsize = (int)((Utils.FONT_SIZE_W*Utils.PAGE_WIDTH_CH) *0.8);
			y += pm.printBitmap(c.getQR(qrsize, qrsize), ((Utils.FONT_SIZE_W*Utils.PAGE_WIDTH_CH) - qrsize)/2, y);
			y += pm.drawText(" ", 0, y);
			printOFDReport(c.signature().OFDReply(), y, pm);
			y += pm.drawText(" ", 0, y);
			if (footer != null)
				y = printText(footer, pm,y);
			for(int i=0;i<10;i++)
				y += pm.drawText(" ", 0, y);
			pm.printPage();
			pm.close();
			oResult = pm.getState();
//			Utils.FONT_NAME = fs;
			if( oResult == 0)
				return;
		}
		askRepeat(ctx, c, pm, 4, oResult, footer);
	}

	/**
	 * Напечатать чек коррекции
	 * 
	 * @param ctx   конекст
	 * @param order чек коррекции
	 * @param pm    устройство печати
	 */
	public static void printCorrection(Context ctx, Correction order, IPrintManager pm) {
		int oResult = pm.open();
		if (oResult == 0) {
			int y = pm.drawText(Utils.center(order.signature().ownerName()), 0, 0, true);
			y += pm.drawText(Utils.center(order.signature().ownerINN()), 0, y, true);
			y += pm.drawText(Utils.center(String.format("ЧЕК КОР. %d", order.number())), 0, y);
			String[] val = ctx.getResources().getStringArray(R.array.c_type_check);
			y += pm.drawText(Utils.center(val[order.type() == 1 ? 0 : 1]), 0, y);
			y += pm.drawText(Utils.right(Utils.formateDate(order.signature().date())), 0, y);
			y += pm.drawText(order.getPayAddress(), 0, y);
			y += pm.drawText("МЕСТО РАСЧЕТОВ", 0, y);
			y += pm.drawText(order.getPayPlace(), 0, y);
			y += pm.drawText(" ", 0, y);

			y += pm.drawText(Utils.align("СМЕНА № ", String.valueOf(order.signature().workDayNumber())), 0, y);
			if (order.casier().isSet())
				y += pm.drawText(Utils.align("Кассир ", order.casier().getName()), 0, y);

			val = ctx.getResources().getStringArray(R.array.tax_modes_p);
			y += pm.drawText(Utils.align("СНО", val[order.getVATMode()]), 0, y);
			y += pm.drawText(" ", 0, y);
			y += pm.drawText(Utils.align("ИТОГ", String.format("= %.02f", order.getSum())), 0, y, true);
			switch (order.getVATType()) {
			case 0:
				if(System.currentTimeMillis() > 1546300800000L)
					y += pm.drawText(Utils.align("СУММА НДС 20%", String.format("= %.02f", order.getSum() * 20f / 120f)), 0,
							y);
				else
					y += pm.drawText(Utils.align("СУММА НДС 18%", String.format("= %.02f", order.getSum() * 18f / 118f)), 0,
							y);
					
				break;
			case 1:
				y += pm.drawText(Utils.align("СУММА НДС 10%", String.format("= %.02f", order.getSum() * 10f / 110f)), 0,
						y);
				break;
			case 2:
				if(System.currentTimeMillis() > 1546300800000L)
					y += pm.drawText(Utils.align("СУММА НДС 120/120", String.format("= %.02f", order.getSum() * 20f / 120f)),
							0, y);
				else
					y += pm.drawText(Utils.align("СУММА НДС 18/118", String.format("= %.02f", order.getSum() * 18f / 118f)),
							0, y);
					
				break;
			case 3:
				y += pm.drawText(Utils.align("СУММА НДС 10/110", String.format("= %.02f", order.getSum() * 10f / 110f)),
						0, y);
				break;

			case 4:
				y += pm.drawText(Utils.align("СУММА НДС 0%", String.format("= %.02f", order.getSum())), 0, y);
				break;
			case 5:
				y += pm.drawText(Utils.align("СУММА БЕЗ НДС", String.format("= %.02f", order.getSum())), 0, y);
				break;

			}
			val = ctx.getResources().getStringArray(R.array.payment_types);
			for (Payment p1 : order.payments())
				y += pm.drawText(Utils.align(val[p1.TYPE], String.format("= %.02f", p1.SUM)), 0, y);
			y += pm.drawText(
					Utils.align("Тип коррекции", order.correctionType() == 0 ? "Самостоятельно" : "По предписанию"), 0,
					y);
			y += pm.drawText(Utils.align("ДАТА", Utils.formateDate(order.getDocumentDate().getTimeInMillis())), 0, y);
			y += pm.drawText(Utils.align("ОСН.ДЛЯ КОРР", order.getDocumentNumber()), 0, y);
			if(order.hasTag(1192)) 
				y += pm.drawText(Utils.align("ДОП. РЕКВИЗИТ", order.get(1192).asString()), 0, y);
			y += pm.drawText(Utils.align("РН ККТ", order.signature().KKTNumber()), 0, y);
			y += pm.drawText(Utils.align("ФН №", order.signature().FNSerial()), 0, y);
			y += pm.drawText(Utils.align("ФД №", String.valueOf(order.signature().number())), 0, y);
			y += pm.drawText(Utils.align("ФПД", String.format("%d", order.signature().signature())), 0, y);
			y += pm.drawText(Utils.align("САЙТ ФНС", order.get(1060).asString()), 0, y);
			y += pm.drawText(" ", 0, y);
			y = printOFDReport(order.signature().OFDReply(), y, pm);
			for(int i=0;i<6;i++)
				y += pm.drawText(" ", 0, y);
			pm.printPage();
			pm.close();
			oResult = pm.getState(); 
			if( oResult == 0)
				return;
		}
		askRepeat(ctx, order, pm, 5, oResult);
	}

	/**
	 * Наечатать отчет о закрытии фискального режима
	 * 
	 * @param ctx конекст
	 * @param doc отчет о закрытии фискального режима
	 * @param pm  устройство печати
	 */
	public static void printCloseReport(Context ctx, ArchiveReport doc, IPrintManager pm) {
		int oResult = pm.open();
		if (oResult == 0) {
			int y = pm.drawText(Utils.center(doc.signature().ownerINN()), 0, 0, true);
			y += pm.drawText(Utils.center(doc.signature().ownerName()), 0, y, true);
			y += pm.drawText(doc.getPayAddress(), 0, y);
			y += pm.drawText("МЕСТО РАСЧЕТОВ", 0, y);
			y += pm.drawText(doc.getPayPlace(), 0, y);
			y += pm.drawText(" ", 0, y);
			y += pm.drawText(Utils.center("Отчет о закрытии"), 0, y, true);
			y += pm.drawText(Utils.center("фискального накопителя"), 0, y, true);
			y += pm.drawText(" ", 0, y);
			y += pm.drawText(Utils.right(Utils.formateDate(doc.signature().date())), 0, y);
			y += pm.drawText(Utils.align("СМЕНА", String.valueOf(doc.signature().workDayNumber())), 0, y);
			if (doc.casier().isSet())
				y += pm.drawText(Utils.align("Кассир ", doc.casier().getName()), 0, y);
			if (doc.isAutoMode())
				y += pm.drawText(Utils.align("Номер автомата", String.valueOf(doc.automateNumer())), 0, y);

			y += pm.drawText(Utils.align("РН ККТ", doc.signature().KKTNumber()), 0, y);
			y += pm.drawText(Utils.align("ФН №", doc.signature().FNSerial()), 0, y);
			y += pm.drawText(Utils.align("ФД №", String.valueOf(doc.signature().number())), 0, y);
			y += pm.drawText(Utils.align("ФПД", String.format("%d", doc.signature().signature())), 0, y);
			y += pm.drawText(" ", 0, y);
			y = printOFDReport(doc.signature().OFDReply(), y, pm);
			for(int i=0;i<5;i++)
				y += pm.drawText(" ", 0, y);
			pm.printPage();
			pm.close();
			oResult = pm.getState(); 
			if( oResult == 0)
				return;
		}
		askRepeat(ctx, doc, pm, 6, oResult);
	}

	private static int printAgentInfo(SparseArray<Tag> sTags, IPrintManager pm, int y, String[] agents) {
		Tag t1 = sTags.get(1057);
		if (t1 != null) {
			int v = 0;
			byte vv = t1.asByte();
			while ((vv & 0x1) != 1) {
				vv >>= 1;
				v++;
			}
			if (v < agents.length)
				y += pm.drawText(agents[v], 0, y);
		}
		t1 = sTags.get(1075);
		if (t1 != null) {
			y += pm.drawText("ТЛФ.ОП.ПЕРЕВОДА", 0, y);
			y += pm.drawText(Utils.right(t1.asString()), 0, y);
		}
		t1 = sTags.get(1044);
		if (t1 != null) {
			y += pm.drawText("ОП.АГЕНТА", 0, y);
			y += pm.drawText(Utils.right(t1.asString()), 0, y);
		}

		t1 = sTags.get(1074);
		if (t1 != null) {
			y += pm.drawText("ТЕЛ.ОП.ПР.ПЛАТЕЖА", 0, y);
			y += pm.drawText(Utils.right(t1.asString()), 0, y);
		}

		t1 = sTags.get(1073);
		if (t1 != null) {
			y += pm.drawText("ТЛФ.ПЛ.АГЕНТА", 0, y);
			y += pm.drawText(Utils.right(t1.asString()), 0, y);
		}
		t1 = sTags.get(1026);
		if (t1 != null) {
			y += pm.drawText("ОПЕРАТОР ПЕРЕВОДА", 0, y);
			y += pm.drawText(Utils.right(t1.asString()), 0, y);
		}
		t1 = sTags.get(1005);
		if (t1 != null) {
			y += pm.drawText("АДР.ОП.ПЕРЕВОДА", 0, y);
			y += pm.drawText(Utils.right(t1.asString()), 0, y);
		}
		t1 = sTags.get(1016);
		if (t1 != null) {
			y += pm.drawText("ИНН ОП.ПЕРЕВОДА", 0, y);
			y += pm.drawText(Utils.right(t1.asString()), 0, y);
		}
		t1 = sTags.get(1171);
		if (t1 != null) {
			y += pm.drawText("ТЛФ.ПОСТ.", 0, y);
			y += pm.drawText(Utils.right(t1.asString()), 0, y);
		}
		t1 = sTags.get(1225);
		if (t1 != null) {
			y += pm.drawText("Наименование поставщика", 0, y);
			y += pm.drawText(Utils.right(t1.asString()), 0, y);
		}

		return y;
	}

	public static void printXReport(Context context, KKMInfo info, IPrintManager pm) {
		int oResult = pm.open(); 
		if ( oResult == 0) {
			
			float [] p_summs = new float[] { 0,0,0,0};
			int [] p_cnts = new int[] {0,0,0,0};
			float [] c_summs = new float[] { 0,0,0,0};
			int [] c_cnts = new int[] {0,0,0,0};
			
			float[] payments = new float[] { 0, 0, 0, 0, 0 };
			
			Cursor c = context.getContentResolver().query(Uri.parse("content://rs.fncore.data/documents"), null, null,
					null, null);
			if(c.moveToLast()) do {
				if(c.getInt(1) == DocTypes.DOC_TYPE_OPENWD) break;
				switch(c.getInt(1)) {
				case DocTypes.DOC_TYPE_ORDER:
					SellOrder order = Utils.unmarshall(c.getBlob(2),SellOrder.CREATOR);
					float sign = 1.0f;
					if(order.getType() == SellOrder.TYPE_OUTCOME || order.getType() == SellOrder.TYPE_RETURN_INCOME)
						sign = -1.0f;
					p_summs[order.getType()] += order.sum();
					p_cnts[order.getType()]++;
					for(Payment p : order.payments()) 
						payments[p.TYPE] += (p.SUM * sign);
					
					break;
				case DocTypes.DOC_TYPE_CORRECTION:
					Correction cr = Utils.unmarshall(c.getBlob(2),Correction.CREATOR);
					c_summs[cr.type()] += cr.getSum();
					c_cnts[cr.type()]++;
					sign = 1.0f;
					if(cr.type() == SellOrder.TYPE_OUTCOME || cr.type() == SellOrder.TYPE_RETURN_INCOME)
						sign = -1.0f;
					for(Payment p : cr.payments())
						payments[p.TYPE] += (p.SUM * sign);
					break;
				}
			} while(c.moveToPrevious());
			c.close();
			c = context.getContentResolver().query(Uri.parse("content://rs.fncore.data/cashrests"), null, null,
					null, null);
			float rest = 0;
			if (c.moveToFirst())
				rest = c.getFloat(0);
			c.close();
			int y = pm.drawText(Utils.align("ККТ", info.getRegistrationNo()), 0, 0);
			y += pm.drawText(Utils.right(info.owner().getName()), 0, y);
			y += pm.drawText(Utils.right(info.owner().getINN()), 0, y);
			y += pm.drawText(Utils.align("СМЕНА №", String.valueOf(info.workDayNumber())), 0, y);
			if (info.isWorkDayOpen())
				y += pm.drawText(Utils.right("ОТКРЫТА"), 0, y);
			else
				y += pm.drawText(Utils.right("ЗАКРЫТА"), 0, y);
			y += pm.drawText(Utils.center("ИТОГОВЫЕ СУММЫ"), 0, y,true);
			y += pm.drawText(Utils.alignDot(String.format("%05d ПРИХОД",p_cnts[0]), String.format("%.2f",p_summs[0])), 0, y);
			y += pm.drawText(Utils.alignDot(String.format("%05d ВОЗВРАТ ПРИХОДА",p_cnts[1]), String.format("%.2f",p_summs[1])), 0, y);
			y += pm.drawText(Utils.alignDot(String.format("%05d РАСХОД",p_cnts[2]), String.format("%.2f",p_summs[2])), 0, y);
			y += pm.drawText(Utils.alignDot(String.format("%05d ВОЗВРАТ РАСХОДА",p_cnts[3]), String.format("%.2f",p_summs[3])), 0, y);
			y += pm.drawText(Utils.center("КОРРЕКЦИИ"), 0, y,true);
			y += pm.drawText(Utils.alignDot(String.format("%05d ПРИХОД",c_cnts[0]), String.format("%.2f",c_summs[0])), 0, y);
			y += pm.drawText(Utils.alignDot(String.format("%05d ВОЗВРАТ ПРИХОДА",c_cnts[1]), String.format("%.2f",c_summs[1])), 0, y);
			y += pm.drawText(Utils.alignDot(String.format("%05d РАСХОД",c_cnts[2]), String.format("%.2f",c_summs[2])), 0, y);
			y += pm.drawText(Utils.alignDot(String.format("%05d ВОЗВРАТ РАСХОДА",c_cnts[3]), String.format("%.2f",c_summs[3])), 0, y);
			y += pm.drawText(Utils.center("ПРОДАЖА"), 0, y,true);
			y += pm.drawText(Utils.alignDot("НАЛИЧНЫМИ", String.format("%.2f", payments[0])), 0, y);
			y += pm.drawText(Utils.alignDot("ЭЛЕКТРОННЫМИ", String.format("%.2f", payments[1])), 0, y);
			y += pm.drawText(Utils.center("ОСТАТОК В КАССЕ"), 0, y,true);
			y += pm.drawText(Utils.alignDot("НАЛИЧНЫЕ", String.format("%.2f", rest)), 0, y);
			for (int i = 0; i < 10; i++)
				y += pm.drawText(" ", 0, y);
			pm.printPage();
			pm.close();
			oResult = pm.getState(); 
			if( oResult == 0)
				return;
		} 
		askRepeat(context, info, pm, 7, oResult);
			
	}

}
