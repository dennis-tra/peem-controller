package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.GeneralInformation.GeneralAcquisitionData;
import de.agbauer.physik.PeemCommunicator.PeemProperty;

import java.util.Date;
import java.util.Map;

public class AcquisitionParameters {
    public AcquisitionParameters() {
        voltages = new AcquisitionParametersVoltages();
    }

    AcquisitionParameters(GeneralAcquisitionData data, Map<PeemProperty, String> allVoltages, Map<PeemProperty, String> allCurrents) {
        this.aperture = data.aperture;
        this.excitation = data.excitation;
        this.sampleName = data.sampleName;
        this.note = data.note;

        //this.exposure = "";

        this.createdAt = new Date();

        //this.imageNumber = "";
        voltages = new AcquisitionParametersVoltages(allVoltages);

        this.extractorI = Double.parseDouble(allCurrents.get(PeemProperty.EXTRACTOR));
        this.focusI = Double.parseDouble(allCurrents.get(PeemProperty.FOCUS));
        this.columnI = Double.parseDouble(allCurrents.get(PeemProperty.COLUMN));
        this.projective1I = Double.parseDouble(allCurrents.get(PeemProperty.PROJECTIVE_1));
        this.projective2I = Double.parseDouble(allCurrents.get(PeemProperty.PROJECTIVE_2));
        this.mcpI = Double.parseDouble(allCurrents.get(PeemProperty.MCP));
        this.screenI = Double.parseDouble(allCurrents.get(PeemProperty.SCREEN));

        this.voltages.stigmatorVx = Double.parseDouble(allVoltages.get(PeemProperty.DEFLECTOR_X));
        this.voltages.stigmatorVy = Double.parseDouble(allVoltages.get(PeemProperty.DEFLECTOR_Y));
        this.voltages.stigmatorSx = Double.parseDouble(allVoltages.get(PeemProperty.STIGMATOR_X));
        this.voltages.stigmatorSy = Double.parseDouble(allVoltages.get(PeemProperty.STIGMATOR_Y));
    }

    public double getExtractorU(){
        return this.voltages.extractorU;
    }

    public double getFocusU(){
        return this.voltages.focusU;
    }

    public double getColumnU(){
        return this.voltages.columnU;
    }

    public double getProjective1U(){
        return this.voltages.projective1U;
    }

    public double getProjective2U(){
        return this.voltages.projective2U;
    }

    public double getMcpU(){
        return this.voltages.mcpU;
    }

    public double getScreenU(){
        return this.voltages.screenU;
    }

    public double getStigmatorVx(){
        return this.voltages.stigmatorVx;
    }

    public double getStigmatorVy(){
        return this.voltages.stigmatorVy;
    }

    public double getStigmatorSx(){
        return this.voltages.stigmatorSx;
    }

    public double getStigmatorSy(){
        return this.voltages.stigmatorSy;
    }

    public void setExtractorU(double value){
        this.voltages.extractorU = value;
    }

    public void setFocusU(double value){
        this.voltages.focusU = value;
    }

    public void setColumnU(double value){
        this.voltages.columnU = value;
    }

    public void setProjective1U(double value){
        this.voltages.projective1U = value;
    }

    public void setProjective2U(double value){
        this.voltages.projective2U = value;
    }

    public void setMcpU(double value){
        this.voltages.mcpU = value;
    }

    public void setScreenU(double value){
        this.voltages.screenU = value;
    }

    public void setStigmatorVx(double value){
        this.voltages.stigmatorVx = value;
    }

    public void setStigmatorVy(double value){
        this.voltages.stigmatorVy = value;
    }

    public void setStigmatorSx(double value){
        this.voltages.stigmatorSx = value;
    }

    public void setStigmatorSy(double value){
        this.voltages.stigmatorSy = value;
    }

    public String aperture;
    public String excitation;
    public String sampleName;
    public String exposure;
    public Date createdAt;
    public int imageNumber;
    public String note;

    public double extractorI;

    public double focusI;

    public double columnI;

    public double projective1I;

    public double projective2I;

    public double mcpI;

    public double screenI;

    public AcquisitionParametersVoltages voltages;
}
