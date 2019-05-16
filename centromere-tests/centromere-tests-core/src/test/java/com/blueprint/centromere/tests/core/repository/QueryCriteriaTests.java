package com.blueprint.centromere.tests.core.repository;

import com.blueprint.centromere.core.exceptions.QueryParameterException;
import com.blueprint.centromere.core.repository.Evaluation;
import com.blueprint.centromere.core.repository.QueryCriteria;
import com.blueprint.centromere.core.repository.QueryParameterDescriptor;
import com.blueprint.centromere.core.repository.QueryParameterUtil;
import com.blueprint.centromere.tests.core.TestGene;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author woemler
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class QueryCriteriaTests {

    @Test
    public void criteriaTest() {
        List<QueryCriteria> criterias = Arrays.asList(
            new QueryCriteria("key", "value"),
            new QueryCriteria("num", 3, Evaluation.GREATER_THAN_EQUALS)
        );
        Assert.assertNotNull(criterias);
        Assert.assertTrue(!criterias.isEmpty());
        Assert.assertTrue(criterias.size() == 2);
        QueryCriteria criteria = criterias.get(0);
        Assert.assertTrue("key".equals(criteria.getKey()));
        Assert.assertTrue("value".equals(criteria.getValue()));
        Assert.assertTrue(Evaluation.EQUALS.equals(criteria.getEvaluation()));
        criteria = criterias.get(1);
        Assert.assertNotNull(criteria);
        Assert.assertTrue("num".equals(criteria.getKey()));
        Assert.assertTrue((int) criteria.getValue() == 3);
        Assert.assertTrue(Evaluation.GREATER_THAN_EQUALS.equals(criteria.getEvaluation()));
    }

    @Test
    public void standardQueryParameterDescriptorTest() {
        QueryParameterDescriptor descriptor = new QueryParameterDescriptor();
        descriptor.setParamName("param");
        descriptor.setFieldName("field");
        descriptor.setType(Integer.class);
        descriptor.setEvaluation(Evaluation.GREATER_THAN);
        QueryCriteria criteria = descriptor.createQueryCriteria(4);
        Assert.assertNotNull(criteria);
        Assert.assertTrue("field".equals(criteria.getKey()));
        Assert.assertTrue((int) criteria.getValue() == 4);
        Assert.assertTrue(Evaluation.GREATER_THAN.equals(criteria.getEvaluation()));
    }

    @Test
    public void regexQueryParameterDescriptorTest() {
        QueryParameterDescriptor descriptor = new QueryParameterDescriptor();
        descriptor.setParamName("attributes.\\w+");
        descriptor.setEvaluation(Evaluation.EQUALS);
        descriptor.setRegexMatch(true);
        Assert.assertTrue(descriptor.parameterNameMatches("attributes.isKinase"));
        Assert.assertEquals(Evaluation.EQUALS,
            descriptor.getDynamicEvaluation("attributes.isKinase"));
        Assert.assertTrue(!descriptor.parameterNameMatches("attributes"));
        Assert.assertTrue(descriptor.parameterNameMatches("attributes.nameIsNull"));
        Assert.assertTrue(
            Evaluation.EQUALS.equals(descriptor.getDynamicEvaluation("attributes.nameIsNull")));
    }

    @Test
    public void dynamicQueryParameterDescriptorTest() {

        QueryParameterDescriptor descriptor = new QueryParameterDescriptor();
        descriptor.setEvaluation(Evaluation.EQUALS);
        descriptor.setParamName("value");
        descriptor.setDynaimicParameters(true);
        descriptor.setFieldName("value");
        descriptor.setType(Double.class);

        Assert.assertTrue(descriptor.parameterNameMatches("value"));
        Assert.assertEquals(Evaluation.EQUALS, descriptor.getDynamicEvaluation("value"));
        Assert.assertTrue(!descriptor.parameterNameMatches("values"));
        Assert.assertTrue(descriptor.parameterNameMatches("valueGreaterThan"));
        Assert.assertEquals(Evaluation.GREATER_THAN,
            descriptor.getDynamicEvaluation("valueGreaterThan"));
        Assert.assertTrue(descriptor.parameterNameMatches("valueBetweenIncluding"));
        Assert.assertEquals(Evaluation.BETWEEN_INCLUSIVE,
            descriptor.getDynamicEvaluation("valueBetweenIncluding"));

        QueryCriteria criteria = descriptor.createQueryCriteria(1.23);
        Assert.assertEquals("value", criteria.getKey());
        Assert.assertEquals(criteria.getValue(), 1.23);
        Assert.assertEquals(Evaluation.EQUALS, criteria.getEvaluation());

        criteria = descriptor.createQueryCriteria("valueGreaterThan", 1.23);
        Assert.assertEquals("value", criteria.getKey());
        Assert.assertEquals(criteria.getValue(), 1.23);
        Assert.assertEquals(Evaluation.GREATER_THAN, criteria.getEvaluation());

        Exception exception = null;
        try {
            descriptor.createQueryCriteria("valueBadSuffix", 1.23);
        } catch (Exception e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
        Assert.assertTrue(exception instanceof QueryParameterException);

    }

    @Test
    public void modelToDescriptorTest() {
        Map<String, QueryParameterDescriptor> descriptorMap
            = QueryParameterUtil.getAvailableQueryParameters(TestGene.class);
        for (Map.Entry entry : descriptorMap.entrySet()) {
            System.out.println(String.format("param: %s   descriptor: %s", entry.getKey(),
                entry.getValue()));
        }
        Assert.assertNotNull(descriptorMap);
        Assert.assertTrue(!descriptorMap.isEmpty());
        Assert.assertEquals(descriptorMap.size(), 9);
        Assert.assertTrue(descriptorMap.containsKey("attributes.\\w+"));
        Assert.assertTrue(!descriptorMap.containsKey("attributes"));
        QueryParameterDescriptor descriptor = descriptorMap.get("entrezGeneId");
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(descriptor.getType(), Integer.class);
        descriptor = descriptorMap.get("symbol");
        Assert.assertNotNull(descriptor);
        Assert.assertEquals("symbol", descriptor.getParamName());
        Assert.assertEquals("symbol", descriptor.getFieldName());
        Assert.assertEquals(Evaluation.EQUALS, descriptor.getEvaluation());
        Assert.assertTrue(!descriptorMap.containsKey("geneSymbol"));
    }

    @Test
    public void parameterToCriteriaTest() {

        QueryCriteria criteria = QueryParameterUtil
            .getQueryCriteriaFromParameter("name", new Object[]{"Will"}, String.class,
                Evaluation.EQUALS);
        Assert.assertNotNull(criteria);
        Assert.assertNotNull(criteria.getKey());
        Assert.assertEquals("name", criteria.getKey());
        Assert.assertNotNull(criteria.getValue());
        Assert.assertEquals("Will", criteria.getValue());
        Assert.assertNotNull(criteria.getEvaluation());
        Assert.assertEquals(Evaluation.EQUALS, criteria.getEvaluation());

        criteria = QueryParameterUtil
            .getQueryCriteriaFromParameter("name", new Object[]{"Will", "Joe"}, String.class,
                Evaluation.EQUALS);
        Assert.assertNotNull(criteria);
        Assert.assertNotNull(criteria.getKey());
        Assert.assertEquals("name", criteria.getKey());
        Assert.assertNotNull(criteria.getValue());
        Assert.assertTrue(criteria.getValue() instanceof List);
        Assert.assertTrue(!((List<String>) criteria.getValue()).isEmpty());
        Assert.assertEquals("Will", ((List<String>) criteria.getValue()).get(0));
        Assert.assertNotNull(criteria.getEvaluation());
        Assert.assertEquals(Evaluation.IN, criteria.getEvaluation());

        criteria = QueryParameterUtil
            .getQueryCriteriaFromParameter("value", new Object[]{"1.2", "3.1"}, Double.class,
                Evaluation.BETWEEN);
        Assert.assertNotNull(criteria);
        Assert.assertNotNull(criteria.getKey());
        Assert.assertEquals("value", criteria.getKey());
        Assert.assertNotNull(criteria.getValue());
        Assert.assertTrue(criteria.getValue() instanceof List);
        Assert.assertTrue(!((List<Double>) criteria.getValue()).isEmpty());
        Assert.assertEquals(((List<Double>) criteria.getValue()).get(0), Double.valueOf(1.2));
        Assert.assertNotNull(criteria.getEvaluation());
        Assert.assertEquals(Evaluation.BETWEEN, criteria.getEvaluation());

    }

}
