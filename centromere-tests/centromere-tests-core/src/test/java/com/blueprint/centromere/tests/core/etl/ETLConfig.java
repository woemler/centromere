package com.blueprint.centromere.tests.core.etl;

import com.blueprint.centromere.core.etl.processor.DataProcessor;
import com.blueprint.centromere.core.etl.processor.ModelProcessorBeanRegistry;
import com.blueprint.centromere.tests.core.TestGene;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author woemler
 */
@Configuration
public class ETLConfig {

    @Bean
    public GeneInfoProcessor<TestGene, String> geneInfoProcessor() {
        return new GeneInfoProcessor<>(
            TestGene.class, new
            GeneInfoReader<>(TestGene.class),
            new TestWriter<>()
        );
    }

    @Bean
    public ModelProcessorBeanRegistry<DataProcessor<?>> processorRegistry() {
        return new ModelProcessorBeanRegistry<>();
    }

}
