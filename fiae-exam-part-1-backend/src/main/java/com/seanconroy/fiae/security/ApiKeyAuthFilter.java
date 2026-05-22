package com.seanconroy.fiae.security;

import com.seanconroy.fiae.entity.WhitelistUser;
import com.seanconroy.fiae.service.AuthContext;
import com.seanconroy.fiae.service.WhitelistService;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;


import java.util.Optional;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class ApiKeyAuthFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(ApiKeyAuthFilter.class);

    @Inject
    WhitelistService whitelistService;

    @Inject
    AuthContext authContext;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();

        if (!path.startsWith("api/")) {
            return;
        }

        if (isPublicPath(path)) {
            return;
        }

        String apiKey = requestContext.getHeaderString("X-API-KEY");

        Optional<WhitelistUser> resolvedUser = whitelistService.resolveUserFromApiKey(apiKey);

        if (resolvedUser.isEmpty()) {
            LOG.warnf("Rejected request to /%s beacause API key is missing, invalid, or inactive", path);

            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity("{\"message\":\"Missing or invalid API key\",\"status\":403}")
                            .build());
            return;
        }
        authContext.setCurrentUser(resolvedUser.get());
    }
    private boolean isPublicPath(String path) {
        return path.startsWith("api/cards/")
        || path.startsWith("api/assets/")
        || path.startsWith("api/health")
        || path.startsWith("api/admin/");
    }
}
