package com.jaliansystems.javadriver.examples.swing.ut;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchType;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;


public class LoginDialogTest {

    private LoginDialog login;
    private JavaDriver driver;
    private WebElement user, pass, loginBtn, cancelBtn;
    private WebDriverWait wait;
    private final Logger logger = Logger.getLogger(LoginDialogTest.class.getName());


    @Before
    public void setUp(){
        logger.info("JUNIT 4 BEFORE");
    }

    @BeforeEach
    void init() {
        logger.info("Before each test");
        login = new LoginDialog() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSuccess() {
            }

            @Override
            protected void onCancel() {
            }
        };

        SwingUtilities.invokeLater(() -> login.setVisible(true));
        JavaProfile profile = new JavaProfile(LaunchMode.EMBEDDED);
        profile.setLaunchType(LaunchType.SWING_APPLICATION);
        driver = new JavaDriver(profile);
        wait = new WebDriverWait(driver, 10);
        user = driver.findElement(By.cssSelector("text-field"));
        pass = driver.findElement(By.cssSelector("password-field"));
        loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));
        cancelBtn = driver.findElement(By.cssSelector("button[text='Cancel']"));
    }

    @AfterEach
    void tearDown() {
        logger.info("After each test");
        try {
            if (login != null)
                SwingUtilities.invokeAndWait(() -> login.dispose());
            if (driver != null)
                driver.quit();
        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("Test if login with credentials work")
    @ParameterizedTest(name = "Username {0} and password {1} is a match")
    @CsvSource({"bob,secret"})
    public void testSuccessfulLoginProcess(String username, String password) {
        user.sendKeys(username);
        pass.sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        loginBtn.click();

        assertAll("login",
                () -> assertEquals(username, user.getText(),  user.getText() + " not found "),
                () -> assertEquals(password, pass.getText(), "Password dont match " + user.getText()));

        assertTrue(login.isSucceeded(), "Login attempt failed");
        assertNotNull(login.getSize(), "Returned Null");
    }

    @DisplayName("Test if cancel button works as intended")
    @Test
    public void testCancelButton() {
        cancelBtn.click();
        assertFalse(login.isSucceeded());
    }

    @DisplayName("Test if login with wrong credentials work")
    @ParameterizedTest(name = "Username {0} and password {1} does not match")
    @CsvSource({"bob,wrong"})
    public void testFailedLoginProcess(String username, String password) {
        user.sendKeys(username);
        pass.sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        loginBtn.click();

        driver.switchTo().window("Invalid Login");
        driver.findElement(By.cssSelector("button[text='OK']")).click();
        driver.switchTo().window("Login");

        assertAll("cleared",
                () -> assertEquals("", user.getText(), "Username Field not reset"),
                () -> assertEquals("", pass.getText(), "Password Field not reset"));
    }

    @DisplayName("Test for tooltips on elements, should not return null")
    @Test
    public void testElementsForTooltips() {
        List<WebElement> textComponents = driver.findElements(By.className(JTextComponent.class.getName()));
        for (WebElement tc : textComponents) {
            assertNotEquals(null, tc.getAttribute("toolTipText"), "Tooltip returned null");
        }
    }

    @DisplayName("Test if Caps-lock is on, pass if CAPS not on, fail if on")
    @Test
    public void testCapsLockWarning(){
        boolean isCapsOn = login.capsLockWarning();
        assertFalse(isCapsOn);
    }
}
