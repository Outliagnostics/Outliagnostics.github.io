/* November 2017 
 * Tommy Dang, Assistant professor, iDVL@TTU
 *
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, THE AUTHORS MAKE NO REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

var forceSize = 90; // Max size of force layouts at the bottom

var allSVG = [];
var pointOpacity = 0.9;
var selectedVar = 0;
var selectedScag = 0;

function updateSubLayout(m) {
    svg.selectAll(".force" + m).remove();

    var svg2 = svg.append("svg")
        .attr("class", "force" + m)
        .attr("width", forceSize)
        .attr("height", forceSize)
        .attr("x", xStep - forceSize / 2 + m * XGAP_)
        .attr("y", 26);
    /* svg2.append("rect")
     .attr("width", "100%")
     .attr("height", "100%")
     .attr("fill", "pink")
     .attr("fill-opacity", 0.5);*/
    allSVG.push(svg2);

    var size = 20;
    // var size = 60;//TODO: This is for the teaser only (switch back the previous one for normal page)
    var padding = 0;

    var x2 = 0;
    var y2 = 0;
    var margin = forceSize / 2 - size / 2;
    svg2.append("rect")
        .attr("class", "frame")
        .attr("x", margin)
        .attr("y", margin)
        .attr("rx", 2)
        .attr("ry", 2)
        // .attr("rx", 5)//TODO: This is for the teaser only (switch back the previous one for normal page)
        // .attr("ry", 5)//TODO: This is for the teaser only (switch back the previous one for normal page)
        .attr("width", size - padding)
        .attr("height", size - padding)
        .style("fill", function (d) {
            if (!dataS.YearsData[m]) {
                debugger
            }
            return colorRedBlue(dataS.YearsData[m].Scagnostics0[0]);
        })
        //.style("fill-opacity",0.9)
        .style("stroke", "#000")
        .style("stroke-width", 0.1);

    var dataPoints = [];
    for (var c = 0; c < dataS.Countries.length; c++) {
        var obj = {};
        obj.country = dataS.Countries[c];
        obj.year = m;
        for (var v = 0; v < dataS.Variables.length; v++) {
            obj["s" + v] = dataS.YearsData[m]["s" + v][c];
            obj["v" + v] = dataS.CountriesData[obj.country][m]["v" + v];
            if (v % 2 == 1) {
                var pair = Math.floor(v / 2);
                obj["Scagnostics" + pair] = dataS.YearsData[m]["Scagnostics" + pair]; // 0 is the index of Outlysing
                obj["ScagnosticsLeave1out" + pair] = []; // 0 is the index of Outlysing
                for (var s = 0; s < dataS.Scagnostics.length; s++) {
                    obj["ScagnosticsLeave1out" + pair].push(dataS.CountriesData[obj.country][m][dataS.Scagnostics[s]]);
                }
            }
        }
        dataPoints.push(obj);
    }

    //Filter out data points with "NaN"
    dataPoints = dataPoints.filter(d=>d["v0"]!=="NaN" && d["v1"]!=="NaN" && d["s0"]!=="NaN" && d["s1"]!=="NaN" && (!isNaN(d["ScagnosticsLeave1out0"][0] - d["Scagnostics0"][0])));

    var scaleRadius = d3.scale.linear()
        .range([size / 35, size / 10])
        .domain([0, 1]);

    svg2.selectAll("circle")
        .data(dataPoints)
        .enter().append("circle")
        .attr("class", function (d, i) {
            return "dataPoint" + i;
        })
        .attr("cx", function (d) {
            if (d["v0"] === "NaN")
                return 0;
            else
                return margin + 1.5 + d["s" + selectedVar] * (size - 3);
                // return margin + 1.5 + d["s" + selectedVar] * (size - 9) + 3; //TODO: This is for the teaser only (switch back the previous one for normal page)
        })
        .attr("cy", function (d, i) {
            if (d["v1"] === "NaN")
                return 0;
            else
                return margin + size - 1.5 - d["s" + (selectedVar + 1)] * (size - 3);
                // return margin + size - 1.5 - d["s" + (selectedVar + 1)] * (size - 9) - 3;//TODO: This is for the teaser only (switch back the previous one for normal page)
        })
        .attr("r", function (d) {
            var difAbs = Math.abs(d["ScagnosticsLeave1out0"][0] - d["Scagnostics0"][0]);
            if(isNaN(difAbs)){
                debugger
            }
            return scaleRadius(difAbs);
        })
        .style("stroke", "#fff")
        .style("stroke-width", 0.02)
        .style("stroke-opacity", 0.8)
        .style("fill", function (d) {
            if (d["ScagnosticsLeave1out0"][0] > d["Scagnostics0"][0]) {
                if (d["ScagnosticsLeave1out0"][0] - d["Scagnostics0"][0] < outlyingCut)
                    return "#000";
                else
                    return colorAbove;
            }
            else if (d["ScagnosticsLeave1out0"][0] < d["Scagnostics0"][0]) {
                if (d["Scagnostics0"][0] - d["ScagnosticsLeave1out0"][0] < outlyingCut)
                    return "#000";
                else
                    return colorBelow;
            }
            else
                return "#000";
        })
        .style("fill-opacity", pointOpacity)
        .on("mouseover", function (d, i) {
            brushingDataPoints(d, i);
        })
        .on("mouseout", function (d) {
            hideTip(d);
        });
    setExploreEvent(svg2, dataPoints, dataS.Variables);
}