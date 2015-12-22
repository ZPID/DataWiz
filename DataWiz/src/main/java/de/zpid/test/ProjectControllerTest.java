package de.zpid.test;

import org.junit.Test;

import de.zpid.datawiz.controller.ProjectController;
import de.zpid.datawiz.dto.UserDTO;
import junit.framework.TestCase;

public class ProjectControllerTest extends TestCase {

  // @Test
  // public void testCreateProjectForm() {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testCreateProject() {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testSaveProject() {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testAddContributor() {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testDeleteContributor() {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testEditProject() {
  // fail("Not yet implemented");
  // }

  @Test(expected = Exception.class)
  public void testGetProjectData() throws Exception {
    UserDTO user = new UserDTO();
    user.setId(1);
    user.setEmail("samy@xyz.com");
    ProjectController pr = new ProjectController();
    assertNull(pr.getProjectData(null, null));
    assertNull(pr.getProjectData(null, user));
    assertNull(pr.getProjectData("1", null));
    assertTrue(pr.getProjectData("1", user) == null);

  }
}
