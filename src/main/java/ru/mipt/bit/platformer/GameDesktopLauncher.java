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
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.level.FileLevelGenerator;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.LevelGenerator;
import ru.mipt.bit.platformer.level.RandomLevelGenerator;
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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class GameDesktopLauncher implements ApplicationListener {
    // tile map with the ground, it is not the level description
    private static final String GROUND_TILES_FILE = "level.tmx";
    // text file with trees and the player, it is used when no other file is passed as an argument
    private static final String DEFAULT_LEVEL_DESCRIPTION_FILE = "level.dat";
    private static final String TANK_TEXTURE = "images/tank_blue.png";
    private static final String TREE_TEXTURE = "images/greenTree.png";

    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 1024;
    private static final float PLAYER_MOVEMENT_SPEED = 0.4f;
    private static final int RANDOM_TREES_COUNT = 10;

    // path to a text file with the level, the level is generated randomly when it is null
    private final String levelDescriptionFile;

    private Batch batch;

    private TiledMap level;
    private MapRenderer levelRenderer;
    private TileMovement tileMovement;

    private Tank player;
    private ControlsProcessor controlsProcessor;

    private final List<Renderable> renderables = new ArrayList<>();

    public GameDesktopLauncher(String levelDescriptionFile) {
        this.levelDescriptionFile = levelDescriptionFile;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        // load level tiles
        TiledMapTileLayer groundLayer = loadLevelTiles();
        // fill the level
        Level levelContent = createLevelGenerator(groundLayer).generate();
        // Player
        addPlayer(levelContent);
        // Obstacles
        GameMap map = mapObstacles(groundLayer, levelContent);

        controlsProcessor = new ControlsProcessor(player, map, new GdxKeyboardState(), KeyBindings.wasdAndArrows());
    }

    private LevelGenerator createLevelGenerator(TiledMapTileLayer groundLayer) {
        if (levelDescriptionFile != null) {
            return new FileLevelGenerator(Paths.get(levelDescriptionFile), groundLayer.getHeight());
        }
        return new RandomLevelGenerator(groundLayer.getWidth(), groundLayer.getHeight(), RANDOM_TREES_COUNT);
    }

    private GameMap mapObstacles(TiledMapTileLayer groundLayer, Level levelContent) {
        Tree[] trees = levelContent.getTrees().toArray(new Tree[0]);
        for (Tree tree : trees) {
            renderables.add(new TreeGraphics(groundLayer, tree, TREE_TEXTURE));
        }
        return new GameMap(trees);
    }

    private void addPlayer(Level levelContent) {
        player = new Tank(levelContent.getPlayerPosition(), PLAYER_MOVEMENT_SPEED);
        renderables.add(new TankGraphics(tileMovement, player, TANK_TEXTURE));
    }

    private TiledMapTileLayer loadLevelTiles() {
        level = new TmxMapLoader().load(GROUND_TILES_FILE);
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

    private static String chooseLevelDescriptionFile(String[] args) {
        // the only optional argument is a path to a text file with the level
        if (args.length > 0) {
            return args[0];
        }
        if (Files.exists(Paths.get(DEFAULT_LEVEL_DESCRIPTION_FILE))) {
            return DEFAULT_LEVEL_DESCRIPTION_FILE;
        }
        // no level description, the level is generated randomly
        return null;
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        new Lwjgl3Application(new GameDesktopLauncher(chooseLevelDescriptionFile(args)), config);
    }
}
