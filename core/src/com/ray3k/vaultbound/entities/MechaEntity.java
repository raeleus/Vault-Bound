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
import com.ray3k.vaultbound.states.GameState;

public class MechaEntity extends SpineEntity {
    private boolean stepping;
    
    public MechaEntity() {
        super(Core.DATA_PATH + "/spine/mecha.json", "walking");
    }

    @Override
    public void actSub(float delta) {
        if (stepping) {
            setMotion(60.0f / 300.0f * BuildingManagerEntity.getInstance().getRunSpeed() + 200.0f, 180.0f);
        } else {
            setMotion(60.0f / 300.0f * BuildingManagerEntity.getInstance().getRunSpeed(), 180.0f);
        }
        
        if (getX() < -600.0f) {
            dispose();
        }
    }

    @Override
    public void drawSub(SpriteBatch spriteBatch, float delta) {
    }

    @Override
    public void create() {
        boolean stepping = false;
        getAnimationState().addListener(new AnimationState.AnimationStateAdapter() {
            @Override
            public void event(AnimationState.TrackEntry entry, Event event) {
                if (MechaEntity.this.getAnimationState().getCurrent(0).getAnimation().getName().equals("walking")) {
                    if (event.getData().getName().equals("step")) {
                        MechaEntity.this.stepping = true;
                    } else if (event.getData().getName().equals("stop")) {
                        MechaEntity.this.stepping = false;
                    }
                } else {
                    if (event.getData().getName().equals("blast")) {
                        GameState.inst().playBlast();
                    }
                }
            }

            @Override
            public void complete(AnimationState.TrackEntry entry) {
                if (MechaEntity.this.getAnimationState().getCurrent(0).getAnimation().getName().equals("walking")) {
                    if (MathUtils.randomBoolean(.25f)) {
                        MechaEntity.this.stepping = false;
                        MechaEntity.this.getAnimationState().setAnimation(0, "blasting", false);
                    }
                } else {
                    MechaEntity.this.getAnimationState().setAnimation(0, "walking", true);
                }
            }
        });
        setDepth(200);
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

}
