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
package fr.cnes.doi.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ClientInfo;
import org.restlet.engine.security.RoleMapping;
import org.restlet.security.Enroler;
import org.restlet.security.Group;
import org.restlet.security.Realm;
import org.restlet.security.Role;
import org.restlet.security.SecretVerifier;
import org.restlet.security.User;

/**
 * Had to change method "unmap" l.520, which threw
 * ArrayIndexOutOfBoundsException
 *
 * @author disto
 */
public final class MyMemoryRealm extends Realm {

    /**
     * The modifiable list of role mappings.
     */
    private final List<RoleMapping> roleMappings = new CopyOnWriteArrayList<>();

    /**
     * The modifiable list of root groups.
     */
    private final List<Group> rootGroups = new CopyOnWriteArrayList<>();

    /**
     * The modifiable list of users.
     */
    private final List<User> users = new CopyOnWriteArrayList<>();

    /**
     * Constructor.
     */
    public MyMemoryRealm() {
        super();
        setVerifier(new DefaultVerifier());
        setEnroler(new DefaultEnroler());
    }

    /**
     * Recursively adds groups where a given user is a member.
     *
     * @param user The member user.
     * @param userGroups The set of user groups to update.
     * @param currentGroup The current group to inspect.
     * @param stack The stack of ancestor groups.
     * @param inheritOnly Indicates if only the ancestors groups that have their
     * "inheritRoles" property enabled should be added.
     */
    private void addGroups(final User user, final Set<Group> userGroups,
            final Group currentGroup, final List<Group> stack, final boolean inheritOnly) {
        if ((currentGroup != null) && !stack.contains(currentGroup)) {
            stack.add(currentGroup);

            if (currentGroup.getMemberUsers().contains(user)) {
                userGroups.add(currentGroup);

                // Add the ancestor groups as well
                boolean inherit = !inheritOnly
                        || currentGroup.isInheritingRoles();
                Group group;

                for (int i = stack.size() - 2; inherit && (i >= 0); i--) {
                    group = stack.get(i);
                    userGroups.add(group);
                    inherit = !inheritOnly || group.isInheritingRoles();
                }
            }

            for (final Group group : currentGroup.getMemberGroups()) {
                addGroups(user, userGroups, group, stack, inheritOnly);
            }
        }
    }

    /**
     * Finds the set of groups where a given user is a member. Note that
     * inheritable ancestors groups are also returned.
     *
     * @param user The member user.
     * @return The set of groups.
     */
    public Set<Group> findGroups(final User user) {
        return findGroups(user, true);
    }

    /**
     * Finds the set of groups where a given user is a member.
     *
     * @param user The member user.
     * @param inheritOnly Indicates if only the ancestors groups that have their
     * "inheritRoles" property enabled should be added.
     * @return The set of groups.
     */
    public Set<Group> findGroups(final User user, final boolean inheritOnly) {
        final Set<Group> result = new HashSet<>();
        List<Group> stack;

        // Recursively find user groups
        for (final Group group : getRootGroups()) {
            stack = new ArrayList<>();
            addGroups(user, result, group, stack, inheritOnly);
        }

        return result;
    }

    /**
     * Finds the roles mapped to a given user group.
     *
     * @param application The parent application. Can't be null.
     * @param userGroup The user group.
     * @return The roles found.
     * @throws IllegalArgumentException If application is null.
     */
    public Set<Role> findRoles(final Application application, final Group userGroup) {
        if (application == null) {
            throw new IllegalArgumentException(
                    "The application argument can't be null");
        }

        final Set<Role> result = new HashSet<>();
        Object source;

        for (final RoleMapping mapping : getRoleMappings()) {
            source = mapping.getSource();

            if ((userGroup != null) && userGroup.equals(source)) {
                if (mapping.getTarget().getApplication() == application) {
                    result.add(mapping.getTarget());
                }
            }
        }

        return result;
    }

    /**
     * Finds the roles mapped to given user groups.
     *
     * @param application The parent application. Can't be null.
     * @param userGroups The user groups.
     * @return The roles found.
     * @throws IllegalArgumentException If application is null.
     */
    public Set<Role> findRoles(final Application application, final Set<Group> userGroups) {
        if (application == null) {
            throw new IllegalArgumentException(
                    "The application argument can't be null");
        }

        final Set<Role> result = new HashSet<>();
        Object source;

        for (final RoleMapping mapping : getRoleMappings()) {
            source = mapping.getSource();

            if (userGroups != null && userGroups.contains(source)) {
                if (mapping.getTarget().getApplication() == application) {
                    result.add(mapping.getTarget());
                }
            }
        }

        return result;
    }

    /**
     * Finds the roles mapped to a given user, for a specific application.
     *
     * @param application The parent application. Can't be null.
     * @param user The user.
     * @return The roles found.
     * @throws IllegalArgumentException If application is null.
     */
    public Set<Role> findRoles(final Application application, final User user) {
        if (application == null) {
            throw new IllegalArgumentException(
                    "The application argument can't be null");
        }

        final Set<Role> result = new HashSet<>();
        Object source;

        for (final RoleMapping mapping : getRoleMappings()) {
            source = mapping.getSource();

            if ((user != null) && user.equals(source)) {
                if (mapping.getTarget().getApplication() == application) {
                    result.add(mapping.getTarget());
                }
            }
        }

        return result;
    }

    /**
     * Finds the roles mapped to given user group.
     *
     * @param userGroup The user group.
     * @return The roles found.
     */
    public Set<Role> findRoles(final Group userGroup) {
        final Set<Role> result = new HashSet<>();
        Object source;

        for (final RoleMapping mapping : getRoleMappings()) {
            source = mapping.getSource();

            if ((userGroup != null) && userGroup.equals(source)) {
                result.add(mapping.getTarget());
            }
        }

        return result;
    }

    /**
     * Finds the roles mapped to given user groups.
     *
     * @param userGroups The user groups.
     * @return The roles found.
     */
    public Set<Role> findRoles(final Set<Group> userGroups) {
        final Set<Role> result = new HashSet<>();
        Object source;

        for (final RoleMapping mapping : getRoleMappings()) {
            source = mapping.getSource();

            if (userGroups != null && userGroups.contains(source)) {
                result.add(mapping.getTarget());
            }
        }

        return result;
    }

    /**
     * Finds the roles mapped to a given user.
     *
     * @param user The user.
     * @return The roles found.
     */
    public Set<Role> findRoles(final User user) {
        final Set<Role> result = new HashSet<>();
        Object source;

        for (final RoleMapping mapping : getRoleMappings()) {
            source = mapping.getSource();

            if (user != null && user.equals(source)) {
                result.add(mapping.getTarget());
            }
        }

        return result;
    }

    /**
     * Finds a user in the organization based on its identifier.
     *
     * @param userIdentifier The identifier to match.
     * @return The matched user or null.
     */
    public User findUser(final String userIdentifier) {
        User result = null;
        User user;

        for (int i = 0; (result == null) && (i < getUsers().size()); i++) {
            user = getUsers().get(i);

            if (user.getIdentifier().equals(userIdentifier)) {
                result = user;
            }
        }

        return result;
    }

    /**
     * Returns the modifiable list of role mappings.
     *
     * @return The modifiable list of role mappings.
     */
    private List<RoleMapping> getRoleMappings() {
        return roleMappings;
    }

    /**
     * Returns the modifiable list of root groups.
     *
     * @return The modifiable list of root groups.
     */
    public List<Group> getRootGroups() {
        return rootGroups;
    }

    /**
     * Returns the modifiable list of users.
     *
     * @return The modifiable list of users.
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Maps a group defined in a component to a role defined in the application.
     *
     * @param group The source group.
     * @param role The target role.
     */
    public void map(final Group group, final Role role) {
        getRoleMappings().add(new RoleMapping(group, role));
    }

    /**
     * Maps a user defined in a component to a role defined in the application.
     *
     * @param user The source user.
     * @param application The parent application. Can't be null.
     * @param roleName he target role name.
     * @throws IllegalArgumentException If application is null.
     */
    public void map(final User user, final Application application, final String roleName) {
        map(user, Role.get(application, roleName, null));
    }

    /**
     * Maps a user defined in a component to a role defined in the application.
     *
     * @param user The source user.
     * @param role The target role.
     */
    public void map(final User user, final Role role) {
        getRoleMappings().add(new RoleMapping(user, role));
    }

    /**
     * Sets the modifiable list of root groups. This method clears the current
     * list and adds all entries in the parameter list.
     *
     * @param rootGroups A list of root groups.
     */
    public void setRootGroups(final List<Group> rootGroups) {
        synchronized (getRootGroups()) {
            if (rootGroups != getRootGroups()) {
                getRootGroups().clear();

                if (rootGroups != null) {
                    getRootGroups().addAll(rootGroups);
                }
            }
        }
    }

    /**
     * Sets the modifiable list of users. This method clears the current list
     * and adds all entries in the parameter list.
     *
     * @param users A list of users.
     */
    public void setUsers(final List<User> users) {
        synchronized (getUsers()) {
            if (users != getUsers()) {
                getUsers().clear();

                if (users != null) {
                    getUsers().addAll(users);
                }
            }
        }
    }

    /**
     * Unmaps a group defined in a component from a role defined in the
     * application.
     *
     * @param group The source group.
     * @param application The parent application. Can't be null.
     * @param roleName The target role name.
     * @throws IllegalArgumentException If application is null.
     */
    public void unmap(final Group group, final Application application, final String roleName) {
        unmap(group, Role.get(application, roleName, null));
    }

    /**
     * Unmaps a group defined in a component from a role defined in the
     * application.
     *
     * @param group The source group.
     * @param role The target role.
     */
    public void unmap(final Group group, final Role role) {
        unmap((Object) group, role);
    }

    /**
     * Unmaps an element (user, group or organization) defined in a component
     * from a role defined in the application.
     *
     * @param source The source group.
     * @param role The target role.
     */
    private void unmap(final Object source, final Role role) {
        RoleMapping mapping;

        for (int i = getRoleMappings().size() - 1; i >= 0; i--) {
            mapping = getRoleMappings().get(i);

            if (mapping.getSource().equals(source) && mapping.getTarget().equals(role)) {
                getRoleMappings().remove(i);
            }
        }
    }

    /**
     * Unmaps a user defined in a component from a role defined in the
     * application.
     *
     * @param user The source user.
     * @param application The parent application. Can't be null.
     * @param roleName The target role name.
     * @throws IllegalArgumentException If application is null.
     */
    public void unmap(final User user, final Application application, final String roleName) {
        unmap(user, Role.get(application, roleName, null));
    }

    /**
     * Unmaps a user defined in a component from a role defined in the
     * application.
     *
     * @param user The source user.
     * @param role The target role.
     */
    public void unmap(final User user, final Role role) {
        unmap((Object) user, role);
    }

    /**
     * Enroler based on the default security model.
     */
    private class DefaultEnroler implements Enroler {

        /**
         * {@inheritDoc}
         */
        @Override
        public void enrole(final ClientInfo clientInfo) {
            final User user = findUser(clientInfo.getUser().getIdentifier());

            if (user != null) {
                // Find all the inherited groups of this user
                final Set<Group> userGroups = findGroups(user);

                // Add roles specific to this user
                final Set<Role> userRoles = findRoles(user);

                for (final Role role : userRoles) {
                    clientInfo.getRoles().add(role);
                }

                // Add roles common to group members
                final Set<Role> groupRoles = findRoles(userGroups);

                for (final Role role : groupRoles) {
                    clientInfo.getRoles().add(role);
                }
            }
        }
    }

    /**
     * Verifier based on the default security model. It looks up users in the
     * mapped organizations.
     */
    private class DefaultVerifier extends SecretVerifier {

        /**
         * {@inheritDoc}
         */
        @Override
        protected User createUser(final String identifier, final Request request,
                final Response response) {
            final User result = new User(identifier);

            // Find the reference user
            final User user = findUser(identifier);

            if (user != null) {
                // Copy the properties of the reference user
                result.setEmail(user.getEmail());
                result.setFirstName(user.getFirstName());
                result.setLastName(user.getLastName());
            }

            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int verify(final String identifier, final char[] secret) {
            char[] actualSecret = null;
            final User user = findUser(identifier);

            if (user != null) {
                actualSecret = user.getSecret();
            }

            return compare(secret, actualSecret) ? RESULT_VALID : RESULT_INVALID;
        }
    }
}
