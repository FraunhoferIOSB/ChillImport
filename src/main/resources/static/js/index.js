/**
 * Calls preview function after delimiter has been changed. Checks delimiter to be a valid regular expression.
 * @param del new value of the delimiter
 */
function changedDelimiter(del) {
    try {
        new RegExp(del);
        currentDelimiter = (del === null || del === "") ? "\n" : del;
    } catch(e) {
        currentDelimiter = "\n";
    }
    preview();
}

/**
 * Calls preview function after number of header lines has been changed.
 * @param lines new number of header lines
 */
function changedHeaderLines(lines) {
    if (/^0*[0-9]{1,4}$/.test(lines)){
        currentHeaderLines = lines;
    } else {
        currentHeaderLines = 0;
    }
    preview();
}





/**
 * Opens the accordion with the given id
 * @param accId the id of the accordion
 */
function openaccordion(accId) {
    var tempacc = document.getElementById(accId);
    var panel = tempacc.nextElementSibling;
    panel.style.display = "block";
}

/**
 * Closes the accordion with the id accId
 * @param accId the id of the accordion
 */
function closeaccordion(accId) {
    var tempacc = document.getElementById(accId);
    var panel = tempacc.nextElementSibling;
    panel.style.display = "none";
}

/**
 * This func is called if the user presses the "choose config" button
 *
 */
function chooseConfig() {
    $("#configs").val(null);
    closeaccordion("currentConfigAcc");
    document.getElementById("chooseConfigDialog").style.display = "block";
    optimzeforsource();
    showstep3();
}

/**
 * This func is called if the user presses the "create config" button
 *
 */
function createConfig() {
    document.getElementById("chooseConfigDialog").style.display = "none";
    openaccordion("currentConfigAcc");
    resetConfig(); //includes the function "optimizeforsource"
    showstep1();
}

/**
 * opens the help page
 */
function openHelp() {
    window.open("help.html", "_blank");
}

/**
 * toggles the left log
 */
function togglelog() {
    if (document.getElementById("logbox").style.visibility === "hidden") {
        //show log
        document.getElementById("logbox").style.visibility = "visible";
        document.getElementById("logbutton").innerHTML = "LOG <i class=\"fa fa-arrow-left\"></i>";
    } else {
        //hide log
        document.getElementById("logbox").style.visibility = "hidden";
        document.getElementById("logbutton").innerHTML = "LOG <i class=\"fa fa-arrow-right\"></i>";
    }
}

/**
 * if needed the newmessagetoken will be visible.
 * not needed if log is visible
 */
function showmessagetag() {
    if (document.getElementById("logbox").style.visibility === "hidden") {
        document.getElementById("logbutton").innerHTML = "<div id=\"newmessagetoken\" class=\"loginfo\" style=\"left:135px; bottom: 60px;\">\n" +
            "            <i class=\"fas fa-info-circle\" style=\"font-size: 20px\"></i>\n" +
            "        </div>\n" +
            "        LOG\n" +
            "        <i class=\"fa fa-arrow-right\"></i>";
    }
}

function shownext() {
    if (document.getElementById("thingbox").style.display === "none") {
        showstep2();
    } else if (document.getElementById("thingbox").style.display = "block") {
        showstep3();
    }
}

function showstep1() {
    document.getElementById("thingbox").style.display = "none";
    document.getElementById("streambox").style.display = "none";
    document.getElementById("savebtn").style.display = "none";
    document.getElementById("nextbtn").style.display = "block";
}

function showstep2() {
    document.getElementById("thingbox").style.display = "block";
    document.getElementById("streambox").style.display = "none";
    document.getElementById("savebtn").style.display = "none";
    document.getElementById("nextbtn").style.display = "block";
}

function showstep3() {
    document.getElementById("thingbox").style.display = "block";
    document.getElementById("streambox").style.display = "block";
    document.getElementById("savebtn").style.display = "block";
    document.getElementById("nextbtn").style.display = "none";
}

