package info.touret.musicstore.application;

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
            case IllegalArgumentException _ ->
                    HttpProblem.builder().withDetail(problem.getDetail()).withStatus(Response.Status.BAD_REQUEST).build();
            default -> problem;
        };
    }
}
