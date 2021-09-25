package cn.tjgzy.myrpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author GongZheyi
 * @create 2021-09-19-8:35
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    public String name() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";

}
