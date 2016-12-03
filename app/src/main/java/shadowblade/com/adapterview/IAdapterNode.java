package shadowblade.com.adapterview;

import android.view.View;

/**
 * Created by ShadowBlade on 2016/12/3.
 */

public interface IAdapterNode<T extends IAdapterNodeInfo> {

    void update(T nodeInfo);

    View getView();
}
