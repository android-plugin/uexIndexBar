package com.zywx.uexindexbar;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;

import com.zywx.uexindexbar.MyLetterListView.OnTouchingLetterChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;

public class uexIndexBar extends EUExBase {

	static final String func_callback = "uexIndexBar.onTouchResult";
    private static final String letterListViewTag = "letterListViewTag";
	public static float density;
	private MyLetterListView letterListView;
    private final String[] defaultLetters={ "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z" };
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
                String jsonStr=null;
                if (parm.length>4) {
                    jsonStr = parm[4];
                }
				int x = 0;
				int y = 0;
				int w = 0;
				int h = 0;
                String color="#000000";
                String[] indices=null;
                try {
					x = Integer.parseInt(inX);
					y = Integer.parseInt(inY);
					w = Integer.parseInt(inW);
					h = Integer.parseInt(inH);
                    if (!TextUtils.isEmpty(jsonStr)) {
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        JSONArray jsonArray=jsonObject.getJSONArray("indices");
                        if (jsonArray!=null){
                            indices=new String[jsonArray.length()];
                            for (int i=0;i<jsonArray.length();i++){
                                indices[i]= String.valueOf(jsonArray.get(i));
                            }
                        }
                        color=jsonObject.optString("textColor","#000000");
                    }
                    if (indices==null){
                        indices=defaultLetters;
                    }
				} catch (Exception e) {
					return;
				}
				WindowManager windowManager = ((Activity) mContext)
						.getWindowManager();
				DisplayMetrics displayMetrics = new DisplayMetrics();
				windowManager.getDefaultDisplay().getMetrics(displayMetrics);
				density = displayMetrics.density;
				letterListView = new MyLetterListView(mContext,indices,BUtility.parseColor(color));
				letterListView
						.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

							@Override
							public void onTouchingLetterChanged(String s) {
								myClassCallBack(s);
							}
						});
                AbsoluteLayout.LayoutParams layoutParams = new AbsoluteLayout.LayoutParams(w, h, x, y);
                removeViewFromWebView(letterListViewTag);
				addViewToWebView(letterListView, layoutParams, letterListViewTag);
			}
		});

	}

	protected void myClassCallBack(String result) {
		jsCallback(func_callback, 0, EUExCallback.F_C_TEXT, result);
	}

	// this case remove a custom view from window
	public void close(String[] parm) {
		if (null != letterListView) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    removeViewFromWebView(letterListViewTag);
                }
            });
        }
    }

    @Override
	protected boolean clean() {
		if (null != letterListView) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    removeViewFromWebView(letterListViewTag);
                    letterListView = null;
                }
            });
        }
		return true;
	}

}