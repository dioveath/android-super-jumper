package com.charicha.superjumper;

import com.charicha.game.SpriteBatcher;
import com.charicha.game.Texture;
import com.charicha.game.TextureRegion;

/**
 * Created by Charicha on 1/13/2018.
 */

public class TextWriter {

    Texture texture;
    int glyphWidth, glyphHeight;

    TextureRegion[] glyphs = new TextureRegion[96];

    public TextWriter(Texture texture, int offsetX, int offsetY, int glyphWidth, int glyphHeight, int glyphPerRow){
        this.texture = texture;
        this.glyphWidth = glyphWidth;
        this.glyphHeight = glyphHeight;

        int x = offsetX;
        int y = offsetY;

        for(int i = 0; i < 96; i++){
            glyphs[i] = new TextureRegion(texture, x, y, glyphWidth, glyphHeight);

            x += glyphWidth;
            if(x == offsetX + glyphPerRow * glyphWidth){
                y += glyphHeight;
                x = offsetX;
            }
        }
    }

    public void writeText(SpriteBatcher spriteBatcher, String text, float x, float y){
        for(int i = 0; i < text.length(); i++){
            int glyphIndex = text.charAt(i) - ' ';
            if(glyphIndex < 0 && glyphIndex >  95)
                continue;
            spriteBatcher.drawSprite(x, y, glyphWidth, glyphHeight, glyphs[glyphIndex]);
            x+=glyphWidth;
        }
    }

}
