package cn.tjgzy.myrpc.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author GongZheyi
 * @create 2021-09-17-10:43
 */
@AllArgsConstructor
@Getter
public enum SerializerCode {

    KRYO(0),
    JSON(1),
    PROTOSTUFF(2);

    private final int code;

}
