package com.blueprint.centromere.tests.core.test.repository;

import com.blueprint.centromere.core.commons.model.Gene;
import com.blueprint.centromere.core.repository.Evaluation;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.core.repository.QueryParameterDescriptor;
import com.blueprint.centromere.core.repository.QueryParameterUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class QueryCriteriaTests {

  @Test
  public void criteriaTest(){
    List<QueryCriteria> criterias = Arrays.asList(
        new QueryCriteria("key", "value"),
        new QueryCriteria("num", 3, Evaluation.GREATER_THAN_EQUALS)
    );
    Assert.notNull(criterias);
    Assert.notEmpty(criterias);
    Assert.isTrue(criterias.size() == 2);
    QueryCriteria criteria = criterias.get(0);
    Assert.isTrue("key".equals(criteria.getKey()));
    Assert.isTrue("value".equals(criteria.getValue()));
    Assert.isTrue(Evaluation.EQUALS.equals(criteria.getEvaluation()));
    criteria = criterias.get(1);
    Assert.notNull(criteria);
    Assert.isTrue("num".equals(criteria.getKey()));
    Assert.isTrue((int) criteria.getValue() == 3);
    Assert.isTrue(Evaluation.GREATER_THAN_EQUALS.equals(criteria.getEvaluation()));
  }

  @Test
  public void standardQueryParameterDescriptorTest(){
    QueryParameterDescriptor descriptor = new QueryParameterDescriptor();
    descriptor.setParamName("param");
    descriptor.setFieldName("field");
    descriptor.setType(Integer.class);
    descriptor.setEvaluation(Evaluation.GREATER_THAN);
    QueryCriteria criteria = descriptor.createQueryCriteria(4);
    Assert.notNull(criteria);
    Assert.isTrue("field".equals(criteria.getKey()));
    Assert.isTrue((int) criteria.getValue() == 4);
    Assert.isTrue(Evaluation.GREATER_THAN.equals(criteria.getEvaluation()));
  }

  @Test
  public void regexQueryParameterDescriptorTest(){
    QueryParameterDescriptor descriptor = new QueryParameterDescriptor();
    descriptor.setParamName("attributes.\\w+");
    descriptor.setEvaluation(Evaluation.EQUALS);
    descriptor.setRegexMatch(true);
    Assert.isTrue(descriptor.parameterNameMatches("attributes.isKinase"));
    Assert.isTrue(Evaluation.EQUALS.equals(descriptor.getDynamicEvaluation("attributes.isKinase")),
        String.format("Expected EQUALS, got %s", descriptor.getDynamicEvaluation("attributes.isKinase").toString()));
    Assert.isTrue(!descriptor.parameterNameMatches("attributes"));
    Assert.isTrue(descriptor.parameterNameMatches("attributes.nameIsNull"));
    Assert.isTrue(Evaluation.EQUALS.equals(descriptor.getDynamicEvaluation("attributes.nameIsNull")));
  }

  @Test
  public void dynamicQueryParameterDescriptorTest(){
    QueryParameterDescriptor descriptor = new QueryParameterDescriptor();
    descriptor.setEvaluation(Evaluation.EQUALS);
    descriptor.setParamName("value");
    descriptor.setDynaimicParameters(true);
    descriptor.setFieldName("value");
    descriptor.setType(Double.class);
    Assert.isTrue(descriptor.parameterNameMatches("value"));
    Assert.isTrue(Evaluation.EQUALS.equals(descriptor.getDynamicEvaluation("value")));
    Assert.isTrue(!descriptor.parameterNameMatches("values"));
    Assert.isTrue(descriptor.parameterNameMatches("valueGreaterThan"));
    Assert.isTrue(Evaluation.GREATER_THAN.equals(descriptor.getDynamicEvaluation("valueGreaterThan")),
        String.format("Expected GREATER_THAN, was %s", descriptor.getDynamicEvaluation("valueGreaterThan").toString()));
    Assert.isTrue(descriptor.parameterNameMatches("valueBetweenIncluding"));
    Assert.isTrue(Evaluation.BETWEEN_INCLUSIVE.equals(descriptor.getDynamicEvaluation("valueBetweenIncluding")),
        String.format("Expected BETWEEN_INCLUSIVE, was %s", descriptor.getDynamicEvaluation("valueBetweenIncluding").toString()));
  }

  @Test
  public void modelToDescriptorTest(){
    Map<String,QueryParameterDescriptor> descriptorMap
        = QueryParameterUtil.getAvailableQueryParameters(Gene.class);
    for (Map.Entry entry: descriptorMap.entrySet()){
      System.out.println(String.format("param: %s   descriptor: %s", entry.getKey(),
          (entry.getValue()).toString()));
    }
    Assert.notNull(descriptorMap);
    Assert.notEmpty(descriptorMap);
    Assert.isTrue(descriptorMap.size() == 12, String.format("Size is actually %s", descriptorMap.size()));
    Assert.isTrue(descriptorMap.containsKey("attributes.\\w+"));
    Assert.isTrue(!descriptorMap.containsKey("attributes"));
    QueryParameterDescriptor descriptor = descriptorMap.get("primaryReferenceId");
    Assert.notNull(descriptor);
    Assert.isTrue(descriptor.getType().equals(String.class));
    descriptor = descriptorMap.get("primaryGeneSymbol");
    Assert.notNull(descriptor);
    Assert.isTrue("primaryGeneSymbol".equals(descriptor.getParamName()));
    Assert.isTrue("primaryGeneSymbol".equals(descriptor.getFieldName()));
    Assert.isTrue(Evaluation.EQUALS.equals(descriptor.getEvaluation()));
    Assert.isTrue(!descriptorMap.containsKey("symbol"));
  }

  @Test
  public void parameterToCriteriaTest(){

    QueryCriteria criteria = QueryParameterUtil.getQueryCriteriaFromParameter("name", new Object[]{"Will"}, String.class, Evaluation.EQUALS);
    Assert.notNull(criteria);
    Assert.notNull(criteria.getKey());
    Assert.isTrue("name".equals(criteria.getKey()));
    Assert.notNull(criteria.getValue());
    Assert.isTrue("Will".equals(criteria.getValue()));
    Assert.notNull(criteria.getEvaluation());
    Assert.isTrue(Evaluation.EQUALS.equals(criteria.getEvaluation()));

    criteria = QueryParameterUtil.getQueryCriteriaFromParameter("name", new Object[]{"Will", "Joe"}, String.class, Evaluation.EQUALS);
    Assert.notNull(criteria);
    Assert.notNull(criteria.getKey());
    Assert.isTrue("name".equals(criteria.getKey()));
    Assert.notNull(criteria.getValue());
    Assert.isTrue(criteria.getValue() instanceof List);
    Assert.notEmpty((List<String>) criteria.getValue());
    Assert.isTrue("Will".equals((((List<String>) criteria.getValue()).get(0))));
    Assert.notNull(criteria.getEvaluation());
    Assert.isTrue(Evaluation.IN.equals(criteria.getEvaluation()));

    criteria = QueryParameterUtil.getQueryCriteriaFromParameter("value", new Object[]{"1.2", "3.1"}, Double.class, Evaluation.BETWEEN);
    Assert.notNull(criteria);
    Assert.notNull(criteria.getKey());
    Assert.isTrue("value".equals(criteria.getKey()));
    Assert.notNull(criteria.getValue());
    Assert.isTrue(criteria.getValue() instanceof List);
    Assert.notEmpty((List<Double>) criteria.getValue());
    Assert.isTrue((((List<Double>) criteria.getValue()).get(0) == 1.2));
    Assert.notNull(criteria.getEvaluation());
    Assert.isTrue(Evaluation.BETWEEN.equals(criteria.getEvaluation()));

  }

}
