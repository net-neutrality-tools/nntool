package at.alladin.nettest.service.collector.config;

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

	/*@Bean
	public UiConfiguration swaggerUiConfiguration() {
		return UiConfigurationBuilder.builder()
				.deepLinking(true)
		        .displayOperationId(false)
		        .defaultModelsExpandDepth(1)
		        .defaultModelExpandDepth(1)
		        .defaultModelRendering(ModelRendering.EXAMPLE)
		        .displayRequestDuration(false)
		        .docExpansion(DocExpansion.NONE)
		        .filter(false)
		        .maxDisplayedTags(null)
		        .operationsSorter(OperationsSorter.ALPHA)
		        .showExtensions(false)
		        .tagsSorter(TagsSorter.ALPHA)
		        .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
		        .validatorUrl(null)
		        .build();
	}*/

	/**
	 *
	 * @return
	 */
	@Bean
    public Docket collectorApiV1() {
        return new Docket(DocumentationType.SWAGGER_2)
        		.groupName("collector/controller_v1")
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
			.title("Collector/Controller REST API")
			.description(
				"collector/controller REST API documentation of nntool.eu project."
			)
			.license("Apache License 2.0")
			.licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.html")
			.termsOfServiceUrl("http://nntool.eu")
			.contact(new Contact("alladin-IT GmbH", "https://alladin.at", null))
			.version("v1")
			.build();
	}
}
