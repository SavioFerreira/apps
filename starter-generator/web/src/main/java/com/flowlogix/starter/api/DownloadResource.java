/*
 * Copyright (C) 2011-2026 Flow Logix, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flowlogix.starter.api;

import com.flowlogix.starter.ArchetypeGenerator;
import com.flowlogix.starter.ArchetypeGenerator.Parameter;
import com.flowlogix.starter.ArchetypeGenerator.ReturnValue;
import com.flowlogix.starter.common.ArtifactId;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import lombok.extern.slf4j.Slf4j;
import static com.flowlogix.starter.common.StarterParameters.ARCHETYPE_VERSION;
import static com.flowlogix.starter.common.StarterParameters.ARTIFACT;
import static com.flowlogix.starter.common.StarterParameters.BASE_TYPE;
import static com.flowlogix.starter.common.StarterParameters.GROUP;
import static com.flowlogix.starter.common.StarterParameters.PACKAGE;
import static com.flowlogix.starter.common.StarterParameters.PACKAGING_TYPE;
import static com.flowlogix.starter.common.StarterParameters.PROJECT_NAME;
import static com.flowlogix.starter.common.StarterParameters.USE_AGENTIC_AI;
import static com.flowlogix.starter.common.StarterParameters.USE_ARQUILLIAN_GRAPHENE;
import static com.flowlogix.starter.common.StarterParameters.USE_CODE_COVERAGE;
import static com.flowlogix.starter.common.StarterParameters.USE_LAZY_MODEL;
import static com.flowlogix.starter.common.StarterParameters.USE_MAVEN_CACHE;
import static com.flowlogix.starter.common.StarterParameters.USE_OMNIFACES;
import static com.flowlogix.starter.common.StarterParameters.USE_PRIMEFACES;
import static com.flowlogix.starter.common.StarterParameters.USE_SHIRO;
import static com.flowlogix.starter.common.StarterParameters.VERSION;

@Slf4j
@Path("/")
public class DownloadResource {
    @Inject
    ArchetypeGenerator generator;

    @GET
    @Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN})
    @SuppressWarnings("checkstyle:ParameterNumber")
    public Response downloadFile(@MatrixParam(GROUP) @DefaultValue("com.example") String groupId,
                                 @Pattern(regexp = ArtifactId.PATTERN,
                                          message = "Artifact ID can only contain letters, numbers, underscores, and hyphens")
                                 @Size(min = 1, max = ArtifactId.MAX_LENGTH,
                                       message = "Artifact ID must be between 1 and " + ArtifactId.MAX_LENGTH + " characters")
                                 @MatrixParam(ARTIFACT) @DefaultValue("starter") String artifactId,
                                 @MatrixParam(PROJECT_NAME) String projectName,
                                 @MatrixParam(PACKAGE) String packageName,
                                 @MatrixParam(BASE_TYPE) String baseType,
                                 @MatrixParam(PACKAGING_TYPE) String packagingType,
                                 @MatrixParam(VERSION) String version,
                                 @MatrixParam(ARCHETYPE_VERSION) String archetypeVersion,
                                 @MatrixParam(USE_SHIRO) @DefaultValue("false") boolean useShiro,
                                 @MatrixParam(USE_AGENTIC_AI) @DefaultValue("false") boolean useAgenticAI,
                                 @MatrixParam(USE_OMNIFACES) @DefaultValue("false") boolean useOmniFaces,
                                 @MatrixParam(USE_PRIMEFACES) @DefaultValue("false") boolean usePrimeFaces,
                                 @MatrixParam(USE_LAZY_MODEL) @DefaultValue("false") boolean useLazyModel,
                                 @MatrixParam(USE_MAVEN_CACHE) @DefaultValue("false") boolean useMavenCache,
                                 @MatrixParam(USE_CODE_COVERAGE) @DefaultValue("false") boolean useCodeCoverage,
                                 @MatrixParam(USE_ARQUILLIAN_GRAPHENE) @DefaultValue("false") boolean useArquillianGraphene) {
        ReturnValue result = generator.generateArchetype(new Parameter[] {
                new Parameter("groupId", groupId),
                new Parameter("artifactId", artifactId),
                new Parameter("projectName", projectName),
                new Parameter("package", packageName),
                new Parameter("baseType", baseType),
                new Parameter("packagingType", packagingType),
                new Parameter("version", version),
                new Parameter("archetypeVersion", archetypeVersion),
                new Parameter("useShiro", Boolean.toString(useShiro)),
                new Parameter("useAgenticAI", Boolean.toString(useAgenticAI)),
                new Parameter("useOmniFaces", Boolean.toString(useOmniFaces)),
                new Parameter("usePrimeFaces", Boolean.toString(usePrimeFaces)),
                new Parameter("useLazyModel", Boolean.toString(useLazyModel)),
                new Parameter("useMavenCache", Boolean.toString(useMavenCache)),
                new Parameter("useCodeCoverage", Boolean.toString(useCodeCoverage)),
                new Parameter("useArquillianGraphene", Boolean.toString(useArquillianGraphene))
        });

        if (result.status() != 0) {
            result.close();
            return Response.serverError().type(MediaType.TEXT_PLAIN)
                    .entity(result.output()).build();
        }

        StreamingOutput stream = outputStream -> generator.createZipStream(result, outputStream);
        return Response.ok(stream)
                .header("Content-Disposition", "attachment; filename=\"%s.zip\"".formatted(artifactId))
                .build();
    }
}
