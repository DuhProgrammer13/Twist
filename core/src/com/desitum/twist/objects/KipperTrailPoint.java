package com.desitum.twist.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by dvan6234 on 2/10/2015.
 */
public class KipperTrailPoint extends Sprite{

    private float alphaAmount;
    private float scaleSize;


    public KipperTrailPoint (float x, float y, float size, Texture texture){
        super(texture, 0, 0, texture.getWidth(), texture.getHeight());
        this.setPosition(x, y);
        this.setSize(size, size);

        this.setOriginCenter();

        alphaAmount = 0.4f;
        scaleSize = 1;

    }

    public void update(float delta){
        alphaAmount -= delta;
        if (alphaAmount <= 0) alphaAmount = 0;
        setAlpha(alphaAmount);
        scaleSize -= delta;
        if (scaleSize <= 0) scaleSize = 0;
        setScale(scaleSize);
    }

    public float getScaleSize(){
        return scaleSize;
    }
}
