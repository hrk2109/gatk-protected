package org.broadinstitute.sting.utils.codecs.vcf;

import org.broadinstitute.sting.WalkerTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class VCFIntegrationTest extends WalkerTest {

    @Test(enabled = true)
    public void testReadingAndWritingWitHNoChanges() {

        String md5ofInputVCF = "babf02baabcfa7f72a2c6f7da5fdc996";
        String testVCF = privateTestDir + "vcf4.1.example.vcf";

        String baseCommand = "-R " + b37KGReference + " --no_cmdline_in_header -o %s ";

        String test1 = baseCommand + "-T VariantAnnotator --variant " + testVCF + " -L " + testVCF;
        WalkerTestSpec spec1 = new WalkerTestSpec(test1, 1, Arrays.asList(md5ofInputVCF));
        List<File> result = executeTest("Test Variant Annotator with no changes", spec1).getFirst();

        String test2 = baseCommand + "-T VariantsToVCF --variant " + result.get(0).getAbsolutePath();
        WalkerTestSpec spec2 = new WalkerTestSpec(test2, 1, Arrays.asList(md5ofInputVCF));
        executeTest("Test Variants To VCF from new output", spec2);
    }

    @Test(enabled = false)
    // See https://getsatisfaction.com/gsa/topics/support_vcf_4_1_structural_variation_breakend_alleles?utm_content=topic_link&utm_medium=email&utm_source=new_topic
    public void testReadingAndWritingBreakpointAlleles() {
        String testVCF = privateTestDir + "breakpoint-example.vcf";
        //String testVCF = validationDataLocation + "multiallelic.vcf";

        String baseCommand = "-R " + b37KGReference + " --no_cmdline_in_header -o %s ";

        String test1 = baseCommand + "-T SelectVariants -V " + testVCF;
        WalkerTestSpec spec1 = new WalkerTestSpec(test1, 1, Arrays.asList("355b029487c3b4c499140d71310ca37e"));
        executeTest("Test reading and writing breakpoint VCF", spec1);
    }

    @Test
    public void testReadingAndWritingSamtools() {
        String testVCF = privateTestDir + "samtools.vcf";

        String baseCommand = "-R " + b37KGReference + " --no_cmdline_in_header -o %s ";

        String test1 = baseCommand + "-T SelectVariants -V " + testVCF;
        WalkerTestSpec spec1 = new WalkerTestSpec(test1, 1, Arrays.asList("cf3f09ef63feba6958d32f95d587af80"));
        executeTest("Test reading and writing samtools vcf", spec1);
    }

    @Test
    public void testReadingAndWritingSamtoolsWExBCFExample() {
        String testVCF = privateTestDir + "ex2.vcf";
        String baseCommand = "-R " + b36KGReference + " --no_cmdline_in_header -o %s ";
        String test1 = baseCommand + "-T SelectVariants -V " + testVCF;
        WalkerTestSpec spec1 = new WalkerTestSpec(test1, 1, Arrays.asList("5839ed4972e2ccf3a7b190752ede6596"));
        executeTest("Test reading and writing samtools WEx vcf/BCF example", spec1);
    }
}
