/* November 2017 
 * Tommy Dang, Assistant professor, iDVL@TTU
 *
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, THE AUTHORS MAKE NO REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

var margin = {top: 0, right: 0, bottom: 0, left: 0};
var width = document.body.clientWidth - margin.left - margin.right;
var height = 50 - margin.top - margin.bottom;
var heightSVG = 2500;

//Append a SVG to the body of the html page. Assign this SVG as an object to svg
var svg = d3.select("body").append("svg")
    .attr("width", width)
    .attr("height", heightSVG);
svg.call(tip);  

var minYear, maxYear;
var xStep = 210;
var searchTerm;

var isLensing;
var lensingMul = 6;
var lMonth;
var oldLmonth; // use this variable to compare if we are lensing over a different month

var XGAP_; // gap between months on xAxis
var numLens = 3;

function xScale(m) {
    if (isLensing) {
        var maxM = Math.max(0, lMonth - numLens - 1);
        var numMonthInLense = (lMonth + numLens - maxM + 1);

        //compute the new xGap
        var total = numMonth + numMonthInLense * (lensingMul - 1);
        var xGap = (XGAP_ * numMonth) / total;

        if (m < lMonth - numLens){
            var xx =  m * xGap;
            if (m == lMonth - numLens-1)
                xx+=xGap;
            return xx;
        }  
        else if (m > lMonth + numLens) {
            var xx = maxM * xGap + numMonthInLense * xGap * lensingMul + (m - (lMonth + numLens + 1)) * xGap;
            if (m == lMonth + numLens+1)
                xx-=xGap;
            return xx;
        }
        else {
            return maxM * xGap + (m - maxM) * xGap * lensingMul;
        }
    }
    else {
        return m * XGAP_;
    }
}

var area = d3.svg.area()
    .interpolate("basic")
    .x(function (d) {
        return xStep + xScale(d.monthId);
    })
    .y0(function (d) {
        return d.yNode - yScale(d.value);
    })
    .y1(function (d) {
        return d.yNode + yScale(d.value);
    });

var optArray = [];   // FOR search box
var categories = ["Above Outlying of original plot","Below Outlying of original plot"];
var getColor3;  // Colors of categories
 
//*****************************************************************
var fileList = ["LifeExpectancy"]
var fileName = fileList[0];

// START: loader spinner settings ****************************
var opts = {
  lines: 25, // The number of lines to draw
  length: 15, // The length of each line
  width: 5, // The line thickness
  radius: 25, // The radius of the inner circle
  color: '#000', // #rgb or #rrggbb or array of colors
  speed: 2, // Rounds per second
  trail: 50, // Afterglow percentage
  className: 'spinner', // The CSS class to assign to the spinner
};
var target = document.getElementById('loadingSpinner');
var spinner = new Spinner(opts).spin(target);
// END: loader spinner settings ****************************


addDatasetsOptions(); // Add these dataset to the select dropdown, at the end of this files
drawControlPanel();

var dataS;
function loadData(){
    d3.json("data/"+fileName+".json", function(data_) {
        dataS=data_;
    
        searchTerm = "";
        isLensing = false;
        oldLmonth = -1000;
        lMonth = -lensingMul * 2;
         
        
        minYear =1960;
        maxYear =2015;
        numMonth = maxYear - minYear +1;
        XGAP_ = (width-xStep-2)/numMonth; // gap between months on xAxis


        svg.append("rect")
            .attr("class", "background")
            .style("fill", "#fff")
            .attr("x", 0)
            .attr("y", yTimeBox)
            .attr("width", width)
            .attr("height", heightSVG)

        drawColorLegend();
        drawTimeGrid();
        drawTimeText();
        drawTimeBox(); // This box is for brushing 

        // 2017, this function is main2.js
        computeMonthlyGraphs();

       
        // Spinner Stop ********************************************************************
        spinner.stop();

        for (var i = 0; i < dataS.Countries.length; i++) {
            optArray.push(dataS.Countries[i]);
        }
        optArray = optArray.sort();
        $(function () {
            $("#search").autocomplete({
                source: optArray
            });
        });
        
        //    chartStreamGraphs();  // Streamgraphs********************************************************************
        setTimeout(function(){
            svg.append("text")
                .attr("class", "textLensingArea")
                .attr("x", width/2)
                .attr("y", 20)
                .text("Lensing area")
                .attr("font-family", "sans-serif")
                .attr("font-size", "16px")
                .style("text-anchor", "middle")
                .style("font-weight", "bold")
                .style("text-shadow", "0 0 5px #aaa")
                .style("fill", "#000");
            svg.selectAll(".timeLegendText")
                .style("fill-opacity", 0.05);

            var startTime = new Date().getTime();
            var interval2 = setInterval(function(){ 
                var d = new Date();
                var n = d.getMilliseconds();
                svg.selectAll(".textLensingArea")
                    .style("fill-opacity", (n%1000)/1000);
                if(new Date().getTime() - startTime > 4000){
                    clearInterval(interval2);
                    svg.selectAll(".textLensingArea").remove();
                    svg.selectAll(".timeLegendText")
                        .style("fill-opacity", function (d, i) {
                            return getOpacity(d,i);
                        });
                    return;
                }  
                
            }, 50);    
        }, 3000);  
    });   
}    


$('#btnUpload').click(function () {
    var bar = document.getElementById('progBar'),
        fallback = document.getElementById('downloadProgress'),
        loaded = 0;
    var load = function () {
        loaded += 1;
        bar.value = loaded;

        /* The below will be visible if the progress tag is not supported */
        $(fallback).empty().append("HTML5 progress tag not supported: ");
        $('#progUpdate').empty().append(loaded + "% loaded");

        if (loaded == 100) {
            clearInterval(beginLoad);
            $('#progUpdate').empty().append("Upload Complete");
            console.log('Load was performed.');
        }
    };
    var beginLoad = setInterval(function () {
        load();
    }, 50);

});

// Other fucntions *******************************************************
function searchNode() {
    searchTerm = document.getElementById('search').value;
    var countryIndex = dataS.Countries.indexOf(searchTerm);
    if (countryIndex>=0)
        brushingStreamText(countryIndex);
}

function addDatasetsOptions() {
    var select = document.getElementById("datasetsSelect");   
    for(var i = 0; i < fileList.length; i++) {
        var opt = fileList[i];
        var el = document.createElement("option");
        el.textContent = opt;
        el.value = opt;
        el["data-image"]="images2/datasetThumnails/"+fileList[i]+".png";
        select.appendChild(el);
    }        
    document.getElementById('datasetsSelect').value = fileName;  //************************************************
    fileName = document.getElementById("datasetsSelect").value;
    loadData();
}


function loadNewData(event) {
    //alert(this.options[this.selectedIndex].text + " this.selectedIndex="+this.selectedIndex);
    svg.selectAll("*").remove();
    fileName = this.options[this.selectedIndex].text;
    loadData();
}
