package com.blueprint.centromere.ws.documentation;

import com.blueprint.centromere.core.exceptions.ModelRegistryException;
import com.blueprint.centromere.core.model.Model;
import com.blueprint.centromere.ws.config.ModelResourceRegistry;
import com.fasterxml.classmate.TypeResolver;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingBuilderPlugin;
import springfox.documentation.spi.service.contexts.ApiListingContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * @author woemler
 * @since 0.4.1
 */
public class ModelApiListingPlugin implements ApiListingBuilderPlugin {

    private final ModelResourceRegistry registry;
    private final TypeResolver typeResolver;

    @Value("${centromere.web.api.root-url}")
    private String rootUrl;

    public ModelApiListingPlugin(ModelResourceRegistry registry, TypeResolver typeResolver) {
        this.registry = registry;
        this.typeResolver = typeResolver;
    }

    @Override
    public void apply(ApiListingContext apiListingContext) {
        apiListingContext.apiListingBuilder().apis(getApiDescriptions());

    }

    /**
     * Iterates through each {@link Model} registered with a {@link ModelResourceRegistry} instance
     * and generates a collection of {@link ApiDescription} objects, which describe the available
     * resource endpoints.
     */
    protected List<ApiDescription> getApiDescriptions() {
        Assert.notNull(registry, "ModelRegistry must not be null.");
        List<ApiDescription> descriptions = new ArrayList<>();
        for (Class<? extends Model<?>> model : registry.getRegisteredModels()) {
            if (registry.isRegisteredModel(model)) {
                try {
                    descriptions.addAll(createModelSearchDescriptions(model));
                    descriptions.addAll(createModelAggregationDescriptions(model));
                } catch (ModelRegistryException e) {
                    e.printStackTrace();
                }
            }
        }
        return descriptions;
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return SwaggerPluginSupport.pluginDoesApply(documentationType);
    }

    /**
     * Generates search endpoint descriptions for the given model.
     */
    private List<ApiDescription> createModelSearchDescriptions(Class<? extends Model<?>> model)
        throws ModelRegistryException {
        List<ApiDescription> descriptions = new ArrayList<>();
        descriptions.add(
            new ApiDescription(
                rootUrl + "/search/" + registry.getUriByModel(model) + "/{id}",
                model.getSimpleName() + " single resource operations",
                ModelResourceOperationsPluginUtil.findSingleModelOperations(model, typeResolver),
                false));
        descriptions.add(
            new ApiDescription(
                rootUrl + "/search/" + registry.getUriByModel(model),
                model.getSimpleName() + " collection resource operations",
                ModelResourceOperationsPluginUtil
                    .findCollectionModelOperations(model, typeResolver),
                false));
        //TODO: add other search endpoints programatically
        return descriptions;
    }

    /**
     * Generates aggregation endpoint descriptions for the given model.
     */
    private List<ApiDescription> createModelAggregationDescriptions(
        Class<? extends Model<?>> model) throws ModelRegistryException {
        List<ApiDescription> descriptions = new ArrayList<>();
        descriptions.add(
            new ApiDescription(
                rootUrl + "/aggregate/" + registry.getUriByModel(model) + "/distinct/{field}",
                model.getSimpleName() + " distinct field value operation",
                ModelResourceOperationsPluginUtil.findDistinctOperations(model, typeResolver),
                false));
        descriptions.add(
            new ApiDescription(
                rootUrl + "/aggregate/" + registry.getUriByModel(model) + "/count",
                model.getSimpleName() + " record count operation",
                ModelResourceOperationsPluginUtil.countOperations(model, typeResolver),
                false));
        descriptions.add(
            new ApiDescription(
                rootUrl + "/aggregate/" + registry.getUriByModel(model) + "/group/{field}",
                model.getSimpleName() + " group operation",
                ModelResourceOperationsPluginUtil.findGroupedOperations(model, typeResolver),
                false));
        //TODO: add other search endpoints programatically
        return descriptions;
    }

}
