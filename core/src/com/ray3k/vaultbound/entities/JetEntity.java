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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;
import com.ray3k.vaultbound.Core;
import com.ray3k.vaultbound.Entity;
import com.ray3k.vaultbound.SpineEntity;
import com.ray3k.vaultbound.states.GameState;

public class JetEntity extends SpineEntity {
    private final static Vector2 offset = new Vector2();
    private final static Vector2 size = new Vector2();
    private final static FloatArray temp = new FloatArray();
    
    public JetEntity() {
        super(Core.DATA_PATH + "/spine/jet.json", "animation");
    }

    @Override
    public void create() {
        setX(Gdx.graphics.getWidth());
        setY(MathUtils.random(Gdx.graphics.getHeight() / 2.0f, Gdx.graphics.getHeight()));
        setMotion(1600.0f, 180.0f);
        GameState.inst().playJet();
        setDepth(-200);
    }

    @Override
    public void actSub(float delta) {
        getSkeleton().getBounds(offset, size, temp);
        if (getX() + size.x < 0) {
            dispose();
        }
    }

    @Override
    public void drawSub(SpriteBatch spriteBatch, float delta) {
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
