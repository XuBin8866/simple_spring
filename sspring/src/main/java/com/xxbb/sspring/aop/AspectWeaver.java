package com.xxbb.sspring.aop;

import com.xxbb.sspring.aop.annotation.Aspect;
import com.xxbb.sspring.aop.annotation.Order;
import com.xxbb.sspring.aop.aspect.AspectInfo;
import com.xxbb.sspring.aop.aspect.DefaultAspect;
import com.xxbb.sspring.core.BeanContainer;
import com.xxbb.sspring.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author xxbb
 */
public class AspectWeaver {
    private BeanContainer beanContainer;
    public AspectWeaver(){
        this.beanContainer=BeanContainer.getInstance();
    }
    public void doAOP(){
        //1.获取所有的切面类
        Set<Class<?>> aspectSet = beanContainer.getClassesByAnnotation(Aspect.class);
        //2.拼装AspectInfoList
        List<AspectInfo> aspectInfoList=packAspectInfoList(aspectSet);
        //3.遍历容器里的类
        Set<Class<?>> classSet = beanContainer.getClasses();
        for(Class<?> targetClass:classSet){
            //排除被Aspect注解的类本身
            if(targetClass.isAnnotationPresent(Aspect.class)){
                continue;
            }
            //4.粗筛符合条件的Aspect
            List<AspectInfo> roughMatchedAspectList=collectRoughMatchedAspectListForSpecificClass(aspectInfoList,targetClass);
            //5.尝试进行Aspect织入
            wrapIfNecessary(roughMatchedAspectList,targetClass);
        }

    }



    private void wrapIfNecessary(List<AspectInfo> roughMatchedAspectList, Class<?> targetClass) {
    }


    private List<AspectInfo> collectRoughMatchedAspectListForSpecificClass(List<AspectInfo> aspectInfoList, Class<?> targetClass) {
        return null;
    }
    private List<AspectInfo> packAspectInfoList(Set<Class<?>> aspectSet) {
        return null;
    }


    /**
     * 将通知织入目标对象中
     * @param category 被代理的目标对象的注解
     * @param aspectInfoList 通知集合
     */
    private void waveByCategory(Class<? extends Annotation> category, List<AspectInfo> aspectInfoList) {
        //获取被代理类的集合
        Set<Class<?>> classSet = beanContainer.getClassesByAnnotation(category);
        //遍历被代理类，分别为每个被代理类生成动态代理实例
        if(ValidationUtil.isEmpty(classSet)){
            return;
        }
        for(Class<?> targetClass:classSet){
            //创建动态代理对象
            AspectListExecutor aspectListExecutor=new AspectListExecutor(targetClass,aspectInfoList);
            Object proxyBean=ProxyCreator.createProxy(targetClass,aspectListExecutor);
            //将动态代理对象添加到容器中，取代未被代理前的类实例
            beanContainer.addBean(targetClass,proxyBean);
        }


    }

    /**
     * 将切面类按照不同的织入目标分别去按序织入Aspect逻辑
     * @param categorizedMap
     * @param aspectClass
     */
    private void categorizedAspect(Map<Class<? extends Annotation>, List<AspectInfo>> categorizedMap, Class<?> aspectClass) {
        //获取标签
        Order orderTag=aspectClass.getAnnotation(Order.class);
        Aspect aspectTag=aspectClass.getAnnotation(Aspect.class);
        //获取切面对象
        DefaultAspect aspect=(DefaultAspect)beanContainer.getBean(aspectClass);
        //获取切面信息
        AspectInfo aspectInfo=new AspectInfo(orderTag.value(),aspect);
        if(!categorizedMap.containsKey(aspectTag.value())){
            //如果织入的join point第一次出现，则该joinPoint为key，以新创建的List<AspectInfo>为value
            List<AspectInfo> aspectInfoList=new ArrayList<>();
            aspectInfoList.add(aspectInfo);
            categorizedMap.put(aspectTag.value(),aspectInfoList);
        }else{
            //如果织入的joinPoint不是第一次出现，则往joinPoint对应的Value中添加新的Aspect逻辑
            List<AspectInfo> aspectInfoList=categorizedMap.get(aspectTag.value());
            aspectInfoList.add(aspectInfo);
        }
    }

    /**
     * 验证被@Aspect注解标注的类的合法性
     * 框架一定要遵循给Aspect类添加@Aspect和@Order标签的规范，同时必须继承自DefaultAspect类，也不能是@Aspect自己
     * @param aspectClass 被注解标注类的class对象
     * @return 是否合法
     */
    private boolean verifyAspect(Class<?> aspectClass) {
        return aspectClass.isAnnotationPresent(Aspect.class)&&
                aspectClass.isAnnotationPresent(Order.class)&&
                DefaultAspect.class.isAssignableFrom(aspectClass)&&
                aspectClass.getAnnotation(Aspect.class).value()!=Aspect.class;
    }
}