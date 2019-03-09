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
package fr.cnes.doi.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.cnes.doi.exception.DOIDbException;
import fr.cnes.doi.plugin.impl.db.persistence.impl.DOIDbDataAccessServiceImpl;
import fr.cnes.doi.plugin.impl.db.persistence.model.DOIProject;
import fr.cnes.doi.utils.DOIUser;
import fr.cnes.doi.plugin.impl.db.persistence.service.DOIDbDataAccessService;

public class DOIDBTest {

    private Logger logger = LoggerFactory.getLogger(DOIDBTest.class);

    private DOIDbDataAccessService das = new DOIDbDataAccessServiceImpl(
	    getClass().getClassLoader().getResource("config-test.properties").getFile());

    private DOIUser testuser;
    private DOIProject testProject;

    @Before
    public void init() {

	// Test User
	testuser = new DOIUser();
	testuser.setUsername("user");
	testuser.setAdmin(false);
	testuser.setEmail("doidbuser@mail.com");

	// Test Project
	testProject = new DOIProject();
	testProject.setProjectname("doiprojecttest");
	testProject.setSuffix(1010);

	try {
	    das.removeDOIUser(testuser.getUsername());
	    das.removeDOIProject(testProject.getSuffix());
	} catch (DOIDbException e) {
	    fail();
	}
    }

    @Test
    public void testDoiUsers() {
	try {

	    das.addDOIUser(testuser.getUsername(), testuser.isAdmin(), testuser.getEmail());
	    final List<DOIUser> userFromDb = das.getAllDOIusers();
	    assertEquals(userFromDb.size(), 1);
	    assertEquals(userFromDb.get(0).isEqualTo(testuser), true);

	    // test setAdmin
	    das.setAdmin(testuser.getUsername());
	    assertEquals(das.getAllDOIusers().get(0).isAdmin(), true);

	    // test unsetAdmin
	    das.unsetAdmin(testuser.getUsername());
	    assertEquals(das.getAllDOIusers().get(0).isAdmin(), false);

	    // remove user from testing database
	    das.removeDOIUser(testuser.getUsername());
	    assertEquals(das.getAllDOIusers().size(), 0);

	} catch (DOIDbException e) {
	    logger.error("testDoiUsers failed: unexpected exception: ", e);
	    fail();
	}
    }

    @Test
    public void testDoiProject() {
	try {
	    das.addDOIProject(testProject.getSuffix(), testProject.getProjectname());
	    List<DOIProject> projectsFromDb = das.getAllDOIProjects();
	    assertEquals(projectsFromDb.size(), 1);
	    assertEquals(projectsFromDb.get(0).isEqualTo(testProject), true);
	    das.removeDOIProject(testProject.getSuffix());
	    assertEquals(das.getAllDOIusers().size(), 0);
	} catch (DOIDbException e) {
	    logger.error("testDoiProject failed: unexpected exception: ", e);
	    fail();
	}
    }

    @Test
    public void testDoiProjectsUsersAssignations() {
	try {
	    // should assign a user to a project when both exist in database
	    das.addDOIProject(testProject.getSuffix(), testProject.getProjectname());
	    das.addDOIUser(testuser.getUsername(), testuser.isAdmin(), testuser.getEmail());
	    das.addDOIProjectToUser(testuser.getUsername(), testProject.getSuffix());
	    List<DOIProject> projectsFromDb = das.getAllDOIProjectsForUser(testuser.getUsername());
	    List<DOIUser> userFromProject = das.getAllDOIUsersForProject(testProject.getSuffix());
	    assertEquals(projectsFromDb.size(), 1);
	    assertEquals(userFromProject.size(), 1);
	    assertEquals(projectsFromDb.get(0).isEqualTo(testProject), true);
	    assertEquals(userFromProject.get(0).isEqualTo(testuser), true);
	    das.removeDOIProjectFromUser(testuser.getUsername(), testProject.getSuffix());
	    assertEquals(das.getAllDOIProjectsForUser(testuser.getUsername()).size(), 0);
	} catch (DOIDbException e) {
	    logger.error("testDoiProjectsUsersAssignations failed: unexpected exception: ", e);
	    fail();
	}
    }

    @Test
    public void testDoiProjectsUsersAssignationsExceptionRaisedCase() {
	try {
	    das.addDOIProjectToUser(testuser.getUsername(), testProject.getSuffix());
	    fail();
	} catch (DOIDbException e) {
	} catch (Exception e) {
	    fail();
	}
    }

}
