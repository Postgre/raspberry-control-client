/*
 * Copyright 2012 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skalski.raspberrycontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class Custom_SpotlightView extends View {
	private int mTargetId;

	private Bitmap mMask;
	private float mMaskX;
	private float mMaskY;
	private float mMaskScale;
	private Matrix mShaderMatrix = new Matrix();

	private Bitmap mTargetBitmap;
	private final Paint mPaint = new Paint();

	private AnimationSetupCallback mCallback;

	public interface AnimationSetupCallback {
		void onSetupAnimation(Custom_SpotlightView spotlight);
	}

	public Custom_SpotlightView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs,
                                                      R.styleable.Custom_SpotlightView, 0, 0);
		try {
			mTargetId = a.getResourceId(R.styleable.Custom_SpotlightView_target, 0);

			int maskId = a.getResourceId(R.styleable.Custom_SpotlightView_mask, 0);
			mMask = convertToAlphaMask(BitmapFactory.decodeResource(getResources(), maskId));
		} catch (Exception e) {
		} finally {
			a.recycle();
		}
	}

	public float getMaskScale() {
        return mMaskScale;
	}

	public void setMaskScale(float maskScale) {
		mMaskScale = maskScale;
		invalidate();
	}

	public float getMaskX() {
		return mMaskX;
	}

	public void setMaskX(float maskX) {
		mMaskX = maskX;
		invalidate();
	}

	public float getMaskY() {
        return mMaskY;
	}

	public void setMaskY(float maskY) {
		mMaskY = maskY;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float maskW = mMask.getWidth() / 2.0f;
		float maskH = mMask.getHeight() / 2.0f;

		float x = mMaskX - maskW * mMaskScale;
		float y = mMaskY - maskH * mMaskScale;

		mShaderMatrix.setScale(1.0f / mMaskScale, 1.0f / mMaskScale);
		mShaderMatrix.preTranslate(-x, -y);

		mPaint.getShader().setLocalMatrix(mShaderMatrix);
		
		canvas.translate(x, y);
		canvas.scale(mMaskScale, mMaskScale);
		canvas.drawBitmap(mMask, 0.0f, 0.0f, mPaint);
	}

	public void setAnimationSetupCallback(AnimationSetupCallback callback) {
		mCallback = callback;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				createShader();
				setMaskScale(1.0f);

				if (mCallback != null) {
					mCallback.onSetupAnimation(Custom_SpotlightView.this);
				}

				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}

	private void createShader() {
		View target = getRootView().findViewById(mTargetId);
		mTargetBitmap = createBitmap(target);
		Shader targetShader = createShader(mTargetBitmap);
		mPaint.setShader(targetShader);
	}

	private static Shader createShader(Bitmap b) {
		return new BitmapShader(b, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
	}

	private static Bitmap createBitmap(View target) {
		Bitmap b = Bitmap.createBitmap(target.getWidth(),
                                       target.getHeight(),
                                       Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		target.draw(c);
		return b;
	}

	private static Bitmap convertToAlphaMask(Bitmap b) {
		Bitmap a = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ALPHA_8);
		Canvas c = new Canvas(a);
		c.drawBitmap(b, 0.0f, 0.0f, null);
		return a;
	}

	public float computeMaskScale(float d) {
		return d / (float) mMask.getHeight();
	}
}