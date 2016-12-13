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
package uk.ac.ebi.eva.pipeline.parameters.validation.step;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;

import uk.ac.ebi.eva.pipeline.jobs.steps.tasklets.PopulationStatisticsGeneratorStep;
import uk.ac.ebi.eva.pipeline.parameters.JobParametersNames;
import uk.ac.ebi.eva.test.rules.PipelineTemporaryFolderRule;

import java.io.IOException;

/**
 * Tests that the arguments necessary to run a {@link PopulationStatisticsGeneratorStep}
 * are correctly validated
 */
public class PopulationStatisticsGeneratorStepParametersValidatorTest {
    private PopulationStatisticsGeneratorStepParametersValidator validator;

    @Rule
    public PipelineTemporaryFolderRule temporaryFolderRule = new PipelineTemporaryFolderRule();

    @Before
    public void initialize() {
        validator = new PopulationStatisticsGeneratorStepParametersValidator();
    }

    @Test
    public void allJobParametersAreValid() throws JobParametersInvalidException, IOException {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString(JobParametersNames.DB_NAME, "dbName");
        jobParametersBuilder.addString(JobParametersNames.OUTPUT_DIR_STATISTICS, temporaryFolderRule.getRoot().getCanonicalPath());
        jobParametersBuilder.addString(JobParametersNames.INPUT_STUDY_ID, "inputStudyId");
        jobParametersBuilder.addString(JobParametersNames.INPUT_VCF_ID, "inputVcfId");

        validator.validate(jobParametersBuilder.toJobParameters());
    }

    @Test
    public void allJobParametersIncludingOptionalAreValid() throws JobParametersInvalidException, IOException {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString(JobParametersNames.DB_NAME, "dbName");
        jobParametersBuilder.addString(JobParametersNames.OUTPUT_DIR_STATISTICS, temporaryFolderRule.getRoot().getCanonicalPath());
        jobParametersBuilder.addString(JobParametersNames.INPUT_STUDY_ID, "inputStudyId");
        jobParametersBuilder.addString(JobParametersNames.INPUT_VCF_ID, "inputVcfId");
        jobParametersBuilder.addString(JobParametersNames.STATISTICS_OVERWRITE, "true");

        validator.validate(jobParametersBuilder.toJobParameters());
    }

    @Test(expected = JobParametersInvalidException.class)
    public void dbNameIsMissing() throws JobParametersInvalidException, IOException {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString(JobParametersNames.OUTPUT_DIR_STATISTICS, temporaryFolderRule.getRoot().getCanonicalPath());
        jobParametersBuilder.addString(JobParametersNames.INPUT_STUDY_ID, "inputStudyId");
        jobParametersBuilder.addString(JobParametersNames.INPUT_VCF_ID, "inputVcfId");

        validator.validate(jobParametersBuilder.toJobParameters());
    }

    @Test(expected = JobParametersInvalidException.class)
    public void outputDirStatisticsIsMissing() throws JobParametersInvalidException, IOException {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString(JobParametersNames.DB_NAME, "dbName");
        jobParametersBuilder.addString(JobParametersNames.INPUT_STUDY_ID, "inputStudyId");
        jobParametersBuilder.addString(JobParametersNames.INPUT_VCF_ID, "inputVcfId");

        validator.validate(jobParametersBuilder.toJobParameters());
    }

    @Test(expected = JobParametersInvalidException.class)
    public void inputStudyIdIsMissing() throws JobParametersInvalidException, IOException {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString(JobParametersNames.DB_NAME, "dbName");
        jobParametersBuilder.addString(JobParametersNames.OUTPUT_DIR_STATISTICS, temporaryFolderRule.getRoot().getCanonicalPath());
        jobParametersBuilder.addString(JobParametersNames.INPUT_VCF_ID, "inputVcfId");

        validator.validate(jobParametersBuilder.toJobParameters());
    }

    @Test(expected = JobParametersInvalidException.class)
    public void inputVcfIdIsMissing() throws JobParametersInvalidException, IOException {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString(JobParametersNames.DB_NAME, "dbName");
        jobParametersBuilder.addString(JobParametersNames.OUTPUT_DIR_STATISTICS, temporaryFolderRule.getRoot().getCanonicalPath());
        jobParametersBuilder.addString(JobParametersNames.INPUT_STUDY_ID, "inputStudyId");

        validator.validate(jobParametersBuilder.toJobParameters());
    }
}
