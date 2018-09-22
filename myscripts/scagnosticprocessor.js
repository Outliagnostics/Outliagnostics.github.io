let scagTip = d3.tip()
    .attr('class', 'd3-tip')
    .offset([-10, 0])
    .html(function (d) {
        return `<strong>${d}</strong>`;
    });
svg.call(scagTip);
let rectFrames = d3.selectAll(".frame");
