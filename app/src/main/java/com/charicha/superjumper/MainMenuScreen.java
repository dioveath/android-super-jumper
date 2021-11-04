package com.charicha.superjumper;

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

public class MainMenuScreen extends GLScreen {

    GL10 gl;

    MyCamera2D camera;
    SpriteBatcher spriteBatcher;

    Rectangle play;
    Rectangle highScores;
    Rectangle help;
    Rectangle sound;

    List<Input.TouchEvent> touchEvents;
    Vector2 touchPos;

    public MainMenuScreen(Game game) {
        super(game);
        gl = glGraphics.getGL();
        camera = new MyCamera2D(glGraphics, 160, 240, 320, 480);
        spriteBatcher = new SpriteBatcher(glGraphics, 100);

        play = new Rectangle(10, 273, 300, 36);
        highScores = new Rectangle(10, 240, 300, 36);
        help = new Rectangle(10, 207, 300, 36);
        sound = new Rectangle(0, 0, 64, 64);

        touchPos = new Vector2(0, 0);
    }

    @Override
    public void update(float deltaTime) {
        touchEvents = mGame.getInput().getTouchEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++){
            Input.TouchEvent cTouch = touchEvents.get(i);
            touchPos.x = cTouch.x;
            touchPos.y = cTouch.y;
            camera.screenToWorldPoint(touchPos);
            if(cTouch.type == Input.TouchEvent.TOUCH_UP){
                if(Collision.pointInRect(touchPos, play)){
                    mGame.changeCurrentScreen(new PlayScreen(mGame));
                    Assets.playSound(Assets.clickSound);
                }
                if(Collision.pointInRect(touchPos, highScores)){
                    mGame.changeCurrentScreen(new HighScoresScreen(mGame));
                    Assets.playSound(Assets.clickSound);
                }
                if(Collision.pointInRect(touchPos, help)){
                    mGame.changeCurrentScreen(new HelpScreen(mGame));
                    Assets.playSound(Assets.clickSound);
                }
                if(Collision.pointInRect(touchPos, sound)){
                    Settings.soundEnabled = !Settings.soundEnabled;
                    Assets.playSound(Assets.clickSound);
                    if(Settings.soundEnabled)
                        Assets.music.play();
                    else
                        Assets.music.pause();
                }
            }
        }
    }

    @Override
    public void render(float deltaTime) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        camera.setOrthoProjection();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        spriteBatcher.beginBatch(Assets.background);
        spriteBatcher.drawSprite(160, 240, 320, 480, Assets.backgroundRegion);
        spriteBatcher.endBatach();
        spriteBatcher.beginBatch(Assets.items);
        spriteBatcher.drawSprite(160, 409, 274, 142, Assets.logo);
        spriteBatcher.drawSprite(highScores.lowerLeft.x + highScores.width/2, highScores.lowerLeft.y + highScores.height/2,300,110, Assets.mainMenu);
        if(Settings.soundEnabled)
            spriteBatcher.drawSprite(32, 32, 64, 64, Assets.soundOn);
        else
            spriteBatcher.drawSprite(32, 32, 64, 64, Assets.soundOff);
        spriteBatcher.endBatach();

        gl.glDisable(GL10.GL_BLEND);
    }

    @Override
    public void resume() {
        gl.glClearColor(0, 0, 0, 1);
        gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
        gl.glEnable(GL10.GL_TEXTURE_2D);
    }

    @Override
    public void pause() {
        Settings.saveLocalSettings(mGame.getFileIO());
    }

    @Override
    public void dispose() {

    }
}
