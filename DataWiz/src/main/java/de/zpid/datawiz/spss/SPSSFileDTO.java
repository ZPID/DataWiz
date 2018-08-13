package de.zpid.datawiz.spss;




import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This file is part of SPSS_IO_UTILS<br />
 *
 * <b>Copyright 2016, Leibniz Institute for Psychology Information (ZPID),
 * <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style=
 * "border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL">
 * Leibniz Institute for Psychology Information (ZPID)</a> is licensed under a
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 */
public class SPSSFileDTO implements Serializable {

    private static final long serialVersionUID = 7663934350286269057L;

    private boolean u8;
    private int hFile;
    private String filepath;
    private String password;
    private int numberOfVariables;
    private int numberOfFileAttributes;
    private long numberOfCases;
    private long estimatedNofCases;
    private long caseSize;
    private String caseWeightVar;
    private SPSSCompression compression;
    private int dateNumOfElements;
    private long dateInfo;
    private int fileCodePage;
    private String fileEncoding;
    private String fileIdString;
    private SPSSPageEncoding interfaceEncoding;
    private String multRespDefsEx;
    private String varSets;

    private List<SPSSVarDTO> variables;
    private List<SPSSValueLabelDTO> attributes;

    private List<List<Object>> dataMatrix;
    private String dataMatrixJson;

    private List<SPSSErrorDTO> errors;

    public SPSSFileDTO(final String filepath) {
        this();
        this.filepath = filepath;
    }

    public SPSSFileDTO() {
        super();
        this.hFile = -1;
        this.u8 = false;
        this.variables = new ArrayList<SPSSVarDTO>();
        this.attributes = new ArrayList<SPSSValueLabelDTO>();
        this.dataMatrix = new ArrayList<List<Object>>();
        this.errors = new LinkedList<SPSSErrorDTO>();
    }

    public SPSSFileDTO(boolean u8, int hFile, String filepath, String password, int numberOfVariables,
                       int numberOfFileAttributes, long numberOfCases, long estimatedNofCases, long caseSize, String caseWeightVar,
                       SPSSCompression compression, int dateNumOfElements, long dateInfo, int fileCodePage, String fileEncoding,
                       String fileIdString, SPSSPageEncoding interfaceEncoding, String multRespDefsEx, String varSets,
                       List<SPSSVarDTO> variables, List<SPSSValueLabelDTO> attributes, List<List<Object>> dataMatrix,
                       String dataMatrixJson, List<SPSSErrorDTO> errors) {
        super();
        this.u8 = u8;
        this.hFile = hFile;
        this.filepath = filepath;
        this.password = password;
        this.numberOfVariables = numberOfVariables;
        this.numberOfFileAttributes = numberOfFileAttributes;
        this.numberOfCases = numberOfCases;
        this.estimatedNofCases = estimatedNofCases;
        this.caseSize = caseSize;
        this.caseWeightVar = caseWeightVar;
        this.compression = compression;
        this.dateNumOfElements = dateNumOfElements;
        this.dateInfo = dateInfo;
        this.fileCodePage = fileCodePage;
        this.fileEncoding = fileEncoding;
        this.fileIdString = fileIdString;
        this.interfaceEncoding = interfaceEncoding;
        this.multRespDefsEx = multRespDefsEx;
        this.varSets = varSets;
        this.variables = variables;
        this.attributes = attributes;
        this.dataMatrix = dataMatrix;
        this.dataMatrixJson = dataMatrixJson;
        this.errors = errors;
    }

    public boolean isU8() {
        return u8;
    }

    public void setU8(boolean u8) {
        this.u8 = u8;
    }

    public int gethFile() {
        return hFile;
    }

    public void sethFile(int hFile) {
        this.hFile = hFile;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNumberOfVariables() {
        return numberOfVariables;
    }

    public void setNumberOfVariables(int numberOfVariables) {
        this.numberOfVariables = numberOfVariables;
    }

    public int getNumberOfFileAttributes() {
        return numberOfFileAttributes;
    }

    public void setNumberOfFileAttributes(int numberOfFileAttributes) {
        this.numberOfFileAttributes = numberOfFileAttributes;
    }

    public long getNumberOfCases() {
        return numberOfCases;
    }

    public void setNumberOfCases(long numberOfCases) {
        this.numberOfCases = numberOfCases;
    }

    public long getEstimatedNofCases() {
        return estimatedNofCases;
    }

    public void setEstimatedNofCases(long estimatedNofCases) {
        this.estimatedNofCases = estimatedNofCases;
    }

    public long getCaseSize() {
        return caseSize;
    }

    public void setCaseSize(long caseSize) {
        this.caseSize = caseSize;
    }

    public String getCaseWeightVar() {
        return caseWeightVar;
    }

    public void setCaseWeightVar(String caseWeightVar) {
        this.caseWeightVar = caseWeightVar;
    }

    public SPSSCompression getCompression() {
        return compression;
    }

    public void setCompression(SPSSCompression compression) {
        this.compression = compression;
    }

    public int getDateNumOfElements() {
        return dateNumOfElements;
    }

    public void setDateNumOfElements(int dateNumOfElements) {
        this.dateNumOfElements = dateNumOfElements;
    }

    public long getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(long dateInfo) {
        this.dateInfo = dateInfo;
    }

    public int getFileCodePage() {
        return fileCodePage;
    }

    public void setFileCodePage(int fileCodePage) {
        this.fileCodePage = fileCodePage;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public String getFileIdString() {
        return fileIdString;
    }

    public void setFileIdString(String fileIdString) {
        this.fileIdString = fileIdString;
    }

    public SPSSPageEncoding getInterfaceEncoding() {
        return interfaceEncoding;
    }

    public void setInterfaceEncoding(SPSSPageEncoding interfaceEncoding) {
        this.interfaceEncoding = interfaceEncoding;
    }

    public String getMultRespDefsEx() {
        return multRespDefsEx;
    }

    public void setMultRespDefsEx(String multRespDefsEx) {
        this.multRespDefsEx = multRespDefsEx;
    }

    public List<SPSSVarDTO> getVariables() {
        return variables;
    }

    public void setVariables(List<SPSSVarDTO> variables) {
        this.variables = variables;
    }

    public List<SPSSValueLabelDTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<SPSSValueLabelDTO> attributes) {
        this.attributes = attributes;
    }

    public List<List<Object>> getDataMatrix() {
        return dataMatrix;
    }

    public void setDataMatrix(List<List<Object>> dataMatrix) {
        this.dataMatrix = dataMatrix;
    }

    public String getDataMatrixJson() {
        return dataMatrixJson;
    }

    public void setDataMatrixJson(String dataMatrixJson) {
        this.dataMatrixJson = dataMatrixJson;
    }

    public List<SPSSErrorDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<SPSSErrorDTO> errors) {
        this.errors = errors;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getVarSets() {
        return varSets;
    }

    public void setVarSets(String varSets) {
        this.varSets = varSets;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + (int) (caseSize ^ (caseSize >>> 32));
        result = prime * result + ((caseWeightVar == null) ? 0 : caseWeightVar.hashCode());
        result = prime * result + ((compression == null) ? 0 : compression.hashCode());
        result = prime * result + ((dataMatrix == null) ? 0 : dataMatrix.hashCode());
        result = prime * result + ((dataMatrixJson == null) ? 0 : dataMatrixJson.hashCode());
        result = prime * result + (int) (dateInfo ^ (dateInfo >>> 32));
        result = prime * result + dateNumOfElements;
        result = prime * result + ((errors == null) ? 0 : errors.hashCode());
        result = prime * result + (int) (estimatedNofCases ^ (estimatedNofCases >>> 32));
        result = prime * result + fileCodePage;
        result = prime * result + ((fileEncoding == null) ? 0 : fileEncoding.hashCode());
        result = prime * result + ((fileIdString == null) ? 0 : fileIdString.hashCode());
        result = prime * result + ((filepath == null) ? 0 : filepath.hashCode());
        result = prime * result + hFile;
        result = prime * result + ((interfaceEncoding == null) ? 0 : interfaceEncoding.hashCode());
        result = prime * result + ((multRespDefsEx == null) ? 0 : multRespDefsEx.hashCode());
        result = prime * result + ((varSets == null) ? 0 : varSets.hashCode());
        result = prime * result + (int) (numberOfCases ^ (numberOfCases >>> 32));
        result = prime * result + numberOfFileAttributes;
        result = prime * result + numberOfVariables;
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + (u8 ? 1231 : 1237);
        result = prime * result + ((variables == null) ? 0 : variables.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SPSSFileDTO other = (SPSSFileDTO) obj;
        if (attributes == null) {
            if (other.attributes != null)
                return false;
        } else if (!attributes.equals(other.attributes))
            return false;
        if (caseSize != other.caseSize)
            return false;
        if (caseWeightVar == null) {
            if (other.caseWeightVar != null)
                return false;
        } else if (!caseWeightVar.equals(other.caseWeightVar))
            return false;
        if (compression != other.compression)
            return false;
        if (dataMatrix == null) {
            if (other.dataMatrix != null)
                return false;
        } else if (!dataMatrix.equals(other.dataMatrix))
            return false;
        if (dataMatrixJson == null) {
            if (other.dataMatrixJson != null)
                return false;
        } else if (!dataMatrixJson.equals(other.dataMatrixJson))
            return false;
        if (dateInfo != other.dateInfo)
            return false;
        if (dateNumOfElements != other.dateNumOfElements)
            return false;
        if (errors == null) {
            if (other.errors != null)
                return false;
        } else if (!errors.equals(other.errors))
            return false;
        if (estimatedNofCases != other.estimatedNofCases)
            return false;
        if (fileCodePage != other.fileCodePage)
            return false;
        if (fileEncoding == null) {
            if (other.fileEncoding != null)
                return false;
        } else if (!fileEncoding.equals(other.fileEncoding))
            return false;
        if (fileIdString == null) {
            if (other.fileIdString != null)
                return false;
        } else if (!fileIdString.equals(other.fileIdString))
            return false;
        if (filepath == null) {
            if (other.filepath != null)
                return false;
        } else if (!filepath.equals(other.filepath))
            return false;
        if (hFile != other.hFile)
            return false;
        if (interfaceEncoding != other.interfaceEncoding)
            return false;
        if (multRespDefsEx == null) {
            if (other.multRespDefsEx != null)
                return false;
        } else if (!multRespDefsEx.equals(other.multRespDefsEx))
            return false;
        if (varSets == null) {
            if (other.varSets != null)
                return false;
        } else if (!varSets.equals(other.varSets))
            return false;
        if (numberOfCases != other.numberOfCases)
            return false;
        if (numberOfFileAttributes != other.numberOfFileAttributes)
            return false;
        if (numberOfVariables != other.numberOfVariables)
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (u8 != other.u8)
            return false;
        if (variables == null) {
            if (other.variables != null)
                return false;
        } else if (!variables.equals(other.variables))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SPSSFileDTO [u8=" + u8 + ", hFile=" + hFile + ", filepath=" + filepath + ", password=" + password
                + ", numberOfVariables=" + numberOfVariables + ", numberOfFileAttributes=" + numberOfFileAttributes
                + ", numberOfCases=" + numberOfCases + ", estimatedNofCases=" + estimatedNofCases + ", caseSize=" + caseSize
                + ", caseWeightVar=" + caseWeightVar + ", compression=" + compression + ", dateNumOfElements="
                + dateNumOfElements + ", dateInfo=" + dateInfo + ", fileCodePage=" + fileCodePage + ", fileEncoding="
                + fileEncoding + ", fileIdString=" + fileIdString + ", interfaceEncoding=" + interfaceEncoding
                + ", multRespDefsEx=" + multRespDefsEx + ", varSets=" + varSets + ", variables=" + variables + ", attributes="
                + attributes + ", dataMatrix=" + dataMatrix + ", dataMatrixJson=" + dataMatrixJson + ", errors=" + errors + "]";
    }

}
