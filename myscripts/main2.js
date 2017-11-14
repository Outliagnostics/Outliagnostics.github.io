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


var snapshotScale = 0.20; // Snapshiot Size******************************************************
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
var maxAbs; 
var yStart;
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
        var arr = [];
        for (var att in top200terms) {
           // var att = termArray[i].term;
            if (terms[att][m]) {  
                var obj = new Object();
                var previous = 0;
                if (terms[att][m - 1])
                    previous = terms[att][m - 1];
                var net = (terms[att][m] + 1) / (previous + 1);
                obj.term = att;
                obj.net = net;
                obj.count = terms[att][m];
                obj.category = top200terms[att].category;
                obj.m = m;
                arr.push(obj);
            }
        }

        arr.sort(function (a, b) {
            var var1 = a.net * 1000 + a.count;
            var var2 = b.net * 1000 + b.count;
            if (selectedSetNodeBy==1){
                var1 = a.net + 1000*a.count;
                var2 = b.net + 1000*b.count;
            }
            if (var1 < var2) {
                return 1;
            }
            if (var1 > var2) {
                return -1;
            }
            return 0;
        });
        var arr2 = arr.filter(function (d, i) {
            return i <= maxNodesInSnapshot;
        });

        var cut = 1;
        graphByMonths[m] = [];
        while (cut <= numCut) {
            // *********** VERTICES **************
            var nodes5 = [];
            for (var i = 0; i < arr2.length; i++) {
                var nod = new Object();
                nod.id = i;
                nod.m = m;
                nod.category = arr2[i].category;
                nod.name = arr2[i].term;
                nod.net = arr2[i].net;
                nod.count = arr2[i].count;
                nod.x = xStep + xScale(nod.m);   // 2016 initialize x position
                nod.y = height / 2;

                //if (termArray3[i].isConnected>0)  // Only allow connected items
                nodes5.push(nod);

                termList[nod.name] = nod;// List of term to feed to TimeArcs in main.js
            }
            // *********** EDGES **************
            var links5 = [];
            var relationshipMax5 = 0;
            // for(var cut=1; cut<30;cut++){
            for (var i = 0; i < nodes5.length; i++) {
                var term1 = nodes5[i].name;
                for (var j = i + 1; j < nodes5.length; j++) {
                    var term2 = nodes5[j].name;
                    if (relationship[term1 + "__" + term2] && relationship[term1 + "__" + term2][m] >= cut) {
                        var l = new Object();
                        l.source = nodes5[i];
                        nodes5[i].isConnected = true;
                        l.target = nodes5[j];
                        nodes5[j].isConnected = true;
                        l.count = relationship[term1 + "__" + term2][m];
                        l.m = m;
                        links5.push(l);
                        if (relationship[term1 + "__" + term2][m] > relationshipMax5)
                            relationshipMax5 = relationship[term1 + "__" + term2][m];
                    }
                }
            }

            var tempnodes = nodes5.filter(function (d, i) {
                return d.isConnected;
            });
            var templinks = links5;
           
            var graph = {};
            graph.nodes = tempnodes;
            graph.links = templinks;
            var node_ids = [], link_ids = [];
            tempnodes.forEach(function (d) {
                node_ids.push(d.id);
            });
            templinks.forEach(function (d) {
                link_ids.push({"source": d.source.id, "target": d.target.id, "weight": 1})
            });

            var community = jLouvain().nodes(node_ids).edges(link_ids)();
            var adjmatrix = create_adjmatrix(graph);
            graph.nodes.forEach(function (d) {
                d.community = community[d.id];
            });

            var groups = d3.nest()
                .key(function (d) {
                    return d.community;
                })
                .entries(graph.nodes);
            var partition = [];
            groups.forEach(function (d) {
                var par = [];
                d.values.forEach(function (a) {
                    par.push(graph.nodes.findIndex(x => x.id == a.id)
                    )
                });
                partition.push(par);
            })
            graph.Qmodularity = modularity(partition, adjmatrix);
            graph.cutoff = cut;
            graphByMonths[m].push(graph);
            cut += 1;
        }
        
        // Draw network snapshot
        if (graphByMonths[m][selectedCut] != undefined) {
            updateSubLayout(graphByMonths[m][selectedCut].nodes, graphByMonths[m][selectedCut].links, m);
        }
    }
    // Update the layout
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

function drawgraph2() {
    var startMonth = lMonth > numLens ? lMonth - numLens : 0;
    if (lMonth<0) 
        startMonth=-100;   // Do not draw arc diagram if not lensed
    var endMonth = startMonth + numLens * 2 + 1;
    var breakCheck = false;
    lNodes = [];
    for (var m = startMonth; m < endMonth; m++) {
        var newCut = selectedCut;
        if (newCut<0){  // Best Q modularity selected
            newCut = cutOffvalue[m]-1;
        }

        if (graphByMonths[m] == undefined || graphByMonths[m][newCut] == undefined) continue;
        for (var i = 0; i < graphByMonths[m][newCut].nodes.length; i++) {
            if (lNodes.length == 200) {
                breakCheck = true;
                break;
            }
            var nod = graphByMonths[m][newCut].nodes[i];
            var found = false;
            for (var j = 0; j < lNodes.length; j++) {
                if (lNodes[j].name == nod.name) {
                    found = true;
                    break;
                }
            }
            if (!found) {
               lNodes.push(nod);
            }
        }
        if (breakCheck)
            break;
    }

    // compute the frequency of node at month m
    for (var i=0; i<lNodes.length; i++){
        var nod = lNodes[i];
        nod.frequency = 0;
        if (terms[nod.name]!=undefined && terms[nod.name][nod.m])
            nod.frequency = terms[nod.name][nod.m];
    }

    // Now compute the node size based on a selected measure
    for (var i=0; i<lNodes.length; i++){
        var nod = lNodes[i];
        nod.measurement = 0;
        if (selectedSetNodeBy==1) {
            nod.measurement = 100*nod.frequency+nod.net+nod.weight;
        }
        else if (selectedSetNodeBy==2) {
            nod.measurement = nod.frequency+100*nod.net+nod.weight;
        }
        else if (selectedSetNodeBy==3) {
            nod.measurement = nod.frequency+nod.net+100*nod.weight;
        }
        else if (selectedSetNodeBy==4) {
            var bet= nod.betweenness;
            if (bet==undefined || isNaN(bet)) {
                bet=0;
            }
            else if (bet>1) {
                bet=1;
                nod.betweenness=1;
            }
            nod.measurement = nod.frequency+nod.net+nod.weight+100*bet;
        }
    }


    lNodes.sort(function (a, b) {
        if (a.m < b.m) { // order by month
            return -1;
        }
        else if (a.m > b.m) {
            return 1;
        }
        else {
            if (a.community < b.community) { // cluster id, generated by Vinh Nguyen
                return -1;
            }
            else if (a.community > b.community) {
                return 1;
            }
            else {
                if (a.measurement < b.measurement) {
                    return 1;
                }
                else if (a.measurement > b.measurement) {
                    return -1;
                }
                else
                    return -1;
            }
        }
    });


    var yStartHistogram = height + 110; // y starts drawing the stream graphs
    drawHistograms(yStartHistogram);   // in main3.js
    if (selectedCut<0){
        updateHistogramOptimized();   // Update histogram by
    }

    var max = 1;
    yStart = height + 220; // y starts drawing the stream graphs

    var yTemp = yStart;
    var numNodesInFirstMonth = 0;
    // Compute y position of small multiple *******
    var yStep1 = Math.min(11,(heightSVG-yStart-10)/lNodes.length)
    var yStep2 = Math.max(yStepOfStreamGraph,yStep1);
    for (var i=0;i<lNodes.length;i++){
        if (lNodes[i].measurement>max)
            max = lNodes[i].measurement;
        if (lNodes[i].m==lNodes[0].m){
            yTemp += yStep2;
            numNodesInFirstMonth++;
        }
        else{
            var yStep3= Math.min(yStep1,(heightSVG-yStart-numNodesInFirstMonth*yStep2-10)/(lNodes.length-numNodesInFirstMonth));
            yTemp += yStep3;
        }
        lNodes[i].yInMultiples =  yTemp;
    }        

    // ********************************* Node scales *********************************************************************
    var rScale = d3.scale.linear()
                    .range(nodeRadiusRange)
                    .domain([0, Math.sqrt(max)]);    
    
    for (var i=0; i<allSVG.length;i++){
        var svg2 = allSVG[i];
        svg2.selectAll(".node5")
            .transition().duration(500)
            .attr("r", function(d,i){
                if (startMonth<=d.m && d.m < endMonth){
                    var r = isNaN(rScale(d.measurement))? 0.2 : rScale(Math.sqrt(d.measurement));
                    return r;
                }
                else{
                    return 1; // min value of rScale
                }                 
            });
    }
 
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

    var yTemp2 = yStart;
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
            brushingStreamText(d, countryIndex, this);
        })
        .on("mouseout", function(d){
            hideTip(d);
        });   

    /*    
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
                return colorPurpleGreen(d[d.maxYearAbove].OutlyingDif);
        })
        .style("text-anchor", "middle")
        .style("text-shadow", "1px 1px 0 rgba(255, 255, 255, 0.99")
        .attr("x", function (d,i) {
            //console.log(d.maxYearAbove);
            if (d.maxYearAbove==undefined)
                return 0;
            else
                return xStep + xScale(d.maxYearAbove);    // x position is at the arcs
        })
        .attr("y", function (d, i) {
            if (d.maxYearAbove==undefined || d.maxYearAbove==0 || d[d.maxYearAbove]==undefined)
                return d[0].y;
            else{
                //console.log(d.maxYearAbove+" above "+d[d.maxYearAbove]+" "+d[d.maxYearAbove].Outlying);
                return d[0].y-yScaleS(d[d.maxYearAbove].Outlying);     // Copy node y coordinate    
            }
        })
        .attr("font-family", "sans-serif")
        .attr("font-size", "11px")
        .text(function (d) {
            return d[0].country;
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
                return colorPurpleGreen(d[d.maxYearBelow].OutlyingDif);
        
        })
        .style("text-anchor", "middle")
        .style("text-shadow", "1px 1px 0 rgba(255, 255, 255, 0.99")
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
                return d[0].y-yScaleS(d[d.maxYearBelow].Outlying); 
        })
        .attr("font-family", "sans-serif")
        .attr("font-size", "11px")
        .text(function (d) {
            return d[0].country;
        });  
    */
    //** TEXT CLOUD **********************************************************    
    var yTextClouds = height + 120; // y starts drawing the stream graphs
    drawTextClouds(yTextClouds);    // in main3.js
             
}

function updategraph2() {
    maxAbs = Math.max(maxDifAboveForAll, Math.abs(maxDifBelowForAll));    
    svg.selectAll(".textCloud3").transition().duration(transitionTime)
        .attr("x", function(d,i) {
            return xStep + xScale(Math.floor(i/numTermsWordCloud));    // x position is at the arcs
        })
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
        });
    if (0<=lMonth && lMonth<dataS.YearsData.length)  { 
        var year =  lMonth+1;

        countryList.sort(function (a, b) {
            var maxOutlyingDif_A = 0;
            var maxOutlyingDif_B = 0;
            for (var i=year-numLens; i<=year+numLens;i++){
                if (0<i && i<=dataS.YearsData.length){ // within the lensing interval
                    maxOutlyingDif_A = Math.max(maxOutlyingDif_A, Math.abs(a[i].OutlyingDif));
                    maxOutlyingDif_B = Math.max(maxOutlyingDif_B, Math.abs(b[i].OutlyingDif)); 
                }  
            }
            if (maxOutlyingDif_A < maxOutlyingDif_B)
                return 1;
            else 
                return -1;
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
        
        svg.selectAll(".maxAboveText")
        .attr("x", function (d,i) {
            if (d.maxYearAbove==undefined)
                return 0;
            else
                return xStep + xScale(d.maxYearAbove);    // x position is at the arcs
        });
        svg.selectAll(".maxBelowText")
            .attr("x", function (d) {
                if (d.maxYearBelow==undefined)
                    return 0;
                else
                    return xStep + xScale(d.maxYearBelow);    // x position is at the arcs
            }); 

    }
          
        
}    

function computeMonthlyData(term) {
    var monthly = [];
    for (var m = 0; m < numMonth; m++) {
        var mon = new Object();
        if (terms[term][m]) {
            mon.value = terms[term][m];
            mon.monthId = m;
            monthly.push(mon);
        }
        else if (terms[term][m-1] || terms[term][m+1]) {
            mon.value = 0;
            mon.monthId = m;
            monthly.push(mon);
        }
    }
    return monthly;
}


function linkArc3(d) {
    var term1 = d.source.name;
    var term2 = d.target.name;
    var x1 = xStep + xScale(d.m);
    var x2 = x1;
    if (termList[term1].monthly == undefined || termList[term2].monthly == undefined) return; // no data
    var y1 = termList[term1].monthly[0].yNode;
    var y2 = termList[term2].monthly[0].yNode;
    var dx = x2 - x1,
        dy = y2 - y1,
        dr = Math.sqrt(dx * dx + dy * dy) / 2;
    if (y1 < y2)
        return "M" + x1 + "," + y1 + "A" + dr + "," + dr + " 0 0,1 " + x2 + "," + y2;
    else
        return "M" + x2 + "," + y2 + "A" + dr + "," + dr + " 0 0,1 " + x1 + "," + y1;
}