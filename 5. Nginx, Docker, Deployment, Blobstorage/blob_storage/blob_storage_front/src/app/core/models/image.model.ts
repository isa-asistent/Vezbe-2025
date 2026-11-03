/**
 * Osnovni tip koji definise sliku
 */
export interface Image {
    key: string;
}

/**
 * Namespace koji enkapsulira sve DTO objekte koji su vezani za sliku,
 *  omogucava precizniji izjasnjavanje namere konkretnog tipa u kodu
 */
export namespace Image {
    /**
     * Podprostor imena namenje za demostraciju sirovog upload-a i download-a slika.
     */
    export namespace Raw {
        export type ForGet = Image & {
            file: Blob;
        }
        export type ForCreate = {
            file: Blob
        };
    }
    /**
     * Podprostor imena namenjen za demonstraciju upload-a i download-a slike putem "Presigned URL" mehanizma.
     */
    export namespace URL {
        export type ForGet = Image & { url: string };
        export type ForPut = Image & {
            url: string;
        };
    }
}