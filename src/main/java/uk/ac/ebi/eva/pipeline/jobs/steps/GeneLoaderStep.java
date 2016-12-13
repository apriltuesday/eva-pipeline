/*
 * Copyright 2016 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.eva.pipeline.jobs.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;
import uk.ac.ebi.eva.pipeline.io.mappers.GeneLineMapper;
import uk.ac.ebi.eva.pipeline.io.readers.GeneReader;
import uk.ac.ebi.eva.pipeline.io.writers.GeneWriter;
import uk.ac.ebi.eva.pipeline.jobs.steps.processors.GeneFilterProcessor;
import uk.ac.ebi.eva.pipeline.listeners.SkippedItemListener;
import uk.ac.ebi.eva.pipeline.model.FeatureCoordinates;
import uk.ac.ebi.eva.pipeline.parameters.JobOptions;
import uk.ac.ebi.eva.pipeline.parameters.JobParametersNames;
import uk.ac.ebi.eva.utils.MongoDBHelper;

import java.io.IOException;

/**
 * This step loads a list of genomic features from a species into a DB. This DB is intended to be used as a mapping
 * from feature names into feature coordinates (chromosome, start, end).
 * <p>
 * input: GTF file with a list of genomic features.
 * output: writes the features into MongoDB
 * <p>
 * To do so, this step performs the next stages:
 * - reader: To read the file, uses a {@link GeneReader} that fills a {@link FeatureCoordinates} for each line, using a {@link GeneLineMapper}.
 * - processor: Then, filters some, keeping only transcripts and genes.
 * - writer: And later uses a {@link GeneWriter} to load them into mongo.
 */

@Configuration
@EnableBatchProcessing
@Import(JobOptions.class)
public class GeneLoaderStep {
    private static final Logger logger = LoggerFactory.getLogger(GeneLoaderStep.class);

    public static final String NAME_GENES_LOAD_STEP = "genes-load-step";

    @Autowired
    private JobOptions jobOptions;

    @Bean(NAME_GENES_LOAD_STEP)
    public Step genesLoadStep(StepBuilderFactory stepBuilderFactory) throws IOException {
        logger.debug("Building '" + NAME_GENES_LOAD_STEP + "'");

        MongoOperations mongoOperations = MongoDBHelper
                .getMongoOperations(jobOptions.getDbName(), jobOptions.getMongoConnection());

        return stepBuilderFactory.get(NAME_GENES_LOAD_STEP)
                .<FeatureCoordinates, FeatureCoordinates>chunk(jobOptions.getPipelineOptions().getInt(JobParametersNames.CONFIG_CHUNK_SIZE))
                .reader(new GeneReader(jobOptions.getPipelineOptions().getString(JobParametersNames.INPUT_GTF)))
                .processor(new GeneFilterProcessor())
                .writer(new GeneWriter(mongoOperations, jobOptions.getDbCollectionsFeaturesName()))
                .faultTolerant().skipLimit(50).skip(FlatFileParseException.class)
                .listener(new SkippedItemListener())
                .build();
    }

}
