package uk.ac.evi.eva.pipeline.io.writers;


import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import embl.ebi.variation.eva.VariantJobsArgs;
import embl.ebi.variation.eva.pipeline.config.InitDBConfig;
import embl.ebi.variation.eva.pipeline.gene.FeatureCoordinates;
import embl.ebi.variation.eva.pipeline.gene.GeneLineMapper;
import embl.ebi.variation.eva.pipeline.jobs.JobTestUtils;
import embl.ebi.variation.eva.pipeline.steps.GenesLoad;
import embl.ebi.variation.eva.pipeline.steps.writers.GeneWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.evi.eva.test.data.GtfStaticTestData;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { VariantJobsArgs.class, InitDBConfig.class,})
public class GeneReaderWriterTest {

    @Autowired
    public VariantJobsArgs variantJobsArgs;

    @Test
    public void shouldWriteAllFieldsIntoMongoDb() throws Exception {
        String dbName = variantJobsArgs.getPipelineOptions().getString("db.name");
        JobTestUtils.cleanDBs(dbName);

        GeneWriter geneWriter = new GeneWriter(variantJobsArgs.getMongoOperations(), variantJobsArgs.getDbCollectionsFeaturesName());

        GeneLineMapper lineMapper = new GeneLineMapper();
        List<FeatureCoordinates> genes = new ArrayList<>();
        for (String gtfLine : GtfStaticTestData.GTF_CONTENT.split(GtfStaticTestData.GTF_LINE_SPLIT)) {
            if (!gtfLine.startsWith(GtfStaticTestData.GTF_COMMENT_LINE)) {
                genes.add(lineMapper.mapLine(gtfLine, 0));
            }
        }
        geneWriter.write(genes);

        MongoClient mongoClient = new MongoClient();
        DBCollection genesCollection = mongoClient.getDB(dbName).getCollection(variantJobsArgs.getDbCollectionsFeaturesName());

        // count documents in DB and check they have region (chr + start + end)
        DBCursor cursor = genesCollection.find();

        int count = 0;
        while (cursor.hasNext()) {
            count++;
            DBObject next = cursor.next();
            assertTrue(next.get("chromosome") != null);
            assertTrue(next.get("start") != null);
            assertTrue(next.get("end") != null);
        }
        assertEquals(genes.size(), count);
    }

}
