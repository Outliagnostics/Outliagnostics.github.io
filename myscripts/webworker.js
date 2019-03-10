let w;
function startWorker(fileName, data, onResult, isNew) {
    if (typeof (Worker) !== 'undefined') {
        if(w===undefined){
            w = new Worker(fileName);
        }

        w.postMessage(data);

        w.onmessage = function (e) {
            onResult(e.data);
        };
        return w;
    } else {
        throw "The browser doesn't support web worker";
    }
}