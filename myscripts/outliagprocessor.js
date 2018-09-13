class OutliagProcessor{
    constructor(dataS){
        this.dataS = dataS;
    }
    processOutliagData(){
        let self = this;
        processYearlyOutliags();
        processLeaveOutOutliags();

        function processYearlyOutliags(){
            let years = self.dataS["YearsData"].length;
            for (let year = 0; year < years; year++) {
                self.setYearOutliag(year, self.calculateYearlyOutliag(year));
            }
        }
        function processLeaveOutOutliags(){
            let years = self.dataS["YearsData"].length;
            let countries = d3.keys(self.dataS["CountriesData"]);
            for (let year = 0; year < years; year++) {
                countries.forEach(country =>{
                    self.setLeaveOutCountryOutliag(year, country, self.calculateYearlyLeaveOutOutliag(year, country));
                });
            }
        }
    }
    getYearData(year){
        return this.getYearDataLeaveOutCountry(year, null);
    }
    getYearDataLeaveOutCountry(year, theCountry){
        let cd = this.dataS["CountriesData"];
        let v0 = [],
            v1 = [];
        d3.keys(cd).forEach(country => {
            if(country!==theCountry){
                v0.push(cd[country][year]["v0"]);
                v1.push(cd[country][year]["v1"]);
            }
        });
        //convert to points
        let y = [];
        for (let j = 0; j < v0.length; j++) {
            y.push([v0[j], v1[j]]);
        }
        return y;
    }
    isLeaveOutPointValid(year, country){
        let cd = this.dataS["CountriesData"];
        let d = [(cd[country][year]["v0"]) , (cd[country][year]["v1"])];
        return this.isValidPoint(d);
    }
    getUniqueSize(data){
        return _.uniq(data.map(v => v.join(','))).length;
    }
    calculateYearlyOutliag(year) {
        let y = this.getYearData(year);
        //check if the input points has more than 2 unique values.
        return this.calculateOutliag(y);
    }
    calculateYearlyLeaveOutOutliag(year, country){
        //If the leaveout is empty, then the outliag is the outliag of the year (the same as not leaving it out)
        if(!this.isLeaveOutPointValid(year, country)){
            return this.getYearOutliag(year);
        }
        //Otherwise continue the calculation
        let y = this.getYearDataLeaveOutCountry(year, country);
        return this.calculateOutliag(y);
    }
    calculateOutliag(y) {
        let outliag = 0;
        let self = this;
        y = y.filter(d=> self.isValidPoint(d));
        if (this.getUniqueSize(y) > 3) {
            outliag = new outliagnostics(y, "leader").outlyingScore;
        }
        return outliag;
    }
    isValidPoint(d){
        return (typeof d[0] === 'number') && (typeof d[1] === 'number');
    }
    getYearOutliag(year){
        //The full outliag
        return this.dataS["YearsData"][year]["Scagnostics0"][0];
    }
    setYearOutliag(year, outliag){
        this.dataS["YearsData"][year]["Scagnostics0"][0] = outliag;
    }
    setLeaveOutCountryOutliag(year, country, outliag){
        this.dataS["CountriesData"][country][year]["Outlying"] = outliag;
    }
}