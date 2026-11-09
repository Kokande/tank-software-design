package ru.mipt.bit.platformer.graphics.health;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxNativesLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.graphics.Renderable;
import ru.mipt.bit.platformer.objects.Tank;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HealthBarDecoratorTest {
    private final GL20 previousGl = Gdx.gl;
    private final GL20 previousGl20 = Gdx.gl20;
    private final Graphics previousGraphics = Gdx.graphics;
    private final List<Rectangle> draws = new ArrayList<>();
    private final Rectangle bounds = new Rectangle(10, 20, 42, 46);
    private final HealthBarVisibility visibility = new HealthBarVisibility();
    private final Tank tank = new Tank(new GridPoint2(), 0.4f);
    private int objectDraws;
    private int objectDisposals;
    private int textureDisposals;
    private float packedColor = 123f;
    private HealthBarDecorator decorator;
    private Batch batch;

    @BeforeEach
    void setUp() {
        GdxNativesLoader.load();
        Gdx.graphics = (Graphics) Proxy.newProxyInstance(Graphics.class.getClassLoader(),
                new Class<?>[]{Graphics.class}, (proxy, method, args) -> {
                    if (method.getName().equals("supportsExtension")) return false;
                    throw new AssertionError("Unexpected graphics call: " + method.getName());
                });
        // Exercise real texture ownership without requiring an OpenGL window.
        Gdx.gl = Gdx.gl20 = (GL20) Proxy.newProxyInstance(GL20.class.getClassLoader(),
                new Class<?>[]{GL20.class}, (proxy, method, args) -> {
                    if (method.getName().equals("glDeleteTexture")) textureDisposals++;
                    if (method.getReturnType() == int.class) return 1;
                    if (method.getReturnType() == boolean.class) return false;
                    return null;
                });
        batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(),
                new Class<?>[]{Batch.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getPackedColor": return packedColor;
                        case "setPackedColor": packedColor = (float) args[0]; break;
                        case "setColor": packedColor = 456f; break;
                        case "draw":
                            assertTrue(objectDraws > 0, "wrapped object must render before its bar");
                            draws.add(new Rectangle((float) args[1], (float) args[2],
                                    (float) args[3], (float) args[4]));
                            break;
                        default: fail("Unexpected batch call: " + method.getName());
                    }
                    return null;
                });
        Renderable wrapped = new Renderable() {
            public void render(Batch batch) { objectDraws++; }
            public void dispose() { objectDisposals++; }
        };
        decorator = new HealthBarDecorator(wrapped, () -> bounds,
                tank::getHealth, tank::getMaxHealth, visibility::isVisible);
    }

    @AfterEach
    void tearDown() {
        try {
            if (decorator != null) decorator.dispose();
        } finally {
            Gdx.gl = previousGl;
            Gdx.gl20 = previousGl20;
            Gdx.graphics = previousGraphics;
        }
    }

    @Test
    void togglesOverlayWithoutSuppressingTheObjectAndRestoresBatchColor() {
        decorator.render(batch);
        assertEquals(1, objectDraws);
        assertTrue(draws.isEmpty());

        visibility.toggle();
        decorator.render(batch);
        assertEquals(2, objectDraws);
        assertEquals(2, draws.size());
        assertEquals(new Rectangle(10, 69, 42, 6), draws.get(0));
        assertEquals(new Rectangle(11, 70, 40, 4), draws.get(1));
        assertEquals(123f, packedColor);

        visibility.toggle();
        draws.clear();
        decorator.render(batch);
        assertEquals(3, objectDraws);
        assertTrue(draws.isEmpty());
    }

    @Test
    void followsObjectBoundsAndScalesWithDamageIncludingEmptyHealth() {
        visibility.toggle();
        tank.takeDamage(25);
        bounds.setPosition(25, 35);
        decorator.render(batch);
        assertEquals(new Rectangle(26, 85, 30, 4), draws.get(1));

        tank.takeDamage(75);
        draws.clear();
        decorator.render(batch);
        assertEquals(1, draws.size(), "zero health leaves only the background");
        tank.takeDamage(1);
        draws.clear();
        decorator.render(batch);
        assertEquals(1, draws.size(), "negative health must not draw a negative width");
    }

    @Test
    void disposesWrappedGraphicsAndItsOwnTexture() {
        decorator.dispose();
        decorator = null;
        assertEquals(1, objectDisposals);
        assertEquals(1, textureDisposals);
    }
}
