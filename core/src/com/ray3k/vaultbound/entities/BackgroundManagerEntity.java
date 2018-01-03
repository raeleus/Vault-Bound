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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.ray3k.vaultbound.Entity;
import com.ray3k.vaultbound.states.GameState;
import static com.ray3k.vaultbound.states.GameState.entityManager;
import java.util.Iterator;

public class BackgroundManagerEntity extends Entity {
    private Array<BackgroundBuildingEntity> buildingsFore;
    private Array<BackgroundBuildingEntity> buildingsAft;
    private float mechaTimer;
    
    @Override
    public void create() {
        buildingsFore = new Array<BackgroundBuildingEntity>();
        buildingsAft = new Array<BackgroundBuildingEntity>();
        mechaTimer = 30.0f;
        
        createInitial();
    }

    private void createInitial() {
        float x = 0.0f;
        
        while (x < GameState.GAME_WIDTH) {
            BackgroundBuildingEntity building = spawnForeBuilding(x);
            
            x += building.getPatch().getTotalWidth();
        }
        
        x = 0.0f;
        
        while (x < GameState.GAME_WIDTH) {
            BackgroundBuildingEntity building = spawnAftBuilding(x);
            
            x += building.getPatch().getTotalWidth();
        }  
    }
    
    @Override
    public void act(float delta) {
        Iterator<BackgroundBuildingEntity> iter = buildingsFore.iterator();
        while (iter.hasNext()) {
            BackgroundBuildingEntity building = iter.next();
            if (building.isDestroyed()) {
                iter.remove();
            }
        }
        
        iter = buildingsAft.iterator();
        while (iter.hasNext()) {
            BackgroundBuildingEntity building = iter.next();
            if (building.isDestroyed()) {
                iter.remove();
            }
        }
        
        float edge = buildingsFore.peek().getX() + buildingsFore.peek().getPatch().getTotalWidth();
        if (edge < GameState.GAME_WIDTH) {
            spawnForeBuilding(edge);
        }
        
        edge = buildingsAft.peek().getX() + buildingsAft.peek().getPatch().getTotalWidth();
        if (edge < GameState.GAME_WIDTH) {
            spawnAftBuilding(edge);
        }
        
        mechaTimer -= delta;
        if (mechaTimer < 0) {
            mechaTimer = MathUtils.random(10.0f, 30.0f);
            MechaEntity mecha = new MechaEntity();
            mecha.setPosition(GameState.GAME_WIDTH, GameState.GAME_HEIGHT);
            entityManager.addEntity(mecha);
        }
    }
    
    private BackgroundBuildingEntity spawnForeBuilding(float x) {
        BackgroundBuildingEntity building = new BackgroundBuildingEntity(BackgroundBuildingEntity.Type.FORE);
        GameState.entityManager.addEntity(building);
        buildingsFore.add(building);

        building.setPosition(x, MathUtils.random(200.0f, 400.0f));
        building.getPatch().setColor(new Color(102.0f / 255.0f, 70.0f / 255.0f, 70.0f / 255.0f, 1.0f));
        building.setDepth(100);
        building.setMotion(100.0f, 180.0f);
        
        return building;
    }
    
    private BackgroundBuildingEntity spawnAftBuilding(float x) {
        BackgroundBuildingEntity building = new BackgroundBuildingEntity(BackgroundBuildingEntity.Type.AFT);
        GameState.entityManager.addEntity(building);
        buildingsAft.add(building);

        building.setPosition(x, MathUtils.random(300.0f, 600.0f));
        building.getPatch().setColor(new Color(63.0f / 255.0f, 43.0f / 255.0f, 43.0f / 255.0f, 1.0f));
        building.setDepth(101);
        building.setMotion(60.0f, 180.0f);
        
        return building;
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
}
