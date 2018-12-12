/* 2017
 * Tommy Dang, Assistant professor, iDVL@TTU
 *
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, THE AUTHORS MAKE NO REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

var topNumber = 100;
var top200terms = {}; // top terms from input data
var top100termsArray = []; // for user selection
var termList = {}; // List of term to feed to TimeArcs in main.js
var graphByMonths = [];
var lNodes, lLinks;  // Nodes in the lensing month
var numCut = 5;
var cutOffvalue = [];


var snapshotScale = 0.265; // Snapshiot Size******************************************************
var maxNodesInSnapshot = 30; // ******************************************************

var nodeRadiusRange = [0.1, 0.8];
var linkscaleForSnapshot = 0.15;

var maxHeightOfStreamGraph = 9;
var yStepOfStreamGraph = 9;
var maxRel = 15;   // for scaling, if count > 6 the link will looks similar to 6

// Colors
var colorAbove = "#0a0";
var colorBelow = "#b06";
var maxDifAboveForAll = 0;
var maxDifBelowForAll = 0;
var maxDifBothForAll = 0;
// var outlyingCut=0.021; // Threshold to decide to show Outlier/Inliers in the World Clound
var outlyingCut = 0.008; // Threshold to decide to show Outlier/Inliers in the World Clound
var maxAbs;
var yStart;
var yStartBoxplot;
var yTextClouds;
var boxHeight = 75;
var textCloudHeight = 75;
var transitionTime = 1000;
var countryList = [];
var countryListFiltered = [];
// var countryListYDistance = 15;
var countryListYDistance = 22;//we changed this to increase the country list distance.

var colorPurpleGreen = d3.scale.linear()
    .domain([0, 0, 0])
    .range([colorBelow, "#666", colorAbove]);


var linkScale3 = function (count) {
    var scale = d3.scale.linear()
        .range([0, 3])
        .domain([0, maxRel]);
    var count2 = (count > maxRel) ? maxRel : count;  // for scaling, if count > maxRel the link will looks similar to 6
    return scale(count2);
}


function computeMonthlyGraphs() {
    //console.log("computeMonthlyGraphs");
    allSVG = []; // all SVG in clusters.js
    for (var m = 0; m < numMonth; m++) {
        // Draw network snapshot
        updateSubLayout(m);
    }
    updateTimeLegend();
    oldLmonth = -100;  // This to make sure the histogram and text list is updated
    updateTimeBox();
    drawgraph2();
}


var yScaleS = d3.scale.linear()
    .range([0, 80])
    .domain([0, 1]);


var areaAbove = d3.svg.area()
    .interpolate(interpolation)
    .x(function (d, i) {
        if (i == 0)
            return xStep - 10;
        else
            return xStep + xScale(i - 1);
    })
    .y0(function (d, i) {
        if (i == 0 || i == dataS.YearsData.length + 1) {
            return d.y;
        }
        else {
            return d.y - yScaleS(dataS.YearsData[i - 1].Scagnostics0[selectedScag]);
        }

    })
    .y1(function (d, i) {
        if (i == 0 || i == dataS.YearsData.length + 1)
            return d.y;
        else {
            var scagLeaveOriginal = dataS.YearsData[i - 1].Scagnostics0[selectedScag];
            if (d.OutlyingDif > 0)
                return d.y - yScaleS(scagLeaveOriginal) - yScaleS(d.OutlyingDif);
            else
                return d.y - yScaleS(scagLeaveOriginal);
        }
    });
var outlyingBaseLine = d3.svg.line()
    .interpolate(interpolation)
    .x(function (d, i) {
        if (i == 0)
            return xStep - 10;
        else
            return xStep + xScale(i - 1);
    })
    .y(function (d, i) {
        if (i == 0 || i == dataS.YearsData.length + 1)
            return d.y;
        else {
            return d.y - yScaleS(dataS.YearsData[i - 1].Scagnostics0[selectedScag]);
        }
    });
var countryBaseLine = d3.svg.line()
    .x(function (d, i) {
        if (i == 0)
            return xStep - 10;
        else
            return xStep + xScale(i - 1);
    })
    .y(function (d, i) {
        if (i == 0 || i == dataS.YearsData.length + 1)
            return d.y;
        else {
            return d.y;
        }
    });
var areaBelow = d3.svg.area()
    .interpolate(interpolation)
    .x(function (d, i) {
        if (i == 0)
            return xStep - 10;
        else
            return xStep + xScale(i - 1);
    })
    .y0(function (d, i) {
        if (i == 0 || i == dataS.YearsData.length + 1)
            return d.y;
        else {
            return d.y - yScaleS(dataS.YearsData[i - 1].Scagnostics0[selectedScag]);
        }
    })
    .y1(function (d, i) {
        if (i == 0 || i == dataS.YearsData.length + 1)
            return d.y;
        else {
            var scagLeaveOriginal = dataS.YearsData[i - 1].Scagnostics0[selectedScag];
            if (d.OutlyingDif < 0)
                return d.y - yScaleS(scagLeaveOriginal) + yScaleS(-d.OutlyingDif);
            else
                return d.y - yScaleS(scagLeaveOriginal);
        }
    });

function drawgraph2() {

    //need to reset these values every time we calculate a new data set
    var maxDifAboveForAll = 0;
    var maxDifBelowForAll = 0;

    var startMonth = lMonth > numLens ? lMonth - numLens : 0;
    if (lMonth < 0)
        startMonth = -100;   // Do not draw arc diagram if not lensed
    var endMonth = startMonth + numLens * 2 + 1;

    // yStart = height + 275 + 30; // y starts drawing the stream graphs, added 50 to bring these downs

    // Scagnostics stream graphs
    countryList = [];
    for (var c = 0; c < dataS.Countries.length; c++) {
        var country = dataS.Countries[c];

        // Add the first element
        var obj1 = {};
        obj1.country = country;  // Using for setting time series titles
        var obj2 = {};

        var thisCountryData = dataS.CountriesData[country];
        if (thisCountryData.length == dataS.YearsData.length) { // Avoid multiple push
            thisCountryData.unshift(obj1);
            thisCountryData.push(obj2);
        }
        thisCountryData.maxDifAbove = 0;
        thisCountryData.maxDifBelow = 0;
        for (var y = 0; y < thisCountryData.length; y++) {
            if (y == 0 || y == thisCountryData.length - 1) { // Dummy elements
                thisCountryData[y].OutlyingDif = 0;
            } else {
                var scagLeaveOriginal = dataS.YearsData[y - 1].Scagnostics0[selectedScag];
                var dif = thisCountryData[y].Outlying - scagLeaveOriginal; // Different between leave 1 out and original scatterplot
                thisCountryData[y].OutlyingDif = dif;
                if (dif > 0 && dif > thisCountryData.maxDifAbove) {
                    thisCountryData.maxDifAbove = dif;
                    thisCountryData.maxYearAbove = y - 1;
                }
                else if (dif < 0 && dif < thisCountryData.maxDifBelow) {
                    thisCountryData.maxDifBelow = dif;
                    thisCountryData.maxYearBelow = y - 1;
                }
            }
        }
        thisCountryData.maxDifAbsolute = Math.max(thisCountryData.maxDifAbove, Math.abs(thisCountryData.maxDifBelow));
        // Max of maxDifAbove and maxDifBelow ******
        if (thisCountryData.maxDifAbove > maxDifAboveForAll)
            maxDifAboveForAll = thisCountryData.maxDifAbove;
        if (thisCountryData.maxDifBelow < maxDifBelowForAll)
            maxDifBelowForAll = thisCountryData.maxDifBelow;

        countryList.push(thisCountryData);
    }
    maxAbs = Math.max(maxDifAboveForAll, Math.abs(maxDifBelowForAll));
    //Filtered country list (with outlying score difference > some point).
    countryListFiltered = countryList.filter(c => Math.abs(c.maxDifAbsolute) > 0.01);

    colorPurpleGreen.domain([maxDifBelowForAll, 0, maxDifAboveForAll]);


    countryListFiltered.sort(function (a, b) {
        return a.maxDifBelow - b.maxDifBelow;
    });

    //** TEXT CLOUD **********************************************************
    yTextClouds = height + boxHeight; // 75 is the height of the text cloud section.
    drawTextClouds(yTextClouds);    // in main3.js
    //** BOX PLOT **********************************************************
    drawBoxplot();   // in main3.js
    //** COUNTRY PROFILE **********************************************************
    drawCountryProfiles();
}

function drawCountryProfiles() {

    // <editor-fold desc="TODO: This is enabled for the explanation of the profile only.">
    // countryListYDistance = 80;
    // countryListFiltered = countryListFiltered.filter(c => c[0].country === "Lesotho" || c[0].country === "Zimbabwe"  || c[0].country === "Swaziland");
    // </editor-fold>

    yStart = yStartBoxplot + hBoxplotScale(d3.max(boxplotNodes.map(obj => -obj.maxBelow))) + yScaleS(d3.max(countryListFiltered[0].map(d => d.Outlying + Math.abs(d.OutlyingDif)))) + 10;//10 is for the margin

    var yTemp2 = yStart;
    for (var c = 0; c < countryListFiltered.length; c++) {
        for (var y = 0; y < countryListFiltered[c].length; y++) {
            countryListFiltered[c][y].y = yTemp2;
        }
        yTemp2 += countryListYDistance;
    }

    //<editor-fold desc="TODO: This is enabled for the grid of the item profile only.">
    //Vung's code to Draw profile ticks
    // countryListFiltered.forEach(c=>{
    //         drawProfileGrid(c[0].y);
    // });
    // function drawProfileGrid(yPosition) {
    //     let boxPlotGridData = [];
    //     boxPlotGridData.push({"value": 0});
    //     boxPlotGridData.push({"value": 0.2});
    //     boxPlotGridData.push({"value": 0.4});
    //     boxPlotGridData.push({"value": 0.6});
    //
    //
    //     let profileGrid = svg.append("g").attr("transform", `translate(${0}, ${yPosition})`);
    //     let enter = profileGrid.selectAll(".boxPlotGridLine").data(boxPlotGridData).enter();
    //
    //     function yBoxPlotGrid(d) {
    //         return -yScaleS(d.value);
    //     }
    //
    //     enter.append("line").attr("x1", xStep - 10).attr("y1", yBoxPlotGrid).attr("x2", +svg.attr("width")).attr("y2", yBoxPlotGrid)
    //         .attr("class", "profileGrid")
    //         .style("stroke", "#000")
    //         .style("stroke-opacity", 1)
    //         .style("stroke-width", 0.3)
    //         .style("stroke-dasharray", "3, 1");
    //
    //
    //     enter.append("text").attr("x", xStep - 10 - 5).attr("y", yBoxPlotGrid)
    //         .attr("alignment-baseline", "middle")
    //         .attr("class", "boxPlotTickLabel")
    //         .attr("font-family", "san-serif")
    //         .attr("font-size", "11px")
    //         .text(d => d.value);
    // }

    // </editor-fold>

    svg.selectAll(".layerAbove").remove();
    svg.selectAll(".layerAbove")
        .data(countryListFiltered).enter()
        .append("path")
        .attr("class", "layerAbove")
        .style("stroke", "#000")
        .style("stroke-width", 0.2)
        .style("stroke-opacity", 0.5)
        .style("fill-opacity", 1)
        .style("fill", colorAbove)
        .attr("d", function (d) {
            return areaAbove(d);
        });
    svg.selectAll(".layerBelow").remove();
    svg.selectAll(".layerBelow")
        .data(countryListFiltered).enter()
        .append("path")
        .attr("class", "layerBelow")
        .style("stroke", "#000")
        .style("stroke-width", 0.2)
        .style("stroke-opacity", 0.5)
        .style("fill-opacity", 1)
        .style("fill", colorBelow)
        .attr("d", function (d) {
            return areaBelow(d);
        });
    //<editor-fold desc="TODO: These baselines are enabled to explain the profile only">
    // svg.selectAll(".outlyingBaseLine").remove();
    // svg.selectAll(".outlyingBaseLine")
    //     .data(countryListFiltered).enter()
    //     .append("path")
    //     .style("stroke", "black")
    //     .style("stroke-width", 1)
    //     .style("stroke-opacity", 1)
    //     .style("fill", "none")
    //     .attr("stroke-dasharray", "2, 2")
    //     .attr("d", outlyingBaseLine);
    // svg.selectAll(".countryBaseLine").remove()
    // svg.selectAll(".countryBaseLine")
    //     .data(countryListFiltered).enter()
    //     .append("path")
    //     .style("stroke", "#000")
    //     .style("stroke-width", 1)
    //     .style("stroke-opacity", 1)
    //     .style("fill", "none")
    //     // .attr("stroke-dasharray", "3, 3")
    //     .attr("d", countryBaseLine);
    //</editor-fold>

    svg.selectAll(".countryText").remove();
    svg.selectAll(".countryText")
        .data(countryListFiltered).enter()
        .append("text")
        .attr("class", "countryText")
        .style("fill", function (d) {
            return "#000";
        })
        .style("text-anchor", "end")
        .style("text-shadow", "1px 1px 0 rgba(255, 255, 255, 0.99")
        .attr("x", function (d) {
            return xStep - 11;    // x position is at the arcs
        })
        .attr("y", function (d, i) {
            return d[0].y;     // Copy node y coordinate
        })
        .attr("font-family", "sans-serif")
        .attr("font-size", "14px")
        .text(function (d) {
            return d[0].country;
        })
        .on("mouseover", function (d) {
            var countryIndex = dataS.Countries.indexOf(d[0].country);
            brushingStreamText(countryIndex);
            // if autolensing is enable
            if (document.getElementById("checkbox1").checked && d.maxYearBelow != undefined) {
                isLensing = true;
                lMonth = d.maxYearBelow;

                // Update layout
                updateTimeLegend();
                updateTimeBox();
            }
        })
        .on("mouseout", function (d) {
            hideTip(d);
        });

    // Text of max different appearing on top of the stream graph
    svg.selectAll(".maxAboveText").remove();
    svg.selectAll(".maxAboveText")
        .data(countryListFiltered).enter()
        .append("text")
        .attr("class", "maxAboveText")
        .style("fill", function (d) {
            if (d.maxYearAbove == undefined || d.maxYearAbove == 0 || d[d.maxYearAbove] == undefined)
                return "#f00";
            else
                return colorPurpleGreen(d[d.maxYearAbove + 1].OutlyingDif);
        })
        .style("text-anchor", "middle")
        .style("text-shadow", "0 0 5px #fff")
        .attr("x", function (d, i) {
            if (d.maxYearAbove == undefined)
                return 0;
            else
                return xStep + xScale(d.maxYearAbove);    // x position is at the arcs
        })
        .attr("y", function (d, i) {
            if (d.maxYearAbove == undefined || d.maxYearAbove == 0 || d[d.maxYearAbove] == undefined)
                return d[0].y;
            else {
                return d[0].y - yScaleS(d[d.maxYearAbove + 1].Outlying);     // Copy node y coordinate
            }
        })
        .attr("font-family", "sans-serif")
        .attr("font-size", "1px")
        .text(function (d) {
            if (d.maxYearAbove == undefined || d.maxYearAbove == 0 || d[d.maxYearAbove] == undefined)
                return "";
            else
                return d[d.maxYearAbove + 1].OutlyingDif.toFixed(2);
        });
    // Text of max Below appearing on top of the stream graph
    svg.selectAll(".maxBelowText").remove();
    svg.selectAll(".maxBelowText")
        .data(countryListFiltered).enter()
        .append("text")
        .attr("class", "maxBelowText")
        .style("fill", function (d) {
            if (d.maxYearBelow == undefined || d.maxYearBelow == 0 || d[d.maxYearBelow] == undefined)
                return "#f00";
            else
                return colorPurpleGreen(d[d.maxYearBelow + 1].OutlyingDif);

        })
        .style("text-anchor", "middle")
        .style("text-shadow", "0 0 2px #fff")
        .attr("x", function (d) {
            //console.log(d.maxYearAbove);
            if (d.maxYearBelow == undefined)
                return 0;
            else
                return xStep + xScale(d.maxYearBelow);    // x position is at the arcs
        })
        .attr("y", function (d, i) {
            if (d.maxYearBelow == undefined || d.maxYearBelow == 0 || d[d.maxYearBelow] == undefined)
                return d[0].y;
            else
                return d[0].y - yScaleS(d[d.maxYearBelow + 1].Outlying);
        })
        .attr("font-family", "sans-serif")
        .attr("font-size", "1px")
        .text(function (d) {
            if (d.maxYearBelow == undefined || d.maxYearBelow == 0 || d[d.maxYearBelow] == undefined)
                return "";
            else
                return d[d.maxYearBelow + 1].OutlyingDif.toFixed(2);
        });
}

function updategraph2() {
    updateBoxplots();
    updateTimeSeries();
    updateTextClouds();
}

function updateBoxplots() {
    svg.selectAll(".boxplotLine").transition().duration(transitionTime)
        .attr("x1", function (d, i) {
            return xStep + xScale(i);    // x position is at the arcs
        })
        .attr("x2", function (d, i) {
            return xStep + xScale(i);    // x position is at the arcs
        });

    svg.selectAll(".boxplotLineAbove").transition().duration(transitionTime)
        .attr("x1", function (d, i) {
            return xStep + (xScale(i) - (XGAP_ / 8));    // x position is at the arcs
        })
        .attr("x2", function (d, i) {
            return xStep + (xScale(i) + (XGAP_ / 8));    // x position is at the arcs
        });
    svg.selectAll(".boxplotLineBelow").transition().duration(transitionTime)
        .attr("x1", function (d, i) {
            return xStep + (xScale(i) - (XGAP_ / 8));    // x position is at the arcs
        })
        .attr("x2", function (d, i) {
            return xStep + (xScale(i) + (XGAP_ / 8));    // x position is at the arcs
        });

    svg.selectAll(".boxplotRectAbove").transition().duration(transitionTime)
        .attr("x", function (d, i) {
            var w = XGAP_ / 4;
            if (lMonth - numLens <= i && i <= lMonth + numLens) {
                var w = XGAP_ / 2;
            }
            return xStep + xScale(i) - 0.5 * w;    // x position is at the arcs
        })
        .attr("width", function (d, i) {
            var w = XGAP_ / 4;
            if (lMonth - numLens <= i && i <= lMonth + numLens) {
                var w = XGAP_ / 2;
            }
            return w;
        });
    svg.selectAll(".boxplotRectBelow").transition().duration(transitionTime)
        .attr("x", function (d, i) {
            var w = XGAP_ / 4;
            if (lMonth - numLens <= i && i <= lMonth + numLens) {
                var w = XGAP_ / 2;
            }
            return xStep + xScale(i) - 0.5 * w;    // x position is at the arcs
        })
        .attr("width", function (d, i) {
            var w = XGAP_ / 4;
            if (lMonth - numLens <= i && i <= lMonth + numLens) {
                var w = XGAP_ / 2;
            }
            return w;
        });
}

function updateTextClouds() {
    svg.selectAll(".textCloud3").transition().duration(transitionTime)
        .attr("x", function (d, i) {
            return xStep + xScale(Math.floor(i / numTermsWordCloud));    // x position is at the arcs
        })
        .attr("font-size", function (d, i) {
            var y = Math.floor(i / numTermsWordCloud);
            if (lMonth - numLens <= y && y <= lMonth + numLens) {
                var sizeScale = d3.scale.linear()
                    .range(lensedTextCloudRange)
                    .domain([0, maxAbs]);
                if (Math.abs(d[y + 1].OutlyingDif) < outlyingCut)
                    d.fontSize = 0;
                else
                    d.fontSize = sizeScale(Math.abs(d[y + 1].OutlyingDif));
            }
            else {
                var sizeScale = d3.scale.linear()
                    .range(textCloudRange)
                    .domain([0, maxAbs]);
                if (Math.abs(d[y + 1].OutlyingDif) < outlyingCut * 2)
                    d.fontSize = 0;
                else
                    d.fontSize = sizeScale(Math.abs(d[y + 1].OutlyingDif));
            }
            return d.fontSize;
        })
        .text(function (d, i) {
            var y = Math.floor(i / numTermsWordCloud);
            if (lMonth - numLens - 1 <= y && y <= lMonth + numLens + 1) {
                return d[0].country.substring(0, lensedCloudTextLength);//+" ("+d.count+")";
            }
            else {
                return d[0].country.substring(0, cloudTextLength);
            }
        });
}


function updateTimeSeries() {
    var brushingYear = lMonth + 1;
    var orderby = d3.select('#nodeDropdown').property('value');
    var interval = d3.select('#edgeWeightDropdown').property('value');
    countryListFiltered.sort(function (a, b) {
        var maxOutlyingDif_A = 0;
        var maxOutlyingDif_B = 0;
        for (var i = brushingYear - numLens; i <= brushingYear + numLens; i++) {
            if (0 < i && i <= dataS.YearsData.length) { // within the lensing interval
                if (interval == 1 && i != brushingYear) {  //interval==1: Order by lensing year
                    continue; // if users select brushing year to order 
                }
                if (orderby == 1) { // Order by outlier
                    if (a[i].OutlyingDif < 0)
                        maxOutlyingDif_A = Math.max(maxOutlyingDif_A, Math.abs(a[i].OutlyingDif));
                    if (b[i].OutlyingDif < 0)
                        maxOutlyingDif_B = Math.max(maxOutlyingDif_B, Math.abs(b[i].OutlyingDif));
                }
                else if (orderby == 2) { // Order by inliers
                    if (a[i].OutlyingDif > 0)
                        maxOutlyingDif_A = Math.max(maxOutlyingDif_A, a[i].OutlyingDif);
                    if (b[i].OutlyingDif > 0)
                        maxOutlyingDif_B = Math.max(maxOutlyingDif_B, b[i].OutlyingDif);
                }
                else if (orderby == 3) { // Order by
                    maxOutlyingDif_A = Math.max(maxOutlyingDif_A, Math.abs(a[i].OutlyingDif));
                    maxOutlyingDif_B = Math.max(maxOutlyingDif_B, Math.abs(b[i].OutlyingDif));
                }

            }
        }
        if (maxOutlyingDif_A < maxOutlyingDif_B)
            return 1;
        else if (maxOutlyingDif_A > maxOutlyingDif_B)
            return -1;
        else {
            if (a.maxDifAbsolute < b.maxDifAbsolute)
                return 1;
            else if (a.maxDifAbsolute > b.maxDifAbsolute)
                return -1;
            return -1;
        }

    });

    var yTemp2 = yStart;
    for (var c = 0; c < countryListFiltered.length; c++) {
        for (var y = 0; y < countryListFiltered[c].length; y++) {
            countryListFiltered[c][y].y = yTemp2;
        }
        yTemp2 += countryListYDistance;
    }

    svg.selectAll(".countryText").transition().duration(transitionTime)
        .attr("y", function (d, i) {
            return d[0].y;     // Copy node y coordinate
        })
    svg.selectAll(".layerBelow").transition().duration(transitionTime)
        .attr("d", areaBelow);
    svg.selectAll(".layerAbove").transition().duration(transitionTime)
        .attr("d", areaAbove);

    svg.selectAll(".layerTopAbove").transition().duration(transitionTime)
        .attr("d", areaTopAbove(boxplotNodes));
    svg.selectAll(".layerTopBelow").transition().duration(transitionTime)
        .attr("d", areaTopBelow(boxplotNodes));


    svg.selectAll(".maxAboveText").transition().duration(transitionTime)
        .attr("x", function (d, i) {
            if (d.maxYearAbove == undefined)
                return 0;
            else
                return xStep + xScale(d.maxYearAbove);    // x position is at the arcs
        })
        .attr("y", function (d, i) {
            if (d.maxYearAbove == undefined || d.maxYearAbove == 0 || d[d.maxYearAbove] == undefined)
                return d[0].y;
            else {
                return d[0].y - yScaleS(d[d.maxYearAbove + 1].Outlying);     // Copy node y coordinate
            }
        });
    svg.selectAll(".maxBelowText").transition().duration(transitionTime)
        .attr("x", function (d) {
            if (d.maxYearBelow == undefined)
                return 0;
            else
                return xStep + xScale(d.maxYearBelow);    // x position is at the arcs
        })
        .attr("y", function (d, i) {
            if (d.maxYearBelow == undefined || d.maxYearBelow == 0 || d[d.maxYearBelow] == undefined)
                return d[0].y;
            else
                return d[0].y - yScaleS(d[d.maxYearBelow + 1].Outlying);
        });

}
