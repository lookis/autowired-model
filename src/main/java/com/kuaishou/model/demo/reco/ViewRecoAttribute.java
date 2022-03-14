package com.kuaishou.model.demo.reco;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kuaishou.model.autowire.AttributeProvider;
import com.kuaishou.model.autowire.Autowiring;
import com.kuaishou.model.demo.FeedView;

/**
 * @author 敬思 <liujingsi@kuaishou.com>
 * Created on 2022-03-14
 */
@Service
public class ViewRecoAttribute implements AttributeProvider<FeedView, Integer> {

    @Autowired
    ModelRecoAttribute nameAttribute;

    @Autowired
    Autowiring autowiring;

    @Override
    public String serializedKey(FeedView view) {
        return "nameAttribute";
    }

    @Override
    public Integer resolve(FeedView view) {
        Integer nameValue = autowiring.getter(view.getModel(), nameAttribute);
        return nameValue + 1;
    }

    @Override
    public Map<FeedView, Integer> resolve(Collection<FeedView> views) {
        return views.stream().collect(Collectors.toMap(Function.identity(), this::resolve));
    }

}
