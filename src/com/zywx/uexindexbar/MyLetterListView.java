package com.zywx.uexindexbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

public class MyLetterListView extends View {

	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	private String[] letters =null;
	private int choose = -1;
	private Paint paint = new Paint();
	private boolean showBkg = false;
    private int letterColor=0;

	public MyLetterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyLetterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLetterListView(Context context,String[] letters,int letterColor) {
		super(context);
        this.letters=letters;
        this.letterColor=letterColor;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        if (letters==null){
            return;
        }
		if (showBkg) {
			this.setBackgroundResource(EUExUtil
					.getResDrawableID("plug_in_index_bar_bg"));
		} else {
			this.setBackgroundColor(Color.TRANSPARENT);
		}

		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / letters.length;
		for (int i = 0; i < letters.length; i++) {
			paint.setColor(letterColor);
			paint.setAntiAlias(true);
			paint.setTextSize(Utils.sp2px(14, uexIndexBar.density));
			float xPos = width / 2 - paint.measureText(letters[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(letters[i], xPos, yPos, paint);
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * letters.length);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < letters.length) {
					listener.onTouchingLetterChanged(letters[c]);
					choose = c;
					invalidate();
				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			showBkg = false;
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < letters.length) {
					listener.onTouchingLetterChanged(letters[c]);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			showBkg = false;
			choose = -1;
			invalidate();
			break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}
}