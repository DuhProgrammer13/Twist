package com.desitum.twist.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by kody on 1/30/15.
 */
public class Assets {

    public static Texture playButtonTexture;
    public static Texture highscoreButtonTexture;
    public static Texture volumeOnButtonTexture;
    public static Texture volumeOffButtonTexture;

    public static Texture purpleBarTexture;
    public static Texture yellowBarTexture;
    public static Texture redBarTexture;
    public static Texture orangeBarTexture;
    public static Texture kipperTexture;

    public static Texture backgroundTexture1;
    public static Texture backgroundTexture2;
    public static Texture backgroundTexture3;
    public static Texture backgroundTexture4;

    public static void loadMenuTextures(){
        playButtonTexture = new Texture(Gdx.files.internal("menu/play.png"));
        highscoreButtonTexture = new Texture(Gdx.files.internal("menu/high_scores.png"));
        volumeOnButtonTexture = new Texture(Gdx.files.internal("menu/volume_on.png"));
        volumeOffButtonTexture = new Texture(Gdx.files.internal("menu/volume_off.png"));
    }

    public static void loadGameTextures(){
        purpleBarTexture = new Texture(Gdx.files.internal("menu/purple_bar.png"));
        orangeBarTexture = new Texture(Gdx.files.internal("menu/orange_bar.png"));
        redBarTexture = new Texture(Gdx.files.internal("menu/red_bar.png"));
        yellowBarTexture = new Texture(Gdx.files.internal("menu/yellow_bar.png"));
        kipperTexture = new Texture(Gdx.files.internal("menu/kipper.png"));
    }

    public static void loadBackgroundTextures(){
        backgroundTexture1 = new Texture(Gdx.files.internal("bg/bg_1.png"));
        backgroundTexture2 = new Texture(Gdx.files.internal("bg/bg_2.png"));
        backgroundTexture3 = new Texture(Gdx.files.internal("bg/bg_3.png"));
        backgroundTexture4 = new Texture(Gdx.files.internal("bg/bg_4.png"));
    }
}
