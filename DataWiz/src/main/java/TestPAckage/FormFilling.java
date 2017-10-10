package TestPAckage;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ProjectDTO;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class FormFilling {
	public static final String SRC1 = "/home/ronny/Downloads/dwpdf/20170616Zuordnung_H2020_icon_final_fuer_JH.pdf";
	// public static final String SRC2 = "Zuordnung_PDF/20170725_Zuordnung_BMBF_AZA_Checklistefuer_JH.pdf";
	// public static final String SRC3 = "Zuordnung_PDF/20170828_Zuordnung_PsychData_fuer_JH.pdf";
	// public static final String SRC4 = "Zuordnung_PDF/Zuordnung_dfg_Leitlinien_icon_final_fuer_JH.pdf";
	// public static final String SRC5 = "Zuordnung_PDF/Zuordnung_Praeregistrierung_fuer_JH.pdf";
	public static final String DEST1 = "/home/ronny/Downloads/dwpdf/test.pdf";

	Properties studyp = null;
	Properties dmpp = null;
	Properties projectp = null;

	// die properties einfach im Constructor laden und als globale variablen setzen, erspart dir die Übergabe bei den Funktionen
	public FormFilling() {
		BufferedInputStream bis;
		try {
			this.studyp = new Properties();
			bis = new BufferedInputStream(new FileInputStream("/home/ronny/Downloads/dwpdf/StudyResources_de.properties"));
			this.studyp.load(bis);
			bis.close();
			this.dmpp = new Properties();
			bis = new BufferedInputStream(new FileInputStream("/home/ronny/Downloads/dwpdf/DMPResources_de.properties"));
			this.dmpp.load(bis);
			bis.close();
			this.projectp = new Properties();
			bis = new BufferedInputStream(new FileInputStream("/home/ronny/Downloads/dwpdf/ApplicationResources_de.properties"));
			this.projectp.load(bis);
			bis.close();
		} catch (Exception e) {
			System.err.println("Error reading properties: " + e.getMessage());
			System.exit(0);
		}

	}

	public static void main(String[] args) {

		FormFilling ff = new FormFilling();

		/**
		 * Anfang Project und DMP füllen -> mir fehlen deine Klassen
		 */
		ProjectDTO project = new ProjectDTO();
		project.setDescription(ff.createRandomString(1000));
		DmpDTO dmp = new DmpDTO();
		dmp.setExistingDataRelevance(ff.createRandomString(300));
		List<Integer> usedData = new ArrayList<>();
		usedData.add(234);
		usedData.add(23);
		usedData.add(34);
		usedData.add(4);
		dmp.setUsedDataTypes(usedData);
		dmp.setFileFormat(ff.createRandomString(300));
		dmp.setExistingData(ff.createRandomString(300));
		dmp.setDataCitation(ff.createRandomString(300));

		/**
		 * Ende Project und DMP füllen
		 */

		File file = new File(DEST1);
		file.getParentFile().mkdirs();
		PdfReader reader = null;
		PdfDocument pdf = null;
		try {
			reader = new PdfReader(SRC1);
			pdf = new PdfDocument(reader, new PdfWriter(DEST1));
		} catch (IOException e) {
			System.err.println("Error creating PDF-DOC: " + e.getMessage());
			System.exit(0);
		}
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
		ff.manipulatePdf1(form, dmp, project);
		pdf.close();
	}

	// hab die übergaben mal minimiert
	public void manipulatePdf1(PdfAcroForm form, DmpDTO dmp, ProjectDTO project) {
		Map<String, PdfFormField> fields = form.getFormFields();
		for (String name : fields.keySet()) {
			try {
				PdfFormField tf = fields.get(name);
				if (name.startsWith("dmp.") && dmpp.getProperty(name) != null) {
					tf.setValue(dmpp.getProperty(name));
				} else if (name.startsWith("study.") && studyp.getProperty(name) != null) {
					tf.setValue(studyp.getProperty(name));
				} else if (name.startsWith("project.") && projectp.getProperty(name) != null) {
					tf.setValue(projectp.getProperty(name));
				}
				tf.setReadOnly(true);
			} catch (Exception e) {
				System.err.println("Exception creating field: " + name);
			}
		}
		// das hier geht alles als 1-Zeiler
		form.getFormFields().get("projectAims.txt").setValue(project.getDescription()).setReadOnly(true);
		form.getFormFields().get("existingDataRelevance.txt").setValue(dmp.getExistingDataRelevance()).setReadOnly(true);
		form.getFormFields().get("usedDataTypes.txt").setValue(String.valueOf(dmp.getUsedDataTypes())).setReadOnly(true);
		form.getFormFields().get("fileFormat.txt").setValue(dmp.getFileFormat()).setReadOnly(true);
		form.getFormFields().get("existingData.txt").setValue(dmp.getExistingData()).setReadOnly(true);
		form.getFormFields().get("dataCitation.txt").setValue(dmp.getDataCitation()).setReadOnly(true);
		// hier geht es dann der Liste nach weiter...

	}

	/**
	 * Unwichtig für dich :D Bin nur zu faul überall texte einzufügen
	 * 
	 * @param maxlength
	 * @return
	 */
	public String createRandomString(final int maxlength) {
		int currLength = 0;
		StringBuilder st = new StringBuilder();
		while (currLength < maxlength) {
			int numOfLetters = (int) (Math.random() * 10);
			st.append(RandomStringUtils.randomAlphanumeric(numOfLetters));
			st.append(" ");
			currLength += numOfLetters;
		}
		return st.toString();
	}

}
