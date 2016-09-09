import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import de.zpid.datawiz.configuration.DataWizConfiguration;
import de.zpid.datawiz.configuration.SecurityWebApplicationInitializer;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { DataWizConfiguration.class, SecurityWebApplicationInitializer.class })
public class MockControllerTest {

  @Autowired
  WebApplicationContext wac;
  @Autowired
  MockHttpSession session;
  @Autowired
  MockHttpServletRequest request;
  @Autowired
  UserDAO userDAO;

  private MockMvc mockMvc;

  @Before
  public void setup() throws Exception {
    System.out.println("************ START SETUP *************");
    this.mockMvc = webAppContextSetup(this.wac).apply(springSecurity()).build();
    MvcResult result = mockMvc.perform(formLogin().user("email", "datawiz@zpid.de").password("abc125"))
        .andExpect(authenticated()).andReturn();
    this.session = (MockHttpSession) result.getRequest().getSession();
    System.out.println("************ END SETUP *************");
  }

  @Test
  public void testPanel() throws Exception {
    System.out.println("************ START PANEL *************");
    this.mockMvc.perform(get("/panel").session(this.session).accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
        .andExpect(view().name("panel"));
    System.out.println("************ END PANEL *************");
  }

  @Test
  public void testRegisterPOST() throws Exception {
    UserDTO udto = new UserDTO();
    udto.setEmail("schranzkixme@gmail.com"); 
    
    userDAO.deleteUser(udto);
    
    System.out.println("************ START REGISTER POST ERROR - EMAIL MISSING *************");
    // Password empty -> redirect to register
    this.mockMvc.perform(post("/register").with(csrf()).param("email", "").param("password", "qwerty")
        .param("password_retyped", "qwerty").param("checkedGTC", "true").accept(MediaType.TEXT_HTML))
        .andExpect(status().isOk()).andExpect(view().name("register"));
    System.out.println("************ END REGISTER POST ERROR - EMAIL MISSING *************");
    
    System.out.println("************ START REGISTER POST ERROR - PWD MISSING *************");
    // Password empty -> redirect to register
    this.mockMvc.perform(post("/register").with(csrf()).param("email", udto.getEmail()).accept(MediaType.TEXT_HTML))
        .andExpect(status().isOk()).andExpect(view().name("register"));
    System.out.println("************ END REGISTER POST ERROR - PWD MISSING *************");
    
    System.out.println("************ START REGISTER POST ERROR - PWD DIFFERENT *************");
    // Password empty -> redirect to register
    this.mockMvc.perform(post("/register").with(csrf()).param("email", udto.getEmail()).param("password", "qwerty")
        .param("password_retyped", "qwertz").param("checkedGTC", "true").accept(MediaType.TEXT_HTML))
        .andExpect(status().isOk()).andExpect(view().name("register"));
    System.out.println("************ END REGISTER POST ERROR - PWD DIFFERENT *************");
    
    System.out.println("************ START REGISTER POST SUCCESS *************");
    this.mockMvc
        .perform(post("/register").with(csrf()).param("email", udto.getEmail()).param("password", "qwerty")
            .param("password_retyped", "qwerty").param("checkedGTC", "true").accept(MediaType.TEXT_HTML))
        .andExpect(view().name("redirect:/login?activationmail"));
    System.out.println("************ END REGISTER POST SUCCESS *************");
    
    System.out.println("************ START REGISTER POST ERROR - DOUBLETTE *************");
    this.mockMvc
        .perform(post("/register").with(csrf()).param("email", udto.getEmail()).param("password", "qwerty")
            .param("password_retyped", "qwerty").param("checkedGTC", "true").accept(MediaType.TEXT_HTML))
        .andExpect(view().name("register"));
    System.out.println("************ END REGISTER POST ERROR - DOUBLETTE  *************");
  }

  @Test
  public void testRegisterGET() throws Exception {
    System.out.println("************ START REGISTER GET *************");
    mockMvc.perform(get("/register").session(this.session).accept(MediaType.TEXT_HTML))
        .andExpect(view().name("redirect:/panel"));
    mockMvc.perform(get("/register").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
        .andExpect(view().name("register"));
    System.out.println("************ END REGISTER GET *************");
  }

}
