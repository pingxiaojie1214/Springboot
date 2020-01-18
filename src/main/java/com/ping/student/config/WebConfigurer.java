package com.ping.student.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description: 自定义资源映射
 * <p>
 *     设置虚拟路径，访问绝对路径下资源
 * </p>
 */
@ComponentScan
@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Value("${attachment-path}")
    private  String attachment_path;//上传图片的路径

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/imgs/**")
                .addResourceLocations("file:///" + attachment_path);
    }
}
