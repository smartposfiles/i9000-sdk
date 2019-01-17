package rs.fnsample;



import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import rs.fn.Const.DocTypes;

public class Documents extends Activity {
	private class DocumentsAdapter extends BaseAdapter implements OnItemSelectedListener, TextWatcher {

		private Uri URI = Uri.parse("content://rs.fncore.data/documents");
		
		private class VH implements View.OnClickListener {
			private TextView _docNo, _docType, _ofd;
			private byte[] BLOB;
			private int _type;

			public VH(View v) {
				v.setTag(this);
				_docNo = (TextView) v.findViewById(R.id.lbl_no);
				_docType = (TextView) v.findViewById(R.id.lbl_type);
				_ofd = (TextView) v.findViewById(R.id.lbl_ofd);
				v.findViewById(R.id.iv_view).setOnClickListener(this);
			}

			public void update(Cursor c) {
				BLOB = c.getBlob(2);
				_docNo.setText(String.valueOf(c.getInt(0)));
				_type = c.getInt(1);
				if (c.getInt(3) != -1) {
					String s = String.format("Ответ %02X", c.getInt(3));
					_ofd.setText(s);
				} else
					_ofd.setText("Нет ответа");
				switch (_type) {
				case DocTypes.DOC_TYPE_REGISTRATION:
					_docType.setText("Регистрация");
					break;
				case DocTypes.DOC_TYPE_OPENWD:
					_docType.setText("Открытие смены");
					break;
				case DocTypes.DOC_TYPE_CLOSEWD:
					_docType.setText("Закрытие смены");
					break;
				case DocTypes.DOC_TYPE_ORDER:
					_docType.setText("Приход/Расход");
					break;
				case DocTypes.DOC_TYPE_CORRECTION:
					_docType.setText("Корекция");
					break;
				case DocTypes.DOC_TYPE_CLOSEFN:
					_docType.setText("Закрытие ФН");
					break;
				case DocTypes.DOC_TYPE_REPORT:
					_docType.setText("Отчет о расчетах");
					break;
				}
			}

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Documents.this,DocumentView.class).putExtra("BLOB", BLOB).putExtra("Type", _type);
				startActivity(i);
			}
		}

		private Cursor _c;
		private int _count;
		private int _ft = 0;
		private String _no = "";

		private DocumentsAdapter() {
			_c = getContentResolver().query(URI, null, null, null, null);
			doCount();
		}

		@Override
		public int getCount() {
			return _count;
		}

		@Override
		public Cursor getItem(int position) {
			if (_c.moveToFirst())
				do {
					if (checkFilters()) {
						if (position == 0)
							return _c;
						position--;
					}
				} while (_c.moveToNext());
			return null;

		}

		@Override
		public long getItemId(int position) {
			Cursor c = getItem(position);
			return c == null ? 0 : c.getInt(1);
		}

		@Override
		public View getView(int position, View v, ViewGroup vg) {
			if (v == null) {
				v = getLayoutInflater().inflate(R.layout.doc_row, vg, false);
				new VH(v);
			}
			((VH) v.getTag()).update(getItem(position));
			return v;
		}

		private boolean checkFilters() {
			if (_ft > 0)
				if (_c.getInt(2) != _ft)
					return false;
			if (_no.isEmpty())
				return true;
			return String.valueOf(_c.getInt(1)).startsWith(_no);
		}

		private void doCount() {
			_count = 0;
			if (_c.moveToFirst())
				do {
					if (checkFilters())
						_count++;
				} while (_c.moveToNext());

		}

		private void refresh() {
			doCount();
			notifyDataSetInvalidated();
		}

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
			_ft = position;
			refresh();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			_ft = 0;
			refresh();
		}

		@Override
		public void afterTextChanged(Editable e) {
			_no = e.toString().trim();
			refresh();

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub

		}

	}

	private DocumentsAdapter _documents;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.documents);
		_documents =  new DocumentsAdapter();
		((ListView) findViewById(R.id.lv_documents)).setAdapter(_documents);
		Spinner sp = (Spinner) findViewById(R.id.sp_types);
		sp.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
				new String[] { "Все", "Регистрация", "Откр. смены", "Закр. смены", "Прих/Расх", "Коррекции" }));
		sp.setOnItemSelectedListener(_documents);
		((TextView) findViewById(R.id.ed_doc_no)).addTextChangedListener(_documents);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.log, menu);
//		return true;
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
/*		final ProgressDialog dlg = new ProgressDialog(this);
		dlg.setIndeterminate(true);
		dlg.setCancelable(false);
		dlg.setCanceledOnTouchOutside(false);
		dlg.show();
		new AsyncTask<Void, Void, Exception>() {
			@Override
			protected Exception doInBackground(Void... params) {
				Cursor c = Core.getInstance().db().getDocuments();
				File dir = new File(Environment.getExternalStorageDirectory(), "FNCore");
				File f = new File(dir, "doc.log");
				try {
					DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
					if (c.moveToFirst())
						do {
							byte[] b = c.getBlob(3);
							dos.writeInt(b.length);
							dos.write(b);
						} while (c.moveToNext());
					dos.close();
				} catch (IOException ioe) {
					return ioe;
				}
				c.close();
				FileInputStream fin;
				int read;
				byte [] b = new byte[8192];
				try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ZipOutputStream zs = new ZipOutputStream(bos);
					
					f = new File(dir,"FN.log");
					if(f.exists()) {
						zs.putNextEntry(new ZipEntry("FN.log"));
						fin = new FileInputStream(f);
						while((read = fin.read(b)) > 0) 
							zs.write(b,0,read);
						zs.closeEntry();
					}

					f = new File(dir,"cDOC.log");
					if(f.exists()) {
						zs.putNextEntry(new ZipEntry("cDOC.log"));
						fin = new FileInputStream(f);
						while((read = fin.read(b)) > 0) 
							zs.write(b,0,read);
						zs.closeEntry();
					}
					f = new File(dir,"doc.log");
					if(f.exists()) {
						zs.putNextEntry(new ZipEntry("doc.log"));
						fin = new FileInputStream(f);
						while((read = fin.read(b)) > 0) 
							zs.write(b,0,read);
						zs.closeEntry();
					}
					zs.close();
					FTPClient ftp = new FTPClient();
					ftp.connect("dev.rightscan.ru", 21);
					ftp.login("log", "log123log");
					ftp.setFileTransferMode(FTPClient.BLOCK_TRANSFER_MODE);
					ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
					OutputStream os = ftp.storeFileStream(String.valueOf(System.currentTimeMillis())+".lz");
					ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
					while((read = bis.read(b)) > 0)
						os.write(b,0,read);
					os.close();
					bis.close();
					ftp.disconnect();	
				} catch(IOException ioe) {
					return ioe;
				}
				return null;
				
			}
			@Override
			protected void onPostExecute(Exception result) {
				dlg.dismiss();
				if(result == null) 
					Toast.makeText(Documents.this, "Успешно отправлено", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(Documents.this, "Ошибка "+result.getMessage(), Toast.LENGTH_LONG).show();
			};
		}.execute();*/
		return true;
	}

}
