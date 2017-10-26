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

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.ray3k.vaultbound.Entity;
import com.ray3k.vaultbound.states.GameState;

public class BackgroundBuildingEntity extends Entity {
    private NinePatch patch;
    private Type type;
    public static enum Type {
        FORE, AFT
    }
    
    public BackgroundBuildingEntity(Type type) {
        super();
        
        Array<String> namesFore = new Array<String>(new String[]{"building-1", "building-2", "building-3", "building-4", "building-5"});
        Array<String> namesAft = new Array<String>(new String[]{"building-small-1", "building-small-2", "building-small-3", "building-small-4", "building-small-5"});
        
        this.type = type;
        
        if (type == Type.FORE) {
            patch = GameState.spineAtlas.createPatch(namesFore.random());
        } else {
            patch = GameState.spineAtlas.createPatch(namesAft.random());
        }
    }
    
    @Override
    public void create() {
        
    }

    @Override
    public void act(float delta) {
        if (type == Type.FORE) {
            setMotion(100.0f / 300.0f * BuildingManagerEntity.getInstance().getRunSpeed(), 180.0f);
        } else {
            setMotion(60.0f / 300.0f * BuildingManagerEntity.getInstance().getRunSpeed(), 180.0f);
        }
    }

    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        patch.draw(spriteBatch, getX(), 0.0f, patch.getTotalWidth(), getY());
    }

    @Override
    public void destroy() {
    }

    @Override
    public void collision(Entity other) {
    }

    public NinePatch getPatch() {
        return patch;
    }

    public Type getType() {
        return type;
    }
}
