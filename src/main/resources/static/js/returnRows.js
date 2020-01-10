/*global addToLog*/
var path;

function returnRows() {
	$.ajax({
		type : "GET",
		url : "errors/returnFiles",
		success : function(response) {
			var files = JSON.parse(response);
			var $x = $("#select");
			path = files[files.length - 1];

			files.forEach(function(val) {
				var lul = val;
				$x.append("<option value =" + lul + ">" + lul + "</option>");
			});
		},
		error : function(e) {
			addToLog(e.responseText);
		}
	});
}

function delFile() {
	var file = $("#select").val();
	if (file === null) {
		addToLog("Select a File first");
	}
	var mydata = {
		name : file
	};
	$.ajax({
		type : "GET",
		url : "errors/delFile",
		data : mydata,
		success : function(e) {
			addToLog("Deleted File");
			var selectobject = document.getElementById("select");
			for (var i = 0; i < selectobject.length; i++) {
				if (selectobject.options[i].value === file) {
					selectobject.remove(i);
					selectobject.selectedIndex = "0";
					return;
				}
			}
		},
		error : function(e) {
			addToLog("Could not delete File");
		}
	});
}

function dl() {
	var x = $("#select").val();
	return "/returnRows/" + x;

}