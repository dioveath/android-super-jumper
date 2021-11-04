package com.charicha.superjumper;

import com.charicha.Game;
import com.charicha.Input;
import com.charicha.game.MyCamera2D;
import com.charicha.game.SpriteBatcher;
import com.charicha.impl.GLScreen;
import com.charicha.math.Vector2;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Charicha on 1/13/2018.
 */

public class HighScoresScreen extends GLScreen {

    GL10 gl;

    MyCamera2D camera;
    SpriteBatcher spriteBatcher;
    List<Input.TouchEvent> touchEvents;
    Vector2 touchPos;

    public HighScoresScreen(Game game) {
        super(game);
        gl = glGraphics.getGL();

        camera = new MyCamera2D(glGraphics, 160, 240, 320, 480);
        spriteBatcher = new SpriteBatcher(glGraphics, 100);

        touchPos = new Vector2(0, 0);
    }

    @Override
    public void update(float deltaTime) {
   }

    @Override
    public void render(float deltaTime) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        camera.setOrthoProjection();

        spriteBatcher.beginBatch(Assets.background);
        spriteBatcher.drawSprite(camera.frustumWidth/2, camera.frustumHeight/2, camera.frustumWidth, camera.frustumHeight, Assets.backgroundRegion);
        spriteBatcher.endBatach();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        spriteBatcher.beginBatch(Assets.items);
        spriteBatcher.drawSprite(160, 480-36, 300, 36, Assets.highScoresRegion);
        for(int i = 0; i < 5; i++){
            String highScore = (i + 1) + ". " + Settings.localHighScores[i];
            int textWidth = Assets.textWriter.glyphWidth * highScore.length();
            Assets.textWriter.writeText(spriteBatcher, highScore, 160 - textWidth/2, 240 - Assets.textWriter.glyphHeight * (i - 2));
        }
        spriteBatcher.endBatach();

        gl.glDisable(GL10.GL_BLEND);
    }

    @Override
    public void resume() {
        gl.glClearColor(0.5f, 0.3f, 0, 1);
        gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
        gl.glEnable(GL10.GL_TEXTURE_2D);
        Settings.loadLocalSettings(glGame.getFileIO());
    }

    @Override
    public void pause() {

    }

    @Override
    public void dispose() {

    }
}
