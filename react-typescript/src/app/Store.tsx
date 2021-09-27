import { action, makeAutoObservable, observable } from "mobx"
import { ListData, Sercive } from "./Service"

export class Store {
    @observable
    listData: ListData[];

    @observable
    timeInterval: number = 1;

    @observable
    isLoading: boolean = true;

    sercive = new Sercive()

    constructor() {
        makeAutoObservable(this);
    }

    @action
    setListData = (selected: ListData[]) => {
        this.listData = selected;
    }

    @action
    setTimeInterval = (selected: number) => {
        this.timeInterval = selected;
    }

    @action
    setLoading = (selected: boolean) => {
        this.isLoading = selected;
    }

    getlistData = async () => {
        const result = await this.sercive.listData(this.timeInterval);
        this.setListData(result);
    }

    listDataToString = () => {
        var time:any[][] = [['day', 'a', 'b', 'c', 'd']];
        this.listData.map((value) => {
            time.push([value.time, value.min, value.first, value.last, value.max])
        });
        return time;
    }   
}