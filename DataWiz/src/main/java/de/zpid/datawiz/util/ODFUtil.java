package de.zpid.datawiz.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.MasterPage;
import org.odftoolkit.simple.style.NumberFormat;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.itextpdf.io.source.ByteArrayOutputStream;

import de.zpid.datawiz.dao.FormTypesDAO;
import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.FormTypesDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ProjectForm;

@Component
@Scope("singleton")
public class ODFUtil {

	@Autowired
	private FormTypesDAO formTypeDAO;

	@Autowired
	protected MessageSource messageSource;

	private static Logger log = LogManager.getLogger(ODFUtil.class);

	private final Font headline = new Font("Calibri", FontStyle.REGULAR, 24.0, new Color(91, 155, 213));
	private final Font blue_reg = new Font("Calibri", FontStyle.REGULAR, 8.0, new Color(91, 155, 213));
	private final Font blue_large = new Font("Calibri", FontStyle.REGULAR, 12.0, new Color(91, 155, 213));
	private final Font regular = new Font("Calibri", FontStyle.REGULAR, 8.0, Color.BLACK);
	private final Font regular_it = new Font("Calibri", FontStyle.ITALIC, 8.0, Color.BLACK);
	private final Font regular_bold = new Font("Calibri", FontStyle.BOLD, 8.0, Color.BLACK);
	private final static List<FormTypesDTO> FORMTYPES = new ArrayList<>();

	/**
	 * This function creates the BMBF OTF document for the DPM export On success it returns an byte array, otherwise an exception or an empty byte
	 * array!
	 * 
	 * @param ProjectForm,
	 *          which contains all necessary data for the export
	 * @param Locale
	 *          to distinguish whether the export is in English or German. At the moment only as a construct, because the export is currently only
	 *          implemented in German.
	 * @return The Open Text document as a byte array
	 * @throws Exception
	 */
	public byte[] createBMBFDoc(final ProjectForm pForm, final Locale locale) throws Exception {
		log.trace("Entering createBMBFDoc");
		ProjectDTO project = pForm.getProject();
		DmpDTO dmp = pForm.getDmp();
		if (dmp != null && project != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TextDocument doc = TextDocument.newTextDocument();
			// Erste Seite - blaue Schrift
			doc.addParagraph(messageSource.getMessage("export.odt.zpid", null, locale)).setFont(blue_reg);
			doc.addColumnBreak();
			doc.addParagraph(messageSource.getMessage("export.odt.BMBF.headline", null, locale)).setFont(headline);
			doc.addColumnBreak();
			Paragraph par = doc.addParagraph(messageSource.getMessage("export.odt.BMBF.subline", null, locale));
			par.setFont(blue_reg);
			// Masterpage: Damit die Tabellen etwas mehr Platz haben werden nach der 1 Seite die Ränder (margin) von 20 (Standard) auf 15 gesetzt.
			MasterPage master = MasterPage.getOrCreateMasterPage(doc, "BMBF");
			master.setNumberFormat(NumberFormat.HINDU_ARABIC_NUMBER.toString());
			master.setMargins(15, 15, 15, 15);
			doc.addPageBreak(par, master);
			// Zweite Seite
			doc.addParagraph(messageSource.getMessage("export.odt.BMBF.zu1h", null, locale)).setFont(blue_large);
			doc.addColumnBreak();
			doc.addParagraph(messageSource.getMessage("export.odt.BMBF.zu1", null, locale)).setFont(regular);
			doc.addColumnBreak();
			par = doc.addParagraph(messageSource.getMessage("export.odt.BMBF.zu1it", null, locale));
			par.setFont(regular_it);
			par.appendHyperlink(messageSource.getMessage("export.odt.BMBF.zu1l", null, locale),
			    new URI(messageSource.getMessage("export.odt.BMBF.zu1l", null, locale)));
			doc.addColumnBreak();
			// Seite mit Tabelle
			Table table = doc.addTable(31, 4);
			// Zusammenführen der Zellen
			table.getCellRangeByPosition(0, 0, 1, 0).merge();
			table.getCellRangeByPosition(2, 0, 3, 0).merge();
			table.getCellRangeByPosition(0, 1, 1, 1).merge();
			table.getCellRangeByPosition(0, 2, 1, 2).merge();
			table.getCellRangeByPosition(0, 3, 0, 26).merge();
			table.getCellRangeByPosition(1, 4, 1, 9).merge();
			table.getCellRangeByPosition(1, 10, 1, 17).merge();
			table.getCellRangeByPosition(1, 19, 1, 26).merge();
			table.getCellRangeByPosition(0, 27, 1, 28).merge();
			table.getCellRangeByPosition(0, 29, 1, 29).merge();
			table.getCellRangeByPosition(2, 29, 3, 29).merge();
			table.getCellRangeByPosition(0, 30, 1, 30).merge();
			table.getCellRangeByPosition(2, 30, 3, 30).merge();
			// Breite der Spalten fest setzen, bis auf die letzte
			table.getColumnByIndex(0).setWidth(30);
			table.getColumnByIndex(1).setWidth(30);
			table.getColumnByIndex(2).setWidth(50);
			// Tabelleninhalt
			createCell(table, 0, 0, messageSource.getMessage("export.odt.BMBF.aza", null, locale), regular_bold, new Color(190, 190, 190), locale);
			createCell(table, 2, 0, messageSource.getMessage("export.odt.BMBF.datawiz", null, locale), regular_bold, new Color(190, 190, 190), locale);
			createCell(table, 0, 1, messageSource.getMessage("export.odt.BMBF.aza.th1", null, locale), regular_bold, new Color(210, 210, 210), locale);
			createCell(table, 2, 1, messageSource.getMessage("export.odt.BMBF.aza.th2", null, locale), regular_bold, new Color(210, 210, 210), locale);
			createCell(table, 3, 1, messageSource.getMessage("export.odt.BMBF.aza.th3", null, locale), regular_bold, new Color(210, 210, 210), locale);
			createCell(table, 0, 2, messageSource.getMessage("export.odt.BMBF.aza.td1", null, locale), regular, null, locale);
			createCell(table, 1, 3, messageSource.getMessage("export.odt.BMBF.aza.td2", null, locale), regular, null, locale);
			createCell(table, 2, 3, messageSource.getMessage("dmp.edit.projectAims", null, locale), regular, null, locale);
			createCell(table, 3, 3, project.getDescription(), regular, null, locale);
			createCell(table, 1, 4, messageSource.getMessage("export.odt.BMBF.aza.td3", null, locale), regular, null, locale);
			createCell(table, 2, 4, messageSource.getMessage("dmp.edit.existingData", null, locale), regular, null, locale);
			createCell(table, 3, 4,
			    (dmp.getExistingData() != null && !dmp.getExistingData().isEmpty()
			        ? messageSource.getMessage("dmp.edit.existingData." + dmp.getExistingData(), null, locale)
			        : ""),
			    regular, null, locale);
			createCell(table, 2, 5, messageSource.getMessage("dmp.edit.dataCitation", null, locale), regular, null, locale);
			createCell(table, 3, 5, dmp.getDataCitation(), regular, null, locale);
			createCell(table, 2, 6, messageSource.getMessage("dmp.edit.existingDataRelevance", null, locale), regular, null, locale);
			createCell(table, 3, 6, dmp.getExistingDataRelevance(), regular, null, locale);
			createCell(table, 2, 7, messageSource.getMessage("dmp.edit.existingDataIntegration", null, locale), regular, null, locale);
			createCell(table, 3, 7, dmp.getExistingDataIntegration(), regular, null, locale);
			createCell(table, 2, 8, messageSource.getMessage("dmp.edit.externalCopyright", null, locale), regular, null, locale);
			createCell(table, 3, 8, dmp.isExternalCopyright(), regular, null, locale);
			createCell(table, 2, 9, messageSource.getMessage("dmp.edit.externalCopyrightTxt", null, locale), regular, null, locale);
			createCell(table, 3, 9, dmp.isExternalCopyright() ? dmp.getExternalCopyrightTxt() : null, regular, null, locale);
			createCell(table, 1, 10, messageSource.getMessage("export.odt.BMBF.aza.td4", null, locale), regular, null, locale);
			createCell(table, 2, 10, messageSource.getMessage("dmp.edit.duration", null, locale), regular, null, locale);
			createCell(table, 3, 10, dmp.getDuration(), regular, null, locale);
			createCell(table, 2, 11, messageSource.getMessage("dmp.edit.usedCollectionModes", null, locale), regular, null, locale);
			createCell(table, 3, 11, dmp.getUsedCollectionModes(), regular, null, locale);
			createCell(table, 2, 12, messageSource.getMessage("dmp.edit.measOccasions", null, locale), regular, null, locale);
			createCell(table, 3, 12, dmp.getMeasOccasions(), regular, null, locale);
			createCell(table, 2, 13, messageSource.getMessage("dmp.edit.specificCosts", null, locale), regular, null, locale);
			createCell(table, 3, 13, dmp.getSpecificCosts(), regular, null, locale);
			createCell(table, 2, 14, messageSource.getMessage("dmp.edit.specificCostsTxt", null, locale), regular, null, locale);
			createCell(table, 3, 14, dmp.getSpecificCostsTxt(), regular, null, locale);
			createCell(table, 2, 15, messageSource.getMessage("dmp.edit.bearCost", null, locale), regular, null, locale);
			createCell(table, 3, 15, dmp.getBearCost(), regular, null, locale);
			createCell(table, 2, 16, messageSource.getMessage("dmp.edit.staffDescription", null, locale), regular, null, locale);
			createCell(table, 3, 16, dmp.isStaffDescription(), regular, null, locale);
			createCell(table, 2, 17, messageSource.getMessage("dmp.edit.staffDescriptionTxt", null, locale), regular, null, locale);
			createCell(table, 3, 17, dmp.isStaffDescription() ? dmp.getStaffDescriptionTxt() : null, regular, null, locale);
			createCell(table, 1, 18, messageSource.getMessage("export.odt.BMBF.aza.td5", null, locale), regular, null, locale);
			createCell(table, 2, 18, messageSource.getMessage("dmp.edit.expectedUsage", null, locale), regular, null, locale);
			createCell(table, 3, 18, dmp.getExpectedUsage(), regular, null, locale);
			createCell(table, 1, 19, messageSource.getMessage("export.odt.BMBF.aza.td6", null, locale), regular, null, locale);
			createCell(table, 2, 19, messageSource.getMessage("dmp.edit.organizations", null, locale), regular, null, locale);
			createCell(table, 3, 19, dmp.getOrganizations(), regular, null, locale);
			createCell(table, 2, 20, messageSource.getMessage("dmp.edit.involvedInstitutions", null, locale), regular, null, locale);
			createCell(table, 3, 20, dmp.getInvolvedInstitutions(), regular, null, locale);
			createCell(table, 2, 21, messageSource.getMessage("dmp.edit.involvedInformed", null, locale), regular, null, locale);
			createCell(table, 3, 21, dmp.isInvolvedInformed(), regular, null, locale);
			createCell(table, 2, 22, messageSource.getMessage("dmp.edit.contributionsDefined", null, locale), regular, null, locale);
			createCell(table, 3, 22, dmp.isContributionsDefined(), regular, null, locale);
			createCell(table, 2, 23, messageSource.getMessage("dmp.edit.contributionsDefinedTxt", null, locale), regular, null, locale);
			createCell(table, 3, 23, dmp.getContributionsDefinedTxt(), regular, null, locale);
			createCell(table, 2, 24, messageSource.getMessage("dmp.edit.givenConsent", null, locale), regular, null, locale);
			createCell(table, 3, 24, dmp.isGivenConsent(), regular, null, locale);
			createCell(table, 2, 25, messageSource.getMessage("dmp.edit.depositName", null, locale), regular, null, locale);
			createCell(table, 3, 25, dmp.getDepositName(), regular, null, locale);
			createCell(table, 2, 26, messageSource.getMessage("dmp.edit.acquisitionAgreement", null, locale), regular, null, locale);
			createCell(table, 3, 26, dmp.isAcquisitionAgreement(), regular, null, locale);
			createCell(table, 0, 27, messageSource.getMessage("export.odt.BMBF.aza.td7", null, locale), regular, null, locale);
			createCell(table, 2, 27, messageSource.getMessage("dmp.edit.managementWorkflow", null, locale), regular, null, locale);
			createCell(table, 3, 27, dmp.isManagementWorkflow(), regular, null, locale);
			createCell(table, 2, 28, messageSource.getMessage("dmp.edit.managementWorkflowTxt", null, locale), regular, null, locale);
			createCell(table, 3, 28, dmp.getManagementWorkflowTxt(), regular, null, locale);
			createCell(table, 0, 29, messageSource.getMessage("export.odt.BMBF.aza.td8", null, locale), regular, null, locale);
			createCell(table, 2, 29, messageSource.getMessage("export.odt.no.equivalent", null, locale), regular, new Color(210, 210, 210), locale);
			createCell(table, 0, 30, messageSource.getMessage("export.odt.BMBF.aza.td9", null, locale), regular, null, locale);
			createCell(table, 2, 30, messageSource.getMessage("export.odt.no.equivalent", null, locale), regular, new Color(210, 210, 210), locale);
			doc.addPageBreak();
			// Neue Seite bildungsforschung
			doc.addParagraph(messageSource.getMessage("export.odt.BMBF.zu2h", null, locale)).setFont(blue_large);
			doc.addColumnBreak();
			doc.addParagraph(messageSource.getMessage("export.odt.BMBF.zu2", null, locale)).setFont(regular);
			doc.addColumnBreak();
			par = doc.addParagraph(messageSource.getMessage("export.odt.BMBF.zu2it", null, locale));
			par.setFont(regular_it);
			par.appendHyperlink(messageSource.getMessage("export.odt.BMBF.zu2l", null, locale),
			    new URI(messageSource.getMessage("export.odt.BMBF.zu2l", null, locale)));
			doc.addColumnBreak();
			table = doc.addTable(31, 4);
			// Zusammenführen der Zellen
			table.getCellRangeByPosition(0, 0, 1, 0).merge();
			table.getCellRangeByPosition(2, 0, 3, 0).merge();
			table.getCellRangeByPosition(0, 2, 3, 2).merge();
			createCell(table, 0, 0, messageSource.getMessage("export.odt.BMBF.bifo.th1", null, locale), regular_bold, new Color(190, 190, 190), locale);
			createCell(table, 2, 0, messageSource.getMessage("export.odt.BMBF.datawiz", null, locale), regular_bold, new Color(190, 190, 190), locale);
			createCell(table, 0, 1, messageSource.getMessage("export.odt.BMBF.bifo.th2", null, locale), regular_bold, new Color(210, 210, 210), locale);
			createCell(table, 1, 1, messageSource.getMessage("export.odt.BMBF.bifo.th3", null, locale), regular_bold, new Color(210, 210, 210), locale);
			createCell(table, 2, 1, messageSource.getMessage("export.odt.BMBF.aza.th2", null, locale), regular_bold, new Color(210, 210, 210), locale);
			createCell(table, 3, 1, messageSource.getMessage("export.odt.BMBF.aza.th3", null, locale), regular_bold, new Color(210, 210, 210), locale);
			createCell(table, 0, 2, messageSource.getMessage("export.odt.BMBF.bifo.td1", null, locale), regular_bold, null, locale);

			/**
			 * Zur erklärung: <br>
			 * create cell hat 7 übergabewerte: <br>
			 * 1. die tabelle in der die Zelle liegt <br>
			 * 2. spaltennummer, beginnend bei 0 für die erste spalte <br>
			 * 3. zeilennummer, beginnend bei 0 für die erste Zeite <br>
			 * 4. der inhalt, der in die Zelle kommt <br>
			 * 5. die schriftart, welche oben in der Klasse definiert werden 6. hintergrundfarbe der Zelle, normal = null, bei überschriften new Color(190,
			 * 190, 190) für dunkles grau und new Color(210, 210, 210) für etwas helleres <br>
			 * 6. die locale (die ist immer dabei)<br>
			 * <br>
			 * wenn der Wert an der 4. Postion aus den Sprachdateien kommt, dann nutzt du den messageSource. Falls der Text noch nicht in der Datei ist, was
			 * bei allen Texten in der 1. und 2. Spalte, sowie den anderen Texten ausserhalb der Tabllen der fall ist, in der ExportResource_de.properties
			 * anlegen
			 * 
			 * das schema ist immer "export.odt.<name des Förderes>.<name der tabelle>.<variablenname>=" bsp: export.odt.BMBF.bifo.th1 wenn die Datei nur
			 * eine tabelle hat, dann kannst du <name der tabelle> weglassen, ABER die bezeichner VOR den = müssen eindeutig sein! doppelte vergabe für zu
			 * fehlern! Die Datei findes du unter src/main/resources/locale - die Bearbeitung _en Datei ist nicht notwengig, da der export vorerst nur auf
			 * deutsch ist.
			 * 
			 * Spalte 3 und 4 sollte dir ja aus dem PDF kram bekannt sein. 3 ist immer der vorhande Text aus der Sprachdateien und 4 die Werte aus dmp, oder
			 * project Objekt
			 * 
			 * Du kannst createCell einfach immer untereinander schreiben und musst nicht auf irgendwelche typen achten, da ich die funktion so geschrieben
			 * habe, das Sie erkennt ob der übergebene wert ein text, zahl, boolean oder eine liste ist.
			 * 
			 */

			createCell(table, 0, 3, messageSource.getMessage("export.odt.BMBF.bifo.td2", null, locale), regular, null, locale);
			createCell(table, 1, 3, messageSource.getMessage("export.odt.BMBF.bifo.td3", null, locale), regular, null, locale);
			createCell(table, 2, 3, messageSource.getMessage("project.edit.grantNumber", null, locale), regular, null, locale);
			createCell(table, 3, 3, project.getGrantNumber(), regular, null, locale);
			createCell(table, 0, 4, messageSource.getMessage("export.odt.BMBF.bifo.td4", null, locale), regular, null, locale);
			createCell(table, 1, 4, messageSource.getMessage("export.odt.BMBF,bifo.td5", null, locale), regular, null, locale);
			createCell(table, 2, 4, messageSource.getMessage("project.edit.funding", null, locale), regular, null, locale);
			createCell(table, 3, 4, project.getFunding(), regular, null, locale);
			

			// write doc to outputstream
			doc.save(baos);
			log.trace("Leaving createBMBFDoc");
			return baos.toByteArray();
		} else {
			if (dmp == null) {
				log.warn("Error during createBMBFDoc - DMP is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.dmp", null, locale), DataWizErrorCodes.NO_DATA_ERROR);
			} else {
				log.warn("Error during createBMBFDoc - Project is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.project", null, locale), DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
			}
		}
	}

	/**
	 * This function creates the H2020 OTF document for the DPM export On success it returns an byte array, otherwise an exception or an empty byte
	 * array!
	 * 
	 * @param ProjectForm,
	 *          which contains all necessary data for the export
	 * @param Locale
	 *          to distinguish whether the export is in English or German. At the moment only as a construct, because the export is currently only
	 *          implemented in German.
	 * @return The Open Text document as a byte array
	 * @throws Exception
	 */
	public byte[] createH2020Doc(final ProjectForm pForm, final Locale locale) throws Exception {
		log.trace("Entering createH2020Doc");
		ProjectDTO project = pForm.getProject();
		DmpDTO dmp = pForm.getDmp();
		if (dmp != null && project != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TextDocument doc = TextDocument.newTextDocument();

			/**
			 * TODO
			 */

			doc.save(baos);
			log.trace("Leaving createH2020Doc");
			return baos.toByteArray();
		} else {
			if (dmp == null) {
				log.warn("Error during createH2020Doc - DMP is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.dmp", null, locale), DataWizErrorCodes.NO_DATA_ERROR);
			} else {
				log.warn("Error during createH2020Doc - Project is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.project", null, locale), DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
			}
		}
	}

	/**
	 * This function creates the DFG OTF document for the DPM export On success it returns an byte array, otherwise an exception or an empty byte array!
	 * 
	 * @param ProjectForm,
	 *          which contains all necessary data for the export
	 * @param Locale
	 *          to distinguish whether the export is in English or German. At the moment only as a construct, because the export is currently only
	 *          implemented in German.
	 * @return The Open Text document as a byte array
	 * @throws Exception
	 */
	public byte[] createDFGDoc(final ProjectForm pForm, final Locale locale) throws Exception {
		log.trace("Entering createDFGDoc");
		ProjectDTO project = pForm.getProject();
		DmpDTO dmp = pForm.getDmp();
		if (dmp != null && project != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TextDocument doc = TextDocument.newTextDocument();

			/**
			 * TODO
			 */

			doc.save(baos);
			log.trace("Leaving createDFGDoc");
			return baos.toByteArray();
		} else {
			if (dmp == null) {
				log.warn("Error during createDFGDoc - DMP is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.dmp", null, locale), DataWizErrorCodes.NO_DATA_ERROR);
			} else {
				log.warn("Error during createDFGDoc - Project is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.project", null, locale), DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
			}
		}
	}

	/**
	 * This function creates the PsychData OTF document for the DPM export On success it returns an byte array, otherwise an exception or an empty byte
	 * array!
	 * 
	 * @param ProjectForm,
	 *          which contains all necessary data for the export
	 * @param Locale
	 *          to distinguish whether the export is in English or German. At the moment only as a construct, because the export is currently only
	 *          implemented in German.
	 * @return The Open Text document as a byte array
	 * @throws Exception
	 */
	public byte[] createPsychdataDoc(final ProjectForm pForm, final Locale locale) throws Exception {
		log.trace("Entering createPsychdataDoc");
		ProjectDTO project = pForm.getProject();
		DmpDTO dmp = pForm.getDmp();
		if (dmp != null && project != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TextDocument doc = TextDocument.newTextDocument();

			/**
			 * TODO
			 */

			doc.save(baos);
			log.trace("Leaving createPsychdataDocc");
			return baos.toByteArray();
		} else {
			if (dmp == null) {
				log.warn("Error during createPsychdataDoc - DMP is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.dmp", null, locale), DataWizErrorCodes.NO_DATA_ERROR);
			} else {
				log.warn("Error during createPsychdataDoc - Project is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.project", null, locale), DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
			}
		}
	}

	/**
	 * This function creates the PreRegistration OTF document for the DPM export On success it returns an byte array, otherwise an exception or an empty
	 * byte array!
	 * 
	 * @param ProjectForm,
	 *          which contains all necessary data for the export
	 * @param Locale
	 *          to distinguish whether the export is in English or German. At the moment only as a construct, because the export is currently only
	 *          implemented in German.
	 * @return The Open Text document as a byte array
	 * @throws Exception
	 */
	public byte[] createPreRegistrationDoc(final ProjectForm pForm, final Locale locale) throws Exception {
		log.trace("Entering createPreRegistrationDoc");
		ProjectDTO project = pForm.getProject();
		DmpDTO dmp = pForm.getDmp();
		if (dmp != null && project != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TextDocument doc = TextDocument.newTextDocument();

			/**
			 * TODO
			 */

			doc.save(baos);
			log.trace("Leaving createPreRegistrationDoc");
			return baos.toByteArray();
		} else {
			if (dmp == null) {
				log.warn("Error during createPreRegistrationDoc - DMP is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.dmp", null, locale), DataWizErrorCodes.NO_DATA_ERROR);
			} else {
				log.warn("Error during createPreRegistrationDoc - Project is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.project", null, locale), DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
			}
		}
	}

	/**
	 * This function writes content into a Cell. There it needs the current table and position (colnum, rownum). As content, the function expects an
	 * object of type object and interprets the correct content independently. This currently works with numbers, strings, booleans and lists.
	 * 
	 * @param table
	 *          Table, which is including the cell
	 * @param col
	 *          Column number (starting with 0)
	 * @param row
	 *          Row number (starting with 0)
	 * @param content
	 *          Content, which will be written into the cell
	 * @param font
	 *          Font-type of the cell
	 * @param color
	 *          Background color of the Cell - null for white
	 * @param locale
	 *          Actual locale for the export
	 */
	private void createCell(final Table table, final int col, final int row, final Object content, final Font font, final Color color,
	    final Locale locale) {
		if (content != null) {
			Cell cell = table.getCellByPosition(col, row);
			if (content instanceof Number) {
				cell.setDoubleValue((Double) content);
			} else if (content instanceof Boolean) {
				cell.setStringValue(
				    ((boolean) content) ? messageSource.getMessage("gen.yes", null, locale) : messageSource.getMessage("gen.no", null, locale));
			} else if (content instanceof String) {
				cell.setStringValue(StringEscapeUtils.UNESCAPE_HTML4.translate(String.valueOf(content)));
			} else if (content instanceof List<?>) {
				StringBuilder sb = new StringBuilder();
				List<?> usedTypes = (ArrayList<?>) content;
				AtomicInteger count = new AtomicInteger(1);
				usedTypes.forEach(usedType -> {
					if (FORMTYPES == null || FORMTYPES.isEmpty()) {

						initFormTpes();
					}
					if (usedType instanceof Integer) {
						FormTypesDTO type = FORMTYPES.parallelStream().filter(dt -> (dt.getId() == (int) usedType)).findFirst().orElse(null);
						// TODO OTHER
						if (type != null) {
							if (locale.equals(Locale.ENGLISH))
								sb.append(type.getNameEN());
							else
								sb.append(type.getNameDE());
						}
						if (count.incrementAndGet() <= usedTypes.size())
							sb.append("\n");
					}
				});
				cell.setStringValue(StringEscapeUtils.UNESCAPE_HTML4.translate(sb.toString()));
			} else {
				cell.setStringValue(String.valueOf(content));
			}
			cell.setFont(font);
			if (color != null)
				cell.setCellBackgroundColor(color);
		}
	}

	/**
	 * This function inits the formtypes. The formtypes are static, because the can be used for all instances. At the moment there are no functions to
	 * change the formtypes, therefore it is not necessary to check for updates. If the formtypes are changed directly in the database, the DataWiz
	 * application has to be reloaded, or the tomcat has to be restarted.
	 */
	private void initFormTpes() {
		try {
			log.debug("Entering initFormTpes");
			FORMTYPES.addAll(formTypeDAO.findAllByType(false, DWFieldTypes.DATATYPE));
			FORMTYPES.addAll(formTypeDAO.findAllByType(false, DWFieldTypes.COLLECTIONMODE));
			FORMTYPES.addAll(formTypeDAO.findAllByType(false, DWFieldTypes.METAPORPOSE));
			log.debug("Leaving initFormTpes");
		} catch (Exception e) {
			log.warn("Error during loading formTypes from Database: ", () -> e);
		}
	}

}
