package com.lookis.model.autowire;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author 敬思 <lookisliu@gmail.com>
 * Created on 2022-03-14
 */
@Service
@Lazy
public class Autowiring implements ApplicationContextAware {

    private ApplicationContext ctx;

    private Cache<Object, Map<AttributeProvider, Object>> cached = CacheBuilder.newBuilder().weakKeys().build();

    public <Model> List<AttributeProvider> keys(Class<Model> clz) {
        String[] names =
                ctx.getBeanNamesForType(
                        ResolvableType.forClassWithGenerics(AttributeProvider.class,
                                ResolvableType.forClass(clz), null));
        List<AttributeProvider> providers =
                Arrays.stream(names).map(beanName -> ctx.getBean(beanName, AttributeProvider.class))
                        .collect(Collectors.toList());
        return providers;
    }

    public <Model> void setter(Collection<Model> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
        Class clz = models.iterator().next().getClass();
        String[] names =
                ctx.getBeanNamesForType(
                        ResolvableType.forClassWithGenerics(AttributeProvider.class,
                                ResolvableType.forClass(clz), null));
        Arrays.stream(names).forEach(beanName -> {
            AttributeProvider modelInjector = ctx.getBean(beanName, AttributeProvider.class);
            Map<Model, Object> values = modelInjector.resolve(models);
            //递归
            setter(values.values());
            models.stream().forEach(model -> {
                try {
                    Map<AttributeProvider, Object> valueMap =
                            cached.get(model, () -> new ConcurrentHashMap<>());
                    valueMap.put(modelInjector, values.get(model));
                } catch (ExecutionException e) {
                    //某个 model 失败
                    e.printStackTrace();
                }
            });
        });
    }

    public <Model> void setter(Model model) {
        String[] names =
                ctx.getBeanNamesForType(ResolvableType.forClassWithGenerics(AttributeProvider.class,
                        ResolvableType.forClass(model.getClass()), null));
        Arrays.stream(names).forEach(beanName -> {
            AttributeProvider modelInjector = ctx.getBean(beanName, AttributeProvider.class);
            try {
                Object value = modelInjector.resolve(model);
                Map<AttributeProvider, Object> valueMap =
                        cached.get(model, () -> new ConcurrentHashMap<>());
                //递归
                setter(value);
                valueMap.put(modelInjector, value);
            } catch (ExecutionException e) {
                //某个 key 失败
                e.printStackTrace();
            }
        });
    }

    public <Model, Value> Value getter(Model model, AttributeProvider<Model, Value> injector) {
        Map<AttributeProvider, Object> modelValueMap = cached.getIfPresent(model);
        if (modelValueMap != null) {
            return (Value) modelValueMap.get(injector);
        }
        return null;
    }

    public <Model> Object toSerializable(Model model) {
        if (model instanceof Serializable) {
            return model;
        }
        String[] names =
                ctx.getBeanNamesForType(
                        ResolvableType.forClassWithGenerics(AttributeProvider.class,
                                ResolvableType.forClass(model.getClass()), null));
        Map<String, Object> basedBean = new HashMap<>();
        try {
            basedBean.putAll(BeanUtils.describe(model));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
        if (names.length == 0) {
            return basedBean;
        } else {
            Arrays.stream(names).forEach(beanName -> {
                AttributeProvider attributeProvider = ctx.getBean(beanName, AttributeProvider.class);
                Object value = getter(model, attributeProvider);
                basedBean.put(attributeProvider.serializedKey(model), toSerializable(value));
            });
            return basedBean;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
