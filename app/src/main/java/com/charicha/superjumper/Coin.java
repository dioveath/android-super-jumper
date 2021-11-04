package com.charicha.superjumper;

import com.charicha.game.GameObject;

/**
 * Created by Charicha on 1/14/2018.
 */

public class Coin extends GameObject {

    public static final float COIN_WIDTH = 0.5f;
    public static final float COIN_HEIGHT = 0.8f;
    public static final int COIN_SCORE = 10;

    public float internalTime = 0;

    public Coin(float x, float y) {
        super(x, y, COIN_WIDTH, COIN_HEIGHT);
    }

    public void update(float deltaTime){
        internalTime += deltaTime;
    }
}
