package rs.fn;

import android.graphics.Bitmap;

/**
 * Интерфейс устройства печати
 * @author nick
 *
 */
public interface IPrintManager {
	/**
	 * Инициализировать устройство печати
	 * @return 0 в случае успеха
	 */
	public int open();
	/**
	 * Напечатать текст
	 * @param line текст
	 * @param x позиция по X в точках 
	 * @param y позиция по Y в точках
	 * @return высоту строки в точках
	 */
	public int drawText(String line, int x, int y);
	/**
	 * Напечатать текст
	 * @param line текст
	 * @param x позиция по X в точках 
	 * @param y позиция по Y в точках
	 * @param bold признак жирного шрифта
	 * @return высоту строки в точках
	 */
	public int drawText(String line, int x, int y, boolean bold);
	/**
	 * Напечатать текст
	 * @param line текст
	 * @param x позиция по X в точках 
	 * @param y позиция по Y в точках
	 * @param fontName имя шрифта
	 * @param fontSize размер шрифта
	 * @param bold признак жирного шрифта
	 * @param italic признак наклонного шрифта
	 * @return высоту строки в точках
	 */
	public int drawText(String line, int x, int y, String fontName, int fontSize, boolean bold, boolean italic);
	/**
	 * Напечатать QR код
	 * @param value значение для кодирования 
	 * @param x позиция по X в точках
	 * @param y позиция по Y в точках
	 * @param w ширина в точках
	 * @param h высота в точках
	 * @return высоту напечатанного кода в точках
	 */
	public int printQR(String value, int x, int y, int w, int h);
	
	public int printLinear(String value, int x, int y, String type);
	public int printBitmap(Bitmap b, int x, int y);
	/**
	 * Завершить формирование страницы, напечатать ее 
	 */
	public void printPage();
	/**
	 * Завершить работу с печатающим устройством
	 */
	public void close();
	/**
	 * Получить состояние принтера
	 * @return
	 */
	public int getState();
	
}
