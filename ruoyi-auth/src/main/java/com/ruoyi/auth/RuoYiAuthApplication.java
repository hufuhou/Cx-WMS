package com.ruoyi.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import com.ruoyi.common.security.annotation.EnableRyFeignClients;

/**
 * 认证授权中心
 * 
 * @author ruoyi
 */
@EnableRyFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class RuoYiAuthApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(RuoYiAuthApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  认证授权中心启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "_________ ____  ___\n" +
                "\\_   ___ \\\\   \\/  /\n" +
                "/    \\  \\/ \\     / \n" +
                "\\     \\____/     \\ \n" +
                " \\______  /___/\\  \\\n" +
                "        \\/      \\_/\n" +
                "Have fun. :)\n");
    }
}
