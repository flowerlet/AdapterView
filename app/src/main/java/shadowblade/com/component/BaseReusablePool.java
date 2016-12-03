package shadowblade.com.component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShadowBlade on 2016/12/3.
 * 关联对象复用池，
 * 对象Obj的更新将与Attach相关
 */

public class BaseReusablePool<Obj, Attach> {

    private List<Obj> mObjects = new ArrayList<>();

    public interface IUpdater<Obj, Attach> {
        Obj instantiate(Attach attach);
        void dequeueUpdate(Obj obj, Attach attach);
        void enqueueUpdate(Obj obj);
    }

    private IUpdater<Obj, Attach> mUpdater;
    public BaseReusablePool(IUpdater<Obj, Attach> updater) {
        mUpdater = updater;
    }

    public Obj dequeue(Attach attach) {
        Obj object;
        if (mObjects.size() > 0) {
            object = mObjects.remove(0);
        } else {
            object = mUpdater.instantiate(attach);
        }
        mUpdater.dequeueUpdate(object, attach);
        return object;
    }

    public void enqueue(Obj object) {
        mObjects.add(object);
        mUpdater.enqueueUpdate(object);
    }
}
