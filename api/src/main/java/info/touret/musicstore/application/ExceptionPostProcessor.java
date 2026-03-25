package info.touret.musicstore.application;

import info.touret.musicstore.domain.exception.DataNotFoundException;
import info.touret.musicstore.domain.exception.InvalidDataException;
import io.quarkiverse.resteasy.problem.HttpProblem;
import io.quarkiverse.resteasy.problem.postprocessing.ProblemContext;
import io.quarkiverse.resteasy.problem.postprocessing.ProblemPostProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

/**
 * Quarkus RestEasy Post Processor. Maps custom exceptions to specific HTTP status codes
 */
@ApplicationScoped
public class ExceptionPostProcessor implements ProblemPostProcessor {
    @Override
    public HttpProblem apply(HttpProblem problem, ProblemContext context) {
        return switch (context.cause) {
            case IllegalArgumentException ignored ->
                    HttpProblem.builder().withDetail(problem.getDetail()).withStatus(Response.Status.BAD_REQUEST).build();
            case DataNotFoundException notFoundException ->
                    HttpProblem.builder().withDetail(problem.getDetail()).withStatus(Response.Status.NOT_FOUND).build();
            case InvalidDataException invalidDataExceptionException ->
                    HttpProblem.builder().withDetail(problem.getDetail()).withStatus(Response.Status.BAD_REQUEST).build();
            default -> problem;
        };
    }
}
