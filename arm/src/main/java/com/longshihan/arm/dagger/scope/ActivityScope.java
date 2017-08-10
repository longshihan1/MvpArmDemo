package com.longshihan.arm.dagger.scope;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Administrator
 * @time 2017/8/10 16:07
 * @des 类作用：
 */
@Scope
@Retention(RUNTIME)
public @interface ActivityScope {
}
