package com.lookis.model.demo;

/**
 * @author 敬思 <lookisliu@gmail.com>
 * Created on 2022-03-14
 */
public class FeedModel {
    //只放业务内的属性，不放下游其它业务方的对象，解除第三方依赖
    private String name;
    private String attr1;
    private String attr2;

    public FeedModel(String name, String attr1, String attr2) {
        this.name = name;
        this.attr1 = attr1;
        this.attr2 = attr2;
    }

    public String getName() {
        return name;
    }

    public String getAttr1() {
        return attr1;
    }

    public String getAttr2() {
        return attr2;
    }
}
