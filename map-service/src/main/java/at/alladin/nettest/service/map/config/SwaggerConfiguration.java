package at.alladin.nettest.service.map.config;

import static springfox.documentation.builders.PathSelectors.regex;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	
	/**
	 * 
	 * @return
	 */
	@Bean
    public Docket statisticServiceApiV1() { 
        return new Docket(DocumentationType.SWAGGER_2)
        		.groupName("map-service_v1")
        		.apiInfo(collectorApiInfoV1())
        		.select()
        			.apis(RequestHandlerSelectors.any())
        			.paths(regex("/api/v1/.*"))
        			.build()
        		//.pathMapping("/")
        		//.forCodeGeneration(true)
        		.useDefaultResponseMessages(false)
        		//.ignoredParameterTypes(ApiResponse.class)
    			.directModelSubstitute(LocalDateTime.class, String.class) // ?
    			.genericModelSubstitutes(ResponseEntity.class);
    }
	
	/**
	 * 
	 * @return
	 */
	private ApiInfo collectorApiInfoV1() {
		return new ApiInfoBuilder()
			.title("Map Service REST API")
			.description(
				"Map Service REST API documentation of nntool.eu project."
			)
			.license("Apache License 2.0")
			.licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.html")
			.termsOfServiceUrl("http://nntool.eu") // TODO: correct sub URL and HTTPS
			.contact(new Contact("alladin-IT GmbH", "https://alladin.at", null))
			.version("v1")
			.build();
	}
}
