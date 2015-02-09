package com.desitum.twist.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.desitum.twist.data.Assets;
import com.desitum.twist.data.Settings;
import com.desitum.twist.screens.MainScreen;

/**
 * Created by Zmyth on 1/30/2015.
 */
public class Kipper extends Sprite {
    /*In way of explanation of this file and the weird naming conventions,
     the name of the cube in the game is "Kipper" Like Kipper the Kube.
      Name inspired by KopperKow*/

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int ROTATING = 2;

    private float kipperSpeed; //Max is 8 (4 is lowest, 8 is highest, 4 = slow)
    private int kipperOrientation; //Vertical or Horizontal (0 is Vertical, 1 is Horizontal)
    private float lastHorizontalSpeed; //Left or Right (- or +)

    //This color is going to be used for a blur trail
    private Color kipperColor; //Color of Kipper (Blue?)

    public Kipper(float kipperSpeed, float kipperSize, Color kipperColor, float kipperPositionX, float kipperPositionY) {
        super(Assets.kipperTexture, 0, 0, Assets.kipperTexture.getWidth(), Assets.kipperTexture.getHeight());

        this.kipperSpeed = kipperSpeed;
        this.kipperOrientation = VERTICAL;
        this.kipperColor = kipperColor;
        this.lastHorizontalSpeed = kipperSpeed;

        this.setSize(kipperSize, kipperSize);
        this.setX(kipperPositionX);
        this.setY(kipperPositionY);
    }

    public void update(float delta) {


        if (kipperOrientation == HORIZONTAL)
        {
            if (getX() <= 0) {
                kipperSpeed = Settings.kipperSpeed;
            } else if (getX() + getWidth() >= MainScreen.FRUSTUM_WIDTH) {
                kipperSpeed = -Settings.kipperSpeed;
            }


            setX(getX() + kipperSpeed * delta);

        } else if (kipperOrientation == VERTICAL) {
            setY(getY() + kipperSpeed * delta);
        }
    }

    public float getKipperSpeed() {
        return kipperSpeed;
    }

    public void setKipperSpeed(float kipperSpeed) {
        this.kipperSpeed = kipperSpeed;
    }

    public float getKipperLength() {
        return getBoundingRectangle().getWidth();
    }

    public void setKipperLength(float kipperLength) {
        this.getBoundingRectangle().setWidth(kipperLength);
    }

    public int getKipperOrientation() {
        return kipperOrientation;
    }

    public void toggleKipperOrientation() {
        if (kipperOrientation == VERTICAL) {
            kipperOrientation = HORIZONTAL;
            kipperSpeed = lastHorizontalSpeed;
        } else {
            kipperOrientation = VERTICAL;
            lastHorizontalSpeed = kipperSpeed;
            kipperSpeed = Settings.kipperSpeed;
        }
    }

    public Rectangle getKipperRect() {
        return getBoundingRectangle();
    }

    public void reset(){
        setPosition(Settings.kipperX, Settings.kipperY);
        kipperOrientation = VERTICAL;
        kipperSpeed = Settings.kipperSpeed;
    }
}

