package com.charicha.superjumper;

import com.charicha.game.DynamicObject;

/**
 * Created by Charicha on 1/14/2018.
 */

public class Squirrel extends DynamicObject{

    public static final float SQUIRREL_WIDTH = 1;
    public static final float SQUIRREL_HEIGHT = 0.6f;
    public static final float SQUIRREL_VELOCITY = 3f;

    public float internalTime = 0;

    public Squirrel(float x, float y) {
        super(x, y, SQUIRREL_WIDTH, SQUIRREL_HEIGHT);
        this.velocity.x = SQUIRREL_VELOCITY;
    }

    public void update(float deltaTime){
        internalTime += deltaTime;
        this.position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        this.bounds.lowerLeft.set(position).subtract(SQUIRREL_WIDTH/2, SQUIRREL_HEIGHT/2);

        if(this.position.x > World.WORLD_WIDTH - SQUIRREL_WIDTH/2){
            this.position.x = World.WORLD_WIDTH - SQUIRREL_WIDTH/2;
            this.velocity.x *= -1;
        }
        if(this.position.x < SQUIRREL_WIDTH/2){
            this.position.x = SQUIRREL_WIDTH/2;
            this.velocity.x *= -1;
        }
    }
}
