/*
 * Copyright 2006-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.security.oauth2.client.token.grant.code;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.security.oauth2.client.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author Dave Syer
 * 
 */
public class TestAuthorizationCodeAccessTokenProvider {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	private MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

	private AuthorizationCodeAccessTokenProvider provider = new AuthorizationCodeAccessTokenProvider() {
		@Override
		protected OAuth2AccessToken retrieveToken(MultiValueMap<String, String> form,
				OAuth2ProtectedResourceDetails resource) {
			params.putAll(form);
			return new OAuth2AccessToken("FOO");
		}
	};

	private AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();

	@Test
	public void testGetAccessToken() throws Exception {
		AccessTokenRequest request = new AccessTokenRequest();
		request.setAuthorizationCode("foo");
		resource.setAccessTokenUri("http://localhost/oauth/token");
		assertEquals("FOO", provider.obtainAccessToken(resource, request).getValue());
	}

	@Test
	public void testRedirectToAuthorizationEndpoint() throws Exception {
		AccessTokenRequest request = new AccessTokenRequest();
		request.setCurrentUri("/come/back/soon");
		resource.setUserAuthorizationUri("http://localhost/oauth/authorize");
		try {
			provider.obtainAccessToken(resource, request);
			fail("Expected UserRedirectRequiredException");
		}
		catch (UserRedirectRequiredException e) {
			assertEquals("http://localhost/oauth/authorize", e.getRedirectUri());
			assertEquals("/come/back/soon", e.getStateToPreserve());
		}
	}

	@Test(expected=IllegalStateException.class)
	public void testRedirectNotSpecified() throws Exception {
		AccessTokenRequest request = new AccessTokenRequest();
		resource.setUserAuthorizationUri("http://localhost/oauth/authorize");
		try {
			provider.obtainAccessToken(resource, request);
			fail("Expected UserRedirectRequiredException");
		}
		catch (UserRedirectRequiredException e) {
			assertEquals("http://localhost/oauth/authorize", e.getRedirectUri());
		}
	}

	@Test
	public void testGetAccessTokenRequest() throws Exception {
		AccessTokenRequest request = new AccessTokenRequest();
		request.setAuthorizationCode("foo");
		request.setStateKey("bar");
		resource.setAccessTokenUri("http://localhost/oauth/token");
		resource.setPreEstablishedRedirectUri("http://anywhere.com");
		assertEquals("FOO", provider.obtainAccessToken(resource, request).getValue());
		// System.err.println(params);
		assertEquals("authorization_code", params.getFirst("grant_type"));
		assertEquals("foo", params.getFirst("code"));
		assertEquals("http://anywhere.com", params.getFirst("redirect_uri"));
		assertEquals("bar", params.getFirst("state"));
	}

}
