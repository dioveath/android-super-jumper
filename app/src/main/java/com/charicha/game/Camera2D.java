package com.charicha.game;

import com.charicha.impl.GLGraphics;
import com.charicha.math.Vector2;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Charicha on 1/11/2018.
 */

public class Camera2D {

    GLGraphics glGraphics;
    GL10 gl;
    public final Vector2 position;
    public float zoom;
    public float frustumWidth, frustumHeight;

    public Camera2D(GLGraphics glGraphics, float frustumWidth, float frustumHeight){
        this.glGraphics = glGraphics;
        this.frustumWidth = frustumWidth;
        this.frustumHeight = frustumHeight;
        this.position = new Vector2(frustumWidth/2, frustumHeight/2);
        this.zoom = 1;
    }

    public void touchToWorld(Vector2 touch){
        touch.x =  (touch.x / (float) glGraphics.getWidth()) * frustumWidth * zoom;
        touch.y = (1 - (touch.y / (float) glGraphics.getHeight())) * frustumHeight * zoom;
        touch.add(position).subtract(frustumWidth * zoom/2, frustumHeight * zoom/2);
    }

    public void setViewportAndMatrices(){
        gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(position.x - frustumWidth/2 * zoom,
                position.x + frustumWidth/2 * zoom,
                position.y - frustumHeight/2 * zoom,
                position.y + frustumHeight/2 * zoom,
                -1f, 1f);
    }

}
