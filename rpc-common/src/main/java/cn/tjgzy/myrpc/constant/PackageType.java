package cn.tjgzy.myrpc.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author GongZheyi
 * @create 2021-09-17-10:16
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;

}
