package shadowblade.com.adapterview;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by ShadowBlade on 2016/12/3.
 * 实现节点信息的类保存节点的元数据并被用来更新节点
 */

public interface IAdapterNodeInfo {

    // 标识节点的ID，务必保证唯一性，否则可能会产生错误
    String getID();

    // 返回坐标值将决定节点在视图中的位置
    PointF getCoordinate();

    // 节点实际所占区域，决定节点是否可见，一般来说区域与坐标值相关
    RectF getArea();
}
