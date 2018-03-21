$(document).ready(function() {
	console.log(window.location.pathname.split('/')[1])
	$.ajax({
		url : "/DataWiz/api/sideMenu/",
	}).done(function(data) {
		createSideMenu(data);
	});

	$("#dwSidenavBTN").on("click", function() {
		$('#dwSidenav').fadeIn('fast');
		$('#dwSidenavBTN').fadeOut('fast');

	});
	$("#dwSideNavClose").on("click", function() {
		$('#dwSidenav').fadeOut('fast');
		$('#dwSidenavBTN').fadeIn('fast');
	});
});

function createSideMenu(data) {
	if (data != null && data.items != null && data.items.length > 0) {
		$('#dwSidenavBTN').fadeIn('fast');
		var ul_projects = $('<ul id="dwSidenavProjects"></ul>')
		var i = 0;
		data.items.forEach(function(project) {
			var li_project = $('<li class="accordion"><a data-toggle="collapse" data-parent="#dwSidenavProjects" href="#pro'
					+ i + '">' + project.title + '</a></li>');
			var li_div = $('<div id="pro' + (i++) + '" class="collapse"></div>')
			li_div.append(createProjectLinkList(data, project));
			if (project.sublist != null && project.sublist.length > 0) {
				var ul_studies = $('<ul class="dwSidenavStudies"></ul>')
				project.sublist.forEach(function(study) {
					var li_study = $('<li>' + study.title + '</li>');
					li_study.append(createStudyLinkList(data, project, study));
					if (study.sublist != null && study.sublist.length > 0) {
						var ul_records = $('<ul class="dwSidenavRecords"></ul>')
						study.sublist.forEach(function(record) {
							var li_record = $('<li>' + record.title + '</li>');
							li_record.append(createRecordsLinkList(data, project, study, record));
							ul_records.append(li_record);
						});
						li_study.append(ul_records);
					}
					ul_studies.append(li_study)
				});
				li_div.append(ul_studies);
			}
			li_project.append(li_div)
			ul_projects.append(li_project);
		});
		$('#dwSideNavListContent').append(ul_projects);
	}
}

function createProjectLinkList(data, project) {
	var link_project = '<a href="/DataWiz/project/' + project.id + '">' + data.linkProject + '</a>';
	var link_dmp = '<a href="/DataWiz/dmp/' + project.id + '">' + data.linkDmp + '</a>';
	var link_studies = '<a href="/DataWiz/project/' + project.id + '/studies">' + data.linkStudies + '</a>';
	var link_promat = '<a href="/DataWiz/project/' + project.id + '/material">' + data.linkProMat + '</a>';
	var link_contri = '<a href="/DataWiz/access/' + project.id + '">' + data.linkContri + '</a>';
	var link_export = '<a href="/DataWiz/export/' + project.id + '">' + data.linkExport + '</a>';
	return '<div class="dwSidenavProjectsSub">(&nbsp;' + link_project + '&nbsp;|&nbsp;' + link_dmp + '&nbsp;|&nbsp;'
			+ link_studies + '&nbsp;|&nbsp;' + link_promat + '&nbsp;|&nbsp;' + link_contri + '&nbsp;|&nbsp;' + link_export
			+ '&nbsp;)</div>';
}

function createStudyLinkList(data, project, study) {
	var link_pre = '/DataWiz/project/' + project.id + '/study/' + study.id;
	var link_study = '<a href="' + link_pre + '">' + data.linkStudy + '</a>';
	var link_records = '<a href="' + link_pre + '/records">' + data.linkRecords + '</a>';
	var link_studmat = '<a href="' + link_pre + '/material">' + data.linkStudMat + '</a>';
	return '<div class="dwSidenavProjectsSub">(&nbsp;' + link_study + '&nbsp;|&nbsp;' + link_records + '&nbsp;|&nbsp;'
			+ link_studmat + '&nbsp;)</div>';
}

function createRecordsLinkList(data, project, study, record) {
	var link_pre = '/DataWiz/project/' + project.id + '/study/' + study.id + '/record/' + record.id;
	var link_study = '<a href="' + link_pre + '">' + data.linkRecord + '</a>';
	var link_records = '<a href="' + link_pre + '/codebook">' + data.linkCodebook + '</a>';
	var link_studmat = '<a href="' + link_pre + '/data">' + data.linkMatrix + '</a>';
	return '<div class="dwSidenavProjectsSub">(&nbsp;' + link_study + '&nbsp;|&nbsp;' + link_records + '&nbsp;|&nbsp;'
			+ link_studmat + '&nbsp;)</div>';
}
