package com.charicha.impl;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.charicha.Audio;
import com.charicha.FileIO;
import com.charicha.Game;
import com.charicha.Graphics;
import com.charicha.Input;
import com.charicha.Screen;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Charicha on 12/31/2017.
 */

public abstract class GLAndroidGame extends Activity implements Game, GLSurfaceView.Renderer{

    enum GameState {
        IDLE,
        RUNNING,
        PAUSED,
        STOPPED
    };

    GameState currentState = GameState.IDLE;
    GLSurfaceView glView;
    GLGraphics mGLGraphics;
    AndroidAudio mAudio;
    AndroidFileIO mFileIO;
    AndroidInput mInput;
    Screen mCurrentScreen;

    long startTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        glView = new GLSurfaceView(this);
        glView.setRenderer(this);

        mGLGraphics = new GLGraphics(glView);
        mAudio = new AndroidAudio(this);
        mFileIO = new AndroidFileIO(this);
        mInput = new AndroidInput(this, glView, 1, 1);

        setContentView(glView);
    }

    @Override
    public void onResume(){
        super.onResume();
        glView.onResume();
    }

    @Override
    public void onPause(){
        synchronized (this){
            currentState = GameState.PAUSED;
            if(isFinishing()){
                currentState = GameState.STOPPED;
            }
            try {
                wait();
            } catch(InterruptedException ie){
                ie.printStackTrace();
            }
        }
        glView.onPause();
        mCurrentScreen.pause();
        super.onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mGLGraphics.setGL(gl10);
        synchronized (this){
            if(currentState == GameState.IDLE)
                mCurrentScreen = getStartScreen();
            mCurrentScreen.resume();
            currentState = GameState.RUNNING;
        }
        startTime = System.nanoTime();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        synchronized(this){
            switch(currentState){
                case RUNNING:
                    float deltaTime = (System.nanoTime() - startTime) / 100000000f;
                    mCurrentScreen.update(deltaTime);
                    mCurrentScreen.render(deltaTime);
                    break;
                case PAUSED:
                    mCurrentScreen.pause();
                    notify();
                    break;
                case STOPPED:
                    mCurrentScreen.pause();
                    mCurrentScreen.dispose();
                    notify();
                    break;
            }
        }
    }

    @Override
    public Graphics getGraphics() {
        throw new RuntimeException("Can't use Canvas Graphics for GLSurfaceView..!");
    }

    public GLGraphics getGLGraphics(){
        return mGLGraphics;
    }

    @Override
    public Input getInput() {
        return mInput;
    }

    @Override
    public FileIO getFileIO() {
        return mFileIO;
    }

    @Override
    public Audio getAudio() {
        return mAudio;
    }

    @Override
    public void changeCurrentScreen(Screen screen) {
        if(screen == null)
            throw new RuntimeException("Screen can't be null........");
        synchronized (this){
            mCurrentScreen.pause();
            mCurrentScreen.dispose();
            screen.resume();
            screen.update(0);
            mCurrentScreen = screen;
        }
    }

    @Override
    public Screen getCurrentScreen() {
        return mCurrentScreen;
    }
}
