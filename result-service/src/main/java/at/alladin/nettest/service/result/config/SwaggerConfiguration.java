/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.service.result.config;

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
    public Docket resultApiV1() {
        return new Docket(DocumentationType.SWAGGER_2)
        		.groupName("result_v1")
        		.apiInfo(resultApiInfoV1())
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
	private ApiInfo resultApiInfoV1() {
		return new ApiInfoBuilder()
			.title("result-service REST API")
			.description("result-service REST API documentation of nntool.eu project.")
			.license("Apache License 2.0")
			.licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.html")
			.termsOfServiceUrl("https://nntool.eu")
			.contact(new Contact("alladin-IT GmbH", "https://alladin.at", null))
			.version("v1")
			.build();
	}
}
