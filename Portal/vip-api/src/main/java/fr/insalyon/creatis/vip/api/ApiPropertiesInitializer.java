/*
 * Copyright and authors: see LICENSE.txt in base repository.
 *
 * This software is a web portal for pipeline execution on distributed systems.
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package fr.insalyon.creatis.vip.api;

import fr.insalyon.creatis.vip.api.bean.Module;
import fr.insalyon.creatis.vip.api.rest.model.SupportedTransferProtocol;
import fr.insalyon.creatis.vip.core.server.business.Server;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.Assert;
import org.springframework.web.context.*;

import java.io.IOException;

import static fr.insalyon.creatis.vip.api.CarminProperties.*;

/**
 Vip additional property source location is configured by a property in the
 main property file "$HOME/.vip/vip.conf".
 It is advised to put it in a "vip-api.conf" file in the same folder (default behavior)
 * Created by abonnet on 5/7/18.
 */
public class ApiPropertiesInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

    private static final Logger logger = Logger.getLogger(ApiPropertiesInitializer.class);

    @Override
    public void initialize(ConfigurableWebApplicationContext applicationContext) {
        try {
            applicationContext.getEnvironment().getPropertySources().addLast(
                    new ResourcePropertySource(Server.getInstance().getApiConfFileLocation())
            );
            verifyProperties(applicationContext.getEnvironment());
        } catch (IOException e) {
            logger.error("Cant't init api conf file " + Server.getInstance().getApiConfFileLocation());
            throw new RuntimeException("Error initializing api conf");
        }
    }

    private void verifyProperties(Environment env) {
        Assert.notNull(env.getProperty(CORS_AUTHORIZED_DOMAINS, String[].class));

        Assert.notNull(env.getProperty(PLATFORM_NAME));
        Assert.notNull(env.getProperty(PLATFORM_DESCRIPTION));
        Assert.notNull(env.getProperty(PLATFORM_EMAIL));
        Assert.notEmpty(env.getProperty(SUPPORTED_TRANSFER_PROTOCOLS, SupportedTransferProtocol[].class));
        Assert.notEmpty(env.getProperty(SUPPORTED_MODULES, Module[].class));
        Assert.notNull(env.getProperty(DEFAULT_LIMIT_LIST_EXECUTION, Long.class));
        Assert.isInstanceOf(String[].class, env.getProperty(UNSUPPORTED_METHODS, String[].class));
        Assert.notNull(env.getProperty(SUPPORTED_API_VERSION));
        Assert.notEmpty(env.getProperty(PLATFORM_ERROR_CODES_AND_MESSAGES, String[].class));

        Assert.notNull(env.getProperty(APIKEY_HEADER_NAME));
        Assert.notNull(env.getProperty(APIKEY_GENERATE_NEW_EACH_TIME, Boolean.class));

        Assert.notNull(env.getProperty(API_DIRECTORY_MIME_TYPE));
        Assert.notNull(env.getProperty(API_DEFAULT_MIME_TYPE));
        Assert.notNull(env.getProperty(API_DOWNLOAD_RETRY_IN_SECONDS, Integer.class));
        Assert.notNull(env.getProperty(API_DOWNLOAD_TIMEOUT_IN_SECONDS, Integer.class));
        Assert.notNull(env.getProperty(API_DATA_TRANSFERT_MAX_SIZE, Long.class));
        Assert.notNull(env.getProperty(API_DATA_DOWNLOAD_RELATIVE_PATH));
    }
}
