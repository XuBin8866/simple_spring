package com.xxbbb.demo;

import com.xxbb.client.controller.MainPageController;
import com.xxbb.client.service.combine.HeadLineShopCategoryCombineService;
import com.xxbb.client.service.combine.impl.HeadLineShopCategoryCombineServiceImpl;
import com.xxbb.simpleframework.core.BeanContainer;
import com.xxbb.simpleframework.core.annotation.Controller;
import com.xxbb.simpleframework.inject.DependencyInject;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import javax.servlet.http.HttpServlet;

public class BeanContainerTest extends TestCase {
    private static BeanContainer  beanContainer=BeanContainer.getInstance();
    private static DependencyInject dependencyInject=new DependencyInject();

    @Test
    @Order(1)
    public void testLoadBean(){
        Assert.assertFalse(beanContainer.isLoaded());
        beanContainer.loadBeans("com.xxbb.client");
        Assert.assertEquals(8,beanContainer.size());
        Assert.assertTrue(beanContainer.isLoaded());
    }
    @Test
    @Order(2)
    public void testGetBean(){
        MainPageController mainPageController= (MainPageController) beanContainer.getBean(MainPageController.class);
        assertEquals(true, mainPageController instanceof MainPageController);
    }
    @Test
    @Order(3)
    public void testAnnotation(){
        assertEquals(3,beanContainer.getClassesByAnnotation(Controller.class).size());
    }
    public void testSuper(){
        assertTrue(beanContainer.getClassesBySuper(HttpServlet.class).contains(MainPageController.class));
    }
    @Test
    @Order(4)
    public void testIoc(){
        System.out.println(beanContainer.getBeans());
        dependencyInject.doIoC();
        Object obj=beanContainer.getBean(HeadLineShopCategoryCombineServiceImpl.class);
        System.out.println(obj);
    }
}