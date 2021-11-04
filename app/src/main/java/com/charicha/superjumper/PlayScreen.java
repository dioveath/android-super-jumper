package com.charicha.superjumper;

import android.text.method.Touch;

import com.charicha.Game;
import com.charicha.Input;
import com.charicha.game.MyCamera2D;
import com.charicha.game.SpriteBatcher;
import com.charicha.impl.GLScreen;
import com.charicha.math.Collision;
import com.charicha.math.Rectangle;
import com.charicha.math.Vector2;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Charicha on 1/13/2018.
 */

public class PlayScreen extends GLScreen {

    public enum GameState {
        READY, RUNNING, PAUSED, LEVEL_END, GAME_OVER
    }

    GameState currentGameState;
    GL10 gl;

    MyCamera2D guiCamera;
    SpriteBatcher spriteBatcher;

    World world;
    World.WorldListener worldListener;
    WorldRenderer worldRenderer;
    int lastScore;
    String scoreString;

    Rectangle pauseBounds;
    Rectangle resumeBounds;
    Rectangle quitBounds;

    List<Input.TouchEvent> touchEvents;
    Vector2 touchPos;

    public PlayScreen(Game game) {
        super(game);
        gl = glGraphics.getGL();

        guiCamera = new MyCamera2D(glGraphics,160, 240, 320, 480);
        spriteBatcher = new SpriteBatcher(glGraphics, 200);

        worldListener = new World.WorldListener() {
            @Override
            public void jump() {
                Assets.playSound(Assets.jumpSound);
            }

            @Override
            public void highJump() {
                Assets.playSound(Assets.highJumpSound);
            }

            @Override
            public void hit() {
                Assets.playSound(Assets.hitSound);
            }

            @Override
            public void coin() {
                Assets.playSound(Assets.coinSound);
            }
        };
        world = new World(worldListener);
        worldRenderer = new WorldRenderer(glGraphics, spriteBatcher, world);

        lastScore = 0;
        scoreString = "Score: 0";

        pauseBounds = new Rectangle(320-64, 480-64, 64, 64);
        resumeBounds = new Rectangle(160-96, 240, 192, 36);
        quitBounds = new Rectangle(160-96, 240-36, 192, 36);

        touchPos = new Vector2(0, 0);
        currentGameState = GameState.READY;
    }

    @Override
    public void update(float deltaTime) {
        if(deltaTime > 0.1f) //limiting delta time *********>?????????
            deltaTime = 0.1f;

        switch(currentGameState){
            case READY:
                updateReady();
                break;
            case RUNNING:
                updateRunning(deltaTime);
                break;
            case PAUSED:
                updatePaused();
                break;
            case LEVEL_END:
                updateLevelEnd();
                break;
            case GAME_OVER:
                updateGameOver();
                break;
        }
    }

    public void updateReady(){
        touchEvents = mGame.getInput().getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++){
            if(touchEvents.get(i).type == Input.TouchEvent.TOUCH_UP) {
                currentGameState = GameState.RUNNING;
                break;
            }
        }
    }

    public void updateRunning(float deltaTime){
        touchEvents = mGame.getInput().getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++){
            Input.TouchEvent curTouchEvent = touchEvents.get(i);
            if(curTouchEvent.type != Input.TouchEvent.TOUCH_UP)
                continue;

            touchPos.set(curTouchEvent.x, curTouchEvent.y);
            guiCamera.screenToWorldPoint(touchPos);

            if(Collision.pointInRect(touchPos, pauseBounds)){
                Assets.playSound(Assets.clickSound);
                currentGameState = GameState.PAUSED;
                return;
            }
        }

        world.update(deltaTime, glGame.getInput().getAccelX());
        if(world.score != lastScore){
            lastScore = world.score;
            scoreString = " " + lastScore;
        }
        if(world.currentWorldState == World.WORLD_STATE_NEXT_LEVEL){
            currentGameState = GameState.LEVEL_END;
        }
        if(world.currentWorldState == World.WORLD_STATE_GAME_OVER){
            currentGameState = GameState.GAME_OVER;
            if(lastScore >= Settings.localHighScores[4]){
                scoreString = "New Highscore: " + lastScore;
                Settings.addScore(lastScore);
                Settings.saveLocalSettings(mGame.getFileIO());
            } else {
                scoreString = "Your Score: " + lastScore;
            }
        }

    }

    public void updatePaused(){
        touchEvents = mGame.getInput().getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++){
            Input.TouchEvent curTouchEvent = touchEvents.get(i);
            if(curTouchEvent.type != Input.TouchEvent.TOUCH_UP)
                return;

            touchPos.set(curTouchEvent.x, curTouchEvent.y);
            guiCamera.screenToWorldPoint(touchPos);

            if(Collision.pointInRect(touchPos, resumeBounds)){
                Assets.playSound(Assets.clickSound);
                currentGameState = GameState.RUNNING;
                return;
            }
            if (Collision.pointInRect(touchPos, quitBounds)) {
                Assets.playSound(Assets.clickSound);
                mGame.changeCurrentScreen(new MainMenuScreen(mGame));
                return;
            }
        }
    }

    public void updateLevelEnd(){
        touchEvents = mGame.getInput().getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++){
            Input.TouchEvent curTouchEvent = touchEvents.get(i);
            if(curTouchEvent.type != Input.TouchEvent.TOUCH_UP)
                return;

            world = new World(worldListener);
            worldRenderer = new WorldRenderer(glGraphics, spriteBatcher, world);
            world.score = lastScore;
            currentGameState = GameState.READY;
        }
    }

    public void updateGameOver(){
        touchEvents = mGame.getInput().getTouchEvents();
        if(touchEvents.size() > 0){
            if(touchEvents.get(0).type == Input.TouchEvent.TOUCH_UP){
                mGame.changeCurrentScreen(new MainMenuScreen(glGame));
                return;
            }
        }
    }

    @Override
    public void render(float deltaTime) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        worldRenderer.render();

        guiCamera.setOrthoProjection();
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        spriteBatcher.beginBatch(Assets.items);
        switch(currentGameState){
            case READY:
                renderReady();
                break;
            case RUNNING:
                renderRunning();
                break;
            case PAUSED:
                renderPause();
                break;
            case LEVEL_END:
                renderLevelEnd();
                break;
            case GAME_OVER:
                renderGameOver();
                break;
        }
        spriteBatcher.endBatach();
        gl.glDisable(GL10.GL_BLEND);
    }

    private void renderReady(){
        spriteBatcher.drawSprite(160, 240, 192, 32, Assets.ready);
    }

    private void renderRunning(){
        spriteBatcher.drawSprite(320-32, 480-32, 64, 64, Assets.pause);
        Assets.textWriter.writeText(spriteBatcher, scoreString, 16, 480-20);
    }

    private void renderPause(){
        spriteBatcher.drawSprite(160, 240, 192, 96, Assets.pauseMenu);
        Assets.textWriter.writeText(spriteBatcher, scoreString, 16, 480-20);
    }

    private void renderLevelEnd(){
        String topText = "The princess is in...";
        String bottomText = " another castle!";
        float topWidth = Assets.textWriter.glyphWidth * topText.length();
        float botWidth = Assets.textWriter.glyphWidth * bottomText.length();
        Assets.textWriter.writeText(spriteBatcher, topText, 160 - topWidth/2, 240 + Assets.textWriter.glyphHeight);
        Assets.textWriter.writeText(spriteBatcher, bottomText, 160 - botWidth/2, 240);
    }

    private void renderGameOver(){
        spriteBatcher.drawSprite(160, 240, 160, 96, Assets.gameOver);
        float scoreWidth = Assets.textWriter.glyphWidth * scoreString.length();
        Assets.textWriter.writeText(spriteBatcher, scoreString, 160 - scoreWidth/2, 240 - 96 + Assets.textWriter.glyphHeight);
    }


    @Override
    public void resume() {
        gl.glClearColor(0.3f, 0.5f, 0, 1);
        gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
        gl.glEnable(GL10.GL_TEXTURE_2D);
    }

    @Override
    public void pause() {
        if(currentGameState == GameState.RUNNING)
            currentGameState = GameState.PAUSED;
    }

    @Override
    public void dispose() {

    }
}
