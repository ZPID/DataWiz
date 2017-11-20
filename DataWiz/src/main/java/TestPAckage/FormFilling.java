package TestPAckage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyConstructDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.StudyInstrumentDTO;
import de.zpid.datawiz.dto.StudyListTypesDTO;

@Component
public class FormFilling {
	public static String pathToFolder = "/home/ronny/Downloads/dwpdf/";
	public static final String SRC1 = pathToFolder + "Zuordnung_H2020_icon_final_fuer_JH.pdf";
	public static final String DEST1 = pathToFolder + "export/Zuordnung_H2020_fill.pdf";
	public static final String SRC2 = pathToFolder + "Zuordnung_BMBF_AZA_Checklistefuer_JH.pdf";
	public static final String DEST2 = pathToFolder + "export/Zuordnung_BMBF_fill.pdf";
	public static final String SRC3 = pathToFolder + "Zuordnung_PsychData_fuer_JH.pdf";
	public static final String DEST3 = pathToFolder + "export/Zuordnung_PsychData_fill.pdf";
	public static final String SRC4 = pathToFolder + "Zuordnung_dfg_Leitlinien_icon_final_fuer_JH.pdf";
	public static final String DEST4 = pathToFolder + "export/Zuordnung_dfg_fill.pdf";
	public static final String SRC5 = pathToFolder + "Zurordnung_Praeregistrierung_fuer_JH.pdf";
	public static final String DEST5 = pathToFolder + "export/Zuordnung_Prae_fill.pdf";

	Properties studyp = null;
	Properties dmpp = null;
	Properties projectp = null;

	public FormFilling() {
		BufferedInputStream bis;
		try {
			this.studyp = new Properties();
			bis = new BufferedInputStream(new FileInputStream(pathToFolder + "StudyResources_de.properties"));
			this.studyp.load(bis);
			bis.close();
			this.dmpp = new Properties();
			bis = new BufferedInputStream(new FileInputStream(pathToFolder + "DMPResources_de.properties"));
			this.dmpp.load(bis);
			bis.close();
			this.projectp = new Properties();
			bis = new BufferedInputStream(new FileInputStream(pathToFolder + "ApplicationResources_de.properties"));
			this.projectp.load(bis);
			bis.close();
		} catch (Exception e) {
			System.err.println("Error reading properties: " + e.getMessage());
			System.exit(0);
		}

	}

	public static void main(String[] args) {

		FormFilling ff1 = new FormFilling();
		FormFilling ff2 = new FormFilling();
		FormFilling ff3 = new FormFilling();
		FormFilling ff4 = new FormFilling();
		FormFilling ff5 = new FormFilling();

		DmpDTO dmp = DmpClass.main(args);
		ProjectDTO project = DmpClass.ProjectDTO(args);
		StudyDTO study = StudyClass.main(args);

		File file1 = new File(DEST1);
		file1.getParentFile().mkdirs();
		PdfReader reader1 = null;
		PdfDocument pdf1 = null;
		try {
			reader1 = new PdfReader(SRC1);
			pdf1 = new PdfDocument(reader1, new PdfWriter(DEST1));
		} catch (IOException e) {
			System.err.println("Error creating PDF-DOC: " + e.getMessage());
			System.exit(0);
		}
		PdfAcroForm form1 = PdfAcroForm.getAcroForm(pdf1, true);
		ff1.manipulatePdf1(form1, dmp);
		pdf1.close();

		File file2 = new File(DEST2);
		file2.getParentFile().mkdirs();
		PdfReader reader2 = null;
		PdfDocument pdf2 = null;
		try {
			reader2 = new PdfReader(SRC2);
			pdf2 = new PdfDocument(reader2, new PdfWriter(DEST2));
		} catch (IOException e) {
			System.err.println("Error creating PDF-DOC: " + e.getMessage());
			System.exit(0);
		}
		PdfAcroForm form2 = PdfAcroForm.getAcroForm(pdf2, true);
		ff2.manipulatePdf2(form2, dmp, project);
		pdf2.close();

		File file3 = new File(DEST3);
		file3.getParentFile().mkdirs();
		PdfReader reader3 = null;
		PdfDocument pdf3 = null;
		try {
			reader3 = new PdfReader(SRC3);
			pdf3 = new PdfDocument(reader3, new PdfWriter(DEST3));
		} catch (IOException e) {
			System.err.println("Error creating PDF-DOC: " + e.getMessage());
			System.exit(0);
		}
		PdfAcroForm form3 = PdfAcroForm.getAcroForm(pdf3, true);
		ff3.manipulatePdf3(form3, study);
		pdf3.close();

		File file4 = new File(DEST4);
		file4.getParentFile().mkdirs();
		PdfReader reader4 = null;
		PdfDocument pdf4 = null;
		try {
			reader4 = new PdfReader(SRC4);
			pdf4 = new PdfDocument(reader4, new PdfWriter(DEST4));
		} catch (IOException e) {
			System.err.println("Error creating PDF-DOC: " + e.getMessage());
			System.exit(0);
		}
		PdfAcroForm form4 = PdfAcroForm.getAcroForm(pdf4, true);
		ff4.manipulatePdf4(form4, dmp);
		pdf4.close();

		File file5 = new File(DEST5);
		file5.getParentFile().mkdirs();
		PdfReader reader5 = null;
		PdfDocument pdf5 = null;
		try {
			reader5 = new PdfReader(SRC5);
			pdf5 = new PdfDocument(reader5, new PdfWriter(DEST5));
		} catch (IOException e) {
			System.err.println("Error creating PDF-DOC: " + e.getMessage());
			System.exit(0);
		}
		PdfAcroForm form5 = PdfAcroForm.getAcroForm(pdf5, true);
		ff5.manipulatePdf5(form5, study);
		pdf5.close();
	}

	public void manipulatePdf1(PdfAcroForm form, DmpDTO dmp) {
		Map<String, PdfFormField> fields = form.getFormFields();
		for (String name : fields.keySet()) {
			try {
				PdfFormField tf = fields.get(name);
				if (name.startsWith("dmp.") && dmpp.getProperty(name) != null) {
					tf.setValue(dmpp.getProperty(name));
				} /*
				   * else if (name.startsWith("study.") && studyp.getProperty(name) != null) { tf.setValue(studyp.getProperty(name)); } else if
				   * (name.startsWith("project.") && projectp.getProperty(name) != null) { tf.setValue(projectp.getProperty(name)); }
				   */
				tf.setReadOnly(true);
			} catch (Exception e) {
				System.err.println("Exception creating field: " + name);
			}
		}
		// form.getFormFields().get("projectAims.txt").setValue(( == null) ? "" : dmp.getProjectAims())
		// .setReadOnly(true);
		form.getFormFields().get("existingDataRelevance.txt").setValue((dmp.getExistingDataRelevance() == null) ? "" : dmp.getExistingDataRelevance())
		    .setReadOnly(true);
		form.getFormFields().get("usedDataTypes.txt").setValue((dmp.getUsedDataTypes() == null) ? "" : String.valueOf(dmp.getUsedDataTypes()))
		    .setReadOnly(true);
		form.getFormFields().get("fileFormat.txt").setValue((dmp.getFileFormat() == null) ? "" : dmp.getFileFormat()).setReadOnly(true);
		form.getFormFields().get("existingData.txt").setValue((dmp.getExistingData() == null) ? "" : dmp.getExistingData()).setReadOnly(true);
		form.getFormFields().get("dataCitation.txt").setValue((dmp.getDataCitation() == null) ? "" : dmp.getDataCitation()).setReadOnly(true);
		form.getFormFields().get("existingDataIntegration.txt")
		    .setValue((dmp.getExistingDataIntegration() == null) ? "" : dmp.getExistingDataIntegration()).setReadOnly(true);
		form.getFormFields().get("usedCollectionModes.txt")
		    .setValue((dmp.getUsedCollectionModes() == null) ? "" : String.valueOf(dmp.getUsedCollectionModes())).setReadOnly(true);
		form.getFormFields().get("storageExpectedSize.txt").setValue((dmp.getStorageExpectedSize() == null) ? "" : dmp.getStorageExpectedSize())
		    .setReadOnly(true);
		form.getFormFields().get("expectedUsage.txt").setValue((dmp.getExpectedUsage() == null) ? "" : dmp.getExpectedUsage()).setReadOnly(true);
		form.getFormFields().get("storageHeadline.txt").setValue("PRÜFEN").setColor(Color.RED).setReadOnly(true);
		// form.getFormFields().get("searchableData.txt").setValue(String.valueOf(dmp.isSearchableData()))
		// .setReadOnly(true);
		form.getFormFields().get("usedPID.txt").setValue((dmp.getUsedPID() == null) ? "" : dmp.getUsedPID()).setReadOnly(true);
		// form.getFormFields().get("storageTechnologies.txt")
		// .setValue((dmp.getStorageTechnologies() == null) ? "" : dmp.getStorageTechnologies()).setReadOnly(true);
		form.getFormFields().get("storageBackups.txt").setValue((dmp.getStorageBackups() == null) ? "" : dmp.getStorageBackups()).setReadOnly(true);
		form.getFormFields().get("metaGeneration.txt").setValue((dmp.getMetaGeneration() == null) ? "" : dmp.getMetaGeneration()).setReadOnly(true);
		form.getFormFields().get("selectedMetaPurposes.txt")
		    .setValue((dmp.getSelectedMetaPurposes() == null) ? "" : String.valueOf(dmp.getSelectedMetaPurposes())).setReadOnly(true);
		form.getFormFields().get("metaFramework.txt").setValue((dmp.getMetaFramework() == null) ? "" : dmp.getMetaFramework()).setReadOnly(true);
		form.getFormFields().get("metaDescription.txt").setValue((dmp.getMetaDescription() == null) ? "" : dmp.getMetaDescription()).setReadOnly(true);
		form.getFormFields().get("metaMonitor.txt").setValue((dmp.getMetaMonitor() == null) ? "" : dmp.getMetaMonitor()).setReadOnly(true);
		form.getFormFields().get("metaFormat.txt").setValue((dmp.getMetaFormat() == null) ? "" : dmp.getMetaFormat()).setReadOnly(true);
		form.getFormFields().get("sensitiveData.txt").setValue((dmp.getSensitiveData() == null) ? "" : dmp.getSensitiveData()).setReadOnly(true);
		form.getFormFields().get("accessReasonAuthor.txt").setValue((dmp.getAccessReasonAuthor() == null) ? "" : dmp.getAccessReasonAuthor())
		    .setReadOnly(true);
		form.getFormFields().get("noAccessReason.txt").setValue((dmp.getNoAccessReason() == null) ? "" : dmp.getNoAccessReason()).setReadOnly(true);
		form.getFormFields().get("publStrategy.txt").setValue((dmp.getPublStrategy() == null) ? "" : dmp.getPublStrategy()).setReadOnly(true);
		form.getFormFields().get("depositName.txt").setValue((dmp.getDepositName() == null) ? "" : dmp.getDepositName()).setReadOnly(true);
		form.getFormFields().get("accessCosts.txt").setValue(String.valueOf(dmp.isAccessCosts())).setReadOnly(true);
		// form.getFormFields().get("accessCostsTxt.txt")
		// .setValue((dmp.getAccessCostsTxt() == null) ? "" : dmp.getAccessCostsTxt()).setReadOnly(true);
		form.getFormFields().get("releaseObligation.txt").setValue(String.valueOf(dmp.isReleaseObligation())).setReadOnly(true);
		form.getFormFields().get("clarifiedRights.txt").setValue(String.valueOf(dmp.isClarifiedRights())).setReadOnly(true);
		form.getFormFields().get("acquisitionAgreement.txt").setValue(String.valueOf(dmp.isAcquisitionAgreement())).setReadOnly(true);
		form.getFormFields().get("storageRequirements.txt").setValue(String.valueOf(dmp.isStorageRequirements())).setReadOnly(true);
		form.getFormFields().get("storageRequirementsTxt.txt").setValue((dmp.getStorageRequirementsTxt() == null) ? "" : dmp.getStorageRequirementsTxt())
		    .setReadOnly(true);
		form.getFormFields().get("storagePlaces.txt").setValue((dmp.getStoragePlaces() == null) ? "" : dmp.getStoragePlaces()).setReadOnly(true);
		form.getFormFields().get("storageDuration.txt").setValue((dmp.getStorageDuration() == null) ? "" : dmp.getStorageDuration()).setReadOnly(true);
		form.getFormFields().get("internalCopyright.txt").setValue(String.valueOf(dmp.isInternalCopyright())).setReadOnly(true);
		form.getFormFields().get("internalCopyrightTxt.txt").setValue((dmp.getInternalCopyrightTxt() == null) ? "" : dmp.getInternalCopyrightTxt())
		    .setReadOnly(true);
		form.getFormFields().get("externalCopyright.txt").setValue(String.valueOf(dmp.isExternalCopyright())).setReadOnly(true);
		form.getFormFields().get("externalCopyrightTxt.txt").setValue((dmp.getExternalCopyrightTxt() == null) ? "" : dmp.getExternalCopyrightTxt())
		    .setReadOnly(true);
		form.getFormFields().get("usageRestriction.txt").setValue((dmp.getUsageRestriction() == null) ? "" : dmp.getUsageRestriction()).setReadOnly(true);
		form.getFormFields().get("transferTime.txt").setValue((dmp.getTransferTime() == null) ? "" : dmp.getTransferTime()).setReadOnly(true);
		form.getFormFields().get("initialUsage.txt").setValue((dmp.getInitialUsage() == null) ? "" : dmp.getInitialUsage()).setReadOnly(true);
		form.getFormFields().get("reliabilityTraining.txt").setValue((dmp.getReliabilityTraining() == null) ? "" : dmp.getReliabilityTraining())
		    .setReadOnly(true);
		form.getFormFields().get("multipleMeasurements.txt").setValue((dmp.getMultipleMeasurements() == null) ? "" : dmp.getMultipleMeasurements())
		    .setReadOnly(true);
		form.getFormFields().get("qualitityOther.txt").setValue((dmp.getQualitityOther() == null) ? "" : dmp.getQualitityOther()).setReadOnly(true);
		form.getFormFields().get("storageSuccessionTxt.txt").setValue((dmp.getStorageSuccessionTxt() == null) ? "" : dmp.getStorageSuccessionTxt())
		    .setReadOnly(true);
		form.getFormFields().get("specificCosts.txt").setValue((dmp.getSpecificCosts() == null) ? "" : dmp.getSpecificCosts()).setReadOnly(true);
		form.getFormFields().get("specificCostsTxt.txt").setValue((dmp.getSpecificCostsTxt() == null) ? "" : dmp.getSpecificCostsTxt()).setReadOnly(true);
		form.getFormFields().get("bearCost.txt").setValue((dmp.getBearCost() == null) ? "" : dmp.getBearCost()).setReadOnly(true);
		form.getFormFields().get("dmpeditresponsibleUnit").setValue((dmp.getResponsibleUnit() == null) ? "" : dmp.getResponsibleUnit()).setReadOnly(true);
		form.getFormFields().get("storageResponsible.txt").setValue((dmp.getStorageResponsible() == null) ? "" : dmp.getStorageResponsible())
		    .setReadOnly(true);
		form.getFormFields().get("dataProtection.txt").setValue(String.valueOf(dmp.isDataProtection())).setReadOnly(true);
		form.getFormFields().get("protectionRequirements.txt").setValue((dmp.getProtectionRequirements() == null) ? "" : dmp.getProtectionRequirements())
		    .setReadOnly(true);
		form.getFormFields().get("storageTransfer.txt").setValue((dmp.getStorageTransfer() == null) ? "" : dmp.getStorageTransfer()).setReadOnly(true);
		form.getFormFields().get("consentObtained.txt").setValue(String.valueOf(dmp.isConsentObtained())).setReadOnly(true);
		form.getFormFields().get("consentObtainedTxt.txt").setValue((dmp.getConsentObtainedTxt() == null) ? "" : dmp.getConsentObtainedTxt())
		    .setReadOnly(true);
		form.getFormFields().get("sharingConsidered.txt").setValue(String.valueOf(dmp.isSharingConsidered())).setReadOnly(true);
		form.getFormFields().get("irbApproval.txt").setValue(String.valueOf(dmp.isIrbApproval())).setReadOnly(true);
		form.getFormFields().get("irbApprovalTxt.txt").setValue((dmp.getIrbApprovalTxt() == null) ? "" : dmp.getIrbApprovalTxt()).setReadOnly(true);
		form.getFormFields().get("funderRequirements.txt").setValue((dmp.getFunderRequirements() == null) ? "" : dmp.getFunderRequirements())
		    .setReadOnly(true);
	}

	public void manipulatePdf2(PdfAcroForm form, DmpDTO dmp, ProjectDTO project) {
		Map<String, PdfFormField> fields = form.getFormFields();
		for (String name : fields.keySet()) {
			try {
				PdfFormField tf = fields.get(name);
				if (name.startsWith("dmp.") && dmpp.getProperty(name) != null) {
					tf.setValue(dmpp.getProperty(name));
				} /*
				   * else if (name.startsWith("study.") && studyp.getProperty(name) != null) { tf.setValue(studyp.getProperty(name)); }
				   */ else if (name.startsWith("project.") && projectp.getProperty(name) != null) {
					tf.setValue(projectp.getProperty(name));
				}
				tf.setReadOnly(true);
			} catch (Exception e) {
				System.err.println("Exception creating field: " + name);
			}
		}
		// form.getFormFields().get("projectAims.txt").setValue((dmp.getProjectAims() == null) ? "" : dmp.getProjectAims())
		// .setReadOnly(true);
		form.getFormFields().get("existingData.txt").setValue((dmp.getExistingData() == null) ? "" : dmp.getExistingData()).setReadOnly(true);
		form.getFormFields().get("dataCitation.txt").setValue((dmp.getDataCitation() == null) ? "" : dmp.getDataCitation()).setReadOnly(true);
		form.getFormFields().get("existingDataRelevance.txt").setValue((dmp.getExistingDataRelevance() == null) ? "" : dmp.getExistingDataRelevance())
		    .setReadOnly(true);
		form.getFormFields().get("existingDataIntegration.txt")
		    .setValue((dmp.getExistingDataIntegration() == null) ? "" : dmp.getExistingDataIntegration()).setReadOnly(true);
		form.getFormFields().get("externalCopyright.txt").setValue(String.valueOf(dmp.isExternalCopyright())).setReadOnly(true);
		form.getFormFields().get("externalCopyrightTxt.txt").setValue((dmp.getExternalCopyrightTxt() == null) ? "" : dmp.getExternalCopyrightTxt())
		    .setReadOnly(true);
		form.getFormFields().get("duration.txt").setValue((dmp.getDuration() == null) ? "" : dmp.getDuration()).setReadOnly(true);
		form.getFormFields().get("usedCollectionModes.txt")
		    .setValue((dmp.getUsedCollectionModes() == null) ? "" : String.valueOf(dmp.getUsedCollectionModes())).setReadOnly(true);
		form.getFormFields().get("measOccasions.txt").setValue((dmp.getMeasOccasions() == null) ? "" : dmp.getMeasOccasions()).setReadOnly(true);
		form.getFormFields().get("specificCosts.txt").setValue((dmp.getSpecificCosts() == null) ? "" : dmp.getSpecificCosts()).setReadOnly(true);
		form.getFormFields().get("specificCostsTxt.txt").setValue((dmp.getSpecificCostsTxt() == null) ? "" : dmp.getSpecificCostsTxt()).setReadOnly(true);
		form.getFormFields().get("bearCost.txt").setValue((dmp.getBearCost() == null) ? "" : dmp.getBearCost()).setReadOnly(true);
		form.getFormFields().get("staffDescription.txt").setValue(String.valueOf(dmp.isStaffDescription())).setReadOnly(true);
		form.getFormFields().get("staffDescriptionTxt.txt").setValue((dmp.getStaffDescriptionTxt() == null) ? "" : dmp.getStaffDescriptionTxt())
		    .setReadOnly(true);
		form.getFormFields().get("expectedUsage.txt").setValue((dmp.getExpectedUsage() == null) ? "" : dmp.getExpectedUsage()).setReadOnly(true);
		form.getFormFields().get("organizations.txt").setValue((dmp.getOrganizations() == null) ? "" : dmp.getOrganizations()).setReadOnly(true);
		form.getFormFields().get("involvedInstitutions.txt").setValue((dmp.getInvolvedInstitutions() == null) ? "" : dmp.getInvolvedInstitutions())
		    .setReadOnly(true);
		form.getFormFields().get("involvedInformed.txt").setValue(String.valueOf(dmp.isInvolvedInformed())).setReadOnly(true);
		form.getFormFields().get("contributionsDefined.txt").setValue(String.valueOf(dmp.isContributionsDefined())).setReadOnly(true);
		form.getFormFields().get("contributionsDefinedTxt.txt")
		    .setValue((dmp.getContributionsDefinedTxt() == null) ? "" : dmp.getContributionsDefinedTxt()).setReadOnly(true);
		form.getFormFields().get("givenConsent.txt").setValue(String.valueOf(dmp.isGivenConsent())).setReadOnly(true);
		form.getFormFields().get("depositName.txt").setValue((dmp.getDepositName() == null) ? "" : dmp.getDepositName()).setReadOnly(true);
		form.getFormFields().get("acquisitionAgreement.txt").setValue(String.valueOf(dmp.isAcquisitionAgreement())).setReadOnly(true);
		form.getFormFields().get("managementWorkflow.txt").setValue(String.valueOf(dmp.isManagementWorkflow())).setReadOnly(true);
		form.getFormFields().get("managementWorkflowTxt.txt").setValue((dmp.getManagementWorkflowTxt() == null) ? "" : dmp.getManagementWorkflowTxt())
		    .setReadOnly(true);
		form.getFormFields().get("grantNumber.txt").setValue((project.getGrantNumber() == null) ? "" : project.getGrantNumber()).setReadOnly(true);
		// form.getFormFields().get("projectSponsors.txt")
		// .setValue((dmp.getProjectSponsors() == null) ? "" : dmp.getProjectSponsors()).setReadOnly(true);
		form.getFormFields().get("projectName.txt").setValue("PRÜFEN").setColor(Color.RED).setReadOnly(true);
		form.getFormFields().get("existingDataRelevance.txt").setValue((dmp.getExistingDataRelevance() == null) ? "" : dmp.getExistingDataRelevance())
		    .setReadOnly(true);
		form.getFormFields().get("leader.txt").setValue("PRÜFEN").setColor(Color.RED).setReadOnly(true);
		form.getFormFields().get("funderRequirements.txt").setValue((dmp.getFunderRequirements() == null) ? "" : dmp.getFunderRequirements())
		    .setReadOnly(true);
		form.getFormFields().get("usedDataTypes.txt").setValue((dmp.getUsedDataTypes() == null) ? "" : String.valueOf(dmp.getUsedDataTypes()))
		    .setReadOnly(true);
		form.getFormFields().get("reliabilityTraining.txt").setValue((dmp.getReliabilityTraining() == null) ? "" : dmp.getReliabilityTraining())
		    .setReadOnly(true);
		form.getFormFields().get("multipleMeasurements.txt").setValue((dmp.getMultipleMeasurements() == null) ? "" : dmp.getMultipleMeasurements())
		    .setReadOnly(true);
		form.getFormFields().get("qualitityOther.txt").setValue((dmp.getQualitityOther() == null) ? "" : dmp.getQualitityOther()).setReadOnly(true);
		form.getFormFields().get("storageResponsible.txt").setValue((dmp.getStorageResponsible() == null) ? "" : dmp.getStorageResponsible())
		    .setReadOnly(true);
		form.getFormFields().get("storagePlaces.txt").setValue((dmp.getStoragePlaces() == null) ? "" : dmp.getStoragePlaces()).setReadOnly(true);
		form.getFormFields().get("storageBackups.txt").setValue((dmp.getStorageBackups() == null) ? "" : dmp.getStorageBackups()).setReadOnly(true);
		form.getFormFields().get("storageExpectedSize.txt").setValue((dmp.getStorageExpectedSize() == null) ? "" : dmp.getStorageExpectedSize())
		    .setReadOnly(true);
		// form.getFormFields().get("storageTechnologies")
		// .setValue((dmp.getStorageTechnologies() == null) ? "" : dmp.getStorageTechnologies()).setReadOnly(true);
		form.getFormFields().get("fileFormat.txt").setValue((dmp.getFileFormat() == null) ? "" : dmp.getFileFormat()).setReadOnly(true);
		form.getFormFields().get("storageRequirements.txt").setValue(String.valueOf(dmp.isStorageRequirements())).setReadOnly(true);
		form.getFormFields().get("storageRequirementsTxt.txt").setValue((dmp.getStorageRequirementsTxt() == null) ? "" : dmp.getStorageRequirementsTxt())
		    .setReadOnly(true);
		form.getFormFields().get("sensitiveDataIncluded.txt").setValue(String.valueOf(dmp.isSensitiveDataIncluded())).setReadOnly(true);
		form.getFormFields().get("sensitiveDataIncludedTxt.txt")
		    .setValue((dmp.getSensitiveDataIncludedTxt() == null) ? "" : dmp.getSensitiveDataIncludedTxt()).setReadOnly(true);
		form.getFormFields().get("metaDescription.txt").setValue((dmp.getMetaDescription() == null) ? "" : dmp.getMetaDescription()).setReadOnly(true);
		form.getFormFields().get("metaFramework.txt").setValue((dmp.getMetaFramework() == null) ? "" : dmp.getMetaFramework()).setReadOnly(true);
		form.getFormFields().get("selectedMetaPurposes.txt")
		    .setValue((dmp.getSelectedMetaPurposes() == null) ? "" : String.valueOf(dmp.getSelectedMetaPurposes())).setReadOnly(true);
		form.getFormFields().get("frameworkNationality.txt").setValue((dmp.getFrameworkNationality() == null) ? "" : dmp.getFrameworkNationality())
		    .setReadOnly(true);
		form.getFormFields().get("frameworkNationalityTxt.txt")
		    .setValue((dmp.getFrameworkNationalityTxt() == null) ? "" : dmp.getFrameworkNationalityTxt()).setReadOnly(true);
		form.getFormFields().get("internalCopyright.txt").setValue(String.valueOf(dmp.isInternalCopyright())).setReadOnly(true);
		form.getFormFields().get("internalCopyrightTxt.txt").setValue((dmp.getInternalCopyrightTxt() == null) ? "" : dmp.getInternalCopyrightTxt())
		    .setReadOnly(true);
		form.getFormFields().get("sensitiveData.txt").setValue((dmp.getSensitiveData() == null) ? "" : dmp.getSensitiveData()).setReadOnly(true);
		form.getFormFields().get("dataProtection.txt").setValue(String.valueOf(dmp.isDataProtection())).setReadOnly(true);
		form.getFormFields().get("protectionRequirements.txt").setValue((dmp.getProtectionRequirements() == null) ? "" : dmp.getProtectionRequirements())
		    .setReadOnly(true);
		form.getFormFields().get("consentObtained.txt").setValue(String.valueOf(dmp.isConsentObtained())).setReadOnly(true);
		form.getFormFields().get("consentObtainedTxt.txt").setValue((dmp.getConsentObtainedTxt() == null) ? "" : dmp.getConsentObtainedTxt())
		    .setReadOnly(true);
		form.getFormFields().get("sharingConsidered.txt").setValue(String.valueOf(dmp.isSharingConsidered())).setReadOnly(true);
		form.getFormFields().get("irbApproval.txt").setValue(String.valueOf(dmp.isIrbApproval())).setReadOnly(true);
		form.getFormFields().get("irbApprovalTxt.txt").setValue((dmp.getIrbApprovalTxt() == null) ? "" : dmp.getIrbApprovalTxt()).setReadOnly(true);
		form.getFormFields().get("publStrategy.txt").setValue((dmp.getPublStrategy() == null) ? "" : dmp.getPublStrategy()).setReadOnly(true);
		// form.getFormFields().get("searchableData.txt").setValue(String.valueOf(dmp.isSearchableData()))
		// .setReadOnly(true);
		form.getFormFields().get("transferTime.txt").setValue((dmp.getTransferTime() == null) ? "" : dmp.getTransferTime()).setReadOnly(true);
		form.getFormFields().get("accessReasonAuthor.txt").setValue((dmp.getAccessReasonAuthor() == null) ? "" : dmp.getAccessReasonAuthor())
		    .setReadOnly(true);
		form.getFormFields().get("noAccessReason.txt").setValue((dmp.getNoAccessReason() == null) ? "" : dmp.getNoAccessReason()).setReadOnly(true);
		form.getFormFields().get("initialUsage.txt").setValue((dmp.getInitialUsage() == null) ? "" : dmp.getInitialUsage()).setReadOnly(true);
		form.getFormFields().get("usageRestriction.txt").setValue((dmp.getUsageRestriction() == null) ? "" : dmp.getUsageRestriction()).setReadOnly(true);
		form.getFormFields().get("accessCosts.txt").setValue(String.valueOf(dmp.isAccessCosts())).setReadOnly(true);
		// form.getFormFields().get("accessCostsTxt.txt")
		// .setValue((dmp.getAccessCostsTxt() == null) ? "" : dmp.getAccessCostsTxt()).setReadOnly(true);
		form.getFormFields().get("usedPID.txt").setValue((dmp.getUsedPID() == null) ? "" : dmp.getUsedPID()).setReadOnly(true);
		form.getFormFields().get("dataSelection.txt").setValue(String.valueOf(dmp.isDataSelection())).setReadOnly(true);
		form.getFormFields().get("selectionTime.txt").setValue((dmp.getSelectionTime() == null) ? "" : dmp.getSelectionTime()).setReadOnly(true);
		form.getFormFields().get("selectionResp.txt").setValue((dmp.getSelectionResp() == null) ? "" : dmp.getSelectionResp()).setReadOnly(true);
		form.getFormFields().get("storageDuration.txt").setValue((dmp.getStorageDuration() == null) ? "" : dmp.getStorageDuration()).setReadOnly(true);
		form.getFormFields().get("storageSuccession.txt").setValue(String.valueOf(dmp.isStorageSuccession())).setReadOnly(true);
		form.getFormFields().get("storageSuccessionTxt.txt").setValue((dmp.getStorageSuccessionTxt() == null) ? "" : dmp.getStorageSuccessionTxt())
		    .setReadOnly(true);
		form.getFormFields().get("responsibleUnit.txt").setValue((dmp.getResponsibleUnit() == null) ? "" : dmp.getResponsibleUnit()).setReadOnly(true);
	}

	public void manipulatePdf3(PdfAcroForm form, StudyDTO study) {
		Map<String, PdfFormField> fields = form.getFormFields();
		for (String name : fields.keySet()) {
			try {
				PdfFormField tf = fields.get(name);
				/*
				 * if (name.startsWith("dmp.") && dmpp.getProperty(name) != null) { tf.setValue(dmpp.getProperty(name)); } else
				 */ if (name.startsWith("study.") && studyp.getProperty(name) != null) {
					tf.setValue(studyp.getProperty(name));
				} /*
				   * else if (name.startsWith("project.") && projectp.getProperty(name) != null) { tf.setValue(projectp.getProperty(name)); }
				   */
				tf.setReadOnly(true);
			} catch (Exception e) {
				System.err.println("Exception creating field: " + name);
			}
		}
		form.getFormFields().get("studytitle.txt").setValue((study.getTitle() == null) ? "" : study.getTitle()).setReadOnly(true);
		form.getFormFields().get("transTitle.txt").setValue((study.getTransTitle() == null) ? "" : study.getTransTitle()).setReadOnly(true);
		PdfFormField tf01 = form.getFormFields().get("contributors.txt").setValue("");
		for (ContributorDTO cntrbtr : study.getContributors()) {
			tf01.setValue(tf01.getValueAsString() + cntrbtr.getFirstName() + " " + cntrbtr.getLastName() + ". ");
		}
		form.getFormFields().get("sAbstract.txt").setValue((study.getsAbstract() == null) ? "" : study.getsAbstract()).setReadOnly(true);
		form.getFormFields().get("sAbstrctTrans.txt").setValue((study.getsAbstractTrans() == null) ? "" : study.getsAbstractTrans()).setReadOnly(true);
		form.getFormFields().get("completeSel.txt").setValue((study.getCompleteSel() == null) ? "" : study.getCompleteSel()).setReadOnly(true);
		form.getFormFields().get("excerpt.txt").setValue((study.getExcerpt() == null) ? "" : study.getExcerpt()).setReadOnly(true);
		form.getFormFields().get("pubOnData.txt").setValue((study.getPubOnData() == null) ? "" : String.valueOf(study.getPubOnData())).setReadOnly(true);
		form.getFormFields().get("objectives.txt").setValue((study.getObjectives() == null) ? "" : String.valueOf(study.getObjectives()))
		    .setReadOnly(true);
		form.getFormFields().get("repMeasures.txt").setValue((study.getRepMeasures() == null) ? "" : study.getRepMeasures()).setReadOnly(true);
		form.getFormFields().get("timeDim.txt").setValue((study.getTimeDim() == null) ? "" : study.getTimeDim()).setReadOnly(true);
		form.getFormFields().get("intervention.txt").setValue("Exp: " + String.valueOf(study.isExperimentalIntervention()) + "; Surv: "
		    + String.valueOf(study.isSurveyIntervention()) + "; Test: " + String.valueOf(study.isTestIntervention())).setReadOnly(true);
		form.getFormFields().get("interTypeEx.txt").setValue((study.getInterTypeExp() == null) ? "" : study.getInterTypeExp()).setReadOnly(true);
		form.getFormFields().get("interTypeDes.txt").setValue((study.getInterTypeDes() == null) ? "" : study.getInterTypeDes()).setReadOnly(true);
		form.getFormFields().get("interTypeLab.txt").setValue((study.getInterTypeLab() == null) ? "" : study.getInterTypeLab()).setReadOnly(true);
		form.getFormFields().get("randomization.txt").setValue((study.getRandomization() == null) ? "" : study.getRandomization()).setReadOnly(true);
		PdfFormField tf02 = form.getFormFields().get("measOcc.txt").setValue("");
		PdfFormField tf03 = form.getFormFields().get("time.txt").setValue("");
		PdfFormField tf04 = form.getFormFields().get("sort.txt").setValue("");
		for (StudyListTypesDTO occ : study.getMeasOcc()) {
			tf02.setValue(tf02.getValueAsString() + occ.getText() + ". ");
			tf03.setValue(tf03.getValueAsString() + String.valueOf(occ.isTimetable()) + ". ");
			tf04.setValue(tf04.getValueAsString() + occ.getSort() + ". ");
		}
		form.getFormFields().get("surveyType.txt").setValue((study.getSurveyType() == null) ? "" : study.getSurveyType()).setReadOnly(true);
		PdfFormField tf05 = form.getFormFields().get("name.txt").setValue("");
		PdfFormField tf06 = form.getFormFields().get("type.txt").setValue("");
		PdfFormField tf07 = form.getFormFields().get("other.txt").setValue("");
		for (StudyConstructDTO cnstrct : study.getConstructs()) {
			tf05.setValue(tf05.getValueAsString() + cnstrct.getName() + ". ");
			tf06.setValue(tf06.getValueAsString() + cnstrct.getType() + ". ");
			tf07.setValue(tf07.getValueAsString() + cnstrct.getOther() + ". ");
		}
		form.getFormFields().get("instruments.txt").setValue((study.getInstruments() == null) ? "" : String.valueOf(study.getInstruments()))
		    .setReadOnly(true);
		form.getFormFields().get("instrument.txt").setValue("").setReadOnly(true);
		PdfFormField tf08 = form.getFormFields().get("title.txt").setValue("");
		PdfFormField tf09 = form.getFormFields().get("author.txt").setValue("");
		PdfFormField tf10 = form.getFormFields().get("citation.txt").setValue("");
		PdfFormField tf11 = form.getFormFields().get("summary.txt").setValue("");
		PdfFormField tf12 = form.getFormFields().get("theoHint.txt").setValue("");
		PdfFormField tf13 = form.getFormFields().get("structure.txt").setValue("");
		PdfFormField tf14 = form.getFormFields().get("construction.txt").setValue("");
		PdfFormField tf15 = form.getFormFields().get("objectivity.txt").setValue("");
		PdfFormField tf16 = form.getFormFields().get("reliability.txt").setValue("");
		PdfFormField tf17 = form.getFormFields().get("validity.txt").setValue("");
		PdfFormField tf18 = form.getFormFields().get("norm.txt").setValue("");
		for (StudyInstrumentDTO instr : study.getInstruments()) {
			tf08.setValue(tf08.getValueAsString() + instr.getTitle() + ". ");
			tf09.setValue(tf09.getValueAsString() + instr.getAuthor() + ". ");
			tf10.setValue(tf10.getValueAsString() + instr.getCitation() + ". ");
			tf11.setValue(tf11.getValueAsString() + instr.getSummary() + ". ");
			tf12.setValue(tf12.getValueAsString() + instr.getTheoHint() + ". ");
			tf13.setValue(tf13.getValueAsString() + instr.getStructure() + ". ");
			tf14.setValue(tf14.getValueAsString() + instr.getConstruction() + ". ");
			tf15.setValue(tf15.getValueAsString() + instr.getObjectivity() + ". ");
			tf16.setValue(tf16.getValueAsString() + instr.getReliability() + ". ");
			tf17.setValue(tf17.getValueAsString() + instr.getValidity() + ". ");
			tf18.setValue(tf18.getValueAsString() + instr.getNorm() + ". ");
		}
		form.getFormFields().get("description.txt").setValue((study.getDescription() == null) ? "" : study.getDescription()).setReadOnly(true);
		PdfFormField tf19 = form.getFormFields().get("eligibilities.txt").setValue("");
		for (StudyListTypesDTO eli : study.getEligibilities()) {
			tf19.setValue(tf19.getValueAsString() + eli.toString() + ". ");
		}
		form.getFormFields().get("population.txt").setValue((study.getPopulation() == null) ? "" : study.getPopulation()).setReadOnly(true);
		form.getFormFields().get("sampleSize.txt").setValue((study.getSampleSize() == null) ? "" : study.getSampleSize()).setReadOnly(true);
		form.getFormFields().get("obsUnit.txt").setValue((study.getObsUnit() == null) ? "" : study.getObsUnit()).setReadOnly(true);
		form.getFormFields().get("sex.txt").setValue((study.getSex() == null) ? "" : study.getSex()).setReadOnly(true);
		form.getFormFields().get("age.txt").setValue((study.getAge() == null) ? "" : study.getAge()).setReadOnly(true);
		form.getFormFields().get("specGroups.txt").setValue((study.getSpecGroups() == null) ? "" : study.getSpecGroups()).setReadOnly(true);
		form.getFormFields().get("country.txt").setValue((study.getCountry() == null) ? "" : study.getCountry()).setReadOnly(true);
		form.getFormFields().get("city.txt").setValue((study.getCity() == null) ? "" : study.getCity()).setReadOnly(true);
		form.getFormFields().get("region.txt").setValue((study.getRegion() == null) ? "" : study.getRegion()).setReadOnly(true);
		form.getFormFields().get("dataRerun.txt").setValue((study.getDataRerun() == null) ? "" : study.getDataRerun()).setReadOnly(true);
		form.getFormFields().get("responsibility.txt").setValue((study.getResponsibility() == null) ? "" : study.getResponsibility()).setReadOnly(true);
		form.getFormFields().get("collStart.txt").setValue(String.valueOf(study.getCollStart())).setReadOnly(true);
		form.getFormFields().get("collEnd.txt").setValue(String.valueOf(study.getCollEnd())).setReadOnly(true);
		form.getFormFields().get("usedcollectionModes.txt")
		    .setValue((study.getUsedCollectionModes() == null) ? "" : String.valueOf(study.getUsedCollectionModes())).setReadOnly(true);
		form.getFormFields().get("sampMethod.txt").setValue((study.getSampMethod() == null) ? "" : study.getSampMethod()).setReadOnly(true);
		form.getFormFields().get("recruiting.txt").setValue((study.getRecruiting() == null) ? "" : study.getRecruiting()).setReadOnly(true);
		form.getFormFields().get("usedSourFormat.txt").setValue((study.getUsedSourFormat() == null) ? "" : String.valueOf(study.getUsedSourFormat()))
		    .setReadOnly(true);
		form.getFormFields().get("sourTrans.txt").setValue((study.getSourTrans() == null) ? "" : study.getSourTrans()).setReadOnly(true);
		form.getFormFields().get("specCirc.txt").setValue((study.getSpecCirc() == null) ? "" : study.getSpecCirc()).setReadOnly(true);
		form.getFormFields().get("transDescr.txt").setValue((study.getTransDescr() == null) ? "" : study.getTransDescr()).setReadOnly(true);
		form.getFormFields().get("qualInd.txt").setValue((study.getQualInd() == null) ? "" : study.getQualInd()).setReadOnly(true);
		form.getFormFields().get("qualLim.txt").setValue((study.getQualLim() == null) ? "" : study.getQualLim()).setReadOnly(true);
		form.getFormFields().get("persDataColl.txt").setValue(String.valueOf(study.isPersDataColl())).setReadOnly(true);
		form.getFormFields().get("persDataPres.txt").setValue((study.getPersDataPres() == null) ? "" : study.getPersDataPres()).setReadOnly(true);
		form.getFormFields().get("anonymProc.txt").setValue((study.getAnonymProc() == null) ? "" : study.getAnonymProc()).setReadOnly(true);
		form.getFormFields().get("copyright.txt").setValue(String.valueOf(study.isCopyright())).setReadOnly(true);
		form.getFormFields().get("copyrightHolder.txt").setValue((study.getCopyrightHolder() == null) ? "" : study.getCopyrightHolder())
		    .setReadOnly(true);
	}

	public void manipulatePdf4(PdfAcroForm form, DmpDTO dmp) {
		Map<String, PdfFormField> fields = form.getFormFields();
		for (String name : fields.keySet()) {
			try {
				PdfFormField tf = fields.get(name);
				if (name.startsWith("dmp.") && dmpp.getProperty(name) != null) {
					tf.setValue(dmpp.getProperty(name));
				} /*
				   * else if (name.startsWith("study.") && studyp.getProperty(name) != null) { tf.setValue(studyp.getProperty(name)); } else if
				   * (name.startsWith("project.") && projectp.getProperty(name) != null) { tf.setValue(projectp.getProperty(name)); }
				   */
				tf.setReadOnly(true);
			} catch (Exception e) {
				System.err.println("Exception creating field: " + name);
			}
		}
		form.getFormFields().get("storageHeadline.txt").setValue("PRÜFEN").setColor(Color.RED).setReadOnly(true);
		form.getFormFields().get("dataReproducibility.txt").setValue((dmp.getDataReproducibility() == null) ? "" : dmp.getDataReproducibility())
		    .setReadOnly(true);
		form.getFormFields().get("expectedUsage.txt").setValue((dmp.getExpectedUsage() == null) ? "" : dmp.getExpectedUsage()).setReadOnly(true);
		form.getFormFields().get("publStrategy.txt").setValue((dmp.getPublStrategy() == null) ? "" : dmp.getPublStrategy()).setReadOnly(true);
		// form.getFormFields().get("searchableData.txt").setValue(String.valueOf(dmp.isSearchableData()))
		// .setReadOnly(true);
		form.getFormFields().get("initialUsage.txt").setValue((dmp.getInitialUsage() == null) ? "" : dmp.getInitialUsage()).setReadOnly(true);
		form.getFormFields().get("usageRestriction.txt").setValue((dmp.getUsageRestriction() == null) ? "" : dmp.getUsageRestriction()).setReadOnly(true);
		form.getFormFields().get("accessCosts.txt").setValue(String.valueOf(dmp.isAccessCosts())).setReadOnly(true);
		// form.getFormFields().get("accessCostsTxt.txt")
		// .setValue((dmp.getAccessCostsTxt() == null) ? "" : dmp.getAccessCostsTxt()).setReadOnly(true);
		form.getFormFields().get("accessReasonAuthor.txt").setValue((dmp.getAccessReasonAuthor() == null) ? "" : dmp.getAccessReasonAuthor())
		    .setReadOnly(true);
		form.getFormFields().get("noAccessReason.txt").setValue((dmp.getNoAccessReason() == null) ? "" : dmp.getNoAccessReason()).setReadOnly(true);
		// form.getFormFields().get("projectAims.txt").setValue((dmp.getProjectAims() == null) ? "" : dmp.getProjectAims())
		// .setReadOnly(true);
		form.getFormFields().get("usedDataTypes.txt").setValue((dmp.getUsedDataTypes() == null) ? "" : String.valueOf(dmp.getUsedDataTypes()))
		    .setReadOnly(true);
		form.getFormFields().get("existingData.txt").setValue((dmp.getExistingData() == null) ? "" : dmp.getExistingData()).setReadOnly(true);
		form.getFormFields().get("dataCitation.txt").setValue((dmp.getDataCitation() == null) ? "" : dmp.getDataCitation()).setReadOnly(true);
		form.getFormFields().get("usedCollectionModes.txt")
		    .setValue((dmp.getUsedCollectionModes() == null) ? "" : String.valueOf(dmp.getUsedCollectionModes())).setReadOnly(true);
		form.getFormFields().get("qualityAssurance.txt").setValue("PRÜFEN").setColor(Color.RED).setReadOnly(true);
		form.getFormFields().get("reliabilityTraining.txt").setValue((dmp.getReliabilityTraining() == null) ? "" : dmp.getReliabilityTraining())
		    .setReadOnly(true);
		form.getFormFields().get("multipleMeasurements.txt").setValue((dmp.getMultipleMeasurements() == null) ? "" : dmp.getMultipleMeasurements())
		    .setReadOnly(true);
		form.getFormFields().get("qualitityOther.txt").setValue((dmp.getQualitityOther() == null) ? "" : dmp.getQualitityOther()).setReadOnly(true);
		form.getFormFields().get("planningAdherence.txt").setValue((dmp.getPlanningAdherence() == null) ? "" : dmp.getPlanningAdherence())
		    .setReadOnly(true);
		form.getFormFields().get("exisitingDataRelevance.txt").setValue((dmp.getExistingDataRelevance() == null) ? "" : dmp.getExistingDataRelevance())
		    .setReadOnly(true);
		form.getFormFields().get("existingDataIntegration.txt")
		    .setValue((dmp.getExistingDataIntegration() == null) ? "" : dmp.getExistingDataIntegration()).setReadOnly(true);
		form.getFormFields().get("dataSelection.txt").setValue(String.valueOf(dmp.isDataSelection())).setReadOnly(true);
		form.getFormFields().get("selectionTime.txt").setValue((dmp.getSelectionTime() == null) ? "" : dmp.getSelectionTime()).setReadOnly(true);
		form.getFormFields().get("selectionResp.txt").setValue((dmp.getSelectionResp() == null) ? "" : dmp.getSelectionResp()).setReadOnly(true);
		form.getFormFields().get("deleteProcedure.txt").setValue((dmp.getDeleteProcedure() == null) ? "" : dmp.getDeleteProcedure()).setReadOnly(true);
		form.getFormFields().get("storagePlaces.txt").setValue((dmp.getStoragePlaces() == null) ? "" : dmp.getStoragePlaces()).setReadOnly(true);
		form.getFormFields().get("storageBackups.txt").setValue((dmp.getStorageBackups() == null) ? "" : dmp.getStorageBackups()).setReadOnly(true);
		form.getFormFields().get("managementWorkflow.txt").setValue(String.valueOf(dmp.isManagementWorkflow())).setReadOnly(true);
		form.getFormFields().get("managementWorkflowTxt.txt").setValue((dmp.getManagementWorkflowTxt() == null) ? "" : dmp.getManagementWorkflowTxt())
		    .setReadOnly(true);
		form.getFormFields().get("funderRequirements.txt").setValue((dmp.getFunderRequirements() == null) ? "" : dmp.getFunderRequirements())
		    .setReadOnly(true);
		form.getFormFields().get("storageResponsible.txt").setValue((dmp.getStorageResponsible() == null) ? "" : dmp.getStorageResponsible())
		    .setReadOnly(true);
		form.getFormFields().get("storageDuration").setValue((dmp.getStorageDuration() == null) ? "" : dmp.getStorageDuration()).setReadOnly(true);
		form.getFormFields().get("sensitiveData.txt").setValue((dmp.getSensitiveData() == null) ? "" : dmp.getSensitiveData()).setReadOnly(true);
		form.getFormFields().get("clarifiedRights.txt").setValue(String.valueOf(dmp.isClarifiedRights())).setReadOnly(true);
		form.getFormFields().get("usedPID.txt").setValue((dmp.getUsedPID() == null) ? "" : dmp.getUsedPID()).setReadOnly(true);
		form.getFormFields().get("fileFormat.txt").setValue((dmp.getFileFormat() == null) ? "" : dmp.getFileFormat()).setReadOnly(true);
		form.getFormFields().get("metaFramework.txt").setValue((dmp.getMetaFramework() == null) ? "" : dmp.getMetaFramework()).setReadOnly(true);
		form.getFormFields().get("depositName.txt").setValue((dmp.getDepositName() == null) ? "" : dmp.getDepositName()).setReadOnly(true);
		form.getFormFields().get("acquisitionAgreement.txt").setValue(String.valueOf(dmp.isAcquisitionAgreement())).setReadOnly(true);
		form.getFormFields().get("dataProtection.txt").setValue(String.valueOf(dmp.isDataProtection())).setReadOnly(true);
		form.getFormFields().get("protectionRequirements.txt").setValue((dmp.getProtectionRequirements() == null) ? "" : dmp.getProtectionRequirements())
		    .setReadOnly(true);
		form.getFormFields().get("consentObtained.txt").setValue(String.valueOf(dmp.isConsentObtained())).setReadOnly(true);
		form.getFormFields().get("consentObtainedTxt.txt").setValue((dmp.getConsentObtainedTxt() == null) ? "" : dmp.getConsentObtainedTxt())
		    .setReadOnly(true);
		form.getFormFields().get("sharingConsidered.txt").setValue(String.valueOf(dmp.isSharingConsidered())).setReadOnly(true);
		form.getFormFields().get("irbApproval.txt").setValue(String.valueOf(dmp.isIrbApproval())).setReadOnly(true);
		form.getFormFields().get("irbApprovalTxt.txt").setValue((dmp.getIrbApprovalTxt() == null) ? "" : dmp.getIrbApprovalTxt()).setReadOnly(true);
		form.getFormFields().get("sensitiveDataIncluded.txt").setValue(String.valueOf(dmp.isSensitiveDataIncluded())).setReadOnly(true);
		form.getFormFields().get("externalCopyright.txt").setValue(String.valueOf(dmp.isExternalCopyright())).setReadOnly(true);
		form.getFormFields().get("externalCopyrightTxt.txt").setValue((dmp.getExternalCopyrightTxt() == null) ? "" : dmp.getExternalCopyrightTxt())
		    .setReadOnly(true);
		form.getFormFields().get("internalCopyright.txt").setValue(String.valueOf(dmp.isInternalCopyright())).setReadOnly(true);
		form.getFormFields().get("internalCopyrightTxt.txt").setValue((dmp.getInternalCopyrightTxt() == null) ? "" : dmp.getInternalCopyrightTxt())
		    .setReadOnly(true);
		form.getFormFields().get("transferTime.txt").setValue((dmp.getTransferTime() == null) ? "" : dmp.getTransferTime()).setReadOnly(true);
		form.getFormFields().get("specificCosts.txt").setValue((dmp.getSpecificCosts() == null) ? "" : dmp.getSpecificCosts()).setReadOnly(true);
	}

	public void manipulatePdf5(PdfAcroForm form, StudyDTO study) {
		Map<String, PdfFormField> fields = form.getFormFields();
		for (String name : fields.keySet()) {
			try {
				PdfFormField tf = fields.get(name);
				/*
				 * if (name.startsWith("dmp.") && dmpp.getProperty(name) != null) { tf.setValue(dmpp.getProperty(name)); } else
				 */ if (name.startsWith("study.") && studyp.getProperty(name) != null) {
					tf.setValue(studyp.getProperty(name));
				} /*
				   * else if (name.startsWith("project.") && projectp.getProperty(name) != null) { tf.setValue(projectp.getProperty(name)); }
				   */
				tf.setReadOnly(true);
			} catch (Exception e) {
				System.err.println("Exception creating field: " + name);
			}
		}
		form.getFormFields().get("studytitle.txt").setValue((study.getTitle() == null) ? "" : study.getTitle()).setReadOnly(true);
		form.getFormFields().get("transTitle.txt").setValue((study.getTransTitle() == null) ? "" : study.getTransTitle()).setReadOnly(true);
		form.getFormFields().get("sAbstract.txt").setValue((study.getsAbstract() == null) ? "" : study.getsAbstract()).setReadOnly(true);
		form.getFormFields().get("sAbstractTrans.txt").setValue((study.getsAbstractTrans() == null) ? "" : study.getsAbstractTrans()).setReadOnly(true);
		form.getFormFields().get("prevWork.txt").setValue((study.getPrevWork() == null) ? "" : study.getPrevWork()).setReadOnly(true);
		form.getFormFields().get("prevWorkStr.txt").setValue((study.getPrevWorkStr() == null) ? "" : study.getPrevWorkStr()).setReadOnly(true);
		PdfFormField tf01 = form.getFormFields().get("objectives.txt").setValue("");
		for (StudyListTypesDTO obj : study.getObjectives()) {
			tf01.setValue(tf01.getValueAsString() + obj.getText() + " - " + obj.getObjectivetype() + ". ");
		}
		PdfFormField tf02 = form.getFormFields().get("relTheorys.txt").setValue("");
		for (StudyListTypesDTO rel : study.getRelTheorys()) {
			tf02.setValue(tf02.getValueAsString() + rel.getText() + ". ");
		}
		form.getFormFields().get("repMeasures.txt").setValue((study.getRepMeasures() == null) ? "" : study.getRepMeasures()).setReadOnly(true);
		form.getFormFields().get("timeDim.txt").setValue((study.getTimeDim() == null) ? "" : study.getTimeDim()).setReadOnly(true);
		form.getFormFields().get("intervention.txt").setValue("Exp: " + String.valueOf(study.isExperimentalIntervention()) + "; Surv: "
		    + String.valueOf(study.isSurveyIntervention()) + "; Test: " + String.valueOf(study.isTestIntervention())).setReadOnly(true);
		form.getFormFields().get("interTypeEx.txt").setValue((study.getInterTypeExp() == null) ? "" : study.getInterTypeExp()).setReadOnly(true);
		form.getFormFields().get("interTypeDes.txt").setValue((study.getInterTypeDes() == null) ? "" : study.getInterTypeDes()).setReadOnly(true);
		form.getFormFields().get("interTypeLab.txt").setValue((study.getInterTypeLab() == null) ? "" : study.getInterTypeLab()).setReadOnly(true);
		form.getFormFields().get("randomization.txt").setValue((study.getRandomization() == null) ? "" : study.getRandomization()).setReadOnly(true);
		PdfFormField tf03 = form.getFormFields().get("interArms.txt").setValue("");
		for (StudyListTypesDTO arm : study.getInterArms()) {
			tf03.setValue(tf03.getValueAsString() + arm.getText() + ". ");
		}
		form.getFormFields().get("measOcc.txt").setValue("").setReadOnly(true);
		PdfFormField tf04 = form.getFormFields().get("time.txt").setValue("");
		PdfFormField tf05 = form.getFormFields().get("dim.txt").setValue("");
		PdfFormField tf06 = form.getFormFields().get("sort.txt").setValue("");
		for (StudyListTypesDTO moc : study.getMeasOcc()) {
			tf04.setValue(tf04.getValueAsString() + moc.getText() + ". ");
			tf05.setValue(tf05.getValueAsString() + String.valueOf(moc.isTimetable()) + ". ");
			tf06.setValue(tf06.getValueAsString() + moc.getSort() + ". ");
		}
		form.getFormFields().get("surveyType.txt").setValue((study.getSurveyType() == null) ? "" : study.getSurveyType()).setReadOnly(true);
		PdfFormField tf07 = form.getFormFields().get("name.txt").setValue("");
		PdfFormField tf08 = form.getFormFields().get("type.txt").setValue("");
		PdfFormField tf09 = form.getFormFields().get("other.txt").setValue("");
		for (StudyConstructDTO cnstrct : study.getConstructs()) {
			tf07.setValue(tf07.getValueAsString() + cnstrct.getName() + ". ");
			tf08.setValue(tf08.getValueAsString() + cnstrct.getType() + ". ");
			tf09.setValue(tf09.getValueAsString() + cnstrct.getOther() + ". ");
		}
		form.getFormFields().get("instruments.txt").setValue((study.getInstruments() == null) ? "" : String.valueOf(study.getInstruments()))
		    .setReadOnly(true);
		form.getFormFields().get("instrument.txt").setValue("").setReadOnly(true);
		PdfFormField tf10 = form.getFormFields().get("title.txt").setValue("");
		PdfFormField tf11 = form.getFormFields().get("author.txt").setValue("");
		PdfFormField tf12 = form.getFormFields().get("citation.txt").setValue("");
		PdfFormField tf13 = form.getFormFields().get("summary.txt").setValue("");
		PdfFormField tf14 = form.getFormFields().get("theoHint.txt").setValue("");
		PdfFormField tf15 = form.getFormFields().get("structure.txt").setValue("");
		PdfFormField tf16 = form.getFormFields().get("construction.txt").setValue("");
		PdfFormField tf17 = form.getFormFields().get("objectivity.txt").setValue("");
		PdfFormField tf18 = form.getFormFields().get("reliability.txt").setValue("");
		PdfFormField tf19 = form.getFormFields().get("validity.txt").setValue("");
		PdfFormField tf20 = form.getFormFields().get("norm.txt").setValue("");
		for (StudyInstrumentDTO instr : study.getInstruments()) {
			tf10.setValue(tf10.getValueAsString() + instr.getTitle() + ". ");
			tf11.setValue(tf11.getValueAsString() + instr.getAuthor() + ". ");
			tf12.setValue(tf12.getValueAsString() + instr.getCitation() + ". ");
			tf13.setValue(tf13.getValueAsString() + instr.getSummary() + ". ");
			tf14.setValue(tf14.getValueAsString() + instr.getTheoHint() + ". ");
			tf15.setValue(tf15.getValueAsString() + instr.getStructure() + ". ");
			tf16.setValue(tf16.getValueAsString() + instr.getConstruction() + ". ");
			tf17.setValue(tf17.getValueAsString() + instr.getObjectivity() + ". ");
			tf18.setValue(tf18.getValueAsString() + instr.getReliability() + ". ");
			tf19.setValue(tf19.getValueAsString() + instr.getValidity() + ". ");
			tf20.setValue(tf20.getValueAsString() + instr.getNorm() + ". ");
		}
		form.getFormFields().get("description.txt").setValue((study.getDescription() == null) ? "" : study.getDescription()).setReadOnly(true);
		PdfFormField tf21 = form.getFormFields().get("eligibilities.txt").setValue("");
		for (StudyListTypesDTO eli : study.getEligibilities()) {
			tf21.setValue(tf21.getValueAsString() + eli.toString() + ". ");
		}
		form.getFormFields().get("population.txt").setValue((study.getPopulation() == null) ? "" : study.getPopulation()).setReadOnly(true);
		form.getFormFields().get("powerAnalysis.txt").setValue((study.getPowerAnalysis() == null) ? "" : study.getPowerAnalysis()).setReadOnly(true);
		form.getFormFields().get("intSampleSize.txt").setValue((study.getIntSampleSize() == null) ? "" : study.getIntSampleSize()).setReadOnly(true);
		form.getFormFields().get("obsUnit.txt").setValue((study.getObsUnit() == null) ? "" : study.getObsUnit()).setReadOnly(true);
		form.getFormFields().get("multilevel.txt").setValue((study.getMultilevel() == null) ? "" : study.getMultilevel()).setReadOnly(true);
		form.getFormFields().get("country.txt").setValue((study.getCountry() == null) ? "" : study.getCountry()).setReadOnly(true);
		form.getFormFields().get("city.txt").setValue((study.getCity() == null) ? "" : study.getCity()).setReadOnly(true);
		form.getFormFields().get("region.txt").setValue((study.getRegion() == null) ? "" : study.getRegion()).setReadOnly(true);
		form.getFormFields().get("missings.txt").setValue((study.getMissings() == null) ? "" : study.getMissings()).setReadOnly(true);
		form.getFormFields().get("dataRerun.txt").setValue((study.getDataRerun() == null) ? "" : study.getDataRerun()).setReadOnly(true);
		form.getFormFields().get("usedCollectionModes.txt")
		    .setValue((study.getUsedCollectionModes() == null) ? "" : String.valueOf(study.getUsedCollectionModes())).setReadOnly(true);
		form.getFormFields().get("sampMethod.txt").setValue((study.getSampMethod() == null) ? "" : study.getSampMethod()).setReadOnly(true);
		form.getFormFields().get("recruiting.txt").setValue((study.getRecruiting() == null) ? "" : study.getRecruiting()).setReadOnly(true);
		form.getFormFields().get("sourTrans.txt").setValue((study.getSourTrans() == null) ? "" : study.getSourTrans()).setReadOnly(true);
		form.getFormFields().get("specCirc.txt").setValue((study.getSpecCirc() == null) ? "" : study.getSpecCirc()).setReadOnly(true);
	}

}
