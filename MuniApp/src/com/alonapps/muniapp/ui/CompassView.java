package com.alonapps.muniapp.ui;

import com.alonapps.muniapp.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * This is the View for the compass text. Obsolete. Replaced by a compass layout. 
 * @author alon
 *
 */
public class CompassView extends View
{
	private Paint paint;
	private Paint paintFontInbound;
	private Paint paintFontOutbound;
	private float position = 0;
	private int xLocationInbound;
	private Context mContext;

	public CompassView(Context context)
	{
		super(context);
		this.mContext = context;
		init();
	}
	public CompassView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.mContext = context;
		init();
	}

	private void init()
	{
		getMeasuredWidth();
		xLocationInbound = 0;

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		paint.setTextSize(25);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);

		paintFontInbound = new Paint();
		paintFontInbound.setAntiAlias(true);
		paintFontInbound.setStrokeWidth(4);
		paintFontInbound.setTextSize(115);
		paintFontInbound.setStyle(Paint.Style.FILL);
		paintFontInbound.setColor(Color.argb(255, 100, 200, 100));

		paintFontOutbound = new Paint();
		paintFontOutbound.setAntiAlias(true);
		paintFontOutbound.setStrokeWidth(4);
		paintFontOutbound.setTextSize(110);
		paintFontOutbound.setStyle(Paint.Style.FILL);
		paintFontOutbound.setColor(Color.argb(255, 200, 100, 100));
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawText("Inbound", ((float) getMeasuredWidth() / 180) * xLocationInbound + 30,
				getMeasuredHeight() - 50, paintFontInbound);
		canvas.drawText("Outbound", ((float) getMeasuredWidth() / 180) * xLocationInbound
				- getMeasuredWidth(), getMeasuredHeight() - 50, paintFontOutbound);
		canvas.drawText("Inbound", ((float) getMeasuredWidth() / 180) * xLocationInbound
				+ (2 * getMeasuredWidth()) + 5, getMeasuredHeight() - 50, paintFontInbound);
		canvas.drawText("Outbound", ((float) getMeasuredWidth() / 180) * xLocationInbound
				+ getMeasuredWidth(), getMeasuredHeight() - 50, paintFontOutbound);
		
		
//		ImageView img = new ImageView(this.mContext); //(ImageView)findViewById(R.id.compassArrow);
		//mat.set
//		if(img != null)
//			img.setRotation(20);

	}

	public void updateData(float position)
	{
		this.position = Math.round(position);

		this.xLocationInbound = -(int) position;
		invalidate();
	}

}
