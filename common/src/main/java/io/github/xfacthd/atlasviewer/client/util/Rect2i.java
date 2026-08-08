package io.github.xfacthd.atlasviewer.client.util;

public class Rect2i {
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;

    public Rect2i(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final int getMaxX() {
        return x + width;
    }

    public final int getMaxY() {
        return y + height;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final boolean contains(int x, int y) {
        return x >= this.x && y >= this.y && x < this.x + width && y < this.y + height;
    }

    public final boolean contains(Rect2i rect) {
        return contains(rect.x, rect.y) && contains(rect.x + rect.width, rect.y + rect.height);
    }

    public final boolean intersects(Rect2i rect) {
        return x < rect.getMaxX() && getMaxX() > rect.x && y < rect.getMaxY() && getMaxY() > rect.y;
    }
}
