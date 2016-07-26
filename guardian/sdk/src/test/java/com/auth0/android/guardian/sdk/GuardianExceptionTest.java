/*
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.guardian.sdk;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class GuardianExceptionTest {

    @Test
    public void shouldBeUnknownError() throws Exception {
        GuardianException exception = new GuardianException("Some error message");

        assertThat(exception.isInvalidOTP(), is(equalTo(false)));
        assertThat(exception.isInvalidToken(), is(equalTo(false)));
        assertThat(exception.isEnrollmentNotFound(), is(equalTo(false)));
    }

    @Test
    public void testIsInvalidOTP() throws Exception {
        GuardianException exception = new GuardianException(createErrorMap("invalid_otp"));

        assertThat(exception.isInvalidOTP(), is(equalTo(true)));
        assertThat(exception.isInvalidToken(), is(equalTo(false)));
        assertThat(exception.isEnrollmentNotFound(), is(equalTo(false)));
    }

    @Test
    public void testIsInvalidToken() throws Exception {
        GuardianException exception = new GuardianException(createErrorMap("invalid_token"));

        assertThat(exception.isInvalidOTP(), is(equalTo(false)));
        assertThat(exception.isInvalidToken(), is(equalTo(true)));
        assertThat(exception.isEnrollmentNotFound(), is(equalTo(false)));
    }

    @Test
    public void testIsEnrollmentNotFound() throws Exception {
        GuardianException exception = new GuardianException(createErrorMap("device_account_not_found"));

        assertThat(exception.isInvalidOTP(), is(equalTo(false)));
        assertThat(exception.isInvalidToken(), is(equalTo(false)));
        assertThat(exception.isEnrollmentNotFound(), is(equalTo(true)));
    }

    private Map<String, Object> createErrorMap(String errorCode) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("errorCode", errorCode);
        return errorMap;
    }
}