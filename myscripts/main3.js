/* March 2017
 * Tommy Dang, Assistant professor, iDVL@TTU
 *
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, THE AUTHORS MAKE NO REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

var numTermsWordCloud = 7; // numTerms in each month
    
function setCut(cutvalue){
    var selectedvalue = cutvalue;
    if (selectedvalue === "optimized") {
        selectedCut = -100;
        cutOffvalue=get_bestCut(graphByMonths);
        createForceOptimized();
        updateHistogramOptimized();
    } else {
        selectedCut = +selectedvalue - 1;
        selectHistogram();
    }
     drawgraph2();
}

function setNodesBy(){
    selectedSetNodeBy = d3.select('#nodeDropdown').property('value');

    // Recompute the sub graphs
    computeMonthlyGraphs();


    if(selectedSetNodeBy==1){
       console.log(selectedSetNodeBy);
    }
    else if(selectedSetNodeBy==2){
        console.log(selectedSetNodeBy);
    }
    else if(selectedSetNodeBy==3){
        console.log(selectedSetNodeBy);
    }
    else if(selectedSetNodeBy==4){
        var cut_value = $('#nodeDropdown').val();
        //Check if cutoff is calculated, if yes then skip
        if(cutoff_Check.indexOf(+cut_value)===-1){
            graphInsertBetweeness(graphByMonths, +cut_value);
            cutoff_Check.push(+cut_value);
        }
    }
    drawgraph2();
}


function selectHistogram() {
    for (var c = 0; c < numCut; c++) {
        if (c == selectedCut) {
            svg.selectAll(".histogram" + c).style("fill-opacity", 0)
                .style("stroke-opacity", 1);
            for (var m = 1; m < numMonth; m++) {
                var nodes = [];
                if (graphByMonths[m][c] != undefined) {
                    nodes = graphByMonths[m][c].nodes;
                }
                var links = [];
                if (graphByMonths[m][c] != undefined) {
                    links = graphByMonths[m][c].links;
                }
                updateSubLayout(nodes, links, m);
            }
        }
        else {
            svg.selectAll(".histogram" + c).style("fill-opacity", 0.1)
                .style("stroke-opacity", 0.3);
        }
    }
}
function updateHistogramOptimized() {
    for (var c = 0; c < numCut; c++) {
        svg.selectAll(".histogram" + c)
            .style("fill-opacity", function (d,m) {
                if (c==cutOffvalue[m]-1){
                    return 1;
               }
               else{
                   return 0.1;
               }
            })
            .style("stroke-opacity", function (d,m) {
                if (c==cutOffvalue[m]-1){
                    return 1;
                }
                else{
                    return 0.3;
                }
            });
    }
}
function createForceOptimized() {
    console.log("createForceOptimized");
    for (var c = 0; c < numCut; c++) {
        for (var m = 1; m < numMonth; m++) {
            if (c==cutOffvalue[m]-1){
                var nodes = [];
                if (graphByMonths[m][c] != undefined) {
                    nodes = graphByMonths[m][c].nodes;
                }
                var links = [];
                if (graphByMonths[m][c] != undefined) {
                    links = graphByMonths[m][c].links;
                }
                updateSubLayout(nodes, links, m);
            }
        }
    }
}

function drawHistograms(yStartHistogram) {
    for (var cut = 0; cut < numCut; cut++) {
        svg.selectAll(".histogram" + cut).remove();
        var updateHistogram = svg.selectAll(".histogram" + cut)
            .data(graphByMonths);
        var enterHistogram = updateHistogram.enter();
        enterHistogram.append("rect")
            .attr("class", "histogram" + cut)
            .attr("id", cut)
            .style("stroke", "#000")
            .style("stroke-width", 0.4)
            .style("stroke-opacity", function () {
                return cut == selectedCut ? 1 : 0.25;
            })
            .style("fill", getColor3(cut))
            .style("fill-opacity", function () {
                return cut == selectedCut ? 1 : 0.12;
            })
            .attr("x", function (d, i) {
                var w = XGAP_ / (numCut + 4);
                if (lMonth - numLens <= i && i <= lMonth + numLens){
                     w = w * lensingMul / 2;
                     w = Math.min(w, 15);
                }
                return xStep + xScale(i) + cut * w - 2 * w;    // x position is at the arcs
            })
            .attr("y", function (d, i) {
                if (d == undefined || d[cut] == undefined)
                    return yStartHistogram;
                var hScale = d3.scale.linear()
                    .range([1, 50])
                    .domain([0, 1]);
                return yStartHistogram - hScale(d[cut].Qmodularity);
            })
            .attr("height", function (d, i) {
                if (d == undefined || d[cut] == undefined)
                    return 0;
                var hScale = d3.scale.linear()
                    .range([0, 50])
                    .domain([0, 1]);
                return hScale(d[cut].Qmodularity);
            })
            .attr("width", function (d, i) {
                var w = XGAP_ / (numCut + 4);
                if (lMonth - numLens <= i && i <= lMonth + numLens){
                    w = w * lensingMul / 2;
                    w = Math.min(w, 15);
                }
                    
                return w;
            });
    }
}


// This Texts is independent from the lower text with stream graphs
var tNodes;
function drawTextClouds(yTextClouds) {
    tNodes = [];
    for (var y = 1; y <= dataS.YearsData.length; y++) {
        var newCut = selectedCut;
        if (newCut<0){  // Best Q modularity selected
            newCut = cutOffvalue[m]-1;
        }

        var nodes = [];
        for (var c = 0; c < countryList.length; c++) {
            nodes.push(countryList[c]);
        }

        nodes.sort(function (a, b) {
            if (Math.abs(a[y].OutlyingDif) < Math.abs(b[y].OutlyingDif) )  
                return 1;
            else  
                return -1;
        });

        for (var i = 0; i < numTermsWordCloud; i++) {
            tNodes.push(nodes[i]);
        }
    }
    // ************ maxAbs ************ defined in main2.js 
    var maxAbs = Math.max(maxDifAboveForAll, Math.abs(maxDifBelowForAll));

    svg.selectAll(".textCloud3").remove();
    var yStep = 12;
    var updateText = svg.selectAll(".textCloud3")
        .data(tNodes);
    var enterText = updateText.enter();

 //   console.log("maxAbs="+maxAbs+ " numLens="+numLens+" lMonth="+lMonth);
               
    enterText.append("text")
        .attr("class", "textCloud3")
        .style("text-anchor", "middle")
        .attr("font-family", "sans-serif")
        .attr("font-size", function(d,i) {
            var s;
            var y = Math.floor(i/numTermsWordCloud);
            if (lMonth-numLens<=y && y<=lMonth+numLens){
                var sizeScale = d3.scale.linear()
                    .range([10, 17])
                    .domain([0, maxAbs]);
                s = sizeScale(Math.abs(d[y+1].OutlyingDif));
            }
            else{
                var sizeScale = d3.scale.linear()
                    .range([2, 11])
                .domain([0, maxAbs]);
                s = sizeScale(Math.abs(d[y+1].OutlyingDif));
            }
            if (isNaN(s)) // exception
                s=5;
            return s+"px";
        })
        .style("fill", function(d,i) {
            var y = Math.floor(i/numTermsWordCloud);
            return colorPurpleGreen(d[y+1].OutlyingDif);
        })
        .attr("x", function(d,i) {
            return xStep + xScale(Math.floor(i/numTermsWordCloud));    // x position is at the arcs
        })
        .attr("y", function (d,i) {
            return yTextClouds + (i%numTermsWordCloud) * yStep;     // Copy node y coordinate
        })
        .text(function(d) {
            if (lMonth-numLens<=d.m && d.m<=lMonth+numLens){
                return d[0].country.substring(0,18);//+" ("+d.count+")";
            }
            else{
                return d[0].country.substring(0,10);
            }
        });

}
