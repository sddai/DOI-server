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
package fr.cnes.doi.client;

import fr.cnes.httpclient.HttpClient;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.restlet.Client;
import org.restlet.data.Parameter;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

/**
 * Checks the status of the landing page.
 *
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
public class ClientLandingPage extends BaseClient {

    /**
     * DOI name resolver {@value #BASE_URI}.
     */
    private static final String BASE_URI = "http://doi.org";

    /**
     * List of offline landing pages.
     */
    private final Map<String, Status> errors = new ConcurrentHashMap<>();

    /**
     * Constructor
     *
     * @param dois List of DOIs to check
     */
    public ClientLandingPage(final List<String> dois) {
        super(BASE_URI);
        checkDoi(dois);
    }

    /**
     * Tests for each DOI if the landing page is online.
     *
     * @param dois dois to check
     */
    //TODO : check with Head before. If not implemented, check with get
    private void checkDoi(final List<String> dois) {
        this.getLog().traceEntry("Parameters\n\tdois : {}", dois);
        this.getClient().setMaxRedirects(5);
        this.getClient().setLoggable(true);
        final Client configurationClient = (Client) this.getClient().getNext();
        final Series<Parameter> parameters = configurationClient.getContext().getParameters();
        parameters.add(HttpClient.MAX_REDIRECTION, String.
                valueOf(this.getClient().getMaxRedirects()));
        this.getLog().info("{} landing pages to check.", dois.size());
        for (final String doi : dois) {
            this.getClient().setReference(BASE_URI);
            this.getClient().addSegment(doi);
            this.getLog().info("Checking landing page {}", doi);
            try {
                final Representation rep = this.getClient().get();
                rep.exhaust();
                final Status status = this.getClient().getStatus();
                if (status.isError()) {
                    this.errors.put(doi, status);
                    this.getLog().error("Error for landing page {}", doi);
                } else {
                    this.getLog().info("OK");
                }
            } catch (ResourceException ex) {
                this.getLog().error("Checking landing pages", ex);
                this.errors.put(doi, ex.getStatus());
            } catch (IOException ex) {
                this.getLog().error("Checking landing pages", ex);
                this.errors.put(doi, new Status(Status.SERVER_ERROR_INTERNAL, ex));
            } finally {
                this.getClient().release();
            }
        }
        this.getLog().traceExit();
    }

    /**
     * Returns true when there is no error otherwise False.
     *
     * @return true when there is no error otherwise False
     */
    public boolean isSuccess() {
        this.getLog().traceEntry();
        return this.getLog().traceExit(this.errors.isEmpty());
    }

    /**
     * Returns True when there is more than zero error otherwise False
     *
     * @return True when there is more than zero error otherwise False
     */
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * Returns the errors.
     *
     * @return the error
     */
    public Map<String, Status> getErrors() {
        this.getLog().traceEntry();
        return this.getLog().traceExit(Collections.unmodifiableMap(this.errors));
    }

}
