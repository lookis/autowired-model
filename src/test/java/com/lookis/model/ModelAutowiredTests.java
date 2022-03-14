package com.lookis.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.lookis.model.autowire.Autowiring;
import com.lookis.model.demo.FeedModel;
import com.lookis.model.demo.FeedView;

/**
 * @author 敬思 <lookisliu@gmail.com>
 * Created on 2022-03-14
 */
@SpringBootTest
@Import(TestConfig.class)
public class ModelAutowiredTests {
    @Autowired
    Autowiring autowiring;

    @Test
    public void testSingle() {
        FeedModel model = new FeedModel("rootModel", "attr1", "attribute2");
        //自动织入，feed 业务不关心具体下游有哪些业务逻辑需要基于这个 model 做数据传递
        autowiring.setter(model);
        System.out.println("==============");
        //转 View（从框架角度来说，本质上是不关心 model 还是 view 的，可以无限转，这里只是用 View 这个命名的类来代表）
        FeedView view = new FeedView(model);
        //换了一个对象就再一次织入一次
        autowiring.setter(view);
        //转 json，会自动把下游的数据也都跑出来
        Object resultMap = autowiring.toSerializable(view);
        System.out.println(resultMap);
    }

}
