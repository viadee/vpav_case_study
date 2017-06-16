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
        var overlayHtml = $('<div class="diagram-zahl">' + issues[id].anz + '</div>');
        // add DialofMessage
        function clickOverlay(event) {
            var eId = issues[event.data.id].i.elementId;
            for (y in issues) {
                if (issues[y].i.elementId == eId) {
                    var issue = issues[y].i;
                    var dl = document.getElementById("dia");
                    
                    var hClass = document.createElement("h3");
                    var pRule = document.createElement("p");
                    var pMessage = document.createElement("p");

                    pMessage.setAttribute("id", "message");

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
        overlayHtml.click({ id: id }, clickOverlay);
        // attach the overlayHtml to a node
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

function markNodesIssue(bpmnViewer, paths, bpmnFile) {
    var canvas = bpmnViewer.get('canvas');

    for (id in paths[0]) {
        console.log(paths[0][id].elementId);
        //canvas.addMarker(paths[0][id].elementId, 'VersioningChecker');
    }


    /*
        for (id in elementsToMark)
            if (elementsToMark[id].bpmnFile == ("src\\main\\resources\\" + bpmnFile))
                for (i in paths)
                    if (elementsToMark[id].elementId == paths[i].elementId) {
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
                        } else {
                            canvas.addMarker(elementsToMark[id].elementId, 'new');
                        }
                    }
                    */
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

            myCell = document.createElement("td");
            myText = document.createTextNode(issue.ruleName);
            myRow.setAttribute("id", issue.ruleName) // mark hole row

            //create link to mark the issue path
            var a = document.createElement("a");
            a.appendChild(myText);
            a.setAttribute("onclick", "selectModel('" + bpmnFile + "'," + issue.paths + ")");
            a.setAttribute("href", "#");

            myCell.appendChild(a);
            myRow.appendChild(myCell);

            myCell = document.createElement("td");
            myText = document.createTextNode(issue.elementId);
            myCell.appendChild(myText);
            myRow.appendChild(myCell);

            myCell = document.createElement("td");
            myText = document.createTextNode(issue.elementName);
            myCell.appendChild(myText);
            myRow.appendChild(myCell);

            myCell = document.createElement("td");
            myText = document.createTextNode(issue.classification);
            myCell.appendChild(myText);
            myRow.appendChild(myCell);

            myCell = document.createElement("td");
            myText = document.createTextNode(issue.message);
            myCell.appendChild(myText);
            myRow.appendChild(myCell);

            myCell = document.createElement("td");
            var path_text = "";
            for (id in issue.paths[0]) {
                if (issue.paths[0][id].elementName == null)
                    path_text += issue.paths[0][id].elementId + " -> ";
                else
                    path_text += issue.paths[0][id].elementName + " -> ";
            }
            myText = document.createTextNode(path_text);
            myCell.appendChild(myText);
            myRow.appendChild(myCell);

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
            if (r == 0) {
                markNodes(bpmnViewer, elementsToMark, diagramXML.name);
                addCountOverlay(bpmnViewer, elementsToMark, diagramXML.name);
            } else {
                markNodesIssue(bpmnViewer, paths, diagramXML.name);
            }
            createTable(elementsToMark, diagramXML.name);
        });
    };

    bpmnViewer.xml = diagramXML.xml;

    bpmnViewer.reload = function (model) {
        $("#canvas").empty();
        deleteTable();
        initDiagram(model, 0, null);
    };

    bpmnViewer.reloadMark = function (model, paths) {
        $("#canvas").empty();
        deleteTable();
        initDiagram(model, 1, paths);
    };

    // import xml
    importXML(diagramXML.xml);

    return bpmnViewer;
};

//set Filename as Header
function setUeberschrift(name) {
    $("#modell").html("Consistency check: " + name);
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
