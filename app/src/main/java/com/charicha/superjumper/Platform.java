package com.charicha.superjumper;

import com.charicha.game.DynamicObject;

/**
 * Created by Charicha on 1/14/2018.
 */

public class Platform extends DynamicObject {

    public static final float PLATFORM_WIDTH = 2;
    public static final float PLATFORM_HEIGHT = 0.5f;
    public static final int PLATFORM_TYPE_STATIC = 0;
    public static final int PLATFORM_TYPE_MOVING = 1;
    public static final int PLATFORM_STATE_NORMAL = 0;
    public static final int PLATFORM_STATE_PULVERIZING = 1;
    public static final float PULVERIZE_TIME = 0.2f * 4;
    public static final float PLATFORM_VELOCITY = 2;

    public int platformType;
    public int platformState;
    public float internalTime;

    public Platform(float x, float y, int platformType) {
        super(x, y, PLATFORM_WIDTH, PLATFORM_HEIGHT);
        this.platformType = platformType;
        this.platformState = PLATFORM_STATE_NORMAL;
        if(platformType == PLATFORM_TYPE_MOVING) this.velocity.x = PLATFORM_VELOCITY;
    }

    public void update(float deltaTime){
        if(this.platformType == PLATFORM_TYPE_MOVING){
            this.position.add(this.velocity.x * deltaTime, this.velocity.y * deltaTime);
            this.bounds.lowerLeft.set(position).subtract(PLATFORM_WIDTH/2, PLATFORM_HEIGHT/2);
            if(this.position.x > World.WORLD_WIDTH - PLATFORM_WIDTH/2){
                this.position.x = World.WORLD_WIDTH - PLATFORM_WIDTH;
                this.velocity.x *= -1;
            }
            if(this.position.x < PLATFORM_WIDTH/2){
                this.position.x = PLATFORM_WIDTH/2;
                this.velocity.x *= -1;
            }
        }
        this.internalTime += deltaTime;
    }

    public void pulverize(){
        platformState = PLATFORM_STATE_PULVERIZING;
        internalTime = 0;
        velocity.x = 0;
    }

}
