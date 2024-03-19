/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.gru.agp;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * A simple mock implementation of the {@code Context} interface. Default
 * values are stubbed out, and setters are provided so you can customize
 * the context before passing it to your function.
 */
public class MockContext implements Context {

    public static final int SAMPLE_MEMORY_LIMIT = 128;
    public static final int SAMPLE_REMAINING_TIME = 15000;
    public static final String AWS_REQUEST_ID = "aws_request_id";
    public static final String FUNCTION_NAME = "function_name";
    public static final String FUNCTION_VERSION = "function_version";
    public static final String INVOKED_FUNCTION_ARN = "invoked_function_arn";
    public static final String LOG_GROUP_NAME = "log_group_name";
    public static final String LOG_STREAM_NAME = "log_stream_name";

    private String awsRequestId = AWS_REQUEST_ID;
    private ClientContext clientContext;
    private String functionName = FUNCTION_NAME;
    private String functionVersion = FUNCTION_VERSION;
    private String invokedFunctionArn = INVOKED_FUNCTION_ARN;
    private CognitoIdentity identity;
    private String logGroupName = LOG_GROUP_NAME;
    private String logStreamName = LOG_STREAM_NAME;
    private LambdaLogger logger = new MockLogger();
    private int memoryLimitInMB = SAMPLE_MEMORY_LIMIT;
    private int remainingTimeInMillis = SAMPLE_REMAINING_TIME;

    public String getAwsRequestId() {
        return awsRequestId;
    }

    public void setAwsRequestId(String awsRequestId) {
        this.awsRequestId = awsRequestId;
    }

    public ClientContext getClientContext() {
        return clientContext;
    }

    public void setClientContext(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionVersion() {
        return functionVersion;
    }

    public void setFunctionVersion(String functionVersion) {
        this.functionVersion = functionVersion;
    }

    public String getInvokedFunctionArn() {
        return invokedFunctionArn;
    }

    public void setInvokedFunctionArn(String invokedFunctionArn) {
        this.invokedFunctionArn = invokedFunctionArn;
    }

    public CognitoIdentity getIdentity() {
        return identity;
    }

    public void setIdentity(CognitoIdentity identity) {
        this.identity = identity;
    }

    public String getLogGroupName() {
        return logGroupName;
    }

    public void setLogGroupName(String logGroupName) {
        this.logGroupName = logGroupName;
    }

    public String getLogStreamName() {
        return logStreamName;
    }

    public void setLogStreamName(String logStreamName) {
        this.logStreamName = logStreamName;
    }

    public LambdaLogger getLogger() {
        return logger;
    }

    public void setLogger(LambdaLogger logger) {
        this.logger = logger;
    }

    public int getMemoryLimitInMB() {
        return memoryLimitInMB;
    }

    public void setMemoryLimitInMB(int memoryLimitInMB) {
        this.memoryLimitInMB = memoryLimitInMB;
    }

    public int getRemainingTimeInMillis() {
        return remainingTimeInMillis;
    }

    public void setRemainingTimeInMillis(int remainingTimeInMillis) {
        this.remainingTimeInMillis = remainingTimeInMillis;
    }
}
