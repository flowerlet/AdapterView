package shadowblade.com.adapterview;

import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shadowblade.com.component.BaseReusablePool;

/**
 * Created by ShadowBlade on 2016/12/3.
 * 节点类
 * 节点类关联一个节点信息类T
 * 节点类中会操作一个view作为在视图中显示的view
 */

public abstract class AbsAdapterNodeDatabase<T extends IAdapterNodeInfo, N extends IAdapterNode<T>> implements BaseReusablePool.IUpdater<N, T> {

    public Map<String, T> data = new HashMap<>();

//    public RectF dataArea = new RectF();

    // 复用池存储节点，关联节点信息
    public BaseReusablePool<N, T> nodePool = new BaseReusablePool<>(this);

    // 生成节点的操作交给子类实现
    @Override
    public abstract N instantiate(T t);

    @Override
    public void dequeueUpdate(N n, T t) {
        n.update(t);
        n.getView().setVisibility(View.VISIBLE);
    }

    @Override
    public void enqueueUpdate(N n) {
        // 回收节点时，将View变为不可见
        n.getView().setVisibility(View.INVISIBLE);
    }

    public Collection<N> visibleNodes;
    // 获取在给定矩形区域内的所有节点
    public void generateVisibleNodes(RectF rect) {
        List<T> list = new ArrayList<>();
        // 这里可以用四叉树把碰撞检测优化到lg(n)
        for (T nodeInfo : data.values()) {
            if (RectF.intersects(rect, nodeInfo.getArea())) {
                list.add(nodeInfo);
            }
        }
        visibleNodes = cross(list).values();
    }

    // 两个缓冲节点集合
    private Map<String, N> mCurrentNodes = new HashMap<>();
    private Map<String, N> mTargetNodes = new HashMap<>();
    // 切换两个缓冲节点集合
    private void switchNodeCollection() {
        Map<String, N> temp = mCurrentNodes;
        mCurrentNodes = mTargetNodes;
        mTargetNodes = temp;
    }

    // 取当前缓冲集合与新节点的交集，复用旧缓冲集合的节点，新的节点会在这个过程中生成
    protected Map<String, N> cross(Collection<T> collection) {
        switchNodeCollection();
        for (T t : collection) {
            N node = mTargetNodes.get(t.getID());
            if (node != null) {
                node.update(t);
                mTargetNodes.remove(t.getID());
            } else  {
                // 获取一个新的 Node 对象
                node = nodePool.dequeue(t);
            }
            mCurrentNodes.put(t.getID(), node);
        }

        for (N node : mTargetNodes.values()) {
            nodePool.enqueue(node);
        }
        mTargetNodes.clear();
        return mCurrentNodes;
    }
}
