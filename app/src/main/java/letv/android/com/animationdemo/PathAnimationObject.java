package letv.android.com.animationdemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;

import java.lang.ref.SoftReference;

/**
 * Created by mayongsheng on 16/yanhua1/12.
 */
public class PathAnimationObject {
    private int mCachedKey;
    private SoftReference<Bitmap> mCachedBitmap;
    private Path mPath;
    //pathmeasure can get the current position of path
    private PathMeasure pathMeasure = null;
    //use this paint to set the alpha value of bitmap
    private Paint mPaint;
    //animation time has passed in percentage[0.0~yanhua1.0]
    private float offset = 0.0f;
    private float positionX;
    private float positionY;
    private long length;

    public PathAnimationObject() {
    }

    public void setLength(long length){
        this.length =length;
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public int getKey() {
        return mCachedKey;
    }

    public void setKey(int resourceId) {
        this.mCachedKey = resourceId;
    }

    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        this.mPath = path;
        if (pathMeasure == null) {
            pathMeasure = new PathMeasure();
        }
    }

    public float getOffset() {
        return this.offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    private Bitmap getBitmap() {
        if (mCachedBitmap != null && mCachedBitmap.get() != null) {
            return mCachedBitmap.get();
        }

        return null;
    }

    public void setBitmap(Bitmap bitmap){
        mCachedBitmap = new SoftReference<Bitmap>(bitmap);
    }

    public void drawSelf(Canvas canvas) {
        if (pathMeasure == null||getBitmap()==null) {
            return;
        }

        float[] pos = new float[2];
        pathMeasure.setPath(getPath(), false);
        pathMeasure.getPosTan(pathMeasure.getLength() * getOffset(), pos, null);

        if (mPaint == null) {
            mPaint = new Paint();
        }
        mPaint.setAlpha(Math.round(255 * (1 - getOffset())));

        canvas.drawBitmap(getBitmap(), pos[0], pos[1], mPaint);
    }
}
