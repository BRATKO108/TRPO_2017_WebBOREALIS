package tests.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.kspt.services.auth.AuthService;
import com.kspt.services.auth.User;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:service-test-config.xml"})
public class AuthServiceTest {

    @Autowired
    AuthService authService;

    private User user;

    @Before
    public void startUser() {
        user = createUser("change", "pass");
    }

    @Test
    public void insertUser() {
        user.setUsername("Max");
        authService.insertUser(user);
        assertFalse(user.getId() == 0L);
    }

    @Test
    public void updateUser() {
        user.setUsername("Alex");
        authService.insertUser(user);
        user.setPassword("newpass");
        authService.updateUser(user.getId(), user);
        assertEquals(user, authService.selectUser(user.getId()));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void deleteUser() {
        user.setUsername("Ivan");
        authService.insertUser(user);
        authService.deleteUser(user.getId());
        System.out.println(authService.selectUser(user.getId()));
    }

    @Test
    public void selectUser() {
        user.setUsername("Mike");
        authService.insertUser(user);
        assertEquals(user, authService.selectUser(user.getId()));
    }

    @Test
    public void selectUserByUsername() {
        user.setUsername("Kate");
        authService.insertUser(user);
        assertEquals(user, authService.selectUserByUsername("Kate"));
    }

    @Test
    public void selectAuthorities() {
        String role = "ROLE_ADMIN";
        user.setUsername("admin");
        authService.insertUser(user);
        authService.insertRole(user.getId(), role);
        System.out.println(user);
        List<String> authorities = authService.selectAuthorities(user.getId());
        assertFalse(authorities.isEmpty() || !authorities.contains(role));
    }

    private User createUser(String username, String password) {
        User tmp = new User();
        tmp.setUsername(username);
        tmp.setPassword(password);
        return tmp;
    }
}
