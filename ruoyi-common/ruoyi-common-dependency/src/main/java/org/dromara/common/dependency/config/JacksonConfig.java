//package org.dromara.common.dependency.config;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.module.SimpleModule;
// import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
//
// /**
//  * 统一注解，解决前后端交互Long类型精度丢失的问题
//  *
//  * 此方法存在一定问题，比如秒数本来Long类型，如果使用此类就会改变成String类型，所以不推荐使用
//  *
//  * 作为保留配置类
//  */
// @Configuration
// public class JacksonConfig {
//     @Bean
//     public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
//         ObjectMapper objectMapper = builder.createXmlMapper(false).build();
//         SimpleModule simpleModule = new SimpleModule();
//         simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//         objectMapper.registerModule(simpleModule);
//         return objectMapper;
//     }
// }
