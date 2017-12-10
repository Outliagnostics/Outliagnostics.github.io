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
var cutOffvalue=[];


var snapshotScale = 0.265; // Snapshiot Size******************************************************
var maxNodesInSnapshot =30; // ******************************************************

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
var outlyingCut=0.021; // Threshold to decide to show Outlier/Inliers in the World Clound
var maxAbs; 
var yStart;
var yStartBoxplot;
var transitionTime =1000;
var countryList =[];
    
var colorPurpleGreen= d3.scale.linear()
    .domain([0,0,0])
    .range([colorBelow,"#666",colorAbove]);


var linkScale3 = function (count) {
    var scale = d3.scale.linear()
                    .range([0, 3])
                    .domain([0, maxRel]);
    var count2 = (count>maxRel) ? maxRel : count;  // for scaling, if count > maxRel the link will looks similar to 6                       
    return  scale(count2);   
}        
        

function computeMonthlyGraphs() {
    //console.log("computeMonthlyGraphs");
    allSVG = []; // all SVG in clusters.js
    for (var m = 0; m < numMonth; m++) {
        // Draw network snapshot
        updateSubLayout(m);
    }
    updateTimeLegend();
    oldLmonth =-100;  // This to make sure the histogram and text list is updated
    updateTimeBox();
    drawgraph2();
}


var yScaleS = d3.scale.linear()
        .range([0, 100])
        .domain([0, 1]);

var areaAbove = d3.svg.area()
    .interpolate("cardinal")
    .x(function (d,i) {
        if (i==0)
            return xStep-10;
        else
            return xStep + xScale(i-1);
    })
    .y0(function (d,i) {
        if (i==0 || i==dataS.YearsData.length+1)
            return d.y;
        else{
            return d.y-yScaleS(dataS.YearsData[i-1].Scagnostics0[selectedScag]);
        }
            
    })
    .y1(function (d,i) {
        if(i==0 || i==dataS.YearsData.length+1)
            return d.y;
        else{
            var scagLeaveOriginal = dataS.YearsData[i-1].Scagnostics0[selectedScag];
            if (d.OutlyingDif>0)
                return d.y-yScaleS(d.Outlying);
            else
                return d.y-yScaleS(scagLeaveOriginal);    
        }     
    });
var areaBelow = d3.svg.area()
    .interpolate("cardinal")
    .x(function (d,i) {
        if (i==0)
            return xStep -10;
        else
            return xStep + xScale(i-1);
    })
    .y0(function (d,i) {
         if(i==0 || i==dataS.YearsData.length+1)
            return d.y;
        else{
            return d.y-yScaleS(dataS.YearsData[i-1].Scagnostics0[selectedScag]);
        }    
    })
    .y1(function (d,i) {
        if(i==0 || i==dataS.YearsData.length+1)
            return d.y;
        else{
            var scagLeaveOriginal = dataS.YearsData[i-1].Scagnostics0[selectedScag];
            if (d.OutlyingDif<0)
                return d.y-yScaleS(d.Outlying);
            else
                return d.y-yScaleS(scagLeaveOriginal);     
        }    
    }); 

var areaTopAbove = d3.svg.area()
    .interpolate("cardinal")
    .x(function (d,i) {
        return xStep + xScale(i);
    })
    .y0(function (d,i) {
        return yStartBoxplot;
    })
    .y1(function (d,i) {
        return yStartBoxplot-hBoxplotScale(d.maxAbove);        
    });        
var areaTopBelow = d3.svg.area()
    .interpolate("cardinal")
    .x(function (d,i) {
        return xStep + xScale(i);
    })
    .y0(function (d,i) {
        return yStartBoxplot;
    })
    .y1(function (d,i) {
        return yStartBoxplot-hBoxplotScale(d.maxBelow);        
    });        


function drawgraph2() {
    var startMonth = lMonth > numLens ? lMonth - numLens : 0;
    if (lMonth<0) 
        startMonth=-100;   // Do not draw arc diagram if not lensed
    var endMonth = startMonth + numLens * 2 + 1;
    
    yStart = height + 275; // y starts drawing the stream graphs

    // Scagnostics stream graphs
    countryList =[];
    for (var c=0; c<dataS.Countries.length;c++){
        var country = dataS.Countries[c];
       
        // Add the first element
        var obj1 ={};
        obj1.country =country;  // Using for setting time series titles
        var obj2 ={};
        
        var thisCountryData= dataS.CountriesData[country];
        if (thisCountryData.length==dataS.YearsData.length){ // Avoid multiple push
            thisCountryData.unshift(obj1);
            thisCountryData.push(obj2);
        } 
        countryList.push(thisCountryData);
        thisCountryData.maxDifAbove= 0;
        thisCountryData.maxDifBelow= 0;
        for (var y=0; y<thisCountryData.length;y++){
            if (y==0 || y==thisCountryData.length-1){ // Dummy elements
                thisCountryData[y].OutlyingDif = 0;
            }
            else{
                var scagLeaveOriginal = dataS.YearsData[y-1].Scagnostics0[selectedScag];
                var dif = thisCountryData[y].Outlying - scagLeaveOriginal; // Different between leave 1 out and original scatterplot
                thisCountryData[y].OutlyingDif = dif;
                if (dif>0 && dif>thisCountryData.maxDifAbove){
                    thisCountryData.maxDifAbove=dif;
                    thisCountryData.maxYearAbove = y-1;
                }
                else if (dif<0 && dif<thisCountryData.maxDifBelow){
                    thisCountryData.maxDifBelow=dif;
                    thisCountryData.maxYearBelow = y-1;
                }    
            }
        }
        // Max of maxDifAbove and maxDifBelow ******
        if (thisCountryData.maxDifAbove>maxDifAboveForAll)
            maxDifAboveForAll = thisCountryData.maxDifAbove;
        if (thisCountryData.maxDifBelow<maxDifBelowForAll)
            maxDifBelowForAll = thisCountryData.maxDifBelow;
        thisCountryData.maxDifAbsolute = Math.max(thisCountryData.maxDifAbove, Math.abs(thisCountryData.maxDifBelow));
    }  
    colorPurpleGreen.domain([maxDifBelowForAll,0,maxDifAboveForAll]);
    maxAbs = Math.max(maxDifAboveForAll, Math.abs(maxDifBelowForAll));    
    

    countryList.sort(function (a, b) {
        if (a.maxDifAbsolute < b.maxDifAbsolute) { // order by month
            return 1;
        }
        else if (a.maxDifAbsolute > b.maxDifAbsolute) {
            return -1;
        }
        else {
            -1;
        }
    });

    var yTemp2 = yStart-60;
    for (var c=0; c<countryList.length;c++){
        for (var y=0; y<countryList[c].length;y++){
            countryList[c][y].y = yTemp2;
        }
         yTemp2+=10;
    } 
      
    svg.selectAll(".layerAbove").remove();
    svg.selectAll(".layerAbove")
        .data(countryList).enter()
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
        .data(countryList).enter()
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

    svg.selectAll(".countryText").remove();
    svg.selectAll(".countryText")
        .data(countryList).enter()
        .append("text")
        .attr("class", "countryText")
        .style("fill", function (d) {
            return "#000";
        })
        .style("text-anchor", "end")
        .style("text-shadow", "1px 1px 0 rgba(255, 255, 255, 0.99")
        .attr("x", function (d) {
            return xStep-11;    // x position is at the arcs
        })
        .attr("y", function (d, i) {
            return d[0].y;     // Copy node y coordinate
        })
        .attr("font-family", "sans-serif")
        .attr("font-size", "11px")
        .text(function (d) {
            return d[0].country;
        })
        .on("mouseover", function(d){
            var countryIndex = dataS.Countries.indexOf(d[0].country);
            brushingStreamText(countryIndex);
            // if autolensing is enable
            if (document.getElementById("checkbox1").checked && d.maxYearBelow!=undefined) {
                isLensing = true;
                lMonth = d.maxYearBelow;

                // Update layout
                updateTimeLegend();
                updateTimeBox();
            }  
        })
        .on("mouseout", function(d){
            hideTip(d);
        });   
     
    // Text of max different appearing on top of the stream graph    
    svg.selectAll(".maxAboveText").remove();
    svg.selectAll(".maxAboveText")
        .data(countryList).enter()
        .append("text")
        .attr("class", "maxAboveText")
        .style("fill", function (d) {
             if (d.maxYearAbove==undefined || d.maxYearAbove==0 || d[d.maxYearAbove]==undefined)
                return "#f00";
            else
                return colorPurpleGreen(d[d.maxYearAbove+1].OutlyingDif);
        })
        .style("text-anchor", "middle")
        .style("text-shadow", "0 0 5px #fff")
        .attr("x", function (d,i) {
            if (d.maxYearAbove==undefined)
                return 0;
            else
                return xStep + xScale(d.maxYearAbove);    // x position is at the arcs
        })
        .attr("y", function (d, i) {
            if (d.maxYearAbove==undefined || d.maxYearAbove==0 || d[d.maxYearAbove]==undefined)
                return d[0].y;
            else{
                return d[0].y-yScaleS(d[d.maxYearAbove+1].Outlying);     // Copy node y coordinate    
            }
        })
        .attr("font-family", "sans-serif")
        .attr("font-size", "1px")
        .text(function (d) {
            if (d.maxYearAbove==undefined || d.maxYearAbove==0 || d[d.maxYearAbove]==undefined)
                return "";
            else
                return d[d.maxYearAbove+1].OutlyingDif.toFixed(2);
        });       
    // Text of max Below appearing on top of the stream graph    
    svg.selectAll(".maxBelowText").remove();
    svg.selectAll(".maxBelowText")
        .data(countryList).enter()
        .append("text")
        .attr("class", "maxBelowText")
        .style("fill", function (d) {
            if (d.maxYearBelow==undefined || d.maxYearBelow==0 || d[d.maxYearBelow]==undefined)
                return "#f00";
            else
                return colorPurpleGreen(d[d.maxYearBelow+1].OutlyingDif);
        
        })
        .style("text-anchor", "middle")
        .style("text-shadow", "0 0 2px #fff")
        .attr("x", function (d) {
            //console.log(d.maxYearAbove);
            if (d.maxYearBelow==undefined)
                return 0;
            else
                return xStep + xScale(d.maxYearBelow);    // x position is at the arcs
        })
        .attr("y", function (d, i) {
            if (d.maxYearBelow==undefined || d.maxYearBelow==0 || d[d.maxYearBelow]==undefined)
                return d[0].y;
            else
                return d[0].y-yScaleS(d[d.maxYearBelow+1].Outlying); 
        })
        .attr("font-family", "sans-serif")
        .attr("font-size", "1px")
        .text(function (d) {
            if (d.maxYearBelow==undefined || d.maxYearBelow==0 || d[d.maxYearBelow]==undefined)
                return "";
            else
                return d[d.maxYearBelow+1].OutlyingDif.toFixed(2);
        });  
    
    //** TEXT CLOUD **********************************************************    
    yStartBoxplot = height + 90; // y starts drawing the stream graphs
    drawBoxplot(yStartBoxplot);   // in main3.js

    var yTextClouds = height + 160; // y starts drawing the stream graphs
    drawTextClouds(yTextClouds);    // in main3.js            
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
            if (lMonth - numLens <= i && i <= lMonth + numLens){
                 var w = XGAP_ / 2;
            }
            return xStep + xScale(i) - 0.5*w;    // x position is at the arcs
        })
        .attr("width", function (d, i) {
            var w = XGAP_ / 4;
            if (lMonth - numLens <= i && i <= lMonth + numLens){
                var w = XGAP_ / 2;
            }            
            return w;
        });    
    svg.selectAll(".boxplotRectBelow").transition().duration(transitionTime)
        .attr("x", function (d, i) {
            var w = XGAP_ / 4;
            if (lMonth - numLens <= i && i <= lMonth + numLens){
                 var w = XGAP_ / 2;
            }
            return xStep + xScale(i) - 0.5*w;    // x position is at the arcs
        })
        .attr("width", function (d, i) {
            var w = XGAP_ / 4;
            if (lMonth - numLens <= i && i <= lMonth + numLens){
                var w = XGAP_ / 2;
            }            
            return w;
        });
}

function updateTextClouds() {
    svg.selectAll(".textCloud3").transition().duration(transitionTime)
        .attr("x", function(d,i) {
            return xStep + xScale(Math.floor(i/numTermsWordCloud));    // x position is at the arcs
        })
        .attr("font-size", function(d,i) {
            var y = Math.floor(i/numTermsWordCloud);
            if (lMonth-numLens<=y && y<=lMonth+numLens){
                var sizeScale = d3.scale.linear()
                    .range([10, 17])
                    .domain([0, maxAbs]);
                if (Math.abs(d[y+1].OutlyingDif)<outlyingCut)
                     d.fontSize =0;
                else
                    d.fontSize = sizeScale(Math.abs(d[y+1].OutlyingDif));
            }
            else{
                var sizeScale = d3.scale.linear()
                    .range([6, 9])
                .domain([0, maxAbs]);
                if (Math.abs(d[y+1].OutlyingDif)<outlyingCut*2)
                     d.fontSize =0;
                else
                    d.fontSize = sizeScale(Math.abs(d[y+1].OutlyingDif));
            }
            return d.fontSize;
        })
        .text(function(d,i) {
            var y = Math.floor(i/numTermsWordCloud);
            if (lMonth-numLens-1<=y && y<=lMonth+numLens+1){
                return d[0].country.substring(0,16);//+" ("+d.count+")";
            }
            else{
                return d[0].country.substring(0,5);
            }
        });
}


function updateTimeSeries() {
    var brushingYear =  lMonth+1;
    var orderby = d3.select('#nodeDropdown').property('value');
    var interval = d3.select('#edgeWeightDropdown').property('value');              
    countryList.sort(function (a, b) {
        var maxOutlyingDif_A = 0;
        var maxOutlyingDif_B = 0;
        for (var i=brushingYear-numLens; i<=brushingYear+numLens;i++){
            if (0<i && i<=dataS.YearsData.length){ // within the lensing interval
                if (interval==1 && i!=brushingYear) {  //interval==1: Order by lensing year 
                    continue; // if users select brushing year to order 
                }   
                if (orderby==1){ // Order by outlier
                    if (a[i].OutlyingDif<0)
                        maxOutlyingDif_A = Math.max(maxOutlyingDif_A, Math.abs(a[i].OutlyingDif));
                    if (b[i].OutlyingDif<0)
                        maxOutlyingDif_B = Math.max(maxOutlyingDif_B, Math.abs(b[i].OutlyingDif)); 
                }
                else if (orderby==2){ // Order by inliers
                    if (a[i].OutlyingDif>0)
                        maxOutlyingDif_A = Math.max(maxOutlyingDif_A, a[i].OutlyingDif);
                    if (b[i].OutlyingDif>0)
                        maxOutlyingDif_B = Math.max(maxOutlyingDif_B, b[i].OutlyingDif); 
                }
                else if (orderby==3){ // Order by 
                    maxOutlyingDif_A = Math.max(maxOutlyingDif_A, Math.abs(a[i].OutlyingDif));
                    maxOutlyingDif_B = Math.max(maxOutlyingDif_B, Math.abs(b[i].OutlyingDif)); 
                }

            }  
        }
        if (maxOutlyingDif_A < maxOutlyingDif_B)
            return 1;
        else if (maxOutlyingDif_A > maxOutlyingDif_B)
            return -1;
        else{
            if (a.maxDifAbsolute < b.maxDifAbsolute)
                return 1;
            else if (a.maxDifAbsolute > b.maxDifAbsolute)
                return -1;           
            return -1;
        }
            
    });

    var yTemp2 =  yStart;
    for (var c=0; c<countryList.length;c++){
        for (var y=0; y<countryList[c].length;y++){
            countryList[c][y].y = yTemp2;
        }
         yTemp2+=10;
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
    .attr("x", function (d,i) {
        if (d.maxYearAbove==undefined)
            return 0;
        else
            return xStep + xScale(d.maxYearAbove);    // x position is at the arcs
    })
    .attr("y", function (d, i) {
        if (d.maxYearAbove==undefined || d.maxYearAbove==0 || d[d.maxYearAbove]==undefined)
            return d[0].y;
        else{
            return d[0].y-yScaleS(d[d.maxYearAbove+1].Outlying);     // Copy node y coordinate    
        }
    });
    svg.selectAll(".maxBelowText").transition().duration(transitionTime)
        .attr("x", function (d) {
            if (d.maxYearBelow==undefined)
                return 0;
            else
                return xStep + xScale(d.maxYearBelow);    // x position is at the arcs
        })
        .attr("y", function (d, i) {
        if (d.maxYearBelow==undefined || d.maxYearBelow==0 || d[d.maxYearBelow]==undefined)
            return d[0].y;
        else
            return d[0].y-yScaleS(d[d.maxYearBelow+1].Outlying); 
    });           
   
}
