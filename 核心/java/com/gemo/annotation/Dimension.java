package com.gemo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gemo.enumeration.EMSDimension;

/**
 * 数据维度
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Dimension {

	// 数据维度枚举
	EMSDimension dimension() default EMSDimension.No;
}
