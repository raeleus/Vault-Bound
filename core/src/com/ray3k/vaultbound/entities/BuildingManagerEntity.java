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
import com.badlogic.gdx.utils.Array;
import com.ray3k.vaultbound.Entity;
import static com.ray3k.vaultbound.states.GameState.entityManager;
import java.util.Iterator;

public class BuildingManagerEntity extends Entity {
    private float gap;
    private static final float GAP_MIN = 75.0f;
    private static final float HEIGHT_DIF = 300.0f;
    private static final float LOWEST_HEIGHT = 100.0f;
    private static final float HIGHEST_HEIGHT = 450.0f;
    private Array<BuildingEntity> buildings;
    private float runSpeed;
    private static BuildingManagerEntity instance;

    public static BuildingManagerEntity getInstance() {
        return instance;
    }
    
    @Override
    public void create() {
        instance = this;
        
        gap = MathUtils.random(GAP_MIN, calculateMaxJump());
        buildings = new Array<BuildingEntity>();
        runSpeed = 300.0f;
        
        createInitial();
    }
    
    private void createInitial() {
        float x = 0.0f;
        
        while (x < Gdx.graphics.getWidth()) {
            BuildingEntity building = spawnBuilding(false);
            building.setX(x);
            
            x += building.getWidth() + gap;
        }
    }
    
    private BuildingEntity spawnBuilding(boolean spawnExtras) {
        BuildingEntity building = new BuildingEntity(MathUtils.random(2));
        entityManager.addEntity(building);
        building.setX(Gdx.graphics.getWidth());
        if (buildings.size > 0) {
            BuildingEntity lastBuilding = buildings.peek();
            building.setY(MathUtils.random(Math.max(lastBuilding.getY() - HEIGHT_DIF, LOWEST_HEIGHT), Math.min(lastBuilding.getY() + HEIGHT_DIF, HIGHEST_HEIGHT)));
        } else {
            building.setY(200.0f);
        }
        buildings.add(building);
        
        if (spawnExtras) {
            if (MathUtils.randomBoolean(.5f) && building.getWidth() > 1000) {
                ObstacleEntity obstacle = new ObstacleEntity();
                obstacle.setPosition(building.getX() + MathUtils.random(100.0f, building.getWidth() - 200.0f), building.getY());
                entityManager.addEntity(obstacle);
            }

            if (MathUtils.randomBoolean(.25f)) {
                for (int i = (int) building.getWidth() / 50; i > 0; i--) {
                    DoveEntity dove = new DoveEntity();
                    dove.setPosition(MathUtils.random(building.getX() + 20.0f, building.getX() + building.getWidth() - 20.0f), building.getY());
                    entityManager.addEntity(dove);
                }
            }
        }
        
        return building;
    }

    @Override
    public void act(float delta) {
        if (!PlayerEntity.getInstance().isDestroyed() && PlayerEntity.getInstance().getMode() != PlayerEntity.Mode.HIT) {
            runSpeed += delta * 10.0f;
        }
        
        Iterator<BuildingEntity> iter = buildings.iterator();
        while (iter.hasNext()) {
            BuildingEntity building = iter.next();
            if (building.isDestroyed()) {
                iter.remove();
            }
        }
        
        BuildingEntity building = buildings.peek();
        float edge = building.getX() + building.getWidth() + gap;
        
        if (edge < Gdx.graphics.getWidth()) {
            spawnBuilding(true);
            gap = MathUtils.random(GAP_MIN, calculateMaxJump());
        }
    }

    private float calculateMaxJump() {
        if (runSpeed < 400) return 300.0f;
        else if (runSpeed < 800) return 500.0f;
        else return 750.0f;
    }
    
    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void collision(Entity other) {
    }

    public float getRunSpeed() {
        return runSpeed;
    }

    public void setRunSpeed(float runSpeed) {
        this.runSpeed = runSpeed;
    }

}
