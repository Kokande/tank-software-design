package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.objects.Bullet;

import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

public class BulletGraphics implements Renderable {
    private final Bullet bullet;
    private final TiledMapTileLayer ground;
    private final Texture texture;
    private final Rectangle rectangle = new Rectangle(0, 0, 10, 10);

    public BulletGraphics(TiledMapTileLayer ground, Bullet bullet) {
        this.ground = ground;
        this.bullet = bullet;
        Pixmap pixels = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixels.setColor(1f, 0.8f, 0.1f, 1f);
        pixels.fillCircle(5, 5, 4);
        texture = new Texture(pixels);
        pixels.dispose();
    }

    @Override
    public void render(Batch batch) {
        moveRectangleAtTileCenter(ground, rectangle, bullet.getCoordinates());
        batch.draw(texture, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    @Override
    public void dispose() { texture.dispose(); }
}
