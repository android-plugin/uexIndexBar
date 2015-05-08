package com.zywx.uexindexbar;

import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.zywx.uexindexbar.MyLetterListView.OnTouchingLetterChangedListener;

public class uexIndexBar extends EUExBase {

	static final String func_callback = "uexIndexBar.onTouchResult";

	public static float density;
	private MyLetterListView letterListView;

	public uexIndexBar(Context context, EBrowserView view) {
		super(context, view);
	}

	public void open(final String[] parm) {
		if (parm.length < 4) {
			return;
		}

		((Activity) mContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				String inX = parm[0];
				String inY = parm[1];
				String inW = parm[2];
				String inH = parm[3];

				int x = 0;
				int y = 0;
				int w = 0;
				int h = 0;
				try {
					x = Integer.parseInt(inX);
					y = Integer.parseInt(inY);
					w = Integer.parseInt(inW);
					h = Integer.parseInt(inH);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				WindowManager windowManager = ((Activity) mContext)
						.getWindowManager();
				DisplayMetrics displayMetrics = new DisplayMetrics();
				windowManager.getDefaultDisplay().getMetrics(displayMetrics);
				density = displayMetrics.density;

				letterListView = new MyLetterListView(mContext);
				letterListView
						.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

							@Override
							public void onTouchingLetterChanged(String s) {
								myClassCallBack(s);
							}
						});
				RelativeLayout.LayoutParams lparm = new RelativeLayout.LayoutParams(
						w, h);
				lparm.leftMargin = x;
				lparm.topMargin = y;
				removeViewFromCurrentWindow(letterListView);
				addViewToCurrentWindow(letterListView, lparm);
			}
		});

	}

	protected void myClassCallBack(String result) {
		jsCallback(func_callback, 0, EUExCallback.F_C_TEXT, result);
	}

	// this case remove a custom view from window
	public void close(String[] parm) {
		if (null != letterListView) {
			removeViewFromCurrentWindow(letterListView);
		}
	}

	@Override
	protected boolean clean() {
		if (null != letterListView) {
			removeViewFromCurrentWindow(letterListView);
			letterListView = null;
		}
		return true;
	}

}