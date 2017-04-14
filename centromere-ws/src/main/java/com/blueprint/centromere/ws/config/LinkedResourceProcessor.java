package com.blueprint.centromere.ws.config;

import com.blueprint.centromere.core.model.Linked;
import com.blueprint.centromere.core.model.Model;
import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

/**
 * Uses the {@link Linked} annotation to create hypermedia links to related resources in {@link Model}
 *   resources.
 * 
 * @author woemler
 */
public class LinkedResourceProcessor<T extends Model<?>> implements ResourceProcessor<Resource<T>> {

  private EntityLinks entityLinks;
  
  @Override
  public Resource<T> process(Resource<T> resource) {
    T entity = resource.getContent();
    List<Link> links = new ArrayList<>();
    for (Field field: entity.getClass().getDeclaredFields()){
      if (field.isAnnotationPresent(Linked.class)){
        Linked annotation = field.getAnnotation(Linked.class);
        Class<?> model = annotation.model();
        field.setAccessible(true);
        Object id;
        Link link;
        String relName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, model.getSimpleName());
        try {
          if (Collection.class.isAssignableFrom(field.getType())){
            id = Joiner.on(",").skipNulls().join((Collection) field.get(entity));
            link = new Link(entityLinks.linkFor(model).toString() + "?" + annotation.field() 
                + "=" + id, relName);
          } else {
            id = field.get(entity);
            link = entityLinks.linkFor(model).slash(id.toString())
                .withRel(relName);
          }
        } catch (Exception e){
          throw new RuntimeException(e);
        }
        if (link != null) links.add(link);
      }
    }
    resource.add(links);
    return resource;
  }

  @Autowired
  public void setEntityLinks(EntityLinks entityLinks) {
    this.entityLinks = entityLinks;
  }
}
