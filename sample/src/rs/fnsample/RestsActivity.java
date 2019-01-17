package rs.fnsample;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import rs.fn.data.SellOrder;

public class RestsActivity extends ListActivity {

	private class RestsAdapter extends BaseAdapter {
		private Uri URI = Uri.parse("content://rs.fncore.data/rests");
		private Cursor _c;
		public RestsAdapter() {
			_c = getContentResolver().query(URI, null, null, null, null);
		}
		@Override
		public int getCount() {
			return _c.getCount();
		}

		@Override
		public Cursor getItem(int position) {
			_c.moveToFirst();
			while(position-- > 0 ) _c.moveToNext();
			return _c;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup vg) {
			if(v == null)  {
				v = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, vg,false);
				((TextView)v).setGravity(Gravity.END);
			}
			
			Cursor c = getItem(position);
			Float s  = c.getFloat(2);
			String value = String.valueOf(s);
			Log.d("FNCORE", "R "+c.getInt(1)+" "+s);
			if(c.getInt(1) == SellOrder.TYPE_INCOME || c.getInt(1) == SellOrder.TYPE_RETURN_OUTCOME)  
				value = "+"+value;
			else
				value = "-"+value;
			((TextView)v).setText(value);
			return v;
		}
		
	}
	
	public RestsActivity() {
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setListAdapter(new RestsAdapter());
	}

}
