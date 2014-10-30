/*
 * Copyright 2013 Flavien Laurent
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

package com.skalski.raspberrycontrol.ActionBar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/*
 * Created by f.laurent on 21/11/13.
 * antoine-merle.com inspiration
 */
public class ActionBar_ParallaxImageView extends ImageView {

    private int mCurrentTranslation;

    public ActionBar_ParallaxImageView(Context context) {
        super(context);
    }

    public ActionBar_ParallaxImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionBar_ParallaxImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCurrentTranslation(int currentTranslation) {
        mCurrentTranslation = currentTranslation;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(0, -mCurrentTranslation / 2);
        super.draw(canvas);
        canvas.restore();
    }
}