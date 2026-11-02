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
import ru.mipt.bit.platformer.level.FileLevelGenerator;
import ru.mipt.bit.platformer.level.Level;
import ru.mipt.bit.platformer.level.LevelGenerator;
import ru.mipt.bit.platformer.level.RandomLevelGenerator;
import ru.mipt.bit.platformer.level.RandomTanksLevelGenerator;
import ru.mipt.bit.platformer.graphics.TankGraphics;
import ru.mipt.bit.platformer.graphics.TreeGraphics;
import ru.mipt.bit.platformer.objects.Tank;
import ru.mipt.bit.platformer.objects.Tree;
import ru.mipt.bit.platformer.controls.Command;
import ru.mipt.bit.platformer.controls.ControlStrategy;
import ru.mipt.bit.platformer.controls.ControlsProcessor;
import ru.mipt.bit.platformer.controls.GdxKeyboardState;
import ru.mipt.bit.platformer.controls.KeyBindings;
import ru.mipt.bit.platformer.controls.KeyboardControlStrategy;
import ru.mipt.bit.platformer.controls.RandomControlStrategy;
import ru.mipt.bit.platformer.controls.KeyboardShootStrategy;
import ru.mipt.bit.platformer.mechanics.GameWorld;
import ru.mipt.bit.platformer.objects.Bullet;
import ru.mipt.bit.platformer.graphics.BulletGraphics;
import ru.mipt.bit.platformer.graphics.WorldGraphics;
import ru.mipt.bit.platformer.util.TileMovement;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class GameDesktopLauncher implements ApplicationListener {
    // tile map with the ground, it is not the level description
    private static final String GROUND_TILES_FILE = "level.tmx";
    private static final String TANK_TEXTURE = "images/tank_blue.png";
    private static final String TREE_TEXTURE = "images/greenTree.png";

    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 1024;
    private static final float PLAYER_MOVEMENT_SPEED = 0.4f;
    private static final float AI_MOVEMENT_SPEED = 0.4f;
    private static final int RANDOM_TREES_COUNT = 10;
    private static final int RANDOM_TANKS_COUNT = 5;

    // path to a text file with the level, the level is generated randomly when it is null
    private final String levelDescriptionFile;

    private Batch batch;

    private TiledMap level;
    private MapRenderer levelRenderer;
    private TileMovement tileMovement;

    private Tank player;
    private final List<Tank> aiTanks = new ArrayList<>();
    private ControlsProcessor controlsProcessor;

    private GameWorld world;
    private WorldGraphics worldGraphics;

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
        createWorld(groundLayer);
        for (Tree tree : levelContent.getTrees()) world.add(tree);
        // Player and AI tanks
        addTanks(levelContent);
        controlsProcessor = new ControlsProcessor(createControlStrategies());
    }

    private void createWorld(TiledMapTileLayer groundLayer) {
        world = new GameWorld(groundLayer.getWidth(), groundLayer.getHeight());
        worldGraphics = new WorldGraphics(world, object -> {
            if (object instanceof Tank) return new TankGraphics(tileMovement, (Tank) object, TANK_TEXTURE);
            if (object instanceof Tree) return new TreeGraphics(groundLayer, (Tree) object, TREE_TEXTURE);
            if (object instanceof Bullet) return new BulletGraphics(groundLayer, (Bullet) object);
            throw new IllegalArgumentException("Unsupported graphics object: " + object.getClass());
        });
    }

    private List<ControlStrategy> createControlStrategies() {
        List<ControlStrategy> strategies = new ArrayList<>();
        GdxKeyboardState keyboard = new GdxKeyboardState();
        strategies.add(whileAlive(player, new KeyboardControlStrategy(player, world, keyboard, KeyBindings.wasdAndArrows())));
        strategies.add(whileAlive(player, new KeyboardShootStrategy(player, keyboard)));
        for (Tank tank : aiTanks) {
            strategies.add(whileAlive(tank, new RandomControlStrategy(tank, world)));
        }
        return strategies;
    }

    private ControlStrategy whileAlive(Tank tank, ControlStrategy strategy) {
        return () -> world.contains(tank) ? strategy.nextCommand() : java.util.Optional.empty();
    }

    private LevelGenerator createLevelGenerator(TiledMapTileLayer groundLayer) {
        return new RandomTanksLevelGenerator(createObstaclesGenerator(groundLayer),
                groundLayer.getWidth(), groundLayer.getHeight(), RANDOM_TANKS_COUNT);
    }

    private LevelGenerator createObstaclesGenerator(TiledMapTileLayer groundLayer) {
        if (levelDescriptionFile != null) {
            return new FileLevelGenerator(Paths.get(levelDescriptionFile), groundLayer.getWidth(), groundLayer.getHeight());
        }
        return new RandomLevelGenerator(groundLayer.getWidth(), groundLayer.getHeight(), RANDOM_TREES_COUNT);
    }

    private void addTanks(Level levelContent) {
        player = addTank(levelContent.getPlayerPosition(), PLAYER_MOVEMENT_SPEED);
        for (GridPoint2 position : levelContent.getTankPositions()) {
            aiTanks.add(addTank(position, AI_MOVEMENT_SPEED));
        }
    }

    private Tank addTank(GridPoint2 position, float movementSpeed) {
        Tank tank = new Tank(position, movementSpeed);
        world.add(tank);
        return tank;
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
        List<Command> commands = controlsProcessor.createCommands();
        controlsProcessor.executeCommands(commands);
        world.live(deltaTime);
    }

    private void renderObjects() {
        worldGraphics.render(batch);
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
        worldGraphics.dispose();
        level.dispose();
        batch.dispose();
    }

    static String chooseLevelDescriptionFile(String[] args) {
        if (args.length == 0 || (args.length == 1 && "--random".equals(args[0]))) {
            return null;
        }
        if (args.length == 2 && "--level".equals(args[0])) {
            return args[1];
        }
        // Preserve the original explicit file-path argument.
        if (args.length == 1 && !args[0].startsWith("--")) {
            return args[0];
        }
        throw new IllegalArgumentException("Usage: [--random | --level <path> | <path>]");
    }

    public static void main(String[] args) {
        String levelDescriptionFile = chooseLevelDescriptionFile(args);
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        new Lwjgl3Application(new GameDesktopLauncher(levelDescriptionFile), config);
    }
}
