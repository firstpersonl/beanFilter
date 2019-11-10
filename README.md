## spring boot bean filter 

最近再项目中遇到这这样需求：内部系统对外接入了很多平台，所以需要有一个协议转换的中间模块，所以我们项目的架构是一个maven多模块项目：

```java
|--  build.gradle
|-- main
|	|-- src
|		|-- java
|			|-- ApplicationRun.java
|--common
|	|-- src
|		|-- java
|		    |-- ...
|-- modelA
|	|-- src
|		|-- java
|	         |-- ...
|-- modelB
|	|-- src
|		|-- java
|	         |-- ...
```

我们都知道，如果是需要对接A系统我们单独打包main、common、modelA就行，对接B系统相反也一样。思考，如果是再项目开发完成后没有一个人来专门负责打包，那怎么办。

这时我们自然就想到直接将所有的模块都打包在一起，让和我们控制main模块启动时，运行那个模块的代码就行。那么现在的问题就是如何控制那个模块运行的问题。

我们是spring boot 框架下开发的项目，所以我们还是要从框架入手。我们控制运行那个模块其实就是让spring容器再启动时，让他扫描那个包路径下的文件。所以这就进入到我们今天的主题spring boot 的包扫描过滤器 TypeFilter。

```java
@FunctionalInterface
public interface TypeFilter {

	/**
	 * Determine whether this filter matches for the class described by
	 * the given metadata.
	 * @param metadataReader the metadata reader for the target class
	 * @param metadataReaderFactory a factory for obtaining metadata readers
	 * for other classes (such as superclasses and interfaces)
	 * @return whether this filter matches
	 * @throws IOException in case of I/O failure when reading metadata
	 */
	boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException;

}
```

```java
public enum FilterType {
    ANNOTATION, //注解类型
    ASSIGNABLE_TYPE, //指定的类型
    ASPECTJ, //按照Aspectj的表达式，基本上不会用到
    REGEX, //按照正则表达式
    CUSTOM; //自定义
    private FilterType() {
    }
}
```

TypeFilter有着五种过滤的方式，当然我们这里使用的是CUSTOM自定义实现类TypeFilter

```java
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
        //itkingk.loadModel 从配置文件配置需要加载的模块，多个可以用逗号分隔开
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
            //我们根据配置文件的枚举名字，筛选出要加载的包路径，从而达到过滤目的
            if (className.startsWith(value.getPackageName())) {
                log.info("load model {}", className);
                System.out.println("load class " + className);
                return false;
            }
        }
        return true;
    }
}
```

这里再@ComponentScan 注解中 excludeFilters是排除过滤，includeFilters是加载过滤，所以再自定义的filter中当我们使用excludeFilters时，返回true就是排除，返回false就是加载，includeFilter 则恰恰相反。当然我们这里根据需求应该是使用excludeFilter实现起来比较容易。

这里还需注意一个重要的属性useDefaultFilters，默认值是true,他的意思是是否需要使用默认的过滤器，那让我们来看看默认的过滤器是什么规则：

```java
/**
 * Indicates a {@link Configuration configuration} class that declares one or more
 * {@link Bean @Bean} methods and also triggers {@link EnableAutoConfiguration
 * auto-configuration}, {@link ComponentScan component scanning}, and
 * {@link ConfigurationPropertiesScan configuration properties scanning}. This is a
 * convenience annotation that is equivalent to declaring {@code @Configuration},
 * {@code @EnableAutoConfiguration}, {@code @ComponentScan}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @since 1.2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {...}
```

这是spring 自动装配的原理 ，我们这里暂不做解析。useDefaultFilters 再使用includeFilters时，因该设置为false,否则你的其他使用的@Service、@Component、@Controller等其他你不想扫描到的类也会被扫描进去。

这就是整个实现按配置来运行我们项目的重要思路，代码我放在了[github](https://github.com/firstpersonl/beanFilter)上面，需要的小伙伴可以去看一看。


第一次写文章，思路有些乱，请大家谅解。