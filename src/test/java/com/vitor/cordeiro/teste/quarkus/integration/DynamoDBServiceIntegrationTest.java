package com.vitor.cordeiro.teste.quarkus.integration;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.*;
import com.vitor.cordeiro.teste.quarkus.entity.Book;
import com.vitor.cordeiro.teste.quarkus.service.DynamoDBServiceImpl;
import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.inject.Inject;
import java.util.ArrayList;


//TODO ...

/*
@Testcontainers
@QuarkusTest
public class DynamoDBServiceIntegrationTest {

    private static final Logger LOG = Logger.getLogger(DynamoDBServiceIntegrationTest.class);

    private static final String DYNAMODB_TABLE_NAME = "TEST-TABLE";
    private AmazonDynamoDB dynamoDBClient;
    private DynamoDBMapperConfig mapperConfig;

    @Inject
    DynamoDBServiceImpl dynamoDBService;

    @Container
    public static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:0.11.3"))
            .withServices(LocalStackContainer.Service.DYNAMODB);

    static {
        localstack.start();
        System.setProperty("DYNAMODB_PAYMENT_ORDER_TABLE", DYNAMODB_TABLE_NAME);
    }

    @Before
    public void setUp() {
        setupDynamo();
    }

    void setupDynamo() {
        setDynamoDBClient();
        createTable();
    }

    void setDynamoDBClient() {
        dynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(localstack.getEndpointConfiguration(LocalStackContainer.Service.DYNAMODB))
                .withCredentials(localstack.getDefaultCredentialsProvider())
                .build();
    }

    void createTable() {
        ArrayList<KeySchemaElement> ks = new ArrayList<>();
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        ks.add(new KeySchemaElement().withAttributeName("libraryCode").withKeyType(KeyType.HASH));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("libraryCode").withAttributeType("S"));

        try {
            CreateTableRequest tableRequest = new CreateTableRequest()
                    .withTableName(DYNAMODB_TABLE_NAME)
                    .withKeySchema(ks)
                    .withAttributeDefinitions(attributeDefinitions);

            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
            dynamoDBClient.createTable(tableRequest);

        } catch (ResourceInUseException e) {
            LOG.infof("Falha ao criar a tabela: %s", e.getMessage());
        }
    }

    @Test
    public void simpleDynamoDBCreateTest() {
        String name = "TEST_001";

        dynamoDBService.setClient(dynamoDBClient);

        var book = new Book();
        book.setLibraryCode(name);

        dynamoDBService.save(book);
        var resp = dynamoDBService.findByLibraryCode(name);

        Assertions.assertEquals(resp.getLibraryCode(), book.getLibraryCode());
    }
}
*/
