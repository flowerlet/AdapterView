package shadowblade.com.adapterview;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by ShadowBlade on 2016/12/3.
 */

public abstract class AbsAdapterNodeView<T extends AbsAdapterNodeDatabase<?, ?>> extends ViewGroup {
    public AbsAdapterNodeView(Context context) {
        super(context);
        onCreate(context);
    }

    public AbsAdapterNodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public AbsAdapterNodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate(context);
    }

    protected ScaleGestureDetector mScaleGestureDetector;
    protected GestureDetector mGestureDetector;

    // 视图宽度
    protected float mWidth;
    // 视图高度
    protected float mHeight;

    // 视口宽度（标准化后）
    public float normalizedWidth;
    // 视口高度（标准化后）
    public float normalizedHeight;

    // 单位元素节点宽度
    public float ElementWidth;
    // 单位元素节点高度
    public float ElementHeight;

    // 是否可移动（缩放操作同时受限）
    public boolean enableMove = true;
    // 是否可缩放
    public boolean enableZoom = true;

    // 视图原点
    public PointF viewOrigin = new PointF(0, 0);
    // 画布大小
    public RectF canvasRect = new RectF();
    // 视口矩形
    protected RectF normalizedRect = new RectF();
    // 默认视口扩展长度
    public float Expansion;
    // 扩展后的矩形
    public RectF expansionRect = new RectF();
    // 缩放级别
    public float zoomScale = 1;
    public float maxZoomScale = Float.MAX_VALUE;
    // 最小缩放比例
    public static final float MinZoomScale = 1;

    // 数据集合，应该在构造方法中构造数据集
    public T database;

    // 是否自动加载区域外信息
    public boolean automaticRefreshEnable = true;

    public void onCreate(Context context) {

        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mGestureDetector = new GestureDetector(context, mGestureListener);

        // 默认的视图宽高为屏幕大小
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getApplicationContext().
                getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mWidth = displayMetrics.widthPixels;
        mHeight = displayMetrics.heightPixels;

        // 默认子元素大小
        ElementWidth = displayMetrics.widthPixels * 0.25f;
        ElementHeight = ElementWidth;
        Expansion = displayMetrics.widthPixels * 0.5f;

        updateZoom(zoomScale);
    }
    // 更新视图参数
    public abstract void updateParameters();
    // 视图移动时触发更新的阈值
    public float UpdateOffset;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mWidth = r - l;
        mHeight = b - t;
        UpdateOffset = mWidth * 0.5f;
        mEnableScrollOffset = Math.min(mWidth, mHeight) * 0.05f;
        normalizedWidth = mWidth * zoomScale;
        normalizedHeight = mHeight * zoomScale;
        normalizeRect();
    }

    // 更新缩放参数
    public void updateZoom(float zoom) {
        this.zoomScale = zoom;
        if (zoomScale > maxZoomScale) zoomScale = maxZoomScale;
        if (zoomScale < MinZoomScale) zoomScale = MinZoomScale;
        // 计算标准化后的宽、高，其值相当于单位宽高与缩放系数的乘积
        normalizedWidth = mWidth * zoomScale;
        normalizedHeight = mHeight * zoomScale;
    }

    // 计算视图的矩形框（以及扩展矩形框）
    public RectF normalizeRect() {
        if (viewOrigin.x < canvasRect.left) viewOrigin.x = canvasRect.left;
        if (viewOrigin.x > canvasRect.right - normalizedWidth) viewOrigin.x = canvasRect.right - normalizedWidth;
        if (viewOrigin.y < canvasRect.top) viewOrigin.y = canvasRect.top;
        if (viewOrigin.y > canvasRect.bottom - normalizedHeight) viewOrigin.y = canvasRect.bottom - normalizedHeight;
        normalizedRect.set(viewOrigin.x, viewOrigin.y,
                viewOrigin.x + normalizedWidth, viewOrigin.y + normalizedHeight);
        // 视口大于画布的处理
        if (normalizedWidth > canvasRect.width()) viewOrigin.x =
                canvasRect.left - (normalizedWidth - canvasRect.width()) * 0.5f;
        if (normalizedHeight > canvasRect.height()) viewOrigin.y =
                canvasRect.top - (normalizedHeight - canvasRect.height()) * 0.5f;

        // 计算扩展矩形的参数
        float expansion = Expansion * zoomScale;
        float left = normalizedRect.left - expansion;
        float top = normalizedRect.top - expansion;
        expansionRect.set(left, top, left + normalizedRect.width() * 2, top + normalizedRect.height() * 2);
        return normalizedRect;
    }

    // 更新所有可见节点
    public void updateVisibleNodes() {
        database.generateVisibleNodes(expansionRect);
    }
    // 更新视图及其所有子元素
    public abstract void updateViews();

    // 设置画布中心
    protected void setViewCenter(PointF coordinate, PointF origin) {
        viewOrigin.set(coordinate.x - normalizedWidth * origin.x, coordinate.y - normalizedHeight * origin.y);
        normalizeRect();
        updateVisibleNodes();
        updateViews();
    }

    // 将一个屏幕中的坐标转换为视图中坐标系的坐标
    public PointF transformPoint(PointF origin) {
        // 偏移量
        float dx = origin.x - viewOrigin.x;
        float dy = origin.y - viewOrigin.y;
        return new PointF(dx / normalizedWidth * mWidth, dy / normalizedHeight * mHeight);
    }

    /* 手势识别部分 */
    // 触摸点数量
    protected int mLastTouchCount = 0;
    // 上一个触摸点
    protected PointF mLastTouchPoint = new PointF();
    // 上个触发更新的手势点位置
    protected PointF mLastUpdatePoint = new PointF();
    // 滑动触发距离
    protected float mEnableScrollOffset = 5;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchPoint.set(ev.getX(), ev.getY());
                switch (ev.getPointerCount()) {
                    case 1:
                        mGestureDetector.onTouchEvent(ev);
                        break;
                    case 2:
                        mScaleGestureDetector.onTouchEvent(ev);
                        break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - mLastTouchPoint.x;
                float dy = ev.getY() - mLastTouchPoint.y;
                if (dx * dx + dy * dy > mEnableScrollOffset) return true;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                lastVelocityX = 0;
//                lastVelocityY = 0;
                return true;
            default:
                mLastTouchCount = event.getPointerCount();
                boolean result = mScaleGestureDetector.onTouchEvent(event);
                result = mGestureDetector.onTouchEvent(event) || result;
                return super.onTouchEvent(event) || result;
        }
    }

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if (mLastTouchCount == 1) mLastTouchPoint.set(e.getX(), e.getY());
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mLastTouchCount != 1 || !enableMove) return false;
            float dx = viewOrigin.x + distanceX * zoomScale;
            float dy = viewOrigin.y + distanceY * zoomScale;
//            lastVelocityX = distanceX * zoomScale;
//            lastVelocityY = distanceY * zoomScale;
            viewOrigin.set(dx, dy);
            // 移动原点坐标后更新视口
            normalizeRect();
            // 累计偏移量后，重新确定可见节点
            if (automaticRefreshEnable) {
                dx = viewOrigin.x - mLastUpdatePoint.x;
                dy = viewOrigin.y - mLastUpdatePoint.y;
                if (dx * dx + dy * dy > UpdateOffset * zoomScale) {
                    mLastUpdatePoint.set(viewOrigin);
                    updateVisibleNodes();
                }
            }
            updateViews();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {}

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    // 缩放手势回调
    ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (mLastTouchCount != 2 || !enableMove || !enableZoom) return false;
            zoomScale -= (detector.getScaleFactor() - 1) / 5.0f;
            updateZoom(zoomScale);
            viewOrigin.set(normalizedRect.centerX() - normalizedWidth * 0.5f,
                    normalizedRect.centerY() - normalizedHeight * 0.5f);
            normalizeRect();
            updateViews();
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            updateVisibleNodes();
            updateViews();
        }
    };

}
