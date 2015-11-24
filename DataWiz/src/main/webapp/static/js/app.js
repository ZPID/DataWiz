$(document).ready(function() {
	$('[data-toggle="tooltip"]').tooltip(); 
	$('#accordion').on('hidden.bs.collapse', function () {
		//do something...
		})
		$('.accordion-toggle').click(function (e){
		  var chevState = $(e.target).siblings("row").siblings("i.indicator").toggleClass('glyphicon-chevron-down glyphicon-chevron-up');
		  console.log(chevState);
		  $("i.indicator").not(chevState).removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up");
		});
	
});