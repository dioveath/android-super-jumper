package com.charicha.superjumper;

import com.charicha.game.GameObject;

/**
 * Created by Charicha on 1/14/2018.
 */

public class Spring extends GameObject{

    public static final float SPRING_WIDTH = 0.3f;
    public static final float SPRING_HEIGHT = 0.3f;

    public Spring(float x, float y) {
        super(x, y, SPRING_WIDTH, SPRING_HEIGHT);
    }
}
