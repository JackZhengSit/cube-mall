package com.kkb.cubemall.canal;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCanalClient
public class CubemallCanalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CubemallCanalApplication.class, args);
    }

}
