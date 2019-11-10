package com.itkingk.bean.filter;

import com.itkingk.bean.constant.ModelEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义bean 加载过滤器
 * @author itkingk
 */
@Slf4j
public class MyBeanFilter implements TypeFilter {

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
        String className = metadataReader.getAnnotationMetadata().getClassName();
        String property = ((AnnotationConfigServletWebServerApplicationContext) ((CachingMetadataReaderFactory) metadataReaderFactory).getResourceLoader()).getEnvironment().getProperty("itkingk.loadModel");
        String[] modelList = property.split(",");
        List<ModelEnum> loadModels = new ArrayList<>();
        for (String s : modelList) {
            ModelEnum modelEnum = ModelEnum.valueOfByName(s);
            if (modelEnum != null) {
                loadModels.add(modelEnum);
            }
        }

        for (ModelEnum value : loadModels) {
            if (className.startsWith(value.getPackageName())) {
                log.info("load model {}", className);
                System.out.println("load class " + className);
                return false;
            }
        }
        return true;
    }
}
