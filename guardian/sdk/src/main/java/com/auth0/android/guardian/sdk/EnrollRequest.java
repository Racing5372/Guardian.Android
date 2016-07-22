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

import android.support.annotation.NonNull;

import com.auth0.android.guardian.sdk.networking.Callback;

import java.io.IOException;

class EnrollRequest implements GuardianAPIRequest<ParcelableEnrollment> {

    final GuardianAPIClient client;
    final EnrollmentData enrollmentData;
    final String deviceName;
    final String gcmToken;

    EnrollRequest(@NonNull GuardianAPIClient client,
                  @NonNull EnrollmentData enrollmentData,
                  @NonNull String deviceName,
                  @NonNull String gcmToken) {
        this.client = client;
        this.enrollmentData = enrollmentData;
        this.deviceName = deviceName;
        this.gcmToken = gcmToken;
    }

    @Override
    public ParcelableEnrollment execute() throws IOException, GuardianException {
        String deviceToken = client
                .getDeviceToken(enrollmentData.getEnrollmentTransactionId())
                .execute();
        Device device = client.device(enrollmentData.getDeviceId(), deviceToken)
                .create(deviceName, gcmToken)
                .execute();
        return createEnrollment(device, deviceToken);
    }

    @Override
    public void start(@NonNull final Callback<ParcelableEnrollment> callback) {
        client.getDeviceToken(enrollmentData.getEnrollmentTransactionId())
                .start(deviceTokenCallback(callback));
    }

    private Callback<String> deviceTokenCallback(@NonNull final Callback<ParcelableEnrollment> callback) {
        return new Callback<String>() {
            @Override
            public void onSuccess(String deviceToken) {
                client.device(enrollmentData.getDeviceId(), deviceToken)
                        .create(deviceName, gcmToken)
                        .start(createDeviceCallback(deviceToken, callback));
            }

            @Override
            public void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        };
    }

    private Callback<Device> createDeviceCallback(@NonNull final String deviceToken,
                                                  @NonNull final Callback<ParcelableEnrollment> callback) {
        return new Callback<Device>() {
            @Override
            public void onSuccess(Device device) {
                ParcelableEnrollment enrollment = createEnrollment(device, deviceToken);
                callback.onSuccess(enrollment);
            }

            @Override
            public void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        };
    }

    private ParcelableEnrollment createEnrollment(@NonNull Device device, @NonNull String deviceToken) {
        return new ParcelableEnrollment(client.getUrl(), enrollmentData.getIssuer(),
                enrollmentData.getUser(), enrollmentData.getPeriod(), enrollmentData.getDigits(),
                enrollmentData.getAlgorithm(), enrollmentData.getSecret(), device.getEnrollmentId(),
                device.getDeviceIdentifier(), device.getDeviceName(), device.getGCMToken(), deviceToken);
    }
}