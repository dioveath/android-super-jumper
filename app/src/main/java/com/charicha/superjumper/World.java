package com.charicha.superjumper;

import android.renderscript.BaseObj;

import com.charicha.math.Collision;
import com.charicha.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Charicha on 1/14/2018.
 */

public class World {

    public interface WorldListener {
        void jump();
        void highJump();
        void hit();
        void coin();
    }

    public static final float WORLD_WIDTH = 10;
    public static final float WORLD_HEIGHT = 15 * 20;

    public static final Vector2 GRAVITY = new Vector2(0, -13);

    public static final int WORLD_STATE_RUNNING = 0;
    public static final int WORLD_STATE_NEXT_LEVEL = 1;
    public static final int WORLD_STATE_GAME_OVER = 2;

    int currentWorldState;
    final WorldListener worldListener;

    Castle castle;
    final Bob bob;
    final List<Platform> allPlatforms;
    final List<Coin> allCoins;
    final List<Spring> allSprings;
    final List<Squirrel> allSquirrels;

    float heightSoFar;
    int score;

    final Random random = new Random();

    public World(WorldListener worldListener){
        this.worldListener = worldListener;
        this.bob = new Bob(WORLD_WIDTH/2, Platform.PLATFORM_HEIGHT/2 + Bob.BOB_HEIGHT/2);
        this.allPlatforms = new ArrayList<>(20);
        this.allCoins = new ArrayList<>(20);
        this.allSprings = new ArrayList<>(20);
        this.allSquirrels = new ArrayList<>(20);
        generateWorld();

        currentWorldState = WORLD_STATE_RUNNING;

        this.heightSoFar = 0;
        this.score = 0;
    }

    public void generateWorld(){
        float y = Platform.PLATFORM_HEIGHT/2;
        float bobJumpHeight = Bob.BOB_JUMP_VELOCITY * Bob.BOB_JUMP_VELOCITY / (- 2 * GRAVITY.y);


        while(y < World.WORLD_HEIGHT - bobJumpHeight/2){

            float x = random.nextFloat() * (WORLD_WIDTH - Platform.PLATFORM_WIDTH/2) + Platform.PLATFORM_WIDTH/2;

            Platform platform = new Platform(x, y, random.nextFloat() > 0.8f ? Platform.PLATFORM_TYPE_MOVING : Platform.PLATFORM_TYPE_STATIC);
            allPlatforms.add(platform);

            if(random.nextFloat() > 0.8f && platform.platformType == Platform.PLATFORM_TYPE_STATIC){
                Spring spring = new Spring(platform.position.x, platform.position.y + Platform.PLATFORM_HEIGHT/2 + Spring.SPRING_HEIGHT/2);
                allSprings.add(spring);
            }

            if(y > World.WORLD_HEIGHT/3 && random.nextFloat() > 0.7f){
                Squirrel squirrel = new Squirrel(random.nextFloat() * WORLD_WIDTH - Squirrel.SQUIRREL_WIDTH/2, platform.position.y + Squirrel.SQUIRREL_HEIGHT + random.nextFloat() * (bobJumpHeight/2));
                allSquirrels.add(squirrel);
            }

            if(random.nextFloat() > 0.6f && platform.platformType == Platform.PLATFORM_TYPE_STATIC){
                Coin coin = new Coin(platform.position.x, platform.position.y + Platform.PLATFORM_HEIGHT/2 + Coin.COIN_HEIGHT/2);
                allCoins.add(coin);
            }

            y += (bobJumpHeight - 0.5f);
            y -= random.nextFloat() * (bobJumpHeight/3);
        }

        castle = new Castle(World.WORLD_WIDTH/2, y);

    }

    public void update(float deltaTime, float accelx){
        updateBob(deltaTime, accelx);
        updatePlatforms(deltaTime);
        updateCoins(deltaTime);
        updateSquirrels(deltaTime);
        if(bob.state != Bob.BOB_STATE_DEAD)
            checkCollisions();
        checkIfGameOver();
    }

    private void updateBob(float deltaTime, float accelx){
        if(bob.state != Bob.BOB_STATE_DEAD && bob.position.y < 0.5f){
            bob.landOnPlatform();
            worldListener.jump();
        }
        if(bob.state != Bob.BOB_STATE_DEAD){
            bob.velocity.x = (-accelx/10) * Bob.BOB_MOVE_VELOCITY;
        }

        bob.update(deltaTime);
        heightSoFar = Math.max(bob.position.y, heightSoFar);
    }

    private void updatePlatforms(float deltaTime){
        int len = allPlatforms.size();
        for(int i = 0; i < len; i++){
            Platform platform = allPlatforms.get(i);
            platform.update(deltaTime);
            if(platform.platformState == Platform.PLATFORM_STATE_PULVERIZING){
                if(platform.internalTime >= Platform.PULVERIZE_TIME) {
                    allPlatforms.remove(platform);
                    len = allPlatforms.size();
                }
            }
        }
    }

    private void updateSquirrels(float deltaTime){
        int len = allSquirrels.size();
        for(int i = 0; i < len; i++){
            allSquirrels.get(i).update(deltaTime);
        }
    }

    private void updateCoins(float deltaTime){
        for(int i = 0; i < allCoins.size(); i++)
            allCoins.get(i).update(deltaTime);
    }

    private void checkCollisions(){
        checkPlatformCollisions();
        checkSquirrelCollisions();
        checkItemCollisions();
        checkCastleCollisions();
    }

    private void checkPlatformCollisions(){
        if(bob.velocity.y > 0)
            return;
        int len = allPlatforms.size();
        for(int i = 0; i < len; i++){
            Platform platform = allPlatforms.get(i);
            if(bob.position.y > platform.position.y){
                if(Collision.isColliding(bob.bounds, platform.bounds)){
                    bob.landOnPlatform();
                    worldListener.jump();
                    if(random.nextFloat() > 0.7f)
                        platform.pulverize();
                    break;
                }
            }
        }
    }

    private void checkSquirrelCollisions(){
        for(int i = 0; i < allSquirrels.size(); i++){
            if(Collision.isColliding(bob.bounds, allSquirrels.get(i).bounds)){
                bob.hitSquirrel();
                worldListener.hit();
            }
        }
    }

    private void checkItemCollisions(){
        int len = allCoins.size();
        for(int i = 0; i < len; i++){
            Coin coin = allCoins.get(i);
            if(Collision.isColliding(bob.bounds, coin.bounds)){
                allCoins.remove(coin);
                len = allCoins.size();
                worldListener.coin();
                score += Coin.COIN_SCORE;
                break;
            }
        }

        if(bob.velocity.y > 0)
            return;

        len = allSprings.size();
        for(int i = 0; i < len; i++){
            Spring spring = allSprings.get(i);
            if(bob.position.y > spring.position.y){
                if(Collision.isColliding(bob.bounds, spring.bounds)){
                    bob.landOnSpring();
                    worldListener.highJump();
                    break;
                }
            }
        }
    }

    private void checkCastleCollisions(){
        if(Collision.isColliding(bob.bounds, castle.bounds))
            currentWorldState = WORLD_STATE_NEXT_LEVEL;
    }

    private void checkIfGameOver(){
        if(bob.position.y < heightSoFar - 7.5f)
            currentWorldState = WORLD_STATE_GAME_OVER;
    }

}
