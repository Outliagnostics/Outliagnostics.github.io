/* March 2017
 * Tommy Dang, Assistant professor, iDVL@TTU
 *
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, THE AUTHORS MAKE NO REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

var numTermsWordCloud = 6; // numTerms in each month
var boxplotHeight = 60; // numTerms in each month
var hBoxplotScale = d3.scale.linear()
                .range([1, boxplotHeight])
                .domain([0, 1]);
 var boxplotNodes;
               
    
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
            .style("fill", "#f00")
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

function drawBoxplot(yStartBoxplot) {
    boxplotNodes = [];
    for (var y = 1; y <= dataS.YearsData.length; y++) {
        var nodes = [];

        var obj ={};
        obj.sumAbove =0;
        obj.sumBelow =0;
        obj.countAbove =0;
        obj.countBelow =0;
        for (var c = 0; c < countryList.length; c++) {
            nodes.push(countryList[c]);
            if (countryList[c][y].OutlyingDif>0){
                obj.sumAbove+=countryList[c][y].OutlyingDif;
                obj.countAbove++;
            }      
            else if (countryList[c][y].OutlyingDif<0){
                obj.sumBelow+=countryList[c][y].OutlyingDif;
                obj.countBelow++;
            }         
        }
        nodes.sort(function (a, b) {
            if (a[y].OutlyingDif < b[y].OutlyingDif)  
                return 1;
            else  
                return -1;
        });
        if (obj.countAbove>0)
            obj.averageAbove = obj.sumAbove/obj.countAbove;
        else
            obj.averageAbove = 0;
        if (obj.countBelow>0)
            obj.averageBelow = obj.sumBelow/obj.countBelow;
        else
            obj.averageBelow = 0;

        obj.maxAbove = nodes[0][y].OutlyingDif;
        obj.maxBelow = nodes[nodes.length-1][y].OutlyingDif;
        obj.maxAboveCountry = nodes[0];
        obj.maxBelowCountry = nodes[nodes.length-1];
        boxplotNodes.push(obj);      
    }

   
    hBoxplotScale = d3.scale.linear()
                .range([1, boxplotHeight])
                .domain([0, maxAbs]);

    // Area on the top
    svg.selectAll(".layerTopAbove").remove();
    svg.append("path")
        .attr("class", "layerTopAbove")
        .style("stroke", "#000")
        .style("stroke-width", 0)
        .style("stroke-opacity", 0.5)
        .style("fill-opacity", 0.2)
        .style("fill", colorAbove)
        .attr("d", areaTopAbove(boxplotNodes));
    svg.selectAll(".layerTopBelow").remove();
    svg.append("path")
        .attr("class", "layerTopBelow")
        .style("stroke", "#000")
        .style("stroke-width", 0)
        .style("stroke-opacity", 0.5)
        .style("fill-opacity", 0.2)
        .style("fill", colorBelow)
        .attr("d", areaTopBelow(boxplotNodes));    
    

    svg.selectAll(".boxplotLine").remove();
    svg.selectAll(".boxplotLine")
        .data(boxplotNodes).enter()
        .append("line")
        .attr("class", "boxplotLine")
        .style("stroke", "#000")
        .style("stroke-width", 1)
        .style("stroke-opacity", 0.75)
        .attr("x1", function (d, i) {
            return xStep + xScale(i);    // x position is at the arcs
        })
        .attr("y1", function (d, i) {
            return yStartBoxplot-hBoxplotScale(d.maxBelow);
        })
        .attr("x2", function (d, i) {
            return xStep + xScale(i);    // x position is at the arcs
        })
        .attr("y2", function (d, i) {
            return yStartBoxplot-hBoxplotScale(d.maxAbove);
        });    
    

    svg.selectAll(".boxplotLineAbove").remove();
    svg.selectAll(".boxplotLineAbove")
        .data(boxplotNodes).enter()
        .append("line")
        .attr("class", "boxplotLineAbove")
        .style("stroke", "#000")
        .style("stroke-width", 1)
        .style("stroke-opacity", 0.75)
        .attr("x1", function (d, i) {
            return xStep + (xScale(i) - (XGAP_ / 8));    // x position is at the arcs
        })
        .attr("y1", function (d, i) {
            return yStartBoxplot-hBoxplotScale(d.maxAbove);
        })
        .attr("x2", function (d, i) {
            return xStep + (xScale(i) + (XGAP_ / 8));    // x position is at the arcs
        })
        .attr("y2", function (d, i) {
            return yStartBoxplot-hBoxplotScale(d.maxAbove);
        });
    svg.selectAll(".boxplotLineBelow").remove();
    svg.selectAll(".boxplotLineBelow")
        .data(boxplotNodes).enter()
        .append("line")
        .attr("class", "boxplotLineBelow")
        .style("stroke", "#000")
        .style("stroke-width", 1)
        .style("stroke-opacity", 0.75)
        .attr("x1", function (d, i) {
            return xStep + (xScale(i) - (XGAP_ / 8));    // x position is at the arcs
        })
        .attr("y1", function (d, i) {
            return yStartBoxplot-hBoxplotScale(d.maxBelow);
        })
        .attr("x2", function (d, i) {
            return xStep + (xScale(i) + (XGAP_ / 8));    // x position is at the arcs
        })
        .attr("y2", function (d, i) {
            return yStartBoxplot-hBoxplotScale(d.maxBelow);
        });    
    


    svg.selectAll(".boxplotRectAbove").remove();
    svg.selectAll(".boxplotRectAbove")
        .data(boxplotNodes).enter()
        .append("rect")
        .attr("class", "boxplotRectAbove")
        .style("stroke", "#000")
        .style("stroke-width", 1)
        .style("stroke-opacity", 0.5)
        .style("fill", colorAbove)
        .style("fill-opacity", 1)
        .attr("x", function (d, i) {
            return xStep + (xScale(i) - (XGAP_ / 8));    // x position is at the arcs
        })
        .attr("y", function (d, i) {
            return yStartBoxplot-hBoxplotScale(d.averageAbove);
        })
        .attr("height", function (d) {
            return hBoxplotScale(d.averageAbove);
        })
        .attr("width", XGAP_ / 4);
    svg.selectAll(".boxplotRectBelow").remove();
    svg.selectAll(".boxplotRectBelow")
        .data(boxplotNodes).enter()
        .append("rect")
        .attr("class", "boxplotRectBelow")
        .style("stroke", "#000")
        .style("stroke-width", 1)
        .style("stroke-opacity", 0.5)
        .style("fill", colorBelow)
        .style("fill-opacity", 1)
        .attr("x", function (d, i) {
            return xStep + (xScale(i) - (XGAP_ / 8));    // x position is at the arcs
        })
        .attr("y", yStartBoxplot)
        .attr("height", function (d) {
            return hBoxplotScale(Math.abs(d.averageBelow));
        })
        .attr("width", XGAP_ / 4);  


}

// This Texts is independent from the lower text with stream graphs
var tNodes;
function drawTextClouds(yTextClouds) {
    tNodes = [];
    for (var y = 1; y <= dataS.YearsData.length; y++) {
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
