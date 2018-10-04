/*This is for the tooltip*/
var tooltipDiv = d3.select("body").append("div")
    .attr("class", "tooltipexplorescag")
    .style("opacity", 0)
    .attr("id", "scagExploreId");

/*End tooltip section*/

function setExploreEvent(theSvg, dataPoints) {
    theSvg.dataPoints = dataPoints;
    theSvg
    /*This is for the tooltip section*/
        .on("click", function(d) {
            document.dataPoints = theSvg.dataPoints;
            tooltipDiv.transition()
                .duration(200)
                .style("opacity", 1.0);
            tooltipDiv
                .html('<iframe src="explorescag.html" style="width: 1040px; height: 800px;"></iframe>')
                .style("left", (d3.event.pageX -520) + "px")
                .style("top", (d3.event.pageY + 52) + "px");
        });
    /*End of tooltip section*/
}
$(document).keyup(function(e) {
    if (e.key === "Escape") { // escape key maps to keycode `27`
        tooltipDiv.transition().duration(500).style("opacity", 0);
    }
});