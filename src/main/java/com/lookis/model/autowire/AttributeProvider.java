package com.lookis.model.autowire;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author 敬思 <lookisliu@gmail.com>
 * Created on 2022-03-14
 */
public interface AttributeProvider<Model, Attribute> {

    default String serializedKey(Model model) {
        throw new UnsupportedOperationException("not implements");
    }

    default Attribute resolve(Model model) {
        return resolve(Collections.singleton(model)).get(model);
    }

    Map<Model, Attribute> resolve(Collection<Model> models);
}
