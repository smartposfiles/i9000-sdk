package rs.fn.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.ViewGroup;
import rs.fn.AppCore;

public class MessageDialog implements OnClickListener, OnDismissListener  {
	private Context _context;
	private int [] _messages;
	private View _view;
	public MessageDialog(Context context) {
		_context = context;
				
	}
	public void show(String title, String message, String [] buttons, int [] messages) {
		_view = null;
		AlertDialog.Builder b = new AlertDialog.Builder(_context);
		b.setTitle(title);
		b.setMessage(message);
		_messages = messages;
		for(int i=0;i<messages.length;i++) {
			switch(i-3) {
			case DialogInterface.BUTTON_POSITIVE:
				b.setPositiveButton(buttons[i], this);
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				b.setNeutralButton(buttons[i], this);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				b.setNegativeButton(buttons[i], this);
				break;
			}
		}
		b.setCancelable(false);
		b.show();
	}
	public void show(String title, View v, String [] buttons, int [] messages) {
		_view = v;
		
		AlertDialog.Builder b = new AlertDialog.Builder(_context);
		b.setTitle(title);
		b.setView(v);
		_messages = messages;
		for(int i=0;i<messages.length;i++) {
			switch(i-3) {
			case DialogInterface.BUTTON_POSITIVE:
				b.setPositiveButton(buttons[i], this);
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				b.setNeutralButton(buttons[i], this);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				b.setNegativeButton(buttons[i], this);
				break;
			}
		}
		b.setCancelable(false);
		AlertDialog dlg = b.create();
		dlg.setOnDismissListener(this);
		dlg.show();
	}
	@Override
	public void onClick(DialogInterface paramDialogInterface, int button) {
		AppCore.getInstance().broadcastLocalMessage(_messages[button+3],_view);
	}
	@Override
	public void onDismiss(DialogInterface arg0) {
		if(_view != null) 
			((ViewGroup)_view.getParent()).removeView(_view);
	}

	

}
