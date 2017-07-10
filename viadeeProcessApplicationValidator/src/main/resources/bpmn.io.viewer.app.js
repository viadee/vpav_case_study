//mark all nodes with issues
function markNodes(bpmnViewer, elementsToMark, bpmnFile) {

    var canvas = bpmnViewer.get('canvas');

    for (id in elementsToMark) {
        if (elementsToMark[id].bpmnFile == ("src\\main\\resources\\" + bpmnFile)) {
            if (elementsToMark[id].ruleName == "VersioningChecker") {
                canvas.addMarker(elementsToMark[id].elementId, 'VersioningChecker');
            } else if (elementsToMark[id].ruleName == "ProcessVariablesNameConventionChecker") {
                canvas.addMarker(elementsToMark[id].elementId, 'ProcessVariablesNameConventionChecker');
            } else if (elementsToMark[id].ruleName == "JavaDelegateChecker") {
                canvas.addMarker(elementsToMark[id].elementId, 'JavaDelegateChecker');
            } else if (elementsToMark[id].ruleName == "EmbeddedGroovyScriptChecker") {
                canvas.addMarker(elementsToMark[id].elementId, 'EmbeddedGroovyScriptChecker');
            } else if (elementsToMark[id].ruleName == "DmnTaskChecker") {
                canvas.addMarker(elementsToMark[id].elementId, 'DmnTaskChecker');
            } else if (elementsToMark[id].ruleName == "ProcessVariablesModelChecker") {
                canvas.addMarker(elementsToMark[id].elementId, 'ProcessVariablesModelChecker');
            } else if (elementsToMark[id].ruleName == "TaskNamingConventionChecker") {
                canvas.addMarker(elementsToMark[id].elementId, 'TaskNamingConventionChecker');
            } else if (elementsToMark[id].ruleName == "BusinessRuleTaskChecker") {
                canvas.addMarker(elementsToMark[id].elementId, 'BusinessRuleTaskChecker');
            } else {
                canvas.addMarker(elementsToMark[id].elementId, 'new');
            }
        }
    }
}

//create issue count on each node
function addCountOverlay(bpmnViewer, elementsToMark, bpmnFile) {

    //getElemtIds
    var eId = [];
    for (id in elementsToMark) {
        if (elementsToMark[id].bpmnFile == ("src\\main\\resources\\" + bpmnFile))
            eId[id] = elementsToMark[id].elementId;
    }

    //doppelte Löschen
    var unique = function (origArr) {
        var newArr = [],
            origLen = origArr.length,
            found,
            x, y;

        for (x = 0; x < origLen; x++) {
            found = undefined;
            for (y = 0; y < newArr.length; y++) {
                if (origArr[x] === newArr[y]) {
                    found = true;
                    break;
                }
            }
            if (!found) newArr.push(origArr[x]);
        }
        return newArr;
    }
    var eIdUnique = unique(eId);

    //Anzahl ergänzen
    var i, j;
    var anz = 0;
    var objFehler = { eid: "dummy", anz: 0 };
    var anzArray = [];

    for (i = 0; i < eIdUnique.length; i++) {
        var anzId = eIdUnique[i];
        for (j = 0; j < eId.length; j++) {
            if (eId[j] == anzId)
                anz++;
        }
        objFehler = { eid: eIdUnique[i], anz: anz };
        anzArray[i] = objFehler;
        anz = 0;
    }

    //Anzahl an alle Fehler hängen
    var issue = { i: "dummy", anz: 0 };
    var issues = [];
    for (id in elementsToMark) {
        if (elementsToMark[id].bpmnFile == ("src\\main\\resources\\" + bpmnFile)) {
            var obj = elementsToMark[id];
            for (var i = 0; i < anzArray.length; i++) {
                if (anzArray[i].eid == obj.elementId) {
                    issue = { i: elementsToMark[id], anz: anzArray[i].anz };
                    issues[id] = issue;
                }
            }
        }
    }

    //Add Overlays
    var overlays = bpmnViewer.get('overlays');
    for (id in issues) {
        var overlayHtml = document.createElement("div");
        overlayHtml.setAttribute("class", "diagram-zahl");
        overlayHtml.appendChild(document.createTextNode(issues[id].anz));

        // add DialogMessage
        function clickOverlay(id) {
            //clear dialog
            var dp = document.querySelectorAll('.d');
            for (var i = 0; i < dp.length; i++) {
                if (dp[i].children.length === 0) {
                    dp[i].parentNode.removeChild(dp[i]);
                }
            }

            var eId = issues[id].i.elementId;
            for (y in issues) {
                if (issues[y].i.elementId == eId) {
                    var issue = issues[y].i;
                    var dl = document.getElementById("dia");

                    var hClass = document.createElement("h3");
                    var pRule = document.createElement("p");
                    var pMessage = document.createElement("p");

                    pMessage.setAttribute("id", "d_message");
                    pMessage.setAttribute("class", "d");
                    hClass.setAttribute("class", "d");
                    pRule.setAttribute("class", "d");

                    hClass.appendChild(document.createTextNode(issue.classification));
                    pRule.appendChild(document.createTextNode(issue.ruleName));
                    pMessage.appendChild(document.createTextNode(issue.message));

                    dl.appendChild(hClass);
                    dl.appendChild(pRule);
                    dl.appendChild(pMessage);
                }
            }
            toggleDialog('show');
        }

        overlayHtml.onclick = (function () {
            var currentId = id;
            return function () {
                clickOverlay(currentId);
            };

        })();
        attachOverlay();
    }

    function attachOverlay(r) {
        // attach the overlayHtml to a node
        overlays.add(issues[id].i.elementId, {
            position: {
                bottom: 10,
                right: 20
            },
            html: overlayHtml
        });
    }
}

function deleteTable() {
    //delete tBodys
    var tb = document.querySelectorAll('tbody');
    for (var i = 0; i < tb.length; i++) {
        if (tb[i].children.length === 0) {
            tb[i].parentNode.removeChild(tb[i]);
        }
    }

    var myTable = document.getElementById("table_issues");
    //delete rows
    while (myTable.rows.length > 1) {
        myTable.deleteRow(myTable.rows.length - 1);
    }
}

//create issue table
function createTable(elementsToMark, bpmnFile) {
    var myTable = document.getElementById("table_issues");

    //fill table with all issuesof current model
    for (id in elementsToMark) {
        if (elementsToMark[id].bpmnFile == ("src\\main\\resources\\" + bpmnFile)) {
            issue = elementsToMark[id];
            myParent = document.getElementsByTagName("body").item(0);

            myTBody = document.createElement("tbody");
            myRow = document.createElement("tr");
            //ruleName
            myCell = document.createElement("td");
            myText = document.createTextNode(issue.ruleName);
            myRow.setAttribute("id", issue.ruleName) // mark hole row

            //create link to mark the issue path
            var a = document.createElement("a");
            a.appendChild(myText);
            //link to docu
            a.setAttribute("href", "https://github.com/viadee/vPAV/tree/master/docs/" + issue.ruleName + ".md");

            myCell.appendChild(a);
            myRow.appendChild(myCell);
            //elementId
            myCell = document.createElement("td");
            myText = document.createTextNode(issue.elementId);
            myCell.appendChild(myText);
            myRow.appendChild(myCell);
            //elementName
            myCell = document.createElement("td");
            myText = document.createTextNode(issue.elementName);
            myCell.appendChild(myText);
            myRow.appendChild(myCell);
            //classification
            myCell = document.createElement("td");
            myText = document.createTextNode(issue.classification);
            myCell.appendChild(myText);
            myRow.appendChild(myCell);
            //message
            myCell = document.createElement("td");
            myText = document.createTextNode(issue.message);
            myCell.appendChild(myText);
            myRow.appendChild(myCell);
            //path
            myCell = document.createElement("td");
            var path_text = "";
            for (x in issue.paths) {
                for (y in issue.paths[x]) {
                    if (issue.paths[x][y].elementName == null)
                        if (y < issue.paths[x].length - 1)
                            path_text += issue.paths[x][y].elementId + " -> ";
                        else
                            path_text += issue.paths[x][y].elementId;
                    else
                        if (y < issue.paths[x].length - 1)
                            path_text += issue.paths[x][y].elementName + " -> ";
                        else
                            path_text += issue.paths[x][y].elementName
                }
                myText = document.createTextNode(path_text);
                myCell.appendChild(myText);
                path_text = "";

                //add break
                br = document.createElement("br");
                myCell.appendChild(br);
                //only add break if its not the last one
                if (x < issue.paths.length - 1) {
                    brz = document.createElement("br");
                    myCell.appendChild(brz);
                }
            }
            myRow.appendChild(myCell);
            //---------
            myTBody.appendChild(myRow);
            myTable.appendChild(myTBody);
            myParent.appendChild(myTable);
        }
    }
}

/**
 * bpmn-js-seed
 *
 * This is an example script that loads an embedded diagram file <diagramXML>
 * and opens it using the bpmn-js viewer.
 */
function initDiagram(diagramXML, r, paths) {
    // create viewer
    var bpmnViewer = new window.BpmnJS({
        container: '#canvas'
    });

    // import function
    function importXML(xml) {

        // import diagram
        bpmnViewer.importXML(xml, function (err) {

            if (err) {
                return console.error('could not import BPMN 2.0 diagram', err);
            }

            var canvas = bpmnViewer.get('canvas'),
                overlays = bpmnViewer.get('overlays');

            // zoom to fit full viewport
            canvas.zoom('fit-viewport');
            setUeberschrift(diagramXML.name);
            if (countIssues(diagramXML.name) > 0) {
                if (r == 0) {
                    markNodes(bpmnViewer, elementsToMark, diagramXML.name);
                    addCountOverlay(bpmnViewer, elementsToMark, diagramXML.name);
                } else {
                    markNodesIssue(bpmnViewer, paths, diagramXML.name);
                }
                createTable(elementsToMark, diagramXML.name);
                tableVisible(true);
            } else {
                document.getElementById("noIssues").setAttribute("style", "display: initial");
                tableVisible(false);
            }
        });
    };

    bpmnViewer.xml = diagramXML.xml;

    bpmnViewer.reload = function (model) {
        document.querySelector("#canvas").innerHTML = "";
       
        deleteTable();
        initDiagram(model, 0, null);
    };

    bpmnViewer.reloadMark = function (model, paths) {
        document.querySelector("#canvas").innerHTML = "";
        deleteTable();
        initDiagram(model, 1, paths);
    };

    // import xml
    importXML(diagramXML.xml);

    return bpmnViewer;
};

//set Filename as Header
function setUeberschrift(name) {
     document.querySelector("#modell").innerHTML = "Consistency check: " + name;
    document.getElementById("noIssues").setAttribute("style", "display: none");
}

//hideTable
function tableVisible(show) {
    if (show) {
        document.getElementById("h1_table").setAttribute("style", "display: block");
        document.getElementById("table_issues").setAttribute("style", "display: table");
    } else {
        document.getElementById("h1_table").setAttribute("style", "display: none");
        document.getElementById("table_issues").setAttribute("style", "display: none");
    }
}

//get issue count from specific bpmnFile
function countIssues(bpmnFile) {
    count = 0;
    for (id in elementsToMark) {
        if (elementsToMark[id].bpmnFile == ("src\\main\\resources\\" + bpmnFile)) {
            count++;
        }
    }
    return count;
}

//dialog
var dialogOpen = false, lastFocus, dialog, okbutton, pagebackground;
function toggleDialog(sh) {
    dialog = document.querySelector('dialog');

    if (sh == 'show') {
        dialogOpen = true;
        // show the dialog  
        dialog.setAttribute('open', 'open');

    } else {
        dialogOpen = false;
        dialog.setAttribute('open', 'false');
    }
}

// List all ProcessInstances
(function () {
    for (id in diagramXMLSource) {
        model = diagramXMLSource[id];
        var ul = document.getElementById("linkList");
        var li = document.createElement("li");
        var a = document.createElement("a");
        li.appendChild(a);
        a.appendChild(document.createTextNode(model.name));
        a.setAttribute("onclick", "selectModel('" + model.name + "', null )");
        a.setAttribute("href", "#");
        ul.appendChild(li);
    }
})();

//reload model diagram
function selectModel(name, paths) {
    for (id in diagramXMLSource) {
        if (diagramXMLSource[id].name == name) {
            if (paths == null)
                viewer.reload(diagramXMLSource[id]);
            else
                viewer.reloadMark(diagramXMLSource[id], paths);
        }
    }
}
viewer = initDiagram(diagramXMLSource[0], 0, null);
