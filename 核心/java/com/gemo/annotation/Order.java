package com.gemo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gemo.enumeration.EMSOrder;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Order {

	// 排序属性
	String field();

	// 排序
	EMSOrder order();
}
