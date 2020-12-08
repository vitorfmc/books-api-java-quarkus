package com.vitor.cordeiro.teste.quarkus.util;

import org.eclipse.microprofile.config.ConfigProvider;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConstantHelper {

    private static String googleUrl = getEnvValue("GOOGLE_URL");
    private static String awsRegion = getEnvValue("AWS_REGION");
    private static String dynamoTableName = getEnvValue("DYNAMODB_BOOK_TABLE");

    private static String getEnvValue(String key) {
        if(System.getenv(key) == null)
            return ConfigProvider.getConfig().getValue(key, String.class);
        return System.getenv(key);
    }

    public static String getGoogleUrl() {
        return getEnvValue("GOOGLE_URL");
    }
    public static String getAwsRegion() {
        return getEnvValue("AWS_REGION");
    }
    public static String getDynamoTableName() {
        return getEnvValue("DYNAMODB_BOOK_TABLE");
    }

}
