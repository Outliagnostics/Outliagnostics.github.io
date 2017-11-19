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

  var size =20;
  var padding =0;
  var x2 = 0;
  var y2 = 0;
  var margin = forceSize/ 2-size/2; 
  svg2.append("rect")
      .attr("class", "frame")
      .attr("x", margin)
      .attr("y", margin)
      .attr("rx", 1)
      .attr("ry", 1)
      .attr("width", size - padding)
      .attr("height", size - padding)
      .style("fill", function(d) { 
            return colorRedBlue(dataS.YearsData[m].Scagnostics0[0]);
      })
      //.style("fill-opacity",0.9)
      .style("stroke","#000")
      .style("stroke-width",0.05);
  
  var dataPoints =[];
  for (var c=0; c<dataS.Countries.length;c++){
    var obj = {};
    obj.country = dataS.Countries[c];
    obj.year = m;
    for (var v=0; v<dataS.Variables.length;v++){
      obj["s"+v] = dataS.YearsData[m]["s"+v][c];
      obj["v"+v] = dataS.CountriesData[obj.country][m]["v"+v];
      if (v%2==1){
        var pair = Math.floor(v/2);
        obj["Scagnostics"+pair] = dataS.YearsData[m]["Scagnostics"+pair]; // 0 is the index of Outlysing
        obj["ScagnosticsLeave1out"+pair] = []; // 0 is the index of Outlysing
        for (var s=0; s<dataS.Scagnostics.length;s++){ 
            obj["ScagnosticsLeave1out"+pair].push(dataS.CountriesData[obj.country][m][dataS.Scagnostics[s]]);
        }
      }
    }
    dataPoints.push(obj);
  }    
  //debugger;
  svg2.selectAll("circle")
      .data(dataPoints)
    .enter().append("circle")
        .attr("class", function (d,i) {
            return "dataPoint"+i;
        })
        .attr("cx", function(d) { 
            if (d["v0"]=="NaN")
                return 0;
            else
                return margin+1+d["s"+selectedVar]*(size-2); 
        })
        .attr("cy", function(d,i) { 
            if (d["v1"]=="NaN")
                return 0;
            else
                return margin+size-1-d["s"+(selectedVar+1)]*(size-2); 
        })
        .attr("r", size/30)
        .style("stroke", "#fff")
        .style("stroke-width", 0.02)
        .style("stroke-opacity", 0.8)
        .style("fill", "#000")
        .style("fill-opacity", pointOpacity)
        .on("mouseover", function(d,i){
            brushingDataPoints(d,i);
        })
        .on("mouseout", function(d){
            hideTip(d);
        }); 

  // Show score on each plot    
  /*cell.append("text")
      .attr("class", "scoreCellText")
      .attr("x", 3)
      .attr("y", 14)
      .attr("font-family", "sans-serif")
      .attr("font-size", "8px")
      .style("text-shadow", "1px 1px 0 rgba(0, 0, 0, 0.7")
      .style("fill", "#f6f")
      .text(function(d,i) { 
        var k = -1;  
        if (p.mi<p.mj){
          k = p.mj*(p.mj-1)/2+p.mi; 
        }
        else if (p.mi>p.mj){
           k = p.mi*(p.mi-1)/2+p.mj; 
        }
        return parseFloat(dataS[k][selectedScag]).toFixed(2); 
      })
      .style("fill-opacity", function(){
        return document.getElementById("checkbox1").checked ? 1 : 0;
      });  */       
}