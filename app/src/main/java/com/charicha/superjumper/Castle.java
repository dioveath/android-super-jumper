package com.charicha.superjumper;

import com.charicha.game.GameObject;

/**
 * Created by Charicha on 1/14/2018.
 */

public class Castle extends GameObject {

    public static final float CASTLE_WIDTH = 1.7f;
    public static final float CASTLE_HEIGHT = 1.7f;

    public Castle(float x, float y) {
        super(x, y, CASTLE_WIDTH, CASTLE_HEIGHT);
    }
}
