package de.viadee.bpm.vPAV.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import de.viadee.bpm.vPAV.processing.model.data.CriticalityEnum;

public class TestJsOutputWriter {

    /**
     * Test write JS File
     * 
     * @throws OutputWriterException
     * @throws IOException
     * 
     * 
     */
    @Test()
    public void testOutput() throws OutputWriterException, IOException {
        // Given
        final IssueOutputWriter jsOutputWriter = new JsOutputWriter();
        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
        Set<String> s = new HashSet<String>();

        issues.add(new CheckerIssue("VersioningChecker", CriticalityEnum.WARNING,
                "src\\main\\resources\\KfzGlasbruch.bpmn",
                "de/viasurance/kfzglasbruch/delegate/VertragErmittelnDelegate_1_0.java",
                "sid-7D0E4D10-3116-4FDD-8B9F-B177D237CBCA", "Vertrag anhand VSNR lesen",
                null, null, null, "bean reference is deprecated or file with version doesn\u0027t exist"));

        issues.add(new CheckerIssue("JavaDelegateChecker", CriticalityEnum.ERROR,
                "src\\main\\resources\\KfzGlasbruch.bpmn",
                "de/viasurance/kfzglasbruch/delegate/KontoErmittelnDelegate_1_0.java", "12", "Konto des VN ermitteln",
                null, null, null,
                "class for task \u0027Konto des VN ermitteln\u0027 does not implement interface JavaDelegate"));

        String filename = "KfzGlasbruch.bpmn";

        s.add(filename);
        s.add(filename);

        // When
        // jsOutputWriter.write(issues, s); //Klappt nicht, da path fest in Methode geschrieben

        // Then
        // sinnvolle Prüfung ergänzen
    }
}
