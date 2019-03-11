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
package fr.cnes.doi.ldap.impl;

import fr.cnes.doi.db.AbstractUserRoleDBHelper;
import java.util.List;

import fr.cnes.doi.ldap.exceptions.LDAPAccessException;
import fr.cnes.doi.ldap.persistence.LdapDoidbIntegration;
import fr.cnes.doi.ldap.util.LDAPUser;
import fr.cnes.doi.exception.DOIDbException;
import fr.cnes.doi.db.model.DOIUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fr.cnes.doi.ldap.service.ILDAPAccessService;
import fr.cnes.doi.plugin.PluginFactory;

/**
 * Concrete implementation of {@link LdapDoidbIntegration}.
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
public class LdapDoidbIntegrationImpl implements LdapDoidbIntegration {

    /**
     * Logger.
     */
    private static final Logger LOG = LogManager.getLogger(LdapDoidbIntegrationImpl.class.getName());

    /**
     * Access to the LDAP.
     */
    private final ILDAPAccessService ldapaccessservice = new LDAPAccessServiceImpl();

    @Override
    public void updateDoiServerDataBaseFromLdap() throws LDAPAccessException, DOIDbException {
        LOG.traceEntry();
        final AbstractUserRoleDBHelper manageUsers = PluginFactory.getUserManagement();
        final List<LDAPUser> ldapmembers = ldapaccessservice.getDOIProjectMembers();
        LOG.debug("LDAP members: {}", ldapmembers);
        final List<DOIUser> dbusers = manageUsers.getUsers();
        LOG.debug("Users from database: {}", dbusers);
        LOG.debug("remove from database, users that are no longer members of doi_server project");
        for (final DOIUser dbuser : dbusers) {
            if (!isContained(dbuser, ldapmembers)) {               
                LOG.debug("User {} is removed from database", dbuser.getUsername());
                manageUsers.removeDOIUser(dbuser.getUsername());
            }
        }
        LOG.debug("add to database users that are new members of doi_server project");
        for (final LDAPUser ldapmember : ldapmembers) {
            if (!isContained(ldapmember, dbusers)) {
                // add ldapmember to doi database
                LOG.debug("ldapmember {} is added to database as simple user", ldapmember.getUsername());
                try {
                    manageUsers.addDOIUser(ldapmember.getUsername(), false, ldapmember.getEmail());
                } catch (DOIDbException ex) {
                    LOG.error("Cannot add {}", ldapmember.getUsername(), ex);
                }                
            }
        }
        LOG.traceExit();
    }

    /**
     * Tests if LDAP user is contained in the users from the database.
     * @param ldapmember LDAP member
     * @param dbusers users database
     * @return True when the LDAP user is contained in the users from database otherwise false
     */
    private boolean isContained(final LDAPUser ldapmember, final List<DOIUser> dbusers) {
        LOG.traceEntry("Parameters {},", ldapmember, dbusers);
        boolean isContained = false;
        for (final DOIUser dbuser : dbusers) {
            if (dbuser.getUsername().equals(ldapmember.getUsername())) {
                isContained = true;
                break;
            }
        }
        return LOG.traceExit(isContained);
    }

    /**
     * Tests if a user from the database is contained in the users from the LDAP.
     * @param dbuser the user from the database
     * @param ldapmembers the users from the LDAP
     * @return True when the user from the database is contained from the users from the LDAP 
     */
    private boolean isContained(DOIUser dbuser, List<LDAPUser> ldapmembers) {
        LOG.traceEntry("Parameters {},", dbuser, ldapmembers);
        boolean isContained = false;
        for (final LDAPUser ldapuser : ldapmembers) {
            if (ldapuser.getUsername().equals(dbuser.getUsername())) {
                isContained = true;
                break;
            }
        }
        return LOG.traceExit(isContained);
    }

}
