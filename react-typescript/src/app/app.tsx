import { observer } from "mobx-react-lite"
import React, { useEffect } from 'react';
import ReactDOM from 'react-dom';
import { Store } from "./Store";
import { Chart } from "react-google-charts";

const store = new Store();

export const App: React.FC<{ store : Store }> = observer(({ store }) => {

  useEffect( () => {
      async function requestData() {
        store.setLoading(true);
        await store.getlistData();
        store.setLoading(false);
      }

      requestData()
  }, [store.timeInterval]);

  return store.isLoading ? <div /> : (
    <div>
    <Chart
      width={'100%'}
      height={350}
      chartType="CandlestickChart"
      loader={<div>Loading Chart</div>}
      data={store.listDataToString()}
      options={{
        legend: 'none',
        bar: { groupWidth: '100%' },
        candlestick: {
          fallingColor: { strokeWidth: 0, fill: '#a52714' }, 
          risingColor: { strokeWidth: 0, fill: '#0f9d58' }, 
        },
        explorer: { 
          actions: ['dragToZoom', 'rightClickToReset'],
          axis: 'horizontal',
          keepInBounds: true,
          maxZoomIn: 4.0,
          chartType: "CandlestickChart",
        },
      }}
      rootProps={{ 'data-testid': '2' }}
    />
    <select 
      onChange={e => store.setTimeInterval(parseInt(e.target.value))} 
      className="form-control"
      style={{width: 100, marginLeft: 700, marginTop: 50}}
      value={store.timeInterval}>
      <option value="1">1 min</option>
      <option value="2">5 min</option>
      <option value="3">15 min</option>
      <option value="4">1 h</option>
      <option value="5">3 h</option>
      <option value="6">6 h</option>
      <option value="7">12 h</option>
      <option value="8">24 h</option>
     </select>
    </div>
  );
});

ReactDOM.render(
  <App store={store} />,
  document.getElementById("root")
);