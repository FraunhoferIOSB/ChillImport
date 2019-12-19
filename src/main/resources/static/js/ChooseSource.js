var isExcel = false;
var isCsv = false;
var currentFileName = "";
var currentDelimiter = ";";
var currentHeaderLines = 0;


function getCurrentFileName() {
    return currentFileName;
}

/**
 * checks the input file (must be .csv or .xlsx)
 */
function checkinputfile() {
    var name = $("#file").val().split(/(\\|\/)/g).pop();
    if (name.match(".*\.csv$")) {
        $("#oksource").attr("disabled", false);
        isCsv = true;
        isExcel = false;
    } else if (name.match(".*\.xlsx?$")) {
        $("#oksource").attr("disabled", false);
        isCsv = false;
        isExcel = true;
    } else {
        isExcel = false;
        isCsv = false;
        $("#oksource").attr("disabled", true);
    }
}

/**
 * checks the url of the source
 */
function checkinputurl() {
    var url = $("#sourceinput").val().toString();
    var pattern = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;

    if (pattern.test(url)) {
        isExcel = false;
        isCsv = false;
        $("#oksource").attr("disabled", false);
    } else {
        isExcel = false;
        isCsv = false;
        $("#oksource").attr("disabled", true);
    }
}

function sourcefile() {
    document.getElementById("sourcetext").innerText = "Choose sourcefile:";
    $("#sourceinput").hide();
    $("#file").show();
    checkinputfile();

}

function sourcesite() {
    document.getElementById("sourcetext").innerText = "Choose website:";
    $("#sourceinput").show();
    $("#file").hide();
    checkinputurl();
}

function sourceserver() {
    document.getElementById("sourcetext").innerText = "Choose server:";
    $("#sourceinput").show();
    $("#file").hide();
}

function upload() {
    $("#previewbutton").prop("disabled", true);
    $("#importbutton").prop("disabled", true);
    if ($("input[name=source]:eq(0)").is(":checked")) {
        uploadFile();
    } else if ($("input[name=source]:eq(1)").is(":checked")) {
        uploadUrl();
    }
}

function uploadFile() {
    var form = $("#uploader")[0];

    var data = new FormData(form);

    $.ajax({
        type: "POST",
        enctype: "multipart/form-data",
        url: "upload",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        success: function (e) {
            $.notify({
                message: "File has been uploaded"
            }, {
                allow_dismiss: true,
                type: "success",
                placement: {
                    from: "top",
                    align: "left"
                },
                animate: {
                    enter: "animated fadeInDown",
                    exit: "animated fadeOutUp"
                },
                z_index: 9000
            });
            currentFileName = e;
            $("#importbutton").prop("disabled", false);
            addToLog("Finished processing file. Ready for import.");
            preview();
        },
        error: function (e) {
            $.notify({
                message: "File could not be uploaded. Check Log for errors"
            }, {
                allow_dismiss: true,
                type: "danger",
                placement: {
                    from: "top",
                    align: "left"
                },
                animate: {
                    enter: "animated fadeInDown",
                    exit: "animated fadeOutUp"
                },
                z_index: 9000
            });
            addToLog(e.responseText);
        }
    });

    return false;
}

function uploadUrl() {
    $.ajax({
        type: "POST",
        url: "uploadFromUrl",
        data: {url: $("#sourceinput").val()},
        success: function (e) {
            $.notify({
                message: "File has been uploaded"
            }, {
                allow_dismiss: true,
                type: "success",
                placement: {
                    from: "top",
                    align: "left"
                },
                animate: {
                    enter: "animated fadeInDown",
                    exit: "animated fadeOutUp"
                },
                z_index: 9000
            });
                $("#importbutton").prop("disabled", false);
                addToLog("Finished processing file. Ready for import.");
                preview();
            },
        error: function(e) {
            $.notify({
                message: "File could not be uploaded. Check Log for errors"
            }, {
                allow_dismiss: true,
                type: "danger",
                placement: {
                    from: "top",
                    align: "left"
                },
                animate: {
                    enter: "animated fadeInDown",
                    exit: "animated fadeOutUp"
                },
                z_index: 9000
            });
                addToLog(e.responseText);
            }
    });

    return false;
}


function preview() {
    $("#previewbutton").prop("disabled", true);

    $.ajax({
        type: "GET",
        url: "preview",
        data: {
            filename: currentFileName,
            headerLines: currentHeaderLines,
            delimiter: currentDelimiter
        },
        success: function (result) {
            loadPreview(result);
            $("#previewbutton").prop("disabled", false);
        },
        error: function (e) {
            addToLog(e.responseText);
            $("#previewbutton").prop("disabled", false);
        }
    });
}

function loadPreview(values) {
    var tablebody = $("#previewTable").find("tbody:eq(0)");
    var tablehead = $("#previewTable").find("thead:eq(0)");
    tablehead.empty().append($("<tr>"));
    tablebody.empty();
    var rows = values.length;
    if (rows > 0) {
        var columns = values[0].length;
    }
    for (var i = 0; i < rows; i++) {
        tablebody.append($("<tr>"));
        var current = tablebody.find("tr").last();
        for (var j = 0; j < columns; j++) {
            current.append($("<td>")
                .text(values[i][j])
            );
        }
    }

    var current = tablehead.find("tr").last();
    for (var k = 0; k < columns; k++) {
        current.append($("<th>")
            .text("Column " + k)
        );
    }

}

/**
 * disables inputs which are not needed for excel
 */
function excelconfig() {
    $("#delimiter").attr("disabled", true);

    $("#timeTable").find("tbody tr").each(function () {
        var obj = {},
            $td = $(this).find("td");
        obj["string"] = $td.eq(1).find("input").attr("disabled", true);
    });
}

/**
 * enables all inputs
 */
function csvconfig() {
    $("#delimiter").attr("disabled", false);

    $("#timeTable").find("tbody tr").each(function () {
        var obj = {},
            $td = $(this).find("td");
        obj["string"] = $td.eq(1).find("input").attr("disabled", false);
    });
}

/**
 * adds "disabled" attribute to inputs (which are not needed for excel) if source is a excel file
 * removes "disabled" if source is a csv
 */
function optimzeforsource() {
    if (isCsv && isExcel) {
        addToLog("Unexpected source");
    } else if (isExcel && !isCsv) {
        excelconfig();
    } else if (isCsv && !isExcel) {
        csvconfig();
    } else {
        csvconfig();
    }
}