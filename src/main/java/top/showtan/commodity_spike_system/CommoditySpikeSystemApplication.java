package top.showtan.commodity_spike_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan(value = {"top.showtan.commodity_spike_system.dao"})
@ServletComponentScan(value = {"top.showtan.commodity_spike_system.datasource"})
public class CommoditySpikeSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommoditySpikeSystemApplication.class, args);
    }

}
