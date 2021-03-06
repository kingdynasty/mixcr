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
package com.milaboratory.mixcr.cli;

import com.milaboratory.cli.JCommanderBasedMain;
import com.milaboratory.mixcr.util.TempFileManager;
import com.milaboratory.mixcr.util.VersionInfoProvider;

import java.security.SecureRandom;
import java.util.Arrays;

public class Main {
    public static void main(String... args) throws Exception {
        TempFileManager.seed(Arrays.hashCode(args) + 17 * (new SecureRandom()).nextLong());
        // Getting command string if executed from script
        String command = System.getProperty("mixcr.command", "java -jar mixcr.jar");

        // Setting up main helper
        JCommanderBasedMain main = new JCommanderBasedMain(command,
                new ActionAlign(),
                new ActionExportAlignments(),
                new ActionAssemble(),
                new ActionExportClones(),
                new ActionPrettyAlignments(),
                new ActionAlignmentsStat(),
                new ActionMergeAlignments(),
                new ActionInfo(),
                new ActionExportCloneReads(),
                new VersionInfoAction(),
                new ActionImportSegments(),
                new ActionAlignmentsDiff(),
                new ActionAssemblePartialAlignments(),
                new ActionExportReads(),
                new ActionClonesDiff());

        // Adding version info callback
        main.setVersionInfoCallback(new Runnable() {
            @Override
            public void run() {
                System.err.println(
                        VersionInfoProvider.getVersionString(
                                VersionInfoProvider.OutputType.ToConsole));
            }
        });

        // Executing main method
        JCommanderBasedMain.ProcessResult processResult = main.main(args);

        // If something was wrong, exit with code 1
        if (processResult == JCommanderBasedMain.ProcessResult.Error)
            System.exit(1);
    }
}
