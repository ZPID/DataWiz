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
import de.zpid.datawiz.dto.StudyConstructDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.StudyInstrumentDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.form.StudyForm;

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
	 * This function creates the BMBF OTF document for the DPM export On success it
	 * returns an byte array, otherwise an exception or an empty byte array!
	 * 
	 * @param ProjectForm,
	 *            which contains all necessary data for the export
	 * @param Locale
	 *            to distinguish whether the export is in English or German. At the
	 *            moment only as a construct, because the export is currently only
	 *            implemented in German.
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
			// Masterpage: Damit die Tabellen etwas mehr Platz haben werden nach der 1 Seite
			// die Ränder (margin) von 20 (Standard) auf 15 gesetzt.
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
			createCell(table, 0, 0, messageSource.getMessage("export.odt.BMBF.aza", null, locale), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 2, 0, messageSource.getMessage("export.odt.BMBF.datawiz", null, locale), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 0, 1, messageSource.getMessage("export.odt.BMBF.aza.th1", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 2, 1, messageSource.getMessage("export.odt.BMBF.aza.th2", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 3, 1, messageSource.getMessage("export.odt.BMBF.aza.th3", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 2, messageSource.getMessage("export.odt.BMBF.aza.td1", null, locale), regular, null,
					locale);
			createCell(table, 1, 3, messageSource.getMessage("export.odt.BMBF.aza.td2", null, locale), regular, null,
					locale);
			createCell(table, 2, 3, messageSource.getMessage("dmp.edit.projectAims", null, locale), regular, null,
					locale);
			createCell(table, 3, 3, project.getDescription(), regular, null, locale);
			createCell(table, 1, 4, messageSource.getMessage("export.odt.BMBF.aza.td3", null, locale), regular, null,
					locale);
			createCell(table, 2, 4, messageSource.getMessage("dmp.edit.existingData", null, locale), regular, null,
					locale);
			createCell(table, 3, 4,
					(dmp.getExistingData() != null && !dmp.getExistingData().isEmpty()
							? messageSource.getMessage("dmp.edit.existingData." + dmp.getExistingData(), null, locale)
							: ""),
					regular, null, locale);
			createCell(table, 2, 5, messageSource.getMessage("dmp.edit.dataCitation", null, locale), regular, null,
					locale);
			createCell(table, 3, 5, dmp.getDataCitation(), regular, null, locale);
			createCell(table, 2, 6, messageSource.getMessage("dmp.edit.existingDataRelevance", null, locale), regular,
					null, locale);
			createCell(table, 3, 6, dmp.getExistingDataRelevance(), regular, null, locale);
			createCell(table, 2, 7, messageSource.getMessage("dmp.edit.existingDataIntegration", null, locale), regular,
					null, locale);
			createCell(table, 3, 7, dmp.getExistingDataIntegration(), regular, null, locale);
			createCell(table, 2, 8, messageSource.getMessage("dmp.edit.externalCopyright", null, locale), regular, null,
					locale);
			createCell(table, 3, 8, dmp.isExternalCopyright(), regular, null, locale);
			createCell(table, 2, 9, messageSource.getMessage("dmp.edit.externalCopyrightTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 9, dmp.isExternalCopyright() ? dmp.getExternalCopyrightTxt() : null, regular, null,
					locale);
			createCell(table, 1, 10, messageSource.getMessage("export.odt.BMBF.aza.td4", null, locale), regular, null,
					locale);
			createCell(table, 2, 10, messageSource.getMessage("dmp.edit.duration", null, locale), regular, null,
					locale);
			createCell(table, 3, 10, dmp.getDuration(), regular, null, locale);
			createCell(table, 2, 11, messageSource.getMessage("dmp.edit.usedCollectionModes", null, locale), regular,
					null, locale);
			createCell(table, 3, 11, dmp.getUsedCollectionModes(), regular, null, locale);
			createCell(table, 2, 12, messageSource.getMessage("dmp.edit.measOccasions", null, locale), regular, null,
					locale);
			createCell(table, 3, 12, dmp.getMeasOccasions(), regular, null, locale);
			createCell(table, 2, 13, messageSource.getMessage("dmp.edit.specificCosts", null, locale), regular, null,
					locale);
			createCell(table, 3, 13, dmp.getSpecificCosts(), regular, null, locale);
			createCell(table, 2, 14, messageSource.getMessage("dmp.edit.specificCostsTxt", null, locale), regular, null,
					locale);
			createCell(table, 3, 14, dmp.getSpecificCostsTxt(), regular, null, locale);
			createCell(table, 2, 15, messageSource.getMessage("dmp.edit.bearCost", null, locale), regular, null,
					locale);
			createCell(table, 3, 15, dmp.getBearCost(), regular, null, locale);
			createCell(table, 2, 16, messageSource.getMessage("dmp.edit.staffDescription", null, locale), regular, null,
					locale);
			createCell(table, 3, 16, dmp.isStaffDescription(), regular, null, locale);
			createCell(table, 2, 17, messageSource.getMessage("dmp.edit.staffDescriptionTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 17, dmp.isStaffDescription() ? dmp.getStaffDescriptionTxt() : null, regular, null,
					locale);
			createCell(table, 1, 18, messageSource.getMessage("export.odt.BMBF.aza.td5", null, locale), regular, null,
					locale);
			createCell(table, 2, 18, messageSource.getMessage("dmp.edit.expectedUsage", null, locale), regular, null,
					locale);
			createCell(table, 3, 18, dmp.getExpectedUsage(), regular, null, locale);
			createCell(table, 1, 19, messageSource.getMessage("export.odt.BMBF.aza.td6", null, locale), regular, null,
					locale);
			createCell(table, 2, 19, messageSource.getMessage("dmp.edit.organizations", null, locale), regular, null,
					locale);
			createCell(table, 3, 19, dmp.getOrganizations(), regular, null, locale);
			createCell(table, 2, 20, messageSource.getMessage("dmp.edit.involvedInstitutions", null, locale), regular,
					null, locale);
			createCell(table, 3, 20, dmp.getInvolvedInstitutions(), regular, null, locale);
			createCell(table, 2, 21, messageSource.getMessage("dmp.edit.involvedInformed", null, locale), regular, null,
					locale);
			createCell(table, 3, 21, dmp.isInvolvedInformed(), regular, null, locale);
			createCell(table, 2, 22, messageSource.getMessage("dmp.edit.contributionsDefined", null, locale), regular,
					null, locale);
			createCell(table, 3, 22, dmp.isContributionsDefined(), regular, null, locale);
			createCell(table, 2, 23, messageSource.getMessage("dmp.edit.contributionsDefinedTxt", null, locale),
					regular, null, locale);
			createCell(table, 3, 23, dmp.getContributionsDefinedTxt(), regular, null, locale);
			createCell(table, 2, 24, messageSource.getMessage("dmp.edit.givenConsent", null, locale), regular, null,
					locale);
			createCell(table, 3, 24, dmp.isGivenConsent(), regular, null, locale);
			createCell(table, 2, 25, messageSource.getMessage("dmp.edit.depositName", null, locale), regular, null,
					locale);
			createCell(table, 3, 25, dmp.getDepositName(), regular, null, locale);
			createCell(table, 2, 26, messageSource.getMessage("dmp.edit.acquisitionAgreement", null, locale), regular,
					null, locale);
			createCell(table, 3, 26, dmp.isAcquisitionAgreement(), regular, null, locale);
			createCell(table, 0, 27, messageSource.getMessage("export.odt.BMBF.aza.td7", null, locale), regular, null,
					locale);
			createCell(table, 2, 27, messageSource.getMessage("dmp.edit.managementWorkflow", null, locale), regular,
					null, locale);
			createCell(table, 3, 27, dmp.isManagementWorkflow(), regular, null, locale);
			createCell(table, 2, 28, messageSource.getMessage("dmp.edit.managementWorkflowTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 28, dmp.getManagementWorkflowTxt(), regular, null, locale);
			createCell(table, 0, 29, messageSource.getMessage("export.odt.BMBF.aza.td8", null, locale), regular, null,
					locale);
			createCell(table, 2, 29, messageSource.getMessage("export.odt.no.equivalent", null, locale), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 30, messageSource.getMessage("export.odt.BMBF.aza.td9", null, locale), regular, null,
					locale);
			createCell(table, 2, 30, messageSource.getMessage("export.odt.no.equivalent", null, locale), regular,
					new Color(210, 210, 210), locale);
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
			table = doc.addTable(85, 4);
			// Zusammenführen der Zellen
			table.getCellRangeByPosition(0, 0, 1, 0).merge();
			table.getCellRangeByPosition(2, 0, 3, 0).merge();
			table.getCellRangeByPosition(0, 2, 3, 2).merge();
			table.getCellRangeByPosition(0, 6, 0, 7).merge();
			table.getCellRangeByPosition(1, 6, 1, 7).merge();
			table.getCellRangeByPosition(2, 9, 3, 9).merge();
			table.getCellRangeByPosition(2, 10, 3, 10).merge();
			table.getCellRangeByPosition(2, 11, 3, 11).merge();
			table.getCellRangeByPosition(0, 13, 3, 13).merge();
			table.getCellRangeByPosition(0, 15, 0, 18).merge();
			table.getCellRangeByPosition(1, 15, 1, 18).merge();
			table.getCellRangeByPosition(0, 19, 0, 21).merge();
			table.getCellRangeByPosition(1, 19, 1, 21).merge();
			table.getCellRangeByPosition(2, 22, 3, 22).merge();
			table.getCellRangeByPosition(2, 23, 3, 23).merge();
			table.getCellRangeByPosition(0, 24, 3, 24).merge();
			table.getCellRangeByPosition(0, 25, 0, 28).merge();
			table.getCellRangeByPosition(1, 25, 1, 28).merge();
			table.getCellRangeByPosition(0, 29, 0, 30).merge();
			table.getCellRangeByPosition(1, 29, 1, 30).merge();
			table.getCellRangeByPosition(0, 31, 0, 33).merge();
			table.getCellRangeByPosition(1, 31, 1, 33).merge();
			table.getCellRangeByPosition(0, 34, 0, 35).merge();
			table.getCellRangeByPosition(1, 34, 1, 35).merge();
			table.getCellRangeByPosition(0, 36, 3, 36).merge();
			table.getCellRangeByPosition(0, 38, 0, 39).merge();
			table.getCellRangeByPosition(1, 38, 1, 39).merge();
			table.getCellRangeByPosition(0, 40, 3, 40).merge();
			table.getCellRangeByPosition(0, 41, 0, 46).merge();
			table.getCellRangeByPosition(1, 41, 1, 46).merge();
			table.getCellRangeByPosition(0, 47, 0, 54).merge();
			table.getCellRangeByPosition(1, 47, 1, 54).merge();
			table.getCellRangeByPosition(2, 55, 3, 55).merge();
			table.getCellRangeByPosition(0, 56, 3, 56).merge();
			table.getCellRangeByPosition(0, 57, 0, 66).merge();
			table.getCellRangeByPosition(1, 57, 1, 66).merge();
			table.getCellRangeByPosition(0, 68, 3, 68).merge();
			table.getCellRangeByPosition(0, 69, 0, 71).merge();
			table.getCellRangeByPosition(1, 69, 1, 71).merge();
			table.getCellRangeByPosition(0, 72, 0, 74).merge();
			table.getCellRangeByPosition(1, 72, 1, 74).merge();
			table.getCellRangeByPosition(0, 75, 3, 75).merge();
			table.getCellRangeByPosition(0, 76, 0, 80).merge();
			table.getCellRangeByPosition(1, 76, 1, 80).merge();
			table.getCellRangeByPosition(0, 81, 0, 85).merge();
			table.getCellRangeByPosition(1, 81, 1, 85).merge();

			table.getColumnByIndex(0).setWidth(30);
			table.getColumnByIndex(1).setWidth(30);
			table.getColumnByIndex(2).setWidth(50);

			createCell(table, 0, 0, messageSource.getMessage("export.odt.BMBF.bifo.th1", null, locale), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 2, 0, messageSource.getMessage("export.odt.BMBF.datawiz", null, locale), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 0, 1, messageSource.getMessage("export.odt.BMBF.bifo.th2", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 1, 1, messageSource.getMessage("export.odt.BMBF.bifo.th3", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 2, 1, messageSource.getMessage("export.odt.BMBF.aza.th2", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 3, 1, messageSource.getMessage("export.odt.BMBF.aza.th3", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 2, messageSource.getMessage("export.odt.BMBF.bifo.td1", null, locale), regular_bold,
					null, locale);

			/**
			 * Zur erklärung: <br>
			 * create cell hat 7 übergabewerte: <br>
			 * 1. die tabelle in der die Zelle liegt <br>
			 * 2. spaltennummer, beginnend bei 0 für die erste spalte <br>
			 * 3. zeilennummer, beginnend bei 0 für die erste Zeite <br>
			 * 4. der inhalt, der in die Zelle kommt <br>
			 * 5. die schriftart, welche oben in der Klasse definiert werden 6.
			 * hintergrundfarbe der Zelle, normal = null, bei überschriften new Color(190,
			 * 190, 190) für dunkles grau und new Color(210, 210, 210) für etwas helleres
			 * <br>
			 * 6. die locale (die ist immer dabei)<br>
			 * <br>
			 * wenn der Wert an der 4. Postion aus den Sprachdateien kommt, dann nutzt du
			 * den messageSource. Falls der Text noch nicht in der Datei ist, was bei allen
			 * Texten in der 1. und 2. Spalte, sowie den anderen Texten ausserhalb der
			 * Tabllen der fall ist, in der ExportResource_de.properties anlegen
			 * 
			 * das schema ist immer "export.odt.<name des Förderes>.<name der
			 * tabelle>.<variablenname>=" bsp: export.odt.BMBF.bifo.th1 wenn die Datei nur
			 * eine tabelle hat, dann kannst du <name der tabelle> weglassen, ABER die
			 * bezeichner VOR den = müssen eindeutig sein! doppelte vergabe für zu fehlern!
			 * Die Datei findes du unter src/main/resources/locale - die Bearbeitung _en
			 * Datei ist nicht notwengig, da der export vorerst nur auf deutsch ist.
			 * 
			 * Spalte 3 und 4 sollte dir ja aus dem PDF kram bekannt sein. 3 ist immer der
			 * vorhande Text aus der Sprachdateien und 4 die Werte aus dmp, oder project
			 * Objekt
			 * 
			 * Du kannst createCell einfach immer untereinander schreiben und musst nicht
			 * auf irgendwelche typen achten, da ich die funktion so geschrieben habe, das
			 * Sie erkennt ob der übergebene wert ein text, zahl, boolean oder eine liste
			 * ist.
			 * 
			 */

			createCell(table, 0, 3, messageSource.getMessage("export.odt.BMBF.bifo.td2", null, locale), regular, null,
					locale);
			createCell(table, 1, 3, messageSource.getMessage("export.odt.BMBF.bifo.td3", null, locale), regular, null,
					locale);
			createCell(table, 2, 3, messageSource.getMessage("project.edit.grantNumber", null, locale), regular, null,
					locale);
			createCell(table, 3, 3, project.getGrantNumber(), regular, null, locale);
			createCell(table, 0, 4, messageSource.getMessage("export.odt.BMBF.bifo.td4", null, locale), regular, null,
					locale);
			createCell(table, 1, 4, messageSource.getMessage("export.odt.BMBF.bifo.td5", null, locale), regular, null,
					locale);
			createCell(table, 2, 4, messageSource.getMessage("project.edit.funding", null, locale), regular, null,
					locale);
			createCell(table, 3, 4, project.getFunding(), regular, null, locale);
			createCell(table, 0, 5, messageSource.getMessage("export.odt.BMBF.bifo.td6", null, locale), regular, null,
					locale);
			createCell(table, 1, 5, messageSource.getMessage("export.odt.BMBF.bifo.td7", null, locale), regular, null,
					locale);
			createCell(table, 2, 5, messageSource.getMessage("project.edit.title", null, locale), regular, null,
					locale);
			createCell(table, 3, 5, project.getTitle(), regular, null, locale);
			createCell(table, 0, 6, messageSource.getMessage("export.odt.BMBF.bifo.td8", null, locale), regular, null,
					locale);
			createCell(table, 1, 6, messageSource.getMessage("export.odt.BMBF.bifo.td9", null, locale), regular, null,
					locale);
			createCell(table, 2, 6, messageSource.getMessage("project.edit.description", null, locale), regular, null,
					locale);
			createCell(table, 3, 6, project.getDescription(), regular, null, locale);
			createCell(table, 2, 7, messageSource.getMessage("dmp.edit.existingDataRelevance", null, locale), regular,
					null, locale);
			createCell(table, 3, 7, dmp.getExistingDataRelevance(), regular, null, locale);
			createCell(table, 0, 8, messageSource.getMessage("export.odt.BMBF.bifo.td10", null, locale), regular, null,
					locale);
			createCell(table, 1, 8, messageSource.getMessage("export.odt.BMBF.bifo.td11", null, locale), regular, null,
					locale);
			createCell(table, 2, 8, messageSource.getMessage("dmp.edit.leader", null, locale), regular, null, locale);
			StringBuilder primName = new StringBuilder();
			if (pForm.getPrimaryContributor() != null) {
				if (pForm.getPrimaryContributor().getTitle() != null
						&& !pForm.getPrimaryContributor().getTitle().isEmpty())
					primName.append(pForm.getPrimaryContributor().getTitle()).append(" ");
				if (pForm.getPrimaryContributor().getFirstName() != null
						&& !pForm.getPrimaryContributor().getFirstName().isEmpty())
					primName.append(pForm.getPrimaryContributor().getFirstName()).append(" ");
				if (pForm.getPrimaryContributor().getLastName() != null
						&& !pForm.getPrimaryContributor().getLastName().isEmpty())
					primName.append(pForm.getPrimaryContributor().getLastName());
				if (pForm.getPrimaryContributor().getInstitution() != null
						&& !pForm.getPrimaryContributor().getInstitution().isEmpty())
					primName.append(" (").append(pForm.getPrimaryContributor().getInstitution()).append(")");
				createCell(table, 3, 8, primName.toString(), regular, null, locale);
			}
			createCell(table, 0, 9, messageSource.getMessage("export.odt.BMBF.bifo.td12", null, locale), regular, null,
					locale);
			createCell(table, 1, 9, messageSource.getMessage("export.odt.BMBF.bifo.td13", null, locale), regular, null,
					locale);
			createCell(table, 2, 9, messageSource.getMessage("export.odt.no.equivalent", null, locale), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 10, messageSource.getMessage("export.odt.BMBF.bifo.td14", null, locale), regular, null,
					locale);
			createCell(table, 1, 10, messageSource.getMessage("export.odt.BMBF.bifo.td15", null, locale), regular, null,
					locale);
			createCell(table, 2, 10, messageSource.getMessage("export.odt.no.equivalent", null, locale), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 11, messageSource.getMessage("export.odt.BMBF.bifo.td16", null, locale), regular, null,
					locale);
			createCell(table, 1, 11, messageSource.getMessage("export.odt.BMBF.bifo.td17", null, locale), regular, null,
					locale);
			createCell(table, 2, 11, messageSource.getMessage("export.odt.no.equivalent", null, locale), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 12, messageSource.getMessage("export.odt.BMBF.bifo.td18", null, locale), regular, null,
					locale);
			createCell(table, 1, 12, messageSource.getMessage("export.odt.BMBF.bifo.td19", null, locale), regular, null,
					locale);
			createCell(table, 2, 12, messageSource.getMessage("dmp.edit.funderRequirements", null, locale), regular,
					null, locale);
			createCell(table, 3, 12, dmp.getFunderRequirements(), regular, null, locale);
			createCell(table, 0, 13, messageSource.getMessage("export.odt.BMBF.bifo.td20", null, locale), regular_bold,
					null, locale);
			createCell(table, 0, 14, messageSource.getMessage("export.odt.BMBF.bifo.td21", null, locale), regular, null,
					locale);
			createCell(table, 1, 14, messageSource.getMessage("export.odt.BMBF.bifo.td22", null, locale), regular, null,
					locale);
			createCell(table, 2, 14, messageSource.getMessage("dmp.edit.usedDataTypes", null, locale), regular, null,
					locale);
			createCell(table, 3, 14, dmp.getUsedDataTypes(), regular, null, locale);
			createCell(table, 0, 15, messageSource.getMessage("export.odt.BMBF.bifo.td23", null, locale), regular, null,
					locale);
			createCell(table, 1, 15, messageSource.getMessage("export.odt.BMBF.bifo.td24", null, locale), regular, null,
					locale);
			createCell(table, 2, 15, messageSource.getMessage("dmp.edit.existingData", null, locale), regular, null,
					locale);
			createCell(table, 3, 15, dmp.getExistingData(), regular, null, locale);
			createCell(table, 2, 16, messageSource.getMessage("dmp.edit.dataCitation", null, locale), regular, null,
					locale);
			createCell(table, 3, 16, dmp.getDataCitation(), regular, null, locale);
			createCell(table, 2, 17, messageSource.getMessage("dmp.edit.existingDataRelevance", null, locale), regular,
					null, locale);
			createCell(table, 3, 17, dmp.getExistingDataRelevance(), regular, null, locale);
			createCell(table, 2, 18, messageSource.getMessage("dmp.edit.existingDataIntegration", null, locale),
					regular, null, locale);
			createCell(table, 3, 18, dmp.getExistingDataIntegration(), regular, null, locale);
			createCell(table, 0, 19, messageSource.getMessage("export.odt.BMBF.bifo.td25", null, locale), regular, null,
					locale);
			createCell(table, 1, 19, messageSource.getMessage("export.odt.BMBF.bifo.td26", null, locale), regular, null,
					locale);
			createCell(table, 2, 19, messageSource.getMessage("dmp.edit.reliabilityTraining", null, locale), regular,
					null, locale);
			createCell(table, 3, 19, dmp.getReliabilityTraining(), regular, null, locale);
			createCell(table, 2, 20, messageSource.getMessage("dmp.edit.multipleMeasurements", null, locale), regular,
					null, locale);
			createCell(table, 3, 20, dmp.getMultipleMeasurements(), regular, null, locale);
			createCell(table, 2, 21, messageSource.getMessage("dmp.edit.qualitityOther", null, locale), regular, null,
					locale);
			createCell(table, 3, 21, dmp.getQualitityOther(), regular, null, locale);
			createCell(table, 0, 22, messageSource.getMessage("export.odt.BMBF.bifo.td27", null, locale), regular, null,
					locale);
			createCell(table, 1, 22, messageSource.getMessage("export.odt.BMBF.bifo.td28", null, locale), regular, null,
					locale);
			createCell(table, 2, 22, messageSource.getMessage("export.odt.no.equivalent1", null, locale), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 23, messageSource.getMessage("export.odt.BMBF.bifo.td29", null, locale), regular, null,
					locale);
			createCell(table, 1, 23, messageSource.getMessage("export.odt.BMBF.bifo.td30", null, locale), regular, null,
					locale);
			createCell(table, 2, 23, messageSource.getMessage("export.odt.no.equivalent2", null, locale), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 24, messageSource.getMessage("export.odt.BMBF.bifo.td31", null, locale), regular_bold,
					null, locale);
			createCell(table, 0, 25, messageSource.getMessage("export.odt.BMBF.bifo.td32", null, locale), regular, null,
					locale);
			createCell(table, 1, 25, messageSource.getMessage("export.odt.BMBF.bifo.td33", null, locale), regular, null,
					locale);
			createCell(table, 2, 25, messageSource.getMessage("dmp.edit.storageResponsible", null, locale), regular,
					null, locale);
			createCell(table, 3, 25, dmp.getStorageResponsible(), regular, null, locale);
			createCell(table, 2, 26, messageSource.getMessage("dmp.edit.storagePlaces", null, locale), regular, null,
					locale);
			createCell(table, 3, 26, dmp.getStoragePlaces(), regular, null, locale);
			createCell(table, 2, 27, messageSource.getMessage("dmp.edit.storageBackups", null, locale), regular, null,
					locale);
			createCell(table, 3, 27, dmp.getStorageBackups(), regular, null, locale);
			createCell(table, 2, 28, messageSource.getMessage("dmp.edit.storageExpectedSize", null, locale), regular,
					null, locale);
			createCell(table, 3, 28, dmp.getStorageExpectedSize(), regular, null, locale);
			createCell(table, 0, 29, messageSource.getMessage("export.odt.BMBF.bifo.td34", null, locale), regular, null,
					locale);
			createCell(table, 1, 29, messageSource.getMessage("export.odt.BMBF.bifo.td35", null, locale), regular, null,
					locale);
			createCell(table, 2, 29, messageSource.getMessage("dmp.edit.managementWorkflow", null, locale), regular,
					null, locale);
			createCell(table, 3, 29, dmp.isManagementWorkflow(), regular, null, locale);
			createCell(table, 2, 30, messageSource.getMessage("dmp.edit.managementWorkflowTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 30, dmp.getManagementWorkflowTxt(), regular, null, locale);
			createCell(table, 0, 31, messageSource.getMessage("export.odt.BMBF.bifo.td36", null, locale), regular, null,
					locale);
			createCell(table, 1, 31, messageSource.getMessage("export.odt.BMBF.bifo.td37", null, locale), regular, null,
					locale);
			createCell(table, 2, 31, messageSource.getMessage("dmp.edit.fileFormat", null, locale), regular, null,
					locale);
			createCell(table, 3, 31, dmp.getFileFormat(), regular, null, locale);
			createCell(table, 2, 32, messageSource.getMessage("dmp.edit.storageRequirements", null, locale), regular,
					null, locale);
			createCell(table, 3, 32, dmp.isStorageRequirements(), regular, null, locale);
			createCell(table, 2, 33, messageSource.getMessage("dmp.edit.storageRequirementsTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 33, dmp.getStorageRequirementsTxt(), regular, null, locale);
			createCell(table, 0, 34, messageSource.getMessage("export.odt.BMBF.bifo.td38", null, locale), regular, null,
					locale);
			createCell(table, 1, 34, messageSource.getMessage("export.odt.BMBF.bifo.td39", null, locale), regular, null,
					locale);
			createCell(table, 2, 34, messageSource.getMessage("dmp.edit.sensitiveDataIncluded", null, locale), regular,
					null, locale);
			createCell(table, 3, 34, dmp.isSensitiveDataIncluded(), regular, null, locale);
			createCell(table, 2, 35, messageSource.getMessage("dmp.edit.sensitiveDataIncludedTxt", null, locale),
					regular, null, locale);
			createCell(table, 3, 35, dmp.getSensitiveDataIncludedTxt(), regular, null, locale);
			createCell(table, 0, 36, messageSource.getMessage("export.odt.BMBF.bifo.td40", null, locale), regular_bold,
					null, locale);
			createCell(table, 0, 37, messageSource.getMessage("export.odt.BMBF.bifo.td41", null, locale), regular, null,
					locale);
			createCell(table, 1, 37, messageSource.getMessage("export.odt.BMBF.bifo.td42", null, locale), regular, null,
					locale);
			createCell(table, 2, 37, messageSource.getMessage("dmp.edit.metaDescription", null, locale), regular, null,
					locale);
			createCell(table, 3, 37, dmp.getMetaDescription(), regular, null, locale);
			createCell(table, 0, 38, messageSource.getMessage("export.odt.BMBF.bifo.td43", null, locale), regular, null,
					locale);
			createCell(table, 1, 38, messageSource.getMessage("export.odt.BMBF.bifo.td44", null, locale), regular, null,
					locale);
			createCell(table, 2, 38, messageSource.getMessage("dmp.edit.metaFramework", null, locale), regular, null,
					locale);
			createCell(table, 3, 38, dmp.getMetaFramework(), regular, null, locale);
			createCell(table, 2, 39, messageSource.getMessage("dmp.edit.selectedMetaPurposes", null, locale), regular,
					null, locale);
			createCell(table, 3, 39, dmp.getSelectedMetaPurposes(), regular, null, locale);
			createCell(table, 0, 40, messageSource.getMessage("export.odt.BMBF.bifo.td45", null, locale), regular_bold,
					null, locale);
			createCell(table, 0, 41, messageSource.getMessage("export.odt.BMBF.bifo.td46", null, locale), regular, null,
					locale);
			createCell(table, 1, 41, messageSource.getMessage("export.odt.BMBF.bifo.td47", null, locale), regular, null,
					locale);
			createCell(table, 2, 41, messageSource.getMessage("dmp.edit.frameworkNationality", null, locale), regular,
					null, locale);
			createCell(table, 3, 41, dmp.getFrameworkNationality(), regular, null, locale);
			createCell(table, 2, 42, messageSource.getMessage("dmp.edit.frameworkNationalityTxt", null, locale),
					regular, null, locale);
			createCell(table, 3, 42, dmp.getFrameworkNationalityTxt(), regular, null, locale);
			createCell(table, 2, 43, messageSource.getMessage("dmp.edit.externalCopyright", null, locale), regular,
					null, locale);
			createCell(table, 3, 43, dmp.isExternalCopyright(), regular, null, locale);
			createCell(table, 2, 44, messageSource.getMessage("dmp.edit.externalCopyrightTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 44, dmp.getExternalCopyrightTxt(), regular, null, locale);
			createCell(table, 2, 45, messageSource.getMessage("dmp.edit.internalCopyright", null, locale), regular,
					null, locale);
			createCell(table, 3, 45, dmp.isInternalCopyright(), regular, null, locale);
			createCell(table, 2, 46, messageSource.getMessage("dmp.edit.internalCopyrightTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 46, dmp.getInternalCopyrightTxt(), regular, null, locale);
			createCell(table, 0, 47, messageSource.getMessage("export.odt.BMBF.bifo.td48", null, locale), regular, null,
					locale);
			createCell(table, 1, 47, messageSource.getMessage("export.odt.BMBF.bifo.td49", null, locale), regular, null,
					locale);
			createCell(table, 2, 47, messageSource.getMessage("dmp.edit.sensitiveData", null, locale), regular, null,
					locale);
			createCell(table, 3, 47, dmp.getSensitiveData(), regular, null, locale);
			createCell(table, 2, 48, messageSource.getMessage("dmp.edit.dataProtection", null, locale), regular, null,
					locale);
			createCell(table, 3, 48, dmp.isDataProtection(), regular, null, locale);
			createCell(table, 2, 49, messageSource.getMessage("dmp.edit.protectionRequirements", null, locale), regular,
					null, locale);
			createCell(table, 3, 49, dmp.getProtectionRequirements(), regular, null, locale);
			createCell(table, 2, 50, messageSource.getMessage("dmp.edit.consentObtained", null, locale), regular, null,
					locale);
			createCell(table, 3, 50, dmp.isConsentObtained(), regular, null, locale);
			createCell(table, 2, 51, messageSource.getMessage("dmp.edit.consentObtainedTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 51, dmp.getConsentObtainedTxt(), regular, null, locale);
			createCell(table, 2, 52, messageSource.getMessage("dmp.edit.sharingConsidered", null, locale), regular,
					null, locale);
			createCell(table, 3, 52, dmp.isSharingConsidered(), regular, null, locale);
			createCell(table, 2, 53, messageSource.getMessage("dmp.edit.irbApproval", null, locale), regular, null,
					locale);
			createCell(table, 3, 53, dmp.isIrbApproval(), regular, null, locale);
			createCell(table, 2, 54, messageSource.getMessage("dmp.edit.irbApprovalTxt", null, locale), regular, null,
					locale);
			createCell(table, 3, 54, dmp.getIrbApprovalTxt(), regular, null, locale);
			createCell(table, 0, 55, messageSource.getMessage("export.odt.BMBF.bifo.td50", null, locale), regular, null,
					locale);
			createCell(table, 1, 55, messageSource.getMessage("export.odt.BMBF.bifo.td51", null, locale), regular, null,
					locale);
			createCell(table, 2, 55, messageSource.getMessage("export.odt.no.equivalent", null, locale), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 56, messageSource.getMessage("export.odt.BMBF.bifo.td52", null, locale), regular, null,
					locale);
			createCell(table, 0, 57, messageSource.getMessage("export.odt.BMBF.bifo.td53", null, locale), regular, null,
					locale);
			createCell(table, 1, 57, messageSource.getMessage("export.odt.BMBF.bifo.td54", null, locale), regular, null,
					locale);
			createCell(table, 2, 57, messageSource.getMessage("dmp.edit.publStrategy", null, locale), regular, null,
					locale);
			createCell(table, 3, 57, dmp.getPublStrategy(), regular, null, locale);
			createCell(table, 2, 58, messageSource.getMessage("dmp.edit.depositName", null, locale), regular, null,
					locale);
			createCell(table, 3, 58, dmp.getDepositName(), regular, null, locale);
			createCell(table, 2, 59, messageSource.getMessage("dmp.edit.searchableData", null, locale), regular, null,
					locale);
			createCell(table, 3, 59, dmp.getSearchableData(), regular, null, locale);
			createCell(table, 2, 60, messageSource.getMessage("dmp.edit.transferTime", null, locale), regular, null,
					locale);
			createCell(table, 3, 60, dmp.getTransferTime(), regular, null, locale);
			createCell(table, 2, 61, messageSource.getMessage("dmp.edit.accessReasonAuthor", null, locale), regular,
					null, locale);
			createCell(table, 3, 61, dmp.getAccessReasonAuthor(), regular, null, locale);
			createCell(table, 2, 62, messageSource.getMessage("dmp.edit.noAccessReason", null, locale), regular, null,
					locale);
			createCell(table, 3, 62, dmp.getNoAccessReason(), regular, null, locale);
			createCell(table, 2, 63, messageSource.getMessage("dmp.edit.initialUsage", null, locale), regular, null,
					locale);
			createCell(table, 3, 63, dmp.getInitialUsage(), regular, null, locale);
			createCell(table, 2, 64, messageSource.getMessage("dmp.edit.usageRestriction", null, locale), regular, null,
					locale);
			createCell(table, 3, 64, dmp.getUsageRestriction(), regular, null, locale);
			createCell(table, 2, 65, messageSource.getMessage("dmp.edit.accessCosts", null, locale), regular, null,
					locale);
			createCell(table, 3, 65, dmp.isAccessCosts(), regular, null, locale);
			createCell(table, 2, 66, messageSource.getMessage("dmp.edit.usedPID", null, locale), regular, null, locale);
			createCell(table, 3, 66, dmp.getUsedPID(), regular, null, locale);
			createCell(table, 0, 67, messageSource.getMessage("export.odt.BMBF.bifo.td55", null, locale), regular, null,
					locale);
			createCell(table, 1, 67, messageSource.getMessage("export.odt.BMBF.bifo.td56", null, locale), regular, null,
					locale);
			createCell(table, 2, 67, messageSource.getMessage("dmp.edit.expectedUsage", null, locale), regular, null,
					locale);
			createCell(table, 3, 67, dmp.getExpectedUsage(), regular, null, locale);
			createCell(table, 0, 68, messageSource.getMessage("export.odt.BMBF.bifo.td57", null, locale), regular_bold,
					null, locale);
			createCell(table, 0, 69, messageSource.getMessage("export.odt.BMBF.bifo.td58", null, locale), regular, null,
					locale);
			createCell(table, 1, 69, messageSource.getMessage("export.odt.BMBF.bifo.td59", null, locale), regular, null,
					locale);
			createCell(table, 2, 69, messageSource.getMessage("dmp.edit.dataSelection", null, locale), regular, null,
					locale);
			createCell(table, 3, 69, dmp.isDataSelection(), regular, null, locale);
			createCell(table, 2, 70, messageSource.getMessage("dmp.edit.selectionTime", null, locale), regular, null,
					locale);
			createCell(table, 3, 70, dmp.getSelectionTime(), regular, null, locale);
			createCell(table, 2, 71, messageSource.getMessage("dmp.edit.selectionResp", null, locale), regular, null,
					locale);
			createCell(table, 3, 71, dmp.getSelectionResp(), regular, null, locale);
			createCell(table, 0, 72, messageSource.getMessage("export.odt.BMBF.bifo.td60", null, locale), regular, null,
					locale);
			createCell(table, 1, 72, messageSource.getMessage("export.odt.BMBF.bifo.td61", null, locale), regular, null,
					locale);
			createCell(table, 2, 72, messageSource.getMessage("dmp.edit.storageDuration", null, locale), regular, null,
					locale);
			createCell(table, 3, 72, dmp.getStorageDuration(), regular, null, locale);
			createCell(table, 2, 73, messageSource.getMessage("dmp.edit.storageSuccession", null, locale), regular,
					null, locale);
			createCell(table, 3, 73, dmp.isStorageSuccession(), regular, null, locale);
			createCell(table, 2, 74, messageSource.getMessage("dmp.edit.storageSuccessionTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 74, dmp.getStorageSuccessionTxt(), regular, null, locale);
			createCell(table, 0, 75, messageSource.getMessage("export.odt.BMBF.bifo.td62", null, locale), regular_bold,
					null, locale);
			createCell(table, 0, 76, messageSource.getMessage("export.odt.BMBF.bifo.td63", null, locale), regular, null,
					locale);
			createCell(table, 1, 76, messageSource.getMessage("export.odt.BMBF.bifo.td64", null, locale), regular, null,
					locale);
			createCell(table, 2, 76, messageSource.getMessage("dmp.edit.responsibleUnit", null, locale), regular, null,
					locale);
			createCell(table, 3, 76, dmp.getResponsibleUnit(), regular, null, locale);
			createCell(table, 2, 77, messageSource.getMessage("dmp.edit.involvedInstitutions", null, locale), regular,
					null, locale);
			createCell(table, 3, 77, dmp.getInvolvedInstitutions(), regular, null, locale);
			createCell(table, 2, 78, messageSource.getMessage("dmp.edit.involvedInformed", null, locale), regular, null,
					locale);
			createCell(table, 3, 78, dmp.isInvolvedInformed(), regular, null, locale);
			createCell(table, 2, 79, messageSource.getMessage("dmp.edit.contributionsDefined", null, locale), regular,
					null, locale);
			createCell(table, 3, 79, dmp.isContributionsDefined(), regular, null, locale);
			createCell(table, 2, 80, messageSource.getMessage("dmp.edit.contributionsDefinedTxt", null, locale),
					regular, null, locale);
			createCell(table, 3, 80, dmp.getContributionsDefinedTxt(), regular, null, locale);
			createCell(table, 0, 81, messageSource.getMessage("export.odt.BMBF.bifo.td65", null, locale), regular, null,
					locale);
			createCell(table, 1, 81, messageSource.getMessage("export.odt.BMBF.bifo.td66", null, locale), regular, null,
					locale);
			createCell(table, 2, 81, messageSource.getMessage("dmp.edit.staffDescription", null, locale), regular, null,
					locale);
			createCell(table, 3, 81, dmp.isStaffDescription(), regular, null, locale);
			createCell(table, 2, 82, messageSource.getMessage("dmp.edit.staffDescriptionTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 82, dmp.getStaffDescriptionTxt(), regular, null, locale);
			createCell(table, 2, 83, messageSource.getMessage("dmp.edit.specificCosts", null, locale), regular, null,
					locale);
			createCell(table, 3, 83, dmp.getSpecificCosts(), regular, null, locale);
			createCell(table, 2, 84, messageSource.getMessage("dmp.edit.specificCostsTxt", null, locale), regular, null,
					locale);
			createCell(table, 3, 84, dmp.getSpecificCostsTxt(), regular, null, locale);
			createCell(table, 2, 85, messageSource.getMessage("dmp.edit.bearCost", null, locale), regular, null,
					locale);
			createCell(table, 3, 85, dmp.getBearCost(), regular, null, locale);

			// write doc to outputstream
			doc.save(baos);
			doc.close();
			log.trace("Leaving createBMBFDoc");
			return baos.toByteArray();
		} else {
			if (dmp == null) {
				log.warn("Error during createBMBFDoc - DMP is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.dmp", null, locale),
						DataWizErrorCodes.NO_DATA_ERROR);
			} else {
				log.warn("Error during createBMBFDoc - Project is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.project", null, locale),
						DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
			}
		}
	}

	/**
	 * This function creates the H2020 OTF document for the DPM export On success it
	 * returns an byte array, otherwise an exception or an empty byte array!
	 * 
	 * @param ProjectForm,
	 *            which contains all necessary data for the export
	 * @param Locale
	 *            to distinguish whether the export is in English or German. At the
	 *            moment only as a construct, because the export is currently only
	 *            implemented in German.
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
			// Erste Seite
			doc.addParagraph(messageSource.getMessage("export.odt.H2020.zpid", null, locale)).setFont(blue_reg);
			doc.addColumnBreak();
			Paragraph par = doc.addParagraph(messageSource.getMessage("export.odt.H2020.headline", null, locale));
			par.setFont(headline);
			doc.addColumnBreak();
			// Masterpage
			MasterPage master = MasterPage.getOrCreateMasterPage(doc, "H2020");
			master.setNumberFormat(NumberFormat.HINDU_ARABIC_NUMBER.toString());
			master.setMargins(15, 15, 15, 15);
			doc.addPageBreak(par, master);
			// Zweite Seite
			doc.addParagraph(messageSource.getMessage("export.odt.H2020.zu1", null, locale)).setFont(regular);
			doc.addColumnBreak();
			par = doc.addParagraph(messageSource.getMessage("export.odt.H2020.zu2", null, locale));
			par.setFont(regular_it);
			par.appendHyperlink(messageSource.getMessage("export.odt.H2020.zu3", null, locale),
					new URI(messageSource.getMessage("export.odt.H2020.zu3", null, locale)));
			doc.addColumnBreak();
			Table table = doc.addTable(66, 4);
			// Zusammenführen
			table.getCellRangeByPosition(0, 0, 1, 0).merge();
			table.getCellRangeByPosition(2, 0, 3, 0).merge();
			table.getCellRangeByPosition(0, 2, 0, 12).merge();
			table.getCellRangeByPosition(1, 2, 1, 3).merge();
			table.getCellRangeByPosition(1, 4, 1, 5).merge();
			table.getCellRangeByPosition(1, 6, 1, 8).merge();
			table.getCellRangeByPosition(1, 11, 1, 12).merge();
			table.getCellRangeByPosition(0, 14, 0, 24).merge();
			table.getCellRangeByPosition(2, 17, 3, 17).merge();
			table.getCellRangeByPosition(1, 18, 1, 19).merge();
			table.getCellRangeByPosition(1, 20, 1, 24).merge();
			table.getCellRangeByPosition(0, 25, 0, 41).merge();
			table.getCellRangeByPosition(1, 25, 1, 27).merge();
			table.getCellRangeByPosition(1, 28, 1, 33).merge();
			table.getCellRangeByPosition(1, 34, 1, 35).merge();
			table.getCellRangeByPosition(1, 36, 1, 37).merge();
			table.getCellRangeByPosition(1, 38, 1, 41).merge();
			table.getCellRangeByPosition(0, 42, 0, 43).merge();
			table.getCellRangeByPosition(2, 42, 3, 42).merge();
			table.getCellRangeByPosition(2, 43, 3, 43).merge();
			table.getCellRangeByPosition(0, 44, 0, 51).merge();
			table.getCellRangeByPosition(1, 45, 1, 46).merge();
			table.getCellRangeByPosition(2, 47, 3, 47).merge();
			table.getCellRangeByPosition(1, 48, 1, 50).merge();
			table.getCellRangeByPosition(0, 52, 0, 56).merge();
			table.getCellRangeByPosition(1, 52, 1, 54).merge();
			table.getCellRangeByPosition(2, 56, 3, 56).merge();
			table.getCellRangeByPosition(0, 57, 0, 60).merge();
			table.getCellRangeByPosition(1, 57, 1, 60).merge();
			table.getCellRangeByPosition(0, 61, 0, 65).merge();
			table.getCellRangeByPosition(1, 61, 1, 65).merge();
			// Breite
			table.getColumnByIndex(0).setWidth(30);
			table.getColumnByIndex(1).setWidth(30);
			table.getColumnByIndex(2).setWidth(50);
			// Inhalt
			createCell(table, 0, 0, messageSource.getMessage("export.odt.H2020.FAIR", null, locale), regular_bold, null,
					locale);
			createCell(table, 2, 0, messageSource.getMessage("export.odt.H2020.datawiz", null, locale), regular_bold,
					null, locale);
			createCell(table, 0, 1, messageSource.getMessage("export.odt.H2020.th1", null, locale), regular_bold, null,
					locale);
			createCell(table, 1, 1, messageSource.getMessage("export.odt.H2020.th2", null, locale), regular_bold, null,
					locale);
			createCell(table, 2, 1, messageSource.getMessage("export.odt.H2020.th3", null, locale), regular_bold, null,
					locale);
			createCell(table, 3, 1, messageSource.getMessage("export.odt.H2020.th3", null, locale), regular_bold, null,
					locale);
			createCell(table, 0, 2, messageSource.getMessage("export.odt.H2020.td1", null, locale), regular_bold, null,
					locale);
			createCell(table, 1, 2, messageSource.getMessage("export.odt.H2020.td2", null, locale), regular, null,
					locale);
			createCell(table, 2, 2, messageSource.getMessage("dmp.edit.projectAims", null, locale), regular, null,
					locale);
			createCell(table, 3, 2, project.getDescription(), regular, null, locale);
			createCell(table, 2, 3, messageSource.getMessage("dmp.edit.existingDataRelevance", null, locale), regular,
					null, locale);
			createCell(table, 3, 3, dmp.getExistingDataRelevance(), regular, null, locale);
			createCell(table, 1, 4, messageSource.getMessage("export.odt.H2020.td3", null, locale), regular, null,
					locale);
			createCell(table, 2, 4, messageSource.getMessage("dmp.edit.usedDataTypes", null, locale), regular, null,
					locale);
			createCell(table, 3, 4, dmp.getUsedDataTypes(), regular, null, locale);
			createCell(table, 2, 5, messageSource.getMessage("dmp.edit.fileFormat", null, locale), regular, null,
					locale);
			createCell(table, 3, 5, dmp.getFileFormat(), regular, null, locale);
			createCell(table, 1, 6, messageSource.getMessage("export.odt.H2020.td4", null, locale), regular, null,
					locale);
			createCell(table, 2, 6, messageSource.getMessage("dmp.edit.existingData", null, locale), regular, null,
					locale);
			createCell(table, 3, 6, dmp.getExistingData(), regular, null, locale);
			createCell(table, 2, 7, messageSource.getMessage("dmp.edit.dataCitation", null, locale), regular, null,
					locale);
			createCell(table, 3, 7, dmp.getDataCitation(), regular, null, locale);
			createCell(table, 2, 8, messageSource.getMessage("dmp.edit.existingDataIntegration", null, locale), regular,
					null, locale);
			createCell(table, 3, 8, dmp.getExistingDataIntegration(), regular, null, locale);
			createCell(table, 1, 9, messageSource.getMessage("export.odt.H2020.td5", null, locale), regular, null,
					locale);
			createCell(table, 2, 9, messageSource.getMessage("dmp.edit.usedCollectionModes", null, locale), regular,
					null, locale);
			createCell(table, 3, 9, dmp.getUsedCollectionModes(), regular, null, locale);
			createCell(table, 1, 10, messageSource.getMessage("export.odt.H2020.td6", null, locale), regular, null,
					locale);
			createCell(table, 2, 10, messageSource.getMessage("dmp.edit.storageExpectedSize", null, locale), regular,
					null, locale);
			createCell(table, 3, 10, dmp.getStorageExpectedSize(), regular, null, locale);
			createCell(table, 1, 11, messageSource.getMessage("export.odt.H2020.td7", null, locale), regular, null,
					locale);
			createCell(table, 2, 11, messageSource.getMessage("dmp.edit.expectedUsage", null, locale), regular, null,
					locale);
			createCell(table, 3, 11, dmp.getExpectedUsage(), regular, null, locale);
			createCell(table, 2, 12, messageSource.getMessage("dmp.edit.storage.headline", null, locale), regular, null,
					locale);
			createCell(table, 3, 12, "?", regular, null, locale);
			createCell(table, 0, 13, messageSource.getMessage("export.odt.H2020.td8", null, locale), regular_bold, null,
					locale);
			createCell(table, 0, 14, messageSource.getMessage("export.odt.H2020.td9", null, locale), regular, null,
					locale);
			createCell(table, 1, 14, messageSource.getMessage("export.odt.H2020.td10", null, locale), regular, null,
					locale);
			createCell(table, 2, 14, messageSource.getMessage("dmp.edit.searchableData", null, locale), regular, null,
					locale);
			createCell(table, 3, 14, dmp.getSearchableData(), regular, null, locale);
			createCell(table, 1, 15, messageSource.getMessage("export.odt.H2020.td11", null, locale), regular, null,
					locale);
			createCell(table, 2, 15, messageSource.getMessage("dmp.edit.usedPID", null, locale), regular, null, locale);
			createCell(table, 3, 15, dmp.getUsedPID(), regular, null, locale);
			createCell(table, 1, 16, messageSource.getMessage("export.odt.H2020.td12", null, locale), regular, null,
					locale);
			createCell(table, 2, 16, "dmp.edit.storageTechnologies", regular, null, locale);
			createCell(table, 3, 16, "?", regular, null, locale);
			createCell(table, 1, 17, messageSource.getMessage("export.odt.H2020.td13", null, locale), regular, null,
					locale);
			createCell(table, 2, 17, messageSource.getMessage("export.odt.H2020.no.equivalent", null, locale), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 1, 18, messageSource.getMessage("export.odt.H2020.td14", null, locale), regular, null,
					locale);
			createCell(table, 2, 18, messageSource.getMessage("dmp.edit.storageBackups", null, locale), regular, null,
					locale);
			createCell(table, 3, 18, dmp.getStorageBackups(), regular, null, locale);
			createCell(table, 2, 19, messageSource.getMessage("dmp.edit.metaGeneration", null, locale), regular, null,
					locale);
			createCell(table, 3, 19, dmp.getMetaGeneration(), regular, null, locale);
			createCell(table, 1, 20, messageSource.getMessage("export.odt.H2020.td15", null, locale), regular, null,
					locale);
			createCell(table, 2, 20, messageSource.getMessage("dmp.edit.selectedMetaPurposes", null, locale), regular,
					null, locale);
			createCell(table, 3, 20, dmp.getSelectedMetaPurposes(), regular, null, locale);
			createCell(table, 2, 21, messageSource.getMessage("dmp.edit.metaFramework", null, locale), regular, null,
					locale);
			createCell(table, 3, 21, dmp.getMetaFramework(), regular, null, locale);
			createCell(table, 2, 22, messageSource.getMessage("dmp.edit.metaDescription", null, locale), regular, null,
					locale);
			createCell(table, 3, 22, dmp.getMetaDescription(), regular, null, locale);
			createCell(table, 2, 23, messageSource.getMessage("dmp.edit.metaMonitor", null, locale), regular, null,
					locale);
			createCell(table, 3, 23, dmp.getMetaMonitor(), regular, null, locale);
			createCell(table, 2, 24, messageSource.getMessage("dmp.edit.metaFormat", null, locale), regular, null,
					locale);
			createCell(table, 0, 25, messageSource.getMessage("export.odt.H2020.td16", null, locale), regular, null,
					locale);
			createCell(table, 1, 25, messageSource.getMessage("export.odt.H2020.td17", null, locale), regular, null,
					locale);
			createCell(table, 2, 25, messageSource.getMessage("dmp.edit.sensitiveData", null, locale), regular, null,
					locale);
			createCell(table, 3, 25, dmp.getSensitiveData(), regular, null, locale);
			createCell(table, 2, 26, messageSource.getMessage("dmp.edit.accessReasonAuthor", null, locale), regular,
					null, locale);
			createCell(table, 3, 26, dmp.getAccessReasonAuthor(), regular, null, locale);
			createCell(table, 2, 27, messageSource.getMessage("dmp.edit.noAccessReason", null, locale), regular, null,
					locale);
			createCell(table, 3, 27, dmp.getNoAccessReason(), regular, null, locale);
			createCell(table, 1, 28, messageSource.getMessage("export.odt.H2020.td18", null, locale), regular, null,
					locale);
			createCell(table, 2, 28, messageSource.getMessage("dmp.edit.publStrategy", null, locale), regular, null,
					locale);
			createCell(table, 3, 28, dmp.getPublStrategy(), regular, null, locale);
			createCell(table, 2, 29, messageSource.getMessage("dmp.edit.depositName", null, locale), regular, null,
					locale);
			createCell(table, 3, 29, dmp.getDepositName(), regular, null, locale);
			createCell(table, 2, 30, messageSource.getMessage("dmp.edit.accessCosts", null, locale), regular, null,
					locale);
			createCell(table, 3, 30, dmp.isAccessCosts(), regular, null, locale);
			createCell(table, 2, 31, messageSource.getMessage("dmp.edit.releaseObligation", null, locale), regular,
					null, locale);
			createCell(table, 3, 31, dmp.isReleaseObligation(), regular, null, locale);
			createCell(table, 2, 32, messageSource.getMessage("dmp.edit.clarifiedRights", null, locale), regular, null,
					locale);
			createCell(table, 3, 32, dmp.isClarifiedRights(), regular, null, locale);
			createCell(table, 2, 33, messageSource.getMessage("dmp.edit.acquisitionAgreement", null, locale), regular,
					null, locale);
			createCell(table, 3, 33, dmp.isAcquisitionAgreement(), regular, null, locale);
			createCell(table, 1, 34, messageSource.getMessage("export.odt.H2020.td19", null, locale), regular, null,
					locale);
			createCell(table, 2, 34, messageSource.getMessage("dmp.edit.storageRequirements", null, locale), regular,
					null, locale);
			createCell(table, 3, 34, dmp.isStorageRequirements(), regular, null, locale);
			createCell(table, 2, 35, messageSource.getMessage("dmp.edit.storageRequirementsTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 35, dmp.getStorageRequirementsTxt(), regular, null, locale);
			createCell(table, 1, 36, messageSource.getMessage("export.odt.H2020.td20", null, locale), regular, null,
					locale);
			createCell(table, 2, 36, messageSource.getMessage("dmp.edit.storagePlaces", null, locale), regular, null,
					locale);
			createCell(table, 3, 36, dmp.getStoragePlaces(), regular, null, locale);
			createCell(table, 2, 37, messageSource.getMessage("dmp.edit.storageDuration", null, locale), regular, null,
					locale);
			createCell(table, 3, 37, dmp.getStorageDuration(), regular, null, locale);
			createCell(table, 1, 38, messageSource.getMessage("export.odt.H2020.td21", null, locale), regular, null,
					locale);
			createCell(table, 2, 38, messageSource.getMessage("dmp.edit.internalCopyright", null, locale), regular,
					null, locale);
			createCell(table, 3, 38, dmp.isInternalCopyright(), regular, null, locale);
			createCell(table, 2, 39, messageSource.getMessage("dmp.edit.internalCopyrightTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 39, dmp.getInternalCopyrightTxt(), regular, null, locale);
			createCell(table, 2, 40, messageSource.getMessage("dmp.edit.externalCopyright", null, locale), regular,
					null, locale);
			createCell(table, 3, 40, dmp.isExternalCopyright(), regular, null, locale);
			createCell(table, 2, 41, messageSource.getMessage("dmp.edit.externalCopyrightTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 41, dmp.getExternalCopyrightTxt(), regular, null, locale);
			createCell(table, 0, 42, messageSource.getMessage("export.odt.H2020.td22", null, locale), regular, null,
					locale);
			createCell(table, 1, 42, messageSource.getMessage("export.odt.H2020.td23", null, locale), regular, null,
					locale);
			createCell(table, 2, 42, messageSource.getMessage("export.odt.H2020.no.equivalent", null, locale), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 1, 43, messageSource.getMessage("export.odt.H2020.td24", null, locale), regular, null,
					locale);
			createCell(table, 2, 43, messageSource.getMessage("export.odt.H2020.no.equivalent", null, locale), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 44, messageSource.getMessage("export.odt.H2020.td25", null, locale), regular, null,
					locale);
			createCell(table, 1, 44, messageSource.getMessage("export.odt.H2020.td26", null, locale), regular, null,
					locale);
			createCell(table, 2, 44, messageSource.getMessage("dmp.edit.usageRestriction", null, locale), regular, null,
					locale);
			createCell(table, 3, 44, dmp.getUsageRestriction(), regular, null, locale);
			createCell(table, 1, 45, messageSource.getMessage("export.odt.H2020.td27", null, locale), regular, null,
					locale);
			createCell(table, 2, 45, messageSource.getMessage("dmp.edit.transferTime", null, locale), regular, null,
					locale);
			createCell(table, 3, 45, dmp.getTransferTime(), regular, null, locale);
			createCell(table, 2, 46, messageSource.getMessage("dmp.edit.initialUsage", null, locale), regular, null,
					locale);
			createCell(table, 3, 46, dmp.getInitialUsage(), regular, null, locale);
			createCell(table, 1, 47, messageSource.getMessage("export.odt.H2020.td28", null, locale), regular, null,
					locale);
			createCell(table, 2, 47, messageSource.getMessage("export.odt.H2020.no.equivalent", null, locale), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 1, 48, messageSource.getMessage("export.odt.H2020.td29", null, locale), regular, null,
					locale);
			createCell(table, 2, 48, messageSource.getMessage("dmp.edit.reliabilityTraining", null, locale), regular,
					null, locale);
			createCell(table, 3, 48, dmp.getReliabilityTraining(), regular, null, locale);
			createCell(table, 2, 49, messageSource.getMessage("dmp.edit.multipleMeasurements", null, locale), regular,
					null, locale);
			createCell(table, 3, 49, dmp.getMultipleMeasurements(), regular, null, locale);
			createCell(table, 2, 50, messageSource.getMessage("dmp.edit.qualitityOther", null, locale), regular, null,
					locale);
			createCell(table, 3, 50, dmp.getQualitityOther(), regular, null, locale);
			createCell(table, 1, 51, messageSource.getMessage("export.odt.H2020.td30", null, locale), regular, null,
					locale);
			createCell(table, 2, 51, messageSource.getMessage("dmp.edit.storageSuccessionTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 51, dmp.getStorageSuccessionTxt(), regular, null, locale);
			createCell(table, 0, 52, messageSource.getMessage("export.odt.H2020.td31", null, locale), regular_bold,
					null, locale);
			createCell(table, 1, 52, messageSource.getMessage("export.odt.H2020.td32", null, locale), regular, null,
					locale);
			createCell(table, 2, 52, messageSource.getMessage("dmp.edit.specificCosts", null, locale), regular, null,
					locale);
			createCell(table, 3, 52, dmp.getSpecificCosts(), regular, null, locale);
			createCell(table, 2, 53, messageSource.getMessage("dmp.edit.specificCostsTxt", null, locale), regular, null,
					locale);
			createCell(table, 3, 53, dmp.getSpecificCostsTxt(), regular, null, locale);
			createCell(table, 2, 54, messageSource.getMessage("dmp.edit.bearCost", null, locale), regular, null,
					locale);
			createCell(table, 3, 54, dmp.getBearCost(), regular, null, locale);
			createCell(table, 1, 55, messageSource.getMessage("export.odt.H2020.td33", null, locale), regular, null,
					locale);
			createCell(table, 2, 55, messageSource.getMessage("dmp.edit.responsibleUnit", null, locale), regular, null,
					locale);
			createCell(table, 3, 55, dmp.getResponsibleUnit(), regular, null, locale);
			createCell(table, 1, 56, messageSource.getMessage("export.odt.H2020.td34", null, locale), regular, null,
					locale);
			createCell(table, 2, 56, messageSource.getMessage("export.odt.H2020.no.equivalent", null, locale), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 57, messageSource.getMessage("export.odt.H2020.td35", null, locale), regular_bold,
					null, locale);
			createCell(table, 1, 57, messageSource.getMessage("export.odt.H2020.td36", null, locale), regular, null,
					locale);
			createCell(table, 2, 57, messageSource.getMessage("dmp.edit.storageResponsible", null, locale), regular,
					null, locale);
			createCell(table, 3, 57, dmp.getStorageResponsible(), regular, null, locale);
			createCell(table, 2, 58, messageSource.getMessage("dmp.edit.dataProtection", null, locale), regular, null,
					locale);
			createCell(table, 3, 58, dmp.isDataProtection(), regular, null, locale);
			createCell(table, 2, 59, messageSource.getMessage("dmp.edit.protectionRequirements", null, locale), regular,
					null, locale);
			createCell(table, 3, 59, dmp.getProtectionRequirements(), regular, null, locale);
			createCell(table, 2, 60, messageSource.getMessage("dmp.edit.storageTransfer", null, locale), regular, null,
					locale);
			createCell(table, 3, 60, dmp.getStorageTransfer(), regular, null, locale);
			createCell(table, 0, 61, messageSource.getMessage("export.odt.H2020.td37", null, locale), regular_bold,
					null, locale);
			createCell(table, 1, 61, messageSource.getMessage("export.odt.H2020.td38", null, locale), regular, null,
					locale);
			createCell(table, 2, 61, messageSource.getMessage("dmp.edit.consentObtained", null, locale), regular, null,
					locale);
			createCell(table, 3, 61, dmp.isConsentObtained(), regular, null, locale);
			createCell(table, 2, 62, messageSource.getMessage("dmp.edit.consentObtainedTxt", null, locale), regular,
					null, locale);
			createCell(table, 3, 62, dmp.getConsentObtainedTxt(), regular, null, locale);
			createCell(table, 2, 63, messageSource.getMessage("dmp.edit.sharingConsidered", null, locale), regular,
					null, locale);
			createCell(table, 3, 63, dmp.isSharingConsidered(), regular, null, locale);
			createCell(table, 2, 64, messageSource.getMessage("dmp.edit.irbApproval", null, locale), regular, null,
					locale);
			createCell(table, 3, 64, dmp.isIrbApproval(), regular, null, locale);
			createCell(table, 2, 65, messageSource.getMessage("dmp.edit.irbApprovalTxt", null, locale), regular, null,
					locale);
			createCell(table, 3, 65, dmp.getIrbApprovalTxt(), regular, null, locale);
			createCell(table, 0, 66, messageSource.getMessage("export.odt.H2020.td39", null, locale), regular_bold,
					null, locale);
			createCell(table, 1, 66, messageSource.getMessage("export.odt.H2020.td40", null, locale), regular, null,
					locale);
			createCell(table, 2, 66, messageSource.getMessage("dmp.edit.funderRequirements", null, locale), regular,
					null, locale);
			createCell(table, 3, 66, dmp.getFunderRequirements(), regular, null, locale);

			doc.save(baos);
			doc.close();
			log.trace("Leaving createH2020Doc");
			return baos.toByteArray();
		} else {
			if (dmp == null) {
				log.warn("Error during createH2020Doc - DMP is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.dmp", null, locale),
						DataWizErrorCodes.NO_DATA_ERROR);
			} else {
				log.warn("Error during createH2020Doc - Project is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.project", null, locale),
						DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
			}
		}
	}

	/**
	 * This function creates the DFG OTF document for the DPM export On success it
	 * returns an byte array, otherwise an exception or an empty byte array!
	 * 
	 * @param ProjectForm,
	 *            which contains all necessary data for the export
	 * @param Locale
	 *            to distinguish whether the export is in English or German. At the
	 *            moment only as a construct, because the export is currently only
	 *            implemented in German.
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
			// Erste Seite
			doc.addParagraph(messageSource.getMessage("export.odt.DFG.zpid", null, locale)).setFont(blue_reg);
			doc.addColumnBreak();
			Paragraph par = doc.addParagraph(messageSource.getMessage("export.odt.DFG.headline", null, locale));
			par.setFont(headline);
			doc.addColumnBreak();
			// Masterpage
			MasterPage master = MasterPage.getOrCreateMasterPage(doc, "DFG");
			master.setNumberFormat(NumberFormat.HINDU_ARABIC_NUMBER.toString());
			// master.setMargins(15, 15, 15, 15);
			doc.addPageBreak(par, master);
			// Zweite Seite
			doc.addParagraph(messageSource.getMessage("export.odt.DFG.zu1", null, locale)).setFont(regular);
			doc.addColumnBreak();
			par = doc.addParagraph(messageSource.getMessage("export.odt.DFG.zu2", null, locale));
			par.setFont(regular_it);
			par.appendHyperlink(messageSource.getMessage("export.odt.DFG.zu2l", null, locale),
					new URI(messageSource.getMessage("export.odt.DFG.zu2l", null, locale)));
			doc.addColumnBreak();

			Table table = doc.addTable(56, 3);
			// merge
			table.getCellRangeByPosition(0, 0, 0, 1).merge();
			table.getCellRangeByPosition(1, 0, 2, 0).merge();
			table.getCellRangeByPosition(0, 2, 0, 4).merge();
			table.getCellRangeByPosition(1, 2, 1, 3).merge();
			table.getCellRangeByPosition(2, 2, 2, 3).merge();
			table.getCellRangeByPosition(0, 5, 0, 12).merge();
			table.getCellRangeByPosition(1, 9, 1, 10).merge();
			table.getCellRangeByPosition(2, 9, 2, 10).merge();
			table.getCellRangeByPosition(0, 13, 0, 17).merge();
			table.getCellRangeByPosition(0, 18, 0, 22).merge();
			table.getCellRangeByPosition(1, 18, 1, 19).merge();
			table.getCellRangeByPosition(2, 18, 2, 19).merge();
			table.getCellRangeByPosition(0, 23, 0, 33).merge();
			table.getCellRangeByPosition(0, 34, 0, 38).merge();
			table.getCellRangeByPosition(0, 41, 0, 42).merge();
			table.getCellRangeByPosition(0, 43, 0, 54).merge();

			table.getColumnByIndex(0).setWidth(30);
			table.getColumnByIndex(1).setWidth(30);
			table.getColumnByIndex(2).setWidth(50);

			createCell(table, 0, 0, messageSource.getMessage("export.odt.DFG.th1", null, locale), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 1, 0, messageSource.getMessage("export.odt.DFG.th2", null, locale), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 1, 1, messageSource.getMessage("export.odt.DFG.th3", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 2, 1, messageSource.getMessage("export.odt.DFG.th4", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 2, messageSource.getMessage("export.odt.DFG.td1", null, locale), regular, null,
					locale);
			createCell(table, 1, 2, messageSource.getMessage("dmp.edit.dataReproducibility", null, locale), regular,
					null, locale);
			createCell(table, 2, 2, dmp.getDataReproducibility(), regular, null, locale);
			createCell(table, 1, 4, messageSource.getMessage("dmp.edit.expectedUsage", null, locale), regular, null,
					locale);
			createCell(table, 2, 4, dmp.getExpectedUsage(), regular, null, locale);
			createCell(table, 0, 5, messageSource.getMessage("export.odt.DFG.td2", null, locale), regular, null,
					locale);
			createCell(table, 1, 5, messageSource.getMessage("dmp.edit.publStrategy", null, locale), regular, null,
					locale);
			createCell(table, 2, 5, dmp.getPublStrategy(), regular, null, locale);
			createCell(table, 1, 6, messageSource.getMessage("dmp.edit.searchableData", null, locale), regular, null,
					locale);
			createCell(table, 2, 6, dmp.getSearchableData(), regular, null, locale);
			createCell(table, 1, 7, messageSource.getMessage("dmp.edit.initialUsage", null, locale), regular, null,
					locale);
			createCell(table, 2, 7, dmp.getInitialUsage(), regular, null, locale);
			createCell(table, 1, 8, messageSource.getMessage("dmp.edit.usageRestriction", null, locale), regular, null,
					locale);
			createCell(table, 2, 8, dmp.getUsageRestriction(), regular, null, locale);
			createCell(table, 1, 9, messageSource.getMessage("dmp.edit.accessCosts", null, locale), regular, null,
					locale);
			createCell(table, 2, 9, dmp.isAccessCosts(), regular, null, locale);
			// createCell(table, 1, 10, messageSource.getMessage("dmp.edit.accessCostsTxt",
			// null, locale), regular, null, locale);
			createCell(table, 1, 11, messageSource.getMessage("dmp.edit.accessReasonAuthor", null, locale), regular,
					null, locale);
			createCell(table, 2, 11, dmp.getAccessReasonAuthor(), regular, null, locale);
			createCell(table, 1, 12, messageSource.getMessage("dmp.edit.noAccessReason", null, locale), regular, null,
					locale);
			createCell(table, 2, 12, dmp.getNoAccessReason(), regular, null, locale);
			createCell(table, 0, 13, messageSource.getMessage("export.odt.DFG.td3", null, locale), regular, null,
					locale);
			createCell(table, 1, 13, messageSource.getMessage("dmp.edit.projectAims", null, locale), regular, null,
					locale);
			createCell(table, 2, 13, project.getDescription(), regular, null, locale);
			createCell(table, 1, 14, messageSource.getMessage("dmp.edit.usedDataTypes", null, locale), regular, null,
					locale);
			createCell(table, 2, 14, dmp.getUsedDataTypes(), regular, null, locale);
			createCell(table, 1, 15, messageSource.getMessage("dmp.edit.existingData", null, locale), regular, null,
					locale);
			createCell(table, 2, 15, dmp.getExistingData(), regular, null, locale);
			createCell(table, 1, 16, messageSource.getMessage("dmp.edit.dataCitation", null, locale), regular, null,
					locale);
			createCell(table, 2, 16, dmp.getDataCitation(), regular, null, locale);
			createCell(table, 1, 17, messageSource.getMessage("dmp.edit.usedCollectionModes", null, locale), regular,
					null, locale);
			createCell(table, 2, 17, dmp.getUsedCollectionModes(), regular, null, locale);
			createCell(table, 0, 18, messageSource.getMessage("export.odt.DFG.td4", null, locale), regular, null,
					locale);
			// createCell(table, 1, 18,
			// messageSource.getMessage("dmp.edit.qualityAssurance", null, locale), regular,
			// null, locale);
			createCell(table, 1, 18, messageSource.getMessage("dmp.edit.reliabilityTraining", null, locale), regular,
					null, locale);
			createCell(table, 2, 18, dmp.getReliabilityTraining(), regular, null, locale);
			createCell(table, 1, 20, messageSource.getMessage("dmp.edit.multipleMeasurements", null, locale), regular,
					null, locale);
			createCell(table, 2, 20, dmp.getMultipleMeasurements(), regular, null, locale);
			createCell(table, 1, 21, messageSource.getMessage("dmp.edit.qualitityOther", null, locale), regular, null,
					locale);
			createCell(table, 2, 21, dmp.getQualitityOther(), regular, null, locale);
			createCell(table, 1, 22, messageSource.getMessage("dmp.edit.planningAdherence", null, locale), regular,
					null, locale);
			createCell(table, 2, 22, dmp.getPlanningAdherence(), regular, null, locale);
			createCell(table, 0, 23, messageSource.getMessage("export.odt.DFG.td5", null, locale), regular, null,
					locale);
			createCell(table, 1, 23, messageSource.getMessage("dmp.edit.existingDataRelevance", null, locale), regular,
					null, locale);
			createCell(table, 2, 23, dmp.getExistingDataRelevance(), regular, null, locale);
			createCell(table, 1, 24, messageSource.getMessage("dmp.edit.existingDataIntegration", null, locale),
					regular, null, locale);
			createCell(table, 2, 24, dmp.getExistingDataIntegration(), regular, null, locale);
			createCell(table, 1, 25, messageSource.getMessage("dmp.edit.dataSelection", null, locale), regular, null,
					locale);
			createCell(table, 2, 25, dmp.isDataSelection(), regular, null, locale);
			createCell(table, 1, 26, messageSource.getMessage("dmp.edit.selectionTime", null, locale), regular, null,
					locale);
			createCell(table, 2, 26, dmp.getSelectionTime(), regular, null, locale);
			createCell(table, 1, 27, messageSource.getMessage("dmp.edit.selectionResp", null, locale), regular, null,
					locale);
			createCell(table, 2, 27, dmp.getSelectionResp(), regular, null, locale);
			createCell(table, 1, 28, messageSource.getMessage("dmp.edit.deleteProcedure", null, locale), regular, null,
					locale);
			createCell(table, 2, 28, dmp.getDeleteProcedure(), regular, null, locale);
			createCell(table, 1, 29, messageSource.getMessage("dmp.edit.storagePlaces", null, locale), regular, null,
					locale);
			createCell(table, 2, 29, dmp.getStoragePlaces(), regular, null, locale);
			createCell(table, 1, 30, messageSource.getMessage("dmp.edit.storageBackups", null, locale), regular, null,
					locale);
			createCell(table, 2, 30, dmp.getStorageBackups(), regular, null, locale);
			createCell(table, 1, 31, messageSource.getMessage("dmp.edit.managementWorkflow", null, locale), regular,
					null, locale);
			createCell(table, 2, 31, dmp.isManagementWorkflow(), regular, null, locale);
			createCell(table, 1, 32, messageSource.getMessage("dmp.edit.managementWorkflowTxt", null, locale), regular,
					null, locale);
			createCell(table, 2, 32, dmp.getManagementWorkflowTxt(), regular, null, locale);
			createCell(table, 1, 33, messageSource.getMessage("dmp.edit.funderRequirements", null, locale), regular,
					null, locale);
			createCell(table, 2, 33, dmp.getFunderRequirements(), regular, null, locale);
			createCell(table, 0, 34, messageSource.getMessage("export.odt.DFG.td6", null, locale), regular, null,
					locale);
			createCell(table, 1, 34, messageSource.getMessage("dmp.edit.storageResponsible", null, locale), regular,
					null, locale);
			createCell(table, 2, 34, dmp.getStorageResponsible(), regular, null, locale);
			createCell(table, 1, 35, messageSource.getMessage("dmp.edit.storageDuration", null, locale), regular, null,
					locale);
			createCell(table, 2, 35, dmp.getStorageDuration(), regular, null, locale);
			createCell(table, 1, 36, messageSource.getMessage("dmp.edit.sensitiveData", null, locale), regular, null,
					locale);
			createCell(table, 2, 36, dmp.getSensitiveData(), regular, null, locale);
			createCell(table, 1, 37, messageSource.getMessage("dmp.edit.clarifiedRights", null, locale), regular, null,
					locale);
			createCell(table, 2, 37, dmp.isClarifiedRights(), regular, null, locale);
			createCell(table, 1, 38, messageSource.getMessage("dmp.edit.usedPID", null, locale), regular, null, locale);
			createCell(table, 2, 38, dmp.getUsedPID(), regular, null, locale);
			createCell(table, 0, 39, messageSource.getMessage("export.odt.DFG.td7", null, locale), regular, null,
					locale);
			createCell(table, 1, 39, messageSource.getMessage("dmp.edit.fileFormat", null, locale), regular, null,
					locale);
			createCell(table, 2, 39, dmp.getFileFormat(), regular, null, locale);
			createCell(table, 0, 40, messageSource.getMessage("export.odt.DFG.td8", null, locale), regular, null,
					locale);
			createCell(table, 1, 40, messageSource.getMessage("dmp.edit.metaFramework", null, locale), regular, null,
					locale);
			createCell(table, 2, 40, dmp.getMetaFramework(), regular, null, locale);
			createCell(table, 0, 41, messageSource.getMessage("export.odt.DFG.td9", null, locale), regular, null,
					locale);
			createCell(table, 1, 41, messageSource.getMessage("dmp.edit.depositName", null, locale), regular, null,
					locale);
			createCell(table, 2, 41, dmp.getDepositName(), regular, null, locale);
			createCell(table, 1, 42, messageSource.getMessage("dmp.edit.acquisitionAgreement", null, locale), regular,
					null, locale);
			createCell(table, 2, 42, dmp.isAcquisitionAgreement(), regular, null, locale);
			createCell(table, 0, 43, messageSource.getMessage("export.odt.DFG.td10", null, locale), regular, null,
					locale);
			createCell(table, 1, 43, messageSource.getMessage("dmp.edit.dataProtection", null, locale), regular, null,
					locale);
			createCell(table, 2, 43, dmp.isDataProtection(), regular, null, locale);
			createCell(table, 1, 44, messageSource.getMessage("dmp.edit.protectionRequirements", null, locale), regular,
					null, locale);
			createCell(table, 2, 44, dmp.getProtectionRequirements(), regular, null, locale);
			createCell(table, 1, 45, messageSource.getMessage("dmp.edit.consentObtained", null, locale), regular, null,
					locale);
			createCell(table, 2, 45, dmp.isConsentObtained(), regular, null, locale);
			createCell(table, 1, 46, messageSource.getMessage("dmp.edit.consentObtained", null, locale), regular, null,
					locale);
			createCell(table, 2, 46, dmp.getConsentObtainedTxt(), regular, null, locale);
			createCell(table, 1, 47, messageSource.getMessage("dmp.edit.sharingConsidered", null, locale), regular,
					null, locale);
			createCell(table, 2, 47, dmp.isSharingConsidered(), regular, null, locale);
			createCell(table, 1, 48, messageSource.getMessage("dmp.edit.irbApproval", null, locale), regular, null,
					locale);
			createCell(table, 2, 48, dmp.isIrbApproval(), regular, null, locale);
			createCell(table, 1, 49, messageSource.getMessage("dmp.edit.irbApprovalTxt", null, locale), regular, null,
					locale);
			createCell(table, 2, 49, dmp.getIrbApprovalTxt(), regular, null, locale);
			createCell(table, 1, 50, messageSource.getMessage("dmp.edit.sensitiveDataIncluded", null, locale), regular,
					null, locale);
			createCell(table, 2, 50, dmp.isSensitiveDataIncluded(), regular, null, locale);
			createCell(table, 1, 51, messageSource.getMessage("dmp.edit.externalCopyright", null, locale), regular,
					null, locale);
			createCell(table, 2, 51, dmp.isExternalCopyright(), regular, null, locale);
			createCell(table, 1, 52, messageSource.getMessage("dmp.edit.externalCopyrightTxt", null, locale), regular,
					null, locale);
			createCell(table, 2, 52, dmp.getExternalCopyrightTxt(), regular, null, locale);
			createCell(table, 1, 53, messageSource.getMessage("dmp.edit.internalCopyright", null, locale), regular,
					null, locale);
			createCell(table, 2, 53, dmp.isInternalCopyright(), regular, null, locale);
			createCell(table, 1, 54, messageSource.getMessage("dmp.edit.internalCopyrightTxt", null, locale), regular,
					null, locale);
			createCell(table, 2, 54, dmp.getInternalCopyrightTxt(), regular, null, locale);
			createCell(table, 0, 55, messageSource.getMessage("export.odt.DFG.td11", null, locale), regular, null,
					locale);
			createCell(table, 1, 55, messageSource.getMessage("dmp.edit.transferTime", null, locale), regular, null,
					locale);
			createCell(table, 2, 55, dmp.getTransferTime(), regular, null, locale);

			doc.addColumnBreak();
			doc.addParagraph(messageSource.getMessage("export.odt.DFG.zu3", null, locale)).setFont(regular);
			doc.addColumnBreak();
			table = doc.addTable(3, 3);

			// merge
			table.getCellRangeByPosition(1, 0, 2, 0).merge();

			// content
			createCell(table, 0, 0, messageSource.getMessage("export.odt.DFG.th1", null, locale), regular_bold, null,
					locale);
			createCell(table, 1, 0, messageSource.getMessage("export.odt.DFG.th2", null, locale), regular_bold, null,
					locale);
			createCell(table, 1, 1, messageSource.getMessage("export.odt.DFG.th5", null, locale), regular_bold, null,
					locale);
			createCell(table, 2, 1, messageSource.getMessage("export.odt.DFG.th6", null, locale), regular_bold, null,
					locale);
			createCell(table, 0, 2, messageSource.getMessage("export.odt.DFG.td12", null, locale), regular, null,
					locale);
			createCell(table, 1, 2, messageSource.getMessage("dmp.edit.specificCosts", null, locale), regular, null,
					locale);
			createCell(table, 2, 2, dmp.getSpecificCosts(), regular, null, locale);

			doc.addColumnBreak();

			par = doc.addParagraph(messageSource.getMessage("export.odt.DFG.ft1", null, locale));
			par.setFont(regular);
			par.appendHyperlink(messageSource.getMessage("export.odt.DFG.ft2", null, locale),
					new URI(messageSource.getMessage("export.odt.DFG.ft2l", null, locale)));
			doc.addColumnBreak();
			doc.addParagraph(messageSource.getMessage("export.odt.DFG.src", null, locale)).setFont(regular_bold);
			doc.addColumnBreak();
			par = doc.addParagraph(messageSource.getMessage("export.odt.DFG.src1", null, locale));
			par.setFont(regular_it);
			par.appendHyperlink(messageSource.getMessage("export.odt.DFG.src1l", null, locale),
					new URI(messageSource.getMessage("export.odt.DFG.src1l", null, locale)));
			doc.addColumnBreak();
			par = doc.addParagraph(messageSource.getMessage("export.odt.DFG.src2", null, locale));
			par.setFont(regular_it);
			par.appendHyperlink(messageSource.getMessage("export.odt.DFG.src2l", null, locale),
					new URI(messageSource.getMessage("export.odt.DFG.src2l", null, locale)));

			doc.save(baos);
			doc.close();
			log.trace("Leaving createDFGDoc");
			return baos.toByteArray();
		} else {
			if (dmp == null) {
				log.warn("Error during createDFGDoc - DMP is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.dmp", null, locale),
						DataWizErrorCodes.NO_DATA_ERROR);
			} else {
				log.warn("Error during createDFGDoc - Project is null");
				throw new DataWizSystemException(messageSource.getMessage("export.odt.error.project", null, locale),
						DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
			}
		}
	}

	/**
	 * This function creates the PsychData OTF document for the DPM export On
	 * success it returns an byte array, otherwise an exception or an empty byte
	 * array!
	 * 
	 * @param ProjectForm,
	 *            which contains all necessary data for the export
	 * @param Locale
	 *            to distinguish whether the export is in English or German. At the
	 *            moment only as a construct, because the export is currently only
	 *            implemented in German.
	 * @return The Open Text document as a byte array
	 * @throws Exception
	 */
	public byte[] createPsychdataDoc(final StudyForm sForm, final Locale locale) throws Exception {
		log.trace("Entering createPsychdataDoc");
		StudyDTO study = sForm.getStudy();
		if (study != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TextDocument doc = TextDocument.newTextDocument();
			// Erste Seite
			doc.addParagraph(messageSource.getMessage("export.odt.PsychData.zpid", null, locale)).setFont(blue_reg);
			doc.addColumnBreak();
			Paragraph par = doc.addParagraph(messageSource.getMessage("export.odt.PsychData.headline", null, locale));
			par.setFont(headline);
			doc.addColumnBreak();
			// Masterpage
			MasterPage master = MasterPage.getOrCreateMasterPage(doc, "Psychdata");
			master.setNumberFormat(NumberFormat.HINDU_ARABIC_NUMBER.toString());
			// master.setMargins(15, 15, 15, 15);
			doc.addPageBreak(par, master);
			// Zweite Seite
			doc.addParagraph(messageSource.getMessage("export.odt.PsychData.zu1h", null, locale)).setFont(blue_large);
			doc.addColumnBreak();
			doc.addParagraph(messageSource.getMessage("export.odt.PsychData.zu1", null, locale)).setFont(regular);
			doc.addColumnBreak();

			Table table = doc.addTable(69, 4);
			// merge
			table.getCellRangeByPosition(0, 0, 1, 0).merge();
			table.getCellRangeByPosition(2, 0, 3, 0).merge();
			table.getCellRangeByPosition(0, 7, 0, 8).merge();
			table.getCellRangeByPosition(1, 7, 1, 8).merge();
			table.getCellRangeByPosition(0, 13, 0, 16).merge();
			table.getCellRangeByPosition(1, 13, 1, 16).merge();
			table.getCellRangeByPosition(0, 17, 0, 20).merge();
			table.getCellRangeByPosition(1, 17, 1, 20).merge();
			table.getCellRangeByPosition(0, 21, 1, 21).merge();
			table.getCellRangeByPosition(0, 23, 0, 26).merge();
			table.getCellRangeByPosition(1, 23, 1, 26).merge();
			table.getCellRangeByPosition(0, 27, 0, 40).merge();
			table.getCellRangeByPosition(1, 27, 1, 40).merge();
			table.getCellRangeByPosition(0, 41, 0, 42).merge();
			table.getCellRangeByPosition(1, 41, 1, 42).merge();
			table.getCellRangeByPosition(0, 62, 0, 63).merge();
			table.getCellRangeByPosition(1, 62, 1, 63).merge();
			table.getCellRangeByPosition(0, 64, 0, 66).merge();
			table.getCellRangeByPosition(1, 64, 1, 66).merge();
			table.getCellRangeByPosition(0, 67, 0, 68).merge();
			table.getCellRangeByPosition(1, 67, 1, 68).merge();

			// inhalt
			createCell(table, 0, 0, messageSource.getMessage("export.odt.PsychData.pd", null, locale), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 2, 0, messageSource.getMessage("export.odt.PsychData.dw", null, locale), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 0, 1, messageSource.getMessage("export.odt.PsychData.th1", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 1, 1, messageSource.getMessage("export.odt.PsychData.th2", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 2, 1, messageSource.getMessage("export.odt.PsychData.th3", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 3, 1, messageSource.getMessage("export.odt.PsychData.th4", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 2, messageSource.getMessage("export.odt.PsychData.td1", null, locale), regular, null,
					locale);
			createCell(table, 1, 2, messageSource.getMessage("export.odt.PsychData.td2", null, locale), regular, null,
					locale);
			createCell(table, 2, 2, messageSource.getMessage("study.title", null, locale), regular, null, locale);
			createCell(table, 3, 2, study.getTitle(), regular, null, locale);
			createCell(table, 0, 3, messageSource.getMessage("export.odt.PsychData.td3", null, locale), regular, null,
					locale);
			createCell(table, 1, 3, messageSource.getMessage("export.odt.PsychData.td4", null, locale), regular, null,
					locale);
			createCell(table, 2, 3, messageSource.getMessage("study.transTitle", null, locale), regular, null, locale);
			createCell(table, 3, 3, study.getTransTitle(), regular, null, locale);
			createCell(table, 0, 4, messageSource.getMessage("export.odt.PsychData.td5", null, locale), regular, null,
					locale);
			createCell(table, 1, 4, messageSource.getMessage("export.odt.PsychData.td6", null, locale), regular, null,
					locale);
			createCell(table, 2, 4, messageSource.getMessage("study.contributors", null, locale), regular, null,
					locale);
			createCell(table, 3, 4, study.getContributors(), regular, null, locale);
			createCell(table, 0, 5, messageSource.getMessage("export.odt.PsychData.td7", null, locale), regular, null,
					locale);
			createCell(table, 1, 5, messageSource.getMessage("export.odt.PsychData.td8", null, locale), regular, null,
					locale);
			createCell(table, 2, 5, messageSource.getMessage("study.sAbstract", null, locale), regular, null, locale);
			createCell(table, 3, 5, study.getsAbstract(), regular, null, locale);
			createCell(table, 0, 6, messageSource.getMessage("export.odt.PsychData.td9", null, locale), regular, null,
					locale);
			createCell(table, 1, 6, messageSource.getMessage("export.odt.PsychData.td10", null, locale), regular, null,
					locale);
			createCell(table, 2, 6, messageSource.getMessage("study.sAbstractTrans", null, locale), regular, null,
					locale);
			createCell(table, 3, 6, study.getsAbstractTrans(), regular, null, locale);
			createCell(table, 0, 7, messageSource.getMessage("export.odt.PsychData.td11", null, locale), regular, null,
					locale);
			createCell(table, 1, 7, messageSource.getMessage("export.odt.PsychData.td12", null, locale), regular, null,
					locale);
			createCell(table, 2, 7, messageSource.getMessage("study.completeSel", null, locale), regular, null, locale);
			createCell(table, 3, 7, study.getCompleteSel(), regular, null, locale);
			createCell(table, 2, 8, messageSource.getMessage("study.excerpt", null, locale), regular, null, locale);
			createCell(table, 3, 8, study.getExcerpt(), regular, null, locale);
			createCell(table, 0, 9, messageSource.getMessage("export.odt.PsychData.td13", null, locale), regular, null,
					locale);
			createCell(table, 1, 9, messageSource.getMessage("export.odt.PsychData.td14", null, locale), regular, null,
					locale);
			createCell(table, 2, 9, messageSource.getMessage("study.pubOnData", null, locale), regular, null, locale);
			createCell(table, 3, 9, study.getPubOnData(), regular, null, locale);
			createCell(table, 0, 10, messageSource.getMessage("export.odt.PsychData.td15", null, locale), regular, null,
					locale);
			createCell(table, 1, 10, messageSource.getMessage("export.odt.PsychData.td16", null, locale), regular, null,
					locale);
			createCell(table, 2, 10, messageSource.getMessage("study.objectives", null, locale), regular, null, locale);
			createCell(table, 3, 10, study.getObjectives(), regular, null, locale);
			createCell(table, 0, 11, messageSource.getMessage("export.odt.PsychData.td17", null, locale), regular, null,
					locale);
			createCell(table, 1, 11, messageSource.getMessage("export.odt.PsychData.td18", null, locale), regular, null,
					locale);
			createCell(table, 2, 11, messageSource.getMessage("study.repMeasures", null, locale), regular, null,
					locale);
			createCell(table, 3, 11, study.getRepMeasures(), regular, null, locale);
			createCell(table, 0, 12, messageSource.getMessage("export.odt.PsychData.td19", null, locale), regular, null,
					locale);
			createCell(table, 1, 12, messageSource.getMessage("export.odt.PsychData.td20", null, locale), regular, null,
					locale);
			createCell(table, 2, 12, messageSource.getMessage("study.timeDim", null, locale), regular, null, locale);
			createCell(table, 3, 12, study.getTimeDim(), regular, null, locale);
			createCell(table, 0, 13, messageSource.getMessage("export.odt.PsychData.td21", null, locale), regular, null,
					locale);
			createCell(table, 1, 13, messageSource.getMessage("export.odt.PsychData.td22", null, locale), regular, null,
					locale);
			createCell(table, 2, 13, messageSource.getMessage("study.intervention", null, locale), regular, null,
					locale);
			createCell(table, 3, 13, messageSource.getMessage("export.odt.PsychData.td22.5", null, locale), regular,
					null, locale);
			createCell(table, 2, 14, messageSource.getMessage("study.intervention.survey", null, locale), regular, null,
					locale);
			createCell(table, 3, 14, study.isSurveyIntervention(), regular, null, locale);
			createCell(table, 2, 15, messageSource.getMessage("study.intervention.test", null, locale), regular, null,
					locale);
			createCell(table, 3, 15, study.isTestIntervention(), regular, null, locale);
			createCell(table, 2, 16, messageSource.getMessage("study.intervention.experimental", null, locale), regular,
					null, locale);
			createCell(table, 3, 16, study.isExperimentalIntervention(), regular, null, locale);
			createCell(table, 0, 17, messageSource.getMessage("export.odt.PsychData.td23", null, locale), regular, null,
					locale);
			createCell(table, 1, 17, messageSource.getMessage("export.odt.PsychData.td24", null, locale), regular, null,
					locale);
			createCell(table, 2, 17, messageSource.getMessage("study.interTypeExp", null, locale), regular, null,
					locale);
			createCell(table, 3, 17, study.getInterTypeExp(), regular, null, locale);
			createCell(table, 2, 18, messageSource.getMessage("study.interTypeDes", null, locale), regular, null,
					locale);
			createCell(table, 3, 18, study.getInterTypeDes(), regular, null, locale);
			createCell(table, 2, 19, messageSource.getMessage("study.interTypeLab", null, locale), regular, null,
					locale);
			createCell(table, 3, 19, study.getInterTypeLab(), regular, null, locale);
			createCell(table, 2, 20, messageSource.getMessage("study.randomization", null, locale), regular, null,
					locale);
			createCell(table, 3, 20, study.getRandomization(), regular, null, locale);
			createCell(table, 0, 21, messageSource.getMessage("export.odt.PsychData.no.equivalent", null, locale),
					regular, new Color(210, 210, 210), locale);
			createCell(table, 2, 21, messageSource.getMessage("study.measOcc", null, locale), regular, null, locale);
			createCell(table, 3, 21, study.getMeasOcc(), regular, null, locale);
			createCell(table, 0, 22, messageSource.getMessage("export.odt.PsychData.td23", null, locale), regular, null,
					locale);
			createCell(table, 1, 22, messageSource.getMessage("export.odt.PsychData.td25", null, locale), regular, null,
					locale);
			createCell(table, 2, 22, messageSource.getMessage("study.surveyType", null, locale), regular, null, locale);
			createCell(table, 3, 22, study.getSurveyType(), regular, null, locale);
			createCell(table, 0, 23, messageSource.getMessage("export.odt.PsychData.td26", null, locale), regular, null,
					locale);
			createCell(table, 1, 23, messageSource.getMessage("export.odt.PsychData.td27", null, locale), regular, null,
					locale);
			createCell(table, 2, 23, messageSource.getMessage("study.constructs", null, locale), regular, null, locale);
			createCell(table, 3, 23, study.getConstructs(), regular, null, locale);
			String constructnames = "";
			String constructtypes = "";
			String constructothers = "";
			int index = 1;
			for (StudyConstructDTO construct : study.getConstructs()) {
				constructnames = constructnames + index + ": " + construct.getName() + ". ";
				constructtypes = constructtypes + index + ": " + construct.getType() + ". ";
				constructothers = constructothers + index + ": " + construct.getOther() + ". ";
				index = index + 1;
			}
			createCell(table, 2, 24, messageSource.getMessage("study.constructs.name", null, locale), regular, null,
					locale);
			createCell(table, 3, 24, constructnames, regular, null, locale);
			createCell(table, 2, 25, messageSource.getMessage("study.constructs.type", null, locale), regular, null,
					locale);
			createCell(table, 3, 25, constructtypes, regular, null, locale);
			createCell(table, 2, 26, messageSource.getMessage("study.constructs.other", null, locale), regular, null,
					locale);
			createCell(table, 3, 26, constructothers, regular, null, locale);
			createCell(table, 0, 27, messageSource.getMessage("export.odt.PsychData.td23", null, locale), regular, null,
					locale);
			createCell(table, 1, 27, messageSource.getMessage("export.odt.PsychData.td28", null, locale), regular, null,
					locale);
			createCell(table, 2, 27, messageSource.getMessage("study.instruments", null, locale), regular, null,
					locale);
			createCell(table, 3, 27, study.getInstruments(), regular, null, locale);
			String inids = "";
			String intitles = "";
			String inauthors = "";
			String incitations = "";
			String insummaries = "";
			String intheohints = "";
			String instructures = "";
			String inconstructions = "";
			String inobjectivities = "";
			String inreliabilities = "";
			String invalidities = "";
			String innorms = "";
			index = 1;
			for (StudyInstrumentDTO instrument : study.getInstruments()) {
				inids = inids + index + ": " + instrument.getId() + ". ";
				intitles = intitles + index + ": " + instrument.getTitle() + ". ";
				inauthors = inauthors + index + ": " + instrument.getAuthor() + ". ";
				incitations = incitations + index + ": " + instrument.getCitation() + ". ";
				insummaries = insummaries + index + ": " + instrument.getSummary() + ". ";
				intheohints = intheohints + index + ": " + instrument.getTheoHint() + ". ";
				instructures = instructures + index + ": " + instrument.getStructure() + ". ";
				inconstructions = inconstructions + index + ": " + instrument.getConstruction() + ". ";
				inobjectivities = inobjectivities + index + ": " + instrument.getObjectivity() + ". ";
				inreliabilities = inreliabilities + index + ": " + instrument.getReliability() + ". ";
				invalidities = invalidities + index + ": " + instrument.getValidity() + ". ";
				innorms = innorms + index + ": " + instrument.getNorm() + ". ";
				index = index + 1;
			}
			createCell(table, 2, 28, messageSource.getMessage("study.instrument", null, locale), regular, null, locale);
			createCell(table, 3, 28, inids, regular, null, locale);
			createCell(table, 2, 29, messageSource.getMessage("study.instruments.title", null, locale), regular, null,
					locale);
			createCell(table, 3, 29, intitles, regular, null, locale);
			createCell(table, 2, 30, messageSource.getMessage("study.instruments.author", null, locale), regular, null,
					locale);
			createCell(table, 3, 30, inauthors, regular, null, locale);
			createCell(table, 2, 31, messageSource.getMessage("study.instruments.citation", null, locale), regular,
					null, locale);
			createCell(table, 3, 31, incitations, regular, null, locale);
			createCell(table, 2, 32, messageSource.getMessage("study.instruments.summary", null, locale), regular, null,
					locale);
			createCell(table, 3, 32, insummaries, regular, null, locale);
			createCell(table, 2, 33, messageSource.getMessage("study.instruments.theoHint", null, locale), regular,
					null, locale);
			createCell(table, 3, 33, intheohints, regular, null, locale);
			createCell(table, 2, 34, messageSource.getMessage("study.instruments.structure", null, locale), regular,
					null, locale);
			createCell(table, 3, 34, instructures, regular, null, locale);
			createCell(table, 2, 35, messageSource.getMessage("study.instruments.construction", null, locale), regular,
					null, locale);
			createCell(table, 3, 35, inconstructions, regular, null, locale);
			createCell(table, 2, 36, messageSource.getMessage("study.instruments.objectivity", null, locale), regular,
					null, locale);
			createCell(table, 3, 36, inobjectivities, regular, null, locale);
			createCell(table, 2, 37, messageSource.getMessage("study.instruments.reliability", null, locale), regular,
					null, locale);
			createCell(table, 3, 37, inreliabilities, regular, null, locale);
			createCell(table, 2, 38, messageSource.getMessage("study.instruments.validity", null, locale), regular,
					null, locale);
			createCell(table, 3, 38, invalidities, regular, null, locale);
			createCell(table, 2, 39, messageSource.getMessage("study.instruments.norm", null, locale), regular, null,
					locale);
			createCell(table, 3, 39, innorms, regular, null, locale);
			createCell(table, 2, 40, messageSource.getMessage("study.description", null, locale), regular, null,
					locale);
			createCell(table, 3, 40, study.getDescription(), regular, null, locale);
			createCell(table, 0, 41, messageSource.getMessage("export.odt.PsychData.td30", null, locale), regular, null,
					locale);
			createCell(table, 1, 41, messageSource.getMessage("export.odt.PsychData.td31", null, locale), regular, null,
					locale);
			createCell(table, 2, 41, messageSource.getMessage("study.eligibilities", null, locale), regular, null,
					locale);
			createCell(table, 3, 41, study.getEligibilities(), regular, null, locale);
			createCell(table, 2, 42, messageSource.getMessage("study.population", null, locale), regular, null, locale);
			createCell(table, 0, 43, messageSource.getMessage("export.odt.PsychData.td32", null, locale), regular, null,
					locale);
			createCell(table, 1, 43, messageSource.getMessage("export.odt.PsychData.td33", null, locale), regular, null,
					locale);
			createCell(table, 2, 43, messageSource.getMessage("study.sampleSize", null, locale), regular, null, locale);
			createCell(table, 3, 43, study.getSampleSize(), regular, null, locale);
			createCell(table, 0, 44, messageSource.getMessage("export.odt.PsychData.td34", null, locale), regular, null,
					locale);
			createCell(table, 1, 44, messageSource.getMessage("export.odt.PsychData.td35", null, locale), regular, null,
					locale);
			createCell(table, 2, 44, messageSource.getMessage("study.obsUnit", null, locale), regular, null, locale);
			createCell(table, 3, 44, study.getObsUnit(), regular, null, locale);
			createCell(table, 0, 45, messageSource.getMessage("export.odt.PsychData.td36", null, locale), regular, null,
					locale);
			createCell(table, 1, 45, messageSource.getMessage("export.odt.PsychData.td37", null, locale), regular, null,
					locale);
			createCell(table, 2, 45, messageSource.getMessage("study.sex", null, locale), regular, null, locale);
			createCell(table, 3, 45, study.getSex(), regular, null, locale);
			createCell(table, 0, 46, messageSource.getMessage("export.odt.PsychData.td38", null, locale), regular, null,
					locale);
			createCell(table, 1, 46, messageSource.getMessage("export.odt.PsychData.td39", null, locale), regular, null,
					locale);
			createCell(table, 2, 46, messageSource.getMessage("study.age", null, locale), regular, null, locale);
			createCell(table, 3, 46, study.getAge(), regular, null, locale);
			createCell(table, 0, 47, messageSource.getMessage("export.odt.PsychData.td40", null, locale), regular, null,
					locale);
			createCell(table, 1, 47, messageSource.getMessage("export.odt.PsychData.td41", null, locale), regular, null,
					locale);
			createCell(table, 2, 47, messageSource.getMessage("study.specGroups", null, locale), regular, null, locale);
			createCell(table, 3, 47, study.getSpecGroups(), regular, null, locale);
			createCell(table, 0, 48, messageSource.getMessage("export.odt.PsychData.td42", null, locale), regular, null,
					locale);
			createCell(table, 1, 48, messageSource.getMessage("export.odt.PsychData.td43", null, locale), regular, null,
					locale);
			createCell(table, 2, 48, messageSource.getMessage("study.country", null, locale), regular, null, locale);
			createCell(table, 3, 48, study.getCountry(), regular, null, locale);
			createCell(table, 0, 49, messageSource.getMessage("export.odt.PsychData.td44", null, locale), regular, null,
					locale);
			createCell(table, 1, 49, messageSource.getMessage("export.odt.PsychData.td45", null, locale), regular, null,
					locale);
			createCell(table, 2, 49, messageSource.getMessage("study.city", null, locale), regular, null, locale);
			createCell(table, 3, 49, study.getCity(), regular, null, locale);
			createCell(table, 0, 50, messageSource.getMessage("export.odt.PsychData.td46", null, locale), regular, null,
					locale);
			createCell(table, 1, 50, messageSource.getMessage("export.odt.PsychData.td47", null, locale), regular, null,
					locale);
			createCell(table, 2, 50, messageSource.getMessage("study.region", null, locale), regular, null, locale);
			createCell(table, 3, 50, study.getRegion(), regular, null, locale);
			createCell(table, 0, 51, messageSource.getMessage("export.odt.PsychData.td48", null, locale), regular, null,
					locale);
			createCell(table, 1, 51, messageSource.getMessage("export.odt.PsychData.td49", null, locale), regular, null,
					locale);
			createCell(table, 2, 51, messageSource.getMessage("study.dataRerun", null, locale), regular, null, locale);
			createCell(table, 3, 51, study.getDataRerun(), regular, null, locale);
			createCell(table, 0, 52, messageSource.getMessage("export.odt.PsychData.td50", null, locale), regular, null,
					locale);
			createCell(table, 1, 52, messageSource.getMessage("export.odt.PsychData.td51", null, locale), regular, null,
					locale);
			createCell(table, 2, 52, messageSource.getMessage("study.responsibility", null, locale), regular, null,
					locale);
			createCell(table, 3, 52, study.getResponsibility(), regular, null, locale);
			createCell(table, 0, 53, messageSource.getMessage("export.odt.PsychData.td52", null, locale), regular, null,
					locale);
			createCell(table, 1, 53, messageSource.getMessage("export.odt.PsychData.td53", null, locale), regular, null,
					locale);
			createCell(table, 2, 53, messageSource.getMessage("study.collStart", null, locale), regular, null, locale);
			createCell(table, 3, 53, study.getCollStart(), regular, null, locale);
			createCell(table, 0, 54, messageSource.getMessage("export.odt.PsychData.td54", null, locale), regular, null,
					locale);
			createCell(table, 1, 54, messageSource.getMessage("export.odt.PsychData.td55", null, locale), regular, null,
					locale);
			createCell(table, 2, 54, messageSource.getMessage("study.collEnd", null, locale), regular, null, locale);
			createCell(table, 3, 54, study.getCollEnd(), regular, null, locale);
			createCell(table, 0, 55, messageSource.getMessage("export.odt.PsychData.td56", null, locale), regular, null,
					locale);
			createCell(table, 1, 54, messageSource.getMessage("export.odt.PsychData.td57", null, locale), regular, null,
					locale);
			createCell(table, 2, 54, messageSource.getMessage("study.usedCollectionModes", null, locale), regular, null,
					locale);
			createCell(table, 3, 55, study.getUsedCollectionModes(), regular, null, locale);
			createCell(table, 0, 56, messageSource.getMessage("export.odt.PsychData.td58", null, locale), regular, null,
					locale);
			createCell(table, 1, 56, messageSource.getMessage("export.odt.PsychData.td59", null, locale), regular, null,
					locale);
			createCell(table, 2, 56, messageSource.getMessage("study.sampMethod", null, locale), regular, null, locale);
			createCell(table, 3, 56, study.getSampMethod(), regular, null, locale);
			createCell(table, 0, 57, messageSource.getMessage("export.odt.PsychData.td60", null, locale), regular, null,
					locale);
			createCell(table, 1, 57, messageSource.getMessage("export.odt.PsychData.td61", null, locale), regular, null,
					locale);
			createCell(table, 2, 57, messageSource.getMessage("study.recruiting", null, locale), regular, null, locale);
			createCell(table, 3, 57, study.getRecruiting(), regular, null, locale);
			createCell(table, 0, 58, messageSource.getMessage("export.odt.PsychData.td62", null, locale), regular, null,
					locale);
			createCell(table, 1, 58, messageSource.getMessage("export.odt.PsychData.td63", null, locale), regular, null,
					locale);
			createCell(table, 2, 58, messageSource.getMessage("study.usedSourFormat", null, locale), regular, null,
					locale);
			createCell(table, 3, 58, study.getUsedSourFormat(), regular, null, locale);
			createCell(table, 0, 59, messageSource.getMessage("export.odt.PsychData.td64", null, locale), regular, null,
					locale);
			createCell(table, 1, 59, messageSource.getMessage("export.odt.PsychData.td65", null, locale), regular, null,
					locale);
			createCell(table, 2, 59, messageSource.getMessage("study.sourTrans", null, locale), regular, null, locale);
			createCell(table, 3, 59, study.getSourTrans(), regular, null, locale);
			createCell(table, 0, 60, messageSource.getMessage("export.odt.PsychData.td66", null, locale), regular, null,
					locale);
			createCell(table, 1, 60, messageSource.getMessage("export.odt.PsychData.td67", null, locale), regular, null,
					locale);
			createCell(table, 2, 60, messageSource.getMessage("study.specCirc", null, locale), regular, null, locale);
			createCell(table, 3, 60, study.getSpecCirc(), regular, null, locale);
			createCell(table, 0, 61, messageSource.getMessage("export.odt.PsychData.td68", null, locale), regular, null,
					locale);
			createCell(table, 1, 61, messageSource.getMessage("export.odt.PsychData.td69", null, locale), regular, null,
					locale);
			createCell(table, 2, 61, messageSource.getMessage("study.transDescr", null, locale), regular, null, locale);
			createCell(table, 3, 61, study.getTransDescr(), regular, null, locale);
			createCell(table, 0, 62, messageSource.getMessage("export.odt.PsychData.td70", null, locale), regular, null,
					locale);
			createCell(table, 1, 62, messageSource.getMessage("export.odt.PsychData.td71", null, locale), regular, null,
					locale);
			createCell(table, 2, 62, messageSource.getMessage("study.qualInd", null, locale), regular, null, locale);
			createCell(table, 3, 62, study.getQualInd(), regular, null, locale);
			createCell(table, 2, 63, messageSource.getMessage("study.qualLim", null, locale), regular, null, locale);
			createCell(table, 3, 63, study.getQualLim(), regular, null, locale);
			createCell(table, 0, 64, messageSource.getMessage("export.odt.PsychData.td72", null, locale), regular, null,
					locale);
			createCell(table, 1, 64, messageSource.getMessage("export.odt.PsychData.td73", null, locale), regular, null,
					locale);
			createCell(table, 2, 64, messageSource.getMessage("study.persDataColl", null, locale), regular, null,
					locale);
			createCell(table, 3, 64, study.isPersDataColl(), regular, null, locale);
			createCell(table, 2, 65, messageSource.getMessage("study.persDataPres", null, locale), regular, null,
					locale);
			createCell(table, 3, 65, study.getPersDataPres(), regular, null, locale);
			createCell(table, 2, 66, messageSource.getMessage("study.anonymProc", null, locale), regular, null, locale);
			createCell(table, 3, 66, study.getAnonymProc(), regular, null, locale);
			createCell(table, 0, 67, messageSource.getMessage("export.odt.PsychData.td74", null, locale), regular, null,
					locale);
			createCell(table, 1, 67, messageSource.getMessage("export.odt.PsychData.td75", null, locale), regular, null,
					locale);
			createCell(table, 2, 67, messageSource.getMessage("study.copyright", null, locale), regular, null, locale);
			createCell(table, 3, 67, study.isCopyright(), regular, null, locale);
			createCell(table, 2, 68, messageSource.getMessage("study.copyrightHolder", null, locale), regular, null,
					locale);
			createCell(table, 3, 68, study.getCopyrightHolder(), regular, null, locale);

			doc.addColumnBreak();

			doc.addParagraph(messageSource.getMessage("export.odt.PsychData.ft1h", null, locale)).setFont(blue_large);
			doc.addColumnBreak();
			par = doc.addParagraph(messageSource.getMessage("export.odt.PsychData.ft1", null, locale));
			par.setFont(regular);
			par.appendHyperlink(messageSource.getMessage("export.odt.PsychData.ft1li", null, locale),
					new URI(messageSource.getMessage("export.odt.PsychData.ft1li", null, locale)));
			doc.addColumnBreak();

			doc.save(baos);
			doc.close();
			log.trace("Leaving createPsychdataDoc");
			return baos.toByteArray();
		} else {
			log.warn("Error during createPsychdataDoc - Study is null");
			throw new DataWizSystemException(messageSource.getMessage("export.odt.error.project", null, locale),
					DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
		}
	}

	/**
	 * This function creates the PreRegistration OTF document for the DPM export On
	 * success it returns an byte array, otherwise an exception or an empty byte
	 * array!
	 * 
	 * @param ProjectForm,
	 *            which contains all necessary data for the export
	 * @param Locale
	 *            to distinguish whether the export is in English or German. At the
	 *            moment only as a construct, because the export is currently only
	 *            implemented in German.
	 * @return The Open Text document as a byte array
	 * @throws Exception
	 */
	public byte[] createPreRegistrationDoc(final StudyForm sForm, final Locale locale) throws Exception {
		log.trace("Entering createPsychdataDoc");
		StudyDTO study = sForm.getStudy();
		if (study != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TextDocument doc = TextDocument.newTextDocument();

			// Erste Seite
			doc.addParagraph(messageSource.getMessage("export.odt.Pre.zpid", null, locale)).setFont(blue_reg);
			doc.addColumnBreak();
			doc.addParagraph(messageSource.getMessage("export.odt.Pre.headline", null, locale)).setFont(headline);
			doc.addColumnBreak();
			Paragraph par = doc.addParagraph(messageSource.getMessage("export.odt.Pre.subline", null, locale));
			par.setFont(blue_reg);
			// Masterpage
			MasterPage master = MasterPage.getOrCreateMasterPage(doc, "Preregistration");
			master.setNumberFormat(NumberFormat.HINDU_ARABIC_NUMBER.toString());
			// master.setMargins(15, 15, 15, 15);
			doc.addPageBreak(par, master);
			// Zweite Seite
			doc.addParagraph(messageSource.getMessage("export.odt.Pre.zu1h", null, locale)).setFont(blue_large);
			doc.addColumnBreak();
			doc.addParagraph(messageSource.getMessage("export.odt.Pre.zu1", null, locale)).setFont(regular);
			doc.addColumnBreak();
			doc.addParagraph(messageSource.getMessage("export.odt.Pre.zu2", null, locale)).setFont(regular);
			doc.addColumnBreak();
			doc.addParagraph(messageSource.getMessage("export.odt.Pre.zu2", null, locale)).setFont(regular);
			doc.addColumnBreak();

			Table table = doc.addTable(60, 3);
			// merge
			table.getCellRangeByPosition(0, 0, 0, 1).merge();
			table.getCellRangeByPosition(1, 0, 2, 0).merge();
			table.getCellRangeByPosition(0, 2, 0, 7).merge();
			table.getCellRangeByPosition(0, 8, 0, 9).merge();
			table.getCellRangeByPosition(0, 10, 0, 17).merge();
			// inhalt
			createCell(table, 0, 0, messageSource.getMessage("export.odt.Pre.th1", null, locale), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 1, 0, messageSource.getMessage("export.odt.Pre.dw", null, locale), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 1, 1, messageSource.getMessage("export.odt.Pre.th2", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 2, 1, messageSource.getMessage("export.odt.Pre.th2", null, locale), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 0, 2, messageSource.getMessage("export.odt.Pre.td1", null, locale), regular, null,
					locale);
			createCell(table, 1, 2, messageSource.getMessage("study.title", null, locale), regular, null, locale);
			createCell(table, 2, 2, study.getTitle(), regular, null, locale);
			createCell(table, 1, 3, messageSource.getMessage("study.transTitle", null, locale), regular, null, locale);
			createCell(table, 2, 3, study.getTransTitle(), regular, null, locale);
			createCell(table, 1, 4, messageSource.getMessage("study.sAbstract", null, locale), regular, null, locale);
			createCell(table, 2, 4, study.getsAbstract(), regular, null, locale);
			createCell(table, 1, 5, messageSource.getMessage("study.sAbstractTrans", null, locale), regular, null,
					locale);
			createCell(table, 2, 5, study.getsAbstractTrans(), regular, null, locale);
			createCell(table, 1, 6, messageSource.getMessage("study.prevWork", null, locale), regular, null, locale);
			createCell(table, 2, 6, study.getPrevWork(), regular, null, locale);
			createCell(table, 1, 7, messageSource.getMessage("study.prevWorkStr", null, locale), regular, null, locale);
			createCell(table, 2, 7, study.getPrevWorkStr(), regular, null, locale);
			createCell(table, 0, 8, messageSource.getMessage("export.odt.Pre.td2", null, locale), regular, null,
					locale);
			createCell(table, 1, 8, messageSource.getMessage("study.objectives", null, locale), regular, null, locale);
			createCell(table, 2, 8, study.getObjectives(), regular, null, locale);
			createCell(table, 1, 9, messageSource.getMessage("study.relTheorys", null, locale), regular, null, locale);
			createCell(table, 2, 9, study.getRelTheorys(), regular, null, locale);
			createCell(table, 0, 10, messageSource.getMessage("export.odt.Pre.td3", null, locale), regular, null,
					locale);
			createCell(table, 1, 10, messageSource.getMessage("study.intervention", null, locale), regular, null,
					locale);
			createCell(table, 2, 10, messageSource.getMessage("export.odt.Pre.tdh", null, locale), regular, null,
					locale);
			createCell(table, 1, 11, messageSource.getMessage("study.intervention.experimental", null, locale), regular,
					null, locale);
			createCell(table, 2, 11, study.getInterTypeExp(), regular, null, locale);
			createCell(table, 1, 12, messageSource.getMessage("study.intervention.survey", null, locale), regular, null,
					locale);
			createCell(table, 2, 12, study.getInterTypeDes(), regular, null, locale);
			createCell(table, 1, 13, messageSource.getMessage("study.intervention.test", null, locale), regular, null,
					locale);
			createCell(table, 2, 13, study.getInterTypeLab(), regular, null, locale);
			String constructnames = "";
			String constructtypes = "";
			String constructothers = "";
			int index = 1;
			for (StudyConstructDTO construct : study.getConstructs()) {
				constructnames = constructnames + index + ": " + construct.getName() + ". ";
				constructtypes = constructtypes + index + ": " + construct.getType() + ". ";
				constructothers = constructothers + index + ": " + construct.getOther() + ". ";
				index = index + 1;
			}
			createCell(table, 1, 14, messageSource.getMessage("study.constructs", null, locale), regular, null, locale);
			createCell(table, 2, 14, study.getConstructs(), regular, null, locale);
			createCell(table, 1, 15, messageSource.getMessage("study.constructs.name", null, locale), regular, null,
					locale);
			createCell(table, 2, 15, constructnames, regular, null, locale);
			createCell(table, 1, 16, messageSource.getMessage("study.constructs.type", null, locale), regular, null,
					locale);
			createCell(table, 2, 16, constructtypes, regular, null, locale);
			createCell(table, 1, 17, messageSource.getMessage("study.constructs.other", null, locale), regular, null,
					locale);
			createCell(table, 2, 17, constructothers, regular, null, locale);

			doc.save(baos);
			doc.close();
			log.trace("Leaving createPreregistrationDoc");
			return baos.toByteArray();
		} else {
			log.warn("Error during createPreregistrationDoc - Study is null");
			throw new DataWizSystemException(messageSource.getMessage("export.odt.error.project", null, locale),
					DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
		}
	}

	/**
	 * This function writes content into a Cell. There it needs the current table
	 * and position (colnum, rownum). As content, the function expects an object of
	 * type object and interprets the correct content independently. This currently
	 * works with numbers, strings, booleans and lists.
	 * 
	 * @param table
	 *            Table, which is including the cell
	 * @param col
	 *            Column number (starting with 0)
	 * @param row
	 *            Row number (starting with 0)
	 * @param content
	 *            Content, which will be written into the cell
	 * @param font
	 *            Font-type of the cell
	 * @param color
	 *            Background color of the Cell - null for white
	 * @param locale
	 *            Actual locale for the export
	 */
	private void createCell(final Table table, final int col, final int row, final Object content, final Font font,
			final Color color, final Locale locale) {
		if (content != null) {
			Cell cell = table.getCellByPosition(col, row);
			if (content instanceof Number) {
				cell.setDoubleValue((Double) content);
			} else if (content instanceof Boolean) {
				cell.setStringValue(((boolean) content) ? messageSource.getMessage("gen.yes", null, locale)
						: messageSource.getMessage("gen.no", null, locale));
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
						FormTypesDTO type = FORMTYPES.parallelStream().filter(dt -> (dt.getId() == (Integer) usedType))
								.findFirst().orElse(null);
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
	 * This function inits the formtypes. The formtypes are static, because the can
	 * be used for all instances. At the moment there are no functions to change the
	 * formtypes, therefore it is not necessary to check for updates. If the
	 * formtypes are changed directly in the database, the DataWiz application has
	 * to be reloaded, or the tomcat has to be restarted.
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
