/*
 * Copyright (c) 2014-2015, Bolotin Dmitry, Chudakov Dmitry, Shugay Mikhail
 * (here and after addressed as Inventors)
 * All Rights Reserved
 *
 * Permission to use, copy, modify and distribute any part of this program for
 * educational, research and non-profit purposes, by non-profit institutions
 * only, without fee, and without a written agreement is hereby granted,
 * provided that the above copyright notice, this paragraph and the following
 * three paragraphs appear in all copies.
 *
 * Those desiring to incorporate this work into commercial products or use for
 * commercial purposes should contact the Inventors using one of the following
 * email addresses: chudakovdm@mail.ru, chudakovdm@gmail.com
 *
 * IN NO EVENT SHALL THE INVENTORS BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
 * SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
 * ARISING OUT OF THE USE OF THIS SOFTWARE, EVEN IF THE INVENTORS HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE SOFTWARE PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE INVENTORS HAS
 * NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR
 * MODIFICATIONS. THE INVENTORS MAKES NO REPRESENTATIONS AND EXTENDS NO
 * WARRANTIES OF ANY KIND, EITHER IMPLIED OR EXPRESS, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE, OR THAT THE USE OF THE SOFTWARE WILL NOT INFRINGE ANY
 * PATENT, TRADEMARK OR OTHER RIGHTS.
 */
package com.milaboratory.mixcr.tests;

import cc.redberry.pipe.CUtils;
import com.milaboratory.core.sequence.AminoAcidAlphabet;
import com.milaboratory.core.sequence.AminoAcidSequence;
import com.milaboratory.core.sequence.NSequenceWithQuality;
import com.milaboratory.mixcr.basictypes.*;
import com.milaboratory.mixcr.reference.GeneFeature;
import com.milaboratory.mixcr.reference.LociLibraryManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by dbolotin on 29/12/15.
 */
public class BackwardCompatibilityTests {
    @Test
    public void testAlignments() throws Exception {
        assertGoodVDJCA("/backward_compatibility/alignments_v1.6.vdjca.gz", 98);
        assertGoodVDJCA("/backward_compatibility/test_16.vdjca.gz", 76);
        assertGoodVDJCA("/backward_compatibility/test_17.vdjca.gz", 75);
        assertGoodVDJCA("/backward_compatibility/test_18.vdjca.gz", 76);
    }

    public static void assertGoodVDJCA(String resource, int size) throws IOException {
        try (VDJCAlignmentsReader reader = new VDJCAlignmentsReader(BackwardCompatibilityTests.class
                .getResource(resource).getFile(),
                LociLibraryManager.getDefault())) {
            int countGood = 0;
            for (VDJCAlignments vdjcAlignments : CUtils.it(reader)) {
                NSequenceWithQuality cdr3NQ = vdjcAlignments.getFeature(GeneFeature.CDR3);
                if (cdr3NQ == null)
                    continue;
                AminoAcidSequence aaCDR3 = AminoAcidSequence.translateFromCenter(cdr3NQ.getSequence());
                if (aaCDR3.codeAt(0) == AminoAcidAlphabet.C &&
                        aaCDR3.codeAt(aaCDR3.size() - 1) == AminoAcidAlphabet.F)
                    ++countGood;
            }
            Assert.assertEquals(size, countGood);
        }
    }

    @Test
    public void testBC16Cloneset() throws Exception {
        assertGoodCLNS("/backward_compatibility/clones_v1.6.clns.gz", 24, 24);
        assertGoodCLNS("/backward_compatibility/test_16.clns.gz", 22, 17);
        assertGoodCLNS("/backward_compatibility/test_17.clns.gz", 21, 16);
        assertGoodCLNS("/backward_compatibility/test_18.clns.gz", 81, 66);
    }

    public static void assertGoodCLNS(String resource, int size, int good) throws IOException {
        CloneSet cloneSet = CloneSetIO.read(BackwardCompatibilityTests.class
                        .getResource(resource).getFile(),
                LociLibraryManager.getDefault());
        Assert.assertEquals(size, cloneSet.size());
        int countGood = 0;
        for (Clone clone : cloneSet.getClones()) {
            AminoAcidSequence aaCDR3 = AminoAcidSequence.translateFromCenter(clone.getFeature(GeneFeature.CDR3).getSequence());
            if (aaCDR3.codeAt(0) == AminoAcidAlphabet.C &&
                    aaCDR3.codeAt(aaCDR3.size() - 1) == AminoAcidAlphabet.F) {
                ++countGood;
            }
        }
        Assert.assertEquals(good, countGood);
    }
}
