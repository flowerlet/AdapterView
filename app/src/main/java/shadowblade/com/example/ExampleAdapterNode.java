package shadowblade.com.example;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import shadowblade.com.adapterview.IAdapterNode;

/**
 * Created by ShadowBlade on 2016/12/3.
 */

public class ExampleAdapterNode extends ViewGroup implements IAdapterNode<ExampleAdapterNodeInfo> {

    protected ImageView imageView;
    protected TextView textView;

    public ExampleAdapterNode(ExampleAdapterNodeView nodeView) {
        super(nodeView.getContext());
        imageView = new ImageView(nodeView.getContext());
        imageView.setBackgroundColor(Color.parseColor(GetRandColorCode()));

        textView = new TextView(nodeView.getContext());

        addView(imageView);
        addView(textView);

        nodeView.addView(this);
    }

    public static String GetRandColorCode() {
        String r, g, b;
        Random random = new Random();
        r = Integer.toHexString(random.nextInt(256)).toUpperCase();
        g = Integer.toHexString(random.nextInt(256)).toUpperCase();
        b = Integer.toHexString(random.nextInt(256)).toUpperCase();

        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
        b = b.length() == 1 ? "0" + b : b;

        return "#" + r + g + b;
    }

    public ExampleAdapterNodeInfo nodeInfo;

    @Override
    public void update(ExampleAdapterNodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
        textView.setText("(" + nodeInfo.coordinate.x + ", " + nodeInfo.coordinate.y + ")");
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int w = r - l;
        int h = b - t;
        int fl = (int) (w * 0.1f);
        int ft = (int) (h * 0.1f);
        int fr = (int) (w * 0.9f);
        int fb = (int) (h * 0.9f);
        imageView.layout(fl, ft, fr, fb);
        textView.layout(fl, ft, fr, fb);
    }
}
