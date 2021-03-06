package com.vitor.cordeiro.teste.quarkus.service;

import com.vitor.cordeiro.teste.quarkus.client.GoogleBooksClient;
import com.vitor.cordeiro.teste.quarkus.client.GoogleBooksClientBuilder;
import com.vitor.cordeiro.teste.quarkus.client.model.GoogleBooksResponse;
import com.vitor.cordeiro.teste.quarkus.exception.DataValidationException;
import com.vitor.cordeiro.teste.quarkus.exception.GoogleApiGenericException;
import com.vitor.cordeiro.teste.quarkus.exception.InvalidCredentialResponseStatusException;
import com.vitor.cordeiro.teste.quarkus.util.ConstantHelper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.Arrays;

@ApplicationScoped
public class GoogleBooksServiceImpl implements GoogleBooksService {

    private static final Logger LOG = Logger.getLogger(GoogleBooksServiceImpl.class);

    @Inject
    GoogleBooksClientBuilder clientBuilder;

    private String url = ConstantHelper.getGoogleUrl();

    @Override
    public GoogleBooksResponse getBookByTitle(String q)
            throws GoogleApiGenericException, DataValidationException {

        if(q == null)  throw new DataValidationException(Arrays.asList("q cannot be null"));

        try {
            GoogleBooksClient client = clientBuilder.build(url);

            LOG.infof("[GOOGLE SERVICE] Invoked: %s", url + "/books/v1/volumes?q=+intitle:" + q);

            return client.getBook("+intitle:" + q, 10, 0, "pt");

        } catch (InvalidCredentialResponseStatusException | MalformedURLException e) {
            LOG.errorf("[GOOGLE SERVICE] Error: %s", e.getMessage());
            throw new GoogleApiGenericException(e.getMessage());

        } catch (Exception e) {
            LOG.errorf("[GOOGLE SERVICE] Error: %s", e.getMessage());
            throw e;
        }
    }

}
