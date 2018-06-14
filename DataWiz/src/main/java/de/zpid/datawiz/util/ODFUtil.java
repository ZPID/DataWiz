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
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.zpid", null, locale))).setFont(blue_reg);
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.headline", null, locale))).setFont(headline);
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.subline", null, locale))).setFont(blue_reg);
			
			// Zweite Seite
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.zu1h", null, locale))).setFont(blue_large);
			doc.addColumnBreak();
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.zu1", null, locale))).setFont(regular);
			doc.addColumnBreak();
			Paragraph par = doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.zu1it", null, locale)));
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
			Integer i = 0;
			createCell(table, 0, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza", null, locale)), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.datawiz", null, locale)), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.th1", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.th2", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 3, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.th3", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.td1", null, locale)), regular, null,
					locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.td2", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.projectAims", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, project.getDescription(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.td3", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingData", null, locale)), regular, null,
					locale);
			createCell(table, 3, i,
					(dmp.getExistingData() != null && !dmp.getExistingData().isEmpty()
							? messageSource.getMessage("dmp.edit.existingData." + dmp.getExistingData(), null, locale)
							: ""),
					regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.dataCitation", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getDataCitation(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingDataRelevance", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getExistingDataRelevance(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingDataIntegration", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getExistingDataIntegration(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.externalCopyright", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isExternalCopyright(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.externalCopyrightTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isExternalCopyright() ? dmp.getExternalCopyrightTxt() : null, regular, null,
					locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.td4", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.duration", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getDuration(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.usedCollectionModes", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getUsedCollectionModes(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.measOccasions", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getMeasOccasions(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.specificCosts", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getSpecificCosts(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.specificCostsTxt", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getSpecificCostsTxt(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.bearCost", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getBearCost(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.staffDescription", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isStaffDescription(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.staffDescriptionTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isStaffDescription() ? dmp.getStaffDescriptionTxt() : null, regular, null,
					locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.td5", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.expectedUsage", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getExpectedUsage(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.td6", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.organizations", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getOrganizations(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.involvedInstitutions", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getInvolvedInstitutions(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.involvedInformed", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isInvolvedInformed(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.contributionsDefined", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isContributionsDefined(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.contributionsDefinedTxt", null, locale)),
					regular, null, locale);
			createCell(table, 3, i, dmp.getContributionsDefinedTxt(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.givenConsent", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isGivenConsent(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.depositName", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getDepositName(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.acquisitionAgreement", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isAcquisitionAgreement(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.td7", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.managementWorkflow", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isManagementWorkflow(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.managementWorkflowTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getManagementWorkflowTxt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.td8", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.no.equivalent", null, locale)), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.td9", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.no.equivalent", null, locale)), regular,
					new Color(210, 210, 210), locale);
			doc.addPageBreak();
			// Neue Seite bildungsforschung
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.zu2h", null, locale))).setFont(blue_large);
			doc.addColumnBreak();
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.zu2", null, locale))).setFont(regular);
			doc.addColumnBreak();
			par = doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.zu2it", null, locale)));
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

			i = 0;
			createCell(table, 0, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.th1", null, locale)), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.datawiz", null, locale)), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.th2", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.th3", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.th2", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 3, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.aza.th3", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td1", null, locale)), regular_bold,
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

			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td2", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td3", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("project.edit.grantNumber", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, project.getGrantNumber(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td4", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td5", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("project.edit.funding", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, project.getFunding(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td6", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td7", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("project.edit.title", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, project.getTitle(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td8", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td9", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("project.edit.description", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, project.getDescription(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingDataRelevance", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getExistingDataRelevance(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td10", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td11", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.leader", null, locale)), regular, null, locale);
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
				createCell(table, 3, i, primName.toString(), regular, null, locale);
			}
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td12", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td13", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.no.equivalent", null, locale)), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td14", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td15", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.no.equivalent", null, locale)), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td16", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td17", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.no.equivalent", null, locale)), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td18", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td19", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.funderRequirements", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getFunderRequirements(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td20", null, locale)), regular_bold,
					null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td21", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td22", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.usedDataTypes", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getUsedDataTypes(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td23", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td24", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingData", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getExistingData(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.dataCitation", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getDataCitation(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingDataRelevance", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getExistingDataRelevance(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingDataIntegration", null, locale)),
					regular, null, locale);
			createCell(table, 3, i, dmp.getExistingDataIntegration(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td25", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td26", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.reliabilityTraining", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getReliabilityTraining(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.multipleMeasurements", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getMultipleMeasurements(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.qualitityOther", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getQualitityOther(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td27", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td28", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.no.equivalent1", null, locale)), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td29", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td30", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.no.equivalent2", null, locale)), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td31", null, locale)), regular_bold,
					null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td32", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td33", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageResponsible", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getStorageResponsible(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storagePlaces", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getStoragePlaces(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageBackups", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getStorageBackups(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageExpectedSize", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getStorageExpectedSize(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td34", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td35", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.managementWorkflow", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isManagementWorkflow(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.managementWorkflowTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getManagementWorkflowTxt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td36", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td37", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.fileFormat", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getFileFormat(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageRequirements", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isStorageRequirements(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageRequirementsTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getStorageRequirementsTxt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td38", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td39", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.sensitiveDataIncluded", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isSensitiveDataIncluded(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.sensitiveDataIncludedTxt", null, locale)),
					regular, null, locale);
			createCell(table, 3, i, dmp.getSensitiveDataIncludedTxt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td40", null, locale)), regular_bold,
					null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td41", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td42", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.metaDescription", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getMetaDescription(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td43", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td44", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.metaFramework", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getMetaFramework(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.selectedMetaPurposes", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getSelectedMetaPurposes(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td45", null, locale)), regular_bold,
					null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td46", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td47", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.frameworkNationality", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getFrameworkNationality(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.frameworkNationalityTxt", null, locale)),
					regular, null, locale);
			createCell(table, 3, i, dmp.getFrameworkNationalityTxt(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.externalCopyright", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isExternalCopyright(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.externalCopyrightTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getExternalCopyrightTxt(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.internalCopyright", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isInternalCopyright(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.internalCopyrightTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getInternalCopyrightTxt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td48", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td49", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.sensitiveData", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getSensitiveData(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.dataProtection", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isDataProtection(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.protectionRequirements", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getProtectionRequirements(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.consentObtained", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isConsentObtained(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.consentObtainedTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getConsentObtainedTxt(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.sharingConsidered", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isSharingConsidered(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.irbApproval", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isIrbApproval(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.irbApprovalTxt", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getIrbApprovalTxt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td50", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td51", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.no.equivalent", null, locale)), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td52", null, locale)), regular, null,
					locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td53", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td54", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.publStrategy", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getPublStrategy(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.depositName", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getDepositName(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.searchableData", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getSearchableData(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.transferTime", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getTransferTime(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.accessReasonAuthor", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getAccessReasonAuthor(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.noAccessReason", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getNoAccessReason(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.initialUsage", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getInitialUsage(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.usageRestriction", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getUsageRestriction(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.accessCosts", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isAccessCosts(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.usedPID", null, locale)), regular, null, locale);
			createCell(table, 3, i, dmp.getUsedPID(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td55", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td56", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.expectedUsage", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getExpectedUsage(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td57", null, locale)), regular_bold,
					null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td58", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td59", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.dataSelection", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isDataSelection(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.selectionTime", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getSelectionTime(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.selectionResp", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getSelectionResp(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td60", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td61", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageDuration", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getStorageDuration(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageSuccession", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isStorageSuccession(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageSuccessionTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getStorageSuccessionTxt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td62", null, locale)), regular_bold,
					null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td63", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td64", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.responsibleUnit", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getResponsibleUnit(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.involvedInstitutions", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getInvolvedInstitutions(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.involvedInformed", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isInvolvedInformed(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.contributionsDefined", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isContributionsDefined(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.contributionsDefinedTxt", null, locale)),
					regular, null, locale);
			createCell(table, 3, i, dmp.getContributionsDefinedTxt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td65", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.BMBF.bifo.td66", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.staffDescription", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isStaffDescription(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.staffDescriptionTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getStaffDescriptionTxt(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.specificCosts", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getSpecificCosts(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.specificCostsTxt", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getSpecificCostsTxt(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.bearCost", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getBearCost(), regular, null, locale);

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
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.zpid", null, locale))).setFont(blue_reg);
			doc.addColumnBreak();
			Paragraph par = doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.headline", null, locale)));
			par.setFont(headline);
			doc.addColumnBreak();
			// Masterpage
			MasterPage master = MasterPage.getOrCreateMasterPage(doc, "H2020");
			master.setNumberFormat(NumberFormat.HINDU_ARABIC_NUMBER.toString());
			master.setMargins(15, 15, 15, 15);
			doc.addPageBreak(par, master);
			// Zweite Seite
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.zu1", null, locale))).setFont(regular);
			doc.addColumnBreak();
			par = doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.zu2", null, locale)));
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
			Integer i = 0;
			createCell(table, 0, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.FAIR", null, locale)), regular_bold, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.datawiz", null, locale)), regular_bold,
					null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.th1", null, locale)), regular_bold, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.th2", null, locale)), regular_bold, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.th3", null, locale)), regular_bold, null,
					locale);
			createCell(table, 3, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.th3", null, locale)), regular_bold, null,
					locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td1", null, locale)), regular_bold, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td2", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.projectAims", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, project.getDescription(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingDataRelevance", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getExistingDataRelevance(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td3", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.usedDataTypes", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getUsedDataTypes(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.fileFormat", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getFileFormat(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td4", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingData", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getExistingData(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.dataCitation", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getDataCitation(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingDataIntegration", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getExistingDataIntegration(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td5", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.usedCollectionModes", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getUsedCollectionModes(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td6", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageExpectedSize", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getStorageExpectedSize(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td7", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.expectedUsage", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getExpectedUsage(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storage.headline", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, "?", regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td8", null, locale)), regular_bold, null,
					locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td9", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td10", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.searchableData", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getSearchableData(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td11", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.usedPID", null, locale)), regular, null, locale);
			createCell(table, 3, i, dmp.getUsedPID(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td12", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, "dmp.edit.storageTechnologies", regular, null, locale);
			createCell(table, 3, i, "?", regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td13", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.no.equivalent", null, locale)), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td14", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageBackups", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getStorageBackups(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.metaGeneration", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getMetaGeneration(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td15", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.selectedMetaPurposes", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getSelectedMetaPurposes(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.metaFramework", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getMetaFramework(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.metaDescription", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getMetaDescription(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.metaMonitor", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getMetaMonitor(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.metaFormat", null, locale)), regular, null,
					locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td16", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td17", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.sensitiveData", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getSensitiveData(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.accessReasonAuthor", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getAccessReasonAuthor(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.noAccessReason", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getNoAccessReason(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td18", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.publStrategy", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getPublStrategy(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.depositName", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getDepositName(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.accessCosts", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isAccessCosts(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.releaseObligation", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isReleaseObligation(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.clarifiedRights", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isClarifiedRights(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.acquisitionAgreement", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isAcquisitionAgreement(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td19", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageRequirements", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isStorageRequirements(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageRequirementsTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getStorageRequirementsTxt(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td20", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storagePlaces", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getStoragePlaces(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageDuration", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getStorageDuration(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td21", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.internalCopyright", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isInternalCopyright(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.internalCopyrightTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getInternalCopyrightTxt(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.externalCopyright", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isExternalCopyright(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.externalCopyrightTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getExternalCopyrightTxt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td22", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td23", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.no.equivalent", null, locale)), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td24", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.no.equivalent", null, locale)), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td25", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td26", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.usageRestriction", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getUsageRestriction(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td27", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.transferTime", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getTransferTime(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.initialUsage", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getInitialUsage(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td28", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.no.equivalent", null, locale)), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td29", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.reliabilityTraining", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getReliabilityTraining(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.multipleMeasurements", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getMultipleMeasurements(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.qualitityOther", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getQualitityOther(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td30", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageSuccessionTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getStorageSuccessionTxt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td31", null, locale)), regular_bold,
					null, locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td32", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.specificCosts", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getSpecificCosts(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.specificCostsTxt", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getSpecificCostsTxt(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.bearCost", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getBearCost(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td33", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.responsibleUnit", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getResponsibleUnit(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td34", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.no.equivalent", null, locale)), regular,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td35", null, locale)), regular_bold,
					null, locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td36", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageResponsible", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getStorageResponsible(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.dataProtection", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isDataProtection(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.protectionRequirements", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getProtectionRequirements(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageTransfer", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getStorageTransfer(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td37", null, locale)), regular_bold,
					null, locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td38", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.consentObtained", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isConsentObtained(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.consentObtainedTxt", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getConsentObtainedTxt(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.sharingConsidered", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.isSharingConsidered(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.irbApproval", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.isIrbApproval(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.irbApprovalTxt", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, dmp.getIrbApprovalTxt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td39", null, locale)), regular_bold,
					null, locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.H2020.td40", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.funderRequirements", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, dmp.getFunderRequirements(), regular, null, locale);

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
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.zpid", null, locale))).setFont(blue_reg);
			doc.addColumnBreak();
			Paragraph par = doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.headline", null, locale)));
			par.setFont(headline);
			doc.addColumnBreak();
			// Masterpage
			MasterPage master = MasterPage.getOrCreateMasterPage(doc, "DFG");
			master.setNumberFormat(NumberFormat.HINDU_ARABIC_NUMBER.toString());
			// master.setMargins(15, 15, 15, 15);
			doc.addPageBreak(par, master);
			// Zweite Seite
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.zu1", null, locale))).setFont(regular);
			doc.addColumnBreak();
			par = doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.zu2", null, locale)));
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

			Integer i = 0;
			createCell(table, 0, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.th1", null, locale)), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.th2", null, locale)), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.th3", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.th4", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.td1", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.dataReproducibility", null, locale)), regular,
					null, locale);
			createCell(table, 2, i++, dmp.getDataReproducibility(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.expectedUsage", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getExpectedUsage(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.td2", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.publStrategy", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getPublStrategy(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.searchableData", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getSearchableData(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.initialUsage", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getInitialUsage(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.usageRestriction", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getUsageRestriction(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.accessCosts", null, locale)), regular, null,
					locale);
			createCell(table, 2, i++, dmp.isAccessCosts(), regular, null, locale);
			// createCell(table, 1, 10, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.accessCostsTxt",
			// null, locale), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.accessReasonAuthor", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.getAccessReasonAuthor(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.noAccessReason", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getNoAccessReason(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.td3", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.projectAims", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, project.getDescription(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.usedDataTypes", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getUsedDataTypes(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingData", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getExistingData(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.dataCitation", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getDataCitation(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.usedCollectionModes", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.getUsedCollectionModes(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.td4", null, locale)), regular, null,
					locale);
			// createCell(table, 1, 18,
			// messageSource.getMessage("dmp.edit.qualityAssurance", null, locale)), regular,
			// null, locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.reliabilityTraining", null, locale)), regular,
					null, locale);
			createCell(table, 2, i++, dmp.getReliabilityTraining(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.multipleMeasurements", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.getMultipleMeasurements(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.qualitityOther", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getQualitityOther(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.planningAdherence", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.getPlanningAdherence(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.td5", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingDataRelevance", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.getExistingDataRelevance(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.existingDataIntegration", null, locale)),
					regular, null, locale);
			createCell(table, 2, i, dmp.getExistingDataIntegration(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.dataSelection", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.isDataSelection(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.selectionTime", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getSelectionTime(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.selectionResp", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getSelectionResp(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.deleteProcedure", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getDeleteProcedure(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storagePlaces", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getStoragePlaces(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageBackups", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getStorageBackups(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.managementWorkflow", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.isManagementWorkflow(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.managementWorkflowTxt", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.getManagementWorkflowTxt(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.funderRequirements", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.getFunderRequirements(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.td6", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageResponsible", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.getStorageResponsible(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.storageDuration", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getStorageDuration(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.sensitiveData", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getSensitiveData(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.clarifiedRights", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.isClarifiedRights(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.usedPID", null, locale)), regular, null, locale);
			createCell(table, 2, i, dmp.getUsedPID(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.td7", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.fileFormat", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getFileFormat(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.td8", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.metaFramework", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getMetaFramework(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.td9", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.depositName", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getDepositName(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.acquisitionAgreement", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.isAcquisitionAgreement(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.td10", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.dataProtection", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.isDataProtection(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.protectionRequirements", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.getProtectionRequirements(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.consentObtained", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.isConsentObtained(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.consentObtained", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getConsentObtainedTxt(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.sharingConsidered", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.isSharingConsidered(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.irbApproval", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.isIrbApproval(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.irbApprovalTxt", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getIrbApprovalTxt(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.sensitiveDataIncluded", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.isSensitiveDataIncluded(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.externalCopyright", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.isExternalCopyright(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.externalCopyrightTxt", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.getExternalCopyrightTxt(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.internalCopyright", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.isInternalCopyright(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.internalCopyrightTxt", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, dmp.getInternalCopyrightTxt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.td11", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.transferTime", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getTransferTime(), regular, null, locale);

			doc.addColumnBreak();
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.zu3", null, locale))).setFont(regular);
			doc.addColumnBreak();
			table = doc.addTable(3, 3);

			// merge
			table.getCellRangeByPosition(1, 0, 2, 0).merge();

			// content
			i = 0;
			createCell(table, 0, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.th1", null, locale)), regular_bold, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.th2", null, locale)), regular_bold, null,
					locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.th5", null, locale)), regular_bold, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.th6", null, locale)), regular_bold, null,
					locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.td12", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("dmp.edit.specificCosts", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, dmp.getSpecificCosts(), regular, null, locale);

			doc.addColumnBreak();

			par = doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.ft1", null, locale)));
			par.setFont(regular);
			par.appendHyperlink(messageSource.getMessage("export.odt.DFG.ft2", null, locale),
					new URI(messageSource.getMessage("export.odt.DFG.ft2l", null, locale)));
			doc.addColumnBreak();
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.src", null, locale))).setFont(regular_bold);
			doc.addColumnBreak();
			par = doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.src1", null, locale)));
			par.setFont(regular_it);
			par.appendHyperlink(messageSource.getMessage("export.odt.DFG.src1l", null, locale),
					new URI(messageSource.getMessage("export.odt.DFG.src1l", null, locale)));
			doc.addColumnBreak();
			par = doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.DFG.src2", null, locale)));
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
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.zpid", null, locale))).setFont(blue_reg);
			doc.addColumnBreak();
			Paragraph par = doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.headline", null, locale)));
			par.setFont(headline);
			doc.addColumnBreak();
			// Masterpage
			MasterPage master = MasterPage.getOrCreateMasterPage(doc, "Psychdata");
			master.setNumberFormat(NumberFormat.HINDU_ARABIC_NUMBER.toString());
			// master.setMargins(15, 15, 15, 15);
			doc.addPageBreak(par, master);
			// Zweite Seite
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.zu1h", null, locale))).setFont(blue_large);
			doc.addColumnBreak();
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.zu1", null, locale))).setFont(regular);
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
			// table.getCellRangeByPosition(0, 23, 0, 26).merge();
			// table.getCellRangeByPosition(1, 23, 1, 26).merge();
			// table.getCellRangeByPosition(0, 27, 0, 40).merge();
			// table.getCellRangeByPosition(1, 27, 1, 40).merge();
			// table.getCellRangeByPosition(0, 41, 0, 42).merge();
			// table.getCellRangeByPosition(1, 41, 1, 42).merge();
			// table.getCellRangeByPosition(0, 62, 0, 63).merge();
			// table.getCellRangeByPosition(1, 62, 1, 63).merge();
			// table.getCellRangeByPosition(0, 64, 0, 66).merge();
			// table.getCellRangeByPosition(1, 64, 1, 66).merge();
			// table.getCellRangeByPosition(0, 67, 0, 68).merge();
			// table.getCellRangeByPosition(1, 67, 1, 68).merge();

			// inhalt
			Integer i = 0;
			createCell(table, 0, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.pd", null, locale)), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.dw", null, locale)), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.th1", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.th2", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.th3", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 3, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.th4", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td1", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td2", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.title", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getTitle(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td3", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td4", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.transTitle", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getTransTitle(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td5", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td6", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.contributors", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getContributors(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td7", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td8", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.sAbstract", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getsAbstract(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td9", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td10", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.sAbstractTrans", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getsAbstractTrans(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td11", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td12", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.completeSel", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getCompleteSel(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.excerpt", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getExcerpt(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td13", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td14", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.pubOnData", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getPubOnData(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td15", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td16", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.objectives", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getObjectives(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td17", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td18", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.repMeasures", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getRepMeasures(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td19", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td20", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.timeDim", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getTimeDim(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td21", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td22", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.intervention", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td22.5", null, locale)), regular,
					null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.intervention.survey", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.isSurveyIntervention(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.intervention.test", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.isTestIntervention(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.intervention.experimental", null, locale)), regular,
					null, locale);
			createCell(table, 3, i, study.isExperimentalIntervention(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td23", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td24", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.interTypeExp", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getInterTypeExp(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.interTypeDes", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getInterTypeDes(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.interTypeLab", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getInterTypeLab(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.randomization", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getRandomization(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.no.equivalent", null, locale)),
					regular, new Color(210, 210, 210), locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.measOcc", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getMeasOcc(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td23", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td25", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.surveyType", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getSurveyType(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td26", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td27", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.constructs", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getConstructs(), regular, null, locale); // 23
			int index = 0;
			for (StudyConstructDTO construct : study.getConstructs()) {
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.constructs.name", null, locale)), regular, null,
						locale);
				createCell(table, 3, i, construct.getName(), regular, null, locale); // 24
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.constructs.type", null, locale)), regular, null,
						locale);
				createCell(table, 3, i, construct.getType(), regular, null, locale); // 25
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.constructs.other", null, locale)), regular, null,
						locale);
				createCell(table, 3, i, construct.getOther(), regular, null, locale); // 26
				index++;
			}
			table.getCellRangeByPosition(0, i-index, 0, i).merge();
			table.getCellRangeByPosition(1, i-index, 1, i).merge();
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td23", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td28", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getInstruments(), regular, null, locale); // 27
			index = 0;
			for (StudyInstrumentDTO instrument : study.getInstruments()) {
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instrument", null, locale)), regular, null, locale);
				createCell(table, 3, i, instrument.getId(), regular, null, locale); // 28
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.title", null, locale)), regular, null,
						locale);
				createCell(table, 3, i, instrument.getTitle(), regular, null, locale); // 29
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.author", null, locale)), regular, null,
						locale);
				createCell(table, 3, i, instrument.getAuthor(), regular, null, locale); // 30
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.citation", null, locale)), regular,
						null, locale);
				createCell(table, 3, i, instrument.getCitation(), regular, null, locale); // 31
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.summary", null, locale)), regular, null,
						locale);
				createCell(table, 3, i, instrument.getSummary(), regular, null, locale); // 32
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.theoHint", null, locale)), regular,
						null, locale);
				createCell(table, 3, i, instrument.getTheoHint(), regular, null, locale); // 33
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.structure", null, locale)), regular,
						null, locale);
				createCell(table, 3, i, instrument.getStructure(), regular, null, locale); // 34
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.construction", null, locale)), regular,
						null, locale);
				createCell(table, 3, i, instrument.getConstruction(), regular, null, locale); // 35
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.objectivity", null, locale)), regular,
						null, locale);
				createCell(table, 3, i, instrument.getObjectivity(), regular, null, locale); // 36
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.reliability", null, locale)), regular,
						null, locale);
				createCell(table, 3, i, instrument.getReliability(), regular, null, locale); // 37
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.validity", null, locale)), regular,
						null, locale);
				createCell(table, 3, i, instrument.getValidity(), regular, null, locale); // 38
				index++;
				createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.norm", null, locale)), regular, null,
						locale);
				createCell(table, 3, i, instrument.getNorm(), regular, null, locale); // 39
				index++;
			}
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.description", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getDescription(), regular, null, locale);
			index++;
			table.getCellRangeByPosition(0, i-index, 0, i).merge();
			table.getCellRangeByPosition(1, i-index, 1, i).merge();
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td30", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td31", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.eligibilities", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getEligibilities(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.population", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getPopulation(), regular, null, locale);
			table.getCellRangeByPosition(0, i-1, 0, i).merge();
			table.getCellRangeByPosition(1, i-1, 1, i).merge();
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td32", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td33", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.sampleSize", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getSampleSize(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td34", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td35", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.obsUnit", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getObsUnit(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td36", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td37", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.sex", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getSex(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td38", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td39", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.age", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getAge(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td40", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td41", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.specGroups", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getSpecGroups(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td42", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td43", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.country", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getCountry(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td44", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td45", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.city", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getCity(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td46", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td47", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.region", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getRegion(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td48", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td49", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.dataRerun", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getDataRerun(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td50", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td51", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.responsibility", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getResponsibility(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td52", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td53", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.collStart", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getCollStart(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td54", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td55", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.collEnd", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getCollEnd(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td56", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td57", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.usedCollectionModes", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getUsedCollectionModes(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td58", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td59", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.sampMethod", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getSampMethod(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td60", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td61", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.recruiting", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getRecruiting(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td62", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td63", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.usedSourFormat", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getUsedSourFormat(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td64", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td65", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.sourTrans", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getSourTrans(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td66", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td67", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.specCirc", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getSpecCirc(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td68", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td69", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.transDescr", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getTransDescr(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td70", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td71", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.qualInd", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getQualInd(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.qualLim", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getQualLim(), regular, null, locale);
			table.getCellRangeByPosition(0, i-1, 0, i).merge();
			table.getCellRangeByPosition(1, i-1, 1, i).merge();
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td72", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td73", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.persDataColl", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.isPersDataColl(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.persDataPres", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getPersDataPres(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.anonymProc", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.getAnonymProc(), regular, null, locale);
			table.getCellRangeByPosition(0, i-2, 0, i).merge();
			table.getCellRangeByPosition(1, i-2, 1, i).merge();
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td74", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.td75", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.copyright", null, locale)), regular, null, locale);
			createCell(table, 3, i, study.isCopyright(), regular, null, locale);
			createCell(table, 2, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.copyrightHolder", null, locale)), regular, null,
					locale);
			createCell(table, 3, i, study.getCopyrightHolder(), regular, null, locale);
			table.getCellRangeByPosition(0, i-1, 0, i).merge();
			table.getCellRangeByPosition(1, i-1, 1, i).merge();

			doc.addColumnBreak();

			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.ft1h", null, locale))).setFont(blue_large);
			doc.addColumnBreak();
			par = doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.PsychData.ft1", null, locale)));
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
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.zpid", null, locale))).setFont(blue_reg);
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.headline", null, locale))).setFont(headline);

			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.subline", null, locale))).setFont(blue_reg);

			// Zweite Seite
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.zu1h", null, locale))).setFont(blue_large);
			doc.addColumnBreak();
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.zu1", null, locale))).setFont(regular);
			doc.addColumnBreak();
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.zu2", null, locale))).setFont(regular);
			doc.addColumnBreak();
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.zu3", null, locale))).setFont(regular);
			doc.addColumnBreak();

			Table table = doc.addTable(57, 3);
			// merge
			table.getCellRangeByPosition(0, 0, 0, 1).merge();
			table.getCellRangeByPosition(1, 0, 2, 0).merge();
			table.getCellRangeByPosition(0, 2, 0, 7).merge();
			table.getCellRangeByPosition(0, 8, 0, 9).merge();
			// table.getCellRangeByPosition(0, 10, 0, 17).merge();
			// table.getCellRangeByPosition(0, 18, 0, 28).merge();
			// table.getCellRangeByPosition(0, 29, 0, 30).merge();
			// table.getCellRangeByPosition(0, 31, 0, 54).merge();
			// table.getCellRangeByPosition(0, 55, 0, 56).merge();
			// inhalt
			Integer i = 0;
			createCell(table, 0, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.th1", null, locale)), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.dw", null, locale)), regular_bold,
					new Color(190, 190, 190), locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.th2", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.th2", null, locale)), regular_bold,
					new Color(210, 210, 210), locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.td1", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.title", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getTitle(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.transTitle", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getTransTitle(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.sAbstract", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getsAbstract(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.sAbstractTrans", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.getsAbstractTrans(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.prevWork", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getPrevWork(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.prevWorkStr", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getPrevWorkStr(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.td2", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.objectives", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getObjectives(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.relTheorys", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getRelTheorys(), regular, null, locale);
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.td3", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.intervention", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.tdh", null, locale)), regular, null,
					locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.intervention.experimental", null, locale)), regular,
					null, locale);
			createCell(table, 2, i, study.isExperimentalIntervention(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.intervention.survey", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.isSurveyIntervention(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.intervention.test", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.isTestIntervention(), regular, null, locale); // 13
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.constructs", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getConstructs(), regular, null, locale); // 14
			int index = 4;
			for (StudyConstructDTO construct : study.getConstructs()) {
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.constructs.name", null, locale)), regular, null,
						locale);
				createCell(table, 2, i, construct.getName(), regular, null, locale); // 15
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.constructs.type", null, locale)), regular, null,
						locale);
				createCell(table, 2, i, construct.getType(), regular, null, locale); // 16
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.constructs.other", null, locale)), regular, null,
						locale);
				createCell(table, 2, i, construct.getOther(), regular, null, locale); // 17
				index++;
			}
			table.getCellRangeByPosition(0, i-index, 0, i).merge();
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.td4", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.eligibilities", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.getEligibilities(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.population", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getPopulation(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.powerAnalysis", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.getPowerAnalysis(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.intSampleSize", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.getIntSampleSize(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.obsUnit", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getObsUnit(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.multilevel", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getMultilevel(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.country", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getCountry(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.city", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getCity(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.region", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getRegion(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.usedCollectionModes", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.getUsedCollectionModes(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.recruiting", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getRecruiting(), regular, null, locale); // 28
			table.getCellRangeByPosition(0, i-10, 0, i).merge();
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.td5", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.missings", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getMissings(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.dataRerun", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getDataRerun(), regular, null, locale);
			table.getCellRangeByPosition(0, i-1, 0, i).merge();
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.td6", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.repMeasures", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.getRepMeasures(), regular, null, locale); // 31
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.timeDim", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getTimeDim(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.interTypeExp", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.getInterTypeExp(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.interTypeDes", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.getInterTypeDes(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.interTypeLab", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.getInterTypeLab(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.randomization", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.getRandomization(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.interArms", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getInterArms(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.measOcc", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getMeasOcc(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.surveyType", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getSurveyType(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.getInstruments(), regular, null, locale); // 40
			index = 9;
			for (StudyInstrumentDTO instrument : study.getInstruments()) {
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instrument", null, locale)), regular, null, locale);
				createCell(table, 2, i, instrument.getId(), regular, null, locale); // 41
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.title", null, locale)), regular, null,
						locale);
				createCell(table, 2, i, instrument.getTitle(), regular, null, locale); // 42
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.author", null, locale)), regular, null,
						locale);
				createCell(table, 2, i, instrument.getAuthor(), regular, null, locale); // 43
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.citation", null, locale)), regular,
						null, locale);
				createCell(table, 2, i, instrument.getCitation(), regular, null, locale); // 44
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.summary", null, locale)), regular, null,
						locale);
				createCell(table, 2, i, instrument.getSummary(), regular, null, locale); // 45
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.theoHint", null, locale)), regular,
						null, locale);
				createCell(table, 2, i, instrument.getTheoHint(), regular, null, locale); // 46
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.structure", null, locale)), regular,
						null, locale);
				createCell(table, 2, i, instrument.getStructure(), regular, null, locale); // 47
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.construction", null, locale)), regular,
						null, locale);
				createCell(table, 2, i, instrument.getConstruction(), regular, null, locale); // 48
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.objectivity", null, locale)), regular,
						null, locale);
				createCell(table, 2, i, instrument.getObjectivity(), regular, null, locale); // 49
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.reliability", null, locale)), regular,
						null, locale);
				createCell(table, 2, i, instrument.getReliability(), regular, null, locale); // 50
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.validity", null, locale)), regular,
						null, locale);
				createCell(table, 2, i, instrument.getValidity(), regular, null, locale); // 51
				index++;
				createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.instruments.norm", null, locale)), regular, null,
						locale);
				createCell(table, 2, i, instrument.getNorm(), regular, null, locale); // 52
				index++;
			}

			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.description", null, locale)), regular, null,
					locale);
			createCell(table, 2, i, study.getDescription(), regular, null, locale);
			index++;
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.sampMethod", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getSampMethod(), regular, null, locale); // 54
			index++;
			table.getCellRangeByPosition(0, i-index, 0, i).merge();
			createCell(table, 0, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.td7", null, locale)), regular, null,
					locale);
			createCell(table, 1, i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.sourTrans", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getSourTrans(), regular, null, locale);
			createCell(table, 1, ++i, StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("study.transDescr", null, locale)), regular, null, locale);
			createCell(table, 2, i, study.getTransDescr(), regular, null, locale); // 56
			table.getCellRangeByPosition(0, i-1, 0, i).merge();
			

			doc.addColumnBreak();

			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.ft1h", null, locale))).setFont(blue_large);
			doc.addColumnBreak();
			doc.addParagraph(StringEscapeUtils.UNESCAPE_HTML4.translate(messageSource.getMessage("export.odt.Pre.ft1", null, locale)));

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
				cell.setDoubleValue(Double.parseDouble(content.toString()));
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
					if (FORMTYPES.isEmpty()) {
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
