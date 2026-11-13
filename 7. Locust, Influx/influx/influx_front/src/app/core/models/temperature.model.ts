export interface Temperature{
    value:number;
    location:string;
    timeStamp:string;
}

export namespace Temperature{
    export type ForGet = Temperature;
    export type ForCreate = Omit<Temperature , "timeStamp">;
}