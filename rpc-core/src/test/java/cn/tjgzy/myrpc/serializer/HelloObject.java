package cn.tjgzy.myrpc.serializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author GongZheyi
 * @create 2021-09-16-11:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject implements Serializable {

    private Integer id;
    private String message;

}
