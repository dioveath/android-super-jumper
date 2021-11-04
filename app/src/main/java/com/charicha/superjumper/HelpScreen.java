package com.charicha.superjumper;

import com.charicha.Game;
import com.charicha.Input;
import com.charicha.game.MyCamera2D;
import com.charicha.game.SpriteBatcher;
import com.charicha.game.Texture;
import com.charicha.impl.GLGraphics;
import com.charicha.impl.GLScreen;
import com.charicha.math.Collision;
import com.charicha.math.Rectangle;
import com.charicha.math.Vector2;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Charicha on 1/13/2018.
 */

public class HelpScreen extends GLScreen {

    GL10 gl;
    MyCamera2D camera2D;

    SpriteBatcher spriteBatcher;
    Texture[] allHelpImageTexture;
    int currentHelpIndex;

    Rectangle nextBound;
    Rectangle prevBound;

    List<Input.TouchEvent> touchEvents;
    Vector2 touchPos;

    public HelpScreen(Game game) {
        super(game);
        gl = glGraphics.getGL();
        camera2D = new MyCamera2D(glGraphics, 160, 240, 320, 480);

        spriteBatcher = new SpriteBatcher(glGraphics, 20);
        allHelpImageTexture = new Texture[6];
        for(int i = 0; i < allHelpImageTexture.length; i++){
            String fileName = "help" + (i == 0 ? "" : i + "") + ".png";
            allHelpImageTexture[i] = new Texture(glGame, fileName);
        }
        currentHelpIndex = 0;

        nextBound = new Rectangle(320-64, 0, 64, 64);
        prevBound = new Rectangle(0, 0,64, 64);

        touchPos = new Vector2(0, 0);
    }

    @Override
    public void update(float deltaTime) {
        touchEvents = glGame.getInput().getTouchEvents();
        for(int i = 0;i < touchEvents.size(); i++){
            Input.TouchEvent touchEvent = touchEvents.get(i);
            if(touchEvent.type != Input.TouchEvent.TOUCH_UP)
                return;

            touchPos.set(touchEvent.x, touchEvent.y);
            camera2D.screenToWorldPoint(touchPos);

            if(Collision.pointInRect(touchPos, nextBound)){
                Assets.playSound(Assets.clickSound);
                currentHelpIndex++;
                if(currentHelpIndex >= allHelpImageTexture.length){
                    glGame.changeCurrentScreen(new MainMenuScreen(glGame));
                    return;
                }
            }
            if(Collision.pointInRect(touchPos, prevBound)){
                Assets.playSound(Assets.clickSound);
                currentHelpIndex--;
                if (currentHelpIndex < 0) {
                    glGame.changeCurrentScreen(new MainMenuScreen(glGame));
                    return;
                }
            }

        }
    }

    @Override
    public void render(float deltaTime) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        camera2D.setOrthoProjection();

        Texture texture = allHelpImageTexture[currentHelpIndex];
        spriteBatcher.beginBatch(texture);
        spriteBatcher.drawSprite(160, 240, 320, 480, Assets.backgroundRegion);
        spriteBatcher.endBatach();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        spriteBatcher.beginBatch(Assets.items);
        spriteBatcher.drawSprite(32, 32, 64, 64, Assets.arrow);
        spriteBatcher.drawSprite(320 - 32, 32, -64, 64, Assets.arrow);
        spriteBatcher.endBatach();
    }

    @Override
    public void resume() {
        gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
        gl.glEnable(GL10.GL_TEXTURE_2D);
        for(int i = 0; i < allHelpImageTexture.length; i++){
            allHelpImageTexture[i].reload();
        }

    }

    @Override
    public void pause() {

    }

    @Override
    public void dispose() {

    }

}
