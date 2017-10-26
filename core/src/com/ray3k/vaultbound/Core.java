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
package com.ray3k.vaultbound;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.ray3k.vaultbound.SkeletonDataLoader.SkeletonDataLoaderParameter;
import com.ray3k.vaultbound.states.GameOverState;
import com.ray3k.vaultbound.states.GameState;
import com.ray3k.vaultbound.states.LoadingState;
import com.ray3k.vaultbound.states.MenuState;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

public class Core extends ApplicationAdapter {
    public final static String VERSION = "1";
    public final static String DATA_PATH = "vault_bound_data";
    
    public static Core instance;
    public static AssetManager assetManager;
    public static StateManager stateManager;
    public static SpriteBatch spriteBatch;
    public static TextureAtlas generatedAtlas;
    public static SkeletonRenderer skeletonRenderer;
    public static PixmapPacker pixmapPacker;
    public static ObjectMap<String, Array<String>> imagePacks;
    
    private final static long MS_PER_UPDATE = 10;
    private long previous;
    private long lag;
    
    private SplashWorker splashWorker;
    
    @Override
    public void create() {
        splashWorker.closeSplash();
        
        instance = this;
        try {
            initManagers();

            loadAssets();

            previous = TimeUtils.millis();
            lag = 0;

            stateManager.loadState("loading");
        } catch (Exception e) {
            e.printStackTrace();
            
            FileWriter fw = null;
            try {
                fw = new FileWriter(Gdx.files.local("java-stacktrace.txt").file(), true);
                PrintWriter pw = new PrintWriter(fw);
                e.printStackTrace(pw);
                pw.close();
                fw.close();
                int choice = JOptionPane.showConfirmDialog(null, "Exception occurred. See error log?", "Game Exception!", JOptionPane.YES_NO_OPTION);
                if (choice == 0) {
                    FileHandle startDirectory = Gdx.files.local("java-stacktrace.txt");
                    if (startDirectory.exists()) {
                        File file = startDirectory.file();
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(file);
                    } else {
                        throw new IOException("Directory doesn't exist: " + startDirectory.path());
                    }
                }
                Gdx.app.exit();
            } catch (Exception ex) {
                
            }
        }
    }
    
    public void initManagers() {
        assetManager = new AssetManager(new LocalFileHandleResolver(), true);
        assetManager.setLoader(SkeletonData.class, new SkeletonDataLoader(new LocalFileHandleResolver()));
        
        stateManager = new StateManager(this);
        stateManager.addState("loading", new LoadingState("menu", this));
        stateManager.addState("menu", new MenuState(this));
        stateManager.addState("game", new GameState(this));
        stateManager.addState("game-over", new GameOverState(this));
        
        spriteBatch = new SpriteBatch();
        
        pixmapPacker = new PixmapPacker(1024, 1024, Pixmap.Format.RGBA8888, 5, true, new PixmapPacker.GuillotineStrategy());
        
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        
        imagePacks = new ObjectMap<String, Array<String>>();
        for (String name : new String[] {"run animation", "hit animation", "jump animation", "jump fall animation", "roll animation"}) {
            imagePacks.put(DATA_PATH + "/" + name, new Array<String>());
        }
    }
    
    @Override
    public void render() {
        try {
            long current = TimeUtils.millis();
            long elapsed = current - previous;
            previous = current;
            lag += elapsed;

            while (lag >= MS_PER_UPDATE) {
                stateManager.act(MS_PER_UPDATE / 1000.0f);
                lag -= MS_PER_UPDATE;
            }

            stateManager.draw(spriteBatch, lag / MS_PER_UPDATE);
        } catch (Exception e) {
            e.printStackTrace();
            
            FileWriter fw = null;
            try {
                fw = new FileWriter(Gdx.files.local("java-stacktrace.txt").file(), true);
                PrintWriter pw = new PrintWriter(fw);
                e.printStackTrace(pw);
                pw.close();
                fw.close();
                int choice = JOptionPane.showConfirmDialog(null, "Exception occurred. See error log?", "Game Exception!", JOptionPane.YES_NO_OPTION);
                if (choice == 0) {
                    FileHandle startDirectory = Gdx.files.local("java-stacktrace.txt");
                    if (startDirectory.exists()) {
                        File file = startDirectory.file();
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(file);
                    } else {
                        throw new IOException("Directory doesn't exist: " + startDirectory.path());
                    }
                }
                Gdx.app.exit();
            } catch (Exception ex) {
                
            }
        }
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        stateManager.dispose();
        pixmapPacker.dispose();
        if (generatedAtlas != null) {
            generatedAtlas.dispose();
        }
    }
    
    public void loadAssets() {
        assetManager.clear();
        FileHandle directory = Gdx.files.local(DATA_PATH + "/spine");
        for (FileHandle atlasFile : directory.list("atlas")) {
            SkeletonDataLoaderParameter parameter = new SkeletonDataLoaderParameter(atlasFile.path());
            for (FileHandle jsonFile : directory.list("json")) {
                assetManager.load(jsonFile.path(), SkeletonData.class, parameter);
            }
            break;
        }
        
        directory = Gdx.files.local(DATA_PATH + "/ui");
        for (FileHandle file : directory.list("json")) {
            assetManager.load(file.path(), Skin.class);
        }
        
        directory = Gdx.files.local(DATA_PATH + "/sfx");
        for (FileHandle file : directory.list()) {
            assetManager.load(file.path(), Sound.class);
        }
        
        for (String packName : imagePacks.keys()) {
            FileHandle folder = Gdx.files.local(packName);
            for (FileHandle file : folder.list()) {
                assetManager.load(file.path(), Pixmap.class);
                imagePacks.get(packName).add(file.nameWithoutExtension());
            }
        }
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void pause() {
        
    }

    @Override
    public void resize(int width, int height) {
        stateManager.resize(width, height);
    }

    public void setSplashWorker(SplashWorker splashWorker) {
        this.splashWorker = splashWorker;
    }
}
