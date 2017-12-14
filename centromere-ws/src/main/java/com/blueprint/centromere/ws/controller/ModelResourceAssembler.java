package com.blueprint.centromere.ws.controller;

/**
 * @author woemler
 */

import com.blueprint.centromere.core.config.ModelRepositoryRegistry;
import com.blueprint.centromere.core.model.Linked;
import com.blueprint.centromere.core.model.Model;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

/**
 * Extension of Spring HATEOAS's {@link ResourceAssemblerSupport}, which automatically builds self
 *   links, based upon the {@link Model} class's `getId()` method signature, and by inferring
 *   related models by fields annotated with {@link com.blueprint.centromere.core.model.Linked}.
 *
 * @author woemler
 */
public class ModelResourceAssembler
    extends ResourceAssemblerSupport<Model, FilterableResource> {

  @SuppressWarnings("SpringJavaAutowiringInspection")
  private final ModelRepositoryRegistry registry;

  @Value("${centromere.web.api.root-url}")
  private String rootUrl;

  public ModelResourceAssembler(ModelRepositoryRegistry registry){
    super(ModelCrudController.class, FilterableResource.class);
    this.registry = registry;
  }

  private String getModelUri(Class<? extends Model> model){
    return rootUrl + "/" + registry.getModelUri(model);
  }

  /**
   * Converts a {@link Model} object into a {@link FilterableResource}, adding the appropriate links.
   *
   * @param t
   * @return
   */
  public FilterableResource toResource(Model t) {
    FilterableResource<Model> resource = new FilterableResource<>(t);
    resource.add(new Link(getModelUri(t.getClass()) + "/" + t.getId(), "self"));
    List<Link> links = addLinks(new ArrayList<>());
    links.addAll(this.addLinkedModelLinks(t));
    resource.add(links);
    return resource;
  }

  /**
   * Inspects the target {@link Model} class for {@link com.blueprint.centromere.core.model.Linked} annotations, and creates links
   *   based upon the inferred relationship and field names.
   *
   * @param t
   * @return
   */
  private List<Link> addLinkedModelLinks(Model t){
    List<Link> links = new ArrayList<>();
    Class<?> current = t.getClass();
    while (current.getSuperclass() != null) {
      for (Field field : current.getDeclaredFields()) {
        if (field.isAnnotationPresent(Linked.class)) {
          Linked linked = field.getAnnotation(Linked.class);
          String relName = linked.rel().equals("") ? field.getName() : linked.rel();
          String fieldName = linked.field().equals("") ? field.getName() : linked.field();
          if (!Model.class.isAssignableFrom(linked.model()))
            continue;
          Class<? extends Model<?>> fkClass = (Class<? extends Model<?>>) linked.model();
          Link link = null;
          try {
            field.setAccessible(true);
            if (!field.getType().isArray() && !Collection.class.isAssignableFrom(field.getType())) {
              link = new Link(getModelUri(fkClass) + "/" + field.get(t), relName);
            } else if (getRelatedModelForeignKeyId(current, fkClass) != null
                && (field.getType().isArray() || Collection.class
                .isAssignableFrom(field.getType()))) {
              link = new Link(getModelUri(fkClass) + "?"
                  + getRelatedModelForeignKeyId(current, fkClass) + "=" + t.getId(), relName);
            } else {
              link = new Link(getModelUri(fkClass) + "?" + fieldName + "="
                  + collectionToString(field.get(t)), relName);
            }
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
          if (link != null)
            links.add(link);
        }
      }
      current = current.getSuperclass();
    }
    return links;
  }

  private String getRelatedModelForeignKeyId(Class<?> source, Class<?> relation){
    Class<?> current = relation;
    while (current.getSuperclass() != null) {
      for (Field field : current.getDeclaredFields()) {
        if (field.isAnnotationPresent(Linked.class)) {
          Linked linked = field.getAnnotation(Linked.class);
          if (source.equals(linked.model()))
            return field.getName();
        }
      }
      current = current.getSuperclass();
    }
    return null;
  }

  private String collectionToString(Object object){
    Collection<Object> collection;
    if (Collection.class.isAssignableFrom(object.getClass())){
      collection = (Collection<Object>) object;
    } else if (object.getClass().isArray()){
      collection = Arrays.asList(object);
    } else {
      collection = Collections.singleton(object);
    }
    String s = "";
    boolean flag = false;
    for (Object o: collection){
      if (flag) s = s + ",";
      s = s + o.toString();
      flag = true;
    }
    return s;
  }

  private List<Link> addLinks(List<Link> links){
    return links;
  }

}
