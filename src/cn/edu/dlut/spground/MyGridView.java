package cn.edu.dlut.spground;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class MyGridView extends GridView{

	public MyGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public MyGridView(Context paramContext, AttributeSet paramAttributeSet)

	{

	 super(paramContext, paramAttributeSet);

	}
	public MyGridView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST); 
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
