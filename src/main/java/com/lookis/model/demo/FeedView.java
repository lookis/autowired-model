package com.lookis.model.demo;

/**
 * @author 敬思 <lookisliu@gmail.com>
 * Created on 2022-03-14
 */
public class FeedView {
    //传递一下 model,这样二方逻辑就可以直接从这里来获取数据了
    private FeedModel model;

    public FeedView(FeedModel model) {
        this.model = model;
    }

    public FeedModel getModel() {
        return model;
    }
}
