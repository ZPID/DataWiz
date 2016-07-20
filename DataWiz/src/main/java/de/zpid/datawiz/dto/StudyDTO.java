package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import de.zpid.datawiz.enumeration.InterventionTypes;

public class StudyDTO implements Serializable {

  private static final long serialVersionUID = -7300213401850684971L;
  private long id;
  private long projectId;
  private long lastUserId;
  private LocalDateTime lastEdit;
  private boolean currentlyEdit;
  private LocalDateTime editSince;
  private long editUserId;

  public interface StGeneralVal {

  }

  /** Study01 */
  @NotNull(groups = StGeneralVal.class)
  @Size(min = 0, max = 250, groups = StGeneralVal.class)
  private String title;
  /** Study02 */
  @Size(min = 0, max = 50, groups = StGeneralVal.class)
  private String internalID;
  /** Study03 */
  @Size(min = 0, max = 250, groups = StGeneralVal.class)
  private String transTitle;
  /** Study04 */
  private List<ContributorDTO> contributors;
  /** Study05 */
  @Size(min = 0, max = 2000, groups = StGeneralVal.class)
  private String sAbstract;
  /** Study06 */
  @Size(min = 0, max = 2000, groups = StGeneralVal.class)
  private String sAbstractTrans;
  /** Study07 -> [null , complete, excerpt] */
  @Pattern(regexp = "(^$|COMPLETE|EXCERPT)", groups = StGeneralVal.class)
  private String completeSel;
  /** Study08 */
  @Size(min = 0, max = 500, groups = StGeneralVal.class)
  private String excerpt;
  /** Study09 -> [null , replication, followup, other, norelation] */
  @Pattern(regexp = "(^$|REPLICATION|FOLLOWUP|OTHER|NORELATION)", groups = StGeneralVal.class)
  private String prevWork;
  /** Study10 */
  @Size(min = 0, max = 500, groups = StGeneralVal.class)
  private String prevWorkStr;
  /** Study11 */
  @Valid
  private List<StudyListTypesDTO> software;
  /** Study12 */
  @Valid
  private List<StudyListTypesDTO> pubOnData;
  /** Study13 */
  @Valid
  private List<StudyListTypesDTO> conflInterests;

  public interface StDesignVal {
  }

  /** Study14/15 */
  @Valid
  private List<StudyListTypesDTO> objectives;
  /** Study16 */
  @Valid
  private List<StudyListTypesDTO> relTheorys;
  /** Study17 -> DMP89 */
  @Pattern(regexp = "(^$|SINGLE|MULTIPLE)", groups = StDesignVal.class)
  private String repMeasures;
  /** Study18 */
  @Valid
  private List<StudyListTypesDTO> measOccName;
  /** Study19 */
  @Size(min = 0, max = 2000, groups = StDesignVal.class)
  private String timeDim;
  /** Study20 survey data */
  private boolean surveyIntervention;
  /** Study20 experimental data */
  private boolean experimentalIntervention;
  /** Study20 test data */
  private boolean testIntervention;
  /** Study21 */
  private InterventionTypes interTypeExp;
  /** Study22 */
  private InterventionTypes interTypeDes;
  /** Study23 */
  private InterventionTypes interTypeLab;
  /** Study24 */
  private InterventionTypes randomization;
  /** Study25 */
  @Valid
  private List<StudyListTypesDTO> interArms;
  /** Study26 */
  /* private List<Boolean> interTimeTable; */
  /** Study27 */
  private InterventionTypes surveyType;
  /** Study28/ Study29 */
  @Valid
  private List<StudyConstructDTO> constructs;
  /** Study30 - Study40 */
  @Valid
  private List<StudyInstrumentDTO> instruments;
  /** Study41 */
  @Size(min = 0, max = 2000, groups = StDesignVal.class)
  private String description;

  public interface StCharacteristicsVal {
  }

  /** Study42 */
  @Valid
  private List<StudyListTypesDTO> eligibilities;
  /** Study43 */
  @Size(min = 0, max = 1000, groups = StCharacteristicsVal.class)
  private String population;
  /** Study44 */
  @Size(min = 0, max = 500, groups = StCharacteristicsVal.class)
  private String sampleSize;
  /** Study45 */
  @Size(min = 0, max = 1000, groups = StCharacteristicsVal.class)
  private String powerAnalysis;
  /** Study46 */
  @Size(min = 0, max = 500, groups = StCharacteristicsVal.class)
  private String intSampleSize;
  /** Study47 */
  @Pattern(regexp = "(^$|INDIVIDUALS|DYADS|FAMILIES|GROUPS|ORGANIZATIONS|OTHER)", groups = StCharacteristicsVal.class)
  private String obsUnit;
  /** Study47 */
  @Size(min = 0, max = 250, groups = StCharacteristicsVal.class)
  private String obsUnitOther;
  /** Study48/49 */
  @Size(min = 0, max = 2000, groups = StCharacteristicsVal.class)
  private String multilevel;
  /** Study50 */
  @Size(min = 0, max = 500, groups = StCharacteristicsVal.class)
  private String sex;
  /** Study51 */
  @Size(min = 0, max = 500, groups = StCharacteristicsVal.class)
  private String age;
  /** Study52 */
  @Size(min = 0, max = 500, groups = StCharacteristicsVal.class)
  private String specGroups;
  /** Study53 */
  @Size(min = 0, max = 250, groups = StCharacteristicsVal.class)
  private String country;
  /** Study54 */
  @Size(min = 0, max = 250, groups = StCharacteristicsVal.class)
  private String city;
  /** Study55 */
  @Size(min = 0, max = 250, groups = StCharacteristicsVal.class)
  private String region;
  /** Study56 */
  @Size(min = 0, max = 2000, groups = StCharacteristicsVal.class)
  private String missings;
  /** Study57 */
  @Size(min = 0, max = 2000, groups = StCharacteristicsVal.class)
  private String dataRerun;

  public interface StCollectionVal {
  }

  /** Study58 */
  @Pattern(regexp = "(^$|PRIMARY|OTHER)", groups = StCollectionVal.class)
  private String responsibility;
  /** Study58 */
  @Size(min = 0, max = 1000, groups = StCollectionVal.class)
  private String responsibilityOther;
  /** Study59 */
  @DateTimeFormat(pattern = "dd/MM/yyyy")
  private LocalDate collStart;
  /** Study60 */
  @DateTimeFormat(pattern = "dd/MM/yyyy")
  private LocalDate collEnd;
  /** Study61 -> DMP14 PsychData - META096 */
  private List<Integer> usedCollectionModes;
  /** Study61 -> DMP87 other Collection Modes with Invest. present */
  @Size(min = 0, max = 1000, groups = StCollectionVal.class)
  private String otherCMIP;
  /** Study61 -> DMP87 other Collection Modes with Invest. not present */
  @Size(min = 0, max = 1000, groups = StCollectionVal.class)
  private String otherCMINP;
  /** Study62 */
  @Pattern(regexp = "(^$|ACCRUING|CENSUS|RANDOM|CLUSTER|STRATIFIED|QUOTA|OTHER)", groups = StCollectionVal.class)
  private String sampMethod;
  /** Study62 */
  @Size(min = 0, max = 500, groups = StCollectionVal.class)
  private String sampMethodOther;
  /** Study63 */
  @Size(min = 0, max = 1000, groups = StCollectionVal.class)
  private String recruiting;
  /** Study64 */
  private List<Integer> usedSourFormat;
  /** Study64 */
  @Size(min = 0, max = 500, groups = StCollectionVal.class)
  private String otherSourFormat;
  /** Study65 select -> [null , simple, complex] */
  @Pattern(regexp = "(^$|SIMPLE|COMPLEX|OTHER)", groups = StCollectionVal.class)
  private String sourTrans;
  /** Study65 if select == complex */
  @Size(min = 0, max = 500, groups = StCollectionVal.class)
  private String otherSourTrans;
  /** Study66 */
  @Size(min = 0, max = 2000, groups = StCollectionVal.class)
  private String specCirc;
  /** Study67 */
  @Size(min = 0, max = 2000, groups = StCollectionVal.class)
  private String transDescr;
  /** Study68 */
  @Size(min = 0, max = 1000, groups = StCollectionVal.class)
  private String qualInd;
  /** Study69 */
  @Size(min = 0, max = 1000, groups = StCollectionVal.class)
  private String qualLim;

  public interface StEthicalVal {
  }

  /** Study70 */
  private boolean irb;
  /** Study71 */
  @Size(min = 0, max = 500, groups = StEthicalVal.class)
  private String irbName;
  /** Study72 */
  private boolean consent;
  /** Study73 */
  private boolean consentShare;
  /** Study74 */
  private boolean persDataColl;
  /** Study75 -> [null , anonymous, non_anonymous] */
  @Pattern(regexp = "(^$|ANONYMOUS|NON_ANONYMOUS)", groups = StEthicalVal.class)
  private String persDataPres;
  /** Study76 */
  @Size(min = 0, max = 1000, groups = StEthicalVal.class)
  private String anonymProc;
  /** Study77 */
  private boolean copyright;
  /** Study78 */
  @Size(min = 0, max = 1000, groups = StEthicalVal.class)
  private String copyrightHolder;
  /** Study79 */
  private boolean thirdParty;
  /** Study80 */
  @Size(min = 0, max = 1000, groups = StEthicalVal.class)
  private String thirdPartyHolder;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getProjectId() {
    return projectId;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

  public boolean isCurrentlyEdit() {
    return currentlyEdit;
  }

  public void setCurrentlyEdit(boolean currentlyEdit) {
    this.currentlyEdit = currentlyEdit;
  }

  public LocalDateTime getEditSince() {
    return editSince;
  }

  public void setEditSince(LocalDateTime editSince) {
    this.editSince = editSince;
  }

  public long getEditUserId() {
    return editUserId;
  }

  public void setEditUserId(long editUserId) {
    this.editUserId = editUserId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getInternalID() {
    return internalID;
  }

  public void setInternalID(String internalID) {
    this.internalID = internalID;
  }

  public String getTransTitle() {
    return transTitle;
  }

  public void setTransTitle(String transTitle) {
    this.transTitle = transTitle;
  }

  public List<ContributorDTO> getContributors() {
    return contributors;
  }

  public void setContributors(List<ContributorDTO> contributors) {
    this.contributors = contributors;
  }

  public String getsAbstract() {
    return sAbstract;
  }

  public void setsAbstract(String sAbstract) {
    this.sAbstract = sAbstract;
  }

  public String getsAbstractTrans() {
    return sAbstractTrans;
  }

  public void setsAbstractTrans(String sAbstractTrans) {
    this.sAbstractTrans = sAbstractTrans;
  }

  public String getCompleteSel() {
    return completeSel;
  }

  public void setCompleteSel(String completeSel) {
    this.completeSel = completeSel;
  }

  public String getExcerpt() {
    return excerpt;
  }

  public void setExcerpt(String excerpt) {
    this.excerpt = excerpt;
  }

  public String getPrevWork() {
    return prevWork;
  }

  public void setPrevWork(String prevWork) {
    this.prevWork = prevWork;
  }

  public String getPrevWorkStr() {
    return prevWorkStr;
  }

  public void setPrevWorkStr(String prevWorkStr) {
    this.prevWorkStr = prevWorkStr;
  }

  public List<StudyListTypesDTO> getSoftware() {
    return software;
  }

  public void setSoftware(List<StudyListTypesDTO> software) {
    this.software = software;
  }

  public List<StudyListTypesDTO> getPubOnData() {
    return pubOnData;
  }

  public void setPubOnData(List<StudyListTypesDTO> pubOnData) {
    this.pubOnData = pubOnData;
  }

  public List<StudyListTypesDTO> getConflInterests() {
    return conflInterests;
  }

  public void setConflInterests(List<StudyListTypesDTO> conflInterests) {
    this.conflInterests = conflInterests;
  }

  public List<StudyListTypesDTO> getObjectives() {
    return objectives;
  }

  public void setObjectives(List<StudyListTypesDTO> objectives) {
    this.objectives = objectives;
  }

  public List<StudyListTypesDTO> getRelTheorys() {
    return relTheorys;
  }

  public void setRelTheorys(List<StudyListTypesDTO> relTheorys) {
    this.relTheorys = relTheorys;
  }

  public String getRepMeasures() {
    return repMeasures;
  }

  public void setRepMeasures(String repMeasures) {
    this.repMeasures = repMeasures;
  }

  public List<StudyListTypesDTO> getMeasOccName() {
    return measOccName;
  }

  public void setMeasOccName(List<StudyListTypesDTO> measOccName) {
    this.measOccName = measOccName;
  }

  public String getTimeDim() {
    return timeDim;
  }

  public void setTimeDim(String timeDim) {
    this.timeDim = timeDim;
  }

  public boolean isSurveyIntervention() {
    return surveyIntervention;
  }

  public void setSurveyIntervention(boolean surveyIntervention) {
    this.surveyIntervention = surveyIntervention;
  }

  public boolean isExperimentalIntervention() {
    return experimentalIntervention;
  }

  public void setExperimentalIntervention(boolean experimentalIntervention) {
    this.experimentalIntervention = experimentalIntervention;
  }

  public boolean isTestIntervention() {
    return testIntervention;
  }

  public void setTestIntervention(boolean testIntervention) {
    this.testIntervention = testIntervention;
  }

  public InterventionTypes getInterTypeExp() {
    return interTypeExp;
  }

  public void setInterTypeExp(InterventionTypes interTypeExp) {
    this.interTypeExp = interTypeExp;
  }

  public InterventionTypes getInterTypeDes() {
    return interTypeDes;
  }

  public void setInterTypeDes(InterventionTypes interTypeDes) {
    this.interTypeDes = interTypeDes;
  }

  public InterventionTypes getInterTypeLab() {
    return interTypeLab;
  }

  public void setInterTypeLab(InterventionTypes interTypeLab) {
    this.interTypeLab = interTypeLab;
  }

  public InterventionTypes getRandomization() {
    return randomization;
  }

  public void setRandomization(InterventionTypes randomization) {
    this.randomization = randomization;
  }

  public List<StudyListTypesDTO> getInterArms() {
    return interArms;
  }

  public void setInterArms(List<StudyListTypesDTO> interArms) {
    this.interArms = interArms;
  }

  /*
   * public List<Boolean> getInterTimeTable() { return interTimeTable; }
   * 
   * public void setInterTimeTable(List<Boolean> interTimeTable) { this.interTimeTable = interTimeTable; }
   */

  public InterventionTypes getSurveyType() {
    return surveyType;
  }

  public void setSurveyType(InterventionTypes surveyType) {
    this.surveyType = surveyType;
  }

  public List<StudyConstructDTO> getConstructs() {
    return constructs;
  }

  public void setConstructs(List<StudyConstructDTO> constructs) {
    this.constructs = constructs;
  }

  public List<StudyInstrumentDTO> getInstruments() {
    return instruments;
  }

  public void setInstruments(List<StudyInstrumentDTO> instruments) {
    this.instruments = instruments;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<StudyListTypesDTO> getEligibilities() {
    return eligibilities;
  }

  public void setEligibilities(List<StudyListTypesDTO> eligibilities) {
    this.eligibilities = eligibilities;
  }

  public String getPopulation() {
    return population;
  }

  public void setPopulation(String population) {
    this.population = population;
  }

  public String getSampleSize() {
    return sampleSize;
  }

  public void setSampleSize(String sampleSize) {
    this.sampleSize = sampleSize;
  }

  public String getPowerAnalysis() {
    return powerAnalysis;
  }

  public void setPowerAnalysis(String powerAnalysis) {
    this.powerAnalysis = powerAnalysis;
  }

  public String getIntSampleSize() {
    return intSampleSize;
  }

  public void setIntSampleSize(String intSampleSize) {
    this.intSampleSize = intSampleSize;
  }

  public String getObsUnit() {
    return obsUnit;
  }

  public void setObsUnit(String obsUnit) {
    this.obsUnit = obsUnit;
  }

  public String getObsUnitOther() {
    return obsUnitOther;
  }

  public void setObsUnitOther(String obsUnitOther) {
    this.obsUnitOther = obsUnitOther;
  }

  public String getMultilevel() {
    return multilevel;
  }

  public void setMultilevel(String multilevelDescr) {
    this.multilevel = multilevelDescr;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public String getSpecGroups() {
    return specGroups;
  }

  public void setSpecGroups(String specGroups) {
    this.specGroups = specGroups;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getMissings() {
    return missings;
  }

  public void setMissings(String missings) {
    this.missings = missings;
  }

  public String getDataRerun() {
    return dataRerun;
  }

  public void setDataRerun(String dataRerun) {
    this.dataRerun = dataRerun;
  }

  public long getLastUserId() {
    return lastUserId;
  }

  public void setLastUserId(long lastUserId) {
    this.lastUserId = lastUserId;
  }

  public LocalDateTime getTimestamp() {
    return lastEdit;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.lastEdit = timestamp;
  }

  public String getResponsibility() {
    return responsibility;
  }

  public void setResponsibility(String responsibility) {
    this.responsibility = responsibility;
  }

  public String getResponsibilityOther() {
    return responsibilityOther;
  }

  public void setResponsibilityOther(String responsibilityOther) {
    this.responsibilityOther = responsibilityOther;
  }

  public LocalDate getCollStart() {
    return collStart;
  }

  public void setCollStart(LocalDate collStart) {
    this.collStart = collStart;
  }

  public LocalDate getCollEnd() {
    return collEnd;
  }

  public void setCollEnd(LocalDate collEnd) {
    this.collEnd = collEnd;
  }

  public List<Integer> getUsedCollectionModes() {
    return usedCollectionModes;
  }

  public void setUsedCollectionModes(List<Integer> usedCollectionModes) {
    this.usedCollectionModes = usedCollectionModes;
  }

  public String getOtherCMIP() {
    return otherCMIP;
  }

  public void setOtherCMIP(String otherCMIP) {
    this.otherCMIP = otherCMIP;
  }

  public String getOtherCMINP() {
    return otherCMINP;
  }

  public void setOtherCMINP(String otherCMINP) {
    this.otherCMINP = otherCMINP;
  }

  public String getSampMethod() {
    return sampMethod;
  }

  public void setSampMethod(String sampMethod) {
    this.sampMethod = sampMethod;
  }

  public String getSampMethodOther() {
    return sampMethodOther;
  }

  public void setSampMethodOther(String sampMethodOther) {
    this.sampMethodOther = sampMethodOther;
  }

  public String getRecruiting() {
    return recruiting;
  }

  public void setRecruiting(String recruiting) {
    this.recruiting = recruiting;
  }

  public List<Integer> getUsedSourFormat() {
    return usedSourFormat;
  }

  public void setUsedSourFormat(List<Integer> usedSourFormat) {
    this.usedSourFormat = usedSourFormat;
  }

  public String getOtherSourFormat() {
    return otherSourFormat;
  }

  public void setOtherSourFormat(String otherSourFormat) {
    this.otherSourFormat = otherSourFormat;
  }

  public String getSourTrans() {
    return sourTrans;
  }

  public void setSourTrans(String sourTrans) {
    this.sourTrans = sourTrans;
  }

  public String getOtherSourTrans() {
    return otherSourTrans;
  }

  public void setOtherSourTrans(String otherSourTrans) {
    this.otherSourTrans = otherSourTrans;
  }

  public String getSpecCirc() {
    return specCirc;
  }

  public void setSpecCirc(String specCirc) {
    this.specCirc = specCirc;
  }

  public String getTransDescr() {
    return transDescr;
  }

  public void setTransDescr(String transDescr) {
    this.transDescr = transDescr;
  }

  public String getQualInd() {
    return qualInd;
  }

  public void setQualInd(String qualInd) {
    this.qualInd = qualInd;
  }

  public String getQualLim() {
    return qualLim;
  }

  public void setQualLim(String qualLim) {
    this.qualLim = qualLim;
  }

  public boolean isIrb() {
    return irb;
  }

  public void setIrb(boolean irb) {
    this.irb = irb;
  }

  public String getIrbName() {
    return irbName;
  }

  public void setIrbName(String irbName) {
    this.irbName = irbName;
  }

  public boolean isConsent() {
    return consent;
  }

  public void setConsent(boolean consent) {
    this.consent = consent;
  }

  public boolean isConsentShare() {
    return consentShare;
  }

  public void setConsentShare(boolean consentShare) {
    this.consentShare = consentShare;
  }

  public boolean isPersDataColl() {
    return persDataColl;
  }

  public void setPersDataColl(boolean persDataColl) {
    this.persDataColl = persDataColl;
  }

  public String getPersDataPres() {
    return persDataPres;
  }

  public void setPersDataPres(String persDataPres) {
    this.persDataPres = persDataPres;
  }

  public String getAnonymProc() {
    return anonymProc;
  }

  public void setAnonymProc(String anonymProc) {
    this.anonymProc = anonymProc;
  }

  public boolean isCopyright() {
    return copyright;
  }

  public void setCopyright(boolean copyright) {
    this.copyright = copyright;
  }

  public String getCopyrightHolder() {
    return copyrightHolder;
  }

  public void setCopyrightHolder(String copyrightHolder) {
    this.copyrightHolder = copyrightHolder;
  }

  public boolean isThirdParty() {
    return thirdParty;
  }

  public void setThirdParty(boolean thirdParty) {
    this.thirdParty = thirdParty;
  }

  public String getThirdPartyHolder() {
    return thirdPartyHolder;
  }

  public void setThirdPartyHolder(String thirdPartyHolder) {
    this.thirdPartyHolder = thirdPartyHolder;
  }

}
