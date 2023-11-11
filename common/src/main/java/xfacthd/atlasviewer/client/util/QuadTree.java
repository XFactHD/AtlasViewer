package xfacthd.atlasviewer.client.util;

import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class QuadTree<T>
{
    private static final int MAX_DEPTH = 12;

    private final Rect2i rect;
    private final List<QuadTree<T>> children;
    private final Rect2i[] childRects;

    private final List<Entry<T>> entries = new ArrayList<>();

    public QuadTree(Rect2i rect, int minSize)
    {
        this(rect, minSize, 0);
    }

    private QuadTree(Rect2i rect, int minSize, int depth)
    {
        this.rect = rect;
        depth++;

        if (depth < MAX_DEPTH && rect.getWidth() > minSize && rect.getWidth() % 2 == 0)
        {
            children = new ArrayList<>(4);
            childRects = new Rect2i[4];

            int childWidth = rect.getWidth() / 2;
            int childHeight = rect.getHeight() / 2;

            if (rect.getWidth() == rect.getHeight() / 2)
            {
                childRects[0] = new Rect2i(rect.getX(), rect.getY(), rect.getWidth(), childHeight);
                childRects[1] = null;
                childRects[2] = null;
                childRects[3] = new Rect2i(rect.getX(), rect.getY() + childHeight, rect.getWidth(), childHeight);

                children.add(new QuadTree<>(childRects[0], minSize, depth));
                children.add(null);
                children.add(null);
                children.add(new QuadTree<>(childRects[3], minSize, depth));
            }
            else if (rect.getHeight() == rect.getWidth() / 2)
            {
                childRects[0] = new Rect2i(rect.getX(), rect.getY(), childWidth, rect.getHeight());
                childRects[1] = new Rect2i(rect.getX() + childWidth, rect.getY(), childWidth, rect.getHeight());
                childRects[2] = null;
                childRects[3] = null;

                children.add(new QuadTree<>(childRects[0], minSize, depth));
                children.add(new QuadTree<>(childRects[1], minSize, depth));
                children.add(null);
                children.add(null);
            }
            else
            {
                childRects[0] = new Rect2i(rect.getX(), rect.getY(), childWidth, childHeight);
                childRects[1] = new Rect2i(rect.getX() + childWidth, rect.getY(), childWidth, childHeight);
                childRects[2] = new Rect2i(rect.getX() + childWidth, rect.getY() + childHeight, childWidth, childHeight);
                childRects[3] = new Rect2i(rect.getX(), rect.getY() + childHeight, childWidth, childHeight);

                for (int i = 0; i < 4; i++)
                {
                    children.add(new QuadTree<>(childRects[i], minSize, depth));
                }
            }
        }
        else
        {
            children = null;
            childRects = null;
        }
    }

    public void insert(T item, Function<T, Rect2i> sizeFactory)
    {
        insert(item, sizeFactory.apply(item));
    }

    private void insert(T item, Rect2i size)
    {
        if (childRects != null)
        {
            for (int i = 0; i < 4; i++)
            {
                if (childRects[i] != null && rectContains(childRects[i], size))
                {
                    children.get(i).insert(item, size);
                    break;
                }
            }
        }

        entries.add(new Entry<>(item, size));
    }

    public T find(int x, int y)
    {
        Rect2i point = new Rect2i(x, y, 1, 1);
        return find(point);
    }

    private T find(Rect2i point)
    {
        if (!entries.isEmpty())
        {
            for (Entry<T> e : entries)
            {
                if (rectContains(e.size, point))
                {
                    return e.item;
                }
            }
        }

        if (childRects != null)
        {
            for (int i = 0; i < 4; i++)
            {
                if (childRects[i] != null && rectContains(childRects[i], point))
                {
                    T item = children.get(i).find(point);
                    if (item != null)
                    {
                        return item;
                    }
                }
            }
        }

        return null;
    }

    private static boolean rectContains(Rect2i r1, Rect2i r2)
    {
        return r1.contains(r2.getX(), r2.getY()) && r1.contains(r2.getX() + r2.getWidth(), r2.getY() + r2.getHeight());
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
            Rect2i minRect = rect;
            for (QuadTree<T> child : children)
            {
                if (child != null)
                {
                    Rect2i childRect = child.minSize();
                    if (childRect.getWidth() < rect.getWidth() || childRect.getHeight() < rect.getHeight())
                    {
                        minRect = childRect;
                    }
                }
            }
            return minRect;
        }
        return rect;
    }

    private record Entry<T>(T item, Rect2i size)
    {

    }
}
