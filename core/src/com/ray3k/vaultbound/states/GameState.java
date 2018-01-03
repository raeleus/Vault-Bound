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
package com.ray3k.vaultbound.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ray3k.vaultbound.Core;
import com.ray3k.vaultbound.EntityManager;
import com.ray3k.vaultbound.InputManager;
import com.ray3k.vaultbound.State;
import com.ray3k.vaultbound.entities.BackgroundEntity;
import com.ray3k.vaultbound.entities.BackgroundManagerEntity;
import com.ray3k.vaultbound.entities.BuildingManagerEntity;
import com.ray3k.vaultbound.entities.JetManagerEntity;
import com.ray3k.vaultbound.entities.PlayerEntity;

public class GameState extends State {
    private static GameState instance;
    private int score;
    private float internalScore;
    private static int highscore = 0;
    private OrthographicCamera gameCamera;
    private Viewport gameViewport;
    private InputManager inputManager;
    private Skin skin;
    private Stage stage;
    private Table table;
    private Label scoreLabel;
    public static EntityManager entityManager;
    public static TextureAtlas spineAtlas;
    public static final float GAME_WIDTH = 800.0f;
    public static final float GAME_HEIGHT = 600.0f;
    
    public static GameState inst() {
        return instance;
    }
    
    public GameState(Core core) {
        super(core);
    }
    
    @Override
    public void start() {
        instance = this;
        
        spineAtlas = Core.assetManager.get(Core.DATA_PATH + "/spine/vault-bound.atlas", TextureAtlas.class);
        
        score = 0;
        internalScore = 0.0f;
        
        inputManager = new InputManager();
        
        gameCamera = new OrthographicCamera();
        gameViewport = new StretchViewport(800.0f, 600.0f, gameCamera);
        gameViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        gameViewport.apply();
        
        skin = Core.assetManager.get(Core.DATA_PATH + "/ui/vault-bound.json", Skin.class);
        stage = new Stage(new StretchViewport(800.0f, 600.0f));
        
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputManager);
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
        
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        
        entityManager = new EntityManager();
        
        createStageElements();
        
        entityManager.addEntity(new BackgroundEntity());
        entityManager.addEntity(new JetManagerEntity());
        entityManager.addEntity(new BuildingManagerEntity());
        
        BackgroundManagerEntity bgManager = new BackgroundManagerEntity();
        entityManager.addEntity(bgManager);
        
        PlayerEntity player = new PlayerEntity();
        player.setPosition(75.0f, 200.0f);
        entityManager.addEntity(player);
    }
    
    private void createStageElements() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        
        scoreLabel = new Label("0", skin);
        root.add(scoreLabel).expandY().padTop(25.0f).top();
    }
    
    @Override
    public void draw(SpriteBatch spriteBatch, float delta) {
        Gdx.gl.glClearColor(207.0f / 255.0f, 111.0f / 255.0f, 101.0f / 255.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        gameCamera.update();
        spriteBatch.setProjectionMatrix(gameCamera.combined);
        spriteBatch.begin();
        spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        entityManager.draw(spriteBatch, delta);
        spriteBatch.end();
        
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        stage.draw();
    }

    @Override
    public void act(float delta) {
        internalScore += delta * BuildingManagerEntity.getInstance().getRunSpeed() / 300.0f;
        setScore((int) internalScore);
        entityManager.act(delta);
        
        stage.act(delta);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void stop() {
        stage.dispose();
    }
    
    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
        scoreLabel.setText(Integer.toString(score));
        if (score > highscore) {
            highscore = score;
        }
    }
    
    public void addScore(int score) {
        this.score += score;
        internalScore += score;
        scoreLabel.setText(Integer.toString(this.score));
        if (this.score > highscore) {
            highscore = this.score;
        }
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public void setGameCamera(OrthographicCamera gameCamera) {
        this.gameCamera = gameCamera;
    }

    public Skin getSkin() {
        return skin;
    }

    public Stage getStage() {
        return stage;
    }
    
    public void playJet() {
        Core.assetManager.get(Core.DATA_PATH + "/sfx/jet.wav", Sound.class).play(.25f);
    }
    
    public void playJump() {
        Core.assetManager.get(Core.DATA_PATH + "/sfx/jump.wav", Sound.class).play(.25f);
    }
    
    public void playHurt() {
        Core.assetManager.get(Core.DATA_PATH + "/sfx/hurt.wav", Sound.class).play(.25f);
    }
    
    public void playThud() {
        Core.assetManager.get(Core.DATA_PATH + "/sfx/thud.wav", Sound.class).play(.25f);
    }
    
    public void playBlast() {
        Core.assetManager.get(Core.DATA_PATH + "/sfx/blast.wav", Sound.class).play(.5f);
    }
}