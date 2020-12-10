package com.vitor.cordeiro.teste.quarkus.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.vitor.cordeiro.teste.quarkus.entity.Book;
import com.vitor.cordeiro.teste.quarkus.exception.DynamoDBGeneralException;
import com.vitor.cordeiro.teste.quarkus.util.ConstantHelper;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DynamoDBServiceImpl implements DynamoDBService {

    private static final Logger LOG = Logger.getLogger(DynamoDBServiceImpl.class);
    private AmazonDynamoDB client;
    private DynamoDBMapper mapper;
    private DynamoDBMapperConfig mapperConfig;
    private String region = ConstantHelper.getAwsRegion();
    private String dynamoTableName = ConstantHelper.getDynamoTableName();

    @PostConstruct
    void initializeAmazon() {

        this.client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(region)
                .build();

        /*BasicAWSCredentials creds = new BasicAWSCredentials("AWS_IAM_KEY", "AWS_IAM_SECRET");
        this.client = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .withRegion(region).build();*/

        mapper = new DynamoDBMapper(client);
        mapperConfig = new DynamoDBMapperConfig
                .Builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(dynamoTableName))
                .build();
    }

    @Override
    public Book save(Book book) throws DynamoDBGeneralException {

        LOG.infof("[DYNAMO-SAVE] START FOR: %s", book.getLibraryCode());

        try {
            mapper.save(book, mapperConfig);
            LOG.infof("[DYNAMO-SAVE] END FOR: %s", book.getLibraryCode());
            return book;

        } catch (Exception e) {
            LOG.errorf("[DYNAMO-SAVE] Error for %s. MESSAGE: %s ", book.getLibraryCode(), e.getMessage());
            throw new DynamoDBGeneralException(e.getMessage());
        }
    }

    @Override
    public void delete(Book book) throws DynamoDBGeneralException {
        LOG.infof("[DYNAMO-DELETE] START: %s", book.getLibraryCode());

        try {
            mapper.delete(book);
            LOG.infof("[DYNAMO-DELETE] END: %s", book.getLibraryCode());

        }catch (Exception e){
            LOG.errorf("[DYNAMO-DELETE] Error for %s. MESSAGE: %s ", book.getLibraryCode(), e.getMessage());
            throw new DynamoDBGeneralException(e.getMessage());
        }
    }

    @Override
    public Book findByLibraryCode(String libraryCode) throws DynamoDBGeneralException {
        LOG.infof("[DYNAMO-FIND] START: %s", libraryCode);

        try {
            Book data = mapper.load(Book.class, libraryCode, mapperConfig);
            LOG.infof("[DYNAMO-FIND] END: %s", libraryCode);

            return data;

        } catch (DynamoDBGeneralException e) {
            throw e;

        } catch (ResourceNotFoundException e){
            return null;

        } catch (Exception e) {
            LOG.errorf("[DYNAMO-FIND] Error for %s. MESSAGE: %s ", libraryCode, e.getMessage());
            throw new DynamoDBGeneralException("[DYNAMO-FIND] Error: " + e.getMessage());
        }
    }
}
