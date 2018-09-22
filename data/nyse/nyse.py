import graphlab

graphlab.set_runtime_config('GRAPHLAB_DEFAULT_NUM_PYLAMBDA_WORKERS', 72)

#load data
f = graphlab.SFrame("prices.csv")
processedf = f

#date time
from datetime import datetime
def parseDate(dateStr):
    dateFrm = '%Y-%m-%d'
    if ":" in dateStr:
        dateFrm = "%Y-%m-%d %H:%M:%S"
    return datetime.strptime(dateStr, dateFrm);
def monthDiff(d1, d2):
    return (d1.year - d2.year) * 12 + d1.month - d2.month

#process date time
processedf['date'] = processedf['date'].apply(parseDate)

minDate = min(processedf['date'])
def monthIndex(d):
    return monthDiff(d, minDate);

processedf['monthIndex'] = processedf['date'].apply(monthIndex)

#processing variables
Variables = ["close", "open"]

#aggregate data
import graphlab.aggregate as agg

result = processedf.groupby(key_columns=['symbol', 'monthIndex'], operations={Variables[0]: agg.MEAN(Variables[0]), Variables[1]: agg.MEAN(Variables[1])})

Scagnostics = ["Outlying"]

Symbols = result['symbol'].unique()

def getDataFromMonth(month):
    r = result[result['monthIndex'].apply(lambda x: x==month)]
    maxV0 = max(r[Variables[0]])
    minV0 = min(r[Variables[0]])
    maxV1 = max(r[Variables[1]])
    minV1 = min(r[Variables[1]])
    r['normalized'+Variables[0]] = r[Variables[0]].apply(lambda c: (c-minV0)/(maxV0-minV0))
    r['normalized'+Variables[1]] = r[Variables[1]].apply(lambda o: (o-minV1)/(maxV1-minV1))
    return r.remove_column('monthIndex')

monthData = []
for month in range(max(result['monthIndex'])+1):
    monthData.append(getDataFromMonth(month))

def getDataMonthSymbol(month, symbol):
    m = monthData[month]
    sm = m[m['symbol'].apply(lambda s: s==symbol)]
    return sm;

#now build the data
processedMonthData = []
for month in range(max(result['monthIndex'])+1):
    s0 = []
    s1 = []
    for symbol in Symbols:
        d = getDataMonthSymbol(month, symbol)
        if d.num_rows()>0:
            s0.append(d['normalized'+Variables[0]][0])
            s1.append(d['normalized'+Variables[1]][0])
        else:
            s0.append('NaN')
            s1.append('NaN')
    processedMonthData.append({'s0': s0, 's1': s1})

Scagnostics0 = [0];
for md in processedMonthData:
    md['Scagnostics0'] = Scagnostics0

processedSymbolData = {}
for symbol in Symbols:
    singleSymbolData = []
    for month in range(max(result['monthIndex'])+1):
        d = getDataMonthSymbol(month, symbol)
        if d.num_rows()>0:
            v0 = d[Variables[0]][0]
            v1 = d[Variables[1]][0]
            s0 = d['normalized'+Variables[0]][0]
            s1 = d['normalized'+Variables[1]][0]
        else:
            v0 = 'NaN'
            v1 = 'NaN'
            s0 = 'NaN'
            s1 = 'NaN'
        singleMonthData ={"v0": v0, "v1": v1, "s0": s0, "s1": s1, "Outlying": 0, "year": month}
        singleSymbolData.append(singleMonthData)
    processedSymbolData[symbol] = singleSymbolData


finalResult = {}
finalResult['Scagnostics'] = Scagnostics
finalResult['Variables'] = Variables
finalResult['Countries'] = list(Symbols)
finalResult['YearsData'] = processedMonthData
finalResult['CountriesData'] = processedSymbolData

import json
with open('prices.json', 'w') as outfile:
    json.dump(finalResult, outfile)
