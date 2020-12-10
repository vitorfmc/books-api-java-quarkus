package com.vitor.cordeiro.teste.quarkus.resource;

import com.vitor.cordeiro.teste.quarkus.dto.BookCreateDTO;
import com.vitor.cordeiro.teste.quarkus.dto.BookResponseDto;
import com.vitor.cordeiro.teste.quarkus.dto.BookUpdateDTO;
import com.vitor.cordeiro.teste.quarkus.exception.DataValidationException;
import com.vitor.cordeiro.teste.quarkus.exception.EntityNotFoundException;
import com.vitor.cordeiro.teste.quarkus.exception.GoogleApiGenericException;
import com.vitor.cordeiro.teste.quarkus.service.BookService;
import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Timeout;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Path("book")
@Consumes("application/json")
public class BookResource {

    @Inject
    BookService service;

    @POST
    @Timeout(unit = ChronoUnit.SECONDS, value = 10)
    @CircuitBreaker(
            requestVolumeThreshold = 20,
            failureRatio = 0.2,
            delay = 2,
            delayUnit = ChronoUnit.SECONDS,
            successThreshold = 2
    )
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(BookCreateDTO dto){
        try{
            var book = service.save(dto);
            return Response.status(Response.Status.CREATED)
                    .entity(new BookResponseDto(book, null)).build();

        }catch (DataValidationException e){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new BookResponseDto(null, e.getMessages())).build();
        }catch (GoogleApiGenericException | Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new BookResponseDto(null, Arrays.asList(e.getMessage()))).build();
        }
    }

    @PUT
    @Path("/{libraryCode}")
    @Timeout(unit = ChronoUnit.SECONDS, value = 10)
    @CircuitBreaker(
            requestVolumeThreshold = 20,
            failureRatio = 0.2,
            delay = 2,
            delayUnit = ChronoUnit.SECONDS,
            successThreshold = 2
    )
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("libraryCode") String libraryCode, BookUpdateDTO dto){
        try{
            var book = service.update(libraryCode, dto);
            return Response.status(Response.Status.OK).entity(new BookResponseDto(book, null)).build();

        }catch (EntityNotFoundException e){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new BookResponseDto(null, Arrays.asList(e.getMessage()))).build();
        }catch (DataValidationException e){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new BookResponseDto(null, e.getMessages())).build();
        }catch (GoogleApiGenericException | Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new BookResponseDto(null, Arrays.asList(e.getMessage()))).build();
        }
    }

    @DELETE
    @Path("/{libraryCode}")
    public Response delete(@PathParam("libraryCode") String libraryCode){

        try{
            service.delete(libraryCode);
            return Response.status(Response.Status.NO_CONTENT).entity(null).build();

        }catch (EntityNotFoundException e){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new BookResponseDto(null, Arrays.asList(e.getMessage()))).build();
        }catch (DataValidationException e){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new BookResponseDto(null, e.getMessages())).build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new BookResponseDto(null, Arrays.asList(e.getMessage()))).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(@NotNull @QueryParam("libraryCode") String libraryCode) {
        try {
            var data = service.findByLibraryCode(libraryCode);
            return Response.ok()
                    .entity(new BookResponseDto(data, null)).build();

        }catch (EntityNotFoundException e){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new BookResponseDto(null, Arrays.asList(e.getMessage()))).build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new BookResponseDto(null, Arrays.asList(e.getMessage()))).build();
        }
    }

}
