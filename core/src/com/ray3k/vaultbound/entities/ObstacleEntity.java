/*
 * The MIT License
 *
 * Copyright 2017 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ray3k.vaultbound.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.ray3k.vaultbound.Core;
import com.ray3k.vaultbound.Entity;
import com.ray3k.vaultbound.SpineEntity;

public class ObstacleEntity extends SpineEntity {
    private boolean knockedOver;
    @Override
    public void actSub(float delta) {
        setMotion(BuildingManagerEntity.getInstance().getRunSpeed(), 180.0f);
    }

    @Override
    public void drawSub(SpriteBatch spriteBatch, float delta) {
    }

    @Override
    public void create() {
        setSkeletonData(Core.DATA_PATH + "/spine/obstacle.json", "standing");
        switch (MathUtils.random(2)) {
            case 0:
                getSkeleton().setSkin("ac-unit");
                break;
            case 1:
                getSkeleton().setSkin("cone");
                break;
            case 2:
                getSkeleton().setSkin("crate");
                break;
        }
        
        getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
                if (event.getData().getName().equals("dispose")) {
                    ObstacleEntity.this.dispose();
                }
            }
            
        });
        
        knockedOver = false;
    }

    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void collision(Entity other) {
    }
    
    public void knockOver() {
        if (!knockedOver) {
            getAnimationState().setAnimation(0, "destroy", false);
            knockedOver = true;
        }
    }
}
