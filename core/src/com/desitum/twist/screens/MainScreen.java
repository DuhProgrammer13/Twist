package com.desitum.twist.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.desitum.twist.GooglePlayServicesInterface;
import com.desitum.twist.TwistGame;
import com.desitum.twist.data.Assets;
import com.desitum.twist.data.BackgroundManager;
import com.desitum.twist.data.Settings;
import com.desitum.twist.libraries.CollisionDetection;
import com.desitum.twist.objects.Bar;
import com.desitum.twist.objects.MenuButton;
import com.desitum.twist.world.GameRenderer;
import com.desitum.twist.world.GameWorld;
import com.desitum.twist.world.MenuRenderer;
import com.desitum.twist.world.MenuWorld;

/**
 * Created by kody on 1/30/15.
 */
public class MainScreen implements Screen {

    public static final float FRUSTUM_WIDTH = 10;
    public static final float FRUSTUM_HEIGHT = 15;

    public static int state = 0;

    public static final int MENU_BEFORE_TRANSITION = 0;
    public static final int MENU_WAITING = 1;
    public static final int MENU_TRANSITION = 2;
    public static final int GAME_BEFORE = 3;
    public static final int GAME_RUNNING = 4;
    public static final int GAME_PAUSED = 5;
    public static final int GAME_OVER = 6;
    public static final int GAME_OVER_TRANSITION = 7;

    public static String PLAY = "play";
    public static String VOLUMES = "volumes";
    public static String OPEN_SCORES = "open_scores";
    public static String SHARE = "share";

    private OrthographicCamera cam;
    private OrthographicCamera textCam;
    private SpriteBatch spriteBatch;

    private MenuWorld menuWorld;
    private GameWorld gameWorld;

    private MenuRenderer menuRenderer;
    private GameRenderer gameRenderer;

    private Vector3 touchPoint;

    private BackgroundManager backgroundManager;
    private GooglePlayServicesInterface gpgs;

    public MainScreen(GooglePlayServicesInterface gps) {
        gpgs = gps;
        cam = new OrthographicCamera(FRUSTUM_WIDTH * 10, FRUSTUM_HEIGHT * 10);
        textCam = new OrthographicCamera(100, 150);
        cam.position.set(FRUSTUM_WIDTH * 10 / 2, FRUSTUM_HEIGHT * 10 / 2, 0);
        spriteBatch = new SpriteBatch();
        backgroundManager = new BackgroundManager();

        Assets.menuMusic.play();

        menuWorld = new MenuWorld();
        gameWorld = new GameWorld();

        menuRenderer = new MenuRenderer(menuWorld, spriteBatch, backgroundManager);
        gameRenderer = new GameRenderer(gameWorld, spriteBatch, backgroundManager);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1, 0, 1, 1);


        if (Gdx.input.justTouched()) {
            if (state == MENU_WAITING || state == MENU_TRANSITION) {
                touchPoint = menuRenderer.getCam().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            } else if (state == GAME_BEFORE || state == GAME_RUNNING || state == GAME_PAUSED || state == GAME_OVER){
                touchPoint = gameRenderer.getCam().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            }
            onClick();
        }

        update(delta);
        //draw();

        cam.update();
        spriteBatch.enableBlending();
        spriteBatch.begin();

        draw();

        spriteBatch.end();
    }

    //region onClicks

    /**
     * handles Gdx.input.justTouched() input
     */
    private void onClick() {
        switch (state) {
            case MENU_WAITING:
                onClickMenuWaiting();
                break;
            case GAME_BEFORE:
                onClickGameBefore();
                break;
            case GAME_PAUSED:
                onClickGamePaused();
                break;
            case GAME_RUNNING:
                onClickGameRunning();
                break;
            case GAME_OVER:
                onClickGameOver();
                break;
        }
    }

    /**
     * called when click input when game state is MENU_WAITING
     */
    private void onClickMenuWaiting() {
        for (MenuButton mb : menuWorld.getMenuButtons()) {
            if (CollisionDetection.pointInRectangle(mb.getBoundingRectangle(), touchPoint)) { // if touched a rectangle
                if (mb.getCommand().equals(PLAY)) { // if the button was play
                        Assets.buttonSound.play(Settings.volume);
                    state = MENU_TRANSITION;
                } else if (mb.getCommand().equals(OPEN_SCORES)) { // if the button was high scores
                        Assets.buttonSound.play(Settings.volume);
                    gpgs.getLeaderBoard();
                } else if (mb.getCommand().equals(VOLUMES)) { // if the button was volumes
                    Settings.volumeOn = !Settings.volumeOn; // toggle whether volume is on
                    Settings.getSound(); //Gets the Sound (if volume is on)
                    if (Settings.volumeOn) { // update texture for the Volume button
                        mb.setTexture(Assets.volumeOnButtonTexture);
                        Assets.buttonSound.play(Settings.volume);
                    } else {
                        mb.setTexture(Assets.volumeOffButtonTexture);
                    }
                }
            }
        }
    }

    /**
     * called when click input when game state is GAME_BEFORE
     */
    private void onClickGameBefore() {
        state = GAME_RUNNING;
        gpgs.hideAd();
    }

    /**
     * called when click input when game state is GAME_PAUSED
     */
    private void onClickGamePaused() {
        state = GAME_RUNNING;
    }

    /**
     * called when click input when game state is GAME_RUNNING
     */
    private void onClickGameRunning() {
        gameWorld.toggleKipperDirection();
        Assets.twistSound.play(Settings.volume * 2);
    }

    /**
     * called when click input when game state is GAME_OVER
     */
    private void onClickGameOver() {
        for (MenuButton mb: gameWorld.getGameOverButtons()){
            if (CollisionDetection.pointInRectangle(mb.getBoundingRectangle(), touchPoint)){
                if (mb.getCommand().equals(PLAY) && mb.isInPlace()){
                    Assets.buttonSound.play(Settings.volume);
                    state = GAME_OVER_TRANSITION;
                } else if (mb.getCommand().equals(OPEN_SCORES)){
                    Assets.buttonSound.play(Settings.volume);
                    gpgs.getLeaderBoard();
                } else if (mb.getCommand().equals(SHARE)){
                    gpgs.shareScore(gameWorld.getScore());
                }
            }
        }
    }
    //endregion

    //region folding for update methods

    /**
     * update in general, switch case determines what area to update
     * @param delta delta time
     */
    private void update(float delta) {
        switch (state) {
            case MENU_BEFORE_TRANSITION:
                updateMenuBeforeTransition(delta);
                break;
            case MENU_WAITING:
                updateMenuWaiting(delta);
                break;
            case MENU_TRANSITION:
                updateMenuTransition(delta);
                break;
            case GAME_BEFORE:
                updateGameBefore(delta);
                break;
            case GAME_PAUSED:
                updateGamePaused(delta);
                break;
            case GAME_RUNNING:
                updateGameRunning(delta);
                break;
            case GAME_OVER:
                updateGameOver(delta);
                break;
            case GAME_OVER_TRANSITION:
                updateGameOverTransition(delta);
                break;
        }
    }

    private void updateMenuBeforeTransition(float delta){
        boolean canMoveOn = true;
        for (MenuButton mb: menuWorld.getMenuButtons()){
            if (!mb.isMoving()){
                mb.moveIn();
                canMoveOn = false;
            } else {
                mb.update(delta);
                if (!mb.isInPlace()){
                    canMoveOn = false;
                }
            }
        }
        if (canMoveOn){
            state = MENU_WAITING;
        }
    }

    /**
     * method to update game when state equals MENU_WAITING
     * @param delta delta time
     */
    private void updateMenuWaiting(float delta) {
        //Does nothing for now
    }

    /**
     * method to update game when state equals MENU_TRANSITION
     * @param delta delta time
     */
    private void updateMenuTransition(float delta) {
        Assets.menuMusic.stop();
        if (!menuWorld.getMenuButtons().get(0).isMoving()) { // if first menu button isn't moving
            menuWorld.getMenuButtons().get(0).moveOffScreen(); // start it moving
        } else if (!menuWorld.getMenuButtons().get(1).isMoving() && menuWorld.getMenuButtons().get(0).getX() >= 4) { // if first menu button isn't moving
            menuWorld.getMenuButtons().get(1).moveOffScreen(); // start it moving
        } else if (!menuWorld.getMenuButtons().get(2).isMoving() && menuWorld.getMenuButtons().get(1).getX() >= 4) {
            menuWorld.getMenuButtons().get(2).moveOffScreen();
        } else if (menuWorld.getMenuButtons().get(2).getX() >= FRUSTUM_WIDTH){
            state = GAME_BEFORE;
        }
        for (MenuButton mb: menuWorld.getMenuButtons()){
            mb.update(delta);
        }
    }

    /**
     * method to update game when state equals GAME_BEFORE
     * @param delta delta time
     */
    private void updateGameBefore(float delta) {
        gameWorld.update(state, gameRenderer.getCam(), delta);
    }

    /**
     * method to update game when state equals GAME_RUNNING
     * @param delta delta time
     */
    private void updateGameRunning(float delta) {
        gameWorld.update(state, gameRenderer.getCam(), delta);

        for (Bar b: gameWorld.getBars()){
            if (CollisionDetection.overlapRectangles(b.getBoundingRectangle(), gameWorld.getKipper().getBoundingRectangle())) {
                    Assets.endGameSound.play(Settings.volume);
                state = GAME_OVER;
                Settings.saveScore(gameWorld.getScore());
                gpgs.submitScore(Settings.highscore);
                gpgs.showAd();

                if(Settings.highscore >= 2){
                    gpgs.unlockAchievement(TwistGame.FIRST_TIME);
                }
                if(Settings.highscore >= 10){
                    gpgs.unlockAchievement(TwistGame.BEGINNER_TWISTER);
                }
                if(Settings.highscore >= 25){
                    gpgs.unlockAchievement(TwistGame.NOVICE_TWISTER);
                }
                if(Settings.highscore >= 50){
                    gpgs.unlockAchievement(TwistGame.ADVANCED_TWISTER);
                }
                if(Settings.highscore >= 100){
                    gpgs.unlockAchievement(TwistGame.MASTER_TWISTER);
                }
            }
        }
    }

    /**
     * method to update game when state equals GAME_PAUSED
     * @param delta delta time
     */
    private void updateGamePaused(float delta) {

    }

    /**
     * method to update game when state equals GAME_OVER
     * @param delta delta time
     */
    private void updateGameOver(float delta) {
        gameWorld.update(state, gameRenderer.getCam(), delta);
    }

    private void updateGameOverTransition(float delta){
        if (!gameWorld.getGameOverButtons().get(0).isMoving()) { // if first menu button isn't moving
            gameWorld.getGameOverButtons().get(0).moveOffScreen(); // start it moving
        } else if (!gameWorld.getGameOverButtons().get(1).isMoving() && gameWorld.getGameOverButtons().get(0).getX() >= 4) { // if first menu button isn't moving
            gameWorld.getGameOverButtons().get(1).moveOffScreen(); // start it moving
        } else if (!gameWorld.getGameOverButtons().get(2).isMoving() && gameWorld.getGameOverButtons().get(1).getX() >= 4) {
            gameWorld.getGameOverButtons().get(2).moveOffScreen();
        } else if (gameWorld.getGameOverButtons().get(2).getX() >= FRUSTUM_WIDTH){
            resetGame();
            state = GAME_BEFORE;
        }
        for (MenuButton mb: gameWorld.getGameOverButtons()){
            mb.update(delta);
        }
    }

    //endregion

    //region folding for drawing methods
    private void draw() {
        switch (state) {
            case MENU_BEFORE_TRANSITION:
                drawMenuBeforeTransition();
                break;
            case GAME_OVER:
                drawGameOver();
                break;
            case GAME_RUNNING:
                drawGameRunning();
                break;
            case MENU_WAITING:
                drawMenuWaiting();
                break;
            case MENU_TRANSITION:
                drawMenuTransition();
                break;
            case GAME_BEFORE:
                drawGameBefore();
                break;
            case GAME_PAUSED:
                drawGamePaused();
                break;
            case GAME_OVER_TRANSITION:
                drawGameOver();
                break;
        }
    }

    private void drawMenuBeforeTransition(){
        menuRenderer.render();
    }
    private void drawGamePaused() {
        gameRenderer.render();
    }

    private void drawGameBefore() {
        gameRenderer.render();

        if (state == GAME_BEFORE && gameWorld.getKipper().getY() > 3){
            spriteBatch.draw(Assets.tappingHand, FRUSTUM_WIDTH / 2 - 1, gameWorld.getKipper().getY() + 4.5f, 2, 3);
        } else {
            spriteBatch.draw(Assets.tappingHand, FRUSTUM_WIDTH/2 - 1, FRUSTUM_HEIGHT/2, 2, 3);
        }


    }

    private void drawMenuTransition() {
        menuRenderer.render();
    }

    private void drawMenuWaiting() {
        menuRenderer.render();
    }

    private void drawGameRunning() {
        gameRenderer.render();
        spriteBatch.setProjectionMatrix(cam.combined);

        float width = Assets.font.getBounds(String.valueOf(gameWorld.getScore())).width/2;
        float height = Assets.font.getBounds("" + gameWorld.getScore()).height;
        Assets.font.draw(spriteBatch, String.valueOf(gameWorld.getScore()), FRUSTUM_WIDTH*10/2 - width, height);
    }

    private void drawGameOver() {
        gameRenderer.render();
        spriteBatch.setProjectionMatrix(cam.combined);

        Assets.font.setScale(0.1f);
        float width = Assets.font.getBounds("Highscore: " + Settings.highscore).width/2;
        float height = Assets.font.getBounds("Highscore" + Settings.highscore).height;
        Assets.font.draw(spriteBatch, "Highscore: " + Settings.highscore, FRUSTUM_WIDTH * 10 / 2 - width, 10 * 10 + height);
        Assets.font.setScale(0.25f);

        width = Assets.font.getBounds(String.valueOf(gameWorld.getScore())).width/2;
        height = Assets.font.getBounds("" + gameWorld.getScore()).height;
        Assets.font.draw(spriteBatch, String.valueOf(gameWorld.getScore()), FRUSTUM_WIDTH * 10 / 2 - width, 8 * 10 + height);
    }
    //endregion

    private void resetGame(){
        cam.position.set(FRUSTUM_WIDTH * 10 / 2, FRUSTUM_HEIGHT * 10 / 2, 0);
        gameWorld.reset();
        gameRenderer.resetCam();
        menuRenderer.resetCam();
        backgroundManager.reset();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
        if (state == GAME_RUNNING){
            state = GAME_PAUSED;
        }
    }

    @Override
    public void pause() {
        if (state == GAME_RUNNING){
            state = GAME_PAUSED;
        }
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

}
