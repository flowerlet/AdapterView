package shadowblade.com.example;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;

import shadowblade.com.adapterview.AbsAdapterNodeView;

/**
 * Created by ShadowBlade on 2016/12/3.
 */

public class ExampleAdapterNodeView extends AbsAdapterNodeView<ExampleAdapterNodeDatabase> {

    public ExampleAdapterNodeView(Context context) {
        super(context);
    }

    public ExampleAdapterNodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExampleAdapterNodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onCreate(Context context) {
        super.onCreate(context);
        database = new ExampleAdapterNodeDatabase(this);

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                float l = i * ElementWidth;
                float t = j * ElementHeight;
                ExampleAdapterNodeInfo nodeInfo = new ExampleAdapterNodeInfo(i + "," + j,
                        new RectF(l, t, l + ElementWidth, t + ElementHeight));
                database.data.put(nodeInfo.getID(), nodeInfo);
            }
        }

        canvasRect.set(-ElementWidth, -ElementHeight, ElementWidth * 101, ElementHeight * 101);

        normalizeRect();
        updateVisibleNodes();
        updateViews();
    }

    float ViewWidth;
    float ViewHeight;

    @Override
    public void updateParameters() {
        ViewWidth = ElementWidth / zoomScale;
        ViewHeight = ElementHeight / zoomScale;
    }

    public float lastZoomScale = -1;
    @Override
    public void updateViews() {
        if (lastZoomScale != zoomScale) {
            lastZoomScale = zoomScale;
            updateParameters();
        }
        for (ExampleAdapterNode node : database.visibleNodes) {
            PointF origin = transformPoint(node.nodeInfo.getCoordinate());
            node.getView().layout((int) origin.x,
                    (int) origin.y,
                    (int) (origin.x + ViewWidth),
                    (int) (origin.y + ViewHeight));
        }
        invalidate();
    }
}
