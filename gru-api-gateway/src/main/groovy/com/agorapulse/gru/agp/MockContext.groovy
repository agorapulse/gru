package com.agorapulse.gru.agp

import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import groovy.transform.CompileStatic

/**
 * A simple mock implementation of the {@code Context} interface. Default
 * values are stubbed out, and setters are provided so you can customize
 * the context before passing it to your function.
 */
// TODO: ablity to setup the text context from Gru's test definition
@CompileStatic
class MockContext implements Context {

    private static final int SAMPLE_MEMORY_LIMIT = 128
    private static final int SAMPLE_REMAINING_TIME = 15000
    private static final String AWS_REQUEST_ID = 'aws_request_id'
    private static final String FUNCTION_NAME = 'function_name'
    private static final String FUNCTION_VERSION = 'function_version'
    private static final String INVOKED_FUNCTION_ARN = 'invoked_function_arn'
    private static final String LOG_GROUP_NAME = 'log_group_name'
    private static final String LOG_STREAM_NAME = 'log_stream_name'

    String awsRequestId = AWS_REQUEST_ID
    ClientContext clientContext
    String functionName = FUNCTION_NAME
    String functionVersion = FUNCTION_VERSION
    String invokedFunctionArn = INVOKED_FUNCTION_ARN
    CognitoIdentity identity
    String logGroupName = LOG_GROUP_NAME
    String logStreamName = LOG_STREAM_NAME
    LambdaLogger logger = new MockLogger()
    int memoryLimitInMB = SAMPLE_MEMORY_LIMIT
    int remainingTimeInMillis = SAMPLE_REMAINING_TIME

}
