function filterProjectList(text) {
  $(".projectpanel").each(function(i, val) {
    if (text.trim() != '') {
      // get hidden default messages
      var unsetDesc = $('#unsetDescription').val().trim().toLowerCase();
      var unsetStudyDesc = $('#unsetStudyDescription').val().trim().toLowerCase();
      var unsetRes = $('#unsetResearcher').val().trim().toLowerCase();
      // get project description, researcher, title
      var description = $($(val).find('.projectDescription')).text().trim().toLowerCase();
      var researcher = $($(val).find('.projectResearcher')).text().trim().toLowerCase();
      var title = $($(val).find('.projectTitle')).text().trim().toLowerCase();
      // check values
      var foundInResearcher = (unsetRes == researcher) ? false : researcher.indexOf(text.toLowerCase()) >= 0;
      var foundInDescription = (unsetDesc == description) ? false : description.indexOf(text.toLowerCase()) >= 0;
      var foundInTitle = title.indexOf(text.toLowerCase()) >= 0;
      // get study
      var foundInStudy = false;
      if ($('#filterSelect').val() == 'study') {
        $($($(val).find('.studyFilter'))).each(function(j, study) {
          var found = false;
          var studyTitle = $($(study).find('.studyTitleFilter')).text().trim().toLowerCase();
          var studyDesc = $($(study).find('.studyDescriptionFilter')).text().trim().toLowerCase();
          var studyResearcher = $($(study).find('.studyResearcherFilter')).text().trim().toLowerCase();
          found = studyTitle.indexOf(text.toLowerCase()) >= 0;
          if (!found)
            found = (unsetStudyDesc == studyDesc) ? false : studyDesc.indexOf(text.toLowerCase()) >= 0;
          if (!found)
            found = (unsetRes == studyResearcher) ? false : studyResearcher.indexOf(text.toLowerCase()) >= 0;
          if (found) {
            foundInStudy = true;
            $(study).slideDown("fast");
          } else {
            $(study).slideUp("fast");
          }
        });
      } else {
        $('.studyFilter').slideDown("fast");
      }
      // set project if found match
      if (foundInTitle || foundInDescription || foundInResearcher || foundInStudy) {
        if (foundInStudy && $('#filterSelect').val() == 'study') {
          $('#panel_coll_' + (i + 1)).collapse("show");
          $(val).slideDown("slow");
        } else if (!foundInStudy && $('#filterSelect').val() == 'project') {
          $('#panel_coll_' + (i + 1)).collapse("hide");
          $(val).slideDown("slow");
        } else {
          $(val).slideUp("slow");
        }

      } else {
        $(val).slideUp("slow");
        $('#panel_coll_' + (i + 1)).collapse("hide");
      }
    } else {
      $(val).slideDown("slow");
      $('#panel_coll_' + (i + 1)).collapse("hide");
      $('.studyFilter').slideDown("fast");
    }
  });

}