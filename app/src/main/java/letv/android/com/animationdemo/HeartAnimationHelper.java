package letv.android.com.animationdemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by mayongsheng on 16/yanhua1/12.
 */
public class HeartAnimationHelper {
    private static int misscount=0;
    private static int hitcount =0;
    private static final int DEFAULT_SIZE = 30;
    private static final long DEFAULT_DURATION = 1500;
    private List<PathAnimationObject> mListObjects = new ArrayList<>();
    //this cache can avoid recreate bitmap which expensive in memory use.
    private SparseArray<SoftReference<Bitmap>> mCachedBitmap = new SparseArray<>();
    private int[] resources = new int[]{R.drawable.dit,R.drawable.diu,R.drawable.diw,R.drawable.dix,R.drawable.diy};
    private int mSize = DEFAULT_SIZE;
    private long mDuration = DEFAULT_DURATION;


    private int[] gifResource = new int[]{R.drawable.yanhua1, R.drawable.yanhua2, R.drawable.yanhua3, R.drawable.yanhua4,
            R.drawable.yanhua5, R.drawable.yanhua6, R.drawable.yanhua7, R.drawable.yanhua8, R.drawable.yanhua9, R.drawable.yanhua10, R.drawable.yanhua11};
    private long gifDuration = gifResource.length*140;
    private float gifOffset;

    public HeartAnimationHelper setHeartSize(int width, int height) {
        this.mSize = Math.min(width, height);
        return this;
    }

    public HeartAnimationHelper setDuration(long duration){
        this.mDuration = duration;
        return this;
    }

    /**
     * called in view`s onTouchEvent(MotionEvent event) to get the animation start coordinate
     * animation invoked here
     * @param parent
     * @param event
     */
    public void onTouchEvent(View parent, MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            startAnimationAtPosition(parent, event.getX(), event.getY());
        }
    }

    /**
     *  called in view`s onDraw(Canvas canvas) to draw the heart
     *
     * @param canvas
     */
    public void draw(Canvas canvas) {
        if (mListObjects != null && mListObjects.size() > 0) {
            final int length = mListObjects.size();
            for (int index = 0; index < length; index++) {
                mListObjects.get(index).drawSelf(canvas);
            }
        }
    }

    private float touchX;
    private float touchY;

    public void onTouchEventGif(View parent, MotionEvent event) {
        if(parent.getTag()!=null){
            Animator animator = (Animator) parent.getTag();
            animator.end();
            animator = null;
        }
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            touchX = event.getX();
            touchY = event.getY();
            startGifAnimation(parent);
        }
    }

    public void drawGif(Canvas canvas,View parent) {
        int index = (int) (gifOffset*gifDuration/140);
        Log.i("mayongsheng","index="+index);
        if(index>=0&&index<gifResource.length){
            int resource = gifResource[index];
            Drawable drawable = parent.getContext().getResources().getDrawable(resource);
            drawable.setBounds((int) touchX, (int) touchY, (int) (touchX + 150), (int) (touchY + 52));
            drawable.draw(canvas);
        }
    }

    private Bitmap getCachedBitmap(Context context,int key){
        if(mCachedBitmap.get(key)!=null&&mCachedBitmap.get(key).get()!=null
                &&!mCachedBitmap.get(key).get().isRecycled()){
            hitcount++;
            return mCachedBitmap.get(key).get();
        }
        misscount++;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), key, options);
        int height = options.outHeight;
        int width = options.outHeight;
        int scaleHeight = height > mSize ? height / mSize : 1;
        int scaleWidth = width > mSize ? width / mSize : 1;
        options.inJustDecodeBounds = false;
        options.inSampleSize = Math.max(scaleHeight, scaleWidth);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), key, options);
        mCachedBitmap.put(key,new SoftReference<Bitmap>(bitmap));
        return bitmap;
    }

    private void startAnimationAtPosition(View container, float x, float y) {
        Path path = createPath(x, y);
        PathAnimationObject object = createHeart(container, x, y, path);
        mListObjects.add(object);
        startAnimation(container, object);
    }

    private PathAnimationObject createHeart(View container, float x, float y, Path path) {
        PathAnimationObject heart = new PathAnimationObject();
        heart.setBitmap(getCachedBitmap(container.getContext(),resources[getRandomResource()]));
        Log.i(HeartAnimationHelper.class.getSimpleName(),"hitcount="+hitcount+" misscount="+misscount);
        heart.setPath(path);
        heart.setPositionX(x);
        heart.setPositionY(y);
        return heart;
    }

    private Path createPath(float x, float y) {
        Path path = new Path();
        Point start = new Point(Math.round(x), Math.round(y));
        Point end = new Point(getRandom(start.x - 100, start.x + 100), getRandom(start.y - 250, start.y - 350));
        drawLinePath(path, start, end);
        return path;
    }

    /**
     * use and random value,maybe this can provide good experince
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(Math.abs(max - min)) + min;
    }

    public int getRandomResource(){
        int length= resources.length;
        return new Random().nextInt(length);
    }
    /**
     * draw an straight line
     *
     * @param path
     * @param start
     * @param end
     */
    private void drawLinePath(Path path, Point start, Point end) {
        path.moveTo(start.x, start.y);
        path.lineTo(end.x, end.y);
    }

    private void startAnimation(final View container, PathAnimationObject object) {
        ObjectAnimator mAnimator1 = ObjectAnimator.ofFloat(object, "offset", 0.0f,
                1.0f);
        mAnimator1.setDuration(mDuration);
        mAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ObjectAnimator animator = (ObjectAnimator) animation;
                PathAnimationObject object = (PathAnimationObject) animator.getTarget();
                object.setOffset(animation.getAnimatedFraction());
                container.postInvalidate();
            }
        });
        mAnimator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator animator = (ObjectAnimator) animation;
                mListObjects.remove(animator.getTarget());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator1.setInterpolator(new DecelerateInterpolator(1f));
        mAnimator1.start();
    }

    private void startGifAnimation(final View container) {
        ObjectAnimator mAnimator1 = ObjectAnimator.ofFloat(this, "gifOffset", 0.0f,
                1.0f);
        container.setTag(mAnimator1);
        mAnimator1.setDuration(gifDuration);
        mAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                gifOffset = animation.getAnimatedFraction();
                container.postInvalidate();
            }
        });
        mAnimator1.setInterpolator(new LinearInterpolator());
        mAnimator1.start();
    }
}
