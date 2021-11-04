package com.charicha.superjumper;

import com.charicha.Screen;
import com.charicha.impl.GLAndroidGame;
import com.charicha.impl.GLGame;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Charicha on 1/13/2018.
 */

public class SuperJumper extends GLGame {

    boolean firstTimeCreated = true;

    @Override
    public Screen getStartScreen(){
        return new MainMenuScreen(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig){
        super.onSurfaceCreated(gl10, eglConfig);
        if(firstTimeCreated){
            Settings.loadLocalSettings(getFileIO());
            Assets.load(this);
            firstTimeCreated = false;
        } else {
            Assets.reload();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(Settings.soundEnabled)
            Assets.music.pause();
    }

    @Override
    public void onBackPressed() {
        if(getCurrentScreen() instanceof PlayScreen){
            if(((PlayScreen) getCurrentScreen()).currentGameState == PlayScreen.GameState.RUNNING)
                ((PlayScreen) getCurrentScreen()).currentGameState = PlayScreen.GameState.PAUSED;
        } else if(!(getCurrentScreen() instanceof MainMenuScreen)){
            changeCurrentScreen(new MainMenuScreen(this));
        }  else {
            super.onBackPressed();
        }
    }
}
