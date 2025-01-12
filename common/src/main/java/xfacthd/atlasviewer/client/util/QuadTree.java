package xfacthd.atlasviewer.client.util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class QuadTree<T> extends Rect2i
{
    private static final int MAX_DEPTH = 12;

    private final @Nullable QuadTree<T> @Nullable[] children;
    @Nullable
    private List<Entry<T>> entries = null;

    public QuadTree(int width, int height, int minSize)
    {
        this(0, 0, width, height, minSize, 0);
    }

    @SuppressWarnings("unchecked")
    private QuadTree(int x, int y, int width, int height, int minSize, int depth)
    {
        super(x, y, width, height);
        depth++;

        if (depth < MAX_DEPTH && width > minSize && width % 2 == 0)
        {
            this.children = new QuadTree[4];

            int childWidth = width / 2;
            int childHeight = height / 2;

            if (width == height / 2)
            {
                this.children[0] = new QuadTree<>(x, y, width, childHeight, minSize, depth);
                this.children[1] = null;
                this.children[2] = null;
                this.children[3] = new QuadTree<>(x, y + childHeight, width, childHeight, minSize, depth);
            }
            else if (height == width / 2)
            {
                this.children[0] = new QuadTree<>(x, y, childWidth, height, minSize, depth);
                this.children[1] = new QuadTree<>(x + childWidth, y, childWidth, height, minSize, depth);
                this.children[2] = null;
                this.children[3] = null;
            }
            else
            {
                this.children[0] = new QuadTree<>(x, y, childWidth, childHeight, minSize, depth);
                this.children[1] = new QuadTree<>(x + childWidth, y, childWidth, childHeight, minSize, depth);
                this.children[2] = new QuadTree<>(x + childWidth, y + childHeight, childWidth, childHeight, minSize, depth);
                this.children[3] = new QuadTree<>(x, y + childHeight, childWidth, childHeight, minSize, depth);
            }
        }
        else
        {
            this.children = null;
        }
    }

    public void insert(T item, Function<T, Rect2i> sizeFactory)
    {
        insert(item, sizeFactory.apply(item));
    }

    private void insert(T item, Rect2i size)
    {
        if (children != null)
        {
            for (int i = 0; i < 4; i++)
            {
                QuadTree<T> child = children[i];
                if (child != null && child.contains(size))
                {
                    child.insert(item, size);
                    return;
                }
            }
        }

        if (entries == null)
        {
            entries = new ArrayList<>();
        }
        entries.add(new Entry<>(item, size));
    }

    public void trim()
    {
        if (children != null)
        {
            for (int i = 0; i < children.length; i++)
            {
                QuadTree<T> child = children[i];
                if (child == null) continue;

                child.trim();
                if (child.isEmpty())
                {
                    children[i] = null;
                }
            }
        }
    }

    @Nullable
    public T find(int x, int y)
    {
        if (entries != null)
        {
            for (Entry<T> e : entries)
            {
                if (e.contains(x, y))
                {
                    return e.item;
                }
            }
        }

        if (children != null)
        {
            for (int i = 0; i < 4; i++)
            {
                QuadTree<T> child = children[i];
                if (child != null && child.contains(x, y))
                {
                    T item = child.find(x, y);
                    if (item != null)
                    {
                        return item;
                    }
                }
            }
        }

        return null;
    }

    public boolean isEmpty()
    {
        if (children != null)
        {
            for (QuadTree<T> child : children)
            {
                if (child != null)
                {
                    return false;
                }
            }
        }
        return entries == null;
    }

    public int depth()
    {
        if (children != null)
        {
            int d = 0;
            for (QuadTree<T> child : children)
            {
                if (child != null)
                {
                    d = Math.max(child.depth(), d);
                }
            }
            return d + 1;
        }
        return 1;
    }

    public Rect2i minSize()
    {
        if (children != null)
        {
            Rect2i minRect = this;
            for (QuadTree<T> child : children)
            {
                if (child != null)
                {
                    Rect2i childRect = child.minSize();
                    if (childRect.width < width || childRect.height < height)
                    {
                        minRect = childRect;
                    }
                }
            }
            return minRect;
        }
        return this;
    }

    private static final class Entry<T> extends Rect2i
    {
        private final T item;

        private Entry(T item, Rect2i size)
        {
            super(size.x, size.y, size.width, size.height);
            this.item = item;
        }
    }
}
