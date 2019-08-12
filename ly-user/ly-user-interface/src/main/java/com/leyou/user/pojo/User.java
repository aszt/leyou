package com.leyou.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_user")
@Data
public class User {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    // 用户名
    private String username;

    // 密码
    @JsonIgnore
    private String password;

    // 电话
    private String phone;

    // 创建时间
    private Date created;

    // 密码的盐值
    @JsonIgnore
    private String salt;
}
