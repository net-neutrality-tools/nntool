/*******************************************************************************
 * Copyright 2018-2019 alladin-IT GmbH
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

package at.alladin.nettest.shared.server.helper.spring;

import java.net.InetAddress;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import at.alladin.nettest.shared.server.config.NettestConstants;
import at.alladin.nettest.shared.server.config.spring.SpringConstants;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public final class SpringApplicationHelper {

	static final Logger logger = LoggerFactory.getLogger(SpringApplicationHelper.class);
	
	/**
	 * 
	 */
	private SpringApplicationHelper() {
		
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public static void runSpingApplication(String configDirectoryName, String[] args, Class<?>... sources) throws Exception {
		final String[] properties = getProperties(configDirectoryName);
		
		final SpringApplication app = new SpringApplicationBuilder(sources)
			.properties(properties)
			.application();
		
		addCommandLineDefaultProfileIfNotSet(app, args);
		
		final Environment environment = app.run(args).getEnvironment();
		
		logger.info("\n----------------------------------------------------------------\n\t" +
                "Application '{}' is running!\n\t\n\t" +
                "Local: \t\thttp://localhost:{}\n\t" +
                "External: \thttp://{}:{}\n\t" +
                "Profile(s): \t{}\n\t" + 
                "Properties: \t{}"
                + "\n----------------------------------------------------------------",
            environment.getProperty("spring.application.name"),
            environment.getProperty("server.port"),
            InetAddress.getLocalHost().getHostAddress(),
            environment.getProperty("server.port"),
            environment.getActiveProfiles(),
            Arrays.asList(properties)
        );
	}
	
	/**
	 * 
	 * @param configDirectoryName
	 * @param args
	 * @param application
	 * @param sources
	 * @return
	 */
	public static SpringApplicationBuilder configure(String configDirectoryName, SpringApplicationBuilder application, Class<?>... sources) {
		return application
			.profiles(getWebApplicationProfile())
        	.properties(getProperties(configDirectoryName))
            .sources(sources);
	}
	
	/**
	 * 
	 * @param configDirectoryName
	 * @return
	 */
	private static String[] getProperties(String configDirectoryName) {
		return new String[] {
			//"spring.config.location=file:" + NettestConstants.EXTERNAL_CONFIG_LOCATION + "/" + configDirectoryName // overrides internal config location
			"spring.config.additional-location=file:" + NettestConstants.EXTERNAL_CONFIG_LOCATION + "/" + configDirectoryName + "/" // trailing slash is important!
		};
	}
	
	/**
	 * 
	 * @param app
	 * @param args
	 */
	private static void addCommandLineDefaultProfileIfNotSet(SpringApplication app, String[] args) {
		final PropertySource<?> propertySource = new SimpleCommandLinePropertySource(args);
		
		if (!propertySource.containsProperty(SpringConstants.SPRING_PROFILES_ACTIVE_KEY)
				&& !System.getenv().containsKey(SpringConstants.SPRING_PROFILES_ACTIVE_STRING)) {
			
            app.setAdditionalProfiles(SpringConstants.SPRING_PROFILE_DEVELOPMENT);
        }
	}
	
	/**
	 * 
	 * @return
	 */
	private static String getWebApplicationProfile() {
		final String profile = System.getProperty(SpringConstants.SPRING_PROFILES_ACTIVE_KEY);
        if (profile != null) {
            logger.info("Running with Spring profile(s) : {}", profile);
            return profile;
        }

        logger.warn("No Spring profile configured, running with default configuration");
        return SpringConstants.SPRING_PROFILE_DEVELOPMENT;
	}
}
