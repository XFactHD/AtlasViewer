package xfacthd.atlasviewer.client.util;

import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class QuadTree<T>
{
    private final List<QuadTree<T>> children = new ArrayList<>(4);
    private final Rect2i[] childRects = new Rect2i[4];

    private final List<Entry<T>> entries = new ArrayList<>();

    public QuadTree(Rect2i rect, int minSize)
    {
        if (rect.getWidth() > minSize && rect.getWidth() % 2 == 0)
        {
            int childWidth = rect.getWidth() / 2;
            int childHeight = rect.getHeight() / 2;

            if (rect.getWidth() == rect.getHeight() / 2)
            {
                childRects[0] = new Rect2i(rect.getX(), rect.getY(), rect.getWidth(), childHeight);
                childRects[1] = null;
                childRects[2] = null;
                childRects[3] = new Rect2i(rect.getX(), rect.getY() + childHeight, rect.getWidth(), childHeight);

                children.add(new QuadTree<>(childRects[0], minSize));
                children.add(null);
                children.add(null);
                children.add(new QuadTree<>(childRects[3], minSize));
            }
            else if (rect.getHeight() == rect.getWidth() / 2)
            {
                childRects[0] = new Rect2i(rect.getX(), rect.getY(), childWidth, rect.getHeight());
                childRects[1] = new Rect2i(rect.getX() + childWidth, rect.getY(), childWidth, rect.getHeight());
                childRects[2] = null;
                childRects[3] = null;

                children.add(new QuadTree<>(childRects[0], minSize));
                children.add(new QuadTree<>(childRects[1], minSize));
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
                    children.add(new QuadTree<>(childRects[i], minSize));
                }
            }
        }
    }

    public void insert(T item, Function<T, Rect2i> sizeFactory) { insert(item, sizeFactory.apply(item)); }

    private void insert(T item, Rect2i size)
    {
        for (int i = 0; i < 4; i++)
        {
            if (childRects[i] != null && rectContains(childRects[i], size))
            {
                children.get(i).insert(item, size);
                break;
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

        return null;
    }

    private static boolean rectContains(Rect2i r1, Rect2i r2)
    {
        return r1.contains(r2.getX(), r2.getY()) && r1.contains(r2.getX() + r2.getWidth(), r2.getY() + r2.getHeight());
    }

    private record Entry<T>(T item, Rect2i size)
    {

    }
}
