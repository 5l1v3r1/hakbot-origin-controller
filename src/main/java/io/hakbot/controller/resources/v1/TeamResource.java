/*
 * This file is part of Hakbot Origin Controller.
 *
 * Hakbot Origin Controller is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Hakbot Origin Controller is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Hakbot Origin Controller. If not, see http://www.gnu.org/licenses/.
 */
package io.hakbot.controller.resources.v1;

import alpine.model.ApiKey;
import io.hakbot.controller.model.Team;
import io.hakbot.controller.persistence.QueryManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/v1/team")
@Api(value = "team")
public class TeamResource extends BaseResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Returns a list of all teams",
            notes = "Requires hakmaster permission.",
            response = Team.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    public Response getTeams() {
        if (!isHakmaster()) {
            Response.status(Response.Status.UNAUTHORIZED);
        }
        try (QueryManager qm = new QueryManager()) {
            List<Team> teams = qm.getHakbotTeams();
            return Response.ok(teams).build();
        }
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Returns a specific team",
            notes = "Requires hakmaster permission.",
            response = Team.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "The user could not be found")
    })
    public Response getTeam(
            @ApiParam(value = "The UUID of the team to retrieve", required = true)
            @PathParam("uuid") String uuid) {
        if (!isHakmaster()) {
            Response.status(Response.Status.UNAUTHORIZED);
        }
        try (QueryManager qm = new QueryManager()) {
            Team team = qm.getObjectByUuid(Team.class, uuid);
            if (team != null) {
                return Response.ok(team).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("The user could not be found.").build();
            }
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Creates a new team along with an associated API key",
            notes = "Requires hakmaster permission.",
            response = Team.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    public Response createTeam(Team jsonTeam) {
        if (!isHakmaster()) {
            Response.status(Response.Status.UNAUTHORIZED);
        }
        try (QueryManager qm = new QueryManager()) {
            Team team = qm.createTeam(jsonTeam.getName(), false, true);
            return Response.status(Response.Status.CREATED).entity(team).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Updates a team's fields including name and hakmaster",
            notes = "Requires hakmaster permission.",
            response = Team.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "The UUID of the team could not be found")
    })
    public Response updateTeam(Team jsonTeam) {
        if (!isHakmaster()) {
            Response.status(Response.Status.UNAUTHORIZED);
        }
        try (QueryManager qm = new QueryManager()) {
            Team team = qm.getObjectByUuid(Team.class, jsonTeam.getUuid());
            if (team != null) {
                team.setName(jsonTeam.getName());
                team.setHakmaster(jsonTeam.isHakmaster());
                team = qm.updateTeam(jsonTeam);
                return Response.ok(team).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("The UUID of the team could not be found.").build();
            }
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Deletes a team",
            notes = "Requires hakmaster permission."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "The UUID of the team could not be found")
    })
    public Response deleteTeam(Team jsonTeam) {
        if (!isHakmaster()) {
            Response.status(Response.Status.UNAUTHORIZED);
        }
        try (QueryManager qm = new QueryManager()) {
            Team team = qm.getObjectByUuid(Team.class, jsonTeam.getUuid(), Team.FetchGroup.ALL.getName());
            if (team != null) {
                qm.delete(team.getApiKeys());
                qm.delete(team);
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("The UUID of the team could not be found.").build();
            }
        }
    }

    @PUT
    @Path("/{uuid}/key")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Generates an API key and returns its value",
            notes = "Requires hakmaster permission.",
            response = ApiKey.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "The UUID of the team could not be found")
    })
    public Response generateApiKey(
            @ApiParam(value = "The UUID of the team to generate a key for", required = true)
            @PathParam("uuid") String uuid) {
        if (!isHakmaster()) {
            Response.status(Response.Status.UNAUTHORIZED);
        }
        try (QueryManager qm = new QueryManager()) {
            Team team = qm.getObjectByUuid(Team.class, uuid);
            if (team != null) {
                ApiKey apiKey = qm.createApiKey(team);
                return Response.status(Response.Status.CREATED).entity(apiKey).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("The UUID of the team could not be found.").build();
            }
        }
    }

    @POST
    @Path("/key/{apikey}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Regenerates an API key by removing the specified key, generating a new one and returning its value",
            notes = "Requires hakmaster permission.",
            response = ApiKey.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "The API key could not be found")
    })
    public Response regenerateApiKey(
            @ApiParam(value = "The API key to regenerate", required = true)
            @PathParam("apikey") String apikey) {
        if (!isHakmaster()) {
            Response.status(Response.Status.UNAUTHORIZED);
        }
        try (QueryManager qm = new QueryManager()) {
            ApiKey apiKey = qm.getApiKey(apikey);
            if (apiKey != null) {
                apiKey = qm.regenerateApiKey(apiKey);
                return Response.ok(apiKey).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("The API key could not be found.").build();
            }
        }
    }

    @DELETE
    @Path("/key/{apikey}")
    @ApiOperation(
            value = "Deletes the specified API key",
            notes = "Requires hakmaster permission."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "The API key could not be found")
    })
    public Response deleteApiKey(
            @ApiParam(value = "The API key to delete", required = true)
            @PathParam("apikey") String apikey) {
        if (!isHakmaster()) {
            Response.status(Response.Status.UNAUTHORIZED);
        }
        try (QueryManager qm = new QueryManager()) {
            ApiKey apiKey = qm.getApiKey(apikey);
            if (apiKey != null) {
                qm.delete(apiKey);
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("The API key could not be found.").build();
            }
        }
    }

}
