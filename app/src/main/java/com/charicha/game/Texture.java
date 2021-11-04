package com.charicha.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.charicha.impl.GLGame;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Charicha on 1/2/2018.
 */

public class Texture  {

    GLGame mGame;
    String mFilename;
    GL10 gl;

    int textureId;
    int minFilter;
    int magFilter;

    public int width;
    public int height;

    public Texture(GLGame game, String fileName){
        mGame = game;
        mFilename = fileName;
        gl = game.getGLGraphics().getGL();
        load();
    }

    public void load(){
        InputStream inputStream = null;
        try {
            inputStream = mGame.getFileIO().openAsset(mFilename);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            width = bitmap.getWidth();
            height = bitmap.getHeight();

            int[] textureIds = new int[1];
            gl.glGenTextures(1, textureIds, 0);
            textureId = textureIds[0];

            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            setFilters(GL10.GL_LINEAR, GL10.GL_LINEAR);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

            bitmap.recycle();
        } catch(IOException ioe){
            ioe.printStackTrace();
        } finally {
            try {
                    inputStream.close();
            } catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
    }

    public void bind(){
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
    }

    public void reload(){
        load();
        bind();
    }

    public void unbind(){
        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
    }

    public void dispose(){
        unbind();
        gl.glDeleteTextures(1, new int[textureId], 0);
    }

    public void setFilters(int minFilter, int magFilter){
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, minFilter);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, magFilter);
    }

}
