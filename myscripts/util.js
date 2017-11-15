var diameter = 1000,
    radius = diameter / 2,
    innerRadius = radius - 120;
var months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
// Add color legend
var yTimeBox = 0;

var colorArray = ["#9dbee6","#afcae6","#c8dce6","#e6e6e6","#e6e6d8","#e6d49c","#e6b061","#e6852f","#e6531a","#e61e1a"];

var colorRedBlue = d3.scale.linear()
    .domain([0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1])
    .range(colorArray);

// These texts are different upon input dataset   
var text1 = "terms";
var text2 = "blogs";
var textFile = "";
       
function drawColorLegend() {
    var xx = 10;
    var yy = 58;
    var rr = 6;
    // number of input terms
    if (fileName.indexOf("Cards_Fries")>=0){
        text1 = "proteins";
        text2 = "index cards";
        textFile = "Fries Cards";
    }
    else if (fileName.indexOf("VIS")>=0){
        text1 = "authors";
        text2 = "papers";
        textFile = "VIS publications";
    }
    else{
        text1 = "terms";
        text2 = "blogs";
        textFile = fileName.split("/")[1].split(".tsv")[0];
    }
    
    /*
    svg.append("text")
        .attr("class", "nodeLegend")
        .attr("x", xx - 10)
        .attr("y", yy-17)
        .text(numberWithCommas(termArray.length) + " "+text1+" of " + numberWithCommas(data.length) + " "+text2)
        .attr("dy", ".21em")
        .attr("font-family", "sans-serif")
        .attr("font-size", "13px")
        .style("text-anchor", "left")
        .style("fill", "#000000");

     // Count terms for each category
    var countList = {};      
    for(var i = 0;i <termArray.length; i++){
        if (countList[termArray[i].category]==undefined)
            countList[termArray[i].category] = 1;
        else
            countList[termArray[i].category]++;
    }  */ 
            
    
    // Scagnostics color legend ****************
    //Append a defs (for definition) element to your SVG
    var defs = svg.append("defs");
    //Append a linearGradient element to the defs and give it a unique id
    var linearGradient = defs.append("linearGradient")
        .attr("id", "linear-gradient");    
    //Horizontal gradient
    linearGradient
        .attr("x1", "0%")
        .attr("y1", "0%")
        .attr("x2", "100%")
        .attr("y2", "0%");    
    for (var i =0; i<colorArray.length;i++){
        var percent = i*10;
        linearGradient.append("stop") 
            .attr("offset", percent+"%")   
            .attr("stop-color", colorArray[i]); //dark blue  
    }      

    var yScagLegend = 60;
    var wScagLegend = 160;
    //Draw the rectangle and fill with gradient
    svg.append("rect")
        .attr("x", 11)
        .attr("y", yScagLegend+5)
        .attr("width", wScagLegend)
        .attr("height", 20)
        .style("fill", "url(#linear-gradient)");
    
    svg.append("text")
        .attr("x", wScagLegend/2+8)
        .attr("y", yScagLegend)
        .attr("font-family", "sans-serif")
        .attr("font-size", "12px")
        .style("text-anchor", "middle")
        .style("font-weight", "bold")
        .style("fill", "#000")
        .text("Outlying measure"); 
    svg.append("text")
        .attr("x", 2)
        .attr("y", yScagLegend+19)
        .attr("font-family", "sans-serif")
        .attr("font-size", "12px")
        .style("text-anchor", "left")
        .style("font-weight", "bold")
        .style("fill", "#000")
        .text("0");  
    svg.append("text")
        .attr("x", wScagLegend+12)
        .attr("y", yScagLegend+19)
        .attr("font-family", "sans-serif")
        .attr("font-size", "12px")
        .style("text-anchor", "left")
        .style("font-weight", "bold")
        .style("fill", "#000")
        .text("1");           

     // Draw color legend **************************************************
    var yScagLegend2 = 110;
    svg.selectAll(".legendCircle").remove();
    svg.selectAll(".legendCircle")
        .data(categories).enter()
        .append("circle")
        .attr("class", "legendCircle")
        .attr("cx", xx-3)
        .attr("cy", function (d, i) {
            return yScagLegend2 + i * 16;
        })
        .attr("r", rr)
        .style("fill", function (d, i) {
            if (i==0) return colorAbove;
            else if (i==1) return colorBelow;
            else return "#000";
        });
    svg.selectAll(".legendText").remove();
    svg.selectAll(".legendText")
        .data(categories).enter()
        .append("text")
        .attr("class", "legendText")
        .attr("x", xx+6)
        .attr("y", function (d, i) {
            return yScagLegend2 + i * 16 + 2;
        })
        .text(function (d) {
            return d;
        })
        .attr("dy", ".21em")
        .attr("font-family", "sans-serif")
        .attr("font-size", "12px")
        .style("text-anchor", "left")
        .style("fill", function (d, i) {
            if (i==0) return colorAbove;
            else if (i==1) return colorBelow;
            else return "#000";
        });
            
    //drawTopEntities(text1);
}

// ******************************************Process top 100 entities array ******************************************
//Called at the end of drawColorLegend()
function drawTopEntities(text1){
   top100termsArray.sort(function (a, b) {
        if (a.count < b.count) {
            return 1;
        }
        if (a.count > b.count) {
            return -1;
        }
        return 0;
    });

    var x6 = 4;
    var y6 = 350;

    svg.append("text")
        .attr("class", "textTopEntities")
        .attr("x", x6)
        .attr("y", function (d, i) {
            return y6;
        })
        .text(function (d) {
            return "Top " +top100termsArray.length +" "+text1;
        })
        .attr("dy", ".21em")
        .attr("font-family", "sans-serif")
        .attr("font-size", "14px")
        .style("text-anchor", "left")
        .style("font-weight", "bold")
        .style("fill", "#000");

    var node6 = svg.selectAll(".node6Text")
        .data(top100termsArray)
        .enter()
        .append("text")
        .attr("class", "node6Text")
        .attr("x", x6)
        .attr("y", function (d, i) {
            return y6 +17 + i * 14;
        })
        .text(function (d) {
            return d.term + " ("+numberWithCommas(d.count)+")";
        })
        .attr("dy", ".21em")
        .attr("font-family", "sans-serif")
        .attr("font-size", "12px")
        .style("text-anchor", "left")
        .style("fill", function (d, i) {
            return getColor3(d.category);
        })
        .on("mouseover", function(d){
            svg.selectAll(".node6Text")
            .style("fill", function(d2){
                if (d2.term==d.term){
                    return "#000";
                }
                else{
                    return getColor3(d2.category);
                }
            });
        })
        .on("mouseout", function(d){
            svg.selectAll(".node6Text")
                .style("fill", function(d2){
                    return getColor3(d2.category);
                 });
        })
        .on("click", function(d){
            searchTerm = d.term;
            recompute();
        });
;
}


function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function removeColorLegend() {
    svg.selectAll(".nodeLegend").remove();
}

var listX;
function drawTimeGrid() {
    listX = [];
    if (fileName.indexOf("VIS")>=0|| fileName.indexOf("IMDB")>=0 || fileName.indexOf("PopCha")>=0 || fileName.indexOf("Cards")>=0){
        for (var i = minYear; i <= maxYear; i++) {
            var xx = xStep + xScale(i - minYear);
            var obj = {};
            obj.x = xx;
            obj.year = i;
            listX.push(obj);   
        }
    }
    else{    
        for (var i = minYear; i <= maxYear; i++) {
            for (var j = 0; j < 12; j++) {
                var xx = xStep + xScale((i - minYear) * 12 + j);
                var obj = {};
                obj.x = xx;
                obj.year = i;
                listX.push(obj);
            }
        }
    }
    svg.selectAll(".timeLegendLine").data(listX)
        .enter().append("line")
        .attr("class", "timeLegendLine")
        .style("stroke", "#000")
        .style("stroke-opacity", 1)
        .style("stroke-width", 0.3)
        .attr("x1", function (d) {
            return d.x;
        })
        .attr("x2", function (d) {
            return d.x;
        })
        .attr("y1", 0)
        .attr("y2", 1500);
}
function drawTimeText() {
    svg.selectAll(".timeLegendText").data(listX)
        .enter().append("text")
        .attr("class", "timeLegendText")
        .style("fill", "#000")
        .style("text-anchor", "start")
        .style("text-shadow", "0px 1px 1px rgba(255, 255, 255, 1")
        .attr("x", function (d) {
            return d.x;
        })
        .attr("y", function (d, i) {
                return height - 15;
        })
        .attr("dy", ".21em")
        .attr("font-family", "sans-serif")
        .attr("font-size", "15px")
        .text(function (d, i) {
            if (fileName.indexOf("VIS")>=0 || fileName.indexOf("IMDB")>=0 || fileName.indexOf("PopCha")>=0 || fileName.indexOf("Cards")>=0){
                return d.year;
            }    
            else{
                if (i % 12 == 0)
                    return d.year;
                else
                    return months[i % 12];
            }    
        });
}

function updateTimeLegend() {
    var listX = [];
    if (fileName.indexOf("VIS")>=0 || fileName.indexOf("IMDB")>=0 || fileName.indexOf("PopCha")>=0 || fileName.indexOf("Cards")>=0){
        for (var i = minYear; i <= maxYear; i++) {
            var xx = xStep + xScale(i - minYear);
            var obj = {};
            obj.x = xx;
            obj.year = i;
            listX.push(obj);   
        }
    }
    else{    
        for (var i = minYear; i <= maxYear; i++) {
            for (var j = 0; j < 12; j++) {
                var xx = xStep + xScale((i - minYear) * 12 + j);
                var obj = {};
                obj.x = xx;
                obj.year = i;
                listX.push(obj);
            }
        }
    }

    svg.selectAll(".timeLegendLine").data(listX).transition().duration(transitionTime)
        .style("stroke-dasharray", function (d, i) {
            if (fileName.indexOf("VIS")>=0 || fileName.indexOf("IMDB")>=0 || fileName.indexOf("PopCha")>=0 || fileName.indexOf("Cards")>=0){
                return i % 5 == 0 ? "3, 1" : "1, 3"
            }
            else{ 
                if (!isLensing)
                    return "1, 2";
                else
                    return i % 12 == 0 ? "3, 1" : "1, 3"
            }    
        })
        .style("stroke-opacity", function (d, i) {
            if (fileName.indexOf("VIS")>=0 || fileName.indexOf("IMDB")>=0 || fileName.indexOf("PopCha")>=0 || fileName.indexOf("Cards")>=0){
                return 1;
            }
            else{    
                if (i % 12 == 0)
                    return 1;
                else {
                    if (isLensing && lMonth - numLens <= i && i <= lMonth + numLens)
                        return 1;
                    else
                        return 0;
                }
            }
        })
        .attr("x1", function (d) {
            return d.x;
        })
        .attr("x2", function (d) {
            return d.x;
        });
    svg.selectAll(".timeLegendText").data(listX).transition().duration(transitionTime)
        .style("fill-opacity", function (d, i) {
            return getOpacity(d,i);
        })
        .attr("x", function (d, i) {
            return d.x;
        });

    // ************************************SCALE force layouts ************************************
   // snapshotScale = 0.10;
    for (var i = minYear; i <= maxYear; i++) {
        for (var j = 0; j < 12; j++) {
            var m = (i - minYear) * 12 + j;
            var view = "0 0 " + forceSize + " " + forceSize;
            if (lMonth - numLens <= m && m <= lMonth + numLens)
                view = (forceSize * (1-snapshotScale)/2) + " " + (forceSize * (1-snapshotScale)/2) + " " + (forceSize * snapshotScale) + " " + (forceSize * snapshotScale);
            else if (lMonth - numLens == m+1 ){
                var snapshotScale2 = snapshotScale*1.8;
                view = (forceSize * (1-snapshotScale2*1.085)/2) + " " + (forceSize * (1-snapshotScale2)/2) + " " + (forceSize * snapshotScale2) + " " + (forceSize * snapshotScale2);  
            }
            else if (m-1 == lMonth + numLens){
                var snapshotScale2 = snapshotScale*1.8;
                view = (forceSize * (1-snapshotScale2/1.09)/2) + " " + (forceSize * (1-snapshotScale2)/2) + " " + (forceSize * snapshotScale2) + " " + (forceSize * snapshotScale2);
               
            }
            
            svg.selectAll(".force" + m).transition().duration(transitionTime)
                .attr("x", xStep - forceSize / 2 + xScale(m))
                .attr("viewBox", view);
        }
    }
}
// Used in util.js and main.js *****************
function getOpacity(d,i) {
    if (fileName.indexOf("VIS")>=0 || fileName.indexOf("IMDB")>=0 || fileName.indexOf("PopCha")>=0 || fileName.indexOf("Cards")>=0){
        if (i % 5 == 0)
            return 1;
        else {
            if (isLensing && lMonth - numLens <= i && i <= lMonth + numLens)
                return 1;
            else
                return 0;
        }
    }    
    else{
        if (i % 12 == 0)
            return 1;
        else {
            if (isLensing && lMonth - numLens <= i && i <= lMonth + numLens)
                return 1;
            else
                return 0;
        }
    }        
}


function drawTimeBox() {
    svg.append("rect")
        .attr("class", "timeBox")
        .style("fill", "#666")
        .style("fill-opacity", 0.5)
        .attr("x", xStep)
        .attr("y", yTimeBox-1)
        .attr("width", width-xStep)
        .attr("height", 30)
        .on("mouseout", function () {
            isLensing = false;
            coordinate = d3.mouse(this);
            lMonth = Math.floor((coordinate[0] - xStep) / XGAP_);
        })
        .on("mousemove", function () {
            isLensing = true;
            coordinate = d3.mouse(this);
            lMonth = Math.floor((coordinate[0] - xStep) / XGAP_);
 
            // Update layout
            updateTimeLegend();
            updateTimeBox();

        });
}

function updateTimeBox() {
    svg.selectAll(".timeLegendText")
        .attr("y", function (d, i) {
            if (fileName.indexOf("VIS")>=0  || fileName.indexOf("IMDB")>=0 || fileName.indexOf("PopCha")>=0 || fileName.indexOf("Cards")>=0){
                return yTimeBox + 20;
            }
            else{
                return (i % 12 == 0) ? yTimeBox + 12 : yTimeBox + 22;
            }    
            
        })
        .attr("x", function (d, i) {
            return d.x;
        });

    // Recompute the timeArcs
    if (oldLmonth != lMonth) {
        updategraph2();
        oldLmonth = lMonth;
    }
}

var buttonLensingWidth =100;
var buttonheight =18;
var roundConner = 4;
var colorHighlight = "#fc8";
var buttonColor = "#ddd";

// Control panel on the left *********************
function drawControlPanel(){
    //  node Dropdown *********************
    var nodedata = [{"id": 1, "value": "Frequency"}, {"id": 2, "value": "Net frequency"}, {"id": 3, "value": "Degree"},{
        "id": 4,"value": "Betweenness centrality"
    }];
    var selectOrder = d3.select('#nodeDropdown').on('change',setNodesBy);
    var Orderoptions = selectOrder.selectAll('option').data(nodedata).enter().append('option').attr('value', function (d) {
        return d.id;
    }).text(function (d) {
        return d.value;
    })

    //  edge Weight Dropdown *********************
    var edgeData =[{"id":1, "value":">=1"},{"id":2, "value":">=2"},{"id":3, "value":">=3"},{"id":4, "value":">=4"},{"id":5, "value":">=5"},{"id":"optimized", "value":"Best Q modularity"}];
    var select = d3.select('#edgeWeightDropdown').on('change',function () {
        selectValue = d3.select('#edgeWeightDropdown').property('value');
        setCut(selectValue);
    })
    var options = select.selectAll('option').data(edgeData).enter().append('option').attr('value', function (d) {
        return d.id;
    }).text(function (d) {
        return d.value;
    })

   
}
