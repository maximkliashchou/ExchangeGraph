export interface ListData{
    time: string;
    min: number;
    max: number;
    first: number;
    last: number;
}

export class Sercive {

    listData(timeInterval: number): Promise<ListData[]> {
       return fetch(`http://localhost:8080/ExchangeGraph/graph/list/${timeInterval}`, {method: 'POST'}).then((response) => {
    return response.json();
  })
    }
}