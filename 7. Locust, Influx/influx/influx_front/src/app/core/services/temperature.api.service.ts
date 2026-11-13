import { HttpClient } from "@angular/common/http";
import { inject, Injectable, Service } from "@angular/core";
import { Temperature } from "../models/temperature.model";

@Injectable({
    providedIn:'root'
})
export class TemperatureApiService {
    private readonly http: HttpClient = inject(HttpClient)
    private readonly api_base = "http://localhost:8080/api/temperature"
    public get(request:{span:string}){
        return this.http.get<Temperature.ForGet[]>(this.api_base, {
            params: {...request}
        })
    }
    public create(request: Temperature.ForCreate){
        return this.http.post(this.api_base, request)
    }
}
