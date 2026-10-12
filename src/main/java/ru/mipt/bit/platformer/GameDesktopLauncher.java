package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.graphics.Renderable;
import ru.mipt.bit.platformer.graphics.TankGraphics;
import ru.mipt.bit.platformer.graphics.TreeGraphics;
import ru.mipt.bit.platformer.objects.Tank;
import ru.mipt.bit.platformer.objects.Tree;
import ru.mipt.bit.platformer.mechanics.GameMap;
import ru.mipt.bit.platformer.controls.ControlsProcessor;
import ru.mipt.bit.platformer.controls.GdxKeyboardState;
import ru.mipt.bit.platformer.controls.KeyBindings;
import ru.mipt.bit.platformer.util.TileMovement;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class GameDesktopLauncher implements ApplicationListener {
    private static final String LEVEL_FILE = "level.tmx";
    private static final String TANK_TEXTURE = "images/tank_blue.png";
    private static final String TREE_TEXTURE = "images/greenTree.png";

    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 1024;
    private static final float PLAYER_MOVEMENT_SPEED = 0.4f;

    private Batch batch;

    private TiledMap level;
    private MapRenderer levelRenderer;
    private TileMovement tileMovement;

    private Tank player;
    private ControlsProcessor controlsProcessor;

    private final List<Renderable> renderables = new ArrayList<>();

    @Override
    public void create() {
        batch = new SpriteBatch();
        // load level tiles
        TiledMapTileLayer groundLayer = loadLevelTiles();
        // Player
        addPlayer();
        // Obstacles
        GameMap map = mapObstacles(groundLayer);

        controlsProcessor = new ControlsProcessor(player, map, new GdxKeyboardState(), KeyBindings.wasdAndArrows());
    }

    private GameMap mapObstacles(TiledMapTileLayer groundLayer) {
        Tree[] trees = {new Tree(1, 3)};
        for (Tree tree : trees) {
            renderables.add(new TreeGraphics(groundLayer, tree, TREE_TEXTURE));
        }
        return new GameMap(trees);
    }

    private void addPlayer() {
        player = new Tank(new GridPoint2(1, 1), PLAYER_MOVEMENT_SPEED);
        renderables.add(new TankGraphics(tileMovement, player, TANK_TEXTURE));
    }

    private TiledMapTileLayer loadLevelTiles() {
        level = new TmxMapLoader().load(LEVEL_FILE);
        levelRenderer = createSingleLayerMapRenderer(level, batch);
        TiledMapTileLayer groundLayer = getSingleLayer(level);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
        return groundLayer;
    }

    @Override
    public void render() {
        // clear the screen
        clearTheScreen();
        // get time passed since the last render
        tickRender();
        // render each tile of the level
        levelRenderer.render();
        // start recording all drawing commands
        batch.begin();
        // render player and obstacles
        renderObjects();
        // submit all drawing requests
        batch.end();
    }

    private void tickRender() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        controlsProcessor.processInput();
        player.update(deltaTime);
    }

    private void renderObjects() {
        for (Renderable renderable : renderables) {
            renderable.render(batch);
        }
    }

    private void clearTheScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
        // do not react to window resizing
    }

    @Override
    public void pause() {
        // game doesn't get paused
    }

    @Override
    public void resume() {
        // game doesn't get paused
    }

    @Override
    public void dispose() {
        // dispose of all the native resources (classes which implement com.badlogic.gdx.utils.Disposable)
        for (Renderable renderable : renderables) {
            renderable.dispose();
        }
        level.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}
