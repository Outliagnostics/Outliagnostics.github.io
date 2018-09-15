class OutliagProcessor {
    constructor(dataS) {
        this.dataS = dataS;
        this.allYearsBins = [];
    }

    processOutliagData() {
        let self = this;
        processYearlyOutliags();
        processLeaveOut();
        function processYearlyOutliags() {
            let years = self.dataS["YearsData"].length;
            let countries = d3.keys(self.dataS["CountriesData"]);
            //Make sure that each year there is a bin
            for (let year = 0; year < years; year++) {
                let outliag = self.calculateYearlyOutliag(year);
                let outlyingScore = 0;
                let bins = null;
                if (outliag != null) {//outliag = null means set of valid unique points has length < 3
                    outlyingScore = outliag.outlyingScore;
                    bins = outliag.bins;
                }
                self.setYearOutliagScore(year, outlyingScore);
                self.allYearsBins.push(bins);
                //By default, leave out a country would not affect anything => so we set its default leave out to be the same as the not leaveout score.
                countries.forEach(country=>{
                    self.setYearCountryOutliagScore(year, country, outlyingScore);
                })
            }

        }
        function processLeaveOut() {
            //Only need to process the bins !=null and each bin with length > 1.
            let allBinsLength = self.allYearsBins.length;
            for (let year = 0; year < allBinsLength ; ++year) {
                let bins = self.allYearsBins[year];
                if (bins != null) {//beans = null means that year, there is no data (nor the data points <=3).
                    let binLength = bins.length;
                    for (let i = 0; i < binLength; ++i) {
                        let theBin = bins[i];
                        if(theBin.length==1){//Only leave out the bin if it is single, since we assume if a bin has more members, it would not affect the overall score if remove one member
                            let bins1 = bins.slice(0);//copy to avoid modifying the original one.
                            //remove the current bin.
                            bins1.splice(i, 1);
                            //calcualte outliag.
                            let outlyingScore = self.calculateOutliag(bins1.map(b => [b.x, b.y]), true, true).outlyingScore;
                            self.setYearCountryOutliagScore(year, theBin[0].data, outlyingScore);
                        }
                    }
                }
            }
        }
    }

    getYearData(year) {
        let cd = this.dataS["CountriesData"];
        //convert to points
        let y = [];
        d3.keys(cd).forEach(country => {
            let point = [cd[country][year]["v0"], cd[country][year]["v1"]];
            point.data = country;
            y.push(point);
        });
        return y;
    }

    getUniqueSize(data) {
        return _.uniq(data.map(v => v.join(','))).length;
    }

    calculateYearlyOutliag(year) {
        let y = this.getYearData(year);
        //check if the input points has more than 2 unique values.
        let outliag = this.calculateOutliag(y, false);
        return outliag;
    }


    calculateOutliag(y, isNormalized, isBinned) {
        var outliag = null;
        let self = this;
        y = y.filter(d => self.isValidPoint(d));
        if (this.getUniqueSize(y) > 3) {
            outliag = outliagnostics(y, "leader", isNormalized, isBinned);
        }
        return outliag;
    }

    isValidPoint(d) {
        return (typeof d[0] === 'number') && (typeof d[1] === 'number');
    }


    setYearOutliagScore(year, outliagScore) {
        this.dataS["YearsData"][year]["Scagnostics0"][0] = outliagScore;
    }

    setYearCountryOutliagScore(year, country, outlyingScore) {
        this.dataS["CountriesData"][country][year]["Outlying"] = outlyingScore;
    }
}