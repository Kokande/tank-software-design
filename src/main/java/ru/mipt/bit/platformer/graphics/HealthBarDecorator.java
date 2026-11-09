package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static com.badlogic.gdx.math.MathUtils.clamp;

public class HealthBarDecorator implements Renderable {
    private static final float HEIGHT = 6f;
    private static final float GAP = 3f;
    private static final float BORDER = 1f;

    private final Renderable wrapped;
    private final Supplier<Rectangle> bounds;
    private final IntSupplier health;
    private final IntSupplier maxHealth;
    private final BooleanSupplier visible;
    private final Texture pixel;

    public HealthBarDecorator(Renderable wrapped, Supplier<Rectangle> bounds,
                              IntSupplier health, IntSupplier maxHealth, BooleanSupplier visible) {
        this.wrapped = wrapped;
        this.bounds = bounds;
        this.health = health;
        this.maxHealth = maxHealth;
        this.visible = visible;
        Pixmap pixels = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        try {
            pixels.setColor(1f, 1f, 1f, 1f);
            pixels.fill();
            pixel = new Texture(pixels);
        } finally {
            pixels.dispose();
        }
    }

    @Override
    public void render(Batch batch) {
        wrapped.render(batch);
        if (!visible.getAsBoolean()) return;

        Rectangle rectangle = bounds.get();
        float y = rectangle.y + rectangle.height + GAP;
        int maximum = maxHealth.getAsInt();
        float fraction = maximum > 0 ? clamp((float) health.getAsInt() / maximum, 0f, 1f) : 0f;
        float oldColor = batch.getPackedColor();
        try {
            batch.setColor(0.1f, 0.1f, 0.1f, 1f);
            batch.draw(pixel, rectangle.x, y, rectangle.width, HEIGHT);
            float width = Math.max(0f, rectangle.width - 2 * BORDER) * fraction;
            if (width > 0f) {
                batch.setColor(1f - fraction, fraction, 0f, 1f);
                batch.draw(pixel, rectangle.x + BORDER, y + BORDER, width, HEIGHT - 2 * BORDER);
            }
        } finally {
            batch.setPackedColor(oldColor);
        }
    }

    @Override
    public void dispose() {
        try {
            wrapped.dispose();
        } finally {
            pixel.dispose();
        }
    }
}
