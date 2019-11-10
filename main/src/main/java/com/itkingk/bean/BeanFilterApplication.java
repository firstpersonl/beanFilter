package com.itkingk.bean;

import com.itkingk.bean.filter.MyBeanFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @author itKingk
 */
@EnableAutoConfiguration
@SpringBootConfiguration
@ComponentScan(value = "com.itkingk.*", excludeFilters ={@ComponentScan.Filter(type = FilterType.CUSTOM, classes = MyBeanFilter.class)})
public class BeanFilterApplication {
	public static void main(String[] args) {
		SpringApplication.run(BeanFilterApplication.class, args);
	}

}
