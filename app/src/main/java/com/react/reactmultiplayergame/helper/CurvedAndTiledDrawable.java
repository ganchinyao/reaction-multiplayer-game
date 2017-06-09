package com.react.reactmultiplayergame.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.react.reactmultiplayergame.R;

/**
 * Created by gan on 29/4/17.
 * <p>
 * Used by gameContent whereby we are setting custom bitmap in the gameContent background.
 * This class provides a way to curve the bitmap background, and tiled it to repeat mode.
 */

public class CurvedAndTiledDrawable extends Drawable {

    private final float mCornerRadius;
    private final RectF mRect = new RectF();
    private final BitmapShader mBitmapShader;
    private final Paint mTilePaint;

    public CurvedAndTiledDrawable(Context context, Bitmap bitmap) {
        // here the radius is exactly the same as the radius that is used in the gameContent middle rectangle rounded corner.
        // hence if that radius is changed, change this radius too
        mCornerRadius = context.getResources().getDimension(R.dimen._20sdp);

        mBitmapShader = new BitmapShader(bitmap,
                Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        mTilePaint = new Paint();
        mTilePaint.setAntiAlias(true);
        mTilePaint.setShader(mBitmapShader);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRect.set(0, 0, bounds.width(), bounds.height());
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mTilePaint);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mTilePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mTilePaint.setColorFilter(cf);
    }

}