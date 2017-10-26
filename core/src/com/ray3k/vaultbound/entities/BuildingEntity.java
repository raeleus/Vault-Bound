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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.ray3k.vaultbound.Entity;
import com.ray3k.vaultbound.states.GameState;

public class BuildingEntity extends Entity {
    private TiledDrawable left, middle, topMiddle, right;
    private TextureRegion topLeft, topRight;
    private int type;
    private float width;
    
    public BuildingEntity(int type) {
        super();
        this.type = type;
    }
    
    @Override
    public void create() {
        if (type == 0) {
            topLeft = GameState.spineAtlas.findRegion("platform1-top-left");
            topMiddle = new TiledDrawable(GameState.spineAtlas.findRegion("platform1-top"));
            topRight = GameState.spineAtlas.findRegion("platform1-top-right");
            left = new TiledDrawable(GameState.spineAtlas.findRegion("platform1-left"));
            middle = new TiledDrawable(GameState.spineAtlas.findRegion("platform1-middle"));
            right = new TiledDrawable(GameState.spineAtlas.findRegion("platform1-right"));
        } else if (type == 1) {
            topLeft = GameState.spineAtlas.findRegion("platform2-top-left");
            topMiddle = new TiledDrawable(GameState.spineAtlas.findRegion("platform2-top"));
            topRight = GameState.spineAtlas.findRegion("platform2-top-right");
            left = new TiledDrawable(GameState.spineAtlas.findRegion("platform2-left"));
            middle = new TiledDrawable(GameState.spineAtlas.findRegion("platform2-middle"));
            right = new TiledDrawable(GameState.spineAtlas.findRegion("platform2-right"));
        } else if (type == 2) {
            topLeft = GameState.spineAtlas.findRegion("platform3-top-left");
            topMiddle = new TiledDrawable(GameState.spineAtlas.findRegion("platform3-top"));
            topRight = GameState.spineAtlas.findRegion("platform3-top-right");
            left = new TiledDrawable(GameState.spineAtlas.findRegion("platform3-left"));
            middle = new TiledDrawable(GameState.spineAtlas.findRegion("platform3-middle"));
            right = new TiledDrawable(GameState.spineAtlas.findRegion("platform3-right"));
        }
        
        float widthMinRequired = left.getRegion().getRegionWidth() + right.getRegion().getRegionWidth();
//        float widthMin;
//        if (BuildingManagerEntity.getInstance().getRunSpeed() < 500) widthMin = 500.0f;
//        else if (BuildingManagerEntity.getInstance().getRunSpeed() < 700) widthMin = 600.0f;
//        else widthMin = 700.0f;
        width = widthMinRequired + MathUtils.ceil(MathUtils.random(500, 1900.0f) / middle.getRegion().getRegionWidth()) * middle.getRegion().getRegionWidth();
    }

    @Override
    public void act(float delta) {
        setMotion(BuildingManagerEntity.getInstance().getRunSpeed(), 180.0f);
        if (getX() + width < 0.0f) {
            dispose();
        }
    }

    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        spriteBatch.draw(topLeft, getX(), getY() - topLeft.getRegionHeight());
        float height = MathUtils.ceil((getY() - topLeft.getRegionHeight()) / left.getRegion().getRegionHeight()) * left.getRegion().getRegionHeight();
        left.draw(spriteBatch, getX(), getY() - topLeft.getRegionHeight() - height, left.getRegion().getRegionWidth(), height);
        topMiddle.draw(spriteBatch, getX() + topLeft.getRegionWidth(), getY() - topMiddle.getRegion().getRegionHeight(), width - topLeft.getRegionWidth() - topRight.getRegionWidth(), topMiddle.getRegion().getRegionHeight());
        spriteBatch.draw(topRight, getX() + width - topRight.getRegionWidth(), getY() - topRight.getRegionHeight());
        height = MathUtils.ceil((getY() - topRight.getRegionHeight()) / right.getRegion().getRegionHeight()) * right.getRegion().getRegionHeight();
        right.draw(spriteBatch, getX() + width - topRight.getRegionWidth(), getY() - topRight.getRegionHeight() - height, right.getRegion().getRegionWidth(), height);
        height = MathUtils.ceil((getY() - topMiddle.getRegion().getRegionHeight()) / middle.getRegion().getRegionHeight()) * middle.getRegion().getRegionHeight();
        middle.draw(spriteBatch, getX() + left.getRegion().getRegionWidth(), getY() - topMiddle.getRegion().getRegionHeight() - height, width - left.getRegion().getRegionWidth() - right.getRegion().getRegionWidth(), height);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void collision(Entity other) {
    }
    
    public float getWidth() {
        return width;
    }
}
