package shadowblade.com.example;

import android.graphics.PointF;
import android.graphics.RectF;

import shadowblade.com.adapterview.IAdapterNodeInfo;

/**
 * Created by ShadowBlade on 2016/12/3.
 */

public class ExampleAdapterNodeInfo implements IAdapterNodeInfo {

    protected String id;
    protected PointF coordinate = new PointF();
    protected RectF area = new RectF();

    public ExampleAdapterNodeInfo(String id, RectF area) {
        this.id = id;
        coordinate.set(area.left, area.top);
        this.area = area;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public PointF getCoordinate() {
        return coordinate;
    }

    @Override
    public RectF getArea() {
        return area;
    }
}
