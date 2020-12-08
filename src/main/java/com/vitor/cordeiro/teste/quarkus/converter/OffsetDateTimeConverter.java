package com.vitor.cordeiro.teste.quarkus.converter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.enterprise.context.ApplicationScoped;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@RegisterForReflection
@ApplicationScoped
public class OffsetDateTimeConverter implements DynamoDBTypeConverter<String, OffsetDateTime> {

    @Override
    public String convert(OffsetDateTime offsetDateTime) {
        String zonedDateTime = null;
        try {
            if (offsetDateTime != null) {
                zonedDateTime = offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return zonedDateTime;
    }

    @Override
    public OffsetDateTime unconvert(String offSetDateTimeStr) {

        OffsetDateTime zonedDateTime = null;
        try {
            if (!offSetDateTimeStr.isEmpty() && offSetDateTimeStr != null) {
                zonedDateTime = OffsetDateTime.parse(offSetDateTimeStr);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return zonedDateTime;
    }
}