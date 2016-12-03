package shadowblade.com.example;

import shadowblade.com.adapterview.AbsAdapterNodeDatabase;

/**
 * Created by ShadowBlade on 2016/12/3.
 */

public class ExampleAdapterNodeDatabase extends AbsAdapterNodeDatabase<ExampleAdapterNodeInfo, ExampleAdapterNode> {

    public ExampleAdapterNodeView nodeView;

    public ExampleAdapterNodeDatabase(ExampleAdapterNodeView view) {
        nodeView = view;
    }
    @Override
    public ExampleAdapterNode instantiate(ExampleAdapterNodeInfo exampleAdapterNodeInfo) {
        ExampleAdapterNode node = new ExampleAdapterNode(nodeView);
        node.update(exampleAdapterNodeInfo);
        return node;
    }
}
