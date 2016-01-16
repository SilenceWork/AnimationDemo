package letv.android.com.animationdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class FlowerAnimation extends View {

    private HeartAnimationHelper mAnimtionHelper = new HeartAnimationHelper();

    public FlowerAnimation(Context context) {
        super(context);
    }

    public FlowerAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowerAnimation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mAnimtionHelper.onTouchEventGif(this, event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mAnimtionHelper.drawGif(canvas,this);
        super.onDraw(canvas);
    }
}