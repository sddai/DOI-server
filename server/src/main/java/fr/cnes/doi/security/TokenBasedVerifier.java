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
package fr.cnes.doi.security;

import fr.cnes.doi.db.AbstractTokenDBHelper;
import fr.cnes.doi.db.AbstractUserRoleDBHelper;
import fr.cnes.doi.logging.business.JsonMessage;
import fr.cnes.doi.plugin.PluginFactory;
import fr.cnes.doi.utils.spec.Requirement;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.security.Verifier;

/**
 * Security class for checking token.
 *
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
@Requirement(reqId = Requirement.DOI_AUTH_020, reqName = Requirement.DOI_AUTH_020_NAME)
public class TokenBasedVerifier implements Verifier {

    /**
     * Logger.
     */
    private static final Logger LOG = LogManager.getLogger(TokenBasedVerifier.class.getName());

    /**
     * Token DB.
     */
    private final AbstractTokenDBHelper tokenDB;

    /**
     * Constructor.
     *
     * @param tokenDB token DB
     */
    public TokenBasedVerifier(final AbstractTokenDBHelper tokenDB) {
        LOG.traceEntry(new JsonMessage(tokenDB));
        this.tokenDB = tokenDB;
    }

    /**
     * Verifies the token.
     *
     * @param request request
     * @param response response
     * @return the result
     */
    @Override
    public int verify(final Request request, final Response response) {
        LOG.traceEntry(new JsonMessage(request));
        final int result;
        final ChallengeResponse challResponse = request.getChallengeResponse();

        if (challResponse == null) {
            result = Verifier.RESULT_MISSING;
        } else if (challResponse.getScheme().equals(ChallengeScheme.HTTP_BASIC)) {
            result = Verifier.RESULT_MISSING;
        } else {
            result = processAuthentication(request, challResponse);
        }
        return LOG.traceExit(result);
    }

    /**
     * Process Authentication.
     *
     * @param request request
     * @param challResponse authentication object
     * @return the authentication status
     */
    private int processAuthentication(final Request request,
            final ChallengeResponse challResponse) {
        LOG.traceEntry(new JsonMessage(request));
        final int result;
        final String token = challResponse.getRawValue();
        LOG.debug("Token from challenge response : {}", token);
        if (token == null) {
            LOG.warn("Token is missing !");
            result = Verifier.RESULT_MISSING;
        } else if (TokenSecurity.getInstance().isExpired(token)) {
            LOG.debug("Token is expired !");
            result = Verifier.RESULT_STALE;
        } else if (this.tokenDB.isExist(token)) {
            LOG.debug("Token exists in the database.");
            result = processToken(request, token);
        } else {
            LOG.error("Token is invalid !");
            result = Verifier.RESULT_INVALID;
        }
        return LOG.traceExit(result);
    }

    /**
     * Process token.
     *
     * @param request request
     * @param token token
     * @return the status given by {@link Verifier}
     */
    private int processToken(final Request request, final String token) {
        LOG.traceEntry(new JsonMessage(request));
        LOG.traceEntry(token);
        final AbstractUserRoleDBHelper manageUsers = PluginFactory.getUserManagement();
        final int result = Verifier.RESULT_VALID;
        final Jws<Claims> tokenInfo = TokenSecurity.getInstance().getTokenInformation(token);
        final Claims body = tokenInfo.getBody();
        final String userID = body.getSubject();
        final Integer projectID = (Integer) body.get(TokenSecurity.PROJECT_ID);
        LOG.info("token {} is valid, {} for {} are authenticated", token, userID, projectID);
        request.getClientInfo().setUser(manageUsers.getRealm().findUser(userID));

        return LOG.traceExit(result);
    }
}
