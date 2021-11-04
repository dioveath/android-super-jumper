package com.charicha.superjumper;

import com.charicha.game.DynamicObject;

/**
 * Created by Charicha on 1/14/2018.
 */

public class Bob extends DynamicObject {

    public static final float BOB_WIDTH = 0.8f;
    public static final float BOB_HEIGHT = 0.8f;
    public static final int BOB_STATE_JUMPING = 0;
    public static final int BOB_STATE_FALLING = 1;
    public static final int BOB_STATE_DEAD = 2;
    public static final float BOB_JUMP_VELOCITY = 11f;
    public static final float BOB_MOVE_VELOCITY = 13f;


    float internalTime = 0;
    int state;

    public Bob(float x, float y) {
        super(x, y, BOB_WIDTH, BOB_HEIGHT);
        state = BOB_STATE_FALLING;
    }

    public void update(float deltaTime){
        velocity.add(World.GRAVITY.x * deltaTime, World.GRAVITY.y * deltaTime);
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        bounds.lowerLeft.set(position).subtract(BOB_WIDTH/2, BOB_HEIGHT/2);

        if(velocity.y > 0 && state != BOB_STATE_DEAD){
            if(state != BOB_STATE_JUMPING){
                state = BOB_STATE_JUMPING;
                internalTime = 0;
            }
        }

        if(velocity.y < 0 && state != BOB_STATE_DEAD){
            if(state != BOB_STATE_FALLING){
                state = BOB_STATE_FALLING;
                internalTime = 0;
            }
        }

        if(position.x < 0)
            position.x = World.WORLD_WIDTH;
        if(position.x > World.WORLD_WIDTH)
            position.x = 0;

        internalTime += deltaTime;
    }

    public void hitSquirrel(){
        velocity.set(0, 0);
        state = BOB_STATE_DEAD;
    }

    public void landOnPlatform(){
        velocity.y = BOB_JUMP_VELOCITY;
        state = BOB_STATE_JUMPING;
        internalTime = 0;
    }

    public void landOnSpring(){
        velocity.y = BOB_JUMP_VELOCITY * 1.5f;
        state = BOB_STATE_JUMPING;
        internalTime = 0;
    }


}
