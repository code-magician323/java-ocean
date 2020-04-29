package cn.edu.ntu.boot.swagger2.configuration;

import io.swagger.annotations.Api;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * config swagger for API. Uri: http://localhost:8080/swagger-ui.html <br>
 * link: https://www.jianshu.com/p/4539e312ce87<br>
 * link2:
 * https://www.ibm.com/developerworks/cn/java/j-using-swagger-in-a-spring-boot-project/index.html
 * <br>
 *
 * @author zack <br>
 * @create 2020-04-27 11:45 <br>
 */
@Configuration
@EnableSwagger2
@ConditionalOnProperty(
    prefix = "swagger2",
    value = {"enable"},
    havingValue = "true")
public class SwaggerConfig {

  @Bean
  public Docket createRestApi() {

    List<ResponseMessage> responseMessageList = new ArrayList<>();
    responseMessageList.add(new ResponseMessageBuilder().code(404).message("Not Found").build());
    responseMessageList.add(
        new ResponseMessageBuilder()
            .code(400)
            .message("Internal Error")
            .responseModel(new ModelRef("ErrorMessage"))
            .build());
    responseMessageList.add(new ResponseMessageBuilder().code(401).message("Unauthorized").build());

    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        //.useDefaultResponseMessages(false)
        .globalResponseMessage(RequestMethod.GET, responseMessageList)
        .globalResponseMessage(RequestMethod.POST, responseMessageList)
        .globalResponseMessage(RequestMethod.PUT, responseMessageList)
        .globalResponseMessage(RequestMethod.DELETE, responseMessageList)
        // Extensibility mechanism to add a servlet path mapping,
        // if there is one, to the apis base path.
        // .pathMapping("/")
        .select()
        .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
        // .apis(RequestHandlerSelectors.basePackage("cn.edu.ntu.boot.swagger2.controller"))
        .paths(PathSelectors.any())
        .build()
        .globalOperationParameters(parameter());
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Api Document")
        .description("Api Document Description")
        .contact(
            new Contact(
                "Alice52", "https://github.com/Alice52/tutorials-sample/", "zzhang_xz@163.com"))
        .termsOfServiceUrl("https://github.com/Alice52/java-ocean/")
        .version("1.0")
        .build();
  }

  private List<Parameter> parameter() {
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(
        new ParameterBuilder()
            .name("token")
            .description("Api Token")
            .modelRef(new ModelRef("String"))
            .parameterType("header")
            .required(false)
            .build());

    return parameters;
  }
}
