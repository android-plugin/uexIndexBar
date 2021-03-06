package com.zywx.uexindexbar;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;

import com.zywx.uexindexbar.MyLetterListView.OnTouchingLetterChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;

public class uexIndexBar extends EUExBase {

	static final String func_callback = "uexIndexBar.onTouchResult";
    static final String FUNC_ON_INDEX_CLICK_CALLBACK = "uexIndexBar.onIndexClick";
    private static final String letterListViewTag = "letterListViewTag";
	public static float density;
	private MyLetterListView letterListView;
    private final String[] defaultLetters={ "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z" };
	//用来判断indexBar是否会滚动，如果值为true,会将其嵌入到WebView中, indexBar会跟随网页滚动，如果为false,嵌入到window中,indexBar不会滚动。
    private boolean isScrollable = false;
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
                if (null != letterListView) {
                    removeLetterListerView();
                }
                String inX = parm[0];
                String inY = parm[1];
                String inW = parm[2];
                String inH = parm[3];
                String jsonStr = null;
                if (parm.length > 4) {
                    jsonStr = parm[4];
                }
                int x = 0;
                int y = 0;
                int w = 0;
                int h = -1;
                String color = "#000000";
                String[] indices = null;
                try {
                    x = Integer.parseInt(inX);
                    y = Integer.parseInt(inY);
                    w = Integer.parseInt(inW);
                    h = Integer.parseInt(inH);
                    if (!TextUtils.isEmpty(jsonStr)) {
                        JSONObject jsonObject = new JSONObject(jsonStr);
                        if (jsonObject.has("indices")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("indices");
                            if (jsonArray != null) {
                                indices = new String[jsonArray.length()];
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    indices[i] = String.valueOf(jsonArray.get(i));
                                }
                            }
                        }
                        color = jsonObject.optString("textColor", "#000000");
                        isScrollable = jsonObject.optBoolean("isScrollable", false);
                    }
                    if (indices == null) {
                        indices = defaultLetters;
                    }
                } catch (Exception e) {
                    return;
                }
                WindowManager windowManager = ((Activity) mContext)
                        .getWindowManager();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                density = displayMetrics.density;
                letterListView = new MyLetterListView(mContext, indices, BUtility.parseColor(color));
                letterListView
                        .setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

                            @Override
                            public void onTouchingLetterChanged(String s) {
                                myClassCallBack(s);
                            }
                        });
                if (isScrollable) {
                    AbsoluteLayout.LayoutParams layoutParams = new AbsoluteLayout.LayoutParams(w, h, x, y);
                    addViewToWebView(letterListView, layoutParams, letterListViewTag);
                } else {
                    RelativeLayout.LayoutParams lparm = new RelativeLayout.LayoutParams(
                            w, h);
                    lparm.leftMargin = x;
                    lparm.topMargin = y;
                    addViewToCurrentWindow(letterListView, lparm);
                }


            }
        });

	}

	protected void myClassCallBack(String result) {
		jsCallback(func_callback, 0, EUExCallback.F_C_TEXT, result);
        callBackPluginJs(FUNC_ON_INDEX_CLICK_CALLBACK, result);
	}
    private void callBackPluginJs(String methodName, String jsonData) {
        String js = SCRIPT_HEADER + "if(" + methodName + "){"
                + methodName + "('" + jsonData + "');}";
        onCallback(js);
    }
	// this case remove a custom view from window
	public void close(String[] parm) {
		if (null != letterListView) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    removeLetterListerView();
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
                    removeLetterListerView();
                }
            });
        }
		return true;
	}
    private void removeLetterListerView() {
        if(isScrollable) {
            removeViewFromWebView(letterListViewTag);
        } else {
            removeViewFromCurrentWindow(letterListView);
        }
        letterListView = null;
    }
}