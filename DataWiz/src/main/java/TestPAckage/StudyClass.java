package TestPAckage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//import org.dom4j.Document;
//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;
//import org.dom4j.Namespace;
//import org.dom4j.io.OutputFormat;
//import org.dom4j.io.XMLWriter;

//import java.io.IOException;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyConstructDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.StudyInstrumentDTO;
import de.zpid.datawiz.dto.StudyListTypesDTO;

public class StudyClass {

	public static StudyDTO main(String[] args) {

		//Namespace ddi = new Namespace("ddi", null);
		//Namespace dwz = new Namespace("dw", null);

		//Document document = DocumentHelper.createDocument();

		List<ContributorDTO> contributors = new ArrayList<ContributorDTO>();

		ProjectDTO project = new ProjectDTO();
		project.setId(0);
		project.setFunding("Finanz");
		project.setGrantNumber("666");
		project.setDescription("Ein tolles Projekt");

		DmpDTO dmp = new DmpDTO();
		dmp.setId(0);
		dmp.setDepositName("Harald");

		ContributorDTO usr1 = new ContributorDTO();
		usr1.setTitle("Prof.");
		usr1.setFirstName("Testi");
		usr1.setLastName("Testmann");
		usr1.setInstitution("Uni Trier");
		usr1.setDepartment("ZPID");
		usr1.setOrcid("U1");
		usr1.setPrimaryContributor(true);
		usr1.setProjectId(0);
		contributors.add(usr1);

		ContributorDTO usr2 = new ContributorDTO();
		usr2.setTitle("Dr.");
		usr2.setFirstName("Donald");
		usr2.setLastName("Trump");
		usr2.setInstitution("Uni Trier");
		usr2.setDepartment("ZPID");
		usr2.setOrcid("U2");
		usr2.setPrimaryContributor(false);
		usr2.setProjectId(0);
		contributors.add(usr2);
		

		List<StudyListTypesDTO> software = new ArrayList<StudyListTypesDTO>();
		StudyListTypesDTO sw1 = new StudyListTypesDTO();
		sw1.setStudyid(1);
		sw1.setText("DataWiz");
		software.add(sw1);
		StudyListTypesDTO sw2 = new StudyListTypesDTO();
		sw2.setStudyid(1);
		sw2.setText("computer");
		software.add(sw2);

		List<StudyListTypesDTO> eligs = new ArrayList<StudyListTypesDTO>();
		StudyListTypesDTO elig1 = new StudyListTypesDTO();
		elig1.setStudyid(1);
		elig1.setText("blablabla");
		eligs.add(elig1);
		StudyListTypesDTO elig2 = new StudyListTypesDTO();
		elig2.setStudyid(2);
		elig2.setText("asdfg");
		eligs.add(elig2);

		List<Integer> sourf = new ArrayList<Integer>();
		sourf.add(10);
		sourf.add(20);
		sourf.add(30);

		List<StudyListTypesDTO> objs = new ArrayList<StudyListTypesDTO>();
		StudyListTypesDTO obj1 = new StudyListTypesDTO();
		obj1.setText("abc");
		obj1.setObjectivetype("Typ 1");
		objs.add(obj1);
		StudyListTypesDTO obj2 = new StudyListTypesDTO();
		obj2.setText("def");
		obj2.setObjectivetype("Typ 2");
		objs.add(obj2);

		List<StudyConstructDTO> cons = new ArrayList<StudyConstructDTO>();
		StudyConstructDTO con1 = new StudyConstructDTO();
		con1.setName("Konstrukt 1");
		con1.setType("T1");
		con1.setOther("");
		cons.add(con1);
		StudyConstructDTO con2 = new StudyConstructDTO();
		con2.setName("Konstrukt 2");
		con2.setType("other");
		con2.setOther("O1");
		cons.add(con2);

		List<StudyListTypesDTO> mocs = new ArrayList<StudyListTypesDTO>();
		StudyListTypesDTO moc1 = new StudyListTypesDTO();
		moc1.setText("punkt a");
		moc1.setTimetable(true);
		moc1.setSort(0);
		mocs.add(moc1);
		StudyListTypesDTO moc2 = new StudyListTypesDTO();
		moc2.setText("punkt 1");
		moc2.setTimetable(false);
		moc2.setSort(1);
		mocs.add(moc2);


		List<StudyInstrumentDTO> inss = new ArrayList<StudyInstrumentDTO>();
		StudyInstrumentDTO ins1 = new StudyInstrumentDTO();
		ins1.setTitle("Titel 1");
		ins1.setAuthor("xxx");
		ins1.setCitation("zitat");
		ins1.setSummary("zusammenfassung");
		ins1.setTheoHint("hint");
		ins1.setStructure("struktur");
		ins1.setConstruction("konstruktion");
		ins1.setObjectivity("objektivitaet");
		ins1.setReliability("reliabilitaet");
		ins1.setValidity("validitaet");
		ins1.setNorm("norm");
		inss.add(ins1);
		StudyInstrumentDTO ins2 = new StudyInstrumentDTO();
		ins2.setTitle("Titel 2");
		ins2.setAuthor("xxx");
		ins2.setCitation("zitat2");
		ins2.setSummary("zusfassung2");
		ins2.setTheoHint("hint2");
		ins2.setStructure("struktur2");
		ins2.setConstruction("konstrukt");
		ins2.setObjectivity("obj");
		ins2.setReliability("rel");
		ins2.setValidity("val");
		ins2.setNorm("norm2");
		inss.add(ins2);

		List<Integer> collm = new ArrayList<Integer>();
		collm.add(5);
		collm.add(7);

		List<StudyListTypesDTO> rels = new ArrayList<StudyListTypesDTO>();
		StudyListTypesDTO rel1 = new StudyListTypesDTO();
		rel1.setText("one");
		rels.add(rel1);
		StudyListTypesDTO rel2 = new StudyListTypesDTO();
		rel2.setText("two");
		rels.add(rel2);

		List<StudyListTypesDTO> arms = new ArrayList<StudyListTypesDTO>();
		StudyListTypesDTO arm1 = new StudyListTypesDTO();
		arm1.setText("rechts");
		arms.add(arm1);
		StudyListTypesDTO arm2 = new StudyListTypesDTO();
		arm2.setText("links");
		arms.add(arm2);

		List<StudyListTypesDTO> pubs = new ArrayList<StudyListTypesDTO>();
		StudyListTypesDTO pub1 = new StudyListTypesDTO();
		pub1.setText("alpha");
		pubs.add(pub1);
		StudyListTypesDTO pub2 = new StudyListTypesDTO();
		pub2.setText("beta");
		pubs.add(pub2);

		List<StudyListTypesDTO> confs = new ArrayList<StudyListTypesDTO>();
		StudyListTypesDTO conf1 = new StudyListTypesDTO();
		conf1.setText("eins");
		confs.add(conf1);
		StudyListTypesDTO conf2 = new StudyListTypesDTO();
		conf2.setText("zwei");
		confs.add(conf2);

		StudyDTO study = new StudyDTO();
		study.setContributors(contributors);
		study.setProjectId(0);
		study.setId(1);
		study.setInternalID("ha");
		study.setTitle("1st Study");
		study.setTransTitle("erste Studie");
		study.setCopyright(true);
		study.setCopyrightHolder("Uni Trier");
		study.setThirdParty(true);
		study.setThirdPartyHolder("ACME");
		study.setSoftware(software);
		study.setsAbstract("abcdefghijklmnopqrstuvwxyz");
		study.setsAbstractTrans("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		study.setCollStart(LocalDate.now());
		study.setCollEnd(LocalDate.now());
		study.setCountry("Germany");
		study.setCity("Trier");
		study.setRegion("RLP");
		study.setObsUnit("unit");
		study.setObsUnitOther("");
		study.setEligibilities(eligs);
		study.setPopulation("Testpopulation 1");
		study.setSex("m");
		study.setAge("2");
		study.setSpecGroups("cool");
		study.setExperimentalIntervention(true);
		study.setSurveyIntervention(false);
		study.setTestIntervention(false);
		study.setUsedSourFormat(sourf);
		study.setObjectives(objs);
		study.setConstructs(cons);
		study.setRepMeasures("reprep");
		study.setTimeDim("dim 1");
		study.setResponsibility("ich");
		study.setResponsibilityOther("");
		study.setMeasOcc(mocs);
		study.setSampleSize("1000");
		study.setPowerAnalysis("ja");
		study.setSampMethod("bla");
		study.setSampMethodOther("");
		study.setQualInd("qay");
		study.setQualLim("yaq");
		study.setDescription("reiner selbstzweck");
		study.setInstruments(inss);
		study.setUsedCollectionModes(collm);
		study.setOtherCMIP("andrer");
		study.setOtherCMINP("nichtpres");
		study.setInterTypeDes("des typ");
		study.setInterTypeExp("exp typ");
		study.setInterTypeLab("lab typ");
		study.setRandomization("randomisiert");
		study.setSurveyType("normal");
		study.setOtherSourFormat("kamel");
		study.setRelTheorys(rels);
		study.setTransDescr("tower river");
		study.setMultilevel("doppelt");
		study.setRecruiting("gabala");
		study.setSpecCirc("Kreis");
		study.setIntSampleSize("300");
		study.setDataRerun("nochmal");
		study.setPersDataPres("ne");
		study.setAnonymProc("ne");
		study.setCompleteSel("kompl");
		study.setExcerpt("und damit");
		study.setIrb(true);
		study.setIrbName("Frank");
		study.setConsent(true);
		study.setConsentShare(true);
		study.setPersDataColl(true);
		study.setPrevWork("nicht viel");
		study.setPrevWorkStr("noch weniger");
		study.setInterArms(arms);
		study.setPubOnData(pubs);
		study.setConflInterests(confs);

		/*
		Element root = document.addElement("codebook");

		ParseXML exp = new ParseXML();

		try {
			exp.exportStd(project, study, dmp, document, ddi, dwz, root);
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer;
			writer = new XMLWriter(System.out, format);
			writer.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		} */
		
		return study;

	}

}
