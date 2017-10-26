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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ray3k.vaultbound.Core;
import com.ray3k.vaultbound.Entity;
import com.ray3k.vaultbound.states.GameState;

public class PlayerEntity extends Entity {
    private Animation<TextureRegion> run, roll, jump, fall, hit;
    private Animation<TextureRegion> animation;
    private float time;
    private float physicsTime;
    private Mode mode;
    public static enum Mode {
        RUNNING, START_JUMP, SHORT_JUMP, LONG_JUMP, ROLL, JUMP_ROLL, FALLING, HIT;
    }
    private boolean doRoll;
    private static final Rectangle rectangle1 = new Rectangle();
    private static final Rectangle rectangle2 = new Rectangle();
    private static PlayerEntity instance;

    public static PlayerEntity getInstance() {
        return instance;
    }
    
    @Override
    public void create() {
        instance = this;
        setDepth(-100);
        
        Array<TextureRegion> regions = new Array<TextureRegion>();
        for (String string : Core.imagePacks.get(Core.DATA_PATH + "/run animation")) {
            regions.add(Core.generatedAtlas.findRegion(string));
        }
        run = new Animation<TextureRegion>(.1f, regions, Animation.PlayMode.LOOP);
        
        regions = new Array<TextureRegion>();
        for (String string : Core.imagePacks.get(Core.DATA_PATH + "/roll animation")) {
            regions.add(Core.generatedAtlas.findRegion(string));
        }
        roll = new Animation<TextureRegion>(.08f, regions, Animation.PlayMode.NORMAL);
        
        regions = new Array<TextureRegion>();
        for (String string : Core.imagePacks.get(Core.DATA_PATH + "/jump animation")) {
            regions.add(Core.generatedAtlas.findRegion(string));
        }
        jump = new Animation<TextureRegion>(.1f, regions, Animation.PlayMode.NORMAL);
        
        regions = new Array<TextureRegion>();
        for (String string : Core.imagePacks.get(Core.DATA_PATH + "/jump fall animation")) {
            regions.add(Core.generatedAtlas.findRegion(string));
        }
        fall = new Animation<TextureRegion>(.1f, regions, Animation.PlayMode.LOOP);
        
        regions = new Array<TextureRegion>();
        for (String string : Core.imagePacks.get(Core.DATA_PATH + "/hit animation")) {
            regions.add(Core.generatedAtlas.findRegion(string));
        }
        hit = new Animation<TextureRegion>(.1f, regions, Animation.PlayMode.NORMAL);
        
        mode = Mode.RUNNING;
        animation = run;
    }

    @Override
    public void act(float delta) {
        time += delta;
        
        if (getY() + animation.getKeyFrame(0.0f).getRegionHeight() < 0) {
            GameState.entityManager.addEntity(new GameOverTimerEntity(2.0f));
            if (mode != Mode.HIT) {
                GameState.inst().playHurt();
                BuildingManagerEntity.getInstance().setRunSpeed(0.0f);
            }
            dispose();
        }
        
        boolean hitObstacle = false;
        rectangle1.set(getX(), getY(), animation.getKeyFrame(0.0f).getRegionWidth(), animation.getKeyFrame(0.0f).getRegionHeight());
        for (Entity entity : GameState.entityManager.getEntities()) {
            if (entity instanceof ObstacleEntity) {
                ObstacleEntity obstacle = (ObstacleEntity) entity;
                rectangle2.set(obstacle.getX(), obstacle.getY(), 30.0f, 30.0f);
                if (rectangle1.overlaps(rectangle2)) {
                    obstacle.knockOver();
                    hitObstacle = true;
                }
            }
        }
        
        if (mode == Mode.RUNNING) {
            if (Gdx.input.isKeyPressed(Keys.SPACE)) {
                GameState.inst().playJump();
                time = 0.0f;
                animation = jump;
                mode = Mode.START_JUMP;
                physicsTime = 0.0f;
                setMotion(1200.0f, 90.0f);
                setGravity(1800.0f, 270.0f);
            } else {
                if (hitObstacle) {
                    GameState.inst().playThud();
                    float runSpeed = BuildingManagerEntity.getInstance().getRunSpeed() - 100.0f;
                    if (runSpeed < 300.0f) runSpeed = 300.0f;
                    BuildingManagerEntity.getInstance().setRunSpeed(runSpeed);
                    time = 0.0f;
                    animation = roll;
                    mode = Mode.ROLL;
                    GameState.inst().addScore(-10);
                } else {
                    boolean doFall = true;
                    for (Entity entity : GameState.entityManager.getEntities()) {
                        if (entity instanceof BuildingEntity) {
                            BuildingEntity building = (BuildingEntity) entity;
                            if (getX() > building.getX() && getX() < building.getX() + building.getWidth()) {
                                if (getY() <= building.getY()) {
                                    doFall = false;
                                }
                            }
                        }
                    }

                    if (doFall) {
                        animation = fall;
                        time = 0.0f;
                        mode = Mode.FALLING;
                        setGravity(3000.0f, 270.0f);
                        doRoll = false;
                    }
                }
            }
        } else if (mode == Mode.START_JUMP) {
            physicsTime += delta;
            if (physicsTime < .25f) {
                if (!Gdx.input.isKeyPressed(Keys.SPACE)) {
                    mode = Mode.SHORT_JUMP;
                    setGravity(3000.0f, 270.0f);
                }
            } else {
                mode = Mode.LONG_JUMP;
            }
        } else if (mode == Mode.SHORT_JUMP) {
            physicsTime += delta;
            if (physicsTime >= .025f) {
            }
            
            if (getYspeed() < 0) {
                animation = fall;
                time = 0.0f;
                mode = Mode.FALLING;
                doRoll = false;
            }
        } else if (mode == Mode.LONG_JUMP) {
            if (getYspeed() < 0) {
                animation = fall;
                time = 0.0f;
                mode = Mode.FALLING;
                doRoll = true;
            }
        } else if (mode == Mode.FALLING) {
            for (Entity entity : GameState.entityManager.getEntities()) {
                if (entity instanceof BuildingEntity) {
                    BuildingEntity building = (BuildingEntity) entity;
                    
                    float frontX = getX() + animation.getKeyFrame(0.0f).getRegionWidth();
                    if ((getX() > building.getX() && getX() < building.getX() + building.getWidth()) || (frontX > building.getX() && frontX < building.getX() + building.getWidth())) {
                        if (getY() < building.getY()) {
                            if (getY() - getYspeed() * delta > building.getY()) {
                                setY(building.getY());
                                time = 0.0f;
                                if (doRoll) {
                                    animation = roll;
                                    mode = Mode.JUMP_ROLL;
                                    GameState.inst().playThud();
                                } else {
                                    animation = run;
                                    mode = Mode.RUNNING;
                                }
                                setGravity(0.0f, 0.0f);
                                setMotion(0.0f, 0.0f);
                            } else {
                                GameState.inst().playHurt();
                                BuildingManagerEntity.getInstance().setRunSpeed(0.0f);
                                time = 0.0f;
                                animation = hit;
                                mode = Mode.HIT;
                                setXspeed(-50.0f);
                                setYspeed(0.0f);
                            }
                            break;
                        }
                    }
                }
            }
        } else if (mode == Mode.ROLL) {
            if (time > animation.getAnimationDuration()) {
                mode = Mode.RUNNING;
                animation = run;
                time = 0.0f;
            } else {
                boolean doFall = true;
                for (Entity entity : GameState.entityManager.getEntities()) {
                    if (entity instanceof BuildingEntity) {
                        BuildingEntity building = (BuildingEntity) entity;
                        if (getX() > building.getX() && getX() < building.getX() + building.getWidth()) {
                            if (getY() <= building.getY()) {
                                doFall = false;
                            }
                        }
                    }
                }

                if (doFall) {
                    animation = fall;
                    time = 0.0f;
                    mode = Mode.FALLING;
                    setGravity(3000.0f, 270.0f);
                }
            }
        } else if (mode == Mode.JUMP_ROLL) {
            if (time > animation.getAnimationDuration() || Gdx.input.isKeyPressed(Keys.SPACE)) {
                mode = Mode.RUNNING;
                animation = run;
                time = 0.0f;
            } else {
                boolean doFall = true;
                for (Entity entity : GameState.entityManager.getEntities()) {
                    if (entity instanceof BuildingEntity) {
                        BuildingEntity building = (BuildingEntity) entity;
                        if (getX() > building.getX() && getX() < building.getX() + building.getWidth()) {
                            if (getY() <= building.getY()) {
                                doFall = false;
                            }
                        }
                    }
                }

                if (doFall) {
                    animation = fall;
                    time = 0.0f;
                    mode = Mode.FALLING;
                    setGravity(3000.0f, 270.0f);
                }
            }
        }
    }

    @Override
    public void actEnd(float delta) {
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatch.draw(animation.getKeyFrame(time + delta), getX(), getY());
        spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void collision(Entity other) {
    }

    public Mode getMode() {
        return mode;
    }
}
