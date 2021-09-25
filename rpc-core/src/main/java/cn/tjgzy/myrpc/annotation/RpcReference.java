package cn.tjgzy.myrpc.annotation;

/**
 * @author GongZheyi
 * @create 2021-09-23-11:22
 */
public @interface RpcReference {

    /**
     * Service group, default value is empty string
     */
    String group() default "";
}
