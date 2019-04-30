package com.blueprint.centromere.tests.core.etl;

import com.blueprint.centromere.core.etl.processor.DataProcessor;
import com.blueprint.centromere.core.etl.processor.ModelProcessorBeanRegistry;
import com.blueprint.centromere.tests.core.TestGene;
import com.blueprint.centromere.tests.core.models.Sample;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author woemler
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ETLConfig.class})
public class ProcessorTests {

    @Autowired(required = false)
    private ModelProcessorBeanRegistry processorBeanRegistry;
    @Autowired(required = false)
    private GeneInfoProcessor geneInfoProcessor;

    @Test
    public void configTest() throws Exception {
        Assert.assertNotNull(processorBeanRegistry);
        Assert.assertNotNull(geneInfoProcessor);
    }

    @Test
    public void processorRegistryTest() throws Exception {
        Assert.assertTrue(processorBeanRegistry.isRegisteredModel(TestGene.class));
        Assert.assertTrue(!processorBeanRegistry.isRegisteredModel(Sample.class));
        Assert.assertTrue(processorBeanRegistry.isRegisteredDataType("gene_info"));
        Assert.assertTrue(!processorBeanRegistry.isRegisteredDataType("sample_info"));
        DataProcessor processor = processorBeanRegistry.getProcessorByDataType("gene_info");
        Assert.assertNotNull(processor);
        Assert.assertTrue(processor instanceof GeneInfoProcessor);
        GeneInfoProcessor geneProcessor = (GeneInfoProcessor) processor;
        Assert.assertEquals(geneProcessor.getModel(), TestGene.class);
    }

}
