package org.springframework.security.oauth2.provider.verification;

import junit.framework.TestCase;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;
import java.util.Collection;

public abstract class TestVerificationCodeServicesBase extends TestCase {

  abstract VerificationCodeServices getVerificationCodeServices();

  public void testCreateVerificationCode() {
    OAuth2Authentication<VerificationCodeAuthenticationToken, TestAuthentication> expectedAuthentication = new OAuth2Authentication<VerificationCodeAuthenticationToken, TestAuthentication>(new VerificationCodeAuthenticationToken("id", null, null, null), new TestAuthentication("test2", false));
    String code = getVerificationCodeServices().createVerificationCode(expectedAuthentication);
    assertNotNull(code);

    OAuth2Authentication actualAuthentication = getVerificationCodeServices().consumeVerificationCode(code);
    assertEquals(expectedAuthentication, actualAuthentication);
  }

  public void testConsumeRemovesCode() {
    OAuth2Authentication<VerificationCodeAuthenticationToken, TestAuthentication> expectedAuthentication = new OAuth2Authentication<VerificationCodeAuthenticationToken, TestAuthentication>(new VerificationCodeAuthenticationToken("id", null, null, null), new TestAuthentication("test2", false));
    String code = getVerificationCodeServices().createVerificationCode(expectedAuthentication);
    assertNotNull(code);

    OAuth2Authentication actualAuthentication = getVerificationCodeServices().consumeVerificationCode(code);
    assertEquals(expectedAuthentication, actualAuthentication);

    try {
      getVerificationCodeServices().consumeVerificationCode(code);
      fail("Should have thrown exception");
    }
    catch (InvalidGrantException e) {
      // good we expected this
    }
  }

  public void testConsumeNonExistingCode() {
    try {
      getVerificationCodeServices().consumeVerificationCode("doesnt exist");
      fail("Should have thrown exception");
    }
    catch (InvalidGrantException e) {
      // good we expected this
    }
  }

  protected static class TestAuthentication implements Authentication, Serializable {
    private String name;
    private boolean authenticated;

    public TestAuthentication(String name, boolean authenticated) {
      this.name = name;
      this.authenticated = authenticated;
    }

    public Collection<GrantedAuthority> getAuthorities() {
      return null;
    }

    public Object getCredentials() {
      return null;
    }

    public Object getDetails() {
      return null;
    }

    public Object getPrincipal() {
      return null;
    }

    public boolean isAuthenticated() {
      return authenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
      this.authenticated = isAuthenticated;
    }

    public String getName() {
      return name;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      TestAuthentication that = (TestAuthentication) o;

      if (authenticated != that.authenticated) {
        return false;
      }
      if (name != null ? !name.equals(that.name) : that.name != null) {
        return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (authenticated ? 1 : 0);
      return result;
    }
  }

}