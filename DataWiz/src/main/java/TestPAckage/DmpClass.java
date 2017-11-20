package TestPAckage;

//import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import org.dom4j.Document;
//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;
//import org.dom4j.Namespace;
//import org.dom4j.QName;
//import org.dom4j.io.OutputFormat;
//import org.dom4j.io.XMLWriter;

import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ProjectDTO;

public class DmpClass {

	public static DmpDTO main(String[] args) {
		
		//Namespace ddi = new Namespace("ddi", null);
		//Namespace dwz = new Namespace("dw", null);
		
		//Document document = DocumentHelper.createDocument();
		
		ProjectDTO project = new ProjectDTO();
		project.setTitle("Project #1");
		project.setId(1);
		project.setOwnerId(0);
		project.setGrantNumber("1A2B");
		
		List<Integer> datatypes = new ArrayList<Integer>();
		for (int i = 0; i < 6; i++) {
			datatypes.add(i);
		}
		
		List<Integer> collmodes = new ArrayList<Integer>();
		for (int i = 6; i < 11; i++) {
			collmodes.add(i);
		}
		
		List<Integer> metapurp = new ArrayList<Integer>();
		for (int i = 11; i < 16; i++) {
			metapurp.add(i);
		}
		
		DmpDTO dmp = new DmpDTO();
		dmp.setId(project.getId());
		//dmp.setProjectAims("Ziel 1");
		dmp.setPlanAims("Plan A");
		//dmp.setProjectSponsors("Aldi");
		dmp.setDuration("100d");
		dmp.setOrganizations("Uni Trier");
		dmp.setExistingData("ex data");
		dmp.setExistingDataRelevance("ja");
		dmp.setExistingDataIntegration("nein");
		dmp.setDataCitation("ja");
		dmp.setUsedDataTypes(datatypes);
		dmp.setOtherDataTypes("other");
		dmp.setDataReproducibility("Reproduzierbarkeit");
		dmp.setUsedCollectionModes(collmodes);
		dmp.setOtherCMIP("present");
		dmp.setOtherCMINP("not present");
		dmp.setMeasOccasions("occasion");
		dmp.setReliabilityTraining("Reliabilitaet");
		dmp.setMultipleMeasurements("Messungen");
		dmp.setQualitityOther("other");
		dmp.setFileFormat("format");
		dmp.setWorkingCopy(true);
		dmp.setGoodScientific(true);
		dmp.setSubsequentUse(true);
		dmp.setRequirements(true);
		dmp.setDocumentation(true);
		dmp.setDataSelection(true);
		dmp.setSelectionTime("time");
		dmp.setSelectionResp("resp");
		dmp.setStorageDuration("300d");
		dmp.setDeleteProcedure("del");
		dmp.setSelectedMetaPurposes(metapurp);
		dmp.setMetaDescription("Beschreibung");
		dmp.setMetaFramework("Rahmen");
		dmp.setMetaGeneration("Generation");
		dmp.setMetaMonitor("Monitor");
		dmp.setMetaFormat("Format");
		dmp.setReleaseObligation(true);
		dmp.setExpectedUsage("Gebrauch");
		dmp.setPublStrategy("Strategie");
		dmp.setDepositName("Lidl");
		//dmp.setSearchableData(true);
		dmp.setTransferTime("10d");
		dmp.setSensitiveData("sensitiv");
		dmp.setInitialUsage("initial");
		dmp.setUsageRestriction("Restriktion");
		dmp.setAccessCosts(true);
		//dmp.setAccessCostsTxt("0.5");
		dmp.setClarifiedRights(true);
		dmp.setAcquisitionAgreement(true);
		dmp.setUsedPID("PID");
		dmp.setUsedPIDTxt("text");
		dmp.setStorageResponsible("Verantwortung");
		dmp.setStoragePlaces("hier, da");
		dmp.setStorageBackups("Backup");
		dmp.setStorageTransfer("Transfer");
		dmp.setStorageExpectedSize("1000000");
		dmp.setStorageRequirements(true);
		dmp.setStorageRequirementsTxt("none");
		dmp.setStorageSuccession(true);
		dmp.setStorageSuccessionTxt("none");
		dmp.setFrameworkNationality("de");
		dmp.setFrameworkNationalityTxt("german");
		dmp.setResponsibleUnit("none");
		dmp.setInvolvedInstitutions("Uni Trier");
		dmp.setInvolvedInformed(true);
		dmp.setContributionsDefined(true);
		dmp.setContributionsDefinedTxt("none");
		dmp.setGivenConsent(true);
		dmp.setManagementWorkflow(true);
		dmp.setManagementWorkflowTxt("none");
		dmp.setStaffDescription(true);
		dmp.setStaffDescriptionTxt("none");
		dmp.setFunderRequirements("funder");
		dmp.setPlanningAdherence("Planung");
		dmp.setDataProtection(true);
		dmp.setProtectionRequirements("none");
		dmp.setConsentObtained(true);
		dmp.setConsentObtainedTxt("none");
		dmp.setSharingConsidered(true);
		dmp.setIrbApproval(true);
		dmp.setIrbApprovalTxt("none");
		dmp.setSensitiveDataIncluded(true);
		dmp.setSensitiveDataIncludedTxt("none");
		dmp.setInternalCopyright(true);
		dmp.setInternalCopyrightTxt("none");
		dmp.setExternalCopyright(true);
		dmp.setExternalCopyrightTxt("none");
		dmp.setSpecificCosts("spezifische");
		dmp.setSpecificCostsTxt("Kosten");
		dmp.setBearCost("Baer");
		//dmp.setSearchableData(true);
		
		/*
		Element root = document.addElement("codebook");
		Element dmpElement = root.addElement(new QName("dmp", dwz));
		
		ParseXML exp = new ParseXML();
		
		try {
			exp.exportDmp(project, dmp, document, ddi, dwz, dmpElement);
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer;
			writer = new XMLWriter(System.out, format);
			writer.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		} */
		
		return dmp;
		
	}
	
	public static ProjectDTO ProjectDTO (String[] args) {
		
			ProjectDTO project = new ProjectDTO();
			project.setTitle("Project #1");
			project.setId(1);
			project.setOwnerId(0);
			project.setGrantNumber("1A2B");
			
			return project;
	}

}
