package com.charicha.superjumper;

import android.content.Context;

import com.charicha.game.MyCamera2D;
import com.charicha.game.SpriteBatcher;
import com.charicha.game.TextureRegion;
import com.charicha.impl.GLGraphics;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Charicha on 1/15/2018.
 */

public class WorldRenderer {

    static final float FRUSTUM_WIDTH = 10;
    static final float FRUSTUM_HEIGHT = 15;

    GLGraphics glGraphics;
    GL10 gl;
    SpriteBatcher spriteBatcher;
    World world;
    MyCamera2D camera2D;

    public WorldRenderer(GLGraphics glGraphics, SpriteBatcher spriteBatcher, World world){
        this.glGraphics = glGraphics;
        this.gl = glGraphics.getGL();
        this.spriteBatcher = spriteBatcher;
        this.world = world;
        camera2D = new MyCamera2D(glGraphics, FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
    }

    public void render(){
        if (world.bob.position.y > camera2D.y)
            camera2D.y = world.bob.position.y;

        gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
        camera2D.setOrthoProjection();
        renderBackground();
        renderObjects();
    }

    private void renderBackground(){
        spriteBatcher.beginBatch(Assets.background);
        spriteBatcher.drawSprite(camera2D.x, camera2D.y, FRUSTUM_WIDTH, FRUSTUM_HEIGHT, Assets.backgroundRegion);
        spriteBatcher.endBatach();
    }

    private void renderObjects(){
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        spriteBatcher.beginBatch(Assets.items);
        renderBob();
        renderPlatforms();
        renderItems();
        renderSquirrels();
        renderCastle();
        spriteBatcher.endBatach();
        gl.glDisable(GL10.GL_BLEND);
    }

    private void renderBob(){
        TextureRegion keyFrame;
        switch(world.bob.state){
            case Bob.BOB_STATE_JUMPING:
                keyFrame = Assets.bobFall.getKeyFrame(world.bob.internalTime, true);
                break;
            case Bob.BOB_STATE_FALLING:
                keyFrame = Assets.bobFall.getKeyFrame(world.bob.internalTime, true);
                break;
            case Bob.BOB_STATE_DEAD:
            default:
                keyFrame = Assets.bobHit;
        }
        float side = world.bob.velocity.x < 0 ? -1 : 1;
        /*Note also that we don’t use BOB_WIDTH or BOB_HEIGHT to specify the size of the rectangle we draw for Bob.
        Those sizes are the sizes of the bounding shapes, which are not necessarily the sizes of the
        rectangles we render. Instead, we use our 1×1-meter-to-32×32-pixel mapping. That’s something we’ll do
        for all sprite rendering; we’ll either use a 1×1 rectangle (Bob, coins, squirrels, springs), a 2×0.5
        rectangle (platforms), or a 2×2 rectangle (castle); */

        spriteBatcher.drawSprite(world.bob.position.x, world.bob.position.y, side * 1, 1, keyFrame);
    }

    private void renderPlatforms(){
        int len = world.allPlatforms.size();
        for(int i = 0; i < len; i++){
            Platform platform = world.allPlatforms.get(i);
            TextureRegion keyFrame = Assets.platform;
            if(platform.platformState == Platform.PLATFORM_STATE_PULVERIZING){
                keyFrame = Assets.breakingPlatform.getKeyFrame(platform.internalTime, false);
            }
            spriteBatcher.drawSprite(platform.position.x, platform.position.y, 2, 0.5f, keyFrame);
        }
    }

    private void renderItems(){
        int len = world.allSprings.size();
        for(int i = 0; i < len; i++){
            Spring spring = world.allSprings.get(i);
            spriteBatcher.drawSprite(spring.position.x, spring.position.y, 1f, 1f, Assets.spring);
        }

        len = world.allCoins.size();
        TextureRegion keyFrame;
        for(int i = 0; i < len; i++){
            Coin coin = world.allCoins.get(i);
            keyFrame = Assets.coinAnim.getKeyFrame(coin.internalTime, true);
            spriteBatcher.drawSprite(coin.position.x, coin.position.y, 1f, 1f, keyFrame);
        }
    }

    private void renderSquirrels(){
        int len = world.allSquirrels.size();
        TextureRegion keyFrame;
        for(int i = 0; i < len; i++){
            Squirrel squirrel = world.allSquirrels.get(i);
            keyFrame = Assets.squirrelFly.getKeyFrame(squirrel.internalTime, true);
            spriteBatcher.drawSprite(squirrel.position.x, squirrel.position.y, 1f, 1f, keyFrame);
        }
    }

    private void renderCastle(){
        Castle castle = world.castle;
        spriteBatcher.drawSprite(castle.position.x, castle.position.y, 2f, 2f, Assets.castle);
    }

}
