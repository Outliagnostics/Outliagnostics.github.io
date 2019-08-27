importScripts('outliagnostics.min.js');
onmessage = function(e){
    let outliag = null;
    if(getUniqueSize(e.data.data)>3){
        const ops = e.data.options;
        outliag = outliagnostics(e.data.data, ops.binType, ops.startBinGridSize, ops.isNormalized, ops.isBinned, ops.outlyingUpperBound, ops.minBins, ops.maxBins);
    }
    const result = {};
    result.outliag = outliag;
    result.year = e.data.year;
    result.country = e.data.country;
    postMessage(result);

}
function getUniqueSize(data) {
    const setdata = new Set(data.map(v => v.join(',')));
    return setdata.size;
}
