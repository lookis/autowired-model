package com.kuaishou.model.demo.reco;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kuaishou.model.autowire.AttributeProvider;
import com.kuaishou.model.demo.FeedModel;

/**
 * @author 敬思 <liujingsi@kuaishou.com>
 * Created on 2022-03-14
 */
@Service
public class ModelRecoAttribute implements AttributeProvider<FeedModel, Integer> {
    @Override
    public Map<FeedModel, Integer> resolve(Collection<FeedModel> models) {
        return models.stream().collect(Collectors.toMap(Function.identity(), model -> model.getName().length()));
    }
}
