/* 2016 
 * Tuan Dang (on the BioLinker project, as Postdoc for EVL, UIC)
 *
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, THE AUTHORS MAKE NO REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

var tipWidth = 270;
var tipHeight = 470;
var tip_svg;
var y_svg;

var colorHighlight = "#fc8";
var buttonColor = "#ddd";
var timeDelay = 150;


var tip = d3.tip()
  .attr('class', 'd3-tip')
  .style('border', '1px solid #000');


function showTip(d,brushingIndex,tipItem) { 
  // Update network
  for (var i=0; i<allSVG.length;i++){
      var svg2 = allSVG[i];
      for (var c=0; c<dataS.Countries.length;c++){
        if (c==brushingIndex){
          svg2.selectAll(".dataPoint"+c)
            .style("fill-opacity", function(d2){ return 1; });
           // .style("stroke-opacity", function(d2){ return 1; }); 
        }
        else{
          svg2.selectAll(".dataPoint"+c)
            .style("fill-opacity", function(d2){ return 0; })
           // .style("stroke-opacity", function(d2){ return 0.1; }); 
        }
      }  
      svg.selectAll(".textCloud3") 
        .style("fill-opacity", function(d2){ return (d.name == d2.name) ? 1 : 0.1; });  
      svg.selectAll(".layer3")
        //.transition().duration(timeDelay)  
        .style("fill-opacity", function(d2){ return (d.name == d2.name) ? 0.8 : 0.08; })
        .style("stroke-opacity", function(d2){ return (d.name == d2.name) ? 1 : 0; });  
       
       var nameList = "";
       svg.selectAll(".linkArc3") 
          .style("stroke-opacity", function(d2){
            // Create list of name
            if (d.name == d2.source.name || d.name == d2.target.name) {
              if (nameList.indexOf(d2.source.name)<0)
                nameList+= "__"+d2.source.name;
              if (nameList.indexOf(d2.target.name)<0)
                nameList+= "_"+d2.target.name+"_";
            }
            return (d.name == d2.source.name || d.name == d2.target.name) ? 1 : 0.1;
          });   
      svg.selectAll(".nodeText3")  
        .style("fill-opacity", function(d2){ return (nameList.indexOf("_"+d2.name+"_")>=0) ? 1 : 0.1; });  
  }

  // Add time series of frequeny{}
  tip.html(function(d) {
    var str ="";
    str+="<b> Country info: </b>"
    str+="<table border='1px'  style='width:100%'>"
    str+=  "<tr><td>Country</td> <td align='right'>  <span style='color:black'>" +d.country+ "</span> </td></tr>";
    str+=  "<tr><td>Selected Year</td> <td align='right'>  <span style='color:black'>" +(minYear+d.year)+ "</span> </td></tr>";
    for (var v=0; v<dataS.Variables.length;v++){
      str+=  "<tr><td>"+dataS.Variables[v]+"</td> <td align='right'>  <span style='color:black'>" +d["v"+v]+ "</span> </td></tr>";
    }
    str+="</table> <br>"


    str+="<b> Scaterplot info: </b>";
    str+="<table border='0.5px'  style='width:100%'>";
    // **************************** Heading ****************************
    str+=  "<tr><td style='background-color:rgb(180,180,180);'>Scagnostics</td> <td align='center' style='background-color:rgb(180,180,180);'>Original Scaterplot</td> <td align='center' style='background-color:rgb(180,180,180);'>Leave '"+d.country+"' out</td> </tr>";
    
    for (var v=0; v<dataS.Variables.length;v++){
      if (v%2==1){
        var pair = Math.floor(v/2);
        for (var s=0; s<dataS.Scagnostics.length;s++){
          if (s==selectedScag)
            str+=  "<tr><td><b>"+dataS.Scagnostics[s]
               +"</b></td> <td align='center' style=\"background-color:"+hexToRgbA(colorRedBlue(d["Scagnostics"+pair][s]))+"\">  <span style='color:black; text-shadow: 0px 1px 1px #fff;'><b>" 
               +d["Scagnostics"+pair][s]+ "</b></span> </td> <td align='center' style=\"background-color:"+hexToRgbA(colorRedBlue(d["Scagnostics"+pair][s]))+"\">  <span style='color:black; text-shadow: 0px 1px 1px #fff;'><b>" 
               +d["ScagnosticsLeave1out"+pair][s]+ "</b></span> </td></tr>";
          else  
            str+=  "<tr><td>"+dataS.Scagnostics[s]
                +"</td> <td align='center' style=\"background-color:"+hexToRgbA(colorRedBlue(d["Scagnostics"+pair][s]))+"\">  <span style='color:black; text-shadow: 0px 1px 1px #fff;'>" 
                +d["Scagnostics"+pair][s]+ "</span> </td><td align='center' style=\"background-color:"+hexToRgbA(colorRedBlue(d["Scagnostics"+pair][s]))+"\">  <span style='color:black; text-shadow: 0px 1px 1px #fff;'>" 
                +d["ScagnosticsLeave1out"+pair][s]+ "</span> </td></tr>";
        }         
      }
    } 
    str+="</table>"
    return str;
      
   });   
  tip.direction('se');
  //tip.direction('n') 

  tip.offset([-d3.event.pageY+380,-d3.event.pageX]) // d3.event.pageX is the mouse position in the main windown
      
  tip.show(d);   
}    
function hexToRgbA(hex){
    var c;
    if(/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)){
        c= hex.substring(1).split('');
        if(c.length== 3){
            c= [c[0], c[0], c[1], c[1], c[2], c[2]];
        }
        c= '0x'+c.join('');
        return 'rgba('+[(c>>16)&255, (c>>8)&255, c&255].join(',')+',1)';
    }
    throw new Error('Bad Hex');
}

function hideTip(d) { 
  // Update network
  for (var i=0; i<allSVG.length;i++){
    var svg2 = allSVG[i];
    for (var c=0; c<dataS.Countries.length;c++){
       svg2.selectAll(".dataPoint"+c)
          .style("fill-opacity", pointOpacity)
          .style("stroke-opacity", 1); 
     }     
  }
  svg.selectAll(".textCloud3")  
        //.transition().duration(100)       
        .style("fill-opacity", 1);    
  svg.selectAll(".layer3")  
        //.transition().duration(100)
        .style("fill-opacity", 0.3)
        .style("stroke-opacity", 1);  
  svg.selectAll(".linkArc3") 
        //.transition().duration(100)
        .style("stroke-opacity", 0.6);     
  svg.selectAll(".nodeText3")  
        //.transition().duration(timeDelay)      
        .style("fill-opacity", 1);       
  tip.hide();
}  



