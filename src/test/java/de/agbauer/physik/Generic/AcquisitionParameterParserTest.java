package de.agbauer.physik.Generic;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class AcquisitionParameterParserTest {
    private AcquisitionParameters ap;

    @Before
    public void setUp() throws Exception {
        this.ap = new AcquisitionParameters();
    }

    @Test
    public void parsesApertureValue() throws Exception {
        ap = AcquisitionParameterParser.parseLine(this.ap, "ContrastAperture 500               ");
        assertEquals("500", ap.aperture);

        ap = AcquisitionParameterParser.parseLine(this.ap, "ContrastAperture 800");
        assertEquals("800", ap.aperture);

        ap.aperture = null;
        ap = AcquisitionParameterParser.parseLine(this.ap, "ContrastAperture ");
        assertEquals(null, ap.aperture);

        ap.aperture = null;
        ap = AcquisitionParameterParser.parseLine(this.ap, "ContrastAperture");
        assertEquals(null, ap.aperture);

        ap.aperture = null;
        ap = AcquisitionParameterParser.parseLine(this.ap, "ContrastAperture 800 +3");
        assertEquals("800 +3", ap.aperture);
    }

    @Test
    public void parsesExcitationValue() throws Exception {
        ap = AcquisitionParameterParser.parseLine(this.ap, "Excitation       Hg                ");
        assertEquals("Hg", ap.excitation);

        ap.excitation = null;
        ap = AcquisitionParameterParser.parseLine(this.ap, "Excitation       210p  ");
        assertEquals("210p", ap.excitation);

        ap.excitation = null;
        ap = AcquisitionParameterParser.parseLine(this.ap, "Excitation       210p + 410vH  ");
        assertEquals("210p + 410vH", ap.excitation);
    }

    @Test
    public void parsesSampleNameValue() throws Exception {
        ap = AcquisitionParameterParser.parseLine(this.ap, "Sample           Si_300SiO2_MoS2_P9");
        assertEquals("Si_300SiO2_MoS2_P9", ap.sampleName);
    }

    @Test
    public void parsesExposureValue() throws Exception {
        ap = AcquisitionParameterParser.parseLine(this.ap, "Integration      120s              ");
        assertEquals("120s", ap.exposure);

    }

    @Test
    public void parsesDateValue() throws Exception {
        Date date = new SimpleDateFormat("yyyyMMdd-HHmm").parse("20170206-1723");
        ap = AcquisitionParameterParser.parseLine(this.ap, "Run on 20170206-1723");
        assertEquals(date.compareTo(ap.createdAt), 0);

    }

    @Test
    public void parsesImageNumber() throws Exception {
        ap = AcquisitionParameterParser.parseLine(this.ap, "Imagenr          01                ");
        assertEquals(ap.imageNumber, 1);
        ap = AcquisitionParameterParser.parseLine(this.ap, "Imagenr          13                ");
        assertEquals(ap.imageNumber, 13);

        ap.imageNumber = 0;
        ap = AcquisitionParameterParser.parseLine(this.ap, "Imagenr                          ");
        assertEquals(ap.imageNumber, 0);

        ap.imageNumber = 0;
        ap = AcquisitionParameterParser.parseLine(this.ap, "Imagenr         asdf                 ");
        assertEquals(ap.imageNumber, 0);
    }

    @Test
    public void parsesNote() throws Exception {
        ap = AcquisitionParameterParser.parseLine(this.ap, "Note                               ");
        assertEquals(null, ap.note);

        ap = AcquisitionParameterParser.parseLine(this.ap, "Note          this is a test ");
        assertEquals("this is a test", ap.note);
    }

    @Test
    public void parsesExtractorValues() throws Exception {
        ap = AcquisitionParameterParser.parseLine(this.ap, "extractor   12502,2  40300");
        assertEquals(12502.2, ap.getExtractorU(), 0.01);
        assertEquals(40300, ap.extractorI, 0.01);

        ap.setExtractorU(0.0);
        ap.extractorI = 0.0;
        ap = AcquisitionParameterParser.parseLine(this.ap, "extractor   ");
        assertEquals(0.0, ap.getExtractorU(), 0.01);
        assertEquals(0.0, ap.extractorI, 0.01);

        ap.setExtractorU(0.0);
        ap.extractorI = 0.0;
        ap = AcquisitionParameterParser.parseLine(this.ap, "extractor  32.4 533,3 ");
        assertEquals(32.4, ap.getExtractorU(), 0.01);
        assertEquals(533.3, ap.extractorI, 0.01);
    }

    @Test
    public void parsesStigmatorValues() throws Exception {
        ap = AcquisitionParameterParser.parseLine(this.ap, "stigmator -2,883 -0,003 0,06  0");
        assertEquals(-2.883, ap.getStigmatorVx(), 0.01);
        assertEquals(-0.003, ap.getStigmatorVy(), 0.01);
        assertEquals(0.06, ap.getStigmatorSx(), 0.01);
        assertEquals(0, ap.getStigmatorSy(), 0.01);
    }


}