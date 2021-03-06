/*
 * Copyright (C) 2017-2019 Centre National d'Etudes Spatiales (CNES).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package fr.cnes.doi;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import fr.cnes.doi.client.ClientProxyTest;
import fr.cnes.doi.security.UtilsCryptography;
import fr.cnes.doi.settings.DoiSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to read the settings from the crypted config file and to enable the
 * proxy if the system property has been sets
 *
 * @author Claire
 *
 */
public final class InitSettingsForTest {

    /**
     * Properties file for tests.
     */
    public static final String CONFIG_TEST_PROPERTIES = "config-test.properties";
    
    /**
     * Properties file for testing DOI creation on Datacite (file no commited)
     */
    public static final String CONFIG_IT_DATACITE_PROPERTIES = "config-it-datacite.properties";

    /**
     * Properties files for it.
     */
    public static final String CONFIG_IT_PROPERTIES = "config-it.properties";

    /**
     * Init loggers.
     */
    private static final Logger LOGGER = LogManager.getLogger(InitSettingsForTest.class);

    /**
     * "Static" class cannot be instantiated
     */
    private InitSettingsForTest() {
    }

    /**
     * Reads the settings.
     *
     * @param configProperties
     *            config properties
     */
    public static void init(final String configProperties) {
	try {
	    final String secretKey = System.getProperty("private.key");
	    String result;
	    try (InputStream inputStream = ClientProxyTest.class
		    .getResourceAsStream("/" + configProperties)) {
		result = new BufferedReader(new InputStreamReader(inputStream)).lines()
			.collect(Collectors.joining("\n"));
	    }
	    if (secretKey != null) {
		result = UtilsCryptography.decrypt(result, secretKey);
	    } else {
		LOGGER.warn("No private.key provided, the configuration is not crypted");
	    }
	    // Replace the value to use the proxy by the system property
	    final String useProxy = System.getProperty("proxy.use");
	    if (useProxy != null) {
		result = result.replace("Starter.Proxy.used=false",
			"Starter.Proxy.used=" + useProxy);
	    } else {
		LOGGER.warn("The key proxy.use is not set, default param applied");
	    }
	    final InputStream stream = new ByteArrayInputStream(
		    result.getBytes(StandardCharsets.UTF_8));
	    DoiSettings.getInstance().setPropertiesFile(stream);

	} catch (IOException e) {
	    LOGGER.warn("Error during initialisation of the settings", e);
	}
    }

    /**
     * Reads the settings.
     *
     */
    public static void init() {
	init(DoiSettings.CONFIG_PROPERTIES);
    }

}
